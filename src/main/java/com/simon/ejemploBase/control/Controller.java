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
	private Queue<Integer> sequence;	// Secuencia de colores

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
		this(configuration, new Model(6));
	}

	public Controller(Configuration configuration, ApplicationWindow view, Model model) {
		this(configuration, model);
		this.view = view;
	}
	public void init() {

	}

	public void startGame() {
		System.out.println("Iniciando nuevo juego..");
		data.startNewGame();
		playNextColorInSequence();
	}

	// Manejar la selección de un color por el jugador
	public void handleColorSelection(int selectedColor) {
		// Verificar si el juego ha terminado
		if (data.isGameOver()) {
			System.out.println("Juego terminado.");
			return;
		}

		// Mostrar el color seleccionado y la secuencia actual
		System.out.printf("Color seleccionado: %d%n", selectedColor);
		System.out.println("Secuencia actual: " + sequence);

		// Obtener el siguiente color en la secuencia
		int nextColor = sequence.remove();

		// Reproducir el sonido correspondiente al color seleccionado
		String soundFilePath = String.format("src/main/resources/sounds/%d.wav", selectedColor);
		view.playSound(soundFilePath);

		// Comprobar si la secuencia todavía tiene elementos
		if (!sequence.isEmpty()) {
			System.out.println("Color siguiente: " + nextColor);
			// Comprobar si el color seleccionado coincide con el siguiente color en la secuencia
			if (nextColor != selectedColor) {
				gameIsOver(); // Llama a la función para terminar el juego
				return;
			}
			// El color seleccionado es correcto
			System.out.println("Color correcto.");
		} else {
			// El jugador ha completado la ronda
			System.out.println("Ronda completada.");
			// Esperar un segundo antes de pasar a la siguiente ronda
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			playNextColorInSequence(); // Llama a la función para continuar con la siguiente secuencia
		}
	}

	private void gameIsOver() {
		// DEBUG
		System.out.println("Juego terminado.");
		System.out.println("Color incorrecto.");
		// Establecer el estado del juego como terminado
		data.setGameOver(true);
		// Guardar el nuevo puntaje
		data.saveScore();
		// Reproducir el sonido de error
		view.playSound("src/main/resources/sounds/error_sound.wav");
		// Mostrar el mensaje de que ha terminado el juego
		view.showMessage("Ha terminado el juego. Su puntaje es: " + data.getCurrentRound());

	}

	public List<Integer> getScores() {
		return data.getScores();
	}


	// Reproducir el siguiente color en la secuencia
	private void playNextColorInSequence() {
		Queue<Integer> sequenceCopy = new LinkedList<>(data.getNextSequence()); // Crea una nueva instancia y copia la secuencia
		if (sequenceCopy == null || sequenceCopy.isEmpty()) {
			System.out.println("La secuencia está vacía..");
			return;
		}
		this.sequence = sequenceCopy;
		int timeBetweenColors = 1000;
		// el tiempo entre colores es de 1 segundo y se va reduciendo en 200 milisegundos por cada 3 rondas
		if (data.getCurrentRound() > 3) {
			timeBetweenColors = 1000 - (data.getCurrentRound() / 3) * 200;
		} else {
			timeBetweenColors = 1000;
		}

		view.highlighSequence(sequence, timeBetweenColors);
	}

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

	public ModelView getModel() {
		// El método regresa una referencia al modelo pero con
		// el tipo de la clase ModelView (ModelView) para limitar
		// los métodos a los que tendrá acceso la vista.
		return getData();
	}

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

	public Configuration getConfiguration() {
		return configuration;
	}

	public Model getData() {
		return data;
	}

	private Configuration configuration;
	private Model data;
	private ApplicationWindow view;

	public void addView(ApplicationWindow app) {
		this.view = app;
		if (app != null) {
			app.init();
		}
	}
}

