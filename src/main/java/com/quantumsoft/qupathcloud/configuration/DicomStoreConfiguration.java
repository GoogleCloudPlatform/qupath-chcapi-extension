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

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quantumsoft.qupathcloud.entities.DicomStore;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * DICOM Store configuration for current project in QuPath.
 */
public class DicomStoreConfiguration {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final Path configurationFileName = Paths.get("conf.json");
  private Path configurationFileInProjectDirectory;
  private ObjectMapper mapper;

  /**
   * Instantiates a new DICOM Store configuration.
   *
   * @param projectDirectory the project directory for the current project
   */
  public DicomStoreConfiguration(Path projectDirectory) {
    configurationFileInProjectDirectory = projectDirectory.resolve(configurationFileName);
    mapper = new ObjectMapper();
  }

  /**
   * Read configuration DICOM Store.
   *
   * @return the DICOM Store
   * @throws QuPathCloudException if IOException occurs
   */
  public DicomStore readConfiguration() throws QuPathCloudException {
    if (Files.exists(configurationFileInProjectDirectory, NOFOLLOW_LINKS)) {
      try {
        LOGGER.debug("Start reading configuration");
        return mapper.readValue(configurationFileInProjectDirectory.toFile(), DicomStore.class);
      } catch (IOException e) {
        throw new QuPathCloudException(e);
      }
    }
    return null;
  }

  /**
   * Save configuration.
   *
   * @param selectedDicomStore the selected DICOM Store for the current project
   * @throws QuPathCloudException if IOException occurs
   */
  public void saveConfiguration(DicomStore selectedDicomStore) throws QuPathCloudException {
    try {
      LOGGER.debug("Start writing configuration");
      mapper.writeValue(configurationFileInProjectDirectory.toFile(), selectedDicomStore);
    } catch (IOException e) {
      throw new QuPathCloudException(e);
    }
  }
}
