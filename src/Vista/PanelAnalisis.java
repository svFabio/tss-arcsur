package Vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelAnalisis extends JPanel {
    public JComboBox<String> comboVariables;
    public JTable tablaFrecuencias;
    public DefaultTableModel modeloTabla;
    public GraficoBarras graficoBarras;

    public PanelAnalisis() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ConfiguracionColores.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel Superior (Selector)
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNorte.setOpaque(false);
        panelNorte.add(new JLabel("Seleccionar Variable:"));
        comboVariables = new JComboBox<>();
        panelNorte.add(comboVariables);
        add(panelNorte, BorderLayout.NORTH);

        // Panel Central (Split: Tabla y Grafico)
        modeloTabla = new DefaultTableModel(new Object[]{"Categoría", "Frecuencia", "Porcentaje (%)"}, 0);
        tablaFrecuencias = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaFrecuencias);

        graficoBarras = new GraficoBarras();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTabla, graficoBarras);
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
