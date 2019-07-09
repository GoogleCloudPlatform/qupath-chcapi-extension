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

package com.quantumsoft.qupathcloud.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for mapping Modality Description to Modality Value.
 */
public enum Modality {
  COMPUTED_RADIOGRAPHY("CR"),
  COMPUTED_TOMOGRAPHY("CT"),
  DIGITAL_RADIOGRAPHY("DX"),
  GENERAL_MICROSCOPY("GM"),
  INTRA_ORAL_RADIOGRAPHY("IO"),
  INTRAVASCULAR_OPTICAL_COHERENCE_TOMOGRAPHY("IVOCT"),
  MAMMOGRAPHY("MG"),
  MAGNETIC_RESONANCE("MR"),
  NUCLEAR_MEDECINE("NM"),
  OPTICAL_COHERENCE_TOMOGRAPHY("OCT"),
  OPHTHALMIC_PHOTOGRAPHY("OP"),
  OPHTHALMIC_TOMOGRAPHY("OPT"),
  POSITRON_EMISSION_TOMOGRAPHY("PT"),
  PANORAMIC_X_RAY("PX"),
  RADIO_FLUOROSCOPY("RF"),
  RADIOGRAPHIC_IMAGING("RG"),
  SLIDE_MICROSCOPY("SM"),
  THERMOGRAPHY("TG"),
  X_RAY_ANGIOGRAPHY("XA"),
  EXTERNAL_CAMERA_PHOTOGRAPHY("XC"),
  QU_PATH_DATA("QPDATA"); // Modality for QuPath annotation

  private String modalityValue;
  private static final List<String> VALUES = new ArrayList<>();

  static {
    for (Modality modality : Modality.values()) {
      VALUES.add(modality.modalityValue);
    }
  }

  Modality(String modalityValue) {
    this.modalityValue = modalityValue;
  }

  /**
   * Returns all Modality Values.
   *
   * @return the values
   */
  public static List<String> getValues() {
    return Collections.unmodifiableList(VALUES);
  }

  /**
   * Returns the Modality Value of the Modality Description.
   *
   * @return the value
   */
  public String getValue() {
    return modalityValue;
  }
}
