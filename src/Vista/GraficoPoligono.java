package Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Gráfico de Polígono de Frecuencias para variables cuantitativas.
 * Conecta los puntos medios (marcas de clase) con una línea prominente y círculos de datos,
 * cerrando el polígono en el eje X en ambos extremos y mostrando las frecuencias en cada vértice.
 */
public class GraficoPoligono extends JPanel {

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
    private Point[] vertices;
    private Rectangle[] hitBoxes;

    public GraficoPoligono() {
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
            vertices = null;
            hitBoxes = null;
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
        vertices = new Point[k];
        hitBoxes = new Rectangle[k];

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
        int paddingRight = 40;
        int paddingTop = 58;
        int paddingBottom = 68;

        int plotW = w - paddingLeft - paddingRight;
        int plotH = h - paddingTop - paddingBottom;
        int yBase = paddingTop + plotH;

        if (plotW <= 0 || plotH <= 0) return;

        // Título
        g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
        g2d.setFont(new Font("SansSerif", Font.BOLD, ConfiguracionTextos.TAMANO_FUENTE_TITULO));
        FontMetrics fmTitulo = g2d.getFontMetrics();
        g2d.drawString(titulo, (w - fmTitulo.stringWidth(titulo)) / 2, 26);

        // Subtítulo
        g2d.setFont(new Font("SansSerif", Font.PLAIN, ConfiguracionTextos.TAMANO_FUENTE_PEQUENA));
        g2d.setColor(new Color(110, 115, 125));
        String subtitulo = String.format(Locale.US,
                "Polígono de Frecuencias (Marcas de Clase Mc, N = %d, c = %.2f)",
                nTotal, anchoIntervalo);
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

        // Líneas de cuadrícula horizontal Y
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

        // Coordenadas de los vértices y extremos en el eje X
        double rangoTotal = limites[k] - limites[0];
        if (rangoTotal == 0) rangoTotal = 1.0;

        int xLeftClose = paddingLeft;
        int xRightClose = paddingLeft + plotW;

        int[] xCoords = new int[k];
        int[] yCoords = new int[k];
        vertices = new Point[k];
        hitBoxes = new Rectangle[k];

        for (int i = 0; i < k; i++) {
            xCoords[i] = paddingLeft + (int) Math.round(((marcasClase[i] - limites[0]) / rangoTotal) * plotW);
            yCoords[i] = yBase - (int) Math.round(((double) frecuencias[i] / yCeil) * plotH);
            vertices[i] = new Point(xCoords[i], yCoords[i]);
            hitBoxes[i] = new Rectangle(xCoords[i] - 12, yCoords[i] - 12, 24, 24);
        }

        // Área sombreada bajo el polígono (cerrado en el eje X)
        Polygon polyArea = new Polygon();
        polyArea.addPoint(xLeftClose, yBase);
        for (int i = 0; i < k; i++) {
            polyArea.addPoint(xCoords[i], yCoords[i]);
        }
        polyArea.addPoint(xRightClose, yBase);

        Paint gradienteArea = new GradientPaint(
                0, paddingTop, new Color(41, 128, 185, 75),
                0, yBase, new Color(41, 128, 185, 12)
        );
        g2d.setPaint(gradienteArea);
        g2d.fill(polyArea);

        // Líneas guía verticales punteadas desde los vértices hasta el eje X
        g2d.setColor(new Color(180, 195, 210));
        g2d.setStroke(strokeDashed);
        for (int i = 0; i < k; i++) {
            g2d.drawLine(xCoords[i], yCoords[i], xCoords[i], yBase);
        }

        // Línea prominente del polígono (conectando extremos y marcas de clase)
        g2d.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(ConfiguracionColores.COLOR_PRIMARIO);

        // Cierre hacia el eje X en el extremo izquierdo
        g2d.drawLine(xLeftClose, yBase, xCoords[0], yCoords[0]);

        // Segmentos entre marcas de clase
        for (int i = 0; i < k - 1; i++) {
            g2d.drawLine(xCoords[i], yCoords[i], xCoords[i + 1], yCoords[i + 1]);
        }

        // Cierre hacia el eje X en el extremo derecho
        g2d.drawLine(xCoords[k - 1], yCoords[k - 1], xRightClose, yBase);

        // Puntos de datos y etiquetas de frecuencias en vértices
        for (int i = 0; i < k; i++) {
            int px = xCoords[i];
            int py = yCoords[i];

            // Anillo exterior y centro blanco
            g2d.setColor(ConfiguracionColores.COLOR_PRIMARIO);
            g2d.fillOval(px - 5, py - 5, 11, 11);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(px - 3, py - 3, 7, 7);
            g2d.setColor(ConfiguracionColores.COLOR_PRIMARIO);
            g2d.drawOval(px - 5, py - 5, 11, 11);

            // Frecuencia en el vértice
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
            String fiLabel = String.valueOf(frecuencias[i]);
            FontMetrics fmFi = g2d.getFontMetrics();
            g2d.drawString(fiLabel, px - fmFi.stringWidth(fiLabel) / 2, py - 9);
        }

        // Marcadores de cierre en el eje X
        g2d.setColor(new Color(110, 110, 120));
        g2d.fillOval(xLeftClose - 3, yBase - 3, 7, 7);
        g2d.fillOval(xRightClose - 3, yBase - 3, 7, 7);

        // Ejes principales
        g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawLine(paddingLeft, yBase, paddingLeft + plotW, yBase); // Eje X
        g2d.drawLine(paddingLeft, yBase, paddingLeft, paddingTop);     // Eje Y

        // Marcas y etiquetas en eje X
        // Límites extremos
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2d.setColor(new Color(100, 110, 120));
        String lim0Str = String.format(Locale.US, "%.1f (Inicio)", limites[0]);
        g2d.drawString(lim0Str, xLeftClose, yBase + 16);
        String limKStr = String.format(Locale.US, "%.1f (Fin)", limites[k]);
        FontMetrics fmLimK = g2d.getFontMetrics();
        g2d.drawString(limKStr, xRightClose - fmLimK.stringWidth(limKStr), yBase + 16);

        // Marcas de clase (Mc)
        for (int i = 0; i < k; i++) {
            int px = xCoords[i];
            g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
            g2d.drawLine(px, yBase, px, yBase + 5);

            g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
            g2d.setColor(new Color(41, 128, 185));
            String mcStr = String.format(Locale.US, "%.2f", marcasClase[i]);
            FontMetrics fmMc = g2d.getFontMetrics();
            g2d.drawString(mcStr, px - fmMc.stringWidth(mcStr) / 2, yBase + 30);
        }

        // Etiqueta general eje X
        g2d.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
        String labelEjeX = "Marcas de Clase (Puntos Medios)";
        FontMetrics fmX = g2d.getFontMetrics();
        g2d.drawString(labelEjeX, paddingLeft + (plotW - fmX.stringWidth(labelEjeX)) / 2, yBase + 54);
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        if (hitBoxes == null || frecuencias == null) return null;
        Point p = e.getPoint();
        for (int i = 0; i < k; i++) {
            if (hitBoxes[i] != null && hitBoxes[i].contains(p)) {
                String corchete = i == 0 ? "[" : "(";
                return String.format(Locale.US,
                        "<html><div style='padding:4px; font-family:sans-serif;'>" +
                                "<b>Vértice %d (Marca de clase):</b> %.2f<br>" +
                                "<b>Intervalo asociado:</b> %s%.2f – %.2f]<br>" +
                                "<b>Frecuencia absoluta (fi):</b> %d<br>" +
                                "<b>Frecuencia relativa (hi):</b> %.2f%%" +
                                "</div></html>",
                        i + 1, marcasClase[i],
                        corchete, limites[i], limites[i + 1],
                        frecuencias[i], frecuenciasRelativas[i]);
            }
        }
        return null;
    }
}
