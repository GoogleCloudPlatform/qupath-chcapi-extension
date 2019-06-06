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

package com.quantumsoft.qupathcloud.gui.qupath;

import com.quantumsoft.qupathcloud.gui.windows.CloudWindow;
import com.quantumsoft.qupathcloud.repository.Repository;
import com.quantumsoft.qupathcloud.synchronization.SynchronizationProjectWithDicomStore;
import java.io.IOException;
import javafx.scene.control.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.extensions.QuPathExtension;

/**
 * Cloud extension class to add this extension to QuPath.
 * @see <a href="https://github.com/qupath/qupath/wiki/Creating-extensions">Creating QuPath extensions</a>
 */
public class CloudExtension implements QuPathExtension {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String EXTENSION_NAME = "CHCAPI extension";
  private static final String EXTENSION_DESCRIPTION = "Adds integration with Google Cloud Healthcare API";

  public void installExtension(QuPathGUI qupath) {
    Button cloudButton = new Button("Cloud");
    Button synchronizeButton = new Button("Synchronize");
    Button logoutButton = new Button("Logout");

    cloudButton.setOnAction(e -> {
      CloudWindow window = new CloudWindow(qupath);
      window.showCloudWindow();
    });
    cloudButton.disableProperty().bind(qupath.projectProperty().isNull());

    synchronizeButton.setOnAction(event -> {
      SynchronizationProjectWithDicomStore sync = new SynchronizationProjectWithDicomStore(qupath,
          Repository.INSTANCE.getDicomStore());
      sync.synchronization();
    });
    synchronizeButton.disableProperty().bind(Repository.INSTANCE.getDicomStoreProperty().isNull());

    logoutButton.setOnAction(event -> {
      Repository.INSTANCE.setDicomStore(null);
      try {
        Repository.INSTANCE.invalidateCredentials();
      } catch (IOException e) {
        LOGGER.error("Error invalidate!", e);
      }
      Repository.INSTANCE.getIsLoggedInProperty().set(true);
    });
    logoutButton.disableProperty().bind(Repository.INSTANCE.getIsLoggedInProperty());

    qupath.addToolbarSeparator();
    qupath.addToolbarButton(cloudButton);
    qupath.addToolbarButton(synchronizeButton);
    qupath.addToolbarButton(logoutButton);
  }

  public String getName() {
    return EXTENSION_NAME;
  }

  public String getDescription() {
    return EXTENSION_DESCRIPTION;
  }
}
