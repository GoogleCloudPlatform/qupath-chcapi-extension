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
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import com.quantumsoft.qupathcloud.oauth20.OAuth20;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import static com.quantumsoft.qupathcloud.dao.CloudDAOImpl.cutHeadersAndBoundary;
import static com.quantumsoft.qupathcloud.dao.Constants.BEARER;
import static com.quantumsoft.qupathcloud.exception.Errors.FAILED_HTTP;
import static org.apache.http.HttpHeaders.*;

public class DownloadDicomCallable implements Callable<Void> {
    private static final Logger LOGGER = LogManager.getLogger();
    private OAuth20 oAuth20;
    private File outputFile;
    private URIBuilder uriBuilder;

    DownloadDicomCallable(OAuth20 oAuth20, File outputFile, URIBuilder uriBuilder) {
        this.oAuth20 = oAuth20;
        this.outputFile = outputFile;
        this.uriBuilder = uriBuilder;
    }

    @Override
    public Void call() throws IOException, QuPathCloudException, URISyntaxException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            URI uri = uriBuilder.build();
            HttpGet request = new HttpGet(uri);
            request.addHeader(ACCEPT, "multipart/related; type=application/dicom; transfer-syntax=*");
            request.addHeader(CONTENT_TYPE, "application/dicom+json; charset=utf-8");
            Credential credential = oAuth20.getCredential();
            request.addHeader(AUTHORIZATION, BEARER + credential.getAccessToken());
            LOGGER.debug("Start downloading DICOM file");
            try (CloseableHttpResponse response = httpclient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatusCodes.STATUS_CODE_OK) {
                    throw new QuPathCloudException(FAILED_HTTP + statusCode);
                }

                HttpEntity entity = response.getEntity();

                try (InputStream inputStream = entity.getContent()) {
                    InputStream inputStream1 = cutHeadersAndBoundary(inputStream);
                    Path path = Paths.get(outputFile.getAbsolutePath());
                    Files.copy(inputStream1, path);
                }
            }
        }
        return null;
    }
}
