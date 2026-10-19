package Vista;

import Modelo.ResumenEstadistico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelAnalisis extends JPanel {
    public JComboBox<String> comboVariables;
    public JComboBox<String> comboTipoGrafico;
    public JButton btnExportarCSV;
    public JTable tablaFrecuencias;
    public DefaultTableModel modeloTabla;
    public GraficoBarras graficoBarras;
    public GraficoPastel graficoPastel;
    public GraficoRadar graficoRadar;
    public JPanel panelGraficos;
    public CardLayout cardLayoutGraficos;
    private PanelTarjetasEstadisticas panelTarjetas;
    private ResumenEstadistico resumenActual;

    public PanelAnalisis() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ConfiguracionColores.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel Superior (Selectores + Tarjetas KPI)
        JPanel panelNorteContenedor = new JPanel();
        panelNorteContenedor.setLayout(new BoxLayout(panelNorteContenedor, BoxLayout.Y_AXIS));
        panelNorteContenedor.setOpaque(false);

        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        panelNorte.setOpaque(false);
        panelNorte.add(new JLabel("Seleccionar Variable:"));
        comboVariables = new JComboBox<>();
        panelNorte.add(comboVariables);

        panelNorte.add(new JLabel("Tipo de Grafico:"));
        comboTipoGrafico = new JComboBox<>(new String[]{"Barras", "Pastel (Pie)", "Radar (Spider)"});
        panelNorte.add(comboTipoGrafico);

        btnExportarCSV = new JButton("Exportar CSV");
        btnExportarCSV.setFont(new Font("SansSerif", Font.BOLD, 12));
        panelNorte.add(btnExportarCSV);

        panelTarjetas = new PanelTarjetasEstadisticas();
        panelTarjetas.setVisible(false); // oculto por defecto

        panelNorteContenedor.add(panelNorte);
        panelNorteContenedor.add(panelTarjetas);
        add(panelNorteContenedor, BorderLayout.NORTH);

        // Panel Central (Split: Tabla y Grafico)
        modeloTabla = new DefaultTableModel(new Object[]{"Categoria", "Frecuencia", "Porcentaje (%)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaFrecuencias = new JTable(modeloTabla);
        tablaFrecuencias.setRowHeight(22);
        tablaFrecuencias.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        JScrollPane scrollTabla = new JScrollPane(tablaFrecuencias);

        graficoBarras = new GraficoBarras();
        graficoPastel = new GraficoPastel();
        graficoRadar = new GraficoRadar();

        cardLayoutGraficos = new CardLayout();
        panelGraficos = new JPanel(cardLayoutGraficos);
        panelGraficos.add(graficoBarras, "Barras");
        panelGraficos.add(graficoPastel, "Pastel (Pie)");
        panelGraficos.add(graficoRadar, "Radar (Spider)");

        comboTipoGrafico.addActionListener(e -> {
            String seleccionado = (String) comboTipoGrafico.getSelectedItem();
            if (seleccionado != null) {
                cardLayoutGraficos.show(panelGraficos, seleccionado);
            }
        });

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTabla, panelGraficos);
        split.setDividerLocation(320);
        add(split, BorderLayout.CENTER);
    }

    public void actualizarVariables(String[] columnas) {
        comboVariables.removeAllItems();
        if (columnas != null) {
            for (String col : columnas) {
                comboVariables.addItem(col);
            }
        }
    }

    public void mostrarTarjetas(ResumenEstadistico resumen) {
        this.resumenActual = resumen;
        panelTarjetas.mostrarTarjetas(resumen);
        revalidate();
        repaint();
    }

    public void ocultarTarjetas() {
        this.resumenActual = null;
        panelTarjetas.ocultarTarjetas();
        revalidate();
        repaint();
    }

    public ResumenEstadistico getResumenActual() {
        return resumenActual;
    }
}
