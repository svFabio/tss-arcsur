package Modelo;

import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExportadorDatos {

    /**
     * Exporta la matriz de contingencia (frecuencias cruzadas) a un archivo CSV
     * compatible con Microsoft Excel (incluye BOM UTF-8 y totales marginales).
     */
    public static boolean exportarMatrizContingenciaCSV(File destino, Object[][] matriz, String resultadoChi,
                                                       String varFila, String varCol) {
        if (matriz == null || matriz.length == 0 || destino == null) {
            return false;
        }

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destino), StandardCharsets.UTF_8))) {
            // Escribir BOM UTF-8 para apertura directa en Excel
            bw.write("\uFEFF");

            // Encabezado del informe
            bw.write("\"REPORTE DE FRECUENCIAS CRUZADAS (CHI-CUADRADA)\"\n");
            bw.write("\"Sistema:\",\"tss-arcsur - Acreditacion ARCUSUR\"\n");
            bw.write("\"Fecha de Generacion:\",\"" + obtenerFechaActual() + "\"\n");
            bw.write("\"Variable Fila (Independiente):\",\"" + escaparCSV(varFila) + "\"\n");
            bw.write("\"Variable Columna (Dependiente):\",\"" + escaparCSV(varCol) + "\"\n\n");

            // Escribir datos de la matriz
            int filas = matriz.length;
            int cols = matriz[0].length;

            for (int i = 0; i < filas; i++) {
                StringBuilder linea = new StringBuilder();
                for (int j = 0; j < cols; j++) {
                    Object val = matriz[i][j];
                    String strVal = (val != null) ? val.toString() : "";
                    linea.append("\"").append(escaparCSV(strVal)).append("\"");
                    if (j < cols - 1) linea.append(",");
                }
                bw.write(linea.toString() + "\n");
            }

            // Pie de estadísticos
            bw.write("\n\"RESULTADOS ESTADISTICOS DE LA PRUEBA:\"\n");
            bw.write("\"Diagnostico:\",\"" + escaparCSV(resultadoChi) + "\"\n");
            bw.write("\"Normativa:\",\"Rigor metodologico ARCUSUR - Prueba de Independencia Chi-Cuadrada\"\n");

            bw.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Exporta la tabla de frecuencias univariadas y el resumen descriptivo a CSV.
     */
    public static boolean exportarResumenUnivariadoCSV(File destino, String nombreVariable,
                                                      DefaultTableModel modeloTabla, ResumenEstadistico resumen) {
        if (destino == null || modeloTabla == null) {
            return false;
        }

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destino), StandardCharsets.UTF_8))) {
            bw.write("\uFEFF");

            bw.write("\"REPORTE ESTADISTICO UNIVARIADO\"\n");
            bw.write("\"Variable Analizada:\",\"" + escaparCSV(nombreVariable) + "\"\n");
            bw.write("\"Fecha de Generacion:\",\"" + obtenerFechaActual() + "\"\n\n");

            // Tabla de frecuencias
            bw.write("\"DISTRIBUCION DE FRECUENCIAS\"\n");
            int colCount = modeloTabla.getColumnCount();
            StringBuilder cabecera = new StringBuilder();
            for (int j = 0; j < colCount; j++) {
                cabecera.append("\"").append(escaparCSV(modeloTabla.getColumnName(j))).append("\"");
                if (j < colCount - 1) cabecera.append(",");
            }
            bw.write(cabecera.toString() + "\n");

            int rowCount = modeloTabla.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                StringBuilder fila = new StringBuilder();
                for (int j = 0; j < colCount; j++) {
                    Object val = modeloTabla.getValueAt(i, j);
                    fila.append("\"").append(escaparCSV(val != null ? val.toString() : "")).append("\"");
                    if (j < colCount - 1) fila.append(",");
                }
                bw.write(fila.toString() + "\n");
            }

            // Estadísticos descriptivos si existen
            if (resumen != null) {
                bw.write("\n\"ESTADISTICOS DESCRIPTIVOS\"\n");
                bw.write("\"Metrica\",\"Valor\"\n");
                bw.write("\"" + escaparCSV(resumen.titulo1) + "\",\"" + escaparCSV(resumen.valor1) + "\"\n");
                bw.write("\"" + escaparCSV(resumen.titulo2) + "\",\"" + escaparCSV(resumen.valor2) + "\"\n");
                bw.write("\"" + escaparCSV(resumen.titulo3) + "\",\"" + escaparCSV(resumen.valor3) + "\"\n");
                bw.write("\"" + escaparCSV(resumen.titulo4) + "\",\"" + escaparCSV(resumen.valor4) + "\"\n");
                bw.write("\"" + escaparCSV(resumen.titulo5) + "\",\"" + escaparCSV(resumen.valor5) + "\"\n");
            }

            bw.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Exporta el dataset depurado (Data Wrangling limpio) a CSV.
     */
    public static boolean exportarDatasetCSV(File destino, String[] columnas, ArrayList<HashMap<String, String>> datos) {
        if (destino == null || columnas == null || datos == null) {
            return false;
        }

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destino), StandardCharsets.UTF_8))) {
            bw.write("\uFEFF");

            // Cabeceras
            StringBuilder cab = new StringBuilder();
            for (int j = 0; j < columnas.length; j++) {
                cab.append("\"").append(escaparCSV(columnas[j])).append("\"");
                if (j < columnas.length - 1) cab.append(",");
            }
            bw.write(cab.toString() + "\n");

            // Filas
            for (HashMap<String, String> fila : datos) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < columnas.length; j++) {
                    String valor = fila.getOrDefault(columnas[j], "");
                    sb.append("\"").append(escaparCSV(valor)).append("\"");
                    if (j < columnas.length - 1) sb.append(",");
                }
                bw.write(sb.toString() + "\n");
            }

            bw.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Genera un informe ejecutivo consolidado en formato HTML interactivo e imprimible.
     */
    public static boolean exportarReporteEjecutivoHTML(File destino, Map<String, String> kpis,
                                                      List<String> hallazgos, Object[][] matrizContingencia,
                                                      String resultadoChi, String var1, String var2) {
        if (destino == null) return false;

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destino), StandardCharsets.UTF_8))) {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n");
            html.append("<meta charset=\"UTF-8\">\n");
            html.append("<title>Informe Ejecutivo - Analisis de Mercado ARCUSUR</title>\n");
            html.append("<style>\n");
            html.append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f6f9; color: #2c3e50; margin: 0; padding: 20px; }\n");
            html.append(".container { max-width: 1000px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.08); }\n");
            html.append("h1 { color: #1e3a8a; border-bottom: 2px solid #3b82f6; padding-bottom: 10px; margin-top: 0; font-size: 24px; }\n");
            html.append("h2 { color: #2563eb; font-size: 18px; margin-top: 25px; border-left: 4px solid #3b82f6; padding-left: 8px; }\n");
            html.append(".kpi-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin: 20px 0; }\n");
            html.append(".kpi-card { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 15px; text-align: center; border-top: 4px solid #2563eb; }\n");
            html.append(".kpi-val { font-size: 26px; font-weight: bold; color: #1e3a8a; margin: 5px 0; }\n");
            html.append(".kpi-lbl { font-size: 13px; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px; }\n");
            html.append(".findings-box { background: #eff6ff; border: 1px solid #bfdbfe; border-radius: 6px; padding: 18px; margin: 20px 0; }\n");
            html.append(".findings-box ul { margin: 0; padding-left: 20px; }\n");
            html.append(".findings-box li { margin-bottom: 8px; line-height: 1.5; }\n");
            html.append("table { width: 100%; border-collapse: collapse; margin-top: 15px; font-size: 14px; }\n");
            html.append("th, td { border: 1px solid #cbd5e1; padding: 8px 12px; text-align: center; }\n");
            html.append("th { background-color: #f1f5f9; color: #334155; font-weight: 600; }\n");
            html.append(".chi-box { background: #fdf2f8; border: 1px solid #fbcfe8; border-radius: 6px; padding: 12px; margin-top: 15px; font-weight: 500; color: #9d174d; }\n");
            html.append(".footer { margin-top: 40px; text-align: center; font-size: 12px; color: #94a3b8; border-top: 1px solid #e2e8f0; padding-top: 15px; }\n");
            html.append("@media print { body { background: white; padding: 0; } .container { box-shadow: none; border: none; } }\n");
            html.append("</style>\n</head>\n<body>\n");

            html.append("<div class=\"container\">\n");
            html.append("<h1>Informe Ejecutivo — Estudio de Mercado y Acreditacion ARCUSUR</h1>\n");
            html.append("<p><strong>Carrera:</strong> Ingenieria en Sistemas — UMSS &nbsp;|&nbsp; <strong>Fecha:</strong> ").append(obtenerFechaActual()).append("</p>\n");

            // KPIs
            html.append("<h2>1. Indicadores Clave de Desempeño (KPIs)</h2>\n");
            html.append("<div class=\"kpi-grid\">\n");
            if (kpis != null) {
                for (Map.Entry<String, String> entry : kpis.entrySet()) {
                    html.append("<div class=\"kpi-card\">\n");
                    html.append("<div class=\"kpi-val\">").append(entry.getValue()).append("</div>\n");
                    html.append("<div class=\"kpi-lbl\">").append(entry.getKey()).append("</div>\n");
                    html.append("</div>\n");
                }
            }
            html.append("</div>\n");

            // Hallazgos y Diagnóstico
            html.append("<h2>2. Diagnostico Estrategico y Recomendaciones Automatizadas</h2>\n");
            html.append("<div class=\"findings-box\">\n<ul>\n");
            if (hallazgos != null && !hallazgos.isEmpty()) {
                for (String h : hallazgos) {
                    html.append("<li>").append(h).append("</li>\n");
                }
            } else {
                html.append("<li>Datos procesados correctamente conforme al pipeline metodologico.</li>\n");
            }
            html.append("</ul>\n</div>\n");

            // Matriz si está disponible
            if (matrizContingencia != null && matrizContingencia.length > 0) {
                html.append("<h2>3. Matriz de Frecuencia Cruzada Representativa</h2>\n");
                html.append("<p>Cruce: <strong>").append(var1).append("</strong> vs. <strong>").append(var2).append("</strong></p>\n");
                html.append("<table>\n");
                for (int i = 0; i < matrizContingencia.length; i++) {
                    html.append("<tr>\n");
                    for (int j = 0; j < matrizContingencia[i].length; j++) {
                        String celda = matrizContingencia[i][j] != null ? matrizContingencia[i][j].toString() : "";
                        if (i == 0 || j == 0) {
                            html.append("<th>").append(celda).append("</th>");
                        } else {
                            html.append("<td>").append(celda).append("</td>");
                        }
                    }
                    html.append("\n</tr>\n");
                }
                html.append("</table>\n");

                if (resultadoChi != null && !resultadoChi.isEmpty()) {
                    html.append("<div class=\"chi-box\"><strong>Contraste de Hipotesis:</strong> ").append(resultadoChi).append("</div>\n");
                }
            }

            html.append("<div class=\"footer\">\n");
            html.append("Generado automaticamente por el sistema tss-arcsur &bull; Cumplimiento de la Historia de Usuario 5 (Acreditacion ARCUSUR)\n");
            html.append("</div>\n");

            html.append("</div>\n</body>\n</html>");

            bw.write(html.toString());
            bw.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String escaparCSV(String texto) {
        if (texto == null) return "";
        return texto.replace("\"", "\"\"");
    }

    private static String obtenerFechaActual() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
