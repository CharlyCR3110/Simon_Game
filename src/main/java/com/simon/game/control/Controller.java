package com.simon.game.control;

import com.simon.game.configuration.Configuration;
import com.simon.game.configuration.GameConfig;
import com.simon.game.model.Model;
import com.simon.game.model.ModelView;
import com.simon.game.view.ApplicationWindow;

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
		String[] colorNames = {"Rojo", "Verde", "Azul", "Amarillo", "Cyan", "Magenta"};

		if (selectedColorIndex >= 0 && selectedColorIndex < colorNames.length) {
			return colorNames[selectedColorIndex];
		} else {
			return "Color desconocido";
		}
	}


	private void gameIsOver(String motivo) {
		data.setGameOver(true);
		data.saveScore();

		view.playSound("src/main/resources/sounds/error_sound.wav");

		String message = String.format(
				"¡Oh no! Parece que has perdido.%nMotivo: %s%nTu puntuación final es de: %d. ¡Anímate y vuelve a intentarlo!",
				motivo, data.getCurrentRound()
		);

		view.showMessage(message);
	}


	public List<Integer> getScores() {
		return data.getScores();
	}

	public List<Integer> getScoresSorterMaxToMin() {
		return data.getScoresSorterMaxToMin();
	}

	private void playNextColorInSequence() {
		Queue<Integer> sequenceCopy = new LinkedList<>(data.getNextSequence());

		if (sequenceCopy == null || sequenceCopy.isEmpty()) {
			System.out.println("La secuencia está vacía..");
			return;
		}

		this.sequence = sequenceCopy;
		int currentRound = data.getCurrentRound();
		int timeBetweenColors = calculateTimeBetweenColors(currentRound);
		System.out.printf("Tiempo entre colores: %d%n", timeBetweenColors);
		view.highlighSequence(sequenceCopy, timeBetweenColors);

		startSequenceDisplayTimer(sequenceCopy.size(), timeBetweenColors);
		startUserResponseTimer(gameConfig.getMaxUserResponseTime());
	}

	private int calculateTimeBetweenColors(int currentRound) {
		int initialTimeBetweenColors = gameConfig.getMaxDisplayTime();
		int reductionPerRound = 200;
		int timeBetweenColors = initialTimeBetweenColors - ((currentRound - 1) / 3) * reductionPerRound;
		return Math.max(timeBetweenColors, gameConfig.getMinDisplayTime());
	}

	private void startSequenceDisplayTimer(int sequenceSize, int timeBetweenColors) {
		Timer sequenceDisplayTimer = new Timer(timeBetweenColors * sequenceSize, e -> startUserResponseTimer(gameConfig.getMaxUserResponseTime()));
		sequenceDisplayTimer.setRepeats(false);
		sequenceDisplayTimer.start();
	}

	private void startUserResponseTimer(int maxUserResponseTime) {
		if (userMoveTimer != null && userMoveTimer.isRunning()) {
			userMoveTimer.stop();
		}

		userMoveTimer = new Timer(maxUserResponseTime, e -> handleUserResponseTimeout());
		userMoveTimer.setRepeats(false);
		userMoveTimer.start();
	}

	private void handleUserResponseTimeout() {
		if (userMoveTimer != null && userMoveTimer.isRunning()) {
			userMoveTimer.stop();
		}

		gameIsOver("Se acabó el tiempo de respuesta.");
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