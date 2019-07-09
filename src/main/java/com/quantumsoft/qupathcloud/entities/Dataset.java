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
 * The Dataset class contains name and the specified timeZone.
 *
 * @see <a href="https://cloud.google.com/healthcare/docs/concepts/projects-datasets-data-stores">Dataset</a>
 */
public class Dataset {

  private String name;
  private String timeZone;

  /**
   * Gets Project ID.
   *
   * @return the Project ID
   */
  @JsonIgnore
  public String getProjectId() {
    String[] arr = name.split("/");
    return arr[arr.length - 5];
  }

  /**
   * Gets Location ID.
   *
   * @return the Location ID
   */
  @JsonIgnore
  public String getLocationId() {
    String[] arr = name.split("/");
    return arr[arr.length - 3];
  }

  /**
   * Gets Dataset ID.
   *
   * @return the Dataset ID
   */
  @JsonIgnore
  public String getDatasetId() {
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
   * @param projectId the Project ID
   * @param locationId the Location ID
   * @param datasetId the Dataset ID
   */
  public void setName(String projectId, String locationId, String datasetId) {
    name = "projects/" + projectId + "/locations/" + locationId + "/datasets/" + datasetId;
  }

  /**
   * Gets time zone.
   *
   * @return the time zone
   */
  public String getTimeZone() {
    return timeZone;
  }

  /**
   * Sets time zone.
   *
   * @param timeZone the time zone
   */
  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Dataset dataset = (Dataset) o;
    return name.equals(dataset.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
