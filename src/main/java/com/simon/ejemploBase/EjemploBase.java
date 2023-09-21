package com.simon.ejemploBase;

import com.simon.ejemploBase.configuration.Configuration;
import com.simon.ejemploBase.control.Controller;
import com.simon.ejemploBase.view.ApplicationWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Georges Alfaro S.
 * @version 1.0.0 2023-09-05
 */
public class EjemploBase {

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

        // 2021-08-06
        // Para la versión 14+ del JDK, no es necesario redefinir los iconos
        // por defecto de la interfaz, ya que no hay problema de despliegue
        // en dispositivos de alta resolución.
        //
        // setIcon("OptionPane.errorIcon", "view/icons/error.png");
        // setIcon("OptionPane.informationIcon", "view/icons/information.png");
        // setIcon("OptionPane.questionIcon", "view/icons/question.png");
        // setIcon("OptionPane.warningIcon", "view/icons/warning.png");
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
        Controller control = new Controller(configuration);
        control.init();

        new ApplicationWindow(getClass().getSimpleName(), control).init();
    }

    private final Configuration configuration;
}