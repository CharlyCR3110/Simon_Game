package com.simon.ejemploBase.control;

import com.simon.ejemploBase.configuration.Configuration;
import com.simon.ejemploBase.model.Model;
import com.simon.ejemploBase.model.ModelView;
import com.simon.ejemploBase.view.ApplicationWindow;

import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Controller {
	private Queue<Integer> sequence;    // Secuencia de colores
	private Configuration configuration;
	private Model data;
	private ApplicationWindow view;

	public Controller(Configuration configuration) {

		// Si la aplicación no obtiene una referencia del modelo
		// externamente (como un recurso de conexión a una base
		// de data, por ejemplo), la clase de control crea la
		// instancia directamente.
		//
		this(configuration, new Model(6));
	}

	public Controller(Configuration configuration, Model data) {
		this.configuration = configuration;
		this.data = data;
		this.view = view;
		System.out.println("Iniciando gestor de la aplicación..");
	}
	public Controller(Configuration configuration, ApplicationWindow view, Model model) {
		this(configuration, model);
		this.view = view;
	}

	public void init() {
		// Inicialización de la aplicación
	}

	public void startGame() {
		System.out.println("Iniciando nuevo juego..");
		data.startNewGame();
		playNextColorInSequence();
	}

	public void handleColorSelection(int selectedColor) {
		/**
		 * Se comprueba que el color seleccionado esté dentro del rango de colores ya que
		 * si se hace click en cualquier otra parte de la ventana, se envía un -1 como
		 * color seleccionado. Y esto no contaria como error.
		 **/
		if (selectedColor < 0 || selectedColor > data.getNumOfColors()) {
			System.out.println("Color inválido.");
			return;
		}

		if (data.isGameOver()) {
			System.out.println("Juego terminado.");
			return;
		}

		System.out.printf("Color seleccionado: %d%n", selectedColor);
		System.out.println("Secuencia actual: " + sequence);

		int nextColor = sequence.poll();

		if (selectedColor != nextColor) {
			System.out.println("Color incorrecto.");
			gameIsOver();
			return;
		}

		String soundFilePath = String.format("src/main/resources/sounds/%d.wav", selectedColor);
		view.playSound(soundFilePath);
		view.highlightSpecificColor(selectedColor);

		if (sequence.isEmpty()) {
			System.out.println("Ronda completada.");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			playNextColorInSequence();
		} else {
			System.out.println("Color correcto.");
		}
	}

	private void gameIsOver() {
		System.out.println("Juego terminado.");
		System.out.println("Color incorrecto.");
		data.setGameOver(true);
		data.saveScore();
		view.playSound("src/main/resources/sounds/error_sound.wav");
		view.showMessage("Ha terminado el juego. Su puntaje es: " + data.getCurrentRound());
	}

	public List<Integer> getScores() {
		return data.getScores();
	}

	private void playNextColorInSequence() {
		Queue<Integer> sequenceCopy = new LinkedList<>(data.getNextSequence());
		if (sequenceCopy == null || sequenceCopy.isEmpty()) {
			System.out.println("La secuencia está vacía..");
			return;
		}
		this.sequence = sequenceCopy;
		int timeBetweenColors = 1000;
		if (data.getCurrentRound() > 3) {
			timeBetweenColors = 1000 - (data.getCurrentRound() / 3) * 200;
		} else {
			timeBetweenColors = 1000;
		}
		view.highlighSequence(sequence, timeBetweenColors);
	}

	public void register(PropertyChangeListener newObserver) {
		System.out.printf("Registrando: %s..%n", newObserver);
		getData().addPropertyChangeListener(newObserver);
	}

	public void remove(PropertyChangeListener current) {
		System.out.printf("Suprimiendo: %s..%n", current);
		getData().removePropertyChangeListener(current);
	}

	public ModelView getModel() {
		return getData();
	}

	public void closeApplication() {
		if (getConfiguration().isUpdated()) {
			getConfiguration().saveConfiguration();
		}

		System.out.println("Aplicación finalizada normalmente..");
		System.exit(0);
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public Model getData() {
		return data;
	}

	public void addView(ApplicationWindow app) {
		this.view = app;
		if (app != null) {
			app.init();
		}
	}
}