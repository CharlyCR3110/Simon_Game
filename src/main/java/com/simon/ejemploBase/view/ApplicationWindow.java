package com.simon.ejemploBase.view;

import com.simon.ejemploBase.configuration.Configuration;
import com.simon.ejemploBase.control.Controller;
import com.simon.ejemploBase.model.ModelView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ApplicationWindow extends JFrame implements PropertyChangeListener {

	private Controller mainControl;
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
		c.add(BorderLayout.CENTER, mainPanel);	// Agrega el panel
		c.add(BorderLayout.PAGE_END, status = new StatusBar());	// Agrega la barra de estado
	}

	private JPanel createMainPanel() {
		JPanel panel =  new JPanel() {
			@Override
			public void paintComponent(Graphics bg) {
				super.paintComponent(bg);	// Limpia el fondo
				Graphics2D g = (Graphics2D) bg;	// Conversión de tipo
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);	// Anti-aliasing
				int cx = getWidth() / 2;	// Centro del panel (Eje X)
				int cy = getHeight() / 2;	// Centro del panel (Eje Y)
				g.setColor(Color.CYAN.darker());	// Color de fondo
				g.drawLine(cx, 0, cx, getHeight());	// Línea vertical
				g.drawLine(0, cy, getWidth(), cy);	// Línea horizontal
				int s = (int) (0.80 * Math.min(getWidth(), getHeight()));	// Tamaño del círculo
				int n = 4;	// Número de sectores (wedges)
				// Dibuja los sectores
				for (int i = 0; i < n; i++) {
					/**
					 *
					 *  Los colores se definen en el arreglo COLORS y se agregan al circulo de derecha a izquierda, es decir,
					 *  en un circulo con 4 sectores, el primer color del arreglo se agrega al sector de la derecha,
					 *  el segudno al sector de arriba, el tercero al sector de la izquierda y el cuarto al sector de abajo
					 *
					 * @param g - Graphics2D, el objeto que dibuja
					 * @param cx - int, centro del panel en el eje X
					 * @param cy - int, centro del panel en el eje Y
					 * @param s - int, tamaño del circulo
					 * @param start - int, inicio del sector
					 * @param end - int, fin del sector
					 * @ param c - Color, color del sector
					 *
					 * */
					drawWedge(g, cx, cy, s, (i * 360 - 180) / n, 360 / n, COLORS[i]);	// Dibuja un sector
					System.out.println("SECTOR COLOR" + COLORS[i]);
				}
				g.setColor(Color.DARK_GRAY);	// Color del circulo central
				g.fillOval(cx - s / 6, cy - s / 6, s / 3, s / 3);	// Dibuja el círculo central
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

	private void setupMenus() {
		mainMenu = new JMenuBar();
		mainMenu.add(fileMenu = new JMenu("Archivo"));
		fileMenu.add(quitItem = new JMenuItem("Salir"));
		setJMenuBar(mainMenu);

		quitItem.addActionListener(e -> closeWindow());
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
}
