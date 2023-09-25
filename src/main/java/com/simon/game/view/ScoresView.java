package com.simon.game.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ScoresView {
	private JDialog scoresDialog;
	private JTable scoresTable;

	public ScoresView(List<Integer> scoreList) {
		// Crear un JDialog para mostrar la tabla de puntajes
		scoresDialog = new JDialog();
		scoresDialog.setTitle("Puntajes");
		scoresDialog.setSize(400, 300);
		scoresDialog.setLocationRelativeTo(null);

		// Crear el modelo de tabla
		DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.addColumn("Nombre del Jugador");
		tableModel.addColumn("Puntaje");

		if (scoreList == null || scoreList.isEmpty()) {
			// Si no hay puntajes, agregar una fila con un mensaje
			tableModel.addRow(new Object[]{"No hay puntajes", ""});
		} else {
			for (Integer score : scoreList) {
				tableModel.addRow(new Object[]{"Jugador", score});
			}
		}

		// Agregar los datos de puntajes a la tabla


		// Crear la tabla con el modelo de datos
		scoresTable = new JTable(tableModel);

		// Agregar la tabla a un JScrollPane para permitir el desplazamiento si hay muchos puntajes
		JScrollPane scrollPane = new JScrollPane(scoresTable);

		// Agregar el JScrollPane al diálogo
		scoresDialog.add(scrollPane);

		// Hacer visible el diálogo de puntajes
		scoresDialog.setVisible(true);
	}

	// Método para cerrar la ventana de puntajes
	public void closeScoresDialog() {
		scoresDialog.dispose();
	}
}
