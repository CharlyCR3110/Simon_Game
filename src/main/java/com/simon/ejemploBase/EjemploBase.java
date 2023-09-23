package com.simon.ejemploBase;

import com.simon.ejemploBase.configuration.Configuration;
import com.simon.ejemploBase.configuration.GameConfig;
import com.simon.ejemploBase.control.Controller;
import com.simon.ejemploBase.view.ApplicationWindow;
import com.simon.xml.XmlPersister;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

public class EjemploBase {
    private final Configuration configuration;

    public EjemploBase() {
        this.configuration = Configuration.getInstance();
    }

    public static void main(String[] args) {
        try {
            setupGUI();

        } catch (ClassNotFoundException
                | IllegalAccessException
                | InstantiationException
                | IOException
                | UnsupportedLookAndFeelException ex) {
            System.err.printf("Excepción: '%s'%n", ex.getMessage());
        }
        new EjemploBase().init();
    }

    private static void setupGUI() throws
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            UnsupportedLookAndFeelException,
            IOException {

        System.out.println("Configurando interfaz..");

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFrame.setDefaultLookAndFeelDecorated(true);
    }

    private static void setIcon(String iconName, String iconFile) throws IOException {
        InputStream in = EjemploBase.class.getResourceAsStream(iconFile);
        ImageIcon icon = new ImageIcon(ImageIO.read(in));
        UIManager.put(iconName, icon);
    }

    public void init() {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    public void createAndShowGUI() {
        GameConfig gameConfig = null;
        try {
            gameConfig = XmlPersister.instance().load();
        } catch (Exception e) {
            gameConfig = new GameConfig();  // en caso de que no se pueda cargar el archivo de configuración se crea uno nuevo
        }
        Controller control = new Controller(configuration, gameConfig);
        control.init();

        ApplicationWindow app = new ApplicationWindow(getClass().getSimpleName(), control);

        control.addView(app);
    }
}
