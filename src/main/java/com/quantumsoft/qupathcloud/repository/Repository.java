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

package com.quantumsoft.qupathcloud.repository;

import static com.quantumsoft.qupathcloud.converter.ImageDataUtilities.LAST_CHANGE;

import com.quantumsoft.qupathcloud.configuration.DicomStoreConfiguration;
import com.quantumsoft.qupathcloud.dao.CloudDao;
import com.quantumsoft.qupathcloud.dao.CloudDaoImpl;
import com.quantumsoft.qupathcloud.entities.DicomStore;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import com.quantumsoft.qupathcloud.oauth20.OAuth20;
import com.quantumsoft.qupathcloud.synchronization.SynchronizationProjectWithDicomStore;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.dialogs.Dialogs;
import qupath.lib.gui.prefs.PathPrefs;
import qupath.lib.images.ImageData;
import qupath.lib.objects.hierarchy.events.PathObjectHierarchyEvent;
import qupath.lib.objects.hierarchy.events.PathObjectHierarchyListener;

/**
 * The repository is a singleton that contains the chosen DICOM Store for the current QuPath
 * project, the instance of CloudDao and isLoggedInProperty.
 */
public enum Repository {
  /**
   * Instance repository.
   */
  INSTANCE;

  private final Logger LOGGER = LogManager.getLogger();
  private final ObjectProperty<DicomStore> dicomStore;
  private final ObjectProperty<CloudDao> cloudDao;
  private final BooleanProperty isLoggedInProperty = new SimpleBooleanProperty();

  private final QuPathHierarchyListener hierarchyListener;

  Repository() {
    isLoggedInProperty.set(true);
    QuPathGUI qupath = QuPathGUI.getInstance();
    dicomStore = new SimpleObjectProperty<>();
    dicomStore.addListener((observableValue, oldStore, newStore) -> {
      if (newStore == null) {
        return;
      }

      Platform.runLater(() -> {
        SynchronizationProjectWithDicomStore sync = new SynchronizationProjectWithDicomStore(qupath,
            newStore);
        sync.synchronization();
      });
    });
    qupath.projectProperty().addListener((observable, oldProject, newProject) -> {
      if (newProject == null) {
        setDicomStore(null);
        return;
      }

      if (oldProject != null && newProject.getPath() == oldProject.getPath()) {
        return;
      }

      Runnable loader = () -> {
        Path projectDirectory = newProject.getPath().getParent();
        DicomStoreConfiguration dicomStoreConfiguration = new DicomStoreConfiguration(
            projectDirectory);
        try {
          DicomStore projectDicomStore = dicomStoreConfiguration.readConfiguration();
          setDicomStore(projectDicomStore);
        } catch (QuPathCloudException e) {
          LOGGER.error("Repository error!", e);
          Dialogs.showErrorMessage("Repository error!", e);
        }
      };
      new Thread(loader).start();
    });

    cloudDao = new SimpleObjectProperty<>();
    Path baseQupathDirectory = Paths.get(PathPrefs.getUserPath());
    OAuth20 oAuth20 = new OAuth20(baseQupathDirectory);
    cloudDao.set(new CloudDaoImpl(oAuth20));

    hierarchyListener = new QuPathHierarchyListener();
    qupath.imageDataProperty().addListener(hierarchyListener);
  }

  /**
   * Gets dicom store property.
   *
   * @return the dicom store property
   */
  public synchronized ObjectProperty<DicomStore> getDicomStoreProperty() {
    return dicomStore;
  }

  /**
   * Gets dicom store.
   *
   * @return the dicom store
   */
  public synchronized DicomStore getDicomStore() {
    return dicomStore.get();
  }

  /**
   * Sets dicom store.
   *
   * @param value the value
   */
  public synchronized void setDicomStore(DicomStore value) {
    dicomStore.set(value);
  }

  /**
   * Gets cloud dao.
   *
   * @return the cloud dao
   */
  public CloudDao getCloudDao() {
    return cloudDao.get();
  }

  /**
   * Gets is logged in property.
   *
   * @return the is logged in property
   */
  public BooleanProperty getIsLoggedInProperty() {
    return isLoggedInProperty;
  }

  /**
   * Invalidate credentials.
   *
   * @throws IOException the io exception
   */
  public void invalidateCredentials() throws IOException {
    cloudDao.get().getoAuth20().invalidateCredentials();
  }

  // attempt to provide meaningfull modification date for Qpdata.
  private class QuPathHierarchyListener implements ChangeListener<ImageData<BufferedImage>>, PathObjectHierarchyListener {

    /**
     * The Image data.
     */
    ImageData<BufferedImage> imageData;

    @Override
    public void hierarchyChanged(PathObjectHierarchyEvent event) {
      LOGGER.trace("PathObjectHierarchyEvent event: " + event);
      // crutch attempt to filter out initial imageData loading
      if (!event.getSource().equals(event.getHierarchy().getRootObject())) {
        imageData.setProperty(LAST_CHANGE, new Date());
        LOGGER.trace("imageData lastChange date updated");
      }
    }

    @Override
    public void changed(ObservableValue<? extends ImageData<BufferedImage>> source,
        ImageData<BufferedImage> imageDataOld, ImageData<BufferedImage> imageDataNew) {
      LOGGER.trace("ImageData change old: " + imageDataOld + ", new:" + imageDataNew);
      if (this.imageData != null) {
        this.imageData.getHierarchy().removePathObjectListener(this);
      }
      if (imageDataNew != null) {
        this.imageData = imageDataNew;
        this.imageData.getHierarchy().addPathObjectListener(this);
      }
    }
  }
}
