package Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Gráfico de Ojiva (Frecuencia Acumulada Ascendente).
 * Calcula frecuencias acumuladas absolutas (Fi) y porcentuales (Fi %), graficando la curva
 * ascendente sobre los límites de clase con puntos y etiquetas hasta alcanzar el 100% de la muestra.
 */
public class GraficoOjiva extends JPanel {

    private ArrayList<Double> datos;
    private String titulo;
    private int k = 6;
    private double[] limites;
    private int[] frecuencias;
    private int[] frecuenciasAcumuladas;
    private double[] porcentajesAcumulados;
    private int nTotal;
    private double min;
    private double max;
    private double anchoIntervalo;

    private Point[] puntosCurva;
    private Rectangle[] hitBoxes;

    public GraficoOjiva() {
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

        calcularOjiva();
        repaint();
    }

    private void calcularOjiva() {
        if (datos == null || datos.isEmpty()) {
            limites = null;
            frecuencias = null;
            frecuenciasAcumuladas = null;
            porcentajesAcumulados = null;
            puntosCurva = null;
            hitBoxes = null;
            nTotal = 0;
            return;
        }

        nTotal = datos.size();
        min = Collections.min(datos);
        max = Collections.max(datos);

        k = 6;
        limites = new double[k + 1];
        frecuencias = new int[k];
        frecuenciasAcumuladas = new int[k + 1];
        porcentajesAcumulados = new double[k + 1];
        puntosCurva = new Point[k + 1];
        hitBoxes = new Rectangle[k + 1];

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

        // Acumulación: punto 0 en el límite inferior con Fi = 0
        frecuenciasAcumuladas[0] = 0;
        porcentajesAcumulados[0] = 0.0;
        for (int i = 1; i <= k; i++) {
            frecuenciasAcumuladas[i] = frecuenciasAcumuladas[i - 1] + frecuencias[i - 1];
            porcentajesAcumulados[i] = nTotal > 0 ? (frecuenciasAcumuladas[i] * 100.0 / nTotal) : 0.0;
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

        if (datos == null || datos.isEmpty() || frecuenciasAcumuladas == null) {
            g2d.setColor(new Color(140, 140, 150));
            g2d.setFont(new Font("SansSerif", Font.PLAIN, ConfiguracionTextos.TAMANO_FUENTE_NORMAL));
            String msg = "Sin datos cuantitativos para graficar";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
            return;
        }

        int paddingLeft = 60;
        int paddingRight = 60;
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

        // Subtítulo
        g2d.setFont(new Font("SansSerif", Font.PLAIN, ConfiguracionTextos.TAMANO_FUENTE_PEQUENA));
        g2d.setColor(new Color(110, 115, 125));
        String subtitulo = String.format(Locale.US,
                "Ojiva Ascendente de Frecuencias Acumuladas (N = %d, k = %d intervalos)",
                nTotal, k);
        FontMetrics fmSub = g2d.getFontMetrics();
        g2d.drawString(subtitulo, (w - fmSub.stringWidth(subtitulo)) / 2, 44);

        // Doble escala Y: Izquierda Fi (0 a N), Derecha Fi % (0% a 100%)
        Stroke strokeOriginal = g2d.getStroke();
        Stroke strokeDashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4.0f, 4.0f}, 0.0f);

        int[] porcentajesGuia = {0, 25, 50, 75, 100};
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
        FontMetrics fmAxis = g2d.getFontMetrics();

        for (int pct : porcentajesGuia) {
            int yGrid = yBase - (int) Math.round((pct / 100.0) * plotH);
            int valFi = (int) Math.round((pct / 100.0) * nTotal);

            // Línea de referencia (50% destacada como mediana acumulada)
            if (pct == 50) {
                g2d.setColor(new Color(231, 76, 60, 130));
                g2d.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{6.0f, 4.0f}, 0.0f));
            } else {
                g2d.setColor(new Color(230, 233, 238));
                g2d.setStroke(strokeDashed);
            }
            g2d.drawLine(paddingLeft, yGrid, paddingLeft + plotW, yGrid);

            // Eje izquierdo: Fi
            g2d.setColor(new Color(100, 110, 120));
            g2d.setStroke(strokeOriginal);
            g2d.drawLine(paddingLeft - 4, yGrid, paddingLeft, yGrid);
            String fiStr = String.valueOf(valFi);
            g2d.drawString(fiStr, paddingLeft - 8 - fmAxis.stringWidth(fiStr), yGrid + 4);

            // Eje derecho: %
            g2d.drawLine(paddingLeft + plotW, yGrid, paddingLeft + plotW + 4, yGrid);
            String pctStr = pct + "%";
            g2d.drawString(pctStr, paddingLeft + plotW + 8, yGrid + 4);
        }

        // Títulos de los ejes Y
        g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
        g2d.drawString("Fi (Acum.)", paddingLeft - 50, paddingTop - 10);
        g2d.drawString("% Acum.", paddingLeft + plotW + 4, paddingTop - 10);

        // Coordenadas de los puntos de la curva (de 0 a k)
        double rangoTotal = limites[k] - limites[0];
        if (rangoTotal == 0) rangoTotal = 1.0;

        puntosCurva = new Point[k + 1];
        hitBoxes = new Rectangle[k + 1];

        for (int i = 0; i <= k; i++) {
            int px = paddingLeft + (int) Math.round(((limites[i] - limites[0]) / rangoTotal) * plotW);
            int py = yBase - (int) Math.round((porcentajesAcumulados[i] / 100.0) * plotH);
            puntosCurva[i] = new Point(px, py);
            hitBoxes[i] = new Rectangle(px - 12, py - 12, 24, 24);
        }

        // Área bajo la curva con gradiente verde esmeralda suave
        Polygon polyArea = new Polygon();
        polyArea.addPoint(puntosCurva[0].x, yBase);
        for (int i = 0; i <= k; i++) {
            polyArea.addPoint(puntosCurva[i].x, puntosCurva[i].y);
        }
        polyArea.addPoint(puntosCurva[k].x, yBase);

        Paint gradienteVerde = new GradientPaint(
                0, paddingTop, new Color(46, 204, 113, 70),
                0, yBase, new Color(46, 204, 113, 8)
        );
        g2d.setPaint(gradienteVerde);
        g2d.fill(polyArea);

        // Curva prominente de la Ojiva
        Color colorCurva = new Color(39, 174, 96);
        g2d.setColor(colorCurva);
        g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < k; i++) {
            g2d.drawLine(puntosCurva[i].x, puntosCurva[i].y, puntosCurva[i + 1].x, puntosCurva[i + 1].y);
        }

        // Líneas guía verticales y puntos de datos
        for (int i = 0; i <= k; i++) {
            int px = puntosCurva[i].x;
            int py = puntosCurva[i].y;

            // Línea guía vertical punteada
            g2d.setColor(new Color(190, 205, 200));
            g2d.setStroke(strokeDashed);
            g2d.drawLine(px, py, px, yBase);

            // Círculo de dato
            g2d.setColor(colorCurva);
            g2d.fillOval(px - 5, py - 5, 11, 11);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(px - 3, py - 3, 7, 7);
            g2d.setColor(colorCurva);
            g2d.drawOval(px - 5, py - 5, 11, 11);

            // Etiqueta del valor acumulado
            g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
            String lblPunto = String.format(Locale.US, "%d (%.1f%%)", frecuenciasAcumuladas[i], porcentajesAcumulados[i]);
            FontMetrics fmLbl = g2d.getFontMetrics();

            // Ubicación inteligente de la etiqueta para evitar superposiciones
            int textX = px - fmLbl.stringWidth(lblPunto) / 2;
            int textY = py - 9;
            if (i == 0) {
                textX = px + 6;
                textY = py - 4;
            } else if (i == k) {
                textX = px - fmLbl.stringWidth(lblPunto) - 6;
                textY = py - 4;
            } else if (i % 2 == 1) {
                textY = py - 11;
            } else {
                textY = py + 18;
            }

            // Fondo blanco semitransparente para legibilidad
            g2d.setColor(new Color(255, 255, 255, 210));
            g2d.fillRect(textX - 2, textY - fmLbl.getAscent(), fmLbl.stringWidth(lblPunto) + 4, fmLbl.getHeight());

            g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
            g2d.drawString(lblPunto, textX, textY);
        }

        // Ejes X y bordes del área gráfica
        g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawLine(paddingLeft, yBase, paddingLeft + plotW, yBase); // Eje X
        g2d.drawLine(paddingLeft, yBase, paddingLeft, paddingTop);     // Eje Y izq
        g2d.drawLine(paddingLeft + plotW, yBase, paddingLeft + plotW, paddingTop); // Eje Y der

        // Ticks y etiquetas de límites en eje X
        for (int i = 0; i <= k; i++) {
            int px = puntosCurva[i].x;
            g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
            g2d.drawLine(px, yBase, px, yBase + 5);

            String limStr = String.format(Locale.US, "%.1f", limites[i]);
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
            FontMetrics fmLim = g2d.getFontMetrics();
            g2d.drawString(limStr, px - fmLim.stringWidth(limStr) / 2, yBase + 18);
        }

        // Etiqueta general eje X
        g2d.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
        String labelEjeX = "Límites Superiores de Intervalo (Años)";
        FontMetrics fmX = g2d.getFontMetrics();
        g2d.drawString(labelEjeX, paddingLeft + (plotW - fmX.stringWidth(labelEjeX)) / 2, yBase + 40);
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        if (hitBoxes == null || frecuenciasAcumuladas == null) return null;
        Point p = e.getPoint();
        for (int i = 0; i <= k; i++) {
            if (hitBoxes[i] != null && hitBoxes[i].contains(p)) {
                return String.format(Locale.US,
                        "<html><div style='padding:4px; font-family:sans-serif;'>" +
                                "<b>Punto %d (Límite):</b> %.2f años<br>" +
                                "<b>Frecuencia acumulada (Fi):</b> %d de %d<br>" +
                                "<b>Porcentaje acumulado (Fi %%):</b> %.2f%%" +
                                "</div></html>",
                        i, limites[i], frecuenciasAcumuladas[i], nTotal, porcentajesAcumulados[i]);
            }
        }
        return null;
    }
}
