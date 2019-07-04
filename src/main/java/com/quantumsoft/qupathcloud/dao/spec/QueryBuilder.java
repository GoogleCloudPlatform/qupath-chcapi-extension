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

package com.quantumsoft.qupathcloud.dao.spec;

import com.quantumsoft.qupathcloud.entities.Location;
import com.quantumsoft.qupathcloud.entities.instance.Instance;
import java.nio.file.Path;
import java.util.List;

/**
 * Query builder for creating a query for Healthcare API.
 */
public class QueryBuilder {

  private String projectId;
  private String locationId;
  private String datasetId;
  private String dicomStoreId;
  private String studyId;
  private String seriesId;
  private String instanceId;
  private int frameNumber;
  private List<Path> paths;
  private Path directory;
  private List<Location> locations;
  private List<Instance> instances;

  private QueryBuilder() {
  }

  /**
   * Instantiates a new Query builder.
   *
   * @param original the original
   */
  public QueryBuilder(QueryBuilder original) {
    this.projectId = original.projectId;
    this.locationId = original.locationId;
    this.datasetId = original.datasetId;
    this.dicomStoreId = original.dicomStoreId;
    this.studyId = original.studyId;
    this.seriesId = original.seriesId;
    this.instanceId = original.instanceId;
    this.frameNumber = original.frameNumber;
    this.paths = original.paths;
    this.directory = original.directory;
    this.locations = original.locations;
    this.instances = original.instances;
  }

  /**
   * For project query builder.
   *
   * @param projectId the project id
   * @return the query builder
   */
  public static QueryBuilder forProject(String projectId) {
    QueryBuilder builder = new QueryBuilder();
    builder.projectId = projectId;
    return builder;
  }

  /**
   * Sets location id.
   *
   * @param locationId the location id
   * @return the location id
   */
  public QueryBuilder setLocationId(String locationId) {
    this.locationId = locationId;
    return this;
  }

  /**
   * Sets dataset id.
   *
   * @param datasetId the dataset id
   * @return the dataset id
   */
  public QueryBuilder setDatasetId(String datasetId) {
    this.datasetId = datasetId;
    return this;
  }

  /**
   * Sets dicom store id.
   *
   * @param dicomStoreId the dicom store id
   * @return the dicom store id
   */
  public QueryBuilder setDicomStoreId(String dicomStoreId) {
    this.dicomStoreId = dicomStoreId;
    return this;
  }

  /**
   * Sets study id.
   *
   * @param studyId the study id
   * @return the study id
   */
  public QueryBuilder setStudyId(String studyId) {
    this.studyId = studyId;
    return this;
  }

  /**
   * Sets series id.
   *
   * @param seriesId the series id
   * @return the series id
   */
  public QueryBuilder setSeriesId(String seriesId) {
    this.seriesId = seriesId;
    return this;
  }

  /**
   * Sets instance id.
   *
   * @param instanceId the instance id
   * @return the instance id
   */
  public QueryBuilder setInstanceId(String instanceId) {
    this.instanceId = instanceId;
    return this;
  }

  /**
   * Sets frame number.
   *
   * @param frameNumber the frame number
   * @return the frame number
   */
  public QueryBuilder setFrameNumber(int frameNumber) {
    this.frameNumber = frameNumber;
    return this;
  }

  /**
   * Sets paths.
   *
   * @param paths the paths
   * @return the paths
   */
  public QueryBuilder setPaths(List<Path> paths) {
    this.paths = paths;
    return this;
  }

  /**
   * Sets directory.
   *
   * @param directory the directory
   * @return the directory
   */
  public QueryBuilder setDirectory(Path directory) {
    this.directory = directory;
    return this;
  }

  /**
   * Sets instances.
   *
   * @param instances the instances
   * @return the instances
   */
  public QueryBuilder setInstances(List<Instance> instances) {
    this.instances = instances;
    return this;
  }

  /**
   * Sets locations.
   *
   * @param locations the locations
   * @return the locations
   */
  public QueryBuilder setLocations(List<Location> locations) {
    this.locations = locations;
    return this;
  }

  /**
   * Gets project id.
   *
   * @return the project id
   */
  String getProjectId() {
    return projectId;
  }

  /**
   * Gets location id.
   *
   * @return the location id
   */
  String getLocationId() {
    return locationId;
  }

  /**
   * Gets dataset id.
   *
   * @return the dataset id
   */
  public String getDatasetId() {
    return datasetId;
  }

  /**
   * Gets dicom store id.
   *
   * @return the dicom store id
   */
  public String getDicomStoreId() {
    return dicomStoreId;
  }

  /**
   * Gets study id.
   *
   * @return the study id
   */
  String getStudyId() {
    return studyId;
  }

  /**
   * Gets series id.
   *
   * @return the series id
   */
  String getSeriesId() {
    return seriesId;
  }

  /**
   * Gets instance id.
   *
   * @return the instance id
   */
  String getInstanceId() {
    return instanceId;
  }

  /**
   * Gets frame number.
   *
   * @return the frame number
   */
  int getFrameNumber() {
    return frameNumber;
  }

  /**
   * Gets paths.
   *
   * @return the paths
   */
  public List<Path> getPaths() {
    return paths;
  }

  /**
   * Gets directory.
   *
   * @return the directory
   */
  public Path getDirectory() {
    return directory;
  }

  /**
   * Gets instances.
   *
   * @return the instances
   */
  public List<Instance> getInstances() {
    return instances;
  }

  /**
   * Gets locations.
   *
   * @return the locations
   */
  public List<Location> getLocations() {
    return locations;
  }
}
