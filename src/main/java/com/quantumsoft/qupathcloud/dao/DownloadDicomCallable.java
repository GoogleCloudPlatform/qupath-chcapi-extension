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

import static com.quantumsoft.qupathcloud.dao.Constants.APPLICATION_DICOM_JSON_CHARSET_UTF8;
import static com.quantumsoft.qupathcloud.dao.Constants.APPLICATION_DICOM_TRANSFER_SYNTAX;
import static com.quantumsoft.qupathcloud.dao.Constants.BEARER;
import static com.quantumsoft.qupathcloud.exception.Errors.FAILED_HTTP;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpStatusCodes;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import com.quantumsoft.qupathcloud.oauth20.OAuth20;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Download chosen Instance from DICOM Store.
 */
public class DownloadDicomCallable implements Callable<Void> {

  private static final Logger LOGGER = LogManager.getLogger();
  private OAuth20 oAuth20;
  private Path outputFile;
  private URIBuilder uriBuilder;

  /**
   * Instantiates a new Download dicom callable.
   *
   * @param oAuth20 the oAuth20
   * @param outputFile the output file
   * @param uriBuilder the uri builder
   */
  DownloadDicomCallable(OAuth20 oAuth20, Path outputFile, URIBuilder uriBuilder) {
    this.oAuth20 = oAuth20;
    this.outputFile = outputFile;
    this.uriBuilder = uriBuilder;
  }

  @Override
  public Void call() throws IOException, QuPathCloudException, URISyntaxException {
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      URI uri = uriBuilder.build();
      HttpGet request = new HttpGet(uri);
      request.addHeader(ACCEPT, APPLICATION_DICOM_TRANSFER_SYNTAX);
      request.addHeader(CONTENT_TYPE, APPLICATION_DICOM_JSON_CHARSET_UTF8);
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
          Files.copy(inputStream, outputFile);
        }
      }
    }
    return null;
  }
}
