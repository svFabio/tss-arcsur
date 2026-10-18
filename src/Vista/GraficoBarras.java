package Vista;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class GraficoBarras extends JPanel {
    private LinkedHashMap<String, Integer> datos;
    private String titulo;
    private boolean esAgrupado;
    private int totalCategoriasOriginales;

    public GraficoBarras() {
        this.datos = new LinkedHashMap<>();
        this.titulo = "";
        this.esAgrupado = false;
        this.totalCategoriasOriginales = 0;
        setBackground(Color.WHITE);
    }

    public void setDatos(LinkedHashMap<String, Integer> datosOriginales, String titulo) {
        this.titulo = titulo != null ? titulo : "";

        if (datosOriginales != null && datosOriginales.size() > 8) {
            LinkedHashMap<String, Integer> agrupados = new LinkedHashMap<>();
            int count = 0;
            int sumaOtros = 0;
            int categoriasRestantes = 0;

            for (Map.Entry<String, Integer> entry : datosOriginales.entrySet()) {
                if (count < 7) {
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
        int padding = 50;
        int maxVal = 0;

        for (int val : datos.values()) {
            if (val > maxVal) maxVal = val;
        }
        if (maxVal == 0) maxVal = 1;

        int numBarras = datos.size();
        int anchoBarra = (width - (2 * padding)) / numBarras;
        if (anchoBarra > 100) anchoBarra = 100;
        int espacio = ((width - (2 * padding)) - (anchoBarra * numBarras)) / (numBarras + 1);

        // Titulo
        g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
        g2d.setFont(new Font("SansSerif", Font.BOLD, ConfiguracionTextos.TAMANO_FUENTE_TITULO));
        FontMetrics fmTitulo = g2d.getFontMetrics();
        g2d.drawString(titulo, (width - fmTitulo.stringWidth(titulo)) / 2, 28);

        // Subtitulo de agrupacion
        if (esAgrupado) {
            g2d.setFont(new Font("SansSerif", Font.ITALIC, ConfiguracionTextos.TAMANO_FUENTE_PEQUENA));
            g2d.setColor(new Color(110, 110, 120));
            String subtitulo = "(Top 7 más frecuentes + agrupación de " + (totalCategoriasOriginales - 7) + " restantes)";
            FontMetrics fmSub = g2d.getFontMetrics();
            g2d.drawString(subtitulo, (width - fmSub.stringWidth(subtitulo)) / 2, 45);
        }

        // Ejes
        g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
        g2d.drawLine(padding, height - padding, width - padding, height - padding); // X
        g2d.drawLine(padding, height - padding, padding, padding); // Y

        int x = padding + espacio;
        int index = 0;

        g2d.setFont(new Font("SansSerif", Font.PLAIN, ConfiguracionTextos.TAMANO_FUENTE_PEQUENA));
        FontMetrics fm = g2d.getFontMetrics();

        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            int altoBarra = (int) (((double) entry.getValue() / maxVal) * (height - 2 * padding));
            int y = height - padding - altoBarra;

            g2d.setColor(ConfiguracionColores.COLORES_GRAFICO[index % ConfiguracionColores.COLORES_GRAFICO.length]);
            g2d.fillRect(x, y, anchoBarra, altoBarra);

            g2d.setColor(ConfiguracionColores.COLOR_TEXTO);
            g2d.drawRect(x, y, anchoBarra, altoBarra);

            // Valor arriba de la barra
            String valor = String.valueOf(entry.getValue());
            g2d.drawString(valor, x + (anchoBarra - fm.stringWidth(valor)) / 2, y - 5);

            // Etiqueta abajo (recortada si es muy larga)
            String label = entry.getKey();
            if (label.length() > 14) label = label.substring(0, 12) + "..";
            g2d.drawString(label, x + (anchoBarra - fm.stringWidth(label)) / 2, height - padding + 15);

            x += anchoBarra + espacio;
            index++;
        }
    }
}
