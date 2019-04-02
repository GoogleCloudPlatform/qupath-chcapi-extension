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

import com.quantumsoft.qupathcloud.configuration.MetadataConfiguration;
import com.quantumsoft.qupathcloud.converter.ImageDataUtilities;
import com.quantumsoft.qupathcloud.converter.qpdata.DataToDcmConverter;
import com.quantumsoft.qupathcloud.converter.qpdata.DcmToDataConverter;
import com.quantumsoft.qupathcloud.dao.CloudDAO;
import com.quantumsoft.qupathcloud.dao.DAOHelper;
import com.quantumsoft.qupathcloud.dao.spec.QueryBuilder;
import com.quantumsoft.qupathcloud.entities.DicomStore;
import com.quantumsoft.qupathcloud.entities.Series;
import com.quantumsoft.qupathcloud.entities.instance.Instance;
import com.quantumsoft.qupathcloud.entities.metadata.ImageMetadataIndex;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import com.quantumsoft.qupathcloud.gui.windows.ConflictsWindow;
import com.quantumsoft.qupathcloud.gui.windows.SynchronizationWindow;
import com.quantumsoft.qupathcloud.repository.Repository;
import javafx.application.Platform;
import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.helpers.DisplayHelpers;
import qupath.lib.projects.Project;
import qupath.lib.projects.ProjectIO;
import qupath.lib.projects.ProjectImageEntry;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.quantumsoft.qupathcloud.configuration.MetadataConfiguration.METADATA_FILE_EXTENSION;

public class SynchronizationProjectWithDicomStore {
    private static final String METADATA_FOLDER = "metadata";
    private static final String TEMPORARY_FOLDER = "temp";
    private static final Logger LOGGER = LogManager.getLogger();
    private CloudDAO cloudDAO;
    private String projectId;
    private String locationId;
    private String datasetId;
    private String dicomStoreId;
    private QuPathGUI qupath;
    private SynchronizationWindow synchronizationWindow;
    private Project<BufferedImage> project;
    private File projectDirectory;

    public SynchronizationProjectWithDicomStore(QuPathGUI qupath, DicomStore dicomStore) {
        this.qupath = qupath;
        cloudDAO = Repository.INSTANCE.getCloudDao();
        projectId = dicomStore.getProjectId();
        locationId = dicomStore.getLocationId();
        datasetId = dicomStore.getDatasetId();
        dicomStoreId = dicomStore.getDicomStoreId();
        synchronizationWindow = new SynchronizationWindow();
        project = qupath.getProject();
        projectDirectory = qupath.getCurrentProjectDirectory();
    }

    public void synchronization() {
        synchronizationWindow.showSynchronizationWindow();
        Runnable loader = () -> {
            try {
                synchronizeImages();
                synchronizeMetadata();
                synchronizeQpdata();
                ProjectIO.writeProject(project);
            } catch (QuPathCloudException e) {
                LOGGER.error("Synchronization error: ", e);
                DisplayHelpers.showErrorMessage("Synchronization error!", e);
            }

            Platform.runLater(() -> {
                qupath.refreshProject();
                synchronizationWindow.close();
            });
        };
        Thread loadThread = new Thread(loader);
        loadThread.start();
    }

    private void synchronizeImages() throws QuPathCloudException {
        File temporaryDirectory = new File(projectDirectory, TEMPORARY_FOLDER);
        if (!temporaryDirectory.exists()) {
            checkedMkDir(temporaryDirectory);
        }
        List<ProjectImageEntry<BufferedImage>> imageList = project.getImageList();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        List<Future<Void>> futureList = new ArrayList<>();
        QueryBuilder queryBuilder = QueryBuilder.forProject(projectId)
                .setLocationId(locationId)
                .setDatasetId(datasetId)
                .setDicomStoreId(dicomStoreId);
        List<Series> series = cloudDAO.getSeriesList(queryBuilder);
        List<Series> remoteSeriesList = DAOHelper.getImagesSeriesList(series);
        for (int i = 0; i < imageList.size(); i++) {
            ProjectImageEntry<BufferedImage> entry = imageList.get(i);
            String pathToImage = entry.getServerPath();
            String extension = FilenameUtils.getExtension(pathToImage);
            String localFileName = FilenameUtils.getBaseName(pathToImage);

            if (!extension.equals(METADATA_FILE_EXTENSION)) {
                if(1==1)
                    return;

                project.removeImage(entry);
                File localFolder = new File(temporaryDirectory, String.valueOf(i));
                checkedMkDir(localFolder);
                File localImageFile = new File(pathToImage);
                String checkedFileName = checkFileName(remoteSeriesList, localFileName);

                //ImageToWsiDcmConverter converter = new ImageToWsiDcmConverter(localImageFile, localFolder);
                //converter.convertImageToWsiDcm(checkedFileName);

                File[] dicomizedFiles = localFolder.listFiles((dir, fname) -> fname.contains(".dcm"));
                if (dicomizedFiles == null || dicomizedFiles.length == 0) {
                    String errorParameter = MessageFormat.format("Dicomization failed for: {0}", pathToImage);
                    throw new QuPathCloudException(errorParameter);
                }
                queryBuilder.setFiles(Arrays.asList(dicomizedFiles));
                Callable<Void> callable = () -> {
                    cloudDAO.uploadToDicomStore(queryBuilder);
                    return null;
                };
                Future<Void> future = executorService.submit(callable);
                futureList.add(future);
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

        try {
            FileUtils.cleanDirectory(temporaryDirectory);
        } catch (IOException e) {
            throw new QuPathCloudException(e);
        }
    }

    private void synchronizeMetadata() throws QuPathCloudException {
        File metadataDirectory = new File(projectDirectory, METADATA_FOLDER);
        QueryBuilder queryBuilder = QueryBuilder.forProject(projectId)
                .setLocationId(locationId)
                .setDatasetId(datasetId)
                .setDicomStoreId(dicomStoreId);
        List<Series> series = cloudDAO.getSeriesList(queryBuilder);
        List<Series> remoteSeriesList = DAOHelper.getImagesSeriesList(series);

        MetadataConfiguration metadataConfiguration = new MetadataConfiguration(metadataDirectory);
        List<ImageMetadataIndex> imageMetadataIndexList;
        if (!metadataDirectory.exists()) {
            checkedMkDir(metadataDirectory);
            imageMetadataIndexList = new ArrayList<>();
            seriesProcess(remoteSeriesList, metadataConfiguration, imageMetadataIndexList);
        } else if (metadataDirectory.listFiles() != null) {
            imageMetadataIndexList = metadataConfiguration.readProjectMetadataIndexFile();
            List<Series> localSeriesList = new ArrayList<>();
            for (ImageMetadataIndex imageMetadataIndex : imageMetadataIndexList) {
                Series localSeries = imageMetadataIndex.getSeries();
                if (remoteSeriesList.contains(localSeries)) {
                    localSeriesList.add(localSeries);
                }
            }
            List<Series> seriesListForDownloading = new ArrayList<>(remoteSeriesList);
            seriesListForDownloading.removeAll(localSeriesList);
            if (seriesListForDownloading.size() != 0) {
                seriesProcess(seriesListForDownloading, metadataConfiguration, imageMetadataIndexList);
            }
        }
    }

    private void seriesProcess(List<Series> seriesList, MetadataConfiguration metadataConfiguration, List<ImageMetadataIndex> imageMetadataIndexList) throws QuPathCloudException {
        for (Series series : seriesList) {
            String studyId = series.getStudyInstanceUID().getValue1();
            String seriesId = series.getSeriesInstanceUID().getValue1();
            String imageComments = series.getImageComments().getValue1();
            QueryBuilder queryBuilder = QueryBuilder.forProject(projectId)
                    .setLocationId(locationId)
                    .setDatasetId(datasetId)
                    .setDicomStoreId(dicomStoreId)
                    .setStudyId(studyId)
                    .setSeriesId(seriesId);
            List<Instance> instances = cloudDAO.getInstancesList(queryBuilder);
            File metadataFile = metadataConfiguration.saveMetadataFile(series, instances);
            String absolutePathToMetadataFile = metadataFile.getAbsolutePath();
            ProjectImageEntry<BufferedImage> newImageEntry = new ProjectImageEntry<>(project, absolutePathToMetadataFile, imageComments, null);
            project.addImage(newImageEntry);
            ImageMetadataIndex imageMetadataIndex = new ImageMetadataIndex();
            imageMetadataIndex.setSeries(series);
            imageMetadataIndexList.add(imageMetadataIndex);
        }
        metadataConfiguration.saveProjectMetadataIndexFile(imageMetadataIndexList);
    }

    private void synchronizeQpdata() throws QuPathCloudException {
        List<Pair<File, Date>> localInfosToUpload = collectLocalDataFileInfos();
        List<Pair<Instance, Date>> remoteInfosToDownload = collectRemoteInstanceInfos();

        List<Instance> remoteInstancesToDelete = new ArrayList<>();
        List<Conflict> conflicts = new ArrayList<>();

        Iterator<Pair<File, Date>> localIter = localInfosToUpload.iterator();
        Pair<File, Date> localInfo;
        while (localIter.hasNext()) {
            localInfo = localIter.next();
            String localName = FilenameUtils.getBaseName(localInfo.getKey().getPath());
            Pair<Instance, Date> remoteInfo;
            Iterator<Pair<Instance, Date>> remoteIter = remoteInfosToDownload.iterator();
            while (remoteIter.hasNext()) {
                remoteInfo = remoteIter.next();
                if (localName.equals(remoteInfo.getKey().getSopAuthorizationComment().getValue1())) {
                    int comparisonResult = localInfo.getValue().compareTo(remoteInfo.getValue());
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

        processUploads(localInfosToUpload, baseQuery);

        processDownloads(remoteInfosToDownload, baseQuery);

        QueryBuilder query = new QueryBuilder(baseQuery).setInstances(remoteInstancesToDelete);
        cloudDAO.deleteInstances(query);
    }

    private List<Pair<File, Date>> collectLocalDataFileInfos() {
        File projectDataDirectory = qupath.getProjectDataDirectory(true);

        File[] dataFiles = projectDataDirectory.listFiles();
        List<Pair<File, Date>> localDataFileInfos = new ArrayList<>();
        if (dataFiles != null) {
            for (File dataFile : dataFiles) {
                Pair<File, Date> fileInfo = new Pair<>(dataFile, ImageDataUtilities.getModificationDate(dataFile));
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
        List<Instance> remoteInstances = DAOHelper.getQpdataInstancesListInDicomStore(instances);
        for (Instance instance : remoteInstances) {
            Pair<Instance, Date> instanceInfo = new Pair<>(instance, instance.getCreationDate());
            remoteInstanceInfos.add(instanceInfo);
        }
        return remoteInstanceInfos;
    }

    private void processUploads(List<Pair<File, Date>> localDataFileInfos, QueryBuilder baseQuery) throws QuPathCloudException {
        File uploadDirectory = prepareTempDirectory("qpDataDcmUpload");

        List<File> dataFilesForUpload = new ArrayList<>();
        List<File> tempFiles = new ArrayList<>();
        for (Pair<File, Date> fileInfo : localDataFileInfos) {
            DataToDcmConverter dataToDcmConverter = new DataToDcmConverter(fileInfo.getKey(), uploadDirectory, fileInfo.getValue());
            File convertedFile = dataToDcmConverter.convertQuPathDataToDcm();
            dataFilesForUpload.add(convertedFile);
            tempFiles.add(convertedFile);
        }

        QueryBuilder query = new QueryBuilder(baseQuery).setFiles(dataFilesForUpload);
        cloudDAO.uploadToDicomStore(query);

        deleteLocalFiles(tempFiles);
    }

    private void processDownloads(List<Pair<Instance, Date>> remoteInstanceInfos, QueryBuilder baseQuery) throws QuPathCloudException {
        File projectDataDirectory = qupath.getProjectDataDirectory(true);
        File downloadDirectory = prepareTempDirectory("qpDataDcmDownload");

        List<Instance> remoteInstancesToDownload = remoteInstanceInfos.stream().map(Pair::getKey).collect(Collectors.toList());
        QueryBuilder query = new QueryBuilder(baseQuery)
                .setDirectory(downloadDirectory)
                .setInstances(remoteInstancesToDownload);
        cloudDAO.downloadDicomStore(query);

        File[] dcmDataFiles = downloadDirectory.listFiles();
        List<File> tempFiles = new ArrayList<>();
        if (dcmDataFiles != null && dcmDataFiles.length > 0) {
            for (File dcmDataFile : dcmDataFiles) {
                DcmToDataConverter dcmToDataConverter = new DcmToDataConverter(dcmDataFile, projectDataDirectory);
                dcmToDataConverter.convertDcmToQuPathData();
            }
            tempFiles.addAll(Arrays.asList(dcmDataFiles));
        }

        deleteLocalFiles(tempFiles);
    }

    private void deleteLocalFiles(List<File> files) {
        for (File localFile : files) {
            if (!localFile.delete()) {
                LOGGER.warn("Failed to delete temp file: " + localFile);
            }
        }
    }

    private File prepareTempDirectory(String name) throws QuPathCloudException {
        File tempDirectory = new File(projectDirectory, name);
        if (!tempDirectory.exists()) {
            checkedMkDir(tempDirectory);
        }
        File[] dcmDataFiles = tempDirectory.listFiles();
        if (dcmDataFiles != null && dcmDataFiles.length > 0) {
            try {
                FileUtils.cleanDirectory(tempDirectory);
            } catch (IOException e) {
                throw new QuPathCloudException(e);
            }
        }
        return tempDirectory;
    }

    private String checkFileName(List<Series> remoteSeriesList, String localFileName) {
        List<String> remoteFileNames = new ArrayList<>();
        for (Series series : remoteSeriesList) {
            String remoteFileName = series.getImageComments().getValue1();
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

    private void checkedMkDir(File directory) throws QuPathCloudException {
        if (!directory.mkdir()) {
            throw new QuPathCloudException("Failed to create directory: " + directory);
        }
    }
}
