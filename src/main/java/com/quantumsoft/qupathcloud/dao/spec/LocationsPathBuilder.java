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

import static com.quantumsoft.qupathcloud.dao.Constants.LOCATIONS;
import static com.quantumsoft.qupathcloud.dao.Constants.PROJECTS;
import static com.quantumsoft.qupathcloud.dao.Constants.STAGE;

/**
 * Path builder for Locations.
 */
public class LocationsPathBuilder implements PathBuilder {

  private QueryBuilder queryBuilder;

  /**
   * Instantiates a new Locations path builder.
   *
   * @param queryBuilder the query builder
   */
  public LocationsPathBuilder(QueryBuilder queryBuilder) {
    this.queryBuilder = queryBuilder;
  }

  public String toPath() {
    return STAGE + PROJECTS + queryBuilder.getProjectId() + LOCATIONS;
  }
}
