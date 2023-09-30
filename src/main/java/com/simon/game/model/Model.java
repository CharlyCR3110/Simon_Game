package com.simon.game.model;

import com.simon.mvc.ObservableModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Model extends ObservableModel implements ModelView {
	private Queue<Integer> sequence;    // Secuencia de colores
	private int currentRound; // Número de ronda actual
	private int numOfColors; // Número de colores (wedges) en el juego
	private boolean gameOver; // Indica si el juego ha terminado
	private List<Integer> scores; // Lista de puntajes
	private Random random; // Generador de números aleatorios

	public Model() {
		initModel(4); // Valor predeterminado de colores
	}

	public Model(int maxColors) {
		initModel(maxColors);
	}

	private void initModel(int maxColors) {
		this.numOfColors = maxColors;
		this.sequence = new LinkedList<>();
		this.currentRound = 0;
		this.gameOver = false;
		this.random = new Random();
	}

	public void startNewGame() {
		sequence.clear();
		currentRound = 0;
		gameOver = false;
		generateNextColor();
		updateData("startNewGame");
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

	public int getNumOfColors() {
		return numOfColors;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	private void updateData(String msg) {
		notifyListeners(msg, this);
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public void nextRound() {
		currentRound++;
		generateNextColor();
		updateData("nextRound");
	}

	public void saveScore() {
		if (scores == null) {
			scores = new ArrayList<>();
		}
		scores.add(currentRound);
	}

	public List<Integer> getScores() {
		if (scores == null) {
			scores = new ArrayList<>();
		}
		return scores;
	}

	public List<Integer> getScoresSorterMaxToMin() {
		if (scores == null) {
			scores = new ArrayList<>();
		}
		List<Integer> scoresCopy = new ArrayList<>(scores);
		scoresCopy.sort((o1, o2) -> o2 - o1);    // Ordenar de mayor a menor
		return scoresCopy;
	}

	private void generateNextColor() {
		int nextColor = random.nextInt(numOfColors);
		sequence.add(nextColor);
	}
}
