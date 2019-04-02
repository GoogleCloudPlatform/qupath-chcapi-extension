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

public class ObjectOfPlanePositionSequence {
    @JsonProperty("0048021E")
    private DicomAttribute<Integer> columnPositionInTotalImagePixelMatrix;
    @JsonProperty("0048021F")
    private DicomAttribute<Integer> rowPositionInTotalImagePixelMatrix;

    public DicomAttribute<Integer> getColumnPositionInTotalImagePixelMatrix() {
        return columnPositionInTotalImagePixelMatrix;
    }

    public void setColumnPositionInTotalImagePixelMatrix(DicomAttribute<Integer> columnPositionInTotalImagePixelMatrix) {
        this.columnPositionInTotalImagePixelMatrix = columnPositionInTotalImagePixelMatrix;
    }

    public DicomAttribute<Integer> getRowPositionInTotalImagePixelMatrix() {
        return rowPositionInTotalImagePixelMatrix;
    }

    public void setRowPositionInTotalImagePixelMatrix(DicomAttribute<Integer> rowPositionInTotalImagePixelMatrix) {
        this.rowPositionInTotalImagePixelMatrix = rowPositionInTotalImagePixelMatrix;
    }
}
