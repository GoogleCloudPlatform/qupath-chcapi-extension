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

package com.quantumsoft.qupathcloud.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * The Series class contains unique identifiers studyInstanceUID and seriesInstanceUID.
 * Field seriesDescription contains image name. With the help of modality field we can define
 * that Series relate to image or Qpdata.
 */
public class Series {

  @JsonProperty("0020000D")
  private DicomAttribute<String> studyInstanceUID;
  @JsonProperty("0020000E")
  private DicomAttribute<String> seriesInstanceUID;
  @JsonProperty("0008103E")
  private DicomAttribute<String> seriesDescription;
  @Deprecated
  @JsonProperty("00204000")
  private DicomAttribute<String> imageComments;
  @JsonProperty("00080060")
  private DicomAttribute<String> modality;

  /**
   * Gets Study Instance UID.
   *
   * @return the Study Instance UID
   */
  public DicomAttribute<String> getStudyInstanceUID() {
    return studyInstanceUID;
  }

  /**
   * Sets Study Instance UID.
   *
   * @param studyInstanceUID the Study Instance UID
   */
  public void setStudyInstanceUID(DicomAttribute<String> studyInstanceUID) {
    this.studyInstanceUID = studyInstanceUID;
  }

  /**
   * Gets Series Instance UID.
   *
   * @return the Series Instance UID
   */
  public DicomAttribute<String> getSeriesInstanceUID() {
    return seriesInstanceUID;
  }

  /**
   * Sets Series Instance UID.
   *
   * @param seriesInstanceUID the Series Instance UID
   */
  public void setSeriesInstanceUID(DicomAttribute<String> seriesInstanceUID) {
    this.seriesInstanceUID = seriesInstanceUID;
  }

  /**
   * Gets Series Description.
   *
   * @return the Series Description
   */
  public DicomAttribute<String> getSeriesDescription() {
    if (seriesDescription == null) {
      String temp = studyInstanceUID.getValue1() + seriesInstanceUID.getValue1();
      seriesDescription = new DicomAttribute<>();
      seriesDescription.setValue(new String[]{String.valueOf(temp.hashCode())});
    }
    return seriesDescription;
  }

  /**
   * Sets Series Description.
   *
   * @param imageComments the image comments
   */
  public void setSeriesDescription(DicomAttribute<String> imageComments) {
    this.seriesDescription = imageComments;
  }

  /**
   * Gets Modality.
   *
   * @return the Modality
   */
  public DicomAttribute<String> getModality() {
    return modality;
  }

  /**
   * Sets Modality.
   *
   * @param modality the Modality
   */
  public void setModality(DicomAttribute<String> modality) {
    this.modality = modality;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Series series = (Series) o;
    return Objects.equals(studyInstanceUID, series.studyInstanceUID) &&
        Objects.equals(seriesInstanceUID, series.seriesInstanceUID) &&
        Objects.equals(seriesDescription, series.seriesDescription);
  }

  @Override
  public int hashCode() {
    return Objects.hash(studyInstanceUID, seriesInstanceUID, seriesDescription);
  }
}
