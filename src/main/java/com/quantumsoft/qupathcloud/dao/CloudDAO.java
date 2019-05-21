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
import com.quantumsoft.qupathcloud.entities.*;
import com.quantumsoft.qupathcloud.entities.instance.Instance;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import com.quantumsoft.qupathcloud.oauth20.OAuth20;

import java.awt.image.BufferedImage;
import java.util.List;

public abstract class CloudDAO{
    private OAuth20 oAuth20;

    CloudDAO(OAuth20 oAuth20){
        this.oAuth20 = oAuth20;
    }

    public abstract List<Project> getProjectsList() throws QuPathCloudException;
    public abstract List<Location> getLocationsList(QueryBuilder queryBuilder) throws QuPathCloudException;
    public abstract List<Dataset> getDatasetsListInAllLocations(QueryBuilder queryBuilder) throws QuPathCloudException;
    public abstract List<DicomStore> getDicomStoresList(QueryBuilder queryBuilder) throws QuPathCloudException;
    public abstract List<Study> getStudiesList(QueryBuilder queryBuilder) throws QuPathCloudException;
    public abstract List<Series> getSeriesList(QueryBuilder queryBuilder) throws QuPathCloudException;
    public abstract List<Instance> getInstancesList(QueryBuilder queryBuilder) throws QuPathCloudException;
    public abstract BufferedImage getFrame(QueryBuilder queryBuilder) throws QuPathCloudException;

    public abstract void createDataset(QueryBuilder queryBuilder) throws QuPathCloudException;
    public abstract void createDicomStore(QueryBuilder queryBuilder) throws QuPathCloudException;
    public abstract void uploadToDicomStore(QueryBuilder queryBuilder) throws QuPathCloudException;
    public abstract void downloadDicomStore(QueryBuilder queryBuilder) throws QuPathCloudException;
    public abstract void deleteInstances(QueryBuilder queryBuilder) throws QuPathCloudException;

    public OAuth20 getoAuth20() {
        return oAuth20;
    }
}
