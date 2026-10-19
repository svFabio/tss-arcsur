package Vista;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class HeatmapTableCellRenderer extends DefaultTableCellRenderer {

    private boolean heatmapActivo = true;
    private int maxFrecuencia = 1;

    public HeatmapTableCellRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    public void setHeatmapActivo(boolean activo) {
        this.heatmapActivo = activo;
    }

    public boolean isHeatmapActivo() {
        return heatmapActivo;
    }

    public void recalcularMaximo(JTable table) {
        int max = 0;
        int rowCount = table.getRowCount();
        int colCount = table.getColumnCount();

        for (int i = 0; i < rowCount; i++) {
            for (int j = 1; j < colCount; j++) {
                // Omitir columna TOTAL si es la última
                String colName = table.getColumnName(j);
                if ("TOTAL".equalsIgnoreCase(colName) || j == colCount - 1) {
                    continue;
                }
                Object val = table.getValueAt(i, j);
                if (val instanceof Number) {
                    int num = ((Number) val).intValue();
                    if (num > max) max = num;
                } else if (val != null) {
                    try {
                        int num = Integer.parseInt(val.toString().trim());
                        if (num > max) max = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        this.maxFrecuencia = Math.max(max, 1);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        int colCount = table.getColumnCount();
        boolean esColumnaEtiqueta = (column == 0);
        boolean esColumnaTotal = (column == colCount - 1) || "TOTAL".equalsIgnoreCase(table.getColumnName(column));
        String rowName = String.valueOf(table.getValueAt(row, 0));
        boolean esFilaTotal = "TOTAL".equalsIgnoreCase(rowName);

        // 1. Columna de Etiquetas (Nombre de Categoría Fila)
        if (esColumnaEtiqueta) {
            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(new Font("SansSerif", Font.BOLD, 12));
            setBackground(new Color(241, 245, 249));
            setForeground(new Color(30, 41, 59));
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 4));
            setToolTipText(value != null ? value.toString() : null);
            return c;
        }

        setHorizontalAlignment(SwingConstants.CENTER);

        // 2. Celdas de Totales Marginales
        if (esColumnaTotal || esFilaTotal) {
            setFont(new Font("SansSerif", Font.BOLD, 12));
            setBackground(new Color(226, 232, 240));
            setForeground(new Color(15, 23, 42));
            setToolTipText("Total marginal: " + (value != null ? value.toString() : "0"));
            return c;
        }

        // 3. Si el modo Heatmap está inactivo, usar estilo neutro
        if (!heatmapActivo) {
            setFont(new Font("SansSerif", Font.PLAIN, 12));
            setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));
            setForeground(isSelected ? table.getSelectionForeground() : new Color(30, 41, 59));
            setToolTipText(null);
            return c;
        }

        // 4. Modo Heatmap Activo: Gradiente cromático por densidad térmica
        int freq = 0;
        if (value instanceof Number) {
            freq = ((Number) value).intValue();
        } else if (value != null) {
            try {
                freq = Integer.parseInt(value.toString().trim());
            } catch (NumberFormatException ignored) {}
        }

        if (freq == 0) {
            setBackground(new Color(248, 250, 252));
            setForeground(new Color(148, 163, 184));
            setFont(new Font("SansSerif", Font.PLAIN, 12));
            setToolTipText("Frecuencia: 0 casos");
        } else {
            double ratio = Math.min(1.0, (double) freq / maxFrecuencia);

            // Gradiente: de azul cielo claro (224, 242, 254) a azul profundo (14, 116, 144) o (27, 79, 138)
            int r = (int) (224 - ratio * (224 - 27));
            int g = (int) (242 - ratio * (242 - 79));
            int b = (int) (254 - ratio * (254 - 138));

            Color fondo = new Color(Math.max(0, r), Math.max(0, g), Math.max(0, b));
            setBackground(fondo);

            if (ratio > 0.48) {
                setForeground(Color.WHITE);
                setFont(new Font("SansSerif", Font.BOLD, 12));
            } else {
                setForeground(new Color(15, 23, 42));
                setFont(new Font("SansSerif", Font.BOLD, 12));
            }

            setToolTipText(String.format("Frecuencia: %d casos (%.1f%% de la concentracion maxima)", freq, ratio * 100));
        }

        return c;
    }
}
