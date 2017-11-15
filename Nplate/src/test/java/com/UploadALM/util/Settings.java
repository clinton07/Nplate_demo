package com.UploadALM.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Settings {

	/*
	 * Read the properties and Store it in properties variable.
	 * 
	 */
	private static Properties properties = loadFromPropertiesFile();

	/**
	 * Function to return the singleton instance of the {@link Properties}
	 * object
	 * 
	 * @return Instance of the {@link Properties} object
	 */
	public static Properties getInstance() {
		return properties;
	}

	private static Properties loadFromPropertiesFile() {
		Util.setRelativePath(new File(System.getProperty("user.dir")).getAbsolutePath());
		if (Util.getRelativePath() == null) {
		}

		Properties properties = new Properties();

		try {
			properties.load(new FileInputStream(
					Util.getRelativePath() + Util.getFileSeparator() + "resources" + Util.getFileSeparator() + "config.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

		}
		return properties;
	}

}
