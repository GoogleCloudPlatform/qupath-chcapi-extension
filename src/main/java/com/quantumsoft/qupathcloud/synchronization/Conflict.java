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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Pair;

import java.io.File;
import java.util.Date;

public class Conflict {

    public enum Resolution {
        Local,
        Remote,
        Cancel
    }

    private final String imageName;
    private final Pair<File, Date> local;
    private final Pair<Instance, Date> remote;
    private final ObjectProperty<Resolution> resolution;

    public Conflict(String imageName, Pair<File, Date> local, Pair<Instance, Date> remote, Resolution resolution) {
        this.imageName = imageName;
        this.local = local;
        this.remote = remote;
        this.resolution = new SimpleObjectProperty<>();
        this.resolution.set(resolution);
    }

    public Pair<File, Date> getLocal() {
        return local;
    }

    public Pair<Instance, Date> getRemote() {
        return remote;
    }

    public ObjectProperty<Resolution> getResolutionProperty() {
        return resolution;
    }

    public Resolution getResolution() {
        return resolution.getValue();
    }

    public Date getLocalDate() {
        return local.getValue();
    }

    public Date getRemoteDate() {
        return remote.getValue();
    }

    public String getImageName() {
        return imageName;
    }
}
