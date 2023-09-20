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
		c.add(BorderLayout.CENTER, mainPanel);
		c.add(BorderLayout.PAGE_END, status = new StatusBar());
	}

	private JPanel createMainPanel() {
		return new JPanel() {
			@Override
			public void paintComponent(Graphics bg) {
				super.paintComponent(bg);
				Graphics2D g = (Graphics2D) bg;
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				int cx = getWidth() / 2;
				int cy = getHeight() / 2;
				g.setColor(Color.CYAN.darker());
				g.drawLine(cx, 0, cx, getHeight());
				g.drawLine(0, cy, getWidth(), cy);
				int s = (int) (0.80 * Math.min(getWidth(), getHeight()));
				int n = 6;	// Número de sectores (wedges)
				for (int i = 0; i < n; i++) {
					drawWedge(g, cx, cy, s, (i * 360 - 180) / n, 360 / n, COLORS[i]);
				}
				g.setColor(Color.DARK_GRAY);
				g.fillOval(cx - s / 6, cy - s / 6, s / 3, s / 3);
			}
		};
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

	private static final Color[] COLORS = {
			Color.RED,
			Color.GREEN,
			Color.YELLOW,
			new Color(72, 72, 255),
			new Color(0, 255, 255),
			new Color(255, 0, 255)
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
