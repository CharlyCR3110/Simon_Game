package com.simon.ejemploBase.view;

import com.simon.ejemploBase.configuration.Configuration;
import com.simon.ejemploBase.control.Controller;
import com.simon.ejemploBase.model.ModelView;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * -------------------------------------------------------------------
 *
 * (c) 2021-2022
 *
 * @author Georges Alfaro S.
 * @version 2.1.0 2021-09-13
 *
 * --------------------------------------------------------------------
 */
public class ApplicationWindow extends JFrame implements PropertyChangeListener {

	public ApplicationWindow(String title, Controller mainControl)
			throws HeadlessException {
		super(title);

		// Asocia la clase de control para poder delegar los métodos
		// que van a ser ejecutados desde la interfaz.
		//
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

		// En lugar de definir un comportamiento por defecto para
		// la ventana, se asocia un WindowListener de manera que
		// la ventana utilice el mismo método cuando se cierra por
		// medio del control en la barra de nombre o cuando se
		// selecciona la opción desde un menú u otro control de
		// interfaz.
		//
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

		JPanel mainPanel = new JPanel() {
			@Override
			public void paintComponent(Graphics bg) {
				super.paintComponent(bg);
				Graphics2D g = (Graphics2D) bg;
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);

				int cx = getWidth() / 2;
				int cy = getHeight() / 2;

				g.setColor(Color.CYAN.darker());
				g.drawLine(cx, 0, cx, getHeight());
				g.drawLine(0, cy, getWidth(), cy);

				int s = (int) (0.80 * Math.min(getWidth(), getHeight()));

				int n = 4;
				for (int i = 0; i < n; i++) {
					drawWedge(g, cx, cy, s, (i * 360 - 180) / n, 360 / n, COLORS[i]);
				}

				g.setColor(Color.DARK_GRAY);
				g.fillOval(cx - s / 6, cy - s / 6, s / 3, s / 3);
			}

			private void drawWedge(Graphics2D g, int cx, int cy, int s, int start, int end, Color c) {

				double r = Math.PI / 180.0;
				int x0 = (int) (cx + s * 0.5 * Math.cos(-start * r));
				int y0 = (int) (cy + s * 0.5 * Math.sin(-start * r));
				int x1 = (int) (cx + s * 0.5 * Math.cos(-(start + end) * r));
				int y1 = (int) (cy + s * 0.5 * Math.sin(-(start + end) * r));

				g.setColor(c);
				g.fillArc(cx - s / 2, cy - s / 2, s, s, start, end);

				g.setColor(Color.DARK_GRAY);
				g.setStroke(new BasicStroke(16f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
				g.drawLine(cx, cy, x0, y0);
				g.drawLine(cx, cy, x1, y1);
				g.drawArc(cx - s / 2, cy - s / 2, s, s, start, end);
			}

			Color[] COLORS = new Color[]{
					Color.RED,
					Color.GREEN,
					Color.YELLOW,
					new Color(72, 72, 255),
					Color.ORANGE,
					Color.PINK,
					Color.WHITE
			};

		};

		mainPanel.setBackground(Color.WHITE);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		c.add(BorderLayout.CENTER, mainPanel);

		c.add(BorderLayout.PAGE_END, status = new StatusBar());

		// Define los eventos asociados a cada componente de la ventana.
		// Los menús son configurados en un método separado.
		//
	}

	private void setupMenus() {
		mainMenu = new JMenuBar();

		mainMenu.add(fileMenu = new JMenu("Archivo"));
		fileMenu.add(quitItem = new JMenuItem("Salir"));

		setJMenuBar(mainMenu);

		quitItem.addActionListener(e -> {
			closeWindow();
		});
	}

	public void init() {

		// Por medio del gestor o clase de control, se registra
		// la ventana con el modelo, para que pueda recibir notificaciones
		// de los eventos que requieran actualizar la interfaz.
		//
		mainControl.register(this);

		getWindowConfiguration();
		status.init();

		setVisible(true);
		status.showMessage(String.format("Interfaz inicializada (%d, %d)..",
				getWidth(), getHeight()));
		System.out.println();

		// Se usa un temporizador para que los mensajes que aparecen en
		// la barra de estado se borren luego de un tiempo especificado.
		//
		status.setTimed(true);
		status.setMaxTime(MAX_MSG_TIME);
	}

	public boolean confirmClose() {
		Object[] options = {"Sí", "No"};
		return JOptionPane.showOptionDialog(this,
				"¿Desea cerrar la aplicación?", "Confirmación",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
				options, // texto de los botones
				options[0] // opción por defecto
		) == JOptionPane.OK_OPTION;
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

		// La ventana recibe una notificación de un evento desde el modelo.
		//
		// En este método, la ventana debe actualizar todos los objetos
		// que muestran algún elemento del modelo en la interfaz.
		// Observe que no hay ninguna referencia al modelo desde los
		// parámetros del método. Si es necesario obtener el estado del
		// modelo, se solicitará a la clase de control.
		//
		status.showMessage(
				String.format("Evento recibido: %s = %s",
						evt.getPropertyName(), evt.getNewValue())
		);

		ModelView model = mainControl.getModel();

		// Aquí se actualiza la interfaz (cuando sea necesario)..
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

	private final Controller mainControl;
	private static final int MAX_MSG_TIME = 5_000;

	private JMenuBar mainMenu;

	private JMenu fileMenu;
	private JMenuItem quitItem;

	private StatusBar status;

	// La ventana no maneja ninguna información del modelo.
	// Todas las operaciones son delegadas a la clase de control.
}

