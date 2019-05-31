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
import com.sun.jna.Pointer;
import com.sun.jna.ptr.DoubleByReference;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

/**
 * JNA Wrapper for library <b>wsi2dcm</b><br> This file was autogenerated by <a
 * href="http://jnaerator.googlecode.com/">JNAerator</a>,<br> a tool written by <a
 * href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses
 * a few opensource projects.</a>.<br> For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a>
 * , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public interface Wsi2dcmLibrary extends Library {

  String JNA_LIBRARY_NAME = "wsi2dcm";
  Wsi2dcmLibrary INSTANCE = Native.load(Wsi2dcmLibrary.JNA_LIBRARY_NAME, Wsi2dcmLibrary.class);

  /**
   * Original signature : <code>int wsi2dcm(char*, char*, long, long, char*, int, int, int, char*,
   * char*, char*, int, double*, bool, int, int, bool)</code><br>
   * <i>native declaration : line 6</i><br>
   *
   * @deprecated use the safer methods {@link #wsi2dcm(java.nio.ByteBuffer, java.nio.ByteBuffer,
   * com.sun.jna.NativeLong, com.sun.jna.NativeLong, java.nio.ByteBuffer, int, int, int,
   * java.nio.ByteBuffer, java.nio.ByteBuffer, java.nio.ByteBuffer, int, java.nio.DoubleBuffer,
   * byte, int, int, byte)} and {@link #wsi2dcm(com.sun.jna.Pointer, com.sun.jna.Pointer,
   * com.sun.jna.NativeLong, com.sun.jna.NativeLong, com.sun.jna.Pointer, int, int, int,
   * com.sun.jna.Pointer, com.sun.jna.Pointer, com.sun.jna.Pointer, int,
   * com.sun.jna.ptr.DoubleByReference, byte, int, int, byte)} instead
   */
  @Deprecated
  int wsi2dcm(Pointer inputFile, Pointer outputFileMask, NativeLong frameSizeX,
      NativeLong frameSizeY, Pointer compression, int quality, int startOnLevel, int stopOnLevel,
      Pointer imageName, Pointer studyId, Pointer seriesId, int retileLevels,
      DoubleByReference downsamples, byte tiled, int batchLimit, int threads, byte debug);

  /**
   * Original signature : <code>int wsi2dcm(char*, char*, long, long, char*, int, int, int, char*,
   * char*, char*, int, double*, bool, int, int, bool)</code><br>
   * <i>native declaration : line 6</i>
   */
  int wsi2dcm(ByteBuffer inputFile, ByteBuffer outputFileMask, NativeLong frameSizeX,
      NativeLong frameSizeY, ByteBuffer compression, int quality, int startOnLevel, int stopOnLevel,
      ByteBuffer imageName, ByteBuffer studyId, ByteBuffer seriesId, int retileLevels,
      DoubleBuffer downsamples, byte tiled, int batchLimit, int threads, byte debug);
}
