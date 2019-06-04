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

import com.quantumsoft.qupathcloud.dao.Modality;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Date;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.StreamUtils;
import org.dcm4che3.util.UIDUtils;

/**
 * Qpdata to dcm converter.
 */
public class DataToDcmConverter {

  static final int QPDATA_TAG = 0xff010001;
  private static final int BUFFER_SIZE = 65536;
  private Path inputFile;
  private Path outputDirectory;
  private Date modificationDate;
  private String imageName;

  /**
   * Instantiates a new Data to dcm converter.
   *
   * @param inputFile the input qpdata file
   * @param outputDirectory the output directory
   * @param modificationDate the modification date
   * @param imageName the image name
   */
  public DataToDcmConverter(Path inputFile, Path outputDirectory, Date modificationDate,
      String imageName) {
    this.inputFile = inputFile;
    this.outputDirectory = outputDirectory;
    this.modificationDate = modificationDate;
    this.imageName = imageName;
  }

  /**
   * Convert qpdata to dcm file.
   *
   * @return the to the converted qpdata file
   * @throws QuPathCloudException if IOException occurs
   */
  public Path convertQuPathDataToDcm() throws QuPathCloudException {
    try {
      Attributes attributes = new Attributes();
      attributes.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID());
      attributes.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID());
      String sopInstanceUID = UIDUtils.createUID();
      attributes.setString(Tag.SOPInstanceUID, VR.UI, sopInstanceUID);

      attributes.setString(Tag.Modality, VR.CS, Modality.QU_PATH_DATA.getValue());
      attributes.setString(Tag.SeriesNumber, VR.IS, "999");
      attributes.setString(Tag.InstanceNumber, VR.IS, "1");

      if (modificationDate == null) {
        modificationDate = new Date();
      }
      attributes.setDate(Tag.ContentDateAndTime, modificationDate);
      attributes.setDate(Tag.InstanceCreationDateAndTime, modificationDate);
      String timezoneString = ZonedDateTime.now().getOffset().toString().replace(":", "");
      attributes.setString(Tag.TimezoneOffsetFromUTC, VR.SH, timezoneString);

      attributes.setString(Tag.SOPAuthorizationComment, VR.LT, imageName);

      attributes.setString(Tag.SOPClassUID, VR.UI, UID.VerificationSOPClass);

      Path outputFile = outputDirectory.resolve(sopInstanceUID + ".dcm");
      try (BufferedInputStream bis = new BufferedInputStream(
          new FileInputStream(inputFile.toFile()))) {
        int inputFileLen = (int) Files.size(inputFile);
        try (DicomOutputStream dos = new DicomOutputStream(outputFile.toFile())) {
          dos.writeDataset(attributes.createFileMetaInformation(UID.VerificationSOPClass),
              attributes);

          dos.writeHeader(QPDATA_TAG, VR.OB, -1); //use custom tag
          dos.writeHeader(Tag.Item, null, (inputFileLen + 1) & ~1);
          byte[] buffer = new byte[BUFFER_SIZE];
          StreamUtils.copy(bis, dos, buffer);
          if ((inputFileLen & 1) != 0) {
            dos.write(0);
          }
          dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);
        }
      }
      return outputFile;
    } catch (IOException e) {
      throw new QuPathCloudException(e);
    }
  }
}
