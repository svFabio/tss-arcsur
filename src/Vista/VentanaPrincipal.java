package Vista;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    public JTabbedPane pestanas;
    public PanelCarga panelCarga;
    public PanelEncuestaTitulados panelEncuestaTitulados;
    public PanelEncuestaEmpleadores panelEncuestaEmpleadores;
    public PanelAnalisis panelTitulados;
    public PanelAnalisis panelEmpleadores;
    public PanelCruces panelCruces;

    public VentanaPrincipal() {
        setTitle(ConfiguracionTextos.TITULO_APLICACION);
        setSize(ConfiguracionVentana.ANCHO_VENTANA, ConfiguracionVentana.ALTO_VENTANA);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ConfiguracionColores.COLOR_FONDO);

        pestanas = new JTabbedPane();
        panelCarga = new PanelCarga();
        panelEncuestaTitulados = new PanelEncuestaTitulados();
        panelEncuestaEmpleadores = new PanelEncuestaEmpleadores();
        panelTitulados = new PanelAnalisis();
        panelEmpleadores = new PanelAnalisis();
        panelCruces = new PanelCruces();

        pestanas.addTab("Carga de Datos", panelCarga);
        pestanas.addTab("Encuesta Titulados", panelEncuestaTitulados);
        pestanas.addTab("Encuesta Empleadores", panelEncuestaEmpleadores);
        pestanas.addTab("Análisis Titulados", panelTitulados);
        pestanas.addTab("Análisis Empleadores", panelEmpleadores);
        pestanas.addTab("Cruces (Chi-Cuadrada)", panelCruces);

        // Deshabilitar pestañas de análisis hasta cargar datos
        pestanas.setEnabledAt(3, false);
        pestanas.setEnabledAt(4, false);
        pestanas.setEnabledAt(5, false);

        add(pestanas, BorderLayout.CENTER);
    }
}
