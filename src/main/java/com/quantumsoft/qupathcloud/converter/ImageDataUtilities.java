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

import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import com.quantumsoft.qupathcloud.imageserver.StubImageServer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import qupath.lib.images.ImageData;
import qupath.lib.images.servers.ImageServer;
import qupath.lib.io.PathIO;

public class ImageDataUtilities {

  public static final String LAST_CHANGE = "lastChange";

  // TODO this exploits bug in PathIO to avoid creating ImageServer with non-local filepath (which "naturally" happens when loading image for viewing normally)
  public static Date getModificationDate(File file) throws QuPathCloudException {
    ImageServer<BufferedImage> imageServer = new StubImageServer();
    ImageData<BufferedImage> imageData = new ImageData<>(imageServer);
    try {
      PathIO.readImageData(file, imageData, imageServer, BufferedImage.class);
    } catch (IOException e) {
      throw new QuPathCloudException("Read image data error!");
    }
    Date savedDate = (Date) imageData.getProperties().get(LAST_CHANGE);
    return savedDate != null ? savedDate : new Date(0);
  }
}
