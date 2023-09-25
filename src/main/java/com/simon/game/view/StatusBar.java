package com.simon.game.view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

/**
 * -------------------------------------------------------------------
 *
 * (c) 2021-2023
 *
 * @author Georges Alfaro S.
 * @version 2.2.1 2023-05-02
 * @since 1.0.1
 *
 * --------------------------------------------------------------------
 */
public class StatusBar extends JPanel {

	public StatusBar() {
		super();

		this.messageFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
		this.statusLabel = null;
		this.msgTimer = new Timer(MAX_MSG_TIME, e -> {
			clearMessage();
		});
		this.timed = false;

		setup();
	}

	private void setup() {
		setLayout(new FlowLayout(FlowLayout.RIGHT));
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		add(statusLabel = new JLabel());
		statusLabel.setFont(messageFont);
	}

	public void init() {
		clearMessage();

		msgTimer.setRepeats(false);
		msgTimer.start();
	}

	public void setMessageFont(Font messageFont) {
		this.messageFont = messageFont;
	}

	public void clearMessage() {
		showMessage(null, NORMAL_MESSAGE_COLOR);
	}

	public void showMessage(String msg) {
		showMessage(msg, NORMAL_MESSAGE_COLOR);
	}

	public void showError(String msg) {
		showMessage(msg, ERROR_MESSAGE_COLOR);
	}

	private void showMessage(String msg, Color color) {
		if (msg == null) {
			msg = "";
		}
		statusLabel.setForeground(color);
		statusLabel.setText(String.format(" %s", msg));
		if (isTimed()) {
			msgTimer.restart();
		}
	}

	public boolean isTimed() {
		return timed;
	}

	public void setTimed(boolean timed) {
		this.timed = timed;
	}

	public void setMaxTime(int maxTime) {
		msgTimer.setDelay(maxTime);
		msgTimer.setInitialDelay(maxTime);
	}

	private static final Color NORMAL_MESSAGE_COLOR = Color.BLACK;
	private static final Color ERROR_MESSAGE_COLOR = new Color(220, 0, 0);
	private static final int MAX_MSG_TIME = 3_000;

	private Font messageFont;
	private JLabel statusLabel;
	private final Timer msgTimer;
	private boolean timed;
}
