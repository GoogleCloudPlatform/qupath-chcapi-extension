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

package com.quantumsoft.qupathcloud.gui.panels;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;

public class BottomPanel {

  private static final double RIGHT_PADDING = 0;
  private static final double LEFT_PADDING = 0;
  private static final double TOP_PADDING = 13;
  private static final double BOTTOM_PADDING = 13;

  public BorderPane getNextPanel(Position position) {

    JFXButton nextButton = new JFXButton("NEXT");
    nextButton.setId("nextButton");

    JFXButton backButton = new JFXButton("BACK");
    backButton.setId("backButton");

    JFXButton okButton = new JFXButton("OK");
    okButton.setId("okButton");

    JFXButton newDatasetButton = new JFXButton("New Dataset");
    newDatasetButton.setId("newDatasetButton");
    newDatasetButton.getStyleClass().add("newDatasetButton");

    JFXButton newDicomStoreButton = new JFXButton("New DICOM Store");
    newDicomStoreButton.setId("newDicomStoreButton");
    newDicomStoreButton.getStyleClass().add("newDicomStoreButton");

    BorderPane borderPane = new BorderPane();

    switch (position) {
      case PROJECTS:
        borderPane.setRight(nextButton);
        break;
      case DATASETS:
        borderPane.setLeft(backButton);
        borderPane.setCenter(newDatasetButton);
        borderPane.setRight(nextButton);
        break;
      case DICOM_STORES:
        borderPane.setLeft(backButton);
        borderPane.setCenter(newDicomStoreButton);
        borderPane.setRight(okButton);
    }

    borderPane.setPadding(new Insets(TOP_PADDING, RIGHT_PADDING, BOTTOM_PADDING, LEFT_PADDING));

    return borderPane;
  }
}
