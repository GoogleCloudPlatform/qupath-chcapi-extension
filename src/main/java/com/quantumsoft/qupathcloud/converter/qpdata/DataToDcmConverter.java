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
import org.apache.commons.io.FilenameUtils;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.StreamUtils;
import org.dcm4che3.util.UIDUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Date;

public class DataToDcmConverter {
    public static final int QPDATA_TAG = 0xff010001;
    private static final int BUFFER_SIZE = 65536;
    private File inputFile;
    private File outputDirectory;
    private Date modificationDate;

    public DataToDcmConverter(File inputFile, File outputDirectory, Date modificationDate){
        this.inputFile = inputFile;
        this.outputDirectory = outputDirectory;
        this.modificationDate = modificationDate;
    }

    public File convertQuPathDataToDcm() throws QuPathCloudException {
        try {
            Attributes attributes = new Attributes();
            attributes.setString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID());
            attributes.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID());
            String sopInstanceUID = UIDUtils.createUID();
            attributes.setString(Tag.SOPInstanceUID, VR.UI, sopInstanceUID);

            attributes.setString(Tag.Modality, VR.CS, Modality.QU_PATH_DATA.getValue());
            attributes.setString(Tag.SeriesNumber, VR.IS, "999");
            attributes.setString(Tag.InstanceNumber, VR.IS, "1");

            if(modificationDate == null){
                modificationDate = new Date();
            }
            attributes.setDate(Tag.ContentDateAndTime, modificationDate);
            attributes.setDate(Tag.InstanceCreationDateAndTime, modificationDate);
            String timezoneString = ZonedDateTime.now().getOffset().toString().replace(":","");
            attributes.setString(Tag.TimezoneOffsetFromUTC, VR.SH, timezoneString);

            // QuPath associates qpdata with ImageEntry by name
            String dataFileName = FilenameUtils.getBaseName(inputFile.getPath());
            attributes.setString(Tag.SOPAuthorizationComment, VR.LT, dataFileName);

            attributes.setString(Tag.SOPClassUID, VR.UI, UID.VerificationSOPClass);

            File outputFile = new File(outputDirectory, sopInstanceUID + ".dcm");
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile))) {
                int inputFileLen = (int)inputFile.length();
                try (DicomOutputStream dos = new DicomOutputStream(outputFile)) {
                    dos.writeDataset(attributes.createFileMetaInformation(UID.VerificationSOPClass), attributes);

                    dos.writeHeader(QPDATA_TAG, VR.OB, -1); //use custom tag
                    dos.writeHeader(Tag.Item, null, (inputFileLen + 1) & ~1);
                    byte [] buffer = new byte[BUFFER_SIZE];
                    StreamUtils.copy(bis, dos, buffer);
                    if ((inputFileLen & 1) != 0)
                        dos.write (0);
                    dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);
                }
            }
            return outputFile;
        } catch (IOException e) {
            throw new QuPathCloudException(e);
        }
    }
}
