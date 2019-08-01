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

package com.quantumsoft.qupathcloud.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;

/**
 * The DicomStore class contains DICOM Store name. The name string contains Project ID, Location ID,
 * Dataset ID and DICOM Store ID.
 *
 * @see <a href="https://cloud.google.com/healthcare/docs/reference/rest/v1beta1/projects.locations.datasets.dicomStores">DicomStore</a>
 */
public class DicomStore {

  private String name;

  /**
   * Gets Project ID.
   *
   * @return the Project ID
   */
  @JsonIgnore
  public String getProjectId() {
    String[] arr = name.split("/");
    return arr[arr.length - 7];
  }

  /**
   * Gets Location ID.
   *
   * @return the Location ID
   */
  @JsonIgnore
  public String getLocationId() {
    String[] arr = name.split("/");
    return arr[arr.length - 5];
  }

  /**
   * Gets Dataset ID.
   *
   * @return the Dataset ID
   */
  @JsonIgnore
  public String getDatasetId() {
    String[] arr = name.split("/");
    return arr[arr.length - 3];
  }

  /**
   * Gets DICOM Store ID.
   *
   * @return the DICOM Store ID
   */
  @JsonIgnore
  public String getDicomStoreId() {
    String[] arr = name.split("/");
    return arr[arr.length - 1];
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets name.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets name.
   *
   * @param projectId    the Project ID
   * @param locationId   the Location ID
   * @param datasetId    the Dataset ID
   * @param dicomStoreId the DICOM Store ID
   */
  public void setName(String projectId, String locationId, String datasetId, String dicomStoreId) {
    name = "projects/" + projectId + "/locations/" + locationId + "/datasets/" + datasetId
        + "/dicomStores/" +
        dicomStoreId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DicomStore that = (DicomStore) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
