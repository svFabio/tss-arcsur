package Vista;

import Modelo.Estadistica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Gráfico de Diagrama de Caja (Boxplot) según la regla de Tukey (1.5·RIQ).
 * Dibuja caja intercuartílica (Q1 a Q3), línea de la mediana, bigotes hasta el mínimo y máximo
 * no atípicos, y destaca valores atípicos con rombos rojos y sus valores numéricos exactos.
 * Incluye leyenda y resumen estadístico completo (Q1, Me, Q3, RIQ, límites y atípicos).
 */
public class GraficoBoxplot extends JPanel {

    private ArrayList<Double> datos;
    private String titulo;
    private double q1;
    private double mediana;
    private double q3;
    private double riq;
    private double limiteInferior;
    private double limiteSuperior;
    private double minNoAtipico;
    private double maxNoAtipico;
    private ArrayList<Double> atipicos;
    private double minGlobal;
    private double maxGlobal;
    private int nTotal;

    private Rectangle boxBounds;
    private ArrayList<Point> outlierPoints;

    public GraficoBoxplot() {
        this.datos = new ArrayList<>();
        this.titulo = "";
        setBackground(Color.WHITE);
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    public void setDatos(ArrayList<Double> datosOriginales, String titulo) {
        this.titulo = titulo != null ? titulo : "";
        this.datos = new ArrayList<>();

        if (datosOriginales != null) {
            for (Double d : datosOriginales) {
                if (d != null && !d.isNaN() && !d.isInfinite()) {
                    this.datos.add(d);
                }
            }
        }

        calcularEstadisticas();
        repaint();
    }

    private void calcularEstadisticas() {
        if (datos == null || datos.isEmpty()) {
            atipicos = null;
            nTotal = 0;
            return;
        }

        nTotal = datos.size();
        ArrayList<Double> ordenados = new ArrayList<>(datos);
        Collections.sort(ordenados);

        minGlobal = ordenados.get(0);
        maxGlobal = ordenados.get(ordenados.size() - 1);

        double[] cuartiles = Estadistica.cuartiles(ordenados);
        q1 = cuartiles[0];
        mediana = cuartiles[1];
        q3 = cuartiles[2];
        riq = Estadistica.rangoIntercuartil(ordenados);

        double[] limites = Estadistica.limitesTukey(ordenados);
        limiteInferior = limites[0];
        limiteSuperior = limites[1];

        atipicos = Estadistica.obtenerAtipicos(ordenados);

        // Identificar mínimo y máximo no atípicos (dentro de [LI, LS])
        ArrayList<Double> noAtipicos = new ArrayList<>();
        for (Double d : ordenados) {
            if (d >= limiteInferior && d <= limiteSuperior) {
                noAtipicos.add(d);
            }
        }

        if (!noAtipicos.isEmpty()) {
            minNoAtipico = Collections.min(noAtipicos);
            maxNoAtipico = Collections.max(noAtipicos);
        } else {
            minNoAtipico = minGlobal;
            maxNoAtipico = maxGlobal;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        if (datos == null || datos.isEmpty()) {
            g2d.setColor(new Color(140, 140, 150));
            g2d.setFont(new Font("SansSerif", Font.PLAIN, ConfiguracionTextos.TAMANO_FUENTE_NORMAL));
            String msg = "Sin datos cuantitativos para graficar";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
            return;
        }

        int paddingLeft = 65;
        int paddingRight = 65;
        int paddingTop = 58;
        int paddingBottom = 130;

        int plotW = w - paddingLeft - paddingRight;
        int areaPlotH = h - paddingTop - paddingBottom;
        if (plotW <= 0 || areaPlotH <= 30) return;

        // Título principal
        g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
        g2d.setFont(new Font("SansSerif", Font.BOLD, ConfiguracionTextos.TAMANO_FUENTE_TITULO));
        FontMetrics fmTitulo = g2d.getFontMetrics();
        g2d.drawString(titulo, (w - fmTitulo.stringWidth(titulo)) / 2, 26);

        // Subtítulo
        g2d.setFont(new Font("SansSerif", Font.PLAIN, ConfiguracionTextos.TAMANO_FUENTE_PEQUENA));
        g2d.setColor(new Color(110, 115, 125));
        String subtitulo = String.format(Locale.US,
                "Diagrama de Caja y Bigotes (Criterio de Tukey: LI = Q1 - 1.5·RIQ, LS = Q3 + 1.5·RIQ, N = %d)",
                nTotal);
        FontMetrics fmSub = g2d.getFontMetrics();
        g2d.drawString(subtitulo, (w - fmSub.stringWidth(subtitulo)) / 2, 44);

        // Escala del eje X
        double escalaMin = Math.min(0.0, minGlobal);
        double escalaMax = Math.max(maxGlobal, limiteSuperior);
        // Redondear escalaMax al múltiplo de 5 superior para margen visual
        escalaMax = Math.ceil(escalaMax / 5.0) * 5.0;
        if (escalaMax - escalaMin < 1.0) escalaMax = escalaMin + 1.0;

        double rango = escalaMax - escalaMin;

        // Coordenadas verticales de la caja
        int centerY = paddingTop + areaPlotH / 2;
        int boxH = Math.min(60, Math.max(36, areaPlotH / 2));
        int topY = centerY - boxH / 2;

        // Conversión a coordenadas de pantalla X
        int xMin = paddingLeft + (int) Math.round(((minNoAtipico - escalaMin) / rango) * plotW);
        int xQ1 = paddingLeft + (int) Math.round(((q1 - escalaMin) / rango) * plotW);
        int xMed = paddingLeft + (int) Math.round(((mediana - escalaMin) / rango) * plotW);
        int xQ3 = paddingLeft + (int) Math.round(((q3 - escalaMin) / rango) * plotW);
        int xMax = paddingLeft + (int) Math.round(((maxNoAtipico - escalaMin) / rango) * plotW);

        boxBounds = new Rectangle(xQ1, topY, Math.max(4, xQ3 - xQ1), boxH);

        // Línea de referencia del Límite Superior de Tukey (si cabe en el gráfico)
        if (limiteSuperior <= escalaMax) {
            int xFenceLS = paddingLeft + (int) Math.round(((limiteSuperior - escalaMin) / rango) * plotW);
            Stroke strokeFence = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4.0f, 4.0f}, 0.0f);
            g2d.setStroke(strokeFence);
            g2d.setColor(new Color(231, 76, 60, 160));
            g2d.drawLine(xFenceLS, topY - 20, xFenceLS, topY + boxH + 20);

            g2d.setFont(new Font("SansSerif", Font.BOLD, 9));
            String lblFence = String.format(Locale.US, "LS = %.1f", limiteSuperior);
            FontMetrics fmFence = g2d.getFontMetrics();
            g2d.drawString(lblFence, xFenceLS - fmFence.stringWidth(lblFence) / 2, topY - 24);
        }

        // Bigotes (Whiskers)
        g2d.setStroke(new BasicStroke(1.8f));
        g2d.setColor(ConfiguracionColores.COLOR_TEXTO);

        // Bigote izquierdo: desde minNoAtipico hasta Q1
        g2d.drawLine(xMin, centerY, xQ1, centerY);
        g2d.drawLine(xMin, centerY - 14, xMin, centerY + 14); // T-bar izquierda

        // Bigote derecho: desde Q3 hasta maxNoAtipico
        g2d.drawLine(xQ3, centerY, xMax, centerY);
        g2d.drawLine(xMax, centerY - 14, xMax, centerY + 14); // T-bar derecha

        // Relleno de la Caja (Q1 a Q3)
        Paint gradienteCaja = new GradientPaint(
                xQ1, topY, new Color(41, 128, 185, 175),
                xQ1, topY + boxH, new Color(41, 128, 185, 230)
        );
        g2d.setPaint(gradienteCaja);
        g2d.fillRect(xQ1, topY, Math.max(2, xQ3 - xQ1), boxH);

        // Borde de la caja
        g2d.setColor(new Color(24, 76, 110));
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawRect(xQ1, topY, Math.max(2, xQ3 - xQ1), boxH);

        // Línea de la Mediana
        g2d.setColor(new Color(231, 76, 60)); // Rojo distintivo
        g2d.setStroke(new BasicStroke(3.2f));
        g2d.drawLine(xMed, topY, xMed, topY + boxH);

        // Valores Atípicos (Outliers)
        outlierPoints = new ArrayList<>();
        if (atipicos != null && !atipicos.isEmpty()) {
            for (double valAtipico : atipicos) {
                int xOut = paddingLeft + (int) Math.round(((valAtipico - escalaMin) / rango) * plotW);
                outlierPoints.add(new Point(xOut, centerY));

                // Rombo rojo
                Polygon rombo = new Polygon();
                rombo.addPoint(xOut, centerY - 7);
                rombo.addPoint(xOut + 7, centerY);
                rombo.addPoint(xOut, centerY + 7);
                rombo.addPoint(xOut - 7, centerY);

                g2d.setColor(new Color(231, 76, 60));
                g2d.fill(rombo);
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(1.2f));
                g2d.draw(rombo);

                // Etiqueta del atípico
                g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2d.setColor(new Color(192, 57, 43));
                String outStr = String.format(Locale.US, "%.1f (Atípico)", valAtipico);
                FontMetrics fmOut = g2d.getFontMetrics();
                g2d.drawString(outStr, xOut - fmOut.stringWidth(outStr) / 2, centerY - 11);
            }
        }

        // Etiquetas numéricas de los estadísticos encima y debajo de la caja
        g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
        FontMetrics fmStats = g2d.getFontMetrics();

        // Min no atípico
        g2d.setColor(new Color(80, 85, 95));
        String minStr = String.format(Locale.US, "Mín: %.1f", minNoAtipico);
        g2d.drawString(minStr, xMin - fmStats.stringWidth(minStr) / 2, topY - 7);

        // Q1
        g2d.setColor(new Color(41, 128, 185));
        String q1Str = String.format(Locale.US, "Q1: %.2f", q1);
        g2d.drawString(q1Str, xQ1 - fmStats.stringWidth(q1Str) / 2, topY - 7);

        // Mediana
        g2d.setColor(new Color(231, 76, 60));
        String medStr = String.format(Locale.US, "Me: %.2f", mediana);
        g2d.drawString(medStr, xMed - fmStats.stringWidth(medStr) / 2, topY + boxH + 16);

        // Q3
        g2d.setColor(new Color(41, 128, 185));
        String q3Str = String.format(Locale.US, "Q3: %.2f", q3);
        g2d.drawString(q3Str, xQ3 - fmStats.stringWidth(q3Str) / 2, topY - 7);

        // Max no atípico
        g2d.setColor(new Color(80, 85, 95));
        String maxStr = String.format(Locale.US, "Máx: %.1f", maxNoAtipico);
        g2d.drawString(maxStr, xMax - fmStats.stringWidth(maxStr) / 2, topY - 7);

        // Eje X inferior con marcas de escala regulares
        int yAxis = topY + boxH + 32;
        g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawLine(paddingLeft, yAxis, paddingLeft + plotW, yAxis);

        int pasoTick = 5;
        if (rango > 50) pasoTick = 10;
        else if (rango <= 15) pasoTick = 2;

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
        FontMetrics fmTicks = g2d.getFontMetrics();

        for (double v = escalaMin; v <= escalaMax + 1e-9; v += pasoTick) {
            int xTick = paddingLeft + (int) Math.round(((v - escalaMin) / rango) * plotW);
            g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
            g2d.drawLine(xTick, yAxis, xTick, yAxis + 5);

            String valTickStr = String.format(Locale.US, "%.0f", v);
            g2d.drawString(valTickStr, xTick - fmTicks.stringWidth(valTickStr) / 2, yAxis + 18);
        }

        // Tarjeta resumen estadístico en la parte inferior
        int yCard = h - 65;
        int cardH = 52;
        g2d.setColor(new Color(248, 249, 251));
        g2d.fillRoundRect(paddingLeft, yCard, plotW, cardH, 8, 8);
        g2d.setColor(new Color(220, 225, 232));
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.drawRoundRect(paddingLeft, yCard, plotW, cardH, 8, 8);

        // Contenido de la tarjeta resumen
        g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
        g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
        g2d.drawString("RESUMEN DE MEDIDAS DE POSICIÓN Y DISPERSIÓN:", paddingLeft + 12, yCard + 16);

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
        String fila1 = String.format(Locale.US,
                "Q1 = %.2f  |  Mediana (Q2) = %.2f  |  Q3 = %.2f  |  RIQ = %.2f  |  Límites Tukey = [%.2f – %.2f]",
                q1, mediana, q3, riq, limiteInferior, limiteSuperior);
        g2d.drawString(fila1, paddingLeft + 12, yCard + 32);

        String fila2 = String.format(Locale.US,
                "Bigotes = [%.2f – %.2f]  |  Valores Atípicos = %d %s  |  Muestra Válida N = %d",
                minNoAtipico, maxNoAtipico,
                atipicos != null ? atipicos.size() : 0,
                (atipicos != null && !atipicos.isEmpty()) ? atipicos.toString() : "(Ninguno)",
                nTotal);
        g2d.setColor(new Color(100, 105, 115));
        g2d.drawString(fila2, paddingLeft + 12, yCard + 46);
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        if (datos == null || datos.isEmpty()) return null;
        Point p = e.getPoint();

        if (outlierPoints != null) {
            for (int i = 0; i < outlierPoints.size(); i++) {
                Point pt = outlierPoints.get(i);
                if (pt.distance(p) <= 10) {
                    double val = (atipicos != null && i < atipicos.size()) ? atipicos.get(i) : 0;
                    String causa = (val > limiteSuperior)
                            ? String.format(Locale.US, "Mayor al Límite Superior de Tukey (LS = %.2f)", limiteSuperior)
                            : String.format(Locale.US, "Menor al Límite Inferior de Tukey (LI = %.2f)", limiteInferior);
                    return String.format(Locale.US,
                            "<html><div style='padding:4px; font-family:sans-serif;'>" +
                                    "<b>Valor Atípico (Outlier):</b> %.2f<br>" +
                                    "<b>Causa:</b> %s<br>" +
                                    "<b>Estado:</b> Observación legítima conservada en análisis" +
                                    "</div></html>", val, causa);
                }
            }
        }

        if (boxBounds != null && boxBounds.contains(p)) {
            return String.format(Locale.US,
                    "<html><div style='padding:4px; font-family:sans-serif;'>" +
                            "<b>Caja Intercuartílica (50%% central de los datos)</b><br>" +
                            "• Cuartil 1 (Q1): %.2f<br>" +
                            "• Mediana (Me): %.2f<br>" +
                            "• Cuartil 3 (Q3): %.2f<br>" +
                            "• Rango Intercuartílico (RIQ): %.2f" +
                            "</div></html>", q1, mediana, q3, riq);
        }

        return null;
    }
}
