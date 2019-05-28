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

package com.quantumsoft.qupathcloud.gui.windows;

import static com.quantumsoft.qupathcloud.gui.panels.Position.DATASETS;
import static com.quantumsoft.qupathcloud.gui.panels.Position.DICOM_STORES;
import static com.quantumsoft.qupathcloud.gui.panels.Position.PROJECTS;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.quantumsoft.qupathcloud.configuration.DicomStoreConfiguration;
import com.quantumsoft.qupathcloud.dao.CloudDAO;
import com.quantumsoft.qupathcloud.dao.spec.QueryBuilder;
import com.quantumsoft.qupathcloud.entities.Dataset;
import com.quantumsoft.qupathcloud.entities.DicomStore;
import com.quantumsoft.qupathcloud.entities.Location;
import com.quantumsoft.qupathcloud.entities.Project;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import com.quantumsoft.qupathcloud.gui.pages.ErrorPage;
import com.quantumsoft.qupathcloud.gui.pages.Page;
import com.quantumsoft.qupathcloud.gui.pages.SpinnerPage;
import com.quantumsoft.qupathcloud.gui.tables.DatasetsTable;
import com.quantumsoft.qupathcloud.gui.tables.DicomStoresTable;
import com.quantumsoft.qupathcloud.gui.tables.ProjectsTable;
import com.quantumsoft.qupathcloud.repository.Repository;
import java.nio.file.Path;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qupath.lib.gui.QuPathGUI;

public class CloudWindow {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final double STAGE_WIDTH = 620;
  private static final double STAGE_HEIGHT = 500;
  private static final double HEADER_HEIGHT_IN_PERCENT = 15;
  private static final double PAGE_HEIGHT_IN_PERCENT = 85;
  private static final String STAGE_TITLE = "QuPath cloud";

  private final CloudDAO cloudDAO;
  private Label headerLabel;
  private GridPane root;
  private Pane currentPage;
  private TableView currentTable;
  private JFXButton nextButton;
  private JFXButton backButton;
  private DicomStoreConfiguration dicomStoreConfiguration;
  private Stage primaryStage;
  private Dataset choseDataset;

  public CloudWindow(QuPathGUI qupath) {
    Path projectDirectory = qupath.getProject().getPath().getParent();
    dicomStoreConfiguration = new DicomStoreConfiguration(projectDirectory);
    primaryStage = new Stage();
    cloudDAO = Repository.INSTANCE.getCloudDao();
    headerLabel = new Label();
    root = new GridPane();
  }

  public void showCloudWindow() {
    headerLabel.getStyleClass().add("cloudWindowHeaderLabel");
    BorderPane header = new BorderPane(headerLabel);
    header.getStyleClass().add("cloudWindowHeaderBackgroundColor");

    ColumnConstraints column = new ColumnConstraints();
    column.setPercentWidth(100);
    root.getColumnConstraints().add(column);

    RowConstraints headerRow = new RowConstraints();
    headerRow.setPercentHeight(HEADER_HEIGHT_IN_PERCENT);
    root.getRowConstraints().add(headerRow);

    RowConstraints contentRow = new RowConstraints();
    contentRow.setPercentHeight(PAGE_HEIGHT_IN_PERCENT);
    root.getRowConstraints().add(contentRow);

    root.add(header, 0, 0);
    currentPage = getSpinnerPage();
    showPage();

    Scene scene = new Scene(root);
    scene.getStylesheets().add("styles/styles.css");

    DicomStore dicomStore = Repository.INSTANCE.getDicomStore();
    if (dicomStore != null) {
      loadData(dicomStore);
    } else {
      authorizationPage();
    }

    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    primaryStage.setX((screenBounds.getWidth() - STAGE_WIDTH) / 2);
    primaryStage.setY((screenBounds.getHeight() - STAGE_HEIGHT) / 2);
    primaryStage.getIcons().add(new Image("/Images/cloud-icon.png"));
    primaryStage.initModality(Modality.APPLICATION_MODAL);
    primaryStage.setWidth(STAGE_WIDTH);
    primaryStage.setHeight(STAGE_HEIGHT);
    primaryStage.setTitle(STAGE_TITLE);
    primaryStage.setResizable(false);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void loadData(DicomStore dicomStore) {
    headerLabel.setText("Select DICOM store");

    String projectId = dicomStore.getProjectId();
    String locationId = dicomStore.getLocationId();
    String datasetId = dicomStore.getDatasetId();

    choseDataset = new Dataset();
    choseDataset.setName(projectId, locationId, datasetId);

    Runnable loader = () -> {
      try {
        QueryBuilder queryBuilder = QueryBuilder.forProject(projectId)
            .setLocationId(locationId)
            .setDatasetId(datasetId);
        List<DicomStore> dicomStores = cloudDAO.getDicomStoresList(queryBuilder);
        currentTable = new DicomStoresTable().getDicomstoresTable(dicomStores);
        currentTable.getSelectionModel().select(dicomStore);

        Platform.runLater(this::showDicomStoresPage);
      } catch (QuPathCloudException e) {
        LOGGER.error("Loading data error!", e);
        Platform.runLater(() -> showErrorPage(e));
      }
    };
    Thread loadThread = new Thread(loader);
    loadThread.start();
  }

  private void authorizationPage() {
    headerLabel.setText("Authorization");
    Runnable loader1 = () -> {
      try {
        List<Project> projects = cloudDAO.getProjectsList();
        if (projects.isEmpty()) {
          throw new QuPathCloudException("Empty list of projects!");
        }

        Platform.runLater(() -> {
          currentTable = new ProjectsTable().getProjectsTable(projects);
          showProjectsPage();
        });
      } catch (QuPathCloudException e) {
        LOGGER.error("Loading authorization page error!", e);
        Platform.runLater(() -> showErrorPage(e));
      }
    };
    Thread loadThread = new Thread(loader1);
    loadThread.start();
  }

  private void showProjectsPage() {
    headerLabel.setText("Select Project");
    clearPage();
    currentPage = new Page().getPage(currentTable, PROJECTS);
    showPage();

    TableView<Project> projectTableView = (TableView<Project>) currentPage.lookup("#projectsTable");
    nextButton = (JFXButton) currentPage.lookup("#nextButton");

    projectTableView.setOnMouseClicked((MouseEvent event) -> {
      if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
        projectProcess(projectTableView);
      }
    });

    nextButton.setOnAction(event -> projectProcess(projectTableView));

    filterDataInTable(projectTableView);
  }

  private void projectProcess(TableView<Project> projectTableView) {
    if (projectTableView.getSelectionModel().getSelectedItem() != null) {
      clearPage();
      currentPage = getSpinnerPage();
      showPage();
      Project selectedProject = projectTableView.getSelectionModel().getSelectedItem();
      String projectId = selectedProject.getProjectId();
      Runnable loader = () -> {
        try {
          QueryBuilder queryBuilder = QueryBuilder.forProject(projectId);
          List<Location> locations = cloudDAO.getLocationsList(queryBuilder);
          queryBuilder.setLocations(locations);
          List<Dataset> datasets = cloudDAO.getDatasetsListInAllLocations(queryBuilder);
          currentTable = new DatasetsTable().getDatasetsTable(datasets);

          Platform.runLater(() -> showDatasetsPage(locations));
        } catch (QuPathCloudException e) {
          LOGGER.error("Error on projects page!", e);
          Platform.runLater(() -> showErrorPage(e));
        }
      };
      Thread loadThread2 = new Thread(loader);
      loadThread2.start();
    }
  }

  private void showDatasetsPage(List<Location> locations) {
    headerLabel.setText("Select Dataset");
    clearPage();
    currentPage = new Page().getPage(currentTable, DATASETS);
    showPage();

    TableView<Dataset> datasetTableView = (TableView<Dataset>) currentPage.lookup("#datasetsTable");
    nextButton = (JFXButton) currentPage.lookup("#nextButton");

    datasetTableView.setOnMouseClicked((MouseEvent event) -> {
      if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
        datasetProcess(datasetTableView);
      }
    });

    nextButton.setOnAction(event -> datasetProcess(datasetTableView));

    backButton = (JFXButton) currentPage.lookup("#backButton");
    backButton.setOnAction(event -> {
      clearPage();
      currentPage = getSpinnerPage();
      showPage();

      Runnable loader = () -> {
        try {
          List<Project> projects = cloudDAO.getProjectsList();

          Platform.runLater(() -> {
            currentTable = new ProjectsTable().getProjectsTable(projects);
            showProjectsPage();
          });
        } catch (QuPathCloudException e) {
          LOGGER.error("Error on Datasets page!", e);
          Platform.runLater(() -> showErrorPage(e));
        }
      };
      Thread loadThread = new Thread(loader);
      loadThread.start();
    });

    JFXButton newDatasetButton = (JFXButton) currentPage.lookup("#newDatasetButton");
    newDatasetButton.setOnAction(event -> showNewDatasetPage(locations));

    filterDataInTable(datasetTableView);
  }

  private void datasetProcess(TableView<Dataset> datasetTableView) {
    if (datasetTableView.getSelectionModel().getSelectedItem() != null) {
      clearPage();
      currentPage = getSpinnerPage();
      showPage();
      choseDataset = datasetTableView.getSelectionModel().getSelectedItem();
      String projectId = choseDataset.getProjectId();
      String locationId = choseDataset.getLocationId();
      String datasetId = choseDataset.getDatasetId();
      Runnable loader = () -> {
        try {
          QueryBuilder queryBuilder = QueryBuilder.forProject(projectId)
              .setLocationId(locationId)
              .setDatasetId(datasetId);
          List<DicomStore> dicomStores = cloudDAO.getDicomStoresList(queryBuilder);
          currentTable = new DicomStoresTable().getDicomstoresTable(dicomStores);

          Platform.runLater(this::showDicomStoresPage);
        } catch (QuPathCloudException e) {
          LOGGER.error("Error on Datasets page!", e);
          Platform.runLater(() -> showErrorPage(e));
        }
      };
      Thread loadThread = new Thread(loader);
      loadThread.start();
    }
  }

  private void showDicomStoresPage() {
    headerLabel.setText("Select DICOM store");
    clearPage();
    currentPage = new Page().getPage(currentTable, DICOM_STORES);
    showPage();

    backButton = (JFXButton) currentPage.lookup("#backButton");
    backButton.setOnAction(event -> {
      clearPage();
      currentPage = getSpinnerPage();
      showPage();
      Runnable loader = () -> {
        try {
          String projectId = choseDataset.getProjectId();
          QueryBuilder queryBuilder = QueryBuilder.forProject(projectId);
          List<Location> locations = cloudDAO.getLocationsList(queryBuilder);
          queryBuilder.setLocations(locations);
          List<Dataset> datasets = cloudDAO.getDatasetsListInAllLocations(queryBuilder);
          currentTable = new DatasetsTable().getDatasetsTable(datasets);

          Platform.runLater(() -> showDatasetsPage(locations));
        } catch (QuPathCloudException e) {
          LOGGER.error("Error on DicomStores page!", e);
          Platform.runLater(() -> showErrorPage(e));
        }
      };
      Thread loadThread = new Thread(loader);
      loadThread.start();
    });

    JFXButton okButton = (JFXButton) currentPage.lookup("#okButton");
    okButton.setOnAction(event -> {
      DicomStore selectedDicomStore = (DicomStore) currentTable.getSelectionModel()
          .getSelectedItem();
      if (selectedDicomStore != null) {
        try {
          dicomStoreConfiguration.saveConfiguration(selectedDicomStore);
          Repository.INSTANCE.setDicomStore(selectedDicomStore);
          primaryStage.close();
        } catch (QuPathCloudException e) {
          LOGGER.error("Error on DicomStores page!", e);
          showErrorPage(e);
        }
      }
    });

    JFXButton newDicomStoreButton = (JFXButton) currentPage.lookup("#newDicomStoreButton");
    newDicomStoreButton.setOnAction(event -> showNewDicomStorePage());

    filterDataInTable(currentTable);
  }

  private void showNewDicomStorePage() {
    NewDicomStoreWindow newDicomStoreWindow = new NewDicomStoreWindow();
    Pane pane = newDicomStoreWindow.showWindow();
    Stage newDicomStoreStage = newDicomStoreWindow.getStage();

    JFXButton createButton = (JFXButton) pane.lookup("#createButton");
    JFXTextField dicomStoreIdField = (JFXTextField) pane.lookup("#dicomStoreId");
    createButton.setOnAction(event -> {
      String newDicomStoreId = dicomStoreIdField.textProperty().get();
      if (newDicomStoreId.length() > 0) {
        newDicomStoreStage.close();
        clearPage();
        currentPage = getSpinnerPage();
        showPage();
        String projectId = choseDataset.getProjectId();
        String locationId = choseDataset.getLocationId();
        String datasetId = choseDataset.getDatasetId();
        Runnable loader = () -> {
          try {
            QueryBuilder queryBuilder = QueryBuilder.forProject(projectId)
                .setLocationId(locationId)
                .setDatasetId(datasetId)
                .setDicomStoreId(newDicomStoreId);
            cloudDAO.createDicomStore(queryBuilder);
            List<DicomStore> dicomStores = cloudDAO.getDicomStoresList(queryBuilder);
            currentTable = new DicomStoresTable().getDicomstoresTable(dicomStores);

            Platform.runLater(() -> {
              showDicomStoresPage();
              DicomStore createdStore = new DicomStore();
              createdStore.setName(projectId, locationId, datasetId, newDicomStoreId);
              int index = currentTable.getItems().indexOf(createdStore);
              currentTable.getSelectionModel().select(index);
            });
          } catch (QuPathCloudException e) {
            LOGGER.error("Error on new DicomStores page!", e);
            Platform.runLater(() -> showErrorPage(e));
          }
        };
        Thread loadThread = new Thread(loader);
        loadThread.start();
      }
    });

    JFXButton cancelButton = (JFXButton) pane.lookup("#cancelButton");
    cancelButton.setOnAction(event -> newDicomStoreStage.close());
  }

  private void showNewDatasetPage(List<Location> locations) {
    NewDatasetWindow newDatasetWindow = new NewDatasetWindow(locations);
    Pane pane = newDatasetWindow.showWindow();
    Stage newDatasetStage = newDatasetWindow.getStage();

    JFXButton createButton = (JFXButton) pane.lookup("#createButton");
    JFXTextField datasetIdField = (JFXTextField) pane.lookup("#datasetId");
    JFXComboBox<Label> locationsComboBox = (JFXComboBox) pane.lookup("#locationsComboBoxId");
    createButton.setOnAction(event -> {
      String newDatasetId = datasetIdField.textProperty().get();
      Label selectedLabel = locationsComboBox.getSelectionModel().getSelectedItem();
      if (newDatasetId.length() > 0 && selectedLabel != null) {
        newDatasetStage.close();
        clearPage();
        currentPage = getSpinnerPage();
        showPage();
        String selectedLocationId = selectedLabel.getText();
        String projectId = locations.get(0).getProjectId();
        Runnable loader = () -> {
          try {
            QueryBuilder queryBuilder = QueryBuilder.forProject(projectId)
                .setLocationId(selectedLocationId)
                .setDatasetId(newDatasetId)
                .setLocations(locations);
            cloudDAO.createDataset(queryBuilder);
            List<Dataset> datasets = cloudDAO.getDatasetsListInAllLocations(queryBuilder);
            currentTable = new DatasetsTable().getDatasetsTable(datasets);

            Platform.runLater(() -> {
              showDatasetsPage(locations);
              Dataset dataset = new Dataset();
              dataset.setName(projectId, selectedLocationId, newDatasetId);
              int index = currentTable.getItems().indexOf(dataset);
              currentTable.getSelectionModel().select(index);
            });
          } catch (QuPathCloudException e) {
            LOGGER.error("Error on new DicomStores page!", e);
            Platform.runLater(() -> showErrorPage(e));
          }
        };
        Thread loadThread = new Thread(loader);
        loadThread.start();
      }
    });

    JFXButton cancelButton = (JFXButton) pane.lookup("#cancelButton");
    cancelButton.setOnAction(event -> newDatasetStage.close());
  }

  private Pane getSpinnerPage() {
    return new SpinnerPage().getSpinnerPage();
  }

  private void clearPage() {
    root.getChildren().remove(currentPage);
  }

  private void showPage() {
    root.add(currentPage, 0, 1);
  }

  private void showErrorPage(QuPathCloudException e) {
    root.getChildren().remove(currentPage);
    currentPage = new ErrorPage().showErrorPage(e);
    root.add(currentPage, 0, 1);
  }

  private <T> void filterDataInTable(TableView<T> table) {
    JFXTextField filter = (JFXTextField) currentPage.lookup("#search");
    ObservableList data = table.getItems();
    filter.textProperty().addListener(
        (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
          if (oldValue != null && (newValue.length() < oldValue.length())) {
            table.setItems(data);
          }
          String value = newValue.toLowerCase();
          ObservableList<T> subentries = FXCollections.observableArrayList();

          for (int i = 0; i < table.getItems().size(); i++) {
            for (int j = 0; j < table.getColumns().size(); j++) {
              String entry = "" + table.getColumns().get(j).getCellData(i);
              if (entry.toLowerCase().contains(value)) {
                subentries.add(table.getItems().get(i));
                break;
              }
            }
          }
          table.setItems(subentries);
        });
  }
}
