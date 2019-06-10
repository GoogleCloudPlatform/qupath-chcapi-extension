# QuPath Cloud Healthcare API Extension
The **QuPath Cloud Healthcare API** provides viewing whole-slide images online without 
full downloading and synchronize annotations with the help of 
[Google Cloud Healthcare API](https://cloud.google.com/healthcare/).

## Installation:
1) Download the latest JAR from the releases tab.
2) Run **QuPath** application and drop the JAR on it's window or just put to /extensions in QuPath 
data folder.
>If you haven't installed QuPath v0.2.0-m2, install QuPath from 
>[here](https://github.com/qupath/qupath/releases/tag/v0.2.0-m2).  
>For new version recommended using another /extension folder to avoid conflict with the previous 
>extension version.

## Setting up project to use DICOM cloud:
To upload to the cloud it is necessary to dicomize your whole-slide images using embedded wsi2dcm 
dicomizer in qupath-chcapi-extension or using 
[OrthancWSIDicomizer](https://www.orthanc-server.com/browse.php?path=/whole-slide-imaging) 
and upload it with the help of [cURL](https://curl.haxx.se/)  or 
[dcm4che stowrs](https://sourceforge.net/projects/dcm4che/).

1) Create empty project in QuPath.
2) Click **Cloud** button on the button bar. Login with OAuth in your default browser with your 
account which have access permission (1 time).
3) Select Project and click **NEXT** button in extension window.
4) Create Dataset. Click **New Dataset** button, input Dataset name, choose near Location and after 
that click **NEXT** button. Also, you can choose existing Dataset.
5) Create DICOM Store. Click **New DICOM Store** button, input DICOM Store name. After that click 
**OK** button.
6) Upload dicomized images into created DICOM Store and Dataset in chosen Location:
   1) If you want to use the embedded wsi2dcm dicomizer in the qupath-chcapi-extension, just add 
   images to the project. When you click **Synchronize** button images will be dicomized and 
   uploaded.
   2) Alternatively you can use [OrthancWSIDicomizer](http://book.orthanc-server.com/plugins/wsi.html). 
   Once source images have been dicomized, you can upload them via 
   [curl](https://cloud.google.com/healthcare/docs/how-tos/dicom-import-export) or 
   [dcm4che stowrs tool](https://github.com/dcm4che/dcm4che/tree/master/dcm4che-tool/dcm4che-tool-stowrs).
7) Click **Synchronize** button on the button bar in QuPath to synchronize DICOM Store with your 
project.
>Extension saved in your chosen dicom store for existing project and saved your access permission 
on computer.   
>Supported formats for qupath-chcapi-extension and pathology test data you can see 
[here](https://openslide.org/).

## Synchronization annotations:
1) Add/Edit image annotations in existing project.
2) Save changed annotations via **File**->**Save**.
3) Click **Synchronize** button (annotations will be uploaded in chosen DICOM Store).
4) If DICOM Store contains dicomized annotations that are locally absent they will be downloaded.
5) If DICOM Store contains different versions of annotations for same images, user will be 
presented window with conflict list and asked to resolve them (with defaults set based on last 
modified timestamp).
6) (Re)open annotated image - need to reload ImageData containing annotations.

## Note:
QuPath uses embedded Java and may cause some errors. If you get any errors with it, follow these 
steps:
1) Download and install Java SE Development Kit 11.
2) Open QuPath/app folder in the terminal.
3) Launch QuPath with following parameters `java -Djava.library.path=. -jar qupath-0.2.0-m2.jar`

## Compilation:
Perform the following steps for compiling extension and packaging to qupath-chcapi-extension.jar:
1) **git clone https://github.com/qupath/qupath.git** in the terminal in your local folder (for 
adding dependencies from QuPath).
2) Add **apply plugin: 'maven'** string in build.gradle(Build all projects - all projects point) in 
qupath folder.
3) **./gradlew install** in qupath folder in the terminal.
4) **mvn package** in qupath-chcapi-extension folder in the terminal (for packaging to jar).  

Resulting **qupath-chcapi-extension-1.1.jar** will be put into **qupath-chcapi-extension/target** 
folder.

## License:
This extension is licensed under GPL v3. Full license text is available in LICENSE.
