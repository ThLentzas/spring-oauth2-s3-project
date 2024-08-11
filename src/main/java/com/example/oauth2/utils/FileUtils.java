package com.example.oauth2.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.example.oauth2.exception.ServerErrorException;

public final class FileUtils {
    private static final Map<String, String> supportedImageTypes = new HashMap<>();
    private static final Tika tika = new Tika();
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    // .jpeg and jpg both under mage/jpeg https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
    static {
        supportedImageTypes.put("image/png", ".png");
        supportedImageTypes.put("image/jpeg", ".jpeg");
    }

    private FileUtils() {
        // prevent instantiation
        throw new UnsupportedOperationException("FileUtils is a utility class and cannot be instantiated");
    }

    public static boolean isFileSupported(MultipartFile file) {
        try {
            String contentType = tika.detect(file.getInputStream());
            return supportedImageTypes.containsKey(contentType);
        } catch (IOException ioe) {
            logger.info("Failed to detect file. Error: {}", ioe.getMessage());
            throw new ServerErrorException("The server encountered an internal error and was unable to complete your request. Please try again later");
        }
    }

    public static String getFileExtension(MultipartFile file) {
        try {
            String contentType = tika.detect(file.getInputStream());
            return supportedImageTypes.get(contentType);
        } catch (IOException ioe) {
            logger.info("Failed to get file extension. Error: {}", ioe.getMessage());
            throw new ServerErrorException("The server encountered an internal error and was unable to complete your request. Please try again later");
        }
    }
}
