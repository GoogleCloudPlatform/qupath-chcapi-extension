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

package com.quantumsoft.qupathcloud.gui.pages;

import com.quantumsoft.qupathcloud.gui.panels.BottomPanel;
import com.quantumsoft.qupathcloud.gui.panels.Position;
import com.quantumsoft.qupathcloud.gui.panels.TopPanel;
import javafx.geometry.Insets;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

/**
 * The Page for presenting page with information, a current position with search box and
 * control buttons.
 */
public class Page {

  private static final double RIGHT_PADDING = 13;
  private static final double LEFT_PADDING = 13;
  private static final double TOP_PADDING = 0;
  private static final double BOTTOM_PADDING = 0;

  /**
   * Gets page.
   *
   * @param table    the table
   * @param position the position
   * @return the page
   */
  public GridPane getPage(TableView table, Position position) {

    GridPane pageGrid = new GridPane();
    pageGrid.setPadding(new Insets(TOP_PADDING, RIGHT_PADDING, BOTTOM_PADDING, LEFT_PADDING));

    ColumnConstraints columnInPageGrid = new ColumnConstraints();
    columnInPageGrid.setPercentWidth(100);
    pageGrid.getColumnConstraints().add(columnInPageGrid);

    RowConstraints beardCrumbsAndSearchPanelRow = new RowConstraints();
    pageGrid.getRowConstraints().add(beardCrumbsAndSearchPanelRow);

    RowConstraints nodeRow = new RowConstraints();
    pageGrid.getRowConstraints().add(nodeRow);

    RowConstraints nextPanelRow = new RowConstraints();
    pageGrid.getRowConstraints().add(nextPanelRow);

    GridPane beardCrumbsAndSearchRowGrid = new TopPanel().getBeardCrumbsAndSearchGrid(position);
    BorderPane bottomPanel = new BottomPanel().getNextPanel(position);

    pageGrid.add(beardCrumbsAndSearchRowGrid, 0, 0);
    pageGrid.add(table, 0, 1);
    pageGrid.add(bottomPanel, 0, 2);

    return pageGrid;
  }
}
