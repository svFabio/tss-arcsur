package Vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelCruces extends JPanel {
    public JComboBox<String> comboVar1;
    public JComboBox<String> comboVar2;
    public JButton btnCalcular;
    public JTable tablaContingencia;
    public DefaultTableModel modeloTabla;
    public JLabel lblResultadoChi;

    public PanelCruces() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ConfiguracionColores.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNorte.setOpaque(false);
        
        comboVar1 = new JComboBox<>();
        comboVar2 = new JComboBox<>();
        btnCalcular = new JButton("Calcular Chi-Cuadrada");

        panelNorte.add(new JLabel("Variable Fila (Independiente):"));
        panelNorte.add(comboVar1);
        panelNorte.add(new JLabel("Variable Columna (Dependiente):"));
        panelNorte.add(comboVar2);
        panelNorte.add(btnCalcular);

        add(panelNorte, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel();
        tablaContingencia = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaContingencia);
        add(scrollTabla, BorderLayout.CENTER);

        lblResultadoChi = new JLabel("Resultados de Chi-Cuadrada aparecerán aquí");
        lblResultadoChi.setFont(new Font("SansSerif", Font.BOLD, ConfiguracionTextos.TAMANO_FUENTE_NORMAL));
        lblResultadoChi.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblResultadoChi, BorderLayout.SOUTH);
    }

    public void actualizarVariables(String[] columnas) {
        comboVar1.removeAllItems();
        comboVar2.removeAllItems();
        if (columnas != null) {
            for (String col : columnas) {
                comboVar1.addItem(col);
                comboVar2.addItem(col);
            }
        }
    }
}
