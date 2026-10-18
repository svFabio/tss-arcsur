package Modelo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChiCuadrada {

    public static Object[] calcularChiCuadrada(ArrayList<String> var1, ArrayList<String> var2) {
        if (var1.size() != var2.size() || var1.isEmpty()) {
            return new Object[]{null, 0.0, 0, "Error en datos"};
        }

        // 1. Obtener valores únicos
        ArrayList<String> unicosV1 = obtenerUnicos(var1);
        ArrayList<String> unicosV2 = obtenerUnicos(var2);

        // 2. Crear y llenar tabla de contingencia (Frecuencias Observadas)
        int filas = unicosV1.size();
        int cols = unicosV2.size();
        int[][] observadas = new int[filas][cols];
        int[] totalesFila = new int[filas];
        int[] totalesCol = new int[cols];
        int totalGeneral = 0;

        for (int i = 0; i < var1.size(); i++) {
            String v1 = var1.get(i);
            String v2 = var2.get(i);
            if (v1.isEmpty() || v2.isEmpty()) continue;

            int filaIdx = unicosV1.indexOf(v1);
            int colIdx = unicosV2.indexOf(v2);

            observadas[filaIdx][colIdx]++;
            totalesFila[filaIdx]++;
            totalesCol[colIdx]++;
            totalGeneral++;
        }

        if (totalGeneral == 0) return new Object[]{null, 0.0, 0, "Sin datos válidos"};

        // 3. Calcular frecuencias esperadas y Chi-Cuadrada
        double chiCuadrada = 0;
        double[][] esperadas = new double[filas][cols];

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < cols; j++) {
                esperadas[i][j] = (double) (totalesFila[i] * totalesCol[j]) / totalGeneral;
                if (esperadas[i][j] > 0) {
                    double diferencia = observadas[i][j] - esperadas[i][j];
                    chiCuadrada += (diferencia * diferencia) / esperadas[i][j];
                }
            }
        }

        int gradosLibertad = (filas - 1) * (cols - 1);
        
        // Empaquetar matriz para la vista
        Object[][] matrizResultados = new Object[filas + 1][cols + 2];
        matrizResultados[0][0] = "Variables";
        for (int j = 0; j < cols; j++) matrizResultados[0][j + 1] = unicosV2.get(j);
        matrizResultados[0][cols + 1] = "TOTAL";

        for (int i = 0; i < filas; i++) {
            matrizResultados[i + 1][0] = unicosV1.get(i);
            for (int j = 0; j < cols; j++) {
                matrizResultados[i + 1][j + 1] = observadas[i][j];
            }
            matrizResultados[i + 1][cols + 1] = totalesFila[i];
        }

        int minDim = Math.min(filas - 1, cols - 1);
        double cramerV = (totalGeneral > 0 && minDim > 0) ? Math.sqrt(chiCuadrada / ((double) totalGeneral * minDim)) : 0.0;

        String conclusion = gradosLibertad > 0 ? "Valor Chi²: " + String.format("%.2f", chiCuadrada) + 
                            " (GL: " + gradosLibertad + ") | V de Cramér: " + String.format("%.3f", cramerV) + 
                            " (" + interpretarCramer(cramerV) + ")" : "Datos insuficientes para Chi²";

        return new Object[]{matrizResultados, chiCuadrada, gradosLibertad, conclusion};
    }

    private static String interpretarCramer(double v) {
        if (v < 0.10) return "Asociación despreciable";
        if (v < 0.30) return "Asociación débil";
        if (v < 0.50) return "Asociación moderada";
        return "Asociación fuerte";
    }

    private static ArrayList<String> obtenerUnicos(ArrayList<String> lista) {
        ArrayList<String> unicos = new ArrayList<>();
        for (String item : lista) {
            if (!item.isEmpty() && !unicos.contains(item)) {
                unicos.add(item);
            }
        }
        return unicos;
    }
}
