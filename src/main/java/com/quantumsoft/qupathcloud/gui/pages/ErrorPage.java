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

import com.jfoenix.svg.SVGGlyph;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ErrorPage {
    private static final double RIGHT_PADDING = 13;
    private static final double LEFT_PADDING = 13;
    private static final double TOP_PADDING = 13;
    private static final double BOTTOM_PADDING = 13;
    private static final double GLYPH_SIZE = 170;

    public GridPane showErrorPage(QuPathCloudException e){
        SVGGlyph errorGlyph = new SVGGlyph(1, "exclamation-circle","M438.857 877.714q119.429 " +
                "0 220.286-58.857t159.714-159.714 58.857-220.286-58.857-220.286-159.714-159.714-220.286-58.857-220.286 " +
                "58.857-159.714 159.714-58.857 220.286 58.857 220.286 159.714 159.714 220.286 58.857zM512 165.143v108.571q0 " +
                "8-5.143 13.429t-12.571 5.429h-109.714q-7.429 0-13.143-5.714t-5.714-13.143v-108.571q0-7.429 " +
                "5.714-13.143t13.143-5.714h109.714q7.429 0 12.571 5.429t5.143 13.429zM510.857 361.714l10.286 " +
                "354.857q0 6.857-5.714 10.286-5.714 4.571-13.714 4.571h-125.714q-8 " +
                "0-13.714-4.571-5.714-3.429-5.714-10.286l9.714-354.857q0-5.714 5.714-10t13.714-4.286h105.714q8 " +
                "0 13.429 4.286t6 10z", Color.RED);
        errorGlyph.setSize(GLYPH_SIZE);
        errorGlyph.setScaleY(-1);
        GridPane.setHalignment(errorGlyph, HPos.CENTER);
        GridPane.setValignment(errorGlyph, VPos.CENTER);

        Label errorLabel = new Label("Error");
        errorLabel.getStyleClass().add("errorPage");

        TextArea textArea = new TextArea(e.toString());
        textArea.setMaxWidth(800);
        textArea.setMaxHeight(300);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.getStyleClass().add("errorText");
        GridPane.setHalignment(textArea, HPos.CENTER);
        GridPane.setValignment(textArea, VPos.TOP);

        VBox vBox = new VBox(errorLabel,textArea);
        vBox.setAlignment(Pos.CENTER);

        GridPane errorPageGrid = new GridPane();
        errorPageGrid.setPadding(new Insets(TOP_PADDING,RIGHT_PADDING,BOTTOM_PADDING,LEFT_PADDING));
        ColumnConstraints accessDeniedColumn = new ColumnConstraints();
        accessDeniedColumn.setPercentWidth(100);
        errorPageGrid.getColumnConstraints().add(accessDeniedColumn);

        RowConstraints glyphRow = new RowConstraints();
        glyphRow.setPercentHeight(50);
        errorPageGrid.getRowConstraints().add(glyphRow);

        RowConstraints errorLabelRow = new RowConstraints();
        errorLabelRow.setPercentHeight(50);
        errorPageGrid.getRowConstraints().add(errorLabelRow);

        errorPageGrid.add(errorGlyph, 0, 0);
        errorPageGrid.add(vBox, 0, 1);

        return errorPageGrid;
    }
}
