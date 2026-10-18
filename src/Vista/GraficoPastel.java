package Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.LinkedHashMap;
import java.util.Map;

public class GraficoPastel extends JPanel {
    private LinkedHashMap<String, Integer> datos;
    private String titulo;
    private boolean esAgrupado;
    private int totalCategoriasOriginales;

    public GraficoPastel() {
        this.datos = new LinkedHashMap<>();
        this.titulo = "";
        this.esAgrupado = false;
        this.totalCategoriasOriginales = 0;
        setBackground(Color.WHITE);
    }

    public void setDatos(LinkedHashMap<String, Integer> datosOriginales, String titulo) {
        this.titulo = titulo != null ? titulo : "";

        if (datosOriginales != null && datosOriginales.size() > 6) {
            LinkedHashMap<String, Integer> agrupados = new LinkedHashMap<>();
            int count = 0;
            int sumaOtros = 0;
            int categoriasRestantes = 0;

            for (Map.Entry<String, Integer> entry : datosOriginales.entrySet()) {
                if (count < 5) {
                    agrupados.put(entry.getKey(), entry.getValue());
                    count++;
                } else {
                    sumaOtros += entry.getValue();
                    categoriasRestantes++;
                }
            }
            if (sumaOtros > 0) {
                agrupados.put("Otros (" + categoriasRestantes + " categ.)", sumaOtros);
            }
            this.datos = agrupados;
            this.esAgrupado = true;
            this.totalCategoriasOriginales = datosOriginales.size();
        } else {
            this.datos = datosOriginales != null ? new LinkedHashMap<>(datosOriginales) : new LinkedHashMap<>();
            this.esAgrupado = false;
            this.totalCategoriasOriginales = this.datos.size();
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (datos == null || datos.isEmpty()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("SansSerif", Font.PLAIN, ConfiguracionTextos.TAMANO_FUENTE_NORMAL));
            g2d.drawString("Sin datos para graficar", 30, 40);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 1. Título
        g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
        g2d.setFont(new Font("SansSerif", Font.BOLD, ConfiguracionTextos.TAMANO_FUENTE_TITULO));
        FontMetrics fmTitulo = g2d.getFontMetrics();
        g2d.drawString(titulo, (width - fmTitulo.stringWidth(titulo)) / 2, 28);

        // Subtítulo si se agruparon categorías abiertas o de alta cardinalidad
        int topOffset = 48;
        if (esAgrupado) {
            g2d.setFont(new Font("SansSerif", Font.ITALIC, ConfiguracionTextos.TAMANO_FUENTE_PEQUENA));
            g2d.setColor(new Color(110, 110, 120));
            String subtitulo = "(Top 5 más frecuentes + agrupación de " + (totalCategoriasOriginales - 5) + " restantes)";
            FontMetrics fmSub = g2d.getFontMetrics();
            g2d.drawString(subtitulo, (width - fmSub.stringWidth(subtitulo)) / 2, 45);
            topOffset = 60;
        }

        // 2. Suma total
        int total = 0;
        for (int val : datos.values()) {
            total += val;
        }
        if (total == 0) return;

        // 3. Cálculo de layout responsive
        int bottomOffset = 20;
        int availableHeight = height - topOffset - bottomOffset;

        boolean modoLado = width >= 480;
        int diametro;
        int pieX;
        int pieY;
        int leyendaX;
        int leyendaY;

        if (modoLado) {
            int pieAreaWidth = (int) (width * 0.50);
            diametro = Math.min(pieAreaWidth - 40, availableHeight - 20);
            if (diametro < 60) diametro = 60;
            pieX = (pieAreaWidth - diametro) / 2 + 15;
            pieY = topOffset + (availableHeight - diametro) / 2;
            leyendaX = pieAreaWidth + 20;
            leyendaY = topOffset + 20;
        } else {
            diametro = Math.min(width - 40, (int) (availableHeight * 0.52));
            if (diametro < 60) diametro = 60;
            pieX = (width - diametro) / 2;
            pieY = topOffset + 10;
            leyendaX = 25;
            leyendaY = pieY + diametro + 25;
        }

        // 4. Dibujar sectores (fillArc con Arc2D)
        double anguloActual = 90.0; // Iniciar a las 12 en punto
        int index = 0;

        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            double porcion = (double) entry.getValue() / total;
            double angulo = porcion * 360.0;

            Color color = ConfiguracionColores.COLORES_GRAFICO[index % ConfiguracionColores.COLORES_GRAFICO.length];
            g2d.setColor(color);
            g2d.fill(new Arc2D.Double(pieX, pieY, diametro, diametro, anguloActual, angulo, Arc2D.PIE));

            // Borde blanco separador
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.draw(new Arc2D.Double(pieX, pieY, diametro, diametro, anguloActual, angulo, Arc2D.PIE));

            anguloActual += angulo;
            index++;
        }

        // 5. Dibujar leyenda con nombres, cantidades y porcentajes
        g2d.setFont(new Font("SansSerif", Font.PLAIN, ConfiguracionTextos.TAMANO_FUENTE_PEQUENA));
        FontMetrics fmLeyenda = g2d.getFontMetrics();
        int currentY = leyendaY;
        int tamCaja = 12;

        index = 0;
        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            double pct = (entry.getValue() * 100.0) / total;
            Color color = ConfiguracionColores.COLORES_GRAFICO[index % ConfiguracionColores.COLORES_GRAFICO.length];

            // Cuadrito de color
            g2d.setColor(color);
            g2d.fillRect(leyendaX, currentY - tamCaja + 2, tamCaja, tamCaja);
            g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
            g2d.drawRect(leyendaX, currentY - tamCaja + 2, tamCaja, tamCaja);

            // Texto: Categoría (recortada si es muy extensa) + Frecuencia + %
            String etiqueta = entry.getKey();
            if (etiqueta.length() > 28) {
                etiqueta = etiqueta.substring(0, 26) + "..";
            }
            String texto = String.format("%s: %d (%.1f%%)", etiqueta, entry.getValue(), pct);
            g2d.drawString(texto, leyendaX + tamCaja + 8, currentY + 1);

            currentY += fmLeyenda.getHeight() + 8;
            index++;
        }
    }
}
