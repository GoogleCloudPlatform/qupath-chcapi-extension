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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import qupath.lib.images.PathImage;
import qupath.lib.images.servers.ImageChannel;
import qupath.lib.images.servers.ImageServer;
import qupath.lib.images.servers.ImageServerMetadata;
import qupath.lib.images.servers.TileRequest;
import qupath.lib.regions.RegionRequest;

public class StubImageServer implements ImageServer<BufferedImage> {

  private String displayedImageName;
  private String path;

  @Override
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  @Override
  public String getShortServerName() {
    return null;
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
  public double getPreferredDownsampleFactor(double v) {
    return 0;
  }

  @Override
  public double getDownsampleForResolution(int i) {
    return 0;
  }

  @Override
  public int getPreferredTileWidth() {
    return 0;
  }

  @Override
  public int getPreferredTileHeight() {
    return 0;
  }

  @Override
  public double getMagnification() {
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
  public int getLevelWidth(int i) {
    return 0;
  }

  @Override
  public int getLevelHeight(int i) {
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
  public double getTimePoint(int i) {
    return 0;
  }

  @Override
  public TimeUnit getTimeUnit() {
    return null;
  }

  @Override
  public double getZSpacingMicrons() {
    return 0;
  }

  @Override
  public double getPixelWidthMicrons() {
    return 0;
  }

  @Override
  public double getPixelHeightMicrons() {
    return 0;
  }

  @Override
  public double getAveragedPixelSizeMicrons() {
    return 0;
  }

  @Override
  public boolean hasPixelSizeMicrons() {
    return false;
  }

  @Override
  public BufferedImage getBufferedThumbnail(int i, int i1, int i2) {
    return null;
  }

  @Override
  public PathImage<BufferedImage> readRegion(RegionRequest regionRequest) {
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
  public List<String> getSubImageList() {
    return null;
  }

  @Override
  public String getSubImagePath(String s) {
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

  @Override
  public String getDisplayedImageName() {
    return displayedImageName;
  }

  public void setDisplayedImageName(String displayedImageName) {
    this.displayedImageName = displayedImageName;
  }

  @Override
  public boolean containsSubImages() {
    return false;
  }

  @Override
  public boolean usesBaseServer(ImageServer<?> imageServer) {
    return false;
  }

  @Override
  public boolean isEmptyRegion(RegionRequest regionRequest) {
    return false;
  }

  @Override
  public int getBitsPerPixel() {
    return 0;
  }

  @Override
  public Integer getDefaultChannelColor(int i) {
    return null;
  }

  @Override
  public String getChannelName(int i) {
    return null;
  }

  @Override
  public List<ImageChannel> getChannels() {
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
  public boolean usesOriginalMetadata() {
    return false;
  }

  @Override
  public BufferedImage getDefaultThumbnail() {
    return null;
  }

  @Override
  public BufferedImage getDefaultThumbnail(int i, int i1) {
    return null;
  }

  @Override
  public Collection<TileRequest> getAllTileRequests() {
    return null;
  }

  @Override
  public TileRequest getTile(int i, int i1, int i2, int i3, int i4) {
    return null;
  }

  @Override
  public Collection<TileRequest> getTiles(RegionRequest regionRequest) {
    return null;
  }

  @Override
  public void close() {
  }
}
