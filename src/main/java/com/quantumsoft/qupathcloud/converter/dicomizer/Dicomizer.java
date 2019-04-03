package com.quantumsoft.qupathcloud.converter.dicomizer;

import com.sun.jna.NativeLong;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dcm4che3.util.UIDUtils;

import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.charset.StandardCharsets;

public class Dicomizer {
    private static final Logger LOGGER = LogManager.getLogger();

    public void run(Options options) throws IOException {
        int exitCode = DicomizerLibrary.INSTANCE.dicomize(
                StandardCharsets.UTF_8.encode(options.getInputPath()),
                StandardCharsets.UTF_8.encode(options.getOutputFolder()),
                new NativeLong(options.getTileWidth()),
                new NativeLong(options.getTileHeight()),
                StandardCharsets.UTF_8.encode(options.getCompression().getValue()),
                options.getCompressionQuality(),
                0,
                -1,
                StandardCharsets.UTF_8.encode(options.getImageName()),
                StandardCharsets.UTF_8.encode(UIDUtils.createUID()),
                StandardCharsets.UTF_8.encode(UIDUtils.createUID()),
                options.getPyramidLevels(),
                DoubleBuffer.wrap(options.getDownsamples()),
                (byte)1,
                500,
                options.getThreadCount(),
                (byte)0);
        if (exitCode != 0) {
            throw new IOException("Dicomizer error, exit code: " + exitCode);
        }
    }

    public static class Options {
        private String inputPath;
        private String outputFolder;
        private String imageName;

        private Integer threadCount = -1; // all available
        private Integer pyramidLevels = 0; // use as is
        private Integer tileWidth = 500;
        private Integer tileHeight = 500;
        private Compression compression = Compression.JPEG;
        private int compressionQuality = 80;

        public Options inputPath(String value) {
            inputPath = value;
            return this;
        }

        public Options threads(int count) {
            // default 2
            threadCount = count;
            return this;
        }

        public Options generatePyramid(int levels) {
            pyramidLevels = levels;
            return this;
        }

        public Options tileWidth(int width) {
            // Width of the tiles in the target image
            tileWidth = width;
            return this;
        }

        public Options tileHeight(int height) {
            // Height of the tiles in the target image
            tileHeight = height;
            return this;
        }

        public Options compression(Compression value) {
            // Compression of the target image
            compression = value;
            return this;
        }

        public Options compressionQuality(int value) {
            // 0..100
            compressionQuality = value;
            return this;
        }

        public Options outputFolder(String path) {
            outputFolder = path;
            return this;
        }

        public Options imageName(String name) {
            imageName = name;
            return this;

        }

        public String getInputPath() {
            return inputPath;
        }

        public Integer getThreadCount() {
            return threadCount;
        }

        public Integer getPyramidLevels() {
            return pyramidLevels;
        }

        public Integer getTileWidth() {
            return tileWidth;
        }

        public Integer getTileHeight() {
            return tileHeight;
        }

        public Compression getCompression() {
            return compression;
        }

        public String getOutputFolder() {
            return outputFolder;
        }

        public int getCompressionQuality() {
            return compressionQuality;
        }

        public String getImageName() {
            return imageName;
        }

        public double[] getDownsamples() {
            double[] result = new double[pyramidLevels];
            for (int i = 0; i < pyramidLevels; i++) {
                result[i] = Math.pow(2, i);
            }
            return result;
        }

        // check if constants are still same
        public enum Compression {
            NONE("raw"),
            JPEG("jpeg"),
            JPEG2000("jpeg2000");

            private String value;

            Compression(String value) {
                this.value = value;
            }

            public String getValue() {
                return value;
            }
        }
    }
}
