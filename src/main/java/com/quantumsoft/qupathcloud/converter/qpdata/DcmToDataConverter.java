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

package com.quantumsoft.qupathcloud.converter.qpdata;

import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Fragments;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;

public class DcmToDataConverter {

  private Path inputFile;
  private Path outputDirectory;

  public DcmToDataConverter(Path inputFile, Path outputDirectory) {
    this.inputFile = inputFile;
    this.outputDirectory = outputDirectory;
  }

  public Path convertDcmToQuPathData() throws QuPathCloudException {
    try (DicomInputStream dis = new DicomInputStream(inputFile.toFile())) {
      Attributes attrs = dis.readDataset(-1, -1);
      byte[] qpdataBytes = (byte[]) ((Fragments) attrs.getValue(DataToDcmConverter.QPDATA_TAG))
          .get(0);
      String qpdataName = attrs.getString(Tag.SOPAuthorizationComment);
      // TODO: check qpdataName
      Path outputFile = outputDirectory.resolve(qpdataName + ".qpdata");
      try (FileOutputStream fos = new FileOutputStream(outputFile.toFile(), false)) {
        fos.write(qpdataBytes, 0, qpdataBytes.length);
        fos.close();
        return outputFile;
      }
    } catch (IOException e) {
      throw new QuPathCloudException(e);
    }
  }
}
