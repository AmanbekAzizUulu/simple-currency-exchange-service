package com.dandaev.edu.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PropertiesUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);
	private static final Properties properties = new Properties();

	private static final String[] PROPERTIES_FILES = { "application.properties", "database.properties" };

	static {
		for (String file : PROPERTIES_FILES) {
			try (InputStream input = PropertiesUtil.class.getClassLoader().getResourceAsStream(file)) {
				if (input != null) {
					properties.load(input);
					LOGGER.info("Loaded properties from {}", file);
				} else {
					LOGGER.warn("Properties file '{}' not found", file);
				}
			} catch (IOException e) {
				LOGGER.error("Failed to load properties file '{}'", file, e);
			}
		}
	}

	private PropertiesUtil () {
		throw new UnsupportedOperationException("Utility class");
	}

	public static String get (String key) {
		return get(key, null);
	}

	public static String get (String key, String defaultValue) {
		String value = System.getProperty(key);
		if (value != null) {
			return value;
		}
		value = System.getenv(key);
		if (value != null) {
			return value;
		}
		return properties.getProperty(key, defaultValue);
	}

	public static int getInt (String key, int defaultValue) {
		String value = get(key);
		if (value == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			LOGGER.warn("Invalid int for key '{}': '{}'. Using default {}", key, value, defaultValue);
			return defaultValue;
		}
	}

	public static long getLong (String key, long defaultValue) {
		String value = get(key);
		if (value == null) {
			return defaultValue;
		}
		try {
			return Long.parseLong(value.trim());
		} catch (NumberFormatException e) {
			LOGGER.warn("Invalid long for key '{}': '{}'. Using default {}", key, value, defaultValue);
			return defaultValue;
		}
	}

	public static boolean getBoolean (String key, boolean defaultValue) {
		String value = get(key);
		if (value == null) {
			return defaultValue;
		}
		return Boolean.parseBoolean(value.trim());
	}
}
