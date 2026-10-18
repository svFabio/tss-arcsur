package Vista;

import javax.swing.*;
import java.awt.*;

public class PanelCarga extends JPanel {
    public JButton btnCargarTitulados;
    public JButton btnCargarEmpleadores;
    public JButton btnDatosPrueba;
    public JButton btnCargarNube;
    public JLabel lblEstadoTitulados;
    public JLabel lblEstadoEmpleadores;

    public PanelCarga() {
        setLayout(new GridBagLayout());
        setBackground(ConfiguracionColores.COLOR_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblTitulo = new JLabel(ConfiguracionTextos.TITULO_APLICACION);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, ConfiguracionTextos.TAMANO_FUENTE_TITULO));
        lblTitulo.setForeground(ConfiguracionColores.COLOR_PRIMARIO);

        btnCargarTitulados = new JButton(ConfiguracionTextos.BOTON_CARGAR_TITULADOS);
        btnCargarEmpleadores = new JButton(ConfiguracionTextos.BOTON_CARGAR_EMPLEADORES);
        btnDatosPrueba = new JButton("Cargar Datos de Prueba (Simulados)");
        btnCargarNube = new JButton("Descargar Respuestas de la Nube (Supabase)");

        lblEstadoTitulados = new JLabel("Titulados: No cargado");
        lblEstadoEmpleadores = new JLabel("Empleadores: No cargado");

        estilizarBoton(btnCargarTitulados);
        estilizarBoton(btnCargarEmpleadores);
        estilizarBoton(btnDatosPrueba);
        estilizarBoton(btnCargarNube);
        btnCargarNube.setBackground(ConfiguracionColores.COLOR_EXITO);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; add(btnCargarTitulados, gbc);
        gbc.gridx = 1; add(lblEstadoTitulados, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(btnCargarEmpleadores, gbc);
        gbc.gridx = 1; add(lblEstadoEmpleadores, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(btnDatosPrueba, gbc);

        gbc.gridy = 4;
        add(btnCargarNube, gbc);
    }

    private void estilizarBoton(JButton boton) {
        boton.setBackground(ConfiguracionColores.COLOR_PRIMARIO);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setFont(new Font("SansSerif", Font.BOLD, ConfiguracionTextos.TAMANO_FUENTE_NORMAL));
    }
}
