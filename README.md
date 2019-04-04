# QuPath Cloud Healthcare API Extension

The **QuPath Cloud Healthcare API** provides viewing whole-slide images online without full downloading and synchronize annotations with the help of [Google Cloud Healthcare API](https://cloud.google.com/healthcare/).

## Installation:
1) Download the latest JAR from the releases tab.
2) Run **QuPath** application and drop the JAR on it's window or just put to /extensions in QuPath data folder.

>If you haven't installed QuPath, install QuPath from [here](https://github.com/qupath/qupath/releases/tag/v0.1.2). 

## Setting up project to use dicom cloud:
To upload to the cloud it is necessary to dicomize your whole-slide images using [OrthancWSIDicomizer](https://www.orthanc-server.com/browse.php?path=/whole-slide-imaging) and upload it with the help of [cURL](https://curl.haxx.se/)  or [dcm4che stowrs](https://github.com/dcm4che/dcm4che/tree/master/dcm4che-tool/dcm4che-tool-stowrs).
1) Dicomize your whole-slide images, following the [instructions](http://book.orthanc-server.com/plugins/wsi.html).
2) Viewing whole-slide images in QuPath.
   1) Create empty project in QuPath.
   2) Click **Cloud** button on the button bar. Login with OAuth in your default browser with your account which have access permission (1 time).
   3) Select project and click **NEXT** button in extension window.
   4) Create dataset. Click **New Dataset** button, input dataset name, choose near location and after that click **NEXT** button.
   5) Create dicom store. Click **New DICOM Store** button, input dicom store name and after that click **OK** button.
   6) Upload dicomized images in created dicom store and dataset in chosen location, following the [instructions](https://cloud.google.com/healthcare/docs/how-tos/dicom-import-export). Also you can download and use [dcm4che stowrs tool](https://sourceforge.net/projects/dcm4che/).
   7) Click **Synchronize** button on the button bar in QuPath for synchronization DICOM Store with your project.
>Extension saved in your chosen dicom store for existing project and saved your access permission on computer.   
>Supported formats for qupath-cloud extension and pathology test data you can see [here](https://openslide.org/).

## Synchronization annotations:
1) Add/Edit image annotations in existing project.
2) Save changed annotations via **File**->**Save**.
3) Click **Synchronize** button (annotations will be uploaded to Google Cloud Healthcare).
4) If dicom store contains dicomized annotations that are locally absent they will be downloaded.
5) If dicom store contains different versions of annotations for same images, user will be presented window with conflict list and asked to resolve them (with defaults set based on last modified timestamp).
6) (Re)open annotated image - need to reload ImageData containing annotations.

## Note:
There are some bugs in current release of QuPath that affect the extension when QuPath is run using it's embedded java machine on linux.  
They can be bypassed by running QuPath with system java from command line (from Qupath/app directory):  
>java -Djava.library.path=. -jar QuPathApp.jar

Make sure you're using Java 8 as the main QuPath application doesn't work for
Java10+. 

## Compilation

To compile the package from scratch you first need to locally install the main
QuPath package:
> 
>To add this dependencies for compiling extension and packaging to qupath-cloud.jar:
>1) **git clone https://github.com/qupath/qupath.git** in terminal in your local folder (for adding dependencies from QuPath).
>2) **git checkout v.0.1.2** to switch to stable release
>3) **mvn install** in QuPath folder in terminal.
>4) **mvn package** in qupath-cloud folder in terminal (for packaging to jar).  
>
>There is **qupath-cloud-0.1-jar-with-dependencies.jar** in **qupath-cloud/target** folder.

## License:

This extension is licensed under GPL v3. Full license text is available in LICENSE.
