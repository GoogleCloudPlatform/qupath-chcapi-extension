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

package com.quantumsoft.qupathcloud.synchronization;

import com.quantumsoft.qupathcloud.entities.instance.Instance;
import java.awt.image.BufferedImage;
import java.util.Date;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Pair;
import qupath.lib.projects.ProjectImageEntry;

/**
 * Conflict for local and remote Qpdata files. If DICOM Store contains different versions of
 * annotations for the same images, user will be presented window with conflict list and asked
 * to resolve them (with defaults set based on last modified timestamp).
 */
public class Conflict {

  public enum Resolution {
    Local,
    Remote,
    Cancel
  }

  private final String imageName;
  private final Pair<ProjectImageEntry<BufferedImage>, Date> local;
  private final Pair<Instance, Date> remote;
  private final ObjectProperty<Resolution> resolution;

  /**
   * Instantiates a new Conflict.
   *
   * @param imageName the image name
   * @param local the local
   * @param remote the remote
   * @param resolution the resolution
   */
  public Conflict(String imageName, Pair<ProjectImageEntry<BufferedImage>, Date> local,
      Pair<Instance, Date> remote, Resolution resolution) {
    this.imageName = imageName;
    this.local = local;
    this.remote = remote;
    this.resolution = new SimpleObjectProperty<>();
    this.resolution.set(resolution);
  }

  /**
   * Gets local.
   *
   * @return the local
   */
  public Pair<ProjectImageEntry<BufferedImage>, Date> getLocal() {
    return local;
  }

  /**
   * Gets remote.
   *
   * @return the remote
   */
  public Pair<Instance, Date> getRemote() {
    return remote;
  }

  /**
   * Gets resolution property.
   *
   * @return the resolution property
   */
  public ObjectProperty<Resolution> getResolutionProperty() {
    return resolution;
  }

  /**
   * Gets resolution.
   *
   * @return the resolution
   */
  public Resolution getResolution() {
    return resolution.getValue();
  }

  /**
   * Gets local date.
   *
   * @return the local date
   */
  public Date getLocalDate() {
    return local.getValue();
  }

  /**
   * Gets remote date.
   *
   * @return the remote date
   */
  public Date getRemoteDate() {
    return remote.getValue();
  }

  /**
   * Gets image name.
   *
   * @return the image name
   */
  public String getImageName() {
    return imageName;
  }
}
