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

import com.sun.jna.NativeLong;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.charset.StandardCharsets;
import org.dcm4che3.util.UIDUtils;

/**
 * Wrapper for the wsi2dcm library.
 */
public class Dicomizer {

  /**
   * Runs Dicomizer.
   *
   * @param options the options
   * @throws IOException if IOException occurs
   */
  public static void run(Options options) throws IOException {
    int exitCode = Wsi2dcmLibrary.INSTANCE.wsi2dcm(
        StandardCharsets.UTF_8.encode(options.getInputPath()),
        StandardCharsets.UTF_8.encode(options.getOutputFolder()),
        new NativeLong(options.getTileWidth()),
        new NativeLong(options.getTileHeight()),
        StandardCharsets.UTF_8.encode(options.getCompression().getValue()),
        options.getCompressionQuality(),
        0,
        -1,
        StandardCharsets.UTF_8.encode(options.getImageName()),
        StandardCharsets.UTF_8.encode(UIDUtils.createUID()),
        StandardCharsets.UTF_8.encode(UIDUtils.createUID()),
        options.getPyramidLevels(),
        DoubleBuffer.wrap(options.getDownsamples()),
        (byte) 1,
        500,
        options.getThreadCount(),
        (byte) 0);
    if (exitCode != 0) {
      throw new IOException("Dicomizer error, exit code: " + exitCode);
    }
  }

  /**
   * Dicomizer options.
   */
  public static class Options {

    private String inputPath;
    private String outputFolder;
    private String imageName;

    private Integer threadCount = -1; // all available
    private Integer pyramidLevels = 0; // use as is
    private Integer tileWidth = 500;
    private Integer tileHeight = 500;
    private Compression compression = Compression.JPEG;
    private int compressionQuality = 80;

    /**
     * Sets input path options.
     *
     * @param value the input path
     * @return the options
     */
    public Options inputPath(String value) {
      inputPath = value;
      return this;
    }

    /**
     * Sets threads options.
     *
     * @param count the count of threads
     * @return the options
     */
    public Options threads(int count) {
      threadCount = count;
      return this;
    }

    /**
     * Generates a pyramid with n levels, default 0 means 'use as is'
     *
     * @param levels the levels of pyramid
     * @return the options
     */
    public Options generatePyramid(int levels) {
      pyramidLevels = levels;
      return this;
    }

    /**
     * Sets width of the tiles in the target image
     *
     * @param width the tile width
     * @return the options
     */
    public Options tileWidth(int width) {
      tileWidth = width;
      return this;
    }

    /**
     * Sets height of the tiles in the target image
     *
     * @param height the tile height
     * @return the options
     */
    public Options tileHeight(int height) {
      tileHeight = height;
      return this;
    }

    /**
     * Sets compression value of the target image
     *
     * @param value the compression value
     * @return the options
     */
    public Options compression(Compression value) {
      compression = value;
      return this;
    }

    /**
     * Sets quality of the target image (0..100)
     *
     * @param value the compression quality value
     * @return the options
     */
    public Options compressionQuality(int value) {
      compressionQuality = value;
      return this;
    }

    /**
     * Sets output folder options.
     *
     * @param path the output folder path
     * @return the options
     */
    public Options outputFolder(String path) {
      outputFolder = path;
      return this;
    }

    /**
     * Sets image name is set as SeriesDescription tag
     *
     * @param name the image name
     * @return the options
     */
    public Options imageName(String name) {
      imageName = name;
      return this;

    }

    /**
     * Gets input path.
     *
     * @return the input path
     */
    public String getInputPath() {
      return inputPath;
    }

    /**
     * Gets thread count.
     *
     * @return the thread count
     */
    public Integer getThreadCount() {
      return threadCount;
    }

    /**
     * Gets pyramid levels.
     *
     * @return the pyramid levels
     */
    public Integer getPyramidLevels() {
      return pyramidLevels;
    }

    /**
     * Gets tile width.
     *
     * @return the tile width
     */
    public Integer getTileWidth() {
      return tileWidth;
    }

    /**
     * Gets tile height.
     *
     * @return the tile height
     */
    public Integer getTileHeight() {
      return tileHeight;
    }

    /**
     * Gets compression.
     *
     * @return the compression
     */
    public Compression getCompression() {
      return compression;
    }

    /**
     * Gets output folder.
     *
     * @return the output folder
     */
    public String getOutputFolder() {
      return outputFolder;
    }

    /**
     * Gets compression quality.
     *
     * @return the compression quality
     */
    public int getCompressionQuality() {
      return compressionQuality;
    }

    /**
     * Gets image name.
     *
     * @return the image name
     */
    public String getImageName() {
      return imageName;
    }

    /**
     * Gets downsamples double [ ].
     *
     * @return the double [ ]
     */
    public double[] getDownsamples() {
      double[] result = new double[pyramidLevels];
      for (int i = 0; i < pyramidLevels; i++) {
        result[i] = Math.pow(2, i);
      }
      return result;
    }

    /**
     * The enum Compression.
     */
    public enum Compression {
      /**
       * None compression.
       */
      NONE("raw"),
      /**
       * Jpeg compression.
       */
      JPEG("jpeg"),
      /**
       * Jpeg 2000 compression.
       */
      JPEG2000("jpeg2000");

      private String value;

      Compression(String value) {
        this.value = value;
      }

      /**
       * Gets compression value.
       *
       * @return the compression value
       */
      public String getValue() {
        return value;
      }
    }
  }
}
