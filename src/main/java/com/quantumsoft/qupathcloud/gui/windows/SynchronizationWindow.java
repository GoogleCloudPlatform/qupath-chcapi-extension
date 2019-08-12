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

import com.quantumsoft.qupathcloud.gui.pages.SpinnerPage;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * The SynchronizationWindow will be presented if user clicks on the Synchronize button.
 */
public class SynchronizationWindow {

  private static final double STAGE_WIDTH = 620;
  private static final double STAGE_HEIGHT = 500;
  private static final double HEADER_HEIGHT_IN_PERCENT = 15;
  private static final double PAGE_HEIGHT_IN_PERCENT = 85;
  private static final String STAGE_TITLE = "QuPath cloud";

  private Label headerLabel;
  private GridPane root;
  private Pane currentPage;
  private Stage primaryStage;

  /**
   * Instantiates a new Synchronization window.
   */
  public SynchronizationWindow() {
    primaryStage = new Stage();
    headerLabel = new Label("Synchronization");
    root = new GridPane();
  }

  /**
   * Show synchronization window.
   */
  public void showSynchronizationWindow() {
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

  /**
   * Close.
   */
  public void close() {
    primaryStage.close();
  }

  private Pane getSpinnerPage() {
    return new SpinnerPage().getSpinnerPage();
  }

  private void showPage() {
    root.add(currentPage, 0, 1);
  }
}
