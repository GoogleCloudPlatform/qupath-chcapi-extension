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

import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;

public class SpinnerPage {

  private static final double SPINNER_SIZE = 200;

  public BorderPane getSpinnerPage() {
    ProgressIndicator progressIndicator = new ProgressIndicator();
    progressIndicator.setMaxSize(SPINNER_SIZE, SPINNER_SIZE);
    return new BorderPane(progressIndicator);
  }
}
