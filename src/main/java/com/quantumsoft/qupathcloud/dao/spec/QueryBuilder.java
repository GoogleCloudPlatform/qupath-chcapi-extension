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
import java.io.File;
import java.nio.file.Path;
import java.util.List;

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
  private File directory;
  private List<Location> locations;
  private List<Instance> instances;

  private QueryBuilder() {
  }

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

  public static QueryBuilder forProject(String projectId) {
    QueryBuilder builder = new QueryBuilder();
    builder.projectId = projectId;
    return builder;
  }

  public QueryBuilder setLocationId(String locationId) {
    this.locationId = locationId;
    return this;
  }

  public QueryBuilder setDatasetId(String datasetId) {
    this.datasetId = datasetId;
    return this;
  }

  public QueryBuilder setDicomStoreId(String dicomStoreId) {
    this.dicomStoreId = dicomStoreId;
    return this;
  }

  public QueryBuilder setStudyId(String studyId) {
    this.studyId = studyId;
    return this;
  }

  public QueryBuilder setSeriesId(String seriesId) {
    this.seriesId = seriesId;
    return this;
  }

  public QueryBuilder setInstanceId(String instanceId) {
    this.instanceId = instanceId;
    return this;
  }

  public QueryBuilder setFrameNumber(int frameNumber) {
    this.frameNumber = frameNumber;
    return this;
  }

  public QueryBuilder setPaths(List<Path> paths) {
    this.paths = paths;
    return this;
  }

  public QueryBuilder setDirectory(File directory) {
    this.directory = directory;
    return this;
  }

  public QueryBuilder setInstances(List<Instance> instances) {
    this.instances = instances;
    return this;
  }

  public QueryBuilder setLocations(List<Location> locations) {
    this.locations = locations;
    return this;
  }

  String getProjectId() {
    return projectId;
  }

  String getLocationId() {
    return locationId;
  }

  public String getDatasetId() {
    return datasetId;
  }

  public String getDicomStoreId() {
    return dicomStoreId;
  }

  String getStudyId() {
    return studyId;
  }

  String getSeriesId() {
    return seriesId;
  }

  String getInstanceId() {
    return instanceId;
  }

  int getFrameNumber() {
    return frameNumber;
  }

  public List<Path> getPaths() {
    return paths;
  }

  public File getDirectory() {
    return directory;
  }

  public List<Instance> getInstances() {
    return instances;
  }

  public List<Location> getLocations() {
    return locations;
  }
}
