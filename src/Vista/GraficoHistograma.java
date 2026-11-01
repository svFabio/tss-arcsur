package Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Gráfico de Histograma para variables cuantitativas.
 * Calcula automáticamente 6 intervalos de amplitud constante según la regla de Sturges simplificada.
 * Dibuja barras contiguas adyacentes (sin separación), marcas de clase y límites de intervalos en el eje X,
 * y escala de frecuencia en el eje Y.
 */
public class GraficoHistograma extends JPanel {

    private ArrayList<Double> datos;
    private String titulo;
    private int k = 6;
    private double[] limites;
    private double[] marcasClase;
    private int[] frecuencias;
    private double[] frecuenciasRelativas;
    private int nTotal;
    private double min;
    private double max;
    private double anchoIntervalo;
    private Rectangle[] barBounds;

    public GraficoHistograma() {
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

        calcularIntervalosYFrecuencias();
        repaint();
    }

    private void calcularIntervalosYFrecuencias() {
        if (datos == null || datos.isEmpty()) {
            limites = null;
            marcasClase = null;
            frecuencias = null;
            frecuenciasRelativas = null;
            barBounds = null;
            nTotal = 0;
            return;
        }

        nTotal = datos.size();
        min = Collections.min(datos);
        max = Collections.max(datos);

        k = 6;
        limites = new double[k + 1];
        marcasClase = new double[k];
        frecuencias = new int[k];
        frecuenciasRelativas = new double[k];
        barBounds = new Rectangle[k];

        if (max == min) {
            anchoIntervalo = 1.0;
            for (int i = 0; i <= k; i++) {
                limites[i] = min + i * anchoIntervalo;
            }
        } else {
            anchoIntervalo = (max - min) / (double) k;
            for (int i = 0; i < k; i++) {
                limites[i] = min + i * anchoIntervalo;
            }
            limites[k] = max;
        }

        for (int i = 0; i < k; i++) {
            marcasClase[i] = (limites[i] + limites[i + 1]) / 2.0;
        }

        for (double val : datos) {
            if (val <= limites[1]) {
                frecuencias[0]++;
            } else if (val <= limites[2]) {
                frecuencias[1]++;
            } else if (val <= limites[3]) {
                frecuencias[2]++;
            } else if (val <= limites[4]) {
                frecuencias[3]++;
            } else if (val <= limites[5]) {
                frecuencias[4]++;
            } else {
                frecuencias[5]++;
            }
        }

        for (int i = 0; i < k; i++) {
            frecuenciasRelativas[i] = nTotal > 0 ? (frecuencias[i] * 100.0 / nTotal) : 0.0;
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

        if (datos == null || datos.isEmpty() || frecuencias == null) {
            g2d.setColor(new Color(140, 140, 150));
            g2d.setFont(new Font("SansSerif", Font.PLAIN, ConfiguracionTextos.TAMANO_FUENTE_NORMAL));
            String msg = "Sin datos cuantitativos para graficar";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
            return;
        }

        int paddingLeft = 60;
        int paddingRight = 35;
        int paddingTop = 58;
        int paddingBottom = 68;

        int plotW = w - paddingLeft - paddingRight;
        int plotH = h - paddingTop - paddingBottom;
        int yBase = paddingTop + plotH;

        if (plotW <= 0 || plotH <= 0) return;

        // Título principal
        g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
        g2d.setFont(new Font("SansSerif", Font.BOLD, ConfiguracionTextos.TAMANO_FUENTE_TITULO));
        FontMetrics fmTitulo = g2d.getFontMetrics();
        g2d.drawString(titulo, (w - fmTitulo.stringWidth(titulo)) / 2, 26);

        // Subtítulo con parámetros técnicos
        g2d.setFont(new Font("SansSerif", Font.PLAIN, ConfiguracionTextos.TAMANO_FUENTE_PEQUENA));
        g2d.setColor(new Color(110, 115, 125));
        String subtitulo = String.format(Locale.US,
                "Histograma de Frecuencias (k = %d clases, c = %.2f, N = %d, Rango = [%.2f – %.2f])",
                k, anchoIntervalo, nTotal, min, max);
        FontMetrics fmSub = g2d.getFontMetrics();
        g2d.drawString(subtitulo, (w - fmSub.stringWidth(subtitulo)) / 2, 44);

        // Escala eje Y
        int maxFi = 0;
        for (int fi : frecuencias) {
            if (fi > maxFi) maxFi = fi;
        }
        if (maxFi == 0) maxFi = 1;
        int yCeil = ((maxFi + 9) / 10) * 10;
        if (yCeil < maxFi * 1.1) yCeil += 10;

        int numTicksY = 5;
        Stroke strokeOriginal = g2d.getStroke();
        Stroke strokeDashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4.0f, 4.0f}, 0.0f);

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
        FontMetrics fmAxis = g2d.getFontMetrics();

        for (int step = 0; step <= numTicksY; step++) {
            int valY = (int) Math.round((double) yCeil * step / numTicksY);
            int yGrid = yBase - (int) Math.round(((double) valY / yCeil) * plotH);

            g2d.setColor(new Color(230, 233, 238));
            g2d.setStroke(strokeDashed);
            g2d.drawLine(paddingLeft, yGrid, paddingLeft + plotW, yGrid);

            g2d.setColor(new Color(100, 110, 120));
            g2d.setStroke(strokeOriginal);
            g2d.drawLine(paddingLeft - 4, yGrid, paddingLeft, yGrid);

            String tickLabel = String.valueOf(valY);
            g2d.drawString(tickLabel, paddingLeft - 8 - fmAxis.stringWidth(tickLabel), yGrid + 4);
        }

        // Título del eje Y
        g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2d.drawString("Frecuencia (fi)", paddingLeft - 50, paddingTop - 10);

        // Dibujo de Barras Contiguas Adyacentes (sin separación entre barras)
        barBounds = new Rectangle[k];
        Color colorBarra = new Color(41, 128, 185, 215);
        Color colorBorde = new Color(24, 76, 110);

        for (int i = 0; i < k; i++) {
            int x1 = (int) Math.round(paddingLeft + i * ((double) plotW / k));
            int x2 = (int) Math.round(paddingLeft + (i + 1) * ((double) plotW / k));
            int barW = x2 - x1;
            int barH = (int) Math.round(((double) frecuencias[i] / yCeil) * plotH);
            int y = yBase - barH;

            barBounds[i] = new Rectangle(x1, y, barW, barH);

            // Relleno de la barra
            g2d.setColor(colorBarra);
            g2d.fillRect(x1, y, barW, barH);

            // Borde contiguo
            g2d.setColor(colorBorde);
            g2d.setStroke(new BasicStroke(1.2f));
            g2d.drawRect(x1, y, barW, barH);

            // Frecuencia absoluta y relativa arriba de la barra
            g2d.setFont(new Font("SansSerif", Font.BOLD, 11));
            FontMetrics fmVal = g2d.getFontMetrics();
            String labelVal = frecuencias[i] > 0 ? String.valueOf(frecuencias[i]) : "0";
            if (barW >= 70 && frecuencias[i] > 0) {
                labelVal += String.format(Locale.US, " (%.1f%%)", frecuenciasRelativas[i]);
            }
            g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
            g2d.drawString(labelVal, x1 + (barW - fmVal.stringWidth(labelVal)) / 2, y - 5);
        }

        // Ejes X e Y principales
        g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawLine(paddingLeft, yBase, paddingLeft + plotW, yBase); // Eje X
        g2d.drawLine(paddingLeft, yBase, paddingLeft, paddingTop);     // Eje Y

        // Marcas y etiquetas en eje X
        for (int i = 0; i <= k; i++) {
            int xTick = (int) Math.round(paddingLeft + i * ((double) plotW / k));
            g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
            g2d.drawLine(xTick, yBase, xTick, yBase + 5);

            // Límite numérico del intervalo
            String limitStr = String.format(Locale.US, "%.1f", limites[i]);
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
            FontMetrics fmLim = g2d.getFontMetrics();
            g2d.drawString(limitStr, xTick - fmLim.stringWidth(limitStr) / 2, yBase + 16);
        }

        // Marcas de clase (Mc) e Intervalo debajo de cada barra
        for (int i = 0; i < k; i++) {
            int x1 = (int) Math.round(paddingLeft + i * ((double) plotW / k));
            int x2 = (int) Math.round(paddingLeft + (i + 1) * ((double) plotW / k));
            int xCenter = (x1 + x2) / 2;

            g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
            g2d.setColor(new Color(41, 128, 185));
            String mcStr = String.format(Locale.US, "Mc=%.2f", marcasClase[i]);
            FontMetrics fmMc = g2d.getFontMetrics();
            g2d.drawString(mcStr, xCenter - fmMc.stringWidth(mcStr) / 2, yBase + 32);

            g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2d.setColor(new Color(100, 105, 115));
            String intDesc = (i == 0 ? "[" : "(") + String.format(Locale.US, "%.1f", limites[i]) +
                    " – " + String.format(Locale.US, "%.1f", limites[i + 1]) + "]";
            FontMetrics fmDesc = g2d.getFontMetrics();
            g2d.drawString(intDesc, xCenter - fmDesc.stringWidth(intDesc) / 2, yBase + 46);
        }

        // Etiqueta general eje X
        g2d.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
        String labelEjeX = "Límites e Intervalos de Clase";
        FontMetrics fmX = g2d.getFontMetrics();
        g2d.drawString(labelEjeX, paddingLeft + (plotW - fmX.stringWidth(labelEjeX)) / 2, yBase + 62);
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        if (barBounds == null || frecuencias == null) return null;
        Point p = e.getPoint();
        for (int i = 0; i < k; i++) {
            if (barBounds[i] != null && barBounds[i].contains(p)) {
                String corchete = i == 0 ? "[" : "(";
                return String.format(Locale.US,
                        "<html><div style='padding:4px; font-family:sans-serif;'>" +
                                "<b>Intervalo %d:</b> %s%.2f – %.2f]<br>" +
                                "<b>Marca de clase (Mc):</b> %.2f<br>" +
                                "<b>Frecuencia absoluta (fi):</b> %d<br>" +
                                "<b>Frecuencia relativa (hi):</b> %.2f%%" +
                                "</div></html>",
                        i + 1, corchete, limites[i], limites[i + 1],
                        marcasClase[i], frecuencias[i], frecuenciasRelativas[i]);
            }
        }
        return null;
    }
}
