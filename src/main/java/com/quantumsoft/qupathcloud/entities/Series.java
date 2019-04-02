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

package com.quantumsoft.qupathcloud.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Series {
    @JsonProperty("0020000D")
    private DicomAttribute<String> studyInstanceUID;
    @JsonProperty("0020000E")
    private DicomAttribute<String> seriesInstanceUID;
    @JsonProperty("00204000")
    private DicomAttribute<String> imageComments;
    @JsonProperty("00080060")
    private DicomAttribute<String> modality;

    public DicomAttribute<String> getStudyInstanceUID() {
        return studyInstanceUID;
    }

    public void setStudyInstanceUID(DicomAttribute<String> studyInstanceUID) {
        this.studyInstanceUID = studyInstanceUID;
    }

    public DicomAttribute<String> getSeriesInstanceUID() {
        return seriesInstanceUID;
    }

    public void setSeriesInstanceUID(DicomAttribute<String> seriesInstanceUID) {
        this.seriesInstanceUID = seriesInstanceUID;
    }

    public DicomAttribute<String> getImageComments() {
        if(imageComments == null){
            String temp = studyInstanceUID.getValue1() + seriesInstanceUID.getValue1();
            imageComments = new DicomAttribute<>();
            imageComments.setValue( new String[]{ String.valueOf(temp.hashCode())});
        }
        return imageComments;
    }

    public void setImageComments(DicomAttribute<String> imageComments) {
        this.imageComments = imageComments;
    }

    public DicomAttribute<String> getModality() {
        return modality;
    }

    public void setModality(DicomAttribute<String> modality) {
        this.modality = modality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Series series = (Series) o;
        return Objects.equals(studyInstanceUID, series.studyInstanceUID) &&
                Objects.equals(seriesInstanceUID, series.seriesInstanceUID) &&
                Objects.equals(imageComments, series.imageComments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studyInstanceUID, seriesInstanceUID, imageComments);
    }
}
