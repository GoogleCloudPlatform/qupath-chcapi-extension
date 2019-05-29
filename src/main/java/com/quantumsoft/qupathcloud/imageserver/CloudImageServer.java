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

import com.quantumsoft.qupathcloud.dao.CloudDAO;
import com.quantumsoft.qupathcloud.dao.spec.QueryBuilder;
import com.quantumsoft.qupathcloud.entities.DicomStore;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import com.quantumsoft.qupathcloud.pyramid.LoadPyramidFileCallable;
import com.quantumsoft.qupathcloud.pyramid.Pyramid;
import com.quantumsoft.qupathcloud.pyramid.PyramidFrame;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qupath.lib.awt.common.AwtTools;
import qupath.lib.images.DefaultPathImage;
import qupath.lib.images.PathImage;
import qupath.lib.images.servers.AbstractImageServer;
import qupath.lib.images.servers.ImageChannel;
import qupath.lib.images.servers.ImageServer;
import qupath.lib.images.servers.ImageServerMetadata;
import qupath.lib.images.servers.ServerTools;
import qupath.lib.regions.RegionRequest;

public class CloudImageServer extends AbstractImageServer<BufferedImage> {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final int THREADS_COUNT = 16; // light threads that mostly wait for remote data
  private static final String DRAW_DEBUG_INFO_PROPERTY = "quPathCloud.drawDebugInfo";
  private static final String DRAW_PLACEHOLDER_TILES_PROPERTY = "quPathCloud.drawPlaceholderTiles";
  private static final boolean DRAW_DEBUG_INFO = Boolean.getBoolean(DRAW_DEBUG_INFO_PROPERTY);
  private static final boolean DRAW_PLACEHOLDER_TILES =
      Boolean.getBoolean(DRAW_PLACEHOLDER_TILES_PROPERTY);
  private final CloudDAO cloudDAO;
  private ImageServerMetadata originalMetadata;
  private ImageServerMetadata userMetadata;
  private DicomStore dicomStore;
  private Pyramid pyramid;
  private ExecutorService executorService;

  public CloudImageServer(URI uri, CloudDAO cloudDAO, DicomStore dicomStore)
      throws QuPathCloudException {
    this.cloudDAO = cloudDAO;
    this.dicomStore = dicomStore;
    this.executorService = Executors.newFixedThreadPool(THREADS_COUNT);

    pyramid = new LoadPyramidFileCallable(Paths.get(uri)).call();

    originalMetadata = new ImageServerMetadata.Builder(getClass(), uri.toString())
        .height(pyramid.getHeight())
        .width(pyramid.getWidth())
        .channels(ImageChannel.getDefaultRGBChannels())
        .preferredTileSize(pyramid.getTileWidth(), pyramid.getTileHeight())
        .levelsFromDownsamples(pyramid.getDownsamples())
        .rgb(true)
        .build();
  }

  @Override
  public double[] getPreferredDownsamples() {
    return pyramid.getDownsamples();
  }

  @Override
  public boolean isRGB() {
    return true;
  }

  @Override
  public double getTimePoint(int ind) {
    return 0;
  }

  @Override
  public PathImage<BufferedImage> readRegion(RegionRequest request) {
    BufferedImage img = readBufferedImage(request);
    if (img == null) {
      return null;
    }
    return new DefaultPathImage<>(this, request, img);
  }

  @Override
  public BufferedImage readBufferedImage(RegionRequest request) {
    Rectangle region = AwtTools.getBounds(request);
    if (region == null) {
      region = new Rectangle(0, 0, getWidth(), getHeight());
    }

    double downsampleFactor = request.getDownsample();
    int level = ServerTools.getClosestDownsampleIndex(getPreferredDownsamples(), downsampleFactor);
    double downsample = getPreferredDownsamples()[level];
    int levelWidth = (int) (region.width / downsample + .5);
    int levelHeight = (int) (region.height / downsample + .5);

    Map<Point, BufferedImage> tileImagesMap = new HashMap<>();
    List<Callable<Void>> tileCallables = new ArrayList<>();
    int baseTileX = (int) Math.round((request.getX() / downsample + .5) / pyramid.getTileWidth());
    int baseTileY = (int) Math.round((request.getY() / downsample + .5) / pyramid.getTileHeight());
    int widthInTiles = (int) Math.ceil((double) levelWidth / pyramid.getTileWidth());
    int heightInTiles = (int) Math.ceil((double) levelHeight / pyramid.getTileHeight());

    BufferedImage img = new BufferedImage(levelWidth, levelHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D g1 = img.createGraphics();

    if (DRAW_DEBUG_INFO) {
      Font font = new Font("Serif", Font.PLAIN, levelWidth / widthInTiles / 10);
      g1.setFont(font);
      g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    final QueryBuilder baseQuery = QueryBuilder.forProject(dicomStore.getProjectId())
        .setLocationId(dicomStore.getLocationId())
        .setDatasetId(dicomStore.getDatasetId())
        .setDicomStoreId(dicomStore.getDicomStoreId())
        .setStudyId(pyramid.getStudyUID())
        .setSeriesId(pyramid.getSeriesUID());
    for (int x = 0; x < widthInTiles; x++) {
      for (int y = 0; y < heightInTiles; y++) {
        int tileX = baseTileX + x;
        int tileY = baseTileY + y;

        PyramidFrame frame = pyramid.getFrame(tileX + 1, tileY + 1, level);
        if (frame != null) {
          tileCallables.add(new Callable<>() {
            @Override
            public Void call() throws Exception {
              QueryBuilder query = new QueryBuilder(baseQuery)
                  .setInstanceId(frame.getInstanceUID())
                  .setFrameNumber(frame.getIndex());
              BufferedImage tileImage = cloudDAO.getFrame(query);

              if (tileImage != null) {
                synchronized (tileImagesMap) {
                  tileImagesMap.put(new Point(tileX, tileY), tileImage);
                }
              }

              synchronized (tileCallables) {
                tileCallables.remove(this);
              }

              return null;
            }
          });
        } else {
          LOGGER.warn("No frame for " + tileX + "/" + tileY + "/" + level);
        }
      }
    }

    try {
      executorService.invokeAll(tileCallables);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      LOGGER.error("CloudImageServer.readBufferedImage interrupted", e);
    }

    for (int x = 0; x < widthInTiles; x++) {
      for (int y = 0; y < heightInTiles; y++) {
        int tileX = baseTileX + x;
        int tileY = baseTileY + y;
        int graphicsX = x * pyramid.getTileWidth();
        int graphicsY = y * pyramid.getTileHeight();

        BufferedImage tileImage = tileImagesMap.get(new Point(tileX, tileY));

        if (tileImage != null) {
          g1.drawImage(tileImage, graphicsX, graphicsY, null);
        } else {
          if (DRAW_PLACEHOLDER_TILES) {
            // qupath will cache debug tile semi-permanently
            LOGGER.warn("Drawing placeholder tile " + tileX + "/" + tileY + "/" + level);
            drawPlaceholderTile(g1, request.getX(), request.getY(), graphicsX, graphicsY,
                downsample);
          } else {
            // qupath will re-query the region
            LOGGER.warn("Returning null image for region request: " + request);
            return null;
          }
        }

        if (DRAW_DEBUG_INFO) {
          g1.setColor(Color.black);
          g1.drawString(tileX + "/" + tileY + "/" + level, 10 + graphicsX,
              graphicsY + pyramid.getTileHeight() - 20);

          g1.setColor(Color.black);
          g1.drawRect(graphicsX, graphicsY, pyramid.getTileWidth() - 1,
              pyramid.getTileHeight() - 1);
        }
      }
    }

    if (DRAW_DEBUG_INFO) {
      g1.setColor(Color.white);
      g1.drawRect(0, 0, levelWidth - 1, levelHeight - 1);

      g1.setColor(Color.white);
      String tileMark = "x=" + request.getX() + ", y=" + request.getY()
          + "\nw=" + request.getWidth() + ", h=" + request.getHeight()
          + "\nds=" + request.getDownsample()
          + "\nlW=" + levelWidth + ", lH=" + levelHeight
          + "\neDs=" + downsample;
      drawMultiString(g1, tileMark, 10, 10);
    }

    g1.dispose();

    return img;
  }

  @Override
  public String getServerType() {
    return "Cloud Pyramid Server";
  }

  @Override
  public List<String> getSubImageList() {
    return Collections.emptyList();
  }

  @Override
  public List<String> getAssociatedImageList() {
    return Collections.emptyList();
  }

  @Override
  public BufferedImage getAssociatedImage(String name) {
    return null;
  }

  @Override
  public String getDisplayedImageName() {
    return getShortServerName();
  }

  @Override
  public boolean containsSubImages() {
    return false;
  }

  @Override
  public boolean usesBaseServer(ImageServer<?> server) {
    return this == server;
  }

  @Override
  public int getBitsPerPixel() {
    return 8;
  }

  @Override
  public Integer getDefaultChannelColor(int channel) {
    return ImageChannel.getDefaultChannelColor(channel);
  }

  @Override
  public ImageServerMetadata getMetadata() {
    return userMetadata != null ? userMetadata : originalMetadata;
  }

  @Override
  public void setMetadata(ImageServerMetadata metadata) {
    if (!originalMetadata.isCompatibleMetadata(metadata)) {
      throw new RuntimeException(
          "Specified metadata is incompatible with original metadata for " + this);
    }
    userMetadata = metadata;
  }

  @Override
  public ImageServerMetadata getOriginalMetadata() {
    return originalMetadata;
  }

  private void drawMultiString(Graphics g, String text, int x, int y) {
    for (String line : text.split("\n")) {
      g.drawString(line, x, y += g.getFontMetrics().getHeight());
    }
  }

  private void drawPlaceholderTile(Graphics g1, int requestX, int requestY, int x, int y,
      double downsample) {
    float ds = (float) downsample;
    float r = 1.0f * (requestX + x * ds) / getWidth();
    float g = 1.0f * (requestY + y * ds) / getHeight();
    float b = ds / 256.0f;
    r = Math.max(0, Math.min(1, r));
    g = Math.max(0, Math.min(1, g));
    b = Math.max(0, Math.min(1, b));
    g1.setColor(new Color(r, g, b));
    g1.fillRect(x, y, pyramid.getTileWidth(), pyramid.getTileHeight());
  }
}
