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

public class ObjectOfPerframeFunctionalGroupsSequence {

  @JsonProperty("00209111")
  private DicomAttribute<ObjectOfFrameContentSequence> frameContentSequence;
  @JsonProperty("0048021A")
  private DicomAttribute<ObjectOfPlanePositionSequence> planePositionSequence;

  public DicomAttribute<ObjectOfFrameContentSequence> getFrameContentSequence() {
    return frameContentSequence;
  }

  public void setFrameContentSequence(
      DicomAttribute<ObjectOfFrameContentSequence> frameContentSequence) {
    this.frameContentSequence = frameContentSequence;
  }

  public DicomAttribute<ObjectOfPlanePositionSequence> getPlanePositionSequence() {
    return planePositionSequence;
  }

  public void setPlanePositionSequence(
      DicomAttribute<ObjectOfPlanePositionSequence> planePositionSequence) {
    this.planePositionSequence = planePositionSequence;
  }
}
