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

import com.jfoenix.controls.JFXButton;
import com.quantumsoft.qupathcloud.synchronization.Conflict;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ConflictsWindow{
    private static final double STAGE_WIDTH = 920;
    private static final double STAGE_HEIGHT = 500;
    private static final double HEADER_HEIGHT_IN_PERCENT = 15;
    private static final double PAGE_HEIGHT_IN_PERCENT = 85;
    private static final double RIGHT_PADDING = 13;
    private static final double LEFT_PADDING = 13;
    private static final double TOP_PADDING = 13;
    private static final double BOTTOM_PADDING = 13;
    private static final String STAGE_TITLE= "QuPath cloud";

    private Label headerLabel;
    private Stage primaryStage;
    private List<Conflict> conflicts;

    public ConflictsWindow(List<Conflict> conflicts){
        primaryStage = new Stage();
        headerLabel = new Label("Conflict resolution");
        conflicts.sort(Comparator.comparing(Conflict::getImageName));
        this.conflicts = conflicts;
    }

    public void showAndWaitConflictsWindow(){
        headerLabel.getStyleClass().add("cloudWindowHeaderLabel");
        BorderPane header = new BorderPane(headerLabel);
        header.getStyleClass().add("cloudWindowHeaderBackgroundColor");

        GridPane root = new GridPane();
        ColumnConstraints column = new ColumnConstraints();
        column.setPercentWidth(100);
        root.getColumnConstraints().add(column);

        RowConstraints headerRow = new RowConstraints();
        headerRow.setPercentHeight(HEADER_HEIGHT_IN_PERCENT);
        root.getRowConstraints().add(headerRow);

        RowConstraints contentRow = new RowConstraints();
        contentRow.setPercentHeight(PAGE_HEIGHT_IN_PERCENT);
        root.getRowConstraints().add(contentRow);

        ObservableList<Conflict> conflictsObservableList = FXCollections.observableArrayList(conflicts);

        //table of conflicts
        TableView<Conflict> conflictsTable = new TableView<>(conflictsObservableList);
        conflictsTable.setId("conflictsTable");
        conflictsTable.setEditable(true);

        TableColumn<Conflict, String> ImageNameColumn = new TableColumn<>("IMAGE NAME");
        ImageNameColumn.setCellValueFactory(new PropertyValueFactory<>("imageName"));
        conflictsTable.getColumns().add(ImageNameColumn);

        TableColumn<Conflict, String> localDate = new TableColumn<>("LOCAL DATE");
        localDate.setCellValueFactory(new PropertyValueFactory<>("localDate"));
        conflictsTable.getColumns().add(localDate);

        TableColumn<Conflict, String> remoteDate = new TableColumn<>("REMOTE DATE");
        remoteDate.setCellValueFactory(new PropertyValueFactory<>("remoteDate"));
        conflictsTable.getColumns().add(remoteDate);

        TableColumn<Conflict, String> resolution = new TableColumn<>("RESOLUTION");
        conflictsTable.getColumns().add(resolution);

        resolution.setCellValueFactory(cellData -> {
            Conflict conflict = cellData.getValue();
            return new SimpleObjectProperty<>(conflict.getResolution().toString());
        });

        ObservableList<String> conflictList = FXCollections.observableArrayList(
                Arrays.asList(
                        Conflict.Resolution.Local.toString(),
                        Conflict.Resolution.Remote.toString()
                ));
        resolution.setCellFactory(ComboBoxTableCell.forTableColumn(conflictList));

        resolution.setOnEditCommit((TableColumn.CellEditEvent<Conflict, String> event) -> {
            TablePosition<Conflict, String> pos = event.getTablePosition();
            String newValue = event.getNewValue();
            int row = pos.getRow();
            Conflict person = event.getTableView().getItems().get(row);
            person.getResolutionProperty().setValue(Conflict.Resolution.valueOf(newValue));
        });

        //bottom panel
        JFXButton okButton = new JFXButton("OK");
        JFXButton cancelButton = new JFXButton("CANCEL");
        BorderPane bottomPanel = new BorderPane();
        bottomPanel.setLeft(cancelButton);
        bottomPanel.setRight(okButton);
        bottomPanel.setPadding(new Insets(TOP_PADDING,0,0,0));

        okButton.setOnAction(actionEvent -> primaryStage.close());
        cancelButton.setOnAction(actionEvent -> {
            setCancel();
            primaryStage.close();
        });
        primaryStage.setOnCloseRequest(actionEvent -> setCancel());

        //inner page
        GridPane pageGrid = new GridPane();
        pageGrid.setPadding(new Insets(TOP_PADDING,RIGHT_PADDING,BOTTOM_PADDING,LEFT_PADDING));

        ColumnConstraints columnInPageGrid = new ColumnConstraints();
        columnInPageGrid.setPercentWidth(100);
        pageGrid.getColumnConstraints().add(columnInPageGrid);

        RowConstraints tableRow = new RowConstraints();
        pageGrid.getRowConstraints().add(tableRow);

        RowConstraints bottomPanelRow = new RowConstraints();
        pageGrid.getRowConstraints().add(bottomPanelRow);

        pageGrid.add(conflictsTable, 0, 0);
        pageGrid.add(bottomPanel, 0, 1);

        root.add(header, 0, 0);
        root.add(pageGrid, 0, 1);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("styles/styles.css");

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
        primaryStage.showAndWait();
    }

    public List<Conflict> getResult(){
        return conflicts;
    }

    private void setCancel(){
        for(Conflict conflict : conflicts){
            conflict.getResolutionProperty().setValue(Conflict.Resolution.Cancel);
        }
    }
}
