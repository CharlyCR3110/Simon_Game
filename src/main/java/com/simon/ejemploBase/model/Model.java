package com.simon.ejemploBase.model;

import com.simon.mvc.ObservableModel;

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
public class Model extends ObservableModel implements ModelView {
	//<editor-fold desc="constructors">

	public Model() {
		System.out.println("Inicializando modelo..");
	}

	//</editor-fold>
	//<editor-fold desc="methods">
	private void updateData(String msg) {
		// El uso de un PropertyChangeListener permite enviar
		// eventos desde el modelo asociados a atributos o
		// métodos específicos.
		// El primer parámetro del método indica cuál es el
		// nombre del atributo que es modificado, junto con el
		// valor original (nulo en este caso) y el valor actual
		// de dicho atributo. Aquí se utiliza el mensaje para
		// notificar cuál es el método que hace la actualización.
		//
		notifyListeners(String.format("%s", msg), this);
	}

	//</editor-fold>
	//<editor-fold desc="attributes">
	// Componentes del modelo.
	//</editor-fold>
}
