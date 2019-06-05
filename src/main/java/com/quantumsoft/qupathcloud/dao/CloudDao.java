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

import com.quantumsoft.qupathcloud.dao.spec.QueryBuilder;
import com.quantumsoft.qupathcloud.entities.Dataset;
import com.quantumsoft.qupathcloud.entities.DicomStore;
import com.quantumsoft.qupathcloud.entities.Location;
import com.quantumsoft.qupathcloud.entities.Project;
import com.quantumsoft.qupathcloud.entities.Series;
import com.quantumsoft.qupathcloud.entities.Study;
import com.quantumsoft.qupathcloud.entities.instance.Instance;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import com.quantumsoft.qupathcloud.oauth20.OAuth20;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Cloud dao.
 */
public abstract class CloudDao {

  private OAuth20 oAuth20;

  /**
   * Instantiates a new Cloud dao.
   *
   * @param oAuth20 the oAuth20
   */
  CloudDao(OAuth20 oAuth20) {
    this.oAuth20 = oAuth20;
  }

  /**
   * Gets Projects list.
   *
   * @return the list of Projects
   * @throws QuPathCloudException if an error occurs
   */
  public abstract List<Project> getProjectsList() throws QuPathCloudException;

  /**
   * Gets Locations list.
   *
   * @param queryBuilder the query builder
   * @return the list of Locations
   * @throws QuPathCloudException if an error occurs
   */
  public abstract List<Location> getLocationsList(QueryBuilder queryBuilder)
      throws QuPathCloudException;

  /**
   * Gets Datasets list in all locations.
   *
   * @param queryBuilder the query builder
   * @return the list of Datasets in all locations
   * @throws QuPathCloudException if an error occurs
   */
  public abstract List<Dataset> getDatasetsListInAllLocations(QueryBuilder queryBuilder)
      throws QuPathCloudException;

  /**
   * Gets DICOM Stores list.
   *
   * @param queryBuilder the query builder
   * @return the list of DICOM Stores
   * @throws QuPathCloudException if an error occurs
   */
  public abstract List<DicomStore> getDicomStoresList(QueryBuilder queryBuilder)
      throws QuPathCloudException;

  /**
   * Gets Studies list.
   *
   * @param queryBuilder the query builder
   * @return the list of Studies
   * @throws QuPathCloudException if an error occurs
   */
  public abstract List<Study> getStudiesList(QueryBuilder queryBuilder) throws QuPathCloudException;

  /**
   * Gets Series list.
   *
   * @param queryBuilder the query builder
   * @return the list of Series
   * @throws QuPathCloudException if an error occurs
   */
  public abstract List<Series> getSeriesList(QueryBuilder queryBuilder) throws QuPathCloudException;

  /**
   * Gets Instances list.
   *
   * @param queryBuilder the query builder
   * @return the list of Instances
   * @throws QuPathCloudException if an error occurs
   */
  public abstract List<Instance> getInstancesList(QueryBuilder queryBuilder)
      throws QuPathCloudException;

  /**
   * Gets frame.
   *
   * @param queryBuilder the query builder
   * @return the frame
   * @throws QuPathCloudException if an error occurs
   */
  public abstract BufferedImage getFrame(QueryBuilder queryBuilder) throws QuPathCloudException;

  /**
   * Create Dataset.
   *
   * @param queryBuilder the query builder
   * @throws QuPathCloudException if an error occurs
   */
  public abstract void createDataset(QueryBuilder queryBuilder) throws QuPathCloudException;

  /**
   * Create DICOM Store.
   *
   * @param queryBuilder the query builder
   * @throws QuPathCloudException if an error occurs
   */
  public abstract void createDicomStore(QueryBuilder queryBuilder) throws QuPathCloudException;

  /**
   * Upload DICOM files to DICOM Store.
   *
   * @param queryBuilder the query builder
   * @throws QuPathCloudException if an error occurs
   */
  public abstract void uploadToDicomStore(QueryBuilder queryBuilder) throws QuPathCloudException;

  /**
   * Download Instances list from DICOM Store.
   *
   * @param queryBuilder the query builder
   * @throws QuPathCloudException if an error occurs
   */
  public abstract void downloadInstances(QueryBuilder queryBuilder) throws QuPathCloudException;

  /**
   * Delete Instances list in DICOM Store.
   *
   * @param queryBuilder the query builder
   * @throws QuPathCloudException if an error occurs
   */
  public abstract void deleteInstances(QueryBuilder queryBuilder) throws QuPathCloudException;

  /**
   * Gets oAuth20.
   *
   * @return the oAuth20
   */
  public OAuth20 getoAuth20() {
    return oAuth20;
  }
}
