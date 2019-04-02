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

package com.quantumsoft.qupathcloud.dao;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpStatusCodes;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import com.quantumsoft.qupathcloud.oauth20.OAuth20;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.Callable;

import static com.quantumsoft.qupathcloud.dao.Constants.BEARER;
import static com.quantumsoft.qupathcloud.exception.Errors.FAILED_HTTP;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

public class UploadDicomCallable implements Callable<Void> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private OAuth20 oAuth20;
    private File inputFile;
    private URIBuilder uriBuilder;

    UploadDicomCallable(OAuth20 oAuth20, File inputFile, URIBuilder uriBuilder) {
        this.oAuth20 = oAuth20;
        this.inputFile = inputFile;
        this.uriBuilder = uriBuilder;
    }

    @Override
    public Void call() throws IOException, QuPathCloudException, URISyntaxException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            URI uri = uriBuilder.build();
            HttpPost request = new HttpPost(uri);

            ContentType contentType = ContentType.create("application/dicom");
            String boundary = generateBoundary();

            HttpEntity httpEntity = MultipartEntityBuilder
                    .create()
                    .setBoundary(boundary)
                    .addBinaryBody("DICOMFile", inputFile, contentType, inputFile.getName())
                    .build();
            request.setEntity(httpEntity);

            request.addHeader(CONTENT_TYPE, "multipart/related; type=application/dicom; boundary=" + boundary);
            Credential credential = oAuth20.getCredential();
            request.addHeader(AUTHORIZATION, BEARER + credential.getAccessToken());
            LOGGER.debug("Start uploading DICOM file");
            try (CloseableHttpResponse response = httpclient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatusCodes.STATUS_CODE_OK){
                    throw new QuPathCloudException(FAILED_HTTP + statusCode);
                }
            }
        }
        return null;
    }

    private String generateBoundary() {
        StringBuilder buffer = new StringBuilder();
        Random rand = new Random();
        int count = rand.nextInt(11) + 30;
        for(int i = 0; i < count; ++i) {
            buffer.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buffer.toString();
    }
}
