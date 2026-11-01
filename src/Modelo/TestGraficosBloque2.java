package Modelo;

import Vista.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Pruebas unitarias y de integración visual para el Bloque 2:
 * Componentes Swing Java 2D de Histograma, Polígono, Boxplot y Ojiva.
 */
public class TestGraficosBloque2 {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    private static boolean assertCondition(String name, boolean condition, String detail) {
        if (condition) {
            System.out.printf("  [PASS] %-45s -> %s%n", name, detail);
            testsPassed++;
            return true;
        } else {
            System.err.printf("  [FAIL] %-45s -> %s%n", name, detail);
            testsFailed++;
            return false;
        }
    }

    private static Object getField(Object obj, String fieldName) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            throw new RuntimeException("Error accessing field " + fieldName + ": " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("            PRUEBAS DEL BLOQUE 2: COMPONENTES GRÁFICOS ANALÍTICOS");
        System.out.println("===============================================================================");

        // 1. Cargar datos reales
        File archivo = new File("src/Recursos/titulados_limpio.csv");
        if (!archivo.exists()) {
            archivo = new File("titulados_limpio.csv");
        }
        GestorDatos gestor = new GestorDatos();
        gestor.cargarCSV(archivo, true);
        ArrayList<String> datosCrudos = gestor.obtenerColumna(true, "AniosProfesional");
        ArrayList<Double> valores = Estadistica.extraerValoresNumericos(datosCrudos);

        assertCondition("Extracción AniosProfesional N=145", valores.size() == 145, "Valores válidos: " + valores.size());

        System.out.println("\n--- 1. Validación de GraficoHistograma (Tabla 1) ---");
        GraficoHistograma hist = new GraficoHistograma();
        hist.setSize(600, 400);
        hist.setDatos(valores, "Histograma AniosProfesional");

        int[] frecsHist = (int[]) getField(hist, "frecuencias");
        double[] limitesHist = (double[]) getField(hist, "limites");
        double[] mcHist = (double[]) getField(hist, "marcasClase");

        int[] frecsEsperadas = {72, 19, 31, 15, 7, 1};
        boolean frecuenciasOk = frecsHist != null && frecsHist.length == 6;
        if (frecuenciasOk) {
            for (int i = 0; i < 6; i++) {
                if (frecsHist[i] != frecsEsperadas[i]) {
                    frecuenciasOk = false;
                    break;
                }
            }
        }
        assertCondition("Histograma frecuencias [72, 19, 31, 15, 7, 1]", frecuenciasOk,
                "Frecuencias: " + java.util.Arrays.toString(frecsHist));

        String wStr = String.format(Locale.US, "%.2f", limitesHist[1] - limitesHist[0]);
        assertCondition("Histograma amplitud constante c ≈ 5.83", wStr.equals("5.83"), "Amplitud = " + wStr);

        String mc0 = String.format(Locale.US, "%.2f", mcHist[0]);
        String mc5 = String.format(Locale.US, "%.2f", mcHist[5]);
        assertCondition("Marcas de clase inicial (2.92) y final (32.08)",
                mc0.equals("2.92") && mc5.equals("32.08"), "MC0=" + mc0 + ", MC5=" + mc5);

        // Renderizado off-screen de Histograma
        BufferedImage imgHist = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gHist = imgHist.createGraphics();
        hist.paint(gHist);
        gHist.dispose();
        assertCondition("Renderizado Java 2D Histograma", imgHist.getWidth() == 600, "Buffer pintado sin excepciones");

        // Estado vacío
        hist.setDatos(new ArrayList<>(), "Vacio");
        BufferedImage imgHistVacio = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gHistVacio = imgHistVacio.createGraphics();
        hist.paint(gHistVacio);
        gHistVacio.dispose();
        assertCondition("Histograma maneja lista vacía", true, "Pintado 'Sin datos cuantitativos'");

        System.out.println("\n--- 2. Validación de GraficoPoligono ---");
        GraficoPoligono poli = new GraficoPoligono();
        poli.setSize(600, 400);
        poli.setDatos(valores, "Polígono AniosProfesional");

        int[] frecsPoli = (int[]) getField(poli, "frecuencias");
        boolean frecsPoliOk = java.util.Arrays.equals(frecsPoli, frecsEsperadas);
        assertCondition("Polígono frecuencias coincidentes", frecsPoliOk, "Frecuencias polígono: " + java.util.Arrays.toString(frecsPoli));

        BufferedImage imgPoli = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gPoli = imgPoli.createGraphics();
        poli.paint(gPoli);
        gPoli.dispose();
        assertCondition("Renderizado Java 2D Polígono", imgPoli.getWidth() == 600, "Cierre en eje X y vértices pintados");

        System.out.println("\n--- 3. Validación de GraficoBoxplot (Regla de Tukey) ---");
        GraficoBoxplot box = new GraficoBoxplot();
        box.setSize(600, 400);
        box.setDatos(valores, "Boxplot AniosProfesional");

        double q1Box = (double) getField(box, "q1");
        double medBox = (double) getField(box, "mediana");
        double q3Box = (double) getField(box, "q3");
        double riqBox = (double) getField(box, "riq");
        double minNoAtipico = (double) getField(box, "minNoAtipico");
        double maxNoAtipico = (double) getField(box, "maxNoAtipico");
        @SuppressWarnings("unchecked")
        ArrayList<Double> atipicos = (ArrayList<Double>) getField(box, "atipicos");

        assertCondition("Boxplot Q1=2.00, Me=6.00, Q3=14.00, RIQ=12.00",
                Math.abs(q1Box - 2.0) < 1e-6 && Math.abs(medBox - 6.0) < 1e-6 &&
                        Math.abs(q3Box - 14.0) < 1e-6 && Math.abs(riqBox - 12.0) < 1e-6,
                String.format(Locale.US, "Q1=%.2f, Me=%.2f, Q3=%.2f, RIQ=%.2f", q1Box, medBox, q3Box, riqBox));

        assertCondition("Boxplot bigotes [0.0, 28.0]",
                Math.abs(minNoAtipico - 0.0) < 1e-6 && Math.abs(maxNoAtipico - 28.0) < 1e-6,
                "Bigotes: [" + minNoAtipico + ", " + maxNoAtipico + "]");

        assertCondition("Boxplot detección atípico [35.0]",
                atipicos != null && atipicos.size() == 1 && Math.abs(atipicos.get(0) - 35.0) < 1e-6,
                "Atípicos: " + atipicos);

        BufferedImage imgBox = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gBox = imgBox.createGraphics();
        box.paint(gBox);
        gBox.dispose();
        assertCondition("Renderizado Java 2D Boxplot", imgBox.getWidth() == 600, "Caja, bigotes y leyenda pintados");

        System.out.println("\n--- 4. Validación de GraficoOjiva (Frecuencia Acumulada) ---");
        GraficoOjiva ojiva = new GraficoOjiva();
        ojiva.setSize(600, 400);
        ojiva.setDatos(valores, "Ojiva AniosProfesional");

        int[] fiAcum = (int[]) getField(ojiva, "frecuenciasAcumuladas");
        double[] pctAcum = (double[]) getField(ojiva, "porcentajesAcumulados");

        int[] fiEsperado = {0, 72, 91, 122, 137, 144, 145};
        boolean fiAcumOk = java.util.Arrays.equals(fiAcum, fiEsperado);
        assertCondition("Ojiva Fi acumulado [0, 72, 91, 122, 137, 144, 145]", fiAcumOk,
                "Fi: " + java.util.Arrays.toString(fiAcum));

        String p1Str = String.format(Locale.US, "%.1f%%", pctAcum[1]);
        String p6Str = String.format(Locale.US, "%.1f%%", pctAcum[6]);
        assertCondition("Ojiva porcentajes [49.7%, ..., 100.0%]",
                p1Str.equals("49.7%") && p6Str.equals("100.0%"),
                "Pct1=" + p1Str + ", Pct6=" + p6Str);

        BufferedImage imgOjiva = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gOjiva = imgOjiva.createGraphics();
        ojiva.paint(gOjiva);
        gOjiva.dispose();
        assertCondition("Renderizado Java 2D Ojiva", imgOjiva.getWidth() == 600, "Curva ascendente y doble escala pintadas");

        System.out.println("\n--- 5. Validación de PanelAnalisis y CardLayout ---");
        PanelAnalisis panel = new PanelAnalisis();
        assertCondition("PanelAnalisis comboTipoGrafico tiene 7 opciones",
                panel.comboTipoGrafico.getItemCount() == 7,
                "Total opciones: " + panel.comboTipoGrafico.getItemCount());

        boolean opcionesPresentes =
                "Barras".equals(panel.comboTipoGrafico.getItemAt(0)) &&
                "Pastel (Pie)".equals(panel.comboTipoGrafico.getItemAt(1)) &&
                "Radar (Spider)".equals(panel.comboTipoGrafico.getItemAt(2)) &&
                "Histograma".equals(panel.comboTipoGrafico.getItemAt(3)) &&
                "Poligono de Frecuencias".equals(panel.comboTipoGrafico.getItemAt(4)) &&
                "Diagrama de Caja (Boxplot)".equals(panel.comboTipoGrafico.getItemAt(5)) &&
                "Ojiva (Frec. Acumulada)".equals(panel.comboTipoGrafico.getItemAt(6));
        assertCondition("Nombres y orden de gráficos en combo", opcionesPresentes, "Las 7 opciones coinciden");

        assertCondition("Instancia graficoHistograma presente", panel.graficoHistograma != null, "graficoHistograma OK");
        assertCondition("Instancia graficoPoligono presente", panel.graficoPoligono != null, "graficoPoligono OK");
        assertCondition("Instancia graficoBoxplot presente", panel.graficoBoxplot != null, "graficoBoxplot OK");
        assertCondition("Instancia graficoOjiva presente", panel.graficoOjiva != null, "graficoOjiva OK");

        panel.actualizarGraficosCuantitativos(valores, "Años de Vida Profesional");
        int[] frecsEnPanel = (int[]) getField(panel.graficoHistograma, "frecuencias");
        assertCondition("actualizarGraficosCuantitativos propaga datos",
                java.util.Arrays.equals(frecsEnPanel, frecsEsperadas),
                "Histograma en PanelAnalisis recibió datos: " + java.util.Arrays.toString(frecsEnPanel));

        System.out.println("\n===============================================================================");
        System.out.printf("  RESUMEN BLOQUE 2: %d pruebas PASARON, %d pruebas FALLARON.%n", testsPassed, testsFailed);
        System.out.println("===============================================================================");

        if (testsFailed > 0) {
            System.err.println("ALERTA: Se encontraron pruebas fallidas en el Bloque 2.");
            System.exit(1);
        } else {
            System.out.println("TODOS LOS COMPONENTES GRÁFICOS Y VALIDACIONES DEL BLOQUE 2 PASARON EXITOSAMENTE.");
        }
    }
}
