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

import com.quantumsoft.qupathcloud.configuration.DicomStoreConfiguration;
import com.quantumsoft.qupathcloud.dao.CloudDAO;
import com.quantumsoft.qupathcloud.dao.CloudDAOImpl;
import com.quantumsoft.qupathcloud.entities.DicomStore;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import com.quantumsoft.qupathcloud.oauth20.OAuth20;
import com.quantumsoft.qupathcloud.synchronization.SynchronizationProjectWithDicomStore;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.helpers.DisplayHelpers;
import qupath.lib.images.ImageData;
import qupath.lib.objects.hierarchy.events.PathObjectHierarchyEvent;
import qupath.lib.objects.hierarchy.events.PathObjectHierarchyListener;

import java.awt.image.BufferedImage;
import java.util.Date;

import static com.quantumsoft.qupathcloud.converter.ImageDataUtilities.LAST_CHANGE;

public enum Repository {
    INSTANCE;

    private final Logger LOGGER = LogManager.getLogger();
    private final ObjectProperty<DicomStore> dicomStore;
    private final ObjectProperty<CloudDAO> cloudDao;

    private final QuPathHierarchyListener hierarchyListener;

    Repository() {
        QuPathGUI qupath = QuPathGUI.getInstance();
        dicomStore = new SimpleObjectProperty<>();
        dicomStore.addListener((observableValue, oldStore, newStore) -> {
            if(newStore == null){
                return;
            }

            Platform.runLater(()->{
                SynchronizationProjectWithDicomStore sync = new SynchronizationProjectWithDicomStore(qupath, newStore);
                sync.synchronization();
            });
        });
        qupath.projectProperty().addListener((observable, oldProject, newProject) -> {
            if (newProject == null) {
                setDicomStore(null);
                return;
            }

            if (oldProject != null && newProject.getFile() == oldProject.getFile()) {
                return;
            }

            Runnable loader = () -> {
                DicomStoreConfiguration dicomStoreConfiguration = new DicomStoreConfiguration(newProject.getBaseDirectory());
                try {
                    DicomStore projectDicomStore = dicomStoreConfiguration.readConfiguration();
                    setDicomStore(projectDicomStore);
                } catch (QuPathCloudException e) {
                    LOGGER.error("Repository error!", e);
                    DisplayHelpers.showErrorMessage("Repository error!", e);
                }
            };
            new Thread(loader).start();
        });

        cloudDao = new SimpleObjectProperty<>();
        cloudDao.set(new CloudDAOImpl(new OAuth20()));

        hierarchyListener = new QuPathHierarchyListener();
        qupath.addImageDataChangeListener((source, imageDataOld, imageDataNew) -> {
            LOGGER.trace("ImageData change old: " + imageDataOld + ", new:" + imageDataNew);
            hierarchyListener.setListenedImageData(imageDataNew);
        });
    }

    public synchronized ObjectProperty<DicomStore> getDicomStoreProperty() {
        return dicomStore;
    }

    public synchronized DicomStore getDicomStore() {
        return dicomStore.get();
    }

    public synchronized void setDicomStore(DicomStore value) {
        dicomStore.set(value);
    }

    public CloudDAO getCloudDao() {
        return cloudDao.get();
    }

    // attempt to provide meaningfull modification date for qpdata.
    private class QuPathHierarchyListener implements PathObjectHierarchyListener {
        ImageData<BufferedImage> imageData;

        public void setListenedImageData(ImageData<BufferedImage> imageData) {
            if (this.imageData != null) {
                this.imageData.getHierarchy().removePathObjectListener(this);
            }
            if (imageData != null) {
                this.imageData = imageData;
                this.imageData.getHierarchy().addPathObjectListener(this);
            }
        }

        @Override
        public void hierarchyChanged(PathObjectHierarchyEvent event) {
            LOGGER.trace("PathObjectHierarchyEvent event: " + event);
            // crutch attempt to filter out initial imageData loading
            if (!event.getSource().equals(event.getHierarchy().getRootObject())) {
                imageData.setProperty(LAST_CHANGE, new Date());
                LOGGER.trace("imageData lastChange date updated");
            }
        }
    }
}
