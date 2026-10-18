package Vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelAnalisis extends JPanel {
    public JComboBox<String> comboVariables;
    public JComboBox<String> comboTipoGrafico;
    public JTable tablaFrecuencias;
    public DefaultTableModel modeloTabla;
    public GraficoBarras graficoBarras;
    public GraficoPastel graficoPastel;
    public JPanel panelGraficos;
    public CardLayout cardLayoutGraficos;

    public PanelAnalisis() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ConfiguracionColores.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel Superior (Selectores)
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        panelNorte.setOpaque(false);
        panelNorte.add(new JLabel("Seleccionar Variable:"));
        comboVariables = new JComboBox<>();
        panelNorte.add(comboVariables);

        panelNorte.add(new JLabel("Tipo de Gráfico:"));
        comboTipoGrafico = new JComboBox<>(new String[]{"Barras", "Pastel (Pie)"});
        panelNorte.add(comboTipoGrafico);
        add(panelNorte, BorderLayout.NORTH);

        // Panel Central (Split: Tabla y Grafico)
        modeloTabla = new DefaultTableModel(new Object[]{"Categoría", "Frecuencia", "Porcentaje (%)"}, 0);
        tablaFrecuencias = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaFrecuencias);

        graficoBarras = new GraficoBarras();
        graficoPastel = new GraficoPastel();

        cardLayoutGraficos = new CardLayout();
        panelGraficos = new JPanel(cardLayoutGraficos);
        panelGraficos.add(graficoBarras, "Barras");
        panelGraficos.add(graficoPastel, "Pastel (Pie)");

        comboTipoGrafico.addActionListener(e -> {
            String seleccionado = (String) comboTipoGrafico.getSelectedItem();
            if (seleccionado != null) {
                cardLayoutGraficos.show(panelGraficos, seleccionado);
            }
        });

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTabla, panelGraficos);
        split.setDividerLocation(300);
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
}
