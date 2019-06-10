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

import com.quantumsoft.qupathcloud.configuration.MetadataConfiguration;
import com.quantumsoft.qupathcloud.entities.instance.Instance;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * The type Load pyramid file callable.
 */
public class LoadPyramidFileCallable implements Callable<Pyramid> {

  private Path filePath;

  /**
   * Instantiates a new Load pyramid file callable.
   *
   * @param filePath the file path
   */
  public LoadPyramidFileCallable(Path filePath) {
    this.filePath = filePath;
  }

  @Override
  public Pyramid call() throws QuPathCloudException {
    MetadataConfiguration metaConf = new MetadataConfiguration(filePath.getParent());
    List<Instance> instanceList = metaConf.readMetadataFile(filePath);
    return new Pyramid(instanceList);
  }
}
