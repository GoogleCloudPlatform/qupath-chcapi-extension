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

package com.quantumsoft.qupathcloud.gui.tables;

import com.quantumsoft.qupathcloud.entities.Dataset;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Comparator;
import java.util.List;

public class DatasetsTable {

    public TableView getDatasetsTable(List<Dataset> datasets){

        datasets.sort(Comparator.comparing(Dataset::getLocationId));
        datasets.sort(Comparator.comparing(Dataset::getDatasetId));
        ObservableList<Dataset> datasetsObservableList = FXCollections.observableArrayList(datasets);
        TableView<Dataset> datasetsTable = new TableView<>(datasetsObservableList);
        datasetsTable.setId("datasetsTable");

        TableColumn<Dataset, String> nameColumn = new TableColumn<>("ID");
        nameColumn.prefWidthProperty().bind(datasetsTable.widthProperty().multiply(0.498));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("datasetId"));
        datasetsTable.getColumns().add(nameColumn);

        TableColumn<Dataset, String> locationsColumn = new TableColumn<>("LOCATIONS");
        locationsColumn.prefWidthProperty().bind(datasetsTable.widthProperty().multiply(0.498));
        locationsColumn.setCellValueFactory(new PropertyValueFactory<>("locationId"));
        datasetsTable.getColumns().add(locationsColumn);

        return datasetsTable;
    }
}
