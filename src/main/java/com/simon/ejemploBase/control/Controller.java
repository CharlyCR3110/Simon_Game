package com.simon.ejemploBase.control;

import com.simon.ejemploBase.configuration.Configuration;
import com.simon.ejemploBase.configuration.GameConfig;
import com.simon.ejemploBase.model.Model;
import com.simon.ejemploBase.model.ModelView;
import com.simon.ejemploBase.view.ApplicationWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.swing.Timer;

public class Controller {
	private Queue<Integer> sequence;    // Secuencia de colores
	private Configuration configuration;
	private Model data;
	private ApplicationWindow view;
	private GameConfig gameConfig;

	public Controller(Configuration configuration, GameConfig gameConfig) {
		this.configuration = configuration;
		this.data = new Model(gameConfig.getColorsToShow());
		this.gameConfig = gameConfig;
		System.out.println("Iniciando gestor de la aplicación..");
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

		view.highlightSpecificColor(selectedColor);	// resalta y reproduce el sonido del color seleccionado

		if (selectedColor != nextColor) {
			System.out.println("Color incorrecto.");
			gameIsOver();
			return;
		}

		if (sequence.isEmpty()) {
			System.out.println("Ronda completada.");
			// Se crea un temporizador para que el usuario pueda ver el último color de la secuencia
			// antes de que se empiece la siguiente ronda
			Timer timer = new Timer(1000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// Este código se ejecutará después de la pausa de 1 segundo
					playNextColorInSequence();
				}
			});
			// Inicia el temporizador
			timer.setRepeats(false); // Esto asegura que el temporizador solo se ejecute una vez
			timer.start();
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

		// Calcular el tiempo entre colores
		int currentRound = data.getCurrentRound();
		int initialTimeBetweenColors = 1000;
		int reductionPerRound = 200;
		int timeBetweenColors = initialTimeBetweenColors - ((currentRound - 1) / 3) * reductionPerRound;
		timeBetweenColors = Math.max(timeBetweenColors, reductionPerRound); // Asegurar que no sea menor que reductionPerRound
		System.out.printf("Tiempo entre colores: %d%n", timeBetweenColors);
		view.highlighSequence(sequenceCopy, timeBetweenColors);
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
	public GameConfig getGameConfig() {
		return gameConfig;
	}

	public void addView(ApplicationWindow app) {
		this.view = app;
		if (app != null) {
			app.init();
		}
	}
}