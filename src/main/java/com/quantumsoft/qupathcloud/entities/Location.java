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

/**
 * The type Location.
 */
public class Location {

  private String name;
  private String locationId;

  /**
   * Gets Project id.
   *
   * @return the Project id
   */
  @JsonIgnore
  public String getProjectId() {
    String arr[] = name.split("/");
    return arr[arr.length - 3];
  }

  /**
   * Gets Location id.
   *
   * @return the Location id
   */
  public String getLocationId() {
    return locationId;
  }

  /**
   * Sets Location id.
   *
   * @param locationId the Location id
   */
  public void setLocationId(String locationId) {
    this.locationId = locationId;
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
}
