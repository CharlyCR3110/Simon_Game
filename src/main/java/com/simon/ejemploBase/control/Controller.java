package com.simon.ejemploBase.control;

import com.simon.ejemploBase.configuration.Configuration;
import com.simon.ejemploBase.model.Model;
import com.simon.ejemploBase.model.ModelView;
import java.beans.PropertyChangeListener;

/**
 * -------------------------------------------------------------------
 *
 * (c) 2021-2022
 *
 * @author Georges Alfaro S.
 * @version 2.1.0 2021-09-13
 *
 * --------------------------------------------------------------------
 */
public class Controller {
	//<editor-fold desc="constructors">

	public Controller(Configuration configuration, Model data) {
		System.out.println("Iniciando gestor de la aplicación..");
		this.configuration = configuration;
		this.data = data;
	}

	public Controller(Configuration configuration) {

		// Si la aplicación no obtiene una referencia del modelo
		// externamente (como un recurso de conexión a una base
		// de data, por ejemplo), la clase de control crea la
		// instancia directamente.
		//
		this(configuration, new Model());
	}

	public void init() {
	}

	//</editor-fold>
	//<editor-fold desc="MVC">
	public void register(PropertyChangeListener newObserver) {
		// Asocia el modelo a la clase de control, para poder
		// ejecutar los métodos correspondientes.
		//
		System.out.printf("Registrando: %s..%n", newObserver);
		getData().addPropertyChangeListener(newObserver);
	}

	public void remove(PropertyChangeListener current) {
		System.out.printf("Suprimiendo: %s..%n", current);
		getData().removePropertyChangeListener(current);
	}

	//</editor-fold>
	//<editor-fold desc="methods (functionality)">
	public ModelView getModel() {
		// El método regresa una referencia al modelo pero con
		// el tipo de la clase ModelView (ModelView) para limitar
		// los métodos a los que tendrá acceso la vista.
		return getData();
	}
	//</editor-fold>
	//<editor-fold desc="métodos (control de la interfaz)">

	public void closeApplication() {
		if (getConfiguration().isUpdated()) {
			getConfiguration().saveConfiguration();
		}

		System.out.println("Aplicación finalizada normalmente..");

		// Al cerrar la aplicación, todas las ventanas que son atendidas
		// por el EDT (Event dispatching thread) principal, son cerradas
		// también. No es necesario tener una referencia para cerrarlas
		// de manera explícita.
		//
		System.exit(0);
	}
	//</editor-fold>
	//<editor-fold desc="attributes">

	public Configuration getConfiguration() {
		return configuration;
	}

	public Model getData() {
		return data;
	}

	private Configuration configuration;
	private Model data;
	//</editor-fold>
}

