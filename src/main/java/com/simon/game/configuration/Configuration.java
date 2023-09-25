package com.simon.game.configuration;

import com.simon.util.PathUtils;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Georges Alfaro S.
 * @version 1.2.0 2023-09-04
 */
public class Configuration extends Properties {

	private Configuration() {
		updated = false;
	}

	public static Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration();
			try {
				System.out.println("Cargando configuración por defecto..");
				instance.loadFromXML(Configuration.class.getResourceAsStream("/com/simon/game/configuration/config.properties"));

				configurationPath = PathUtils.getUserPath(instance.getProperty("configuration_file"));
				System.out.printf("Cargando configuración del usuario: '%s'..%n", configurationPath);
				instance.loadFromXML(new FileInputStream(configurationPath));

			} catch (IOException ex) {
				System.err.printf("No se pudo cargar el archivo de configuración: '%s'..%n",
						configurationPath);
				System.err.println(ex.getMessage());
				instance.setUpdated(true);
			}
		}
		return instance;
	}

	public void saveConfiguration() {
		if (isUpdated() && (configurationPath != null)) {
			try {
				System.out.println("Actualizando archivo de configuración..");
				storeToXML(new FileOutputStream(configurationPath), getClass().getCanonicalName());
				setUpdated(false);
			} catch (IOException ex) {
				System.err.printf("No se pudo actualizar el archivo de configuración: '%s'..%n",
						CONFIGURATION_FILE);
				System.err.println(ex.getMessage());
			}
		} else {
			System.err.printf("No está definida la ruta del archivo de configuración..");
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

