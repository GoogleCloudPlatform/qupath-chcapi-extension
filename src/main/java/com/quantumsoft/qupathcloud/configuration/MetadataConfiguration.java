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
import com.quantumsoft.qupathcloud.entities.Series;
import com.quantumsoft.qupathcloud.entities.instance.Instance;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for saving metadata of all images for the current project.
 */
public class MetadataConfiguration {

  public static final String METADATA_FILE_EXTENSION = "mtd";
  private static final Logger LOGGER = LogManager.getLogger();
  private static final Path PROJECT_METADATA_INDEX_FILE = Paths.get("project.mtdp");
  private Path metadataDirectory;
  private Path projectMetadataIndexFile;
  private ObjectMapper mapper;

  /**
   * Instantiates a new Metadata configuration.
   *
   * @param metadataDirectory the metadata directory of the current project
   */
  public MetadataConfiguration(Path metadataDirectory) {
    this.metadataDirectory = metadataDirectory;
    projectMetadataIndexFile = metadataDirectory.resolve(PROJECT_METADATA_INDEX_FILE);
    mapper = new ObjectMapper();
  }

  /**
   * Save metadata file of the current image.
   *
   * @param series the series
   * @param instancesInSeries the instances in series
   * @return path to the saved metadata file
   * @throws QuPathCloudException if IOException occurs
   */
  public Path saveMetadataFile(Series series, List<Instance> instancesInSeries)
      throws QuPathCloudException {
    String seriesId = series.getSeriesInstanceUID().getValue1();
    Path metadataFile = Paths.get(seriesId + "." + METADATA_FILE_EXTENSION);
    Path pathToMetadataFile = metadataDirectory.resolve(metadataFile);
    try {
      LOGGER.debug("Start saving metadata file");
      mapper.writeValue(pathToMetadataFile.toFile(), instancesInSeries);
      return pathToMetadataFile;
    } catch (IOException e) {
      throw new QuPathCloudException(e);
    }
  }

  /**
   * Read metadata file of the current image.
   *
   * @param metadataFile the metadata file
   * @return list of instances of the current image
   * @throws QuPathCloudException if IOException occurs
   */
  public List<Instance> readMetadataFile(Path metadataFile) throws QuPathCloudException {
    try {
      LOGGER.debug("Start reading metadata file");
      return mapper.readValue(metadataFile.toFile(), new TypeReference<List<Instance>>() {});
    } catch (IOException e) {
      throw new QuPathCloudException(e);
    }
  }

  /**
   * Save the project metadata index file which contains information of all images in the project.
   *
   * @param seriesListInProject the series list in project
   * @throws QuPathCloudException if IOException occurs
   */
  public void saveProjectMetadataIndexFile(List<Series> seriesListInProject)
      throws QuPathCloudException {
    try {
      LOGGER.debug("Start saving metadata project file");
      mapper.writeValue(projectMetadataIndexFile.toFile(), seriesListInProject);
    } catch (IOException e) {
      throw new QuPathCloudException(e);
    }
  }

  /**
   * Read project metadata index file list.
   *
   * @return the list
   * @throws QuPathCloudException if IOException occurs
   */
  public List<Series> readProjectMetadataIndexFile() throws QuPathCloudException {
    try {
      LOGGER.debug("Start reading metadata project file");
      return mapper.readValue(projectMetadataIndexFile.toFile(),
          new TypeReference<List<Series>>() {});
    } catch (IOException e) {
      throw new QuPathCloudException(e);
    }
  }
}
