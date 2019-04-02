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

package com.quantumsoft.qupathcloud.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quantumsoft.qupathcloud.entities.instance.Instance;
import com.quantumsoft.qupathcloud.entities.metadata.ImageMetadataIndex;
import com.quantumsoft.qupathcloud.entities.Series;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MetadataConfiguration {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String METADATA_FILE_EXTENSION = "mtd";
    private static final String PROJECT_METADATA_INDEX_FILE = "project.mtdp";
    private File metadataDirectory;
    private File projectMetadataIndexFile;
    private ObjectMapper mapper;

    public MetadataConfiguration(File metadataDirectory){
        this.metadataDirectory = metadataDirectory;
        projectMetadataIndexFile = new File(metadataDirectory, PROJECT_METADATA_INDEX_FILE);
        mapper = new ObjectMapper();
    }

    public File saveMetadataFile(Series series, List<Instance> instancesInSeries)
            throws QuPathCloudException{
        String seriesId = series.getSeriesInstanceUID().getValue1();
        File metadataFile = new File(metadataDirectory, seriesId + "." + METADATA_FILE_EXTENSION);
        try {
            LOGGER.debug("Start saving metadata file");
            mapper.writeValue(metadataFile, instancesInSeries);
            return metadataFile;
        } catch (IOException e) {
            throw new QuPathCloudException(e);
        }
    }

    public List<Instance> readMetadataFile(File metadataFile) throws QuPathCloudException {
        try {
            LOGGER.debug("Start reading metadata file");
            return mapper.readValue(metadataFile, new TypeReference<List<Instance>>() {
            });
        } catch (IOException e) {
            throw new QuPathCloudException(e);
        }
    }

    public void saveProjectMetadataIndexFile(List<ImageMetadataIndex> imageMetadataIndexList)
            throws QuPathCloudException{
        try {
            LOGGER.debug("Start saving metadata index file");
            mapper.writeValue(projectMetadataIndexFile, imageMetadataIndexList);
        } catch (IOException e) {
            throw new QuPathCloudException(e);
        }
    }

    public List<ImageMetadataIndex> readProjectMetadataIndexFile() throws QuPathCloudException {
        try {
            LOGGER.debug("Start reading metadata index file");
            return mapper.readValue(projectMetadataIndexFile, new TypeReference<List<ImageMetadataIndex>>() {
            });
        } catch (IOException e) {
            throw new QuPathCloudException(e);
        }
    }
}
