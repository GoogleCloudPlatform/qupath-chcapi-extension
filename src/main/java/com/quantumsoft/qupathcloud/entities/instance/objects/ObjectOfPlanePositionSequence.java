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

package com.quantumsoft.qupathcloud.entities.instance.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quantumsoft.qupathcloud.entities.DicomAttribute;

/**
 * ObjectOfPlanePositionSequence class to support TILED_SPARSE mode.
 *
 * @see <a href="http://dicom.nema.org/medical/dicom/current/output/chtml/part03/sect_C.7.6.17.3.html">TILED_SPARSE</a>
 * @see <a href="http://dicom.nema.org/medical/dicom/current/output/chtml/part03/sect_C.7.6.17.html">Multi-frame Dimension Module</a>
 */
public class ObjectOfPlanePositionSequence {

  @JsonProperty("0048021E")
  private DicomAttribute<Integer> columnPositionInTotalImagePixelMatrix;
  @JsonProperty("0048021F")
  private DicomAttribute<Integer> rowPositionInTotalImagePixelMatrix;

  /**
   * Gets Column Position In Total Image Pixel Matrix.
   *
   * @return the Column Position In Total Image Pixel Matrix
   */
  public DicomAttribute<Integer> getColumnPositionInTotalImagePixelMatrix() {
    return columnPositionInTotalImagePixelMatrix;
  }

  /**
   * Sets Column Position In Total Image Pixel Matrix.
   *
   * @param columnPositionInTotalImagePixelMatrix the Column Position In Total Image Pixel Matrix
   */
  public void setColumnPositionInTotalImagePixelMatrix(
      DicomAttribute<Integer> columnPositionInTotalImagePixelMatrix) {
    this.columnPositionInTotalImagePixelMatrix = columnPositionInTotalImagePixelMatrix;
  }

  /**
   * Gets Row Position In Total Image Pixel Matrix.
   *
   * @return the Row Position In Total Image Pixel Matrix
   */
  public DicomAttribute<Integer> getRowPositionInTotalImagePixelMatrix() {
    return rowPositionInTotalImagePixelMatrix;
  }

  /**
   * Sets Row Position In Total Image Pixel Matrix.
   *
   * @param rowPositionInTotalImagePixelMatrix the Row Position In Total Image Pixel Matrix
   */
  public void setRowPositionInTotalImagePixelMatrix(
      DicomAttribute<Integer> rowPositionInTotalImagePixelMatrix) {
    this.rowPositionInTotalImagePixelMatrix = rowPositionInTotalImagePixelMatrix;
  }
}
