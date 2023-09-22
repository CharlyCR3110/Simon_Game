package com.simon.ejemploBase.view;

import com.simon.ejemploBase.configuration.Configuration;
import com.simon.ejemploBase.control.Controller;
import com.simon.ejemploBase.model.ModelView;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Queue;

public class ApplicationWindow extends JFrame implements PropertyChangeListener {

	private Controller mainControl;
	private int numSectors = 6;	// puede ser 4, 5 o 6
	private int highlightedColorIndex = -1; // Inicialmente, no hay color resaltado

	private static final int MAX_MSG_TIME = 5_000;
	private JMenuBar mainMenu;
	private JMenu fileMenu;
	private JMenuItem quitItem;
	private StatusBar status;

	public ApplicationWindow(String title, Controller mainControl) {
		super(title);
		this.mainControl = mainControl;
		setup();
	}

	private void setup() {
		setupComponents(getContentPane());

		setResizable(true);
		setSize(640, 480);
		setMinimumSize(new Dimension(480, 360));
		setLocationRelativeTo(null);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateWindowConfiguration();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				updateWindowConfiguration();
			}
		});

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeWindow();
			}
		});
	}

	private void setupComponents(Container c) {
		c.setLayout(new BorderLayout());
		setupMenus();

		JPanel mainPanel = createMainPanel();
		mainPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Robot robot = new Robot();
					Color color = robot.getPixelColor(e.getXOnScreen(), e.getYOnScreen());
					System.out.println("Color: " + color);
					System.out.println("Índice: " + getSelectedColorIndex(color));
					mainControl.handleColorSelection(getSelectedColorIndex(color));
//					highlightedColorIndex = getSelectedColorIndex(color);
//					mainPanel.repaint();
				} catch (AWTException ex) {
					ex.printStackTrace();
				}
			}
		});
		c.add(BorderLayout.CENTER, mainPanel);	// Agrega el panel
		c.add(BorderLayout.PAGE_END, status = new StatusBar());	// Agrega la barra de estado
	}

	private JPanel createMainPanel() {
		JPanel panel = new JPanel() {
			@Override
			public void paintComponent(Graphics bg) {
				super.paintComponent(bg);
				Graphics2D g = (Graphics2D) bg;
				setupGraphics(g);
				drawGrid(g);
				drawSectors(g);
				drawCentralCircle(g);
				drawHighlightedColor(g);
			}

			private void setupGraphics(Graphics2D g) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}

			private void drawGrid(Graphics2D g) {
				int cx = getWidth() / 2;
				int cy = getHeight() / 2;
				g.setColor(Color.CYAN.darker());
				g.drawLine(cx, 0, cx, getHeight());
				g.drawLine(0, cy, getWidth(), cy);
			}

			private void drawSectors(Graphics2D g) {
				int cx = getWidth() / 2;
				int cy = getHeight() / 2;
				int s = (int) (0.80 * Math.min(getWidth(), getHeight()));
				int n = numSectors;

				for (int i = 0; i < n; i++) {
					int startAngle = (i * 360) / n;
					int sweepAngle = (360 / n);
					drawWedge(g, cx, cy, s, startAngle, sweepAngle, COLORS[i]);
				}
			}

			private void drawCentralCircle(Graphics2D g) {
				int cx = getWidth() / 2;
				int cy = getHeight() / 2;
				int s = (int) (0.80 * Math.min(getWidth(), getHeight()));
				int centralCircleRadius = s / 6;
				g.setColor(Color.DARK_GRAY);
				g.fillOval(cx - centralCircleRadius, cy - centralCircleRadius, centralCircleRadius * 2, centralCircleRadius * 2);
			}

			private void drawHighlightedColor(Graphics2D g) {
				if (highlightedColorIndex >= 0 && highlightedColorIndex < COLORS.length) {
					int cx = getWidth() / 2;
					int cy = getHeight() / 2;
					int s = (int) (0.80 * Math.min(getWidth(), getHeight()));
					int n = numSectors;

					int startAngle = (highlightedColorIndex * 360) / n;
					int sweepAngle = (360 / n);
					Color highlightedColor = COLORS[highlightedColorIndex];

					// Dibuja el color resaltado con un borde especial
					g.setColor(highlightedColor.darker()); // Cambia el color del borde
					g.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL)); // Grosor del borde
					g.drawArc(cx - s / 2, cy - s / 2, s, s, startAngle, sweepAngle);

					// Dibuja el color resaltado con un fondo especial
					g.setColor(new Color(255, 255, 255, 128)); // Color de fondo semitransparente
					g.fillArc(cx - s / 2, cy - s / 2, s, s, startAngle, sweepAngle);
				}
			}
		};

		return panel;
	}


	private void drawWedge(Graphics2D g, int cx, int cy, int s, int start, int end, Color c) {
		double r = Math.PI / 180.0;	// Conversión de grados a radianes
		int x0 = (int) (cx + s * 0.5 * Math.cos(-start * r));	// Puntos de inicio y fin del sector
		int y0 = (int) (cy + s * 0.5 * Math.sin(-start * r));	// Puntos de inicio y fin del sector
		int x1 = (int) (cx + s * 0.5 * Math.cos(-(start + end) * r));	// Puntos de inicio y fin del sector
		int y1 = (int) (cy + s * 0.5 * Math.sin(-(start + end) * r));	// Puntos de inicio y fin del sector
		g.setColor(c);	// Color del sector
		g.fillArc(cx - s / 2, cy - s / 2, s, s, start, end);	// Dibuja el sector
		g.setColor(Color.DARK_GRAY);	// Color de las líneas
		g.setStroke(new BasicStroke(16f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));	// Grosor de las líneas
		g.drawLine(cx, cy, x0, y0);	// Dibuja las líneas
		g.drawLine(cx, cy, x1, y1);	// Dibuja las líneas
		g.drawArc(cx - s / 2, cy - s / 2, s, s, start, end);	// Dibuja el sector
	}

	private static final Color[] COLORS = {
			new Color(255, 0, 0),	// Rojo
			new Color(0, 255, 0),	// Verde
			new Color(0, 0, 255),	// Azul
			new Color(255, 255, 0),	// Amarillo
			new Color(0, 255, 255),	// Cyan
			new Color(255, 0, 255)	// Magenta?
	};

	public int getSelectedColorIndex (Color clickedColor) {
		int index = -1;
		for (int i = 0; i < COLORS.length; i++) {
			if (COLORS[i].equals(clickedColor)) {
				index = i;
				break;
			}
		}
		return index;
	}

	public void playSound(String soundFileName) {
		try {
			// Obtener el archivo de sonido desde una ruta absoluta
			File soundFile = new File(soundFileName);

			if (!soundFile.exists()) {
				throw new IllegalArgumentException("Archivo de sonido no encontrado: " + soundFileName);
			}

			// Cargar el archivo de sonido
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);

			// Obtener el clip de audio
			Clip clip = AudioSystem.getClip();

			// Abre el clip con el audio cargado
			clip.open(audioInputStream);

			// Reproduce el sonido
			clip.start();
		} catch (Exception e) {
			System.err.println("Error al reproducir el sonido: " + soundFileName);
			e.printStackTrace();
		}
	}
	
	// El model le pasa la secuencia al controller y el controller se la pasa a este método
	public void highlighSequence(Queue<Integer> sequence, int timeBetweenColors) {
		SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
			@Override
			protected Void doInBackground() throws Exception {
				int pauseTime = timeBetweenColors; // Tiempo de pausa entre colores
				for (Integer colorIndex : sequence) {
					publish(colorIndex); // Publica el color actual para actualizar la interfaz de usuario
					playSound("src/main/resources/sounds/" + colorIndex +".wav"); // Reproduce el sonido
					Thread.sleep(pauseTime); // Agrega una pausa de 1 segundo entre colores
				}
				return null;
			}

			@Override
			protected void process(java.util.List<Integer> chunks) {
				// Este método se ejecuta en el hilo de la interfaz de usuario
				for (Integer colorIndex : chunks) {
					highlightedColorIndex = colorIndex;
					repaint();
				}
			}

			@Override
			protected void done() {
				// La secuencia ha terminado
				highlightedColorIndex = -1;
				repaint();
			}
		};

		worker.execute();
	}

	private void setupMenus() {
		mainMenu = new JMenuBar();
		mainMenu.add(fileMenu = new JMenu("Archivo"));

		// JMenu para reiniciar el juego
		JMenu gameMenu = new JMenu("Juego");
		mainMenu.add(gameMenu);

		// Agregar el elemento "Reiniciar" al menú "Juego"
		gameMenu.add(new JMenuItem("Reiniciar"));

		// Agregar el elemento "Puntajes" al menú "Juego"
		gameMenu.add(new JMenuItem("Puntajes"));

		// Agregar el elemento "Salir" al menú "Archivo"
		fileMenu.add(quitItem = new JMenuItem("Salir"));

		setJMenuBar(mainMenu);

		quitItem.addActionListener(e -> closeWindow());

		// Agregar un ActionListener para el elemento "Reiniciar"
		gameMenu.getItem(0).addActionListener(e -> restartGame());

		// Agregar un ActionListener para el elemento "Puntajes"
		gameMenu.getItem(1).addActionListener(e -> showScores());
	}

	private void showScores() {
		// crea la ventana de puntajes ScoresView
		ScoresView scoresView = new ScoresView(mainControl.getScores());
	}

	// Método para reiniciar el juego
	private void restartGame() {
//		mainControl.restartGame(); // Llama al controlador para reiniciar el juego
		System.out.println("Reiniciando el juego..");
		mainControl.startGame();
	}


	public void init() {
		mainControl.register(this);
		getWindowConfiguration();
		status.init();
		setVisible(true);
		status.showMessage(String.format("Interfaz inicializada (%d, %d)..", getWidth(), getHeight()));
		System.out.println();
		status.setTimed(true);
		status.setMaxTime(MAX_MSG_TIME);
		mainControl.startGame();
	}

	public boolean confirmClose() {
		Object[] options = {"Sí", "No"};
		return JOptionPane.showOptionDialog(this, "¿Desea cerrar la aplicación?", "Confirmación",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
				options, options[0]) == JOptionPane.OK_OPTION;
	}

	public void closeWindow() {
		if (confirmClose()) {
			System.out.println("Cerrando la aplicación..");
			mainControl.remove(this);
			mainControl.closeApplication();
		}
	}

	public void setNumSectors(int numSectors) {
		this.numSectors = numSectors;
	}

	public int getNumSectors() {
		return numSectors;
	}
	@Override
	public String toString() {
		return String.format("VentanaAplicacion('%s')", getTitle());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		status.showMessage(
				String.format("Evento recibido: %s = %s",
						evt.getPropertyName(), evt.getNewValue())
		);

		ModelView model = mainControl.getModel();
		// Actualizar la interfaz si es necesario
	}

	private void getWindowConfiguration() {
		Configuration cfg = mainControl.getConfiguration();
		try {
			int w = Integer.parseInt(cfg.getProperty("window_width"));
			int h = Integer.parseInt(cfg.getProperty("window_height"));
			setSize(w, h);

			int x = Integer.parseInt(cfg.getProperty("window_x"));
			int y = Integer.parseInt(cfg.getProperty("window_y"));
			setLocation(new Point(x, y));
		} catch (NumberFormatException ex) {
			System.err.printf("Excepción: '%s'%n", ex.getMessage());
		}
	}

	private void updateWindowConfiguration() {
		Configuration cfg = mainControl.getConfiguration();
		cfg.setProperty("window_width", String.valueOf(getWidth()));
		cfg.setProperty("window_height", String.valueOf(getHeight()));
		cfg.setProperty("window_x", String.valueOf(getLocation().x));
		cfg.setProperty("window_y", String.valueOf(getLocation().y));
		cfg.setUpdated(true);
	}
	public void showMessage(String message) {
		JOptionPane.showMessageDialog(this, message);
	}
}
