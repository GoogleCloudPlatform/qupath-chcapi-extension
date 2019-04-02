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

public class Constants {
    public static final String SCHEME = "https";
    public static final String CLOUD_RESOURCE_MANAGER_HOST = "cloudresourcemanager.googleapis.com";
    public static final String PATH_TO_PROJECTS = "/v1/projects/";
    public static final String HEALTHCARE_HOST = "healthcare.googleapis.com";
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

    public static final String PARAM_PAGE_TOKEN = "pageToken";
    public static final String PARAM_DICOM_STORE_ID = "dicomStoreId";
    public static final String PARAM_DATASET_ID = "datasetId";
    public static final String PARAM_INCLUDE_FIELD = "includefield";
    public static final String VALUE_PARAM_STUDY_INSTANCE_UID = "0020000D";
    public static final String VALUE_PARAM_SERIES_INSTANCE_UID = "0020000E";
    public static final String VALUE_PARAM_TOTAL_PIXEL_MATRIX_COLUMNS = "00480006";
    public static final String VALUE_PARAM_TOTAL_PIXEL_MATRIX_ROWS = "00480007";
    public static final String VALUE_PARAM_PER_FRAME_FUNCTIONAL_GROUP_SEQUENCE = "52009230";
    public static final String VALUE_PARAM_IMAGE_COMMENTS = "00204000";
    public static final String VALUE_PARAM_MODALITY = "00080060";
    public static final String VALUE_PARAM_INSTANCE_CREATION_DATE = "00080012";
    public static final String VALUE_PARAM_INSTANCE_CREATION_TIME = "00080013";
    public static final String VALUE_PARAM_TIMEZONE_OFFSET_FROM_UTC = "00080201";
    public static final String VALUE_PARAM_SOP_AUTHORIZATHION_COMMENT = "01000424";

    public static final String BEARER = "Bearer ";
}
