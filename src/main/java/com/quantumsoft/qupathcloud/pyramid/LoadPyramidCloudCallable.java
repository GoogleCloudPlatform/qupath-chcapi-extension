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

package com.quantumsoft.qupathcloud.pyramid;

import com.quantumsoft.qupathcloud.dao.CloudDAO;
import com.quantumsoft.qupathcloud.dao.DAOHelper;
import com.quantumsoft.qupathcloud.dao.spec.QueryBuilder;
import com.quantumsoft.qupathcloud.entities.DicomStore;
import com.quantumsoft.qupathcloud.entities.instance.Instance;
import com.quantumsoft.qupathcloud.repository.Repository;

import java.util.List;
import java.util.concurrent.Callable;

public class LoadPyramidCloudCallable implements Callable<Pyramid> {
    private DAOHelper daoHelper;
    private DicomStore dicomStore;
    private CloudDAO cloudDAO;
    private String studyId;
    private String seriesId;

    public LoadPyramidCloudCallable(String studyId, String seriesId) {
        this.cloudDAO = Repository.INSTANCE.getCloudDao();
        dicomStore = Repository.INSTANCE.getDicomStore();
        this.studyId = studyId;
        this.seriesId = seriesId;
    }

    @Override
    public Pyramid call() throws Exception {
        QueryBuilder queryBuilder = QueryBuilder.forProject(dicomStore.getProjectId())
                .setLocationId(dicomStore.getLocationId())
                .setDatasetId(dicomStore.getDatasetId())
                .setDicomStoreId(dicomStore.getDicomStoreId())
                .setStudyId(studyId)
                .setSeriesId(seriesId);
        List<Instance> instances = cloudDAO.getInstancesList(queryBuilder);
        return new Pyramid(instances);
    }
}
