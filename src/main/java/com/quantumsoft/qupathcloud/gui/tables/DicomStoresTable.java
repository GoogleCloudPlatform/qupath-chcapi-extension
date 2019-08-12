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

import com.quantumsoft.qupathcloud.entities.DicomStore;
import java.util.Comparator;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * The DicomStoresTable represents a table with a list of DICOM Stores.
 */
public class DicomStoresTable {

  /**
   * Gets dicomstores table.
   *
   * @param dicomStores the dicom stores
   * @return the dicomstores table
   */
  public TableView getDicomstoresTable(List<DicomStore> dicomStores) {

    dicomStores.sort(Comparator.comparing(DicomStore::getDicomStoreId));
    ObservableList<DicomStore> dicomStoresObservableList = FXCollections
        .observableArrayList(dicomStores);
    TableView<DicomStore> dicomStoresTable = new TableView<>(dicomStoresObservableList);
    dicomStoresTable.setId("dicomStoresTable");

    TableColumn<DicomStore, String> nameColumn = new TableColumn<>("ID");
    nameColumn.prefWidthProperty().bind(dicomStoresTable.widthProperty().multiply(0.996));
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("dicomStoreId"));
    dicomStoresTable.getColumns().add(nameColumn);

    return dicomStoresTable;
  }
}
