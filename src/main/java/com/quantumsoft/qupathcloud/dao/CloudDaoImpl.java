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

package com.quantumsoft.qupathcloud.dao;

import static com.quantumsoft.qupathcloud.dao.Constants.APPLICATION_JSON_CHARSET_UTF8;
import static com.quantumsoft.qupathcloud.dao.Constants.BEARER;
import static com.quantumsoft.qupathcloud.dao.Constants.CLOUD_RESOURCE_MANAGER_HOST;
import static com.quantumsoft.qupathcloud.dao.Constants.HEALTHCARE_HOST;
import static com.quantumsoft.qupathcloud.dao.Constants.INSTANCES;
import static com.quantumsoft.qupathcloud.dao.Constants.MULTIPART_RELATED_TYPE_IMAGE_JPEG_TRANSFER_SYNTAX;
import static com.quantumsoft.qupathcloud.dao.Constants.PARAM_DATASET_ID;
import static com.quantumsoft.qupathcloud.dao.Constants.PARAM_DICOM_STORE_ID;
import static com.quantumsoft.qupathcloud.dao.Constants.PARAM_INCLUDE_FIELD;
import static com.quantumsoft.qupathcloud.dao.Constants.PARAM_PAGE_TOKEN;
import static com.quantumsoft.qupathcloud.dao.Constants.PATH_TO_PROJECTS;
import static com.quantumsoft.qupathcloud.dao.Constants.SCHEME;
import static com.quantumsoft.qupathcloud.dao.Constants.SERIES;
import static com.quantumsoft.qupathcloud.dao.Constants.VALUE_PARAM_CONCATENATION_FRAME_OFFSET_NUMBER;
import static com.quantumsoft.qupathcloud.dao.Constants.VALUE_PARAM_CONCATENATION_UID;
import static com.quantumsoft.qupathcloud.dao.Constants.VALUE_PARAM_DIMENSION_ORGANIZATION_TYPE;
import static com.quantumsoft.qupathcloud.dao.Constants.VALUE_PARAM_IMAGE_COMMENTS;
import static com.quantumsoft.qupathcloud.dao.Constants.VALUE_PARAM_INSTANCE_CREATION_DATE;
import static com.quantumsoft.qupathcloud.dao.Constants.VALUE_PARAM_INSTANCE_CREATION_TIME;
import static com.quantumsoft.qupathcloud.dao.Constants.VALUE_PARAM_MODALITY;
import static com.quantumsoft.qupathcloud.dao.Constants.VALUE_PARAM_NUMBER_OF_FRAMES;
import static com.quantumsoft.qupathcloud.dao.Constants.VALUE_PARAM_PER_FRAME_FUNCTIONAL_GROUP_SEQUENCE;
import static com.quantumsoft.qupathcloud.dao.Constants.VALUE_PARAM_SERIES_INSTANCE_UID;
import static com.quantumsoft.qupathcloud.dao.Constants.VALUE_PARAM_SOP_AUTHORIZATHION_COMMENT;
import static com.quantumsoft.qupathcloud.dao.Constants.VALUE_PARAM_STUDY_INSTANCE_UID;
import static com.quantumsoft.qupathcloud.dao.Constants.VALUE_PARAM_TIMEZONE_OFFSET_FROM_UTC;
import static com.quantumsoft.qupathcloud.dao.Constants.VALUE_PARAM_TOTAL_PIXEL_MATRIX_COLUMNS;
import static com.quantumsoft.qupathcloud.dao.Constants.VALUE_PARAM_TOTAL_PIXEL_MATRIX_ROWS;
import static com.quantumsoft.qupathcloud.exception.Errors.FAILED_HTTP;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpStatusCodes;
import com.quantumsoft.qupathcloud.dao.spec.DatasetsPathBuilder;
import com.quantumsoft.qupathcloud.dao.spec.DicomStoresPathBuilder;
import com.quantumsoft.qupathcloud.dao.spec.FramePathBuilder;
import com.quantumsoft.qupathcloud.dao.spec.InstancesPathBuilder;
import com.quantumsoft.qupathcloud.dao.spec.LocationsPathBuilder;
import com.quantumsoft.qupathcloud.dao.spec.QueryBuilder;
import com.quantumsoft.qupathcloud.dao.spec.SeriesPathBuilder;
import com.quantumsoft.qupathcloud.dao.spec.StudiesPathBuilder;
import com.quantumsoft.qupathcloud.entities.Dataset;
import com.quantumsoft.qupathcloud.entities.Datasets;
import com.quantumsoft.qupathcloud.entities.DicomStore;
import com.quantumsoft.qupathcloud.entities.DicomStores;
import com.quantumsoft.qupathcloud.entities.Location;
import com.quantumsoft.qupathcloud.entities.Locations;
import com.quantumsoft.qupathcloud.entities.Project;
import com.quantumsoft.qupathcloud.entities.Projects;
import com.quantumsoft.qupathcloud.entities.Series;
import com.quantumsoft.qupathcloud.entities.Study;
import com.quantumsoft.qupathcloud.entities.instance.Instance;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import com.quantumsoft.qupathcloud.oauth20.OAuth20;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.imageio.ImageIO;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Implementation of Cloud dao for access to Healthcare API.
 */
public class CloudDaoImpl extends CloudDao {

  private static final String DCM_EXTENSION = "dcm";
  private static final int THREADS_COUNT = 4;
  private static final int R = 0x0d; // "\r"
  private static final int N = 0x0a; // "\n"
  private static final int H = 0x2d; // "-"

  /**
   * Instantiates a new Cloud dao.
   *
   * @param oAuth20 the o auth 20
   */
  public CloudDaoImpl(OAuth20 oAuth20) {
    super(oAuth20);
  }

  @Override
  public List<Project> getProjectsList() throws QuPathCloudException {
    URIBuilder uriBuilder = new URIBuilder()
        .setScheme(SCHEME)
        .setHost(CLOUD_RESOURCE_MANAGER_HOST)
        .setPath(PATH_TO_PROJECTS);
    Projects projectsOnPage =
        createRequestForObjectList(uriBuilder, new TypeReference<Projects>() {});
    List<Project> projectList = new ArrayList<>(projectsOnPage.getProjects());
    String nextPageToken = projectsOnPage.getNextPageToken();
    while (nextPageToken != null) {
      uriBuilder.addParameter(PARAM_PAGE_TOKEN, nextPageToken);
      projectsOnPage = createRequestForObjectList(uriBuilder, new TypeReference<Projects>() {});
      projectList.addAll(projectsOnPage.getProjects());
      nextPageToken = projectsOnPage.getNextPageToken();
    }
    return projectList;
  }

  @Override
  public List<Location> getLocationsList(QueryBuilder queryBuilder) throws QuPathCloudException {
    LocationsPathBuilder locationsPathBuilder = new LocationsPathBuilder(queryBuilder);
    URIBuilder uriBuilder = new URIBuilder()
        .setScheme(SCHEME)
        .setHost(HEALTHCARE_HOST)
        .setPath(locationsPathBuilder.toPath());
    return createRequestForObjectList(uriBuilder, new TypeReference<Locations>() {}).getLocations();
  }

  @Override
  public List<Dataset> getDatasetsListInAllLocations(QueryBuilder queryBuilder)
      throws QuPathCloudException {
    List<Dataset> datasetList = new ArrayList<>();
    for (Location location : queryBuilder.getLocations()) {
      queryBuilder.setLocationId(location.getLocationId());
      List<Dataset> datasetsInLocation = getDatasetsList(queryBuilder);
      datasetList.addAll(datasetsInLocation);
    }
    return datasetList;
  }

  @Override
  public List<DicomStore> getDicomStoresList(QueryBuilder queryBuilder)
      throws QuPathCloudException {
    DicomStoresPathBuilder dicomStoresPathBuilder = new DicomStoresPathBuilder(queryBuilder);
    URIBuilder uriBuilder = new URIBuilder()
        .setScheme(SCHEME)
        .setHost(HEALTHCARE_HOST)
        .setPath(dicomStoresPathBuilder.toPath());
    DicomStores dicomStoresOnPage =
        createRequestForObjectList(uriBuilder, new TypeReference<DicomStores>() {});
    List<DicomStore> dicomStoresList = new ArrayList<>(dicomStoresOnPage.getDicomStores());
    String nextPageToken = dicomStoresOnPage.getNextPageToken();
    while (nextPageToken != null) {
      uriBuilder.addParameter(PARAM_PAGE_TOKEN, nextPageToken);
      dicomStoresOnPage =
          createRequestForObjectList(uriBuilder, new TypeReference<DicomStores>() {});
      dicomStoresList.addAll(dicomStoresOnPage.getDicomStores());
      nextPageToken = dicomStoresOnPage.getNextPageToken();
    }
    return dicomStoresList;
  }

  @Override
  public List<Study> getStudiesList(QueryBuilder queryBuilder) throws QuPathCloudException {
    StudiesPathBuilder studiesPathBuilder = new StudiesPathBuilder(queryBuilder);
    URIBuilder uriBuilder = new URIBuilder()
        .setScheme(SCHEME)
        .setHost(HEALTHCARE_HOST)
        .setPath(studiesPathBuilder.toPath());
    return createRequestForObjectList(uriBuilder, new TypeReference<List<Study>>() {});
  }

  @Override
  public List<Series> getSeriesList(QueryBuilder queryBuilder) throws QuPathCloudException {
    SeriesPathBuilder seriesPathBuilder = new SeriesPathBuilder(queryBuilder);
    URIBuilder uriBuilder = new URIBuilder()
        .setScheme(SCHEME)
        .setHost(HEALTHCARE_HOST)
        .setParameter(PARAM_INCLUDE_FIELD, VALUE_PARAM_IMAGE_COMMENTS)
        .setPath(seriesPathBuilder.toPath());
    return createRequestForObjectList(uriBuilder, new TypeReference<List<Series>>() {});
  }

  @Override
  public List<Instance> getInstancesList(QueryBuilder queryBuilder) throws QuPathCloudException {
    InstancesPathBuilder instancesPathBuilder = new InstancesPathBuilder(queryBuilder);
    URIBuilder uriBuilder = new URIBuilder()
        .setScheme(SCHEME)
        .setHost(HEALTHCARE_HOST)
        .addParameter(PARAM_INCLUDE_FIELD, VALUE_PARAM_MODALITY)
        .addParameter(PARAM_INCLUDE_FIELD, VALUE_PARAM_STUDY_INSTANCE_UID)
        .addParameter(PARAM_INCLUDE_FIELD, VALUE_PARAM_SERIES_INSTANCE_UID)
        .addParameter(PARAM_INCLUDE_FIELD, VALUE_PARAM_TOTAL_PIXEL_MATRIX_COLUMNS)
        .addParameter(PARAM_INCLUDE_FIELD, VALUE_PARAM_TOTAL_PIXEL_MATRIX_ROWS)
        .addParameter(PARAM_INCLUDE_FIELD, VALUE_PARAM_PER_FRAME_FUNCTIONAL_GROUP_SEQUENCE)
        .addParameter(PARAM_INCLUDE_FIELD, VALUE_PARAM_INSTANCE_CREATION_DATE)
        .addParameter(PARAM_INCLUDE_FIELD, VALUE_PARAM_INSTANCE_CREATION_TIME)
        .addParameter(PARAM_INCLUDE_FIELD, VALUE_PARAM_TIMEZONE_OFFSET_FROM_UTC)
        .addParameter(PARAM_INCLUDE_FIELD, VALUE_PARAM_SOP_AUTHORIZATHION_COMMENT)
        .addParameter(PARAM_INCLUDE_FIELD, VALUE_PARAM_DIMENSION_ORGANIZATION_TYPE)
        .addParameter(PARAM_INCLUDE_FIELD, VALUE_PARAM_CONCATENATION_UID)
        .addParameter(PARAM_INCLUDE_FIELD, VALUE_PARAM_CONCATENATION_FRAME_OFFSET_NUMBER)
        .addParameter(PARAM_INCLUDE_FIELD, VALUE_PARAM_NUMBER_OF_FRAMES)
        .setPath(instancesPathBuilder.toPath());
    return createRequestForObjectList(uriBuilder, new TypeReference<List<Instance>>() {});
  }

  @Override
  public BufferedImage getFrame(QueryBuilder queryBuilder) throws QuPathCloudException {
    FramePathBuilder framePathBuilder = new FramePathBuilder(queryBuilder);
    URIBuilder uriBuilder = new URIBuilder()
        .setScheme(SCHEME)
        .setHost(HEALTHCARE_HOST)
        .setPath(framePathBuilder.toPath());
    return createRequestForFrame(uriBuilder);
  }

  @Override
  public void createDataset(QueryBuilder queryBuilder) throws QuPathCloudException {
    DatasetsPathBuilder locationsPathBuilder = new DatasetsPathBuilder(queryBuilder);
    URIBuilder uriBuilder = new URIBuilder()
        .setScheme(SCHEME)
        .setHost(HEALTHCARE_HOST)
        .setPath(locationsPathBuilder.toPath())
        .setParameter(PARAM_DATASET_ID, queryBuilder.getDatasetId());
    createRequestForCreateQbject(uriBuilder);
  }

  @Override
  public void createDicomStore(QueryBuilder queryBuilder) throws QuPathCloudException {
    DicomStoresPathBuilder dicomStoresPathBuilder = new DicomStoresPathBuilder(queryBuilder);
    URIBuilder uriBuilder = new URIBuilder()
        .setScheme(SCHEME)
        .setHost(HEALTHCARE_HOST)
        .setPath(dicomStoresPathBuilder.toPath())
        .setParameter(PARAM_DICOM_STORE_ID, queryBuilder.getDicomStoreId());
    createRequestForCreateQbject(uriBuilder);
  }

  @Override
  public void uploadToDicomStore(QueryBuilder queryBuilder) throws QuPathCloudException {
    StudiesPathBuilder studiesPathBuilder = new StudiesPathBuilder(queryBuilder);
    URIBuilder uriBuilder = new URIBuilder()
        .setScheme(SCHEME)
        .setHost(HEALTHCARE_HOST)
        .setPath(studiesPathBuilder.toPath());
    ExecutorService executorService = Executors.newFixedThreadPool(THREADS_COUNT);
    List<Future<Void>> list = new ArrayList<>();
    for (Path inputFile : queryBuilder.getPaths()) {
      UploadDicomCallable uploadDicomCallable =
          new UploadDicomCallable(getoAuth20(), inputFile, uriBuilder);
      Future<Void> future = executorService.submit(uploadDicomCallable);
      list.add(future);
    }
    for (Future<Void> future : list) {
      try {
        future.get();
      } catch (InterruptedException | ExecutionException e) {
        throw new QuPathCloudException(e);
      }
    }
    executorService.shutdown();
  }

  @Override
  public void downloadInstances(QueryBuilder queryBuilder)
      throws QuPathCloudException {
    StudiesPathBuilder studiesPathBuilder = new StudiesPathBuilder(queryBuilder);
    ExecutorService executorService = Executors.newFixedThreadPool(THREADS_COUNT);
    List<Future<Void>> list = new ArrayList<>();
    for (Instance instance : queryBuilder.getInstances()) {
      String studyValue = instance.getStudyInstanceUID().getValue1();
      String seriesValue = instance.getSeriesInstanceUID().getValue1();
      String instanceValue = instance.getSopInstanceUID().getValue1();
      URIBuilder uriBuilderInstance = new URIBuilder()
          .setScheme(SCHEME)
          .setHost(HEALTHCARE_HOST)
          .setPath(studiesPathBuilder.toPath() + studyValue + SERIES + seriesValue + INSTANCES
              + instanceValue);
      Path outputDirectory = queryBuilder.getDirectory();
      Path outputFile = outputDirectory.resolve(instanceValue + "." + DCM_EXTENSION);
      DownloadDicomCallable downloadInstance =
          new DownloadDicomCallable(getoAuth20(), outputFile, uriBuilderInstance);
      Future<Void> future = executorService.submit(downloadInstance);
      list.add(future);
    }
    for (Future<Void> future : list) {
      try {
        future.get();
      } catch (InterruptedException | ExecutionException e) {
        throw new QuPathCloudException(e);
      }
    }
    executorService.shutdown();
  }

  @Override
  public void deleteInstances(QueryBuilder queryBuilder) throws QuPathCloudException {
    StudiesPathBuilder studiesPathBuilder = new StudiesPathBuilder(queryBuilder);
    ExecutorService executorService = Executors.newFixedThreadPool(THREADS_COUNT);
    List<Future<Void>> list = new ArrayList<>();
    for (Instance instance : queryBuilder.getInstances()) {
      String studyValue = instance.getStudyInstanceUID().getValue1();
      String seriesValue = instance.getSeriesInstanceUID().getValue1();
      String instanceValue = instance.getSopInstanceUID().getValue1();
      URIBuilder uriBuilderInstance = new URIBuilder()
          .setScheme(SCHEME)
          .setHost(HEALTHCARE_HOST)
          .setPath(studiesPathBuilder.toPath() + studyValue + SERIES + seriesValue + INSTANCES
              + instanceValue);
      DeleteInstanceCallable deleteInstanceCallable =
          new DeleteInstanceCallable(getoAuth20(), uriBuilderInstance);
      Future<Void> future = executorService.submit(deleteInstanceCallable);
      list.add(future);
    }
    for (Future<Void> future : list) {
      try {
        future.get();
      } catch (InterruptedException | ExecutionException e) {
        throw new QuPathCloudException(e);
      }
    }
    executorService.shutdown();
  }

  private <T> T createRequestForObjectList(URIBuilder uriBuilder, TypeReference<T> typeReference)
      throws QuPathCloudException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    T result;
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      URI uri = uriBuilder.build();
      HttpGet request = new HttpGet(uri);
      request.addHeader(CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF8);
      Credential credential = getoAuth20().getCredential();
      request.addHeader(AUTHORIZATION, BEARER + credential.getAccessToken());
      try (CloseableHttpResponse response = httpclient.execute(request)) {
        checkStatusCode(response);

        try (InputStream inputStream = response.getEntity().getContent()) {
          result = mapper.readValue(inputStream, typeReference);
        }
      }
    } catch (IOException | URISyntaxException e) {
      throw new QuPathCloudException(e);
    }
    return result;
  }

  private void createRequestForCreateQbject(URIBuilder uriBuilder) throws QuPathCloudException {
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      URI uri = uriBuilder.build();
      HttpPost request = new HttpPost(uri);
      request.addHeader(CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF8);
      Credential credential = getoAuth20().getCredential();
      request.addHeader(AUTHORIZATION, BEARER + credential.getAccessToken());
      try (CloseableHttpResponse response = httpclient.execute(request)) {
        checkStatusCode(response);
      }
    } catch (IOException | URISyntaxException e) {
      throw new QuPathCloudException(e);
    }
  }

  private BufferedImage createRequestForFrame(URIBuilder uriBuilder) throws QuPathCloudException {
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      URI uri = uriBuilder.build();
      HttpGet request = new HttpGet(uri);
      request.addHeader(CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF8);
      request.addHeader(ACCEPT, MULTIPART_RELATED_TYPE_IMAGE_JPEG_TRANSFER_SYNTAX);
      Credential credential = getoAuth20().getCredential();
      request.addHeader(AUTHORIZATION, BEARER + credential.getAccessToken());
      try (CloseableHttpResponse response = httpclient.execute(request)) {
        checkStatusCode(response);
        HttpEntity entity = response.getEntity();

        try (InputStream inputStream = entity.getContent()) {
          InputStream imageInputStream = cutHeadersAndTopBoundary(inputStream);
          return ImageIO.read(imageInputStream);
        }
      }
    } catch (IOException | URISyntaxException e) {
      throw new QuPathCloudException(e);
    }
  }

  private List<Dataset> getDatasetsList(QueryBuilder queryBuilder) throws QuPathCloudException {
    DatasetsPathBuilder datasetsPathBuilder = new DatasetsPathBuilder(queryBuilder);
    URIBuilder uriBuilder = new URIBuilder()
        .setScheme(SCHEME)
        .setHost(HEALTHCARE_HOST)
        .setPath(datasetsPathBuilder.toPath());
    Datasets datasetsOnPage =
        createRequestForObjectList(uriBuilder, new TypeReference<Datasets>() {});
    List<Dataset> datasetsInProjectLocation = new ArrayList<>(datasetsOnPage.getDatasets());
    String nextPageToken = datasetsOnPage.getNextPageToken();
    while (nextPageToken != null) {
      uriBuilder.addParameter(PARAM_PAGE_TOKEN, nextPageToken);
      datasetsOnPage = createRequestForObjectList(uriBuilder, new TypeReference<Datasets>() {});
      datasetsInProjectLocation.addAll(datasetsOnPage.getDatasets());
      nextPageToken = datasetsOnPage.getNextPageToken();
    }
    return datasetsInProjectLocation;
  }

  /**
   * This method cuts only the top headers and the top boundary besides the down boundary. It's
   * necessary for a delay of ~ 1 millisecond when the method cuts inputStream. This method works
   * only for cuts an image.
   *
   * @param inputStream without cuts body.
   * @return cut inputStream.
   * @throws IOException when read method throws an exception.
   */
  private InputStream cutHeadersAndTopBoundary(InputStream inputStream) throws IOException {
    int b;
    while ((b = inputStream.read()) != -1) {
      if (b == R) {
        b = inputStream.read();
        if (b == N) {
          b = inputStream.read();
          if (b == R) {
            b = inputStream.read();
            if (b == N) {
              break;
            }
          }
        }
      }
    }
    return inputStream;
  }

  /**
   * This method cuts the top headers and the top boundary with the bottom boundary. The minus of
   * this method is a delay of ~ 0.2 - 0.5 seconds during cuts.
   *
   * @param inputStream without cuts body.
   * @return cut inputStream.
   * @throws IOException when toByteArray method throws an exception.
   */
  static InputStream cutHeadersAndBoundary(InputStream inputStream) throws IOException {
    byte[] body = toByteArray(inputStream);
    int bodyLength = body.length;
    int startImageBody = 0;
    for (int i = 0; i < bodyLength - 1; i++) {
      if (body[i] == R && body[i + 1] == N && body[i + 2] == R && body[i + 3] == N) {
        startImageBody = i + 4; //inclusive
        break;
      }
    }
    int endImageBody = 0;
    for (int i = bodyLength - 1; i > 0; i--) {
      if (body[i] == H && body[i - 1] == H && body[i - 2] == N && body[i - 3] == R) {
        endImageBody = i - 3; //not inclusive
        break;
      }
    }
    int imageBodyLength = bodyLength - (bodyLength - endImageBody);
    byte[] imageBody = Arrays.copyOfRange(body, startImageBody, imageBodyLength);
    return new ByteArrayInputStream(imageBody);
  }

  private void checkStatusCode(CloseableHttpResponse response) throws QuPathCloudException {
    int statusCode = response.getStatusLine().getStatusCode();
    if (statusCode != HttpStatusCodes.STATUS_CODE_OK) {
      throw new QuPathCloudException(FAILED_HTTP + statusCode);
    }
  }
}
