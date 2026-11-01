package Modelo;

import Vista.GraficoBoxplot;
import Vista.GraficoHistograma;
import Vista.GraficoOjiva;
import Vista.GraficoPoligono;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class GeneradorGraficosDocumento {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");

        GestorDatos gestor = new GestorDatos();
        File f = new File("src/Recursos/titulados_limpio.csv");
        if (!f.exists()) f = new File("titulados_limpio.csv");

        boolean ok = gestor.cargarCSV(f, true);
        if (!ok) {
            System.err.println("Error al cargar dataset");
            return;
        }

        ArrayList<String> raw = gestor.obtenerColumna(true, "AniosProfesional");
        ArrayList<Double> anios = Estadistica.extraerValoresNumericos(raw);

        File dirSalida = new File("/home/svfabio/university/06-taller-simulacion/02-proyecto-arcusur/doc-actual/graficos");
        if (!dirSalida.exists()) dirSalida.mkdirs();

        int w = 850;
        int h = 480;

        // 1. Histograma
        GraficoHistograma gh = new GraficoHistograma();
        gh.setSize(w, h);
        gh.setDatos(anios, "Histograma — Años de Vida Profesional (n = 145)");
        guardarComponente(gh, w, h, new File(dirSalida, "01_histograma_anios_profesional.png"));

        // 2. Polígono
        GraficoPoligono gp = new GraficoPoligono();
        gp.setSize(w, h);
        gp.setDatos(anios, "Polígono de Frecuencias — Años de Vida Profesional (n = 145)");
        guardarComponente(gp, w, h, new File(dirSalida, "02_poligono_anios_profesional.png"));

        // 3. Boxplot
        GraficoBoxplot gb = new GraficoBoxplot();
        gb.setSize(w, h);
        gb.setDatos(anios, "Diagrama de Caja (Boxplot de Tukey) — Años de Vida Profesional (n = 145)");
        guardarComponente(gb, w, h, new File(dirSalida, "03_boxplot_anios_profesional.png"));

        // 4. Ojiva
        GraficoOjiva go = new GraficoOjiva();
        go.setSize(w, h);
        go.setDatos(anios, "Ojiva (Frecuencia Acumulada) — Años de Vida Profesional (n = 145)");
        guardarComponente(go, w, h, new File(dirSalida, "04_ojiva_anios_profesional.png"));

        System.out.println("Gráficos generados exitosamente en: " + dirSalida.getAbsolutePath());
    }

    private static void guardarComponente(Component comp, int w, int h, File destino) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);
        comp.paint(g2);
        g2.dispose();
        try {
            ImageIO.write(img, "PNG", destino);
            System.out.println("Guardado: " + destino.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
