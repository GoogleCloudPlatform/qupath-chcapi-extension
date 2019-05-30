// Copyright (C) 2019 Google LLC
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.quantumsoft.qupathcloud.synchronization;

import static com.quantumsoft.qupathcloud.configuration.MetadataConfiguration.METADATA_FILE_EXTENSION;

import com.quantumsoft.qupathcloud.configuration.MetadataConfiguration;
import com.quantumsoft.qupathcloud.converter.ImageDataUtilities;
import com.quantumsoft.qupathcloud.converter.dicomizer.ImageToWsiDcmConverter;
import com.quantumsoft.qupathcloud.converter.qpdata.DataToDcmConverter;
import com.quantumsoft.qupathcloud.converter.qpdata.DcmToDataConverter;
import com.quantumsoft.qupathcloud.dao.CloudDAO;
import com.quantumsoft.qupathcloud.dao.DAOHelper;
import com.quantumsoft.qupathcloud.dao.spec.QueryBuilder;
import com.quantumsoft.qupathcloud.entities.DicomStore;
import com.quantumsoft.qupathcloud.entities.Series;
import com.quantumsoft.qupathcloud.entities.instance.Instance;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import com.quantumsoft.qupathcloud.gui.windows.ConflictsWindow;
import com.quantumsoft.qupathcloud.gui.windows.SynchronizationWindow;
import com.quantumsoft.qupathcloud.imageserver.StubImageServer;
import com.quantumsoft.qupathcloud.repository.Repository;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.helpers.DisplayHelpers;
import qupath.lib.images.ImageData;
import qupath.lib.projects.Project;
import qupath.lib.projects.ProjectImageEntry;

public class SynchronizationProjectWithDicomStore {

  private static final Path METADATA_FOLDER = Paths.get("metadata");
  private static final Path QU_PATH_DATA_FILE = Paths.get("data.qpdata");
  private static final Logger LOGGER = LogManager.getLogger();
  private CloudDAO cloudDAO;
  private String projectId;
  private String locationId;
  private String datasetId;
  private String dicomStoreId;
  private QuPathGUI qupath;
  private SynchronizationWindow synchronizationWindow;
  private Project<BufferedImage> project;
  private Path projectDirectory;

  public SynchronizationProjectWithDicomStore(QuPathGUI qupath, DicomStore dicomStore) {
    this.qupath = qupath;
    cloudDAO = Repository.INSTANCE.getCloudDao();
    projectId = dicomStore.getProjectId();
    locationId = dicomStore.getLocationId();
    datasetId = dicomStore.getDatasetId();
    dicomStoreId = dicomStore.getDicomStoreId();
    synchronizationWindow = new SynchronizationWindow();
    project = qupath.getProject();
    projectDirectory = project.getPath().getParent();
  }

  public void synchronization() {
    synchronizationWindow.showSynchronizationWindow();
    Runnable loader = () -> {
      try {
        synchronizeImages();
        synchronizeMetadata();
        synchronizeQpdata();
        project.syncChanges();
      } catch (QuPathCloudException | IOException e) {
        LOGGER.error("Synchronization error: ", e);
        DisplayHelpers.showErrorMessage("Synchronization error!", e);
      } finally {
        Platform.runLater(() -> {
          qupath.refreshProject();
          synchronizationWindow.close();
        });
      }
    };
    Thread loadThread = new Thread(loader);
    loadThread.start();
  }

  private void synchronizeImages() throws QuPathCloudException {
    List<ProjectImageEntry<BufferedImage>> imageList = project.getImageList();
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    List<Future<Void>> futureList = new ArrayList<>();
    QueryBuilder queryBuilder = QueryBuilder.forProject(projectId)
        .setLocationId(locationId)
        .setDatasetId(datasetId)
        .setDicomStoreId(dicomStoreId);
    List<Series> remoteSeriesList = cloudDAO.getSeriesList(queryBuilder);
    List<Series> remoteImageSeriesList = DAOHelper.getImageSeriesList(remoteSeriesList);

    List<Path> tempDirectories = new ArrayList<>();
    for (ProjectImageEntry<BufferedImage> currentEntry : imageList) {
      String serverPath = currentEntry.getServerPath();
      String imageExtension = FilenameUtils.getExtension(serverPath);
      String imageName = FilenameUtils.getBaseName(serverPath);

      if (!imageExtension.equals(METADATA_FILE_EXTENSION)) {
        URI uri;
        try {
          uri = new URI(serverPath);
        } catch (URISyntaxException e) {
          throw new QuPathCloudException(e);
        }

        Path tempDirectory = createTempDirectory("QuPath-");
        tempDirectory.toFile().deleteOnExit();
        tempDirectories.add(tempDirectory);

        Path pathToImage = Paths.get(uri);
        ImageToWsiDcmConverter converter = new ImageToWsiDcmConverter(pathToImage, tempDirectory);
        String checkedFileName = checkFileName(remoteImageSeriesList, imageName);
        converter.convertImageToWsiDcm(checkedFileName);

        List<Path> dicomizedFiles;
        try {
          dicomizedFiles = Files.list(tempDirectory).collect(Collectors.toList());
        } catch (IOException e) {
          throw new QuPathCloudException(e);
        }
        if (dicomizedFiles.size() == 0) {
          String errorParam = MessageFormat.format("Dicomization failed for: {0}", serverPath);
          throw new QuPathCloudException(errorParam);
        }
        queryBuilder.setPaths(dicomizedFiles);
        Callable<Void> callable = () -> {
          cloudDAO.uploadToDicomStore(queryBuilder);
          return null;
        };
        Future<Void> future = executorService.submit(callable);
        futureList.add(future);

        project.removeImage(currentEntry);
      }
    }
    for (Future<Void> future : futureList) {
      try {
        future.get();
      } catch (InterruptedException | ExecutionException e) {
        throw new QuPathCloudException(e);
      }
    }
    executorService.shutdown();

    for (Path tempDirectory : tempDirectories) {
      deleteDirectory(tempDirectory);
    }
  }

  private void synchronizeMetadata() throws QuPathCloudException {
    Path metadataDirectory = projectDirectory.resolve(METADATA_FOLDER);
    MetadataConfiguration metadataConfiguration = new MetadataConfiguration(metadataDirectory);

    QueryBuilder queryBuilder = QueryBuilder.forProject(projectId)
        .setLocationId(locationId)
        .setDatasetId(datasetId)
        .setDicomStoreId(dicomStoreId);
    List<Series> remoteSeriesList = cloudDAO.getSeriesList(queryBuilder);
    List<Series> remoteImageSeriesList = DAOHelper.getImageSeriesList(remoteSeriesList);

    List<Series> seriesListInProject;
    if (Files.notExists(metadataDirectory)) {
      try {
        Files.createDirectory(metadataDirectory);
      } catch (IOException e) {
        throw new QuPathCloudException(e);
      }
      seriesListInProject = new ArrayList<>();
      seriesProcess(remoteImageSeriesList, metadataConfiguration, seriesListInProject);
    } else {
      seriesListInProject = metadataConfiguration.readProjectMetadataIndexFile();
      List<Series> localImageSeriesList = new ArrayList<>();
      for (Series series : seriesListInProject) {
        if (remoteImageSeriesList.contains(series)) {
          localImageSeriesList.add(series);
        }
      }
      List<Series> seriesListForDownloading = new ArrayList<>(remoteImageSeriesList);
      seriesListForDownloading.removeAll(localImageSeriesList);
      if (seriesListForDownloading.size() != 0) {
        seriesProcess(seriesListForDownloading, metadataConfiguration, seriesListInProject);
      }
    }
  }

  private void seriesProcess(List<Series> remoteImageSeriesList,
      MetadataConfiguration metadataConfiguration,
      List<Series> seriesListInProject) throws QuPathCloudException {
    for (Series series : remoteImageSeriesList) {
      String studyId = series.getStudyInstanceUID().getValue1();
      String seriesId = series.getSeriesInstanceUID().getValue1();
      String imageName = series.getSeriesDescription().getValue1();
      QueryBuilder queryBuilder = QueryBuilder.forProject(projectId)
          .setLocationId(locationId)
          .setDatasetId(datasetId)
          .setDicomStoreId(dicomStoreId)
          .setStudyId(studyId)
          .setSeriesId(seriesId);
      List<Instance> instances = cloudDAO.getInstancesList(queryBuilder);
      Path metadataImageFile = metadataConfiguration.saveMetadataFile(series, instances);
      String serverPath = metadataImageFile.toString();

      StubImageServer stubImageServer = new StubImageServer();
      stubImageServer.setDisplayedImageName(imageName);
      stubImageServer.setPath(serverPath);

      project.addImage(stubImageServer);

      seriesListInProject.add(series);
    }
    metadataConfiguration.saveProjectMetadataIndexFile(seriesListInProject);
  }

  private void synchronizeQpdata() throws QuPathCloudException {
    List<Pair<ProjectImageEntry<BufferedImage>, Date>> localInfosToUpload = collectLocalDataFileInfos();
    List<Pair<Instance, Date>> remoteInfosToDownload = collectRemoteInstanceInfos();

    List<Instance> remoteInstancesToDelete = new ArrayList<>();
    List<Conflict> conflicts = new ArrayList<>();

    Iterator<Pair<ProjectImageEntry<BufferedImage>, Date>> localIter = localInfosToUpload
        .iterator();
    Pair<ProjectImageEntry<BufferedImage>, Date> localInfo;
    while (localIter.hasNext()) {
      localInfo = localIter.next();
      String localName = localInfo.getKey().getImageName();
      Pair<Instance, Date> remoteInfo;
      Iterator<Pair<Instance, Date>> remoteIter = remoteInfosToDownload.iterator();
      while (remoteIter.hasNext()) {
        remoteInfo = remoteIter.next();
        if (localName.equals(remoteInfo.getKey().getSopAuthorizationComment().getValue1())) {
          Date localDate = localInfo.getValue();
          Date remoteDate = remoteInfo.getValue();
          int comparisonResult = localDate.compareTo(remoteDate);
          Conflict.Resolution resolution;
          if (comparisonResult > 0) {
            resolution = Conflict.Resolution.Local;
          } else if (comparisonResult == 0) {
            remoteIter.remove();
            localIter.remove();
            break;
          } else {
            resolution = Conflict.Resolution.Remote;
          }
          Conflict conflict = new Conflict(localName, localInfo, remoteInfo, resolution);
          conflicts.add(conflict);
        }
      }
    }

    if (conflicts.size() > 0) {
      Callable<List<Conflict>> task = () -> {
        ConflictsWindow conflictsWindow = new ConflictsWindow(conflicts);
        conflictsWindow.showAndWaitConflictsWindow();
        return conflictsWindow.getResult();
      };
      FutureTask<List<Conflict>> conflictsQuery = new FutureTask<>(task);
      Platform.runLater(conflictsQuery);

      try {
        List<Conflict> resolvedConflicts = conflictsQuery.get();

        for (Conflict conflict : resolvedConflicts) {
          if (conflict.getResolution() == Conflict.Resolution.Cancel) {
            remoteInfosToDownload.remove(conflict.getRemote());
            localInfosToUpload.remove(conflict.getLocal());
          } else if (conflict.getResolution() == Conflict.Resolution.Local) {
            remoteInstancesToDelete.add(conflict.getRemote().getKey());
            remoteInfosToDownload.remove(conflict.getRemote());
          } else if (conflict.getResolution() == Conflict.Resolution.Remote) {
            localInfosToUpload.remove(conflict.getLocal());
          }
        }
      } catch (InterruptedException | ExecutionException e) {
        throw new QuPathCloudException(e);
      }
    }

    QueryBuilder baseQuery = QueryBuilder.forProject(projectId)
        .setLocationId(locationId)
        .setDatasetId(datasetId)
        .setDicomStoreId(dicomStoreId);

    if (localInfosToUpload.size() > 0) {
      processUploads(localInfosToUpload, baseQuery);
    }

    if (remoteInfosToDownload.size() > 0) {
      processDownloads(remoteInfosToDownload, baseQuery);
    }

    QueryBuilder query = new QueryBuilder(baseQuery).setInstances(remoteInstancesToDelete);
    cloudDAO.deleteInstances(query);
  }

  private List<Pair<ProjectImageEntry<BufferedImage>, Date>> collectLocalDataFileInfos()
      throws QuPathCloudException {
    List<ProjectImageEntry<BufferedImage>> imageList = qupath.getProject().getImageList();
    List<Pair<ProjectImageEntry<BufferedImage>, Date>> localDataFileInfos = new ArrayList<>();
    for (ProjectImageEntry<BufferedImage> currentEntry : imageList) {
      Path pathToCurrentEntry = currentEntry.getEntryPath();
      Path pathToCurrentQpdataFile = pathToCurrentEntry.resolve(QU_PATH_DATA_FILE);
      if (Files.exists(pathToCurrentQpdataFile)) {
        Date modificationDate = ImageDataUtilities.getModificationDate(pathToCurrentQpdataFile);
        Pair<ProjectImageEntry<BufferedImage>, Date> fileInfo =
            new Pair<>(currentEntry, modificationDate);
        localDataFileInfos.add(fileInfo);
      }
    }
    return localDataFileInfos;
  }

  private List<Pair<Instance, Date>> collectRemoteInstanceInfos() throws QuPathCloudException {
    List<Pair<Instance, Date>> remoteInstanceInfos = new ArrayList<>();
    QueryBuilder queryBuilder = QueryBuilder.forProject(projectId)
        .setLocationId(locationId)
        .setDatasetId(datasetId)
        .setDicomStoreId(dicomStoreId);
    List<Instance> instances = cloudDAO.getInstancesList(queryBuilder);
    List<Instance> remoteInstances = DAOHelper.getQpdataInstanceListInDicomStore(instances);
    for (Instance instance : remoteInstances) {
      Pair<Instance, Date> instanceInfo = new Pair<>(instance, instance.getCreationDate());
      remoteInstanceInfos.add(instanceInfo);
    }
    return remoteInstanceInfos;
  }

  private void processUploads(List<Pair<ProjectImageEntry<BufferedImage>, Date>> localDataFileInfos,
      QueryBuilder baseQuery)
      throws QuPathCloudException {
    Path directoryToUpload = createTempDirectory("qpDataDcmUpload-");

    List<Path> dataFilesForUpload = new ArrayList<>();
    for (Pair<ProjectImageEntry<BufferedImage>, Date> fileInfo : localDataFileInfos) {
      Path pathToCurrentEntry = fileInfo.getKey().getEntryPath();
      Path pathToCurrentQpdataFile = pathToCurrentEntry.resolve(QU_PATH_DATA_FILE);
      Date modificationDate = fileInfo.getValue();
      String imageName = fileInfo.getKey().getImageName();
      DataToDcmConverter dataToDcmConverter = new DataToDcmConverter(pathToCurrentQpdataFile,
          directoryToUpload, modificationDate, imageName);
      Path convertedFile = dataToDcmConverter.convertQuPathDataToDcm();
      dataFilesForUpload.add(convertedFile);
    }

    QueryBuilder query = new QueryBuilder(baseQuery).setPaths(dataFilesForUpload);
    cloudDAO.uploadToDicomStore(query);

    deleteDirectory(directoryToUpload);
  }

  private void processDownloads(List<Pair<Instance, Date>> remoteInstanceInfos,
      QueryBuilder baseQuery) throws QuPathCloudException {

    List<ProjectImageEntry<BufferedImage>> imageList = qupath.getProject().getImageList();
    Map<String, Path> imageDirectories = new HashMap<>();
    for (ProjectImageEntry<BufferedImage> currentEntry : imageList) {
      Path pathToCurrentEntry = currentEntry.getEntryPath();
      String imageName = currentEntry.getImageName();

      if (Files.notExists(pathToCurrentEntry)) {
        String serverPath = currentEntry.getServerPath();
        StubImageServer stubImageServer = new StubImageServer();
        stubImageServer.setDisplayedImageName(imageName);
        stubImageServer.setPath(serverPath);

        ImageData<BufferedImage> imageData = qupath.createNewImageData(stubImageServer);
        ProjectImageEntry<BufferedImage> entry = project.getImageEntry(serverPath);
        try {
          entry.saveImageData(imageData);
        } catch (IOException e) {
          throw new QuPathCloudException(e);
        }
      }

      imageDirectories.put(imageName, pathToCurrentEntry);
    }

    Path directoryToDownload = createTempDirectory("qpDataDcmDownload-");

    List<Instance> remoteQpdataInstances = remoteInstanceInfos.stream().map(Pair::getKey)
        .collect(Collectors.toList());
    QueryBuilder query = new QueryBuilder(baseQuery)
        .setDirectory(directoryToDownload)
        .setInstances(remoteQpdataInstances);
    cloudDAO.downloadInstances(query);

    for (Instance instance : remoteQpdataInstances) {
      String sopInstanceUID = instance.getSopInstanceUID().getValue1();
      Path downloadedQpdataInstance = directoryToDownload.resolve(sopInstanceUID + ".dcm");
      String imageName = instance.getSopAuthorizationComment().getValue1();
      Path imageDirectory = imageDirectories.get(imageName);
      Path qpdataFile = imageDirectory.resolve(QU_PATH_DATA_FILE);
      DcmToDataConverter dcmToDataConverter =
          new DcmToDataConverter(downloadedQpdataInstance, qpdataFile);
      dcmToDataConverter.convertDcmToQuPathData();
    }

    deleteDirectory(directoryToDownload);
  }

  private Path createTempDirectory(String prefix) throws QuPathCloudException {
    try {
      return Files.createTempDirectory(prefix);
    } catch (IOException e) {
      throw new QuPathCloudException(e);
    }
  }

  private void deleteDirectory(Path path) throws QuPathCloudException {
    try {
      FileUtils.deleteDirectory(path.toFile());
    } catch (IOException e) {
      throw new QuPathCloudException(e);
    }
  }

  private String checkFileName(List<Series> remoteSeriesList, String localFileName) {
    List<String> remoteFileNames = new ArrayList<>();
    for (Series series : remoteSeriesList) {
      String remoteFileName = series.getSeriesDescription().getValue1();
      remoteFileNames.add(remoteFileName);
    }
    int fileNumber = 0;
    if (remoteFileNames.contains(localFileName)) {
      for (int i = 1; i < remoteFileNames.size(); i++) {
        String iteration = localFileName + "_" + i;
        if (remoteFileNames.contains(iteration)) {
          fileNumber = i;
        }
      }
      if (fileNumber == 0) {
        return localFileName + "_1";
      } else {
        fileNumber++;
        return localFileName + "_" + fileNumber;
      }
    }
    return localFileName;
  }
}
