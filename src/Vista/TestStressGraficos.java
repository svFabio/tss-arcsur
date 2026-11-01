package Vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * Dedicated Stress Test Runner for Domain 3: Java 2D Visual Components & Charts.
 * Tests all 8 components against degenerate datasets, edge-case dimensions,
 * null/empty inputs, negative values, and offscreen BufferedImage rendering.
 */
public class TestStressGraficos {

    private static int totalPassed = 0;
    private static int totalFailed = 0;

    private static void testCase(String component, String scenario, Runnable testAction) {
        System.out.printf("[RUNNING] %-25s | %-45s ... ", component, scenario);
        try {
            testAction.run();
            System.out.println("PASSED");
            totalPassed++;
        } catch (Throwable t) {
            System.out.println("FAILED (" + t.getClass().getSimpleName() + ": " + t.getMessage() + ")");
            t.printStackTrace(System.out);
            totalFailed++;
        }
    }

    private static void renderToImage(JComponent comp, int w, int h) {
        comp.setSize(w, h);
        comp.doLayout();
        int imgW = Math.max(1, w);
        int imgH = Math.max(1, h);
        BufferedImage img = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        try {
            comp.paint(g2d);
        } finally {
            g2d.dispose();
        }
    }

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        System.out.println("================================================================================");
        System.out.println("       DEEP QA STRESS TEST RUNNER - DOMAIN 3: JAVA 2D CHARTS & RENDERERS       ");
        System.out.println("================================================================================");

        // ─────────────────────────────────────────────────────────────────────────────
        // 1. GraficoHistograma
        // ─────────────────────────────────────────────────────────────────────────────
        System.out.println("\n--- Testing GraficoHistograma ---");
        testCase("GraficoHistograma", "Null dataset", () -> {
            GraficoHistograma g = new GraficoHistograma();
            g.setDatos(null, "Null Data");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoHistograma", "Empty dataset", () -> {
            GraficoHistograma g = new GraficoHistograma();
            g.setDatos(new ArrayList<>(), "Empty");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoHistograma", "Single value dataset [42.0]", () -> {
            GraficoHistograma g = new GraficoHistograma();
            g.setDatos(new ArrayList<>(Arrays.asList(42.0)), "Single");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoHistograma", "Identical values [10.0, 10.0, 10.0]", () -> {
            GraficoHistograma g = new GraficoHistograma();
            g.setDatos(new ArrayList<>(Arrays.asList(10.0, 10.0, 10.0, 10.0)), "Identical");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoHistograma", "Negative values [-25.0, -10.0, -5.0]", () -> {
            GraficoHistograma g = new GraficoHistograma();
            g.setDatos(new ArrayList<>(Arrays.asList(-25.0, -10.0, -5.0, -2.0)), "Negative");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoHistograma", "Dimensions 0x0", () -> {
            GraficoHistograma g = new GraficoHistograma();
            g.setDatos(new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0)), "0x0");
            renderToImage(g, 0, 0);
        });

        testCase("GraficoHistograma", "Dimensions 10x10", () -> {
            GraficoHistograma g = new GraficoHistograma();
            g.setDatos(new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0)), "10x10");
            renderToImage(g, 10, 10);
        });

        testCase("GraficoHistograma", "Dimensions 1000x1000", () -> {
            GraficoHistograma g = new GraficoHistograma();
            g.setDatos(new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0)), "1000x1000");
            renderToImage(g, 1000, 1000);
        });

        // ─────────────────────────────────────────────────────────────────────────────
        // 2. GraficoPoligono
        // ─────────────────────────────────────────────────────────────────────────────
        System.out.println("\n--- Testing GraficoPoligono ---");
        testCase("GraficoPoligono", "Null dataset", () -> {
            GraficoPoligono g = new GraficoPoligono();
            g.setDatos(null, "Null Data");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoPoligono", "Empty dataset", () -> {
            GraficoPoligono g = new GraficoPoligono();
            g.setDatos(new ArrayList<>(), "Empty");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoPoligono", "Single value [5.0]", () -> {
            GraficoPoligono g = new GraficoPoligono();
            g.setDatos(new ArrayList<>(Arrays.asList(5.0)), "Single");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoPoligono", "Identical values [7.0, 7.0, 7.0]", () -> {
            GraficoPoligono g = new GraficoPoligono();
            g.setDatos(new ArrayList<>(Arrays.asList(7.0, 7.0, 7.0)), "Identical");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoPoligono", "Negative values [-30.0, -15.0, -1.0]", () -> {
            GraficoPoligono g = new GraficoPoligono();
            g.setDatos(new ArrayList<>(Arrays.asList(-30.0, -15.0, -1.0)), "Negative");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoPoligono", "Dimensions 0x0", () -> {
            GraficoPoligono g = new GraficoPoligono();
            g.setDatos(new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0)), "0x0");
            renderToImage(g, 0, 0);
        });

        testCase("GraficoPoligono", "Dimensions 10x10", () -> {
            GraficoPoligono g = new GraficoPoligono();
            g.setDatos(new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0)), "10x10");
            renderToImage(g, 10, 10);
        });

        testCase("GraficoPoligono", "Dimensions 1000x1000", () -> {
            GraficoPoligono g = new GraficoPoligono();
            g.setDatos(new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0)), "1000x1000");
            renderToImage(g, 1000, 1000);
        });

        // ─────────────────────────────────────────────────────────────────────────────
        // 3. GraficoBoxplot
        // ─────────────────────────────────────────────────────────────────────────────
        System.out.println("\n--- Testing GraficoBoxplot ---");
        testCase("GraficoBoxplot", "Null dataset", () -> {
            GraficoBoxplot g = new GraficoBoxplot();
            g.setDatos(null, "Null Data");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoBoxplot", "Empty dataset", () -> {
            GraficoBoxplot g = new GraficoBoxplot();
            g.setDatos(new ArrayList<>(), "Empty");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoBoxplot", "Single value [15.0]", () -> {
            GraficoBoxplot g = new GraficoBoxplot();
            g.setDatos(new ArrayList<>(Arrays.asList(15.0)), "Single");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoBoxplot", "Identical values [8.0, 8.0, 8.0, 8.0]", () -> {
            GraficoBoxplot g = new GraficoBoxplot();
            g.setDatos(new ArrayList<>(Arrays.asList(8.0, 8.0, 8.0, 8.0)), "Identical");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoBoxplot", "Negative values [-50.0, -20.0, -5.0]", () -> {
            GraficoBoxplot g = new GraficoBoxplot();
            g.setDatos(new ArrayList<>(Arrays.asList(-50.0, -20.0, -5.0)), "Negative");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoBoxplot", "Dimensions 0x0", () -> {
            GraficoBoxplot g = new GraficoBoxplot();
            g.setDatos(new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0)), "0x0");
            renderToImage(g, 0, 0);
        });

        testCase("GraficoBoxplot", "Dimensions 10x10", () -> {
            GraficoBoxplot g = new GraficoBoxplot();
            g.setDatos(new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0)), "10x10");
            renderToImage(g, 10, 10);
        });

        testCase("GraficoBoxplot", "Dimensions 600x50 (small height)", () -> {
            GraficoBoxplot g = new GraficoBoxplot();
            g.setDatos(new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0, 10.0)), "600x50");
            renderToImage(g, 600, 50);
        });

        testCase("GraficoBoxplot", "Dimensions 1000x1000", () -> {
            GraficoBoxplot g = new GraficoBoxplot();
            g.setDatos(new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0, 40.0)), "1000x1000");
            renderToImage(g, 1000, 1000);
        });

        // ─────────────────────────────────────────────────────────────────────────────
        // 4. GraficoOjiva
        // ─────────────────────────────────────────────────────────────────────────────
        System.out.println("\n--- Testing GraficoOjiva ---");
        testCase("GraficoOjiva", "Null dataset", () -> {
            GraficoOjiva g = new GraficoOjiva();
            g.setDatos(null, "Null Data");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoOjiva", "Empty dataset", () -> {
            GraficoOjiva g = new GraficoOjiva();
            g.setDatos(new ArrayList<>(), "Empty");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoOjiva", "Single value [20.0]", () -> {
            GraficoOjiva g = new GraficoOjiva();
            g.setDatos(new ArrayList<>(Arrays.asList(20.0)), "Single");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoOjiva", "Identical values [12.0, 12.0, 12.0]", () -> {
            GraficoOjiva g = new GraficoOjiva();
            g.setDatos(new ArrayList<>(Arrays.asList(12.0, 12.0, 12.0)), "Identical");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoOjiva", "Negative values [-40.0, -20.0, -5.0]", () -> {
            GraficoOjiva g = new GraficoOjiva();
            g.setDatos(new ArrayList<>(Arrays.asList(-40.0, -20.0, -5.0)), "Negative");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoOjiva", "Dimensions 0x0", () -> {
            GraficoOjiva g = new GraficoOjiva();
            g.setDatos(new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0)), "0x0");
            renderToImage(g, 0, 0);
        });

        testCase("GraficoOjiva", "Dimensions 10x10", () -> {
            GraficoOjiva g = new GraficoOjiva();
            g.setDatos(new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0)), "10x10");
            renderToImage(g, 10, 10);
        });

        testCase("GraficoOjiva", "Dimensions 1000x1000", () -> {
            GraficoOjiva g = new GraficoOjiva();
            g.setDatos(new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0)), "1000x1000");
            renderToImage(g, 1000, 1000);
        });

        // ─────────────────────────────────────────────────────────────────────────────
        // 5. GraficoBarras
        // ─────────────────────────────────────────────────────────────────────────────
        System.out.println("\n--- Testing GraficoBarras ---");
        testCase("GraficoBarras", "Null dataset", () -> {
            GraficoBarras g = new GraficoBarras();
            g.setDatos(null, "Null Data");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoBarras", "Empty dataset", () -> {
            GraficoBarras g = new GraficoBarras();
            g.setDatos(new LinkedHashMap<>(), "Empty");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoBarras", "All values are 0", () -> {
            GraficoBarras g = new GraficoBarras();
            LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
            map.put("A", 0);
            map.put("B", 0);
            map.put("C", 0);
            g.setDatos(map, "All Zeros");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoBarras", "Negative values", () -> {
            GraficoBarras g = new GraficoBarras();
            LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
            map.put("A", -5);
            map.put("B", 10);
            g.setDatos(map, "Negative");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoBarras", "Null key in map", () -> {
            GraficoBarras g = new GraficoBarras();
            LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
            map.put(null, 10);
            map.put("Valid", 20);
            g.setDatos(map, "Null Key");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoBarras", "High cardinality (>8 categories)", () -> {
            GraficoBarras g = new GraficoBarras();
            LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
            for (int i = 0; i < 20; i++) map.put("Cat " + i, i * 2);
            g.setDatos(map, "High Cardinality");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoBarras", "Dimensions 0x0", () -> {
            GraficoBarras g = new GraficoBarras();
            LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
            map.put("A", 10);
            g.setDatos(map, "0x0");
            renderToImage(g, 0, 0);
        });

        testCase("GraficoBarras", "Dimensions 10x10", () -> {
            GraficoBarras g = new GraficoBarras();
            LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
            map.put("A", 10);
            g.setDatos(map, "10x10");
            renderToImage(g, 10, 10);
        });

        testCase("GraficoBarras", "Dimensions 1000x1000", () -> {
            GraficoBarras g = new GraficoBarras();
            LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
            map.put("A", 10);
            map.put("B", 20);
            g.setDatos(map, "1000x1000");
            renderToImage(g, 1000, 1000);
        });

        // ─────────────────────────────────────────────────────────────────────────────
        // 6. GraficoPastel
        // ─────────────────────────────────────────────────────────────────────────────
        System.out.println("\n--- Testing GraficoPastel ---");
        testCase("GraficoPastel", "Null dataset", () -> {
            GraficoPastel g = new GraficoPastel();
            g.setDatos(null, "Null Data");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoPastel", "Empty dataset", () -> {
            GraficoPastel g = new GraficoPastel();
            g.setDatos(new LinkedHashMap<>(), "Empty");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoPastel", "All values are 0 (total == 0)", () -> {
            GraficoPastel g = new GraficoPastel();
            LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
            map.put("A", 0);
            map.put("B", 0);
            g.setDatos(map, "All Zeros");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoPastel", "Negative values in map", () -> {
            GraficoPastel g = new GraficoPastel();
            LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
            map.put("A", -10);
            map.put("B", 20);
            g.setDatos(map, "Negative");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoPastel", "Null key in map", () -> {
            GraficoPastel g = new GraficoPastel();
            LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
            map.put(null, 15);
            map.put("Valid", 25);
            g.setDatos(map, "Null Key");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoPastel", "High cardinality (>6 categories)", () -> {
            GraficoPastel g = new GraficoPastel();
            LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
            for (int i = 0; i < 15; i++) map.put("Cat " + i, i + 1);
            g.setDatos(map, "High Cardinality");
            renderToImage(g, 600, 400);
        });

        testCase("GraficoPastel", "Dimensions 0x0", () -> {
            GraficoPastel g = new GraficoPastel();
            LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
            map.put("A", 10);
            g.setDatos(map, "0x0");
            renderToImage(g, 0, 0);
        });

        testCase("GraficoPastel", "Dimensions 10x10", () -> {
            GraficoPastel g = new GraficoPastel();
            LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
            map.put("A", 10);
            g.setDatos(map, "10x10");
            renderToImage(g, 10, 10);
        });

        testCase("GraficoPastel", "Dimensions 1000x1000", () -> {
            GraficoPastel g = new GraficoPastel();
            LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
            map.put("A", 10);
            map.put("B", 30);
            g.setDatos(map, "1000x1000");
            renderToImage(g, 1000, 1000);
        });

        // ─────────────────────────────────────────────────────────────────────────────
        // 7. GraficoRadar
        // ─────────────────────────────────────────────────────────────────────────────
        System.out.println("\n--- Testing GraficoRadar ---");
        testCase("GraficoRadar", "Null dataset", () -> {
            GraficoRadar g = new GraficoRadar();
            g.setDatos(null, null, null, 100.0);
            renderToImage(g, 600, 400);
        });

        testCase("GraficoRadar", "Empty dimensions", () -> {
            GraficoRadar g = new GraficoRadar();
            g.setDatos(new ArrayList<>(), new ArrayList<>(), "Serie", 100.0);
            renderToImage(g, 600, 400);
        });

        testCase("GraficoRadar", "Less than 3 dimensions (<3 categories)", () -> {
            GraficoRadar g = new GraficoRadar();
            ArrayList<String> dims = new ArrayList<>(Arrays.asList("Dim 1", "Dim 2"));
            ArrayList<Double> vals = new ArrayList<>(Arrays.asList(10.0, 20.0));
            g.setDatos(dims, vals, "Serie", 100.0);
            renderToImage(g, 600, 400);
        });

        testCase("GraficoRadar", "Null elements in valores list", () -> {
            GraficoRadar g = new GraficoRadar();
            ArrayList<String> dims = new ArrayList<>(Arrays.asList("D1", "D2", "D3"));
            ArrayList<Double> vals = new ArrayList<>();
            vals.add(50.0);
            vals.add(null);
            vals.add(75.0);
            g.setDatos(dims, vals, "Serie", 100.0);
            renderToImage(g, 600, 400);
        });

        testCase("GraficoRadar", "Null elements in dimensiones list", () -> {
            GraficoRadar g = new GraficoRadar();
            ArrayList<String> dims = new ArrayList<>();
            dims.add("D1");
            dims.add(null);
            dims.add("D3");
            ArrayList<Double> vals = new ArrayList<>(Arrays.asList(10.0, 20.0, 30.0));
            g.setDatos(dims, vals, "Serie", 100.0);
            renderToImage(g, 600, 400);
        });

        testCase("GraficoRadar", "NaN and Infinite values", () -> {
            GraficoRadar g = new GraficoRadar();
            ArrayList<String> dims = new ArrayList<>(Arrays.asList("D1", "D2", "D3"));
            ArrayList<Double> vals = new ArrayList<>(Arrays.asList(Double.NaN, Double.POSITIVE_INFINITY, -10.0));
            g.setDatos(dims, vals, "Serie", 100.0);
            renderToImage(g, 600, 400);
        });

        testCase("GraficoRadar", "Comparative series with nulls", () -> {
            GraficoRadar g = new GraficoRadar();
            ArrayList<String> dims = new ArrayList<>(Arrays.asList("D1", "D2", "D3", "D4"));
            ArrayList<Double> s1 = new ArrayList<>(Arrays.asList(10.0, null, 30.0, 40.0));
            ArrayList<Double> s2 = new ArrayList<>(Arrays.asList(null, 20.0, 30.0, null));
            g.setDatosComparativos(dims, s1, "S1", s2, "S2", 100.0);
            renderToImage(g, 600, 400);
        });

        testCase("GraficoRadar", "Dimensions 0x0", () -> {
            GraficoRadar g = new GraficoRadar();
            ArrayList<String> dims = new ArrayList<>(Arrays.asList("D1", "D2", "D3"));
            ArrayList<Double> vals = new ArrayList<>(Arrays.asList(10.0, 20.0, 30.0));
            g.setDatos(dims, vals, "Serie", 100.0);
            renderToImage(g, 0, 0);
        });

        testCase("GraficoRadar", "Dimensions 10x10", () -> {
            GraficoRadar g = new GraficoRadar();
            ArrayList<String> dims = new ArrayList<>(Arrays.asList("D1", "D2", "D3"));
            ArrayList<Double> vals = new ArrayList<>(Arrays.asList(10.0, 20.0, 30.0));
            g.setDatos(dims, vals, "Serie", 100.0);
            renderToImage(g, 10, 10);
        });

        testCase("GraficoRadar", "Dimensions 1000x1000", () -> {
            GraficoRadar g = new GraficoRadar();
            ArrayList<String> dims = new ArrayList<>(Arrays.asList("D1", "D2", "D3"));
            ArrayList<Double> vals = new ArrayList<>(Arrays.asList(10.0, 20.0, 30.0));
            g.setDatos(dims, vals, "Serie", 100.0);
            renderToImage(g, 1000, 1000);
        });

        // ─────────────────────────────────────────────────────────────────────────────
        // 8. HeatmapTableCellRenderer
        // ─────────────────────────────────────────────────────────────────────────────
        System.out.println("\n--- Testing HeatmapTableCellRenderer ---");
        testCase("HeatmapTableCellRenderer", "Empty table 0x0", () -> {
            HeatmapTableCellRenderer renderer = new HeatmapTableCellRenderer();
            JTable table = new JTable(new DefaultTableModel(0, 0));
            renderer.recalcularMaximo(table);
            // Try rendering a cell
            renderer.getTableCellRendererComponent(table, null, false, false, 0, 0);
        });

        testCase("HeatmapTableCellRenderer", "Null cell value and non-number strings", () -> {
            HeatmapTableCellRenderer renderer = new HeatmapTableCellRenderer();
            DefaultTableModel model = new DefaultTableModel(new Object[]{"Cat", "Col1", "Col2", "TOTAL"}, 0);
            model.addRow(new Object[]{"Row1", null, "NotANumber", 10});
            model.addRow(new Object[]{"TOTAL", 5, 5, 10});
            JTable table = new JTable(model);
            renderer.recalcularMaximo(table);
            for (int r = 0; r < table.getRowCount(); r++) {
                for (int c = 0; c < table.getColumnCount(); c++) {
                    renderer.getTableCellRendererComponent(table, table.getValueAt(r, c), false, false, r, c);
                }
            }
        });

        testCase("HeatmapTableCellRenderer", "Negative value cell (-15)", () -> {
            HeatmapTableCellRenderer renderer = new HeatmapTableCellRenderer();
            DefaultTableModel model = new DefaultTableModel(new Object[]{"Cat", "Col1", "TOTAL"}, 0);
            model.addRow(new Object[]{"Row1", -15, 10});
            JTable table = new JTable(model);
            renderer.recalcularMaximo(table);
            renderer.getTableCellRendererComponent(table, -15, false, false, 0, 1);
        });

        testCase("HeatmapTableCellRenderer", "Table with 2 columns without TOTAL (only 1 data column)", () -> {
            HeatmapTableCellRenderer renderer = new HeatmapTableCellRenderer();
            DefaultTableModel model = new DefaultTableModel(new Object[]{"Categoria", "Frecuencia"}, 0);
            model.addRow(new Object[]{"Informatica", 50});
            model.addRow(new Object[]{"Civil", 30});
            JTable table = new JTable(model);
            renderer.recalcularMaximo(table);
            // Check if column 1 was treated as TOTAL or if max was recalculated
            Component c = renderer.getTableCellRendererComponent(table, 50, false, false, 0, 1);
            JComponent jc = (JComponent) c;
            if (jc.getToolTipText() != null && jc.getToolTipText().startsWith("Total marginal")) {
                throw new AssertionError("Column 'Frecuencia' was incorrectly treated as Total Marginal column!");
            }
        });

        testCase("HeatmapTableCellRenderer", "RecalcularMaximo including TOTAL row as max", () -> {
            HeatmapTableCellRenderer renderer = new HeatmapTableCellRenderer();
            DefaultTableModel model = new DefaultTableModel(new Object[]{"Cat", "A", "B", "TOTAL"}, 0);
            model.addRow(new Object[]{"Fila1", 10, 20, 30});
            model.addRow(new Object[]{"Fila2", 15, 25, 40});
            model.addRow(new Object[]{"TOTAL", 25, 45, 70});
            JTable table = new JTable(model);
            renderer.recalcularMaximo(table);
            // Data maximum across actual cells (Fila1 and Fila2, columns A and B) is 25!
            // If max is 45, it included the TOTAL row!
            try {
                java.lang.reflect.Field f = renderer.getClass().getDeclaredField("maxFrecuencia");
                f.setAccessible(true);
                int maxFrec = (int) f.get(renderer);
                if (maxFrec == 45) {
                    throw new AssertionError("recalcularMaximo included TOTAL row in max calculation (expected max 25, found 45)!");
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("\n================================================================================");
        System.out.printf("  STRESS TEST SUMMARY: %d PASSED, %d FAILED (out of %d total tests)%n",
                totalPassed, totalFailed, totalPassed + totalFailed);
        System.out.println("================================================================================");
    }
}
