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

/**
 * The Study class contains unique identifier studyInstanceUID.
 */
public class Study {

  @JsonProperty("0020000D")
  private DicomAttribute<String> studyInstanceUID;

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
}
