package com.simon.ejemploBase.model;

import com.simon.mvc.ObservableModel;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Model extends ObservableModel implements ModelView {
	private Queue<Integer> sequence;    // Secuencia de colores
	private int currentRound; // Número de ronda actual
	private int maxColors; // Número máximo de colores
	private boolean gameOver; // Indica si el juego ha terminado

	public Model() {
		System.out.println("Inicializando modelo..");
	}

	public Model(int maxColors) {
		this.maxColors = maxColors; // Corregir el nombre del parámetro aquí
		this.sequence = new LinkedList<>();
		this.currentRound = 0;
		this.gameOver = false;
	}

	public void startNewGame() {
		if (this.sequence == null) {
			this.sequence = new LinkedList<>();
		}
		this.sequence.clear();
		this.currentRound = 0;
		this.gameOver = false;
		generateNextColor();
		updateData("startNewGame");
		System.out.println("SYSO startNewGame");
	}

	public void generateNextColor() {
		Random random = new Random();
		int nextColor = random.nextInt(maxColors);
		sequence.add(nextColor);
	}

	public Queue<Integer> getNextSequence() {
		nextRound();
		return sequence;
	}
	public Queue<Integer> getSequence() {
		return sequence;
	}

	public int getCurrentRound() {
		return currentRound;
	}

	public int getMaxColors() {
		return maxColors;
	}

	public boolean isGameOver() {
		return gameOver;
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

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public void nextRound() {
		currentRound++;
		generateNextColor();
		updateData("nextRound");
	}
}
