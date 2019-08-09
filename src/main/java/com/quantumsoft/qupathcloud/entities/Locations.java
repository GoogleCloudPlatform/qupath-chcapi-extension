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

import java.util.ArrayList;
import java.util.List;

/**
 * Locations contains a list of {@link com.quantumsoft.qupathcloud.entities.Location}
 * retrieved from the server.
 *
 * @see <a href="https://cloud.google.com/healthcare/docs/reference/rest/v1beta1/projects.locations/list">Locations list</a>
 */
public class Locations {

  private List<Location> locations = new ArrayList<>();
  private String nextPageToken;

  /**
   * Gets the list of {@link com.quantumsoft.qupathcloud.entities.Location}.
   *
   * @return the list of {@link com.quantumsoft.qupathcloud.entities.Location}
   */
  public List<Location> getLocations() {
    return locations;
  }

  /**
   * Gets next page token.
   *
   * @return the next page token
   */
  public String getNextPageToken() {
    return nextPageToken;
  }
}
