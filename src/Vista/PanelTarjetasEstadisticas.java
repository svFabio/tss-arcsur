package Vista;

import Modelo.ResumenEstadistico;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Panel de tarjetas tipo KPI Dashboard que muestra estadísticas descriptivas
 * univariadas universales para cualquier variable seleccionada (cuantitativa,
 * ordinal/Likert, binaria o cualitativa nominal).
 */
public class PanelTarjetasEstadisticas extends JPanel {

    private JLabel lblTitulo1, lblValor1;
    private JLabel lblTitulo2, lblValor2;
    private JLabel lblTitulo3, lblValor3;
    private JLabel lblTitulo4, lblValor4;
    private JLabel lblTitulo5, lblValor5;

    public PanelTarjetasEstadisticas() {
        setLayout(new GridLayout(1, 5, 10, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(6, 0, 6, 0));

        JLabel[] t1 = crearTarjeta("Media (x̄)",                ConfiguracionColores.COLOR_PRIMARIO);
        lblTitulo1 = t1[0]; lblValor1 = t1[1];

        JLabel[] t2 = crearTarjeta("Mediana (Me)",               new Color(46, 204, 113));
        lblTitulo2 = t2[0]; lblValor2 = t2[1];

        JLabel[] t3 = crearTarjeta("Desv. Estándar (s)",         new Color(155, 89, 182));
        lblTitulo3 = t3[0]; lblValor3 = t3[1];

        JLabel[] t4 = crearTarjeta("Rango [Mín - Máx]",          new Color(243, 156, 18));
        lblTitulo4 = t4[0]; lblValor4 = t4[1];

        JLabel[] t5 = crearTarjeta("Muestra Válida (N)",          new Color(52, 73, 94));
        lblTitulo5 = t5[0]; lblValor5 = t5[1];
    }

    /**
     * Construye una tarjeta visual con título y valor destacado.
     * @return un arreglo con [JLabel titulo, JLabel valor].
     */
    private JLabel[] crearTarjeta(String titulo, Color colorAcento) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 4));
        tarjeta.setBackground(ConfiguracionColores.COLOR_PANEL);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(colorAcento, 2, true),
            new EmptyBorder(8, 8, 8, 8)
        ));

        // Título superior
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTitulo.setForeground(new Color(90, 95, 105));

        // Valor numérico o categórico destacado abajo
        JLabel lblValor = new JLabel("—", SwingConstants.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValor.setForeground(colorAcento);

        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(lblValor,  BorderLayout.CENTER);

        add(tarjeta);
        return new JLabel[]{lblTitulo, lblValor};
    }

    /**
     * Actualiza los valores y títulos de las 5 tarjetas con la información
     * del resumen estadístico recibido.
     */
    public void mostrarTarjetas(ResumenEstadistico resumen) {
        if (resumen == null) {
            ocultarTarjetas();
            return;
        }

        actualizarTarjeta(lblTitulo1, lblValor1, resumen.titulo1, resumen.valor1);
        actualizarTarjeta(lblTitulo2, lblValor2, resumen.titulo2, resumen.valor2);
        actualizarTarjeta(lblTitulo3, lblValor3, resumen.titulo3, resumen.valor3);
        actualizarTarjeta(lblTitulo4, lblValor4, resumen.titulo4, resumen.valor4);
        actualizarTarjeta(lblTitulo5, lblValor5, resumen.titulo5, resumen.valor5);

        setVisible(true);
    }

    /**
     * Ajusta el texto, tooltip y tamaño de fuente de una tarjeta según la longitud del texto.
     */
    private void actualizarTarjeta(JLabel lblTitulo, JLabel lblValor, String titulo, String valor) {
        lblTitulo.setText(titulo != null ? titulo : "");
        lblValor.setText(valor != null ? valor : "—");
        lblValor.setToolTipText(valor);

        // Control dinámico de tipografía para evitar desbordamiento en nombres largos
        if (valor != null && valor.length() > 15) {
            lblValor.setFont(new Font("Segoe UI", Font.BOLD, 12));
        } else if (valor != null && valor.length() > 10) {
            lblValor.setFont(new Font("Segoe UI", Font.BOLD, 14));
        } else {
            lblValor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        }
    }

    /** Oculta el panel si fuera necesario. */
    public void ocultarTarjetas() {
        setVisible(false);
    }
}
