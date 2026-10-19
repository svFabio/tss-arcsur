package Vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelCruces extends JPanel {
    public JComboBox<String> comboVar1;
    public JComboBox<String> comboVar2;
    public JButton btnCalcular;
    public JButton btnExportarCSV;
    public JCheckBox chkHeatmap;
    public JTable tablaContingencia;
    public DefaultTableModel modeloTabla;
    public JLabel lblResultadoChi;
    public HeatmapTableCellRenderer heatmapRenderer;
    public JPanel panelLeyendaHeatmap;

    public PanelCruces() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ConfiguracionColores.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Panel Norte: Selectores y Acciones ────────────────────────
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelNorte.setOpaque(false);

        comboVar1 = new JComboBox<>();
        comboVar2 = new JComboBox<>();
        btnCalcular = new JButton("Calcular Chi-Cuadrada");
        btnExportarCSV = new JButton("Exportar Matriz (CSV)");
        btnExportarCSV.setEnabled(false); // habilitar tras calcular

        chkHeatmap = new JCheckBox("Mapa de Calor (Heatmap)", true);
        chkHeatmap.setOpaque(false);
        chkHeatmap.setFont(new Font("SansSerif", Font.BOLD, 12));

        panelLeyendaHeatmap = crearPanelLeyenda();

        panelNorte.add(new JLabel("Variable Fila:"));
        panelNorte.add(comboVar1);
        panelNorte.add(new JLabel("Variable Columna:"));
        panelNorte.add(comboVar2);
        panelNorte.add(btnCalcular);
        panelNorte.add(btnExportarCSV);
        panelNorte.add(chkHeatmap);
        panelNorte.add(panelLeyendaHeatmap);

        add(panelNorte, BorderLayout.NORTH);

        // ── Panel Central: Tabla de Contingencia con Heatmap ──────────
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Solo lectura
            }
        };
        tablaContingencia = new JTable(modeloTabla);
        tablaContingencia.setRowHeight(24);
        tablaContingencia.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tablaContingencia.getTableHeader().setBackground(new Color(241, 245, 249));

        heatmapRenderer = new HeatmapTableCellRenderer();
        tablaContingencia.setDefaultRenderer(Object.class, heatmapRenderer);

        chkHeatmap.addActionListener(e -> {
            boolean activo = chkHeatmap.isSelected();
            heatmapRenderer.setHeatmapActivo(activo);
            panelLeyendaHeatmap.setVisible(activo);
            tablaContingencia.repaint();
        });

        JScrollPane scrollTabla = new JScrollPane(tablaContingencia);
        scrollTabla.getViewport().setBackground(Color.WHITE);
        add(scrollTabla, BorderLayout.CENTER);

        // ── Panel Sur: Resultados Estadísticos ────────────────────────
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setOpaque(false);

        lblResultadoChi = new JLabel("Seleccione dos variables y presione 'Calcular Chi-Cuadrada'");
        lblResultadoChi.setFont(new Font("SansSerif", Font.BOLD, ConfiguracionTextos.TAMANO_FUENTE_NORMAL));
        lblResultadoChi.setForeground(new Color(30, 41, 59));
        lblResultadoChi.setBorder(BorderFactory.createEmptyBorder(8, 6, 8, 6));

        panelSur.add(lblResultadoChi, BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);
    }

    private JPanel crearPanelLeyenda() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);

        JLabel lblBaja = new JLabel("0");
        lblBaja.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblBaja.setForeground(Color.GRAY);

        JPanel barraGradiente = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(224, 242, 254), getWidth(), 0, new Color(27, 79, 138)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        barraGradiente.setPreferredSize(new Dimension(50, 12));
        barraGradiente.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel lblAlta = new JLabel("Max");
        lblAlta.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblAlta.setForeground(new Color(27, 79, 138));

        p.add(lblBaja);
        p.add(barraGradiente);
        p.add(lblAlta);
        return p;
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
