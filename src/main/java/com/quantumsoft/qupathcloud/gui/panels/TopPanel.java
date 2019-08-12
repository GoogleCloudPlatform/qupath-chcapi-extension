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

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.svg.SVGGlyph;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;

/**
 * The TopPanel shows a panel with the current position and search box.
 */
public class TopPanel {

  private static final double RIGHT_PADDING = 0;
  private static final double LEFT_PADDING = 0;
  private static final double TOP_PADDING = 10;
  private static final double BOTTOM_PADDING = 10;
  private static final double SEARCH_WIDTH = 150;
  private static final double GLYPH_SIZE = 9;
  private static final double GLYPH_RIGHT_MARGIN = 4;
  private static final double GLYPH_LEFT_MARGIN = 4;

  /**
   * Gets beard crumbs and search grid.
   *
   * @param position the position
   * @return the beard crumbs and search grid
   */
  public GridPane getBeardCrumbsAndSearchGrid(Position position) {

    GridPane beardCrumbsAndSearchGridPanel = new GridPane();
    beardCrumbsAndSearchGridPanel
        .setPadding(new Insets(TOP_PADDING, RIGHT_PADDING, BOTTOM_PADDING, LEFT_PADDING));

    ColumnConstraints beardCrumbsColumn = new ColumnConstraints();
    beardCrumbsColumn.setPercentWidth(50);
    beardCrumbsAndSearchGridPanel.getColumnConstraints().add(beardCrumbsColumn);

    ColumnConstraints searchColumn = new ColumnConstraints();
    searchColumn.setPercentWidth(50);
    beardCrumbsAndSearchGridPanel.getColumnConstraints().add(searchColumn);

    RowConstraints beardCrumbsAndSearchRow = new RowConstraints();
    beardCrumbsAndSearchGridPanel.getRowConstraints().add(beardCrumbsAndSearchRow);

    Label projectLabel = new Label("PROJECT");
    Label datasetLabel = new Label("DATASET");
    Label dicomStoreLabel = new Label("DICOM STORE");

    switch (position) {
      case PROJECTS:
        projectLabel.getStyleClass().add("beardCrumbsBlack");
        datasetLabel.getStyleClass().add("beardCrumbsGray");
        dicomStoreLabel.getStyleClass().add("beardCrumbsGray");
        break;
      case DATASETS:
        projectLabel.getStyleClass().add("beardCrumbsBlack");
        datasetLabel.getStyleClass().add("beardCrumbsBlack");
        dicomStoreLabel.getStyleClass().add("beardCrumbsGray");
        break;
      case DICOM_STORES:
        projectLabel.getStyleClass().add("beardCrumbsBlack");
        datasetLabel.getStyleClass().add("beardCrumbsBlack");
        dicomStoreLabel.getStyleClass().add("beardCrumbsBlack");
    }

    HBox beardCrumbsHBox = new HBox(projectLabel, getAngleRightGlyph(), datasetLabel,
        getAngleRightGlyph(), dicomStoreLabel);
    beardCrumbsHBox.setAlignment(Pos.CENTER_LEFT);

    JFXTextField search = new JFXTextField();
    search.setPrefWidth(SEARCH_WIDTH);
    search.setPromptText("Search");
    search.setId("search");
    GridPane.setHalignment(search, HPos.RIGHT);
    GridPane.setValignment(search, VPos.CENTER);
    GridPane.setFillWidth(search, false);

    beardCrumbsAndSearchGridPanel.add(beardCrumbsHBox, 0, 0);
    beardCrumbsAndSearchGridPanel.add(search, 1, 0);

    return beardCrumbsAndSearchGridPanel;
  }

  private SVGGlyph getAngleRightGlyph() {
    SVGGlyph angleRightGlyph = new SVGGlyph(1, "angle-right", "M340 402.286q0-7.429-5.714-13.1"
        + "43l-266.286-266.286q-5.714-5.714-13.143-5.714t-13.143 5.714l-28.571 28.571q-5.714 5"
        + ".714-5.714 13.143t5.714 13.143l224.571 224.571-224.571 224.571q-5.714 5.714-5.714 1"
        + "3.143t5.714 13.143l28.571 28.571q5.714 5.714 13.143 5.714t13.143-5.714l266.286-266."
        + "286q5.714-5.714 5.714-13.143z", Color.BLACK);
    angleRightGlyph.setSize(GLYPH_SIZE);
    HBox.setMargin(angleRightGlyph, new Insets(0, GLYPH_RIGHT_MARGIN, 0, GLYPH_LEFT_MARGIN));
    return angleRightGlyph;
  }
}
