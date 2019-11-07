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

package com.quantumsoft.qupathcloud.imageserver;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import qupath.lib.images.servers.ImageChannel;
import qupath.lib.images.servers.ImageServer;
import qupath.lib.images.servers.ImageServerMetadata;
import qupath.lib.images.servers.PixelType;
import qupath.lib.images.servers.TileRequest;
import qupath.lib.images.servers.TileRequestManager;
import qupath.lib.regions.RegionRequest;

/**
 * Stub image server which contains only displayed image name and path.
 */
public class StubImageServer implements ImageServer<BufferedImage> {

  private String displayedImageName;
  private String path;

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public Collection<URI> getURIs() {
    return null;
  }

  /**
   * Sets path.
   *
   * @param path the path
   */
  public void setPath(String path) {
    this.path = path;
  }


  @Override
  public double[] getPreferredDownsamples() {
    return new double[0];
  }

  @Override
  public int nResolutions() {
    return 0;
  }

  @Override
  public double getDownsampleForResolution(int i) {
    return 0;
  }


  @Override
  public int getWidth() {
    return 0;
  }

  @Override
  public int getHeight() {
    return 0;
  }

  @Override
  public int nChannels() {
    return 0;
  }

  @Override
  public boolean isRGB() {
    return false;
  }

  @Override
  public int nZSlices() {
    return 0;
  }

  @Override
  public int nTimepoints() {
    return 0;
  }

  @Override
  public BufferedImage getCachedTile(TileRequest tile) {
    return null;
  }

  @Override
  public BufferedImage readBufferedImage(RegionRequest regionRequest) {
    return null;
  }

  @Override
  public String getServerType() {
    return null;
  }

  @Override
  public List<String> getAssociatedImageList() {
    return null;
  }

  @Override
  public BufferedImage getAssociatedImage(String s) {
    return null;
  }


  /**
   * Sets displayed image name.
   *
   * @param displayedImageName the displayed image name
   */
  public void setDisplayedImageName(String displayedImageName) {
    this.displayedImageName = displayedImageName;
  }

  @Override
  public boolean isEmptyRegion(RegionRequest regionRequest) {
    return false;
  }

  @Override
  public PixelType getPixelType() {
    return null;
  }

  @Override
  public ImageChannel getChannel(int channel) {
    return null;
  }

  @Override
  public ImageServerMetadata getMetadata() {
    return null;
  }

  @Override
  public void setMetadata(ImageServerMetadata imageServerMetadata) throws IllegalArgumentException {

  }

  @Override
  public ImageServerMetadata getOriginalMetadata() {
    return null;
  }

  @Override
  public BufferedImage getDefaultThumbnail(int i, int i1) {
    return null;
  }

  @Override
  public TileRequestManager getTileRequestManager() {
    return null;
  }

  @Override
  public Class<BufferedImage> getImageClass() {
    return null;
  }

  @Override
  public void close() {
  }
}
