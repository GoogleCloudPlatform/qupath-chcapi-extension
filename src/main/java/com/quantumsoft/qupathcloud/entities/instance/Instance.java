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

package com.quantumsoft.qupathcloud.entities.instance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.quantumsoft.qupathcloud.entities.DicomAttribute;
import com.quantumsoft.qupathcloud.entities.instance.objects.ObjectOfPerframeFunctionalGroupsSequence;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The type Instance.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Instance {

  private static final String TILED_FULL = "TILED_FULL";

  @JsonProperty("0020000D")
  private DicomAttribute<String> studyInstanceUID; //required includefield parameter
  @JsonProperty("0020000E")
  private DicomAttribute<String> seriesInstanceUID; //required includefield parameter
  @JsonProperty("00080018")
  private DicomAttribute<String> sopInstanceUID;
  @JsonProperty("00080060")
  private DicomAttribute<String> modality;
  @JsonProperty("00480006")
  private DicomAttribute<Integer> totalPixelMatrixColumns; //required includefield parameter
  @JsonProperty("00480007")
  private DicomAttribute<Integer> totalPixelMatrixRows; //required includefield parameter
  @JsonProperty("00280010")
  private DicomAttribute<Integer> rows;
  @JsonProperty("00280011")
  private DicomAttribute<Integer> columns;
  @JsonProperty("52009230")
  private DicomAttribute<ObjectOfPerframeFunctionalGroupsSequence> perframeFunctionalGroupsSequence; //required includefield parameter
  @JsonProperty("00080012")
  private DicomAttribute<String> instanceCreationDate;
  @JsonProperty("00080013")
  private DicomAttribute<String> instanceCreationTime;
  @JsonProperty("00080201")
  private DicomAttribute<String> timezoneOffsetFromUTC;
  @JsonProperty("01000424")
  private DicomAttribute<String> sopAuthorizationComment;
  @JsonProperty("00209311")
  private DicomAttribute<String> dimensionOrganizationType;
  @JsonProperty("00209161")
  private DicomAttribute<String> concatenationUID;
  @JsonProperty("00209228")
  private DicomAttribute<Integer> concatenationFrameOffsetNumber;
  @JsonProperty("00280008")
  private DicomAttribute<Integer> numberOfFrames;


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
   * Gets SOP Instance UID.
   *
   * @return the SOP Instance UID
   */
  public DicomAttribute<String> getSopInstanceUID() {
    return sopInstanceUID;
  }

  /**
   * Sets SOP Instance UID.
   *
   * @param sopInstanceUID the SOP Instance UID
   */
  public void setSopInstanceUID(DicomAttribute<String> sopInstanceUID) {
    this.sopInstanceUID = sopInstanceUID;
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

  /**
   * Gets Total Pixel Matrix Columns.
   *
   * @return the Total Pixel Matrix Columns
   */
  public DicomAttribute<Integer> getTotalPixelMatrixColumns() {
    return totalPixelMatrixColumns;
  }

  /**
   * Sets Total Pixel Matrix Columns.
   *
   * @param totalPixelMatrixColumns the Total Pixel Matrix Columns
   */
  public void setTotalPixelMatrixColumns(DicomAttribute<Integer> totalPixelMatrixColumns) {
    this.totalPixelMatrixColumns = totalPixelMatrixColumns;
  }

  /**
   * Gets Total Pixel Matrix Rows.
   *
   * @return the Total Pixel Matrix Rows
   */
  public DicomAttribute<Integer> getTotalPixelMatrixRows() {
    return totalPixelMatrixRows;
  }

  /**
   * Sets Total Pixel Matrix Rows.
   *
   * @param totalPixelMatrixRows the Total Pixel Matrix Rows
   */
  public void setTotalPixelMatrixRows(DicomAttribute<Integer> totalPixelMatrixRows) {
    this.totalPixelMatrixRows = totalPixelMatrixRows;
  }

  /**
   * Gets Rows.
   *
   * @return the Rows
   */
  public DicomAttribute<Integer> getRows() {
    return rows;
  }

  /**
   * Sets Rows.
   *
   * @param rows the Rows
   */
  public void setRows(DicomAttribute<Integer> rows) {
    this.rows = rows;
  }

  /**
   * Gets Columns.
   *
   * @return the Columns
   */
  public DicomAttribute<Integer> getColumns() {
    return columns;
  }

  /**
   * Sets Columns.
   *
   * @param columns the Columns
   */
  public void setColumns(DicomAttribute<Integer> columns) {
    this.columns = columns;
  }

  /**
   * Gets Per-frame Functional Groups Sequence.
   *
   * @return the Per-frame Functional Groups Sequence
   */
  public DicomAttribute<ObjectOfPerframeFunctionalGroupsSequence> getPerframeFunctionalGroupsSequence() {
    return perframeFunctionalGroupsSequence;
  }

  /**
   * Sets Per-frame Functional Groups Sequence.
   *
   * @param perframeFunctionalGroupsSequence the Per-frame Functional Groups Sequence
   */
  public void setPerframeFunctionalGroupsSequence(
      DicomAttribute<ObjectOfPerframeFunctionalGroupsSequence> perframeFunctionalGroupsSequence) {
    this.perframeFunctionalGroupsSequence = perframeFunctionalGroupsSequence;
  }

  /**
   * Gets creation date.
   *
   * @return the creation date
   * @throws QuPathCloudException if ParseException occurs
   */
  @JsonIgnore
  public Date getCreationDate() throws QuPathCloudException {
    String format = "yyyyMMdd HHmmss.SSS Z";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    try {
      String dateString = instanceCreationDate.getValue1() + " " + instanceCreationTime.getValue1()
          + " " + timezoneOffsetFromUTC.getValue1();
      return simpleDateFormat.parse(dateString);
    } catch (ParseException e) {
      throw new QuPathCloudException(e);
    }
  }

  /**
   * Gets Instance Creation Date.
   *
   * @return the Instance Creation Date
   */
  public DicomAttribute<String> getInstanceCreationDate() {
    return instanceCreationDate;
  }

  /**
   * Gets Instance Creation Time.
   *
   * @return the Instance Creation Time
   */
  public DicomAttribute<String> getInstanceCreationTime() {
    return instanceCreationTime;
  }

  /**
   * Gets timezone offset from UTC.
   *
   * @return the timezone offset from UTC
   */
  public DicomAttribute<String> getTimezoneOffsetFromUTC() {
    return timezoneOffsetFromUTC;
  }

  /**
   * Gets SOP Authorization Comment.
   *
   * @return the SOP Authorization Comment
   */
  public DicomAttribute<String> getSopAuthorizationComment() {
    return sopAuthorizationComment;
  }

  /**
   * Gets Dimension Organization Type.
   *
   * @return the Dimension Organization Type
   */
  public DicomAttribute<String> getDimensionOrganizationType() {
    return dimensionOrganizationType;
  }

  /**
   * Is full tiled boolean.
   *
   * @return the boolean
   */
  @JsonIgnore
  public boolean isFullTiled() {
    if (dimensionOrganizationType == null) {
      return false;
    }
    String[] values = dimensionOrganizationType.getValue();
    if (values.length == 0) {
      return false;
    }
    return values[0].equals(TILED_FULL);
  }

  /**
   * Gets Concatenation UID.
   *
   * @return the Concatenation UID
   */
  public DicomAttribute<String> getConcatenationUID() {
    return concatenationUID;
  }

  /**
   * Gets Concatenation Frame Offset Number.
   *
   * @return the Concatenation Frame Offset Number
   */
  public DicomAttribute<Integer> getConcatenationFrameOffsetNumber() {
    return concatenationFrameOffsetNumber;
  }

  /**
   * Gets Number of Frames.
   *
   * @return the Number of Frames
   */
  public DicomAttribute<Integer> getNumberOfFrames() {
    return numberOfFrames;
  }
}
