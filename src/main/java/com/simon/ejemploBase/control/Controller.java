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
	private Timer userMoveTimer;

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
		userMoveTimer = null;
		data.startNewGame();
		playNextColorInSequence();
	}

	public void handleColorSelection(int selectedColor) {
		// se reinicia el temporizador de respuesta del usuario
		if (userMoveTimer != null && userMoveTimer.isRunning()) {
			userMoveTimer.stop();
		}

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
			gameIsOver("Seleccionaste un color incorrecto, el cual debio ser: " + nameOfSelectedColor(nextColor));
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

	private String nameOfSelectedColor(int selectedColorIndex) {
		switch (selectedColorIndex) {
			case 0:
				return "Rojo";
			case 1:
				return "Verde";
			case 2:
				return "Azul";
			case 3:
				return "Amarillo";
			case 4:
				return "Cyan";
			case 5:
				return "Magenta";
			default:
				return "Color desconocido";
		}
	}

	private void gameIsOver(String motivo) {
		System.out.println("Juego terminado.");
		System.out.println("Color incorrecto.");
		data.setGameOver(true);
		data.saveScore();
		view.playSound("src/main/resources/sounds/error_sound.wav");

		StringBuilder sb = new StringBuilder();
		sb.append("¡Oh no! Parece que has perdido.");
		sb.append(System.lineSeparator());
		sb.append("Motivo:").append(motivo);
		sb.append(System.lineSeparator());
		sb.append("Tu puntuación final es de: ");
		sb.append(data.getCurrentRound());
		sb.append(". ¡Anímate y vuelve a intentarlo!");

		view.showMessage(sb.toString());
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
		int initialTimeBetweenColors = gameConfig.getMaxDisplayTime();	// Tiempo inicial entre colores
		int reductionPerRound = 200;
		int timeBetweenColors = initialTimeBetweenColors - ((currentRound - 1) / 3) * reductionPerRound;
		timeBetweenColors = Math.max(timeBetweenColors, gameConfig.getMinDisplayTime()); // Asegurar que no sea menor que el tiempo mínimo
		System.out.printf("Tiempo entre colores: %d%n", timeBetweenColors);
		view.highlighSequence(sequenceCopy, timeBetweenColors);

		// Para evitar que el tiempo corra mientras se muestra la secuencia se hace lo siguiente
		Timer timer = new Timer(timeBetweenColors * sequenceCopy.size(), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Este código se ejecutará después de la pausa de 1 segundo
				startUserResponseTimer();
			}
		});
		// Inicia el temporizador
		timer.setRepeats(false); // Esto asegura que el temporizador solo se ejecute una vez
		timer.start();

		// Iniciar el temporizador para que el usuario pueda clickear los colores de la secuencia
		int maxUserResponseTime = gameConfig.getMaxUserResponseTime();
		userMoveTimer = new Timer(maxUserResponseTime, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// cuando el temporizador se acabe, se ejecutará este código
				handleUserResponseTimeout();
			}
		});
		userMoveTimer.setRepeats(false); // Esto asegura que el temporizador solo se ejecute una vez
		userMoveTimer.start();
	}

	private void handleUserResponseTimeout() {
		// se detiene el temporizador (en caso de que no se haya detenido ya)
		if (userMoveTimer != null && userMoveTimer.isRunning()) {
			userMoveTimer.stop();
		}

		// gg's
		gameIsOver("Se acabó el tiempo de respuesta.");
	}

	private void startUserResponseTimer() {
		// Se crea un temporizador para que el usuario pueda ver el último color de la secuencia
		if (userMoveTimer != null && userMoveTimer.isRunning()) {
			userMoveTimer.stop();
		}

		userMoveTimer.start();
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