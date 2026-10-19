package Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public class GraficoRadar extends JPanel {

    private String titulo = "Diagrama de Radar — Analisis Multidimensional";
    private ArrayList<String> dimensiones;
    private ArrayList<Double> valoresSerie1;
    private String etiquetaSerie1 = "Titulados";
    private ArrayList<Double> valoresSerie2;
    private String etiquetaSerie2 = "Empleadores";
    private double valorMaximo = 100.0;
    private int niveles = 5;

    public GraficoRadar() {
        this.dimensiones = new ArrayList<>();
        this.valoresSerie1 = new ArrayList<>();
        this.valoresSerie2 = new ArrayList<>();
        setBackground(Color.WHITE);
    }

    public void setDatos(ArrayList<String> dimensiones, ArrayList<Double> valores, String etiqueta, double maximo) {
        this.dimensiones = dimensiones != null ? new ArrayList<>(dimensiones) : new ArrayList<>();
        this.valoresSerie1 = valores != null ? new ArrayList<>(valores) : new ArrayList<>();
        this.etiquetaSerie1 = etiqueta != null ? etiqueta : "Serie 1";
        this.valoresSerie2.clear();
        this.valorMaximo = maximo > 0 ? maximo : 100.0;
        repaint();
    }

    public void setDatosComparativos(ArrayList<String> dimensiones,
                                    ArrayList<Double> serie1, String etiqueta1,
                                    ArrayList<Double> serie2, String etiqueta2,
                                    double maximo) {
        this.dimensiones = dimensiones != null ? new ArrayList<>(dimensiones) : new ArrayList<>();
        this.valoresSerie1 = serie1 != null ? new ArrayList<>(serie1) : new ArrayList<>();
        this.etiquetaSerie1 = etiqueta1 != null ? etiqueta1 : "Serie 1";
        this.valoresSerie2 = serie2 != null ? new ArrayList<>(serie2) : new ArrayList<>();
        this.etiquetaSerie2 = etiqueta2 != null ? etiqueta2 : "Serie 2";
        this.valorMaximo = maximo > 0 ? maximo : 100.0;
        repaint();
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo != null ? titulo : "";
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Título
        g2.setColor(new Color(30, 41, 59));
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        FontMetrics fmTitulo = g2.getFontMetrics();
        int strWTitulo = fmTitulo.stringWidth(titulo);
        int tituloX = (w > strWTitulo) ? (w - strWTitulo) / 2 : 8;
        g2.drawString(titulo, Math.max(8, tituloX), 24);

        if (dimensiones == null || dimensiones.size() < 3) {
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g2.drawString("Se requieren al menos 3 categorias para el diagrama de radar.", 40, h / 2);
            return;
        }

        int n = dimensiones.size();
        int margenSuperior = 50;
        int margenInferior = 40;
        int margenLateral = 105;

        int centroX = w / 2;
        int centroY = margenSuperior + (h - margenSuperior - margenInferior) / 2;
        int radio = Math.min((w - 2 * margenLateral) / 2, (h - margenSuperior - margenInferior) / 2);

        if (radio < 30) return;

        // 2. Dibujar anillos poligonales concéntricos de referencia
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        FontMetrics fmNivel = g2.getFontMetrics();

        for (int i = 1; i <= niveles; i++) {
            double fraccion = (double) i / niveles;
            int rNivel = (int) (radio * fraccion);

            Polygon anillo = new Polygon();
            for (int j = 0; j < n; j++) {
                double angulo = -(Math.PI / 2) + (2 * Math.PI * j / n);
                int px = (int) (centroX + rNivel * Math.cos(angulo));
                int py = (int) (centroY + rNivel * Math.sin(angulo));
                anillo.addPoint(px, py);
            }

            g2.setColor(new Color(226, 232, 240));
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawPolygon(anillo);

            // Etiqueta del nivel
            double valorNivel = valorMaximo * fraccion;
            String textoNivel = (valorMaximo <= 5) ? String.format("%.1f", valorNivel) : String.format("%.0f%%", valorNivel);
            g2.setColor(new Color(148, 163, 184));
            g2.drawString(textoNivel, centroX + 4, centroY - rNivel + 12);
        }

        // 3. Dibujar radios (ejes) y etiquetas de dimensiones
        g2.setStroke(new BasicStroke(1.0f));
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        FontMetrics fmDim = g2.getFontMetrics();

        for (int j = 0; j < n; j++) {
            double angulo = -(Math.PI / 2) + (2 * Math.PI * j / n);
            int px = (int) (centroX + radio * Math.cos(angulo));
            int py = (int) (centroY + radio * Math.sin(angulo));

            // Eje
            g2.setColor(new Color(203, 213, 225));
            g2.drawLine(centroX, centroY, px, py);

            // Posicionamiento inteligente de texto de dimensión
            String dim = dimensiones.get(j);
            int labelDist = radio + 12;
            int lx = (int) (centroX + labelDist * Math.cos(angulo));
            int ly = (int) (centroY + labelDist * Math.sin(angulo));

            int strW = fmDim.stringWidth(dim);
            int strH = fmDim.getAscent();

            // Ajuste horizontal y vertical según cuadrante
            double cos = Math.cos(angulo);
            double sin = Math.sin(angulo);

            if (cos > 0.25) {
                // Derecha
            } else if (cos < -0.25) {
                // Izquierda
                lx -= strW;
            } else {
                // Centro
                lx -= strW / 2;
            }

            if (sin > 0.25) {
                ly += strH / 2;
            } else if (sin < -0.25) {
                ly -= strH / 4;
            } else {
                ly += strH / 3;
            }

            // Clamping defensivo: garantiza que ninguna etiqueta quede fuera del panel visible
            lx = Math.max(6, Math.min(w - strW - 6, lx));
            ly = Math.max(fmDim.getAscent() + 4, Math.min(h - 22, ly));

            g2.setColor(new Color(30, 41, 59));
            g2.drawString(dim, lx, ly);
        }

        // 4. Dibujar Serie 1 (Titulados / Principal)
        if (!valoresSerie1.isEmpty()) {
            Color colorSerie1 = new Color(37, 99, 235); // Azul real
            dibujarPoligonoSerie(g2, centroX, centroY, radio, n, valoresSerie1, colorSerie1);
        }

        // 5. Dibujar Serie 2 (Empleadores / Comparativa) si existe
        if (!valoresSerie2.isEmpty()) {
            Color colorSerie2 = new Color(234, 88, 12); // Naranja cálido
            dibujarPoligonoSerie(g2, centroX, centroY, radio, n, valoresSerie2, colorSerie2);
        }

        // 6. Leyenda inferior
        dibujarLeyenda(g2, w, h);
    }

    private void dibujarPoligonoSerie(Graphics2D g2, int cx, int cy, int radio, int n,
                                      ArrayList<Double> valores, Color color) {
        Path2D.Double path = new Path2D.Double();
        ArrayList<Point> puntos = new ArrayList<>();

        for (int j = 0; j < n; j++) {
            double val = (j < valores.size()) ? valores.get(j) : 0.0;
            double ratio = Math.max(0.0, Math.min(1.0, val / valorMaximo));
            double angulo = -(Math.PI / 2) + (2 * Math.PI * j / n);

            int px = (int) (cx + (radio * ratio) * Math.cos(angulo));
            int py = (int) (cy + (radio * ratio) * Math.sin(angulo));
            puntos.add(new Point(px, py));

            if (j == 0) path.moveTo(px, py);
            else path.lineTo(px, py);
        }
        path.closePath();

        // Relleno semi-transparente
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.32f));
        g2.setColor(color);
        g2.fill(path);
        g2.setComposite(originalComposite);

        // Borde sólido
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2.2f));
        g2.draw(path);

        // Nodos (vértices circulares)
        for (Point p : puntos) {
            g2.setColor(Color.WHITE);
            g2.fillOval(p.x - 4, p.y - 4, 8, 8);
            g2.setColor(color);
            g2.drawOval(p.x - 4, p.y - 4, 8, 8);
        }
    }

    private void dibujarLeyenda(Graphics2D g2, int w, int h) {
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        FontMetrics fm = g2.getFontMetrics();
        int y = h - 14;

        if (valoresSerie2.isEmpty()) {
            // Una sola serie
            int itemW = 16 + 6 + fm.stringWidth(etiquetaSerie1);
            int startX = (w - itemW) / 2;

            g2.setColor(new Color(37, 99, 235));
            g2.fillRect(startX, y - 10, 16, 10);
            g2.setColor(new Color(30, 41, 59));
            g2.drawString(etiquetaSerie1, startX + 22, y);
        } else {
            // Dos series comparativas
            int itemW1 = 16 + 6 + fm.stringWidth(etiquetaSerie1);
            int itemW2 = 16 + 6 + fm.stringWidth(etiquetaSerie2);
            int sep = 25;
            int totalW = itemW1 + sep + itemW2;
            int startX = (w - totalW) / 2;

            // Serie 1
            g2.setColor(new Color(37, 99, 235));
            g2.fillRect(startX, y - 10, 16, 10);
            g2.setColor(new Color(30, 41, 59));
            g2.drawString(etiquetaSerie1, startX + 22, y);

            // Serie 2
            int x2 = startX + itemW1 + sep;
            g2.setColor(new Color(234, 88, 12));
            g2.fillRect(x2, y - 10, 16, 10);
            g2.setColor(new Color(30, 41, 59));
            g2.drawString(etiquetaSerie2, x2 + 22, y);
        }
    }
}
