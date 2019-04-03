package com.quantumsoft.qupathcloud.converter.dicomizer;

import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openslide.OpenSlide;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

public class ImageToWsiDcmConverter {
    private static final long MAX_TOP_SIZE = 2048;
    private static final int MAX_TILE_SIZE = 512;
    private static final int MIN_TILE_SIZE = 64;
    private static final int DEFAULT_TILE_SIZE = 256;

    private static final Logger LOGGER = LogManager.getLogger();
    private File inputFile;
    private File outputDirectory;

    public ImageToWsiDcmConverter(File inputFile, File outputDirectory) {
        this.inputFile = inputFile;
        this.outputDirectory = outputDirectory;
    }

    public void convertImageToWsiDcm(String imageName) throws QuPathCloudException {
        try {
            Dicomizer.Options options = new Dicomizer.Options()
                    .inputPath(inputFile.getPath())
                    .outputFolder(outputDirectory.getPath())
                    .imageName(imageName);
            configurePyramidParameters(options);

            Dicomizer dicomizer = new Dicomizer();
            dicomizer.run(options);
        } catch (Throwable e) {
            LOGGER.error("Error converting image to dcm!", e);
            throw new QuPathCloudException(e);
        }
    }

    private String getOpenslidePath() throws IOException {
        File dir = new File(System.getProperty("user.dir"));
        File[] files = dir.listFiles((dir1, name) -> name.contains("openslide") && !name.contains("jni"));
        if (files == null || files.length != 1) {
            throw new IOException("Can't locate openslide library in: " + dir.getPath());
        }
        return files[0].getPath();
    }

    private void configurePyramidParameters(Dicomizer.Options options) {
        try (OpenSlide os = new OpenSlide(inputFile)) {
            long totalWidth = os.getLevel0Width();
            long totalHeight = os.getLevel0Height();
            LOGGER.info("totalWidth: " + totalWidth + ", totalHeight: " + totalHeight);

            int levelCount = os.getLevelCount();
            long topWidth = os.getLevelWidth(levelCount - 1);
            long topHeight = os.getLevelHeight(levelCount - 1);
            LOGGER.info("topWidth: " + topWidth + ", topHeight: " + topHeight + ", levelCount: " + levelCount);

            Map<String, String> properties = os.getProperties();
            int tileWidth = (int) readNumericPropertyOrDefault(properties, "openslide.level[0].tile-width", -1);
            int tileHeight = (int) readNumericPropertyOrDefault(properties, "openslide.level[0].tile-height", -1);
            LOGGER.info("tileWidth: " + tileWidth + ", tileHeight: " + tileHeight);

            boolean reTile = !(tileWidth >= MIN_TILE_SIZE && tileWidth <= MAX_TILE_SIZE && tileHeight >= MIN_TILE_SIZE && tileHeight <= MAX_TILE_SIZE);
            boolean rePyramid = !(topWidth <= MAX_TOP_SIZE && topHeight <= MAX_TOP_SIZE);

            if (reTile) {
                options.tileHeight(DEFAULT_TILE_SIZE);
                options.tileWidth(DEFAULT_TILE_SIZE);
            } else {
                options.tileWidth(tileWidth);
                options.tileHeight(tileHeight);
            }

            if (rePyramid) {
                int levelsWidthWise = (int) Math.ceil(Math.log((double) totalWidth / MAX_TOP_SIZE) / Math.log(2.0));
                int levelsHeightWise = (int) Math.ceil(Math.log((double) totalHeight / MAX_TOP_SIZE) / Math.log(2.0));
                int levels = Math.max(levelsWidthWise, levelsHeightWise) + 1;
                options.generatePyramid(levels);
            }
        } catch (Throwable e) {
            LOGGER.error("Failed to determine input file tiling via Openslide, setting defaults ", e);
        }
    }

    private double readNumericPropertyOrDefault(Map<String, String> properties, String name, double defaultValue) {
        // Try to read a tile size
        String value = properties.get(name);
        if (value == null) {
            LOGGER.error("Openslide: Property not available: {}", name);
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            LOGGER.error("Openslide: Could not parse property {} with value {}", name, value);
            return defaultValue;
        }
    }

    // TODO newer writeStringToFile call can conflict with older version of commons loaded from other extension jars, namely bioformats
    @SuppressWarnings("deprecation")
    private File generateDataset(String imageName) throws IOException {
        String dataset = MessageFormat.format("'{' \"ImageComments\" : \"{0}\" '}'", imageName);
        String datasetFilename = MessageFormat.format("{0}_dataset.json", imageName);
        File datasetFile = new File(System.getProperty("java.io.tmpdir"), datasetFilename);
        FileUtils.writeStringToFile(datasetFile, dataset);
        return datasetFile;
    }
}
