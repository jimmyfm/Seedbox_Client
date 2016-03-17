package org.github.jimmyfm.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {

	private final Properties props = new Properties();
	private final File PROP_FILE = new File("test.properties");
	private static final Settings instance = new Settings();

	public static final String USERNAME = "USERNAME";
	public static final String PASSWORD = "PASSWORD";
	public static final String PICKUP_FOLDER = "PICKUP_FOLDER";
	public static final String DROPDOWN_FOLDER = "DROPDOWN_FOLDER";

	private Settings() {
		String FILE_SEPARATOR = System.getProperty("file.separator");
		String USER_HOME = System.getProperty("user.home");

		if (PROP_FILE.exists()) {
			try {
				props.load(new FileInputStream(PROP_FILE));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		boolean changed = false;

		if (!props.containsKey(PICKUP_FOLDER)) {
			changed = true;
			props.setProperty(PICKUP_FOLDER, USER_HOME + FILE_SEPARATOR + "Downloads");
		}

		if (!props.containsKey(DROPDOWN_FOLDER)) {
			changed = true;
			props.setProperty(DROPDOWN_FOLDER, USER_HOME + FILE_SEPARATOR + "Downloads");
		}

		if (!props.containsKey(USERNAME)) {
			changed = true;
			props.setProperty(USERNAME, USERNAME);
		}

		if (!props.containsKey(PASSWORD)) {
			changed = true;
			props.setProperty(PASSWORD, PASSWORD);
		}

		if (changed) {
			try {
				props.store(new FileOutputStream(PROP_FILE), null);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static Settings getInstance() {
		return Settings.instance;
	}

	public String getUsername() {
		return props.getProperty(USERNAME);
	}

	public String getPassword() {
		return props.getProperty(PASSWORD);
	}

	public String getPickupFolder() {
		return props.getProperty(PICKUP_FOLDER);
	}

	public String getDropdownFolder() {
		return props.getProperty(DROPDOWN_FOLDER);
	}

}
