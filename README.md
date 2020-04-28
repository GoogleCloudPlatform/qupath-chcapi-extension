# QuPath Cloud Healthcare API Extension

The **QuPath Cloud Healthcare API** provides viewing whole-slide images online without 
full downloading and [synchronize annotations](#synchronization-annotations) with the help of 
[Google Cloud Healthcare API](https://cloud.google.com/healthcare/).

## Installation:

1) Install QuPath v0.2.0-m10 from [here](https://github.com/qupath/qupath/releases/tag/v0.2.0-m10).
2) Download the latest JAR from the releases tab.
3) When you install QuPath, folder for extensions is set to */home/user/QuPath/extensions* by 
default (in Linux). You can put the JAR in this folder or just drag-and-drop it in the QuPath 
window.
> If you already have QuPath and want to install the new version a new QuPath user directory is 
> recommended. You can set another user directory by going to Edit -> Preferences -> Set another 
> user directory in QuPath. Also, you can delete the previous extension version, if you don't want 
> to use another QuPath user directory.

## Setting up project to use DICOM cloud:

1) Create empty project in QuPath.
2) Click **Cloud** on the button bar. Login with OAuth in your default browser with your 
account which has access permission to Cloud Healthcare API (1 time).
3) Select the desired Project and click **NEXT** in extension window.
4) Select an existing Dataset or create a new one. Steps to create a new Dataset:
   1) Click **New Dataset**.
   2) Input the Dataset name.
   3) Select the location where the Dataset should be stored.
   4) Click **CREATE**.
   5) Click **NEXT**.
5) Select an existing DICOM Store which contains whole-slide images or create a new one. Steps to 
create a new DICOM Store:
   1) Click **New DICOM Store**.
   2) Input the DICOM Store name.
   3) Click **CREATE**.
   4) Click **OK**.
6) Upload dicomized images into created DICOM Store and Dataset in chosen Location:
   1) If you want to use the embedded wsi-to-dicom-converter in the qupath-chcapi-extension, just 
   add images to the project. When you click **Synchronize** images will be dicomized and uploaded.
   2) Alternatively you can use 
   [OrthancWSIDicomizer](https://www.orthanc-server.com/browse.php?path=/whole-slide-imaging). 
   Once source images have been dicomized, you can upload them via 
   [gcloud](https://cloud.google.com/healthcare/docs/how-tos/dicom-import-export).
7) Click **Synchronize** on the button bar in QuPath to synchronize DICOM Store with your project.
> Extension saved in your chosen dicom store for existing project and saved your access permission 
> on the computer.   
> Supported formats for qupath-chcapi-extension and pathology test data you can see 
> [here](https://openslide.org/).

## Synchronization annotations:

1) Add/Edit image annotations in existing project.
2) Save changed annotations via **File**->**Save**.
3) Click **Synchronize** (annotations will be uploaded in chosen DICOM Store).
4) If DICOM Store contains dicomized annotations that are locally absent they will be downloaded.
5) If DICOM Store contains different versions of annotations for the same images, user will be 
presented window with conflict list and asked to resolve them (with defaults set based on last 
modified timestamp).
6) (Re)open annotated image - need to reload ImageData containing annotations.

## Note:

QuPath uses embedded Java and may cause some errors. If you get any errors with it, follow these 
steps:
1) Download and install Java SE Development Kit 11.
2) Open QuPath/app folder in the terminal.
3) Launch QuPath with following parameters `java -Djava.library.path=. -jar qupath-0.2.0-m10.jar`

## Compilation:

Perform the following steps for compiling extension and packaging to qupath-chcapi-extension.jar:
1) **git clone https://github.com/qupath/qupath.git** in the terminal in your local folder (for 
adding dependencies from QuPath).
2) **git checkout v0.2.0-m10** to switch to QuPath v0.2.0-m10.
3) Add **apply plugin: 'maven'** string in build.gradle(Build all projects - all projects point) in 
qupath folder.
4) **./gradlew install** in qupath folder in the terminal.
5) Copy **openslide** folder to **.m/repository/org/** from **qupath/maven/repo/org/** folder. It's 
necessary to copy openslide dependencies to the local maven repository.
6) **git clone https://github.com/GoogleCloudPlatform/qupath-chcapi-extension.git**
7) **mvn package** in qupath-chcapi-extension folder in the terminal (for packaging to jar).  

Resulting **qupath-chcapi-extension-X.Y.Z.jar** will be put into **qupath-chcapi-extension/target** 
folder.

## License:

This extension is licensed under GPL v3. Full license text is available in LICENSE.
