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

package com.quantumsoft.qupathcloud.pyramid;

/**
 * The PyramidFrame to display the requested tile of an whole-image in QuPath.
 */
public class PyramidFrame {

  private int index;
  private String instanceUID;

    /**
     * Instantiates a new Pyramid frame.
     *
     * @param instanceUID the Instance UID
     * @param index       the index
     */
    public PyramidFrame(String instanceUID, int index) {
    this.index = index;
    this.instanceUID = instanceUID;
  }

    /**
     * Gets index.
     *
     * @return the index
     */
    public int getIndex() {
    return index;
  }

    /**
     * Gets Instance UID.
     *
     * @return the Instance UID
     */
    public String getInstanceUID() {
    return instanceUID;
  }
}
