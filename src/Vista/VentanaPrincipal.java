package Vista;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    public JTabbedPane pestanas;
    public PanelCarga panelCarga;
    public PanelDashboard panelDashboard;
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
        panelDashboard = new PanelDashboard();
        panelEncuestaTitulados = new PanelEncuestaTitulados();
        panelEncuestaEmpleadores = new PanelEncuestaEmpleadores();
        panelTitulados = new PanelAnalisis();
        panelEmpleadores = new PanelAnalisis();
        panelCruces = new PanelCruces();

        pestanas.addTab("Carga de Datos", panelCarga);
        pestanas.addTab("Dashboard Ejecutivo", panelDashboard);
        pestanas.addTab("Encuesta Titulados", panelEncuestaTitulados);
        pestanas.addTab("Encuesta Empleadores", panelEncuestaEmpleadores);
        pestanas.addTab("Analisis Titulados", panelTitulados);
        pestanas.addTab("Analisis Empleadores", panelEmpleadores);
        pestanas.addTab("Cruces (Chi-Cuadrada)", panelCruces);

        // Deshabilitar pestañas analíticas hasta que se carguen datos
        pestanas.setEnabledAt(1, false); // Dashboard
        pestanas.setEnabledAt(4, false); // Análisis Titulados
        pestanas.setEnabledAt(5, false); // Análisis Empleadores
        pestanas.setEnabledAt(6, false); // Cruces

        add(pestanas, BorderLayout.CENTER);
    }
}
