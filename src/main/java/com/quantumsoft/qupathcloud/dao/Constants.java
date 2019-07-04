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

/**
 * Constants for dao.
 * @see <a href="https://cloud.google.com/healthcare/docs/">Cloud Healthcare API documentation</a>
 */
public class Constants {

  public static final String STAGE = "/v1beta1";
  public static final String PROJECTS = "/projects/";
  public static final String LOCATIONS = "/locations/";
  public static final String DATASETS = "/datasets/";
  public static final String DICOM_STORES = "/dicomStores/";
  public static final String DICOM_WEB = "/dicomWeb";
  public static final String STUDIES = "/studies/";
  public static final String SERIES = "/series/";
  public static final String INSTANCES = "/instances/";
  public static final String FRAMES = "/frames/";

  static final String SCHEME = "https";
  static final String CLOUD_RESOURCE_MANAGER_HOST = "cloudresourcemanager.googleapis.com";
  static final String PATH_TO_PROJECTS = "/v1/projects/";
  static final String HEALTHCARE_HOST = "healthcare.googleapis.com";

  static final String PARAM_PAGE_TOKEN = "pageToken";
  static final String PARAM_DICOM_STORE_ID = "dicomStoreId";
  static final String PARAM_DATASET_ID = "datasetId";
  static final String PARAM_INCLUDE_FIELD = "includefield";
  static final String VALUE_PARAM_STUDY_INSTANCE_UID = "0020000D";
  static final String VALUE_PARAM_SERIES_INSTANCE_UID = "0020000E";
  static final String VALUE_PARAM_TOTAL_PIXEL_MATRIX_COLUMNS = "00480006";
  static final String VALUE_PARAM_TOTAL_PIXEL_MATRIX_ROWS = "00480007";
  static final String VALUE_PARAM_PER_FRAME_FUNCTIONAL_GROUP_SEQUENCE = "52009230";
  static final String VALUE_PARAM_IMAGE_COMMENTS = "00204000";
  static final String VALUE_PARAM_MODALITY = "00080060";
  static final String VALUE_PARAM_INSTANCE_CREATION_DATE = "00080012";
  static final String VALUE_PARAM_INSTANCE_CREATION_TIME = "00080013";
  static final String VALUE_PARAM_TIMEZONE_OFFSET_FROM_UTC = "00080201";
  static final String VALUE_PARAM_SOP_AUTHORIZATHION_COMMENT = "01000424";
  static final String VALUE_PARAM_DIMENSION_ORGANIZATION_TYPE = "00209311";
  static final String VALUE_PARAM_CONCATENATION_UID = "00209161";
  static final String VALUE_PARAM_CONCATENATION_FRAME_OFFSET_NUMBER = "00209228";
  static final String VALUE_PARAM_NUMBER_OF_FRAMES = "00209228";

  static final String BEARER = "Bearer ";
  static final String APPLICATION_JSON_CHARSET_UTF8 = "application/json; charset=utf-8";
  static final String APPLICATION_DICOM_TRANSFER_SYNTAX = "application/dicom; transfer-syntax=*";
  static final String APPLICATION_DICOM_JSON_CHARSET_UTF8 = "application/dicom+json; charset=utf-8";
  static final String MULTIPART_RELATED_TYPE_APPLICATION_DICOM_BOUNDARY =
      "multipart/related; type=application/dicom; boundary=";
  static final String MULTIPART_RELATED_TYPE_IMAGE_JPEG_TRANSFER_SYNTAX =
      "multipart/related; type=image/jpeg; transfer-syntax=1.2.840.10008.1.2.4.50";
}
