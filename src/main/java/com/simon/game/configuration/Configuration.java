package com.simon.game.configuration;

import com.simon.util.PathUtils;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration extends Properties {

	private Configuration() {
		updated = false;
	}

	public static Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration();
			try {
				// Cargando configuración por defecto
				instance.loadFromXML(Configuration.class.getResourceAsStream("/com/simon/game/configuration/config.properties"));

				configurationPath = PathUtils.getUserPath(instance.getProperty("configuration_file"));
				// Cargando configuración personalizada
				instance.loadFromXML(new FileInputStream(configurationPath));

			} catch (IOException ex) {
				// No se pudo cargar el archivo de configuración
				ex.printStackTrace();
				instance.setUpdated(true);
			}
		}
		return instance;
	}

	public void saveConfiguration() {
		if (isUpdated() && (configurationPath != null)) {
			try {
				// Actualizando archivo de configuración
				storeToXML(new FileOutputStream(configurationPath), getClass().getCanonicalName());
				setUpdated(false);
			} catch (IOException ex) {
				// No se pudo guardar el archivo de configuración
				ex.printStackTrace();
			}
		}
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	private static final String CONFIGURATION_FILE = "com/simon/game/configuration/config.properties";
	private static Configuration instance = null;
	private static String configurationPath = null;
	private boolean updated;
}

