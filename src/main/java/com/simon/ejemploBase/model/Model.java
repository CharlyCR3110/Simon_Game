package com.simon.ejemploBase.model;

import com.simon.mvc.ObservableModel;


public class Model extends ObservableModel implements ModelView {

	public Model() {
		System.out.println("Inicializando modelo..");
	}

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

}
