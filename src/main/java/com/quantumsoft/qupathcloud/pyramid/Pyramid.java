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

import com.quantumsoft.qupathcloud.entities.DicomAttribute;
import com.quantumsoft.qupathcloud.entities.instance.Instance;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import qupath.lib.images.servers.ImageChannel;
import qupath.lib.images.servers.ImageServerMetadata;

/**
 * The Pyramid to display an whole-slide image in QuPath.
 */
public class Pyramid {

  private List<PyramidLevel> levels = new ArrayList<>();
  private double[] downsamples;

  private String studyUID;
  private String seriesUID;

  /**
   * Instantiates a new Pyramid.
   *
   * @param instances the instances
   * @param metadataOnly if true, parses metadata, but not actual frames. Qupath uses servers only
   * for metadata in some cases, so this is a useful optimization.
   * @throws QuPathCloudException if an exception occurs
   */
  public Pyramid(List<Instance> instances, boolean metadataOnly) throws QuPathCloudException {
    if (instances.get(0).isFullTiled()) {
      instances.sort(Comparator.comparingInt(Pyramid::getInstanceFrameOffset));
    }
    instances.sort(Comparator.comparingInt(Pyramid::getInstanceWidth).reversed());

    PyramidLevel currentLevel = null;
    for (Instance instance : instances) {
      if (currentLevel == null || currentLevel.getWidth() != getInstanceWidth(instance)) {
        currentLevel = new PyramidLevel(instance);
        levels.add(currentLevel);
      }
      if (!metadataOnly) {
        currentLevel.addInstance(instance);
      }
    }

    downsamples = new double[levels.size()];
    downsamples[0] = 1.0;
    for (int i = 1; i < levels.size(); i++) {
      downsamples[i] = (double) getWidth() / levels.get(i).getWidth();
    }

    studyUID = instances.get(0).getStudyInstanceUID().getValue1();
    seriesUID = instances.get(0).getSeriesInstanceUID().getValue1();
  }

  /**
   * Get downsamples double [ ], which contains ratios of level 0 size to level n sizes.
   * For example, each level being twice smaller would produce [1, 2, 4, 8,...,2^n]
   *
   * @return the double [ ]
   */
  public double[] getDownsamples() {
    return downsamples;
  }

  /**
   * Gets width.
   *
   * @return the width
   */
  public int getWidth() {
    return levels.get(0).getWidth();
  }

  /**
   * Gets height.
   *
   * @return the height
   */
  public int getHeight() {
    return levels.get(0).getHeight();
  }

  /**
   * Gets tile width.
   *
   * @return the tile width
   */
  public int getTileWidth() {
    return levels.get(0).getTileWidth();
  }

  /**
   * Gets tile height.
   *
   * @return the tile height
   */
  public int getTileHeight() {
    return levels.get(0).getTileHeight();
  }

  /**
   * Gets Study UID.
   *
   * @return the Study UID
   */
  public String getStudyUID() {
    return studyUID;
  }

  /**
   * Gets Series UID.
   *
   * @return the Series UID
   */
  public String getSeriesUID() {
    return seriesUID;
  }

  /**
   * Gets frame.
   *
   * @param tileX the tile x
   * @param tileY the tile y
   * @param level the level
   * @return the frame
   */
  public PyramidFrame getFrame(int tileX, int tileY, int level) {
    return levels.get(level).getFrame(tileX, tileY);
  }

  /**
   * Gets Series Metadata.
   *
   * @return the Series Metadata
   */
  public ImageServerMetadata getMetadata() {
    return new ImageServerMetadata.Builder()
        .height(getHeight())
        .width(getWidth())
        .channels(ImageChannel.getDefaultRGBChannels())
        .preferredTileSize(getTileWidth(), getTileHeight())
        .levelsFromDownsamples(getDownsamples())
        .rgb(true)
        .build();
  }

  private static int getInstanceWidth(Instance instance) {
    return instance.getTotalPixelMatrixColumns().getValue1();
  }

  private static int getInstanceFrameOffset(Instance instance) {
    DicomAttribute<Integer> offset = instance.getConcatenationFrameOffsetNumber();
    return offset == null ? 0 : offset.getValue1();
  }
}
