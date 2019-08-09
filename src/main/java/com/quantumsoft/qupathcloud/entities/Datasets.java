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
 * Datasets is the list of {@link com.quantumsoft.qupathcloud.entities.Dataset} retrieved from
 * the server.
 *
 * @see <a href="https://cloud.google.com/healthcare/docs/reference/rest/v1beta1/projects.locations.datasets/list">Datasets list</a>
 */
public class Datasets {

  private List<Dataset> datasets = new ArrayList<>();
  private String nextPageToken;

  /**
   * Gets the list of {@link com.quantumsoft.qupathcloud.entities.Dataset}.
   *
   * @return the list of {@link com.quantumsoft.qupathcloud.entities.Dataset}
   */
  public List<Dataset> getDatasets() {
    return datasets;
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
