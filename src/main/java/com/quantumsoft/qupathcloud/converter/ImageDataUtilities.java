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

package com.quantumsoft.qupathcloud.converter;

import qupath.lib.images.ImageData;
import qupath.lib.images.PathImage;
import qupath.lib.images.servers.ImageServer;
import qupath.lib.images.servers.ImageServerMetadata;
import qupath.lib.io.PathIO;
import qupath.lib.regions.RegionRequest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ImageDataUtilities {
    public static final String LAST_CHANGE = "lastChange";

    // TODO this exploits bug in PathIO to avoid creating ImageServer with non-local filepath (which "naturally" happens when loading image for viewing normally)
    public static Date getModificationDate(File file){
        ImageServer<BufferedImage> imageServer = new FakeImageServer();
        ImageData<BufferedImage> imageData = new ImageData<>(imageServer);
        PathIO.readImageData(file, imageData, imageServer, BufferedImage.class);
        Date savedDate = (Date) imageData.getProperties().get(LAST_CHANGE);
        return savedDate != null ? savedDate : new Date(0);
    }

    public static class FakeImageServer implements ImageServer<BufferedImage> {
        @Override
        public String getPath() {
            return null;
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
        public double getPreferredDownsampleFactor(double requestedDownsample) {
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
        public double getTimePoint(int ind) {
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
        public BufferedImage getBufferedThumbnail(int maxWidth, int maxHeight, int zPosition) {
            return null;
        }

        @Override
        public PathImage<BufferedImage> readRegion(RegionRequest request) {
            return null;
        }

        @Override
        public BufferedImage readBufferedImage(RegionRequest request) {
            return null;
        }

        @Override
        public void close() {

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
        public String getSubImagePath(String imageName) {
            return null;
        }

        @Override
        public List<String> getAssociatedImageList() {
            return null;
        }

        @Override
        public BufferedImage getAssociatedImage(String name) {
            return null;
        }

        @Override
        public String getDisplayedImageName() {
            return null;
        }

        @Override
        public boolean containsSubImages() {
            return false;
        }

        @Override
        public boolean usesBaseServer(ImageServer<?> server) {
            return false;
        }

        @Override
        public File getFile() {
            return null;
        }

        @Override
        public boolean isEmptyRegion(RegionRequest request) {
            return false;
        }

        @Override
        public int getBitsPerPixel() {
            return 0;
        }

        @Override
        public Integer getDefaultChannelColor(int channel) {
            return null;
        }

        @Override
        public ImageServerMetadata getMetadata() {
            return null;
        }

        @Override
        public void setMetadata(ImageServerMetadata metadata) {

        }

        @Override
        public ImageServerMetadata getOriginalMetadata() {
            return null;
        }

        @Override
        public boolean usesOriginalMetadata() {
            return false;
        }
    }
}
