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

import com.quantumsoft.qupathcloud.entities.instance.Instance;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Pyramid {
    private List<PyramidLevel> levels = new ArrayList<>();
    private double[] downsamples;

    private String studyUID;
    private String seriesUID;

    public Pyramid(List<Instance> instances) throws QuPathCloudException {
        instances.sort(Comparator.comparingInt(Pyramid::getInstanceWidth).reversed());

        PyramidLevel currentLevel = null;
        for(Instance instance : instances){
            if (currentLevel == null || currentLevel.getWidth() != getInstanceWidth(instance)){
                currentLevel = new PyramidLevel(instance);
                levels.add(currentLevel);
            } else {
                currentLevel.addInstance(instance);
            }
        }

        downsamples = new double[levels.size()];
        downsamples[0] = 1.0;
        for(int i=1; i< levels.size();i++){
            downsamples[i] = (double) getWidth() / levels.get(i).getWidth();
        }

        studyUID = instances.get(0).getStudyInstanceUID().getValue1();
        seriesUID = instances.get(0).getSeriesInstanceUID().getValue1();
    }

    public double[] getDownsamples() {
        return downsamples;
    }

    public int getWidth(){
        return levels.get(0).getWidth();
    }

    public int getHeight(){
        return levels.get(0).getHeight();
    }

    public int getTileWidth(){
        return levels.get(0).getTileWidth();
    }

    public int getTileHeight(){
        return levels.get(0).getTileHeight();
    }

    public String getStudyUID() {
        return studyUID;
    }

    public String getSeriesUID() {
        return seriesUID;
    }

    public PyramidFrame getFrame(int tileX, int tileY, int level){
        return levels.get(level).getFrame(tileX, tileY);
    }

    private static int getInstanceWidth(Instance instance){
        return instance.getTotalPixelMatrixColumns().getValue1();
    }
}
