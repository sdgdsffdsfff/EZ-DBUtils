package com.ecfront.easybi.base.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class PropertyHelper {
    private static Map<String, String> properties;
    private static String propertiesPath;

    public static void setPropertiesPath(String path) {
        propertiesPath = path;
    }

    public static String get(String name, String defaultVal) {
        String val = get(name);
        if (null == val || "".equals(val.trim())) {
            return defaultVal;
        }
        return val;
    }

    public static String get(String name) {
        if (null == properties) {
            synchronized (PropertyHelper.class) {
                if (null == properties) {
                    try {
                        properties = new HashMap<>();
                        loadProperties(propertiesPath == null ? URLDecoder.decode(PropertyHelper.class.getResource("/").getPath(), "utf-8") : propertiesPath);
                    } catch (IOException e) {
                        logger.error("Get property error:", e);
                    }
                }
            }
        }
        return properties.containsKey(name) ? properties.get(name) : null;
    }

    private static void loadProperties(String path) throws IOException {
        File[] files = new File(path).listFiles();
        if (files == null) {
            logger.error("Config file not exist in :" + path);
            throw new IOException("Config file not exist in :" + path);
        }
        for (File file : files) {
            if (file.getName().endsWith("properties")) {
                loadProperties(file);
            } else if (file.isDirectory()) {
                loadProperties(file.getPath());
            }
        }
    }

    private static void loadProperties(File file) throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream(file));
        String key;
        for (Object o : prop.keySet()) {
            key = (String) o;
            properties.put(key.trim(), ((String) prop.get(key)).trim());
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(PropertyHelper.class);

    private PropertyHelper() {
    }
}
