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

package com.quantumsoft.qupathcloud.converter.dicomizer;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

/**
 * The interface wsi2dcm library.
 */
public interface Wsi2dcmLibrary extends Library {

  String JNA_LIBRARY_NAME = "wsi2dcm";
  Wsi2dcmLibrary INSTANCE = Native.load(Wsi2dcmLibrary.JNA_LIBRARY_NAME, Wsi2dcmLibrary.class);

  /**
   * @param inputFile the input file
   * @param outputFileMask the output file mask
   * @param frameSizeX the frame size x
   * @param frameSizeY the frame size y
   * @param compression the compression
   * @param quality the quality
   * @param startOnLevel the start on level
   * @param stopOnLevel the stop on level
   * @param imageName the image name
   * @param studyId the study id
   * @param seriesId the series id
   * @param retileLevels the retile levels
   * @param downsamples the downsamples
   * @param tiled the tiled
   * @param batchLimit the batch limit
   * @param threads the threads
   * @param debug the debug
   * @return the status code
   */
  int wsi2dcm(ByteBuffer inputFile, ByteBuffer outputFileMask, NativeLong frameSizeX,
      NativeLong frameSizeY, ByteBuffer compression, int quality, int startOnLevel, int stopOnLevel,
      ByteBuffer imageName, ByteBuffer studyId, ByteBuffer seriesId, int retileLevels,
      DoubleBuffer downsamples, byte tiled, int batchLimit, int threads, byte debug);
}
