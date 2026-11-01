package Modelo;

import java.io.File;
import java.util.*;

public class TestValidacionEstadistica {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    private static boolean assertCondition(String name, boolean condition, String detail) {
        if (condition) {
            System.out.printf("  [PASS] %-42s -> %s%n", name, detail);
            testsPassed++;
            return true;
        } else {
            System.err.printf("  [FAIL] %-42s -> %s%n", name, detail);
            testsFailed++;
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("         EJECUCIÓN DE PRUEBAS DE VALIDACIÓN ESTADÍSTICA (BLOQUE 1)");
        System.out.println("===============================================================================");

        // 1. Cargar archivo titulados_limpio.csv
        File archivo = new File("src/Recursos/titulados_limpio.csv");
        if (!archivo.exists()) {
            archivo = new File("titulados_limpio.csv");
        }

        if (!archivo.exists()) {
            System.err.println("ERROR: No se encontró 'titulados_limpio.csv'.");
            System.exit(1);
        }

        GestorDatos gestor = new GestorDatos();
        boolean cargado = gestor.cargarCSV(archivo, true);
        assertCondition("Carga de titulados_limpio.csv", cargado, "Archivo cargado correctamente desde " + archivo.getPath());

        // 2. Extraer valores numéricos de la columna AniosProfesional
        ArrayList<String> datosCrudos = gestor.obtenerColumna(true, "AniosProfesional");
        ArrayList<Double> valores = Estadistica.extraerValoresNumericos(datosCrudos);

        System.out.println("\n--- Medidas de Tendencia Central y Dispersión (AniosProfesional) ---");
        int n = valores.size();
        assertCondition("N (Muestra Válida)", n == 145, "N = " + n + " (esperado: 145)");

        double media = Estadistica.media(valores);
        String mediaRedondeada = String.format(Locale.US, "%.2f", media);
        assertCondition("Media aritmética", mediaRedondeada.equals("8.83"), "Media = " + media + " -> " + mediaRedondeada + " (esperado: 8.83)");

        double mediana = Estadistica.mediana(valores);
        String medianaRedondeada = String.format(Locale.US, "%.2f", mediana);
        assertCondition("Mediana", medianaRedondeada.equals("6.00"), "Mediana = " + mediana + " -> " + medianaRedondeada + " (esperado: 6.00)");

        Double moda = Estadistica.moda(valores);
        int modaCount = 0;
        if (moda != null) {
            for (Double d : valores) {
                if (Objects.equals(d, moda)) modaCount++;
            }
        }
        assertCondition("Moda y frecuencia", moda != null && Math.abs(moda - 1.0) < 1e-9 && modaCount == 23,
                "Moda = " + moda + " con frecuencia = " + modaCount + " (esperado: 1.0 con 23)");

        double varianza = Estadistica.varianza(valores);
        String varianzaRedondeada = String.format(Locale.US, "%.2f", varianza);
        assertCondition("Varianza muestral (Bessel N-1)", varianzaRedondeada.equals("62.76"),
                "Varianza = " + varianza + " -> " + varianzaRedondeada + " (esperado: 62.76)");

        double desv = Estadistica.desviacionEstandar(valores);
        String desvRedondeada = String.format(Locale.US, "%.2f", desv);
        assertCondition("Desviación estándar (Bessel N-1)", desvRedondeada.equals("7.92"),
                "Desv = " + desv + " -> " + desvRedondeada + " (esperado: 7.92)");

        double cv = Estadistica.coeficienteVariacion(valores);
        String cvRedondeado = String.format(Locale.US, "%.2f", cv);
        assertCondition("Coeficiente de Variación (%)", cvRedondeado.equals("89.71"),
                "CV = " + cv + "% -> " + cvRedondeado + "% (esperado: 89.71%)");

        System.out.println("\n--- Medidas de Posición y Detección de Atípicos (Tukey) ---");
        double[] cuartiles = Estadistica.cuartiles(valores);
        double q1 = cuartiles[0];
        double q2 = cuartiles[1];
        double q3 = cuartiles[2];
        String q1Str = String.format(Locale.US, "%.2f", q1);
        String q2Str = String.format(Locale.US, "%.2f", q2);
        String q3Str = String.format(Locale.US, "%.2f", q3);
        assertCondition("Cuartil 1 (Q1)", q1Str.equals("2.00"), "Q1 = " + q1 + " -> " + q1Str + " (esperado: 2.00)");
        assertCondition("Cuartil 2 (Q2 / Mediana)", q2Str.equals("6.00"), "Q2 = " + q2 + " -> " + q2Str + " (esperado: 6.00)");
        assertCondition("Cuartil 3 (Q3)", q3Str.equals("14.00"), "Q3 = " + q3 + " -> " + q3Str + " (esperado: 14.00)");

        double riq = Estadistica.rangoIntercuartil(valores);
        String riqStr = String.format(Locale.US, "%.2f", riq);
        assertCondition("Rango Intercuartílico (RIQ)", riqStr.equals("12.00"), "RIQ = " + riq + " -> " + riqStr + " (esperado: 12.00)");

        double p10 = Estadistica.percentil(valores, 0.10);
        double p90 = Estadistica.percentil(valores, 0.90);
        String p10Str = String.format(Locale.US, "%.2f", p10);
        String p90Str = String.format(Locale.US, "%.2f", p90);
        assertCondition("Percentil 10 (P10)", p10Str.equals("1.00"), "P10 = " + p10 + " -> " + p10Str + " (esperado: 1.00)");
        assertCondition("Percentil 90 (P90)", p90Str.equals("20.00"), "P90 = " + p90 + " -> " + p90Str + " (esperado: 20.00)");

        double[] limites = Estadistica.limitesTukey(valores);
        String liStr = String.format(Locale.US, "%.2f", limites[0]);
        String lsStr = String.format(Locale.US, "%.2f", limites[1]);
        assertCondition("Límites de Tukey [LI, LS]", liStr.equals("-16.00") && lsStr.equals("32.00"),
                "LI = " + liStr + ", LS = " + lsStr + " (esperado: [-16.00, 32.00])");

        ArrayList<Double> atipicos = Estadistica.obtenerAtipicos(valores);
        assertCondition("Atípicos Tukey detectados", atipicos.size() == 1 && Math.abs(atipicos.get(0) - 35.0) < 1e-9,
                "Atípicos = " + atipicos + " (esperado: [35.0])");

        System.out.println("\n--- Validación de Depuración de Duplicados (Emails) ---");
        Map<String, Integer> emailsDups = gestor.obtenerEmailsDuplicadosTitulados();
        int totalDuplicadosDetectados = 0;
        for (int conteo : emailsDups.values()) {
            totalDuplicadosDetectados += (conteo - 1);
        }
        assertCondition("Detección de Emails Duplicados (4 personas)", emailsDups.size() == 4,
                "Personas con envíos duplicados: " + emailsDups.size() + " (" + emailsDups + ")");

        int eliminados = gestor.depurarDuplicadosTitulados();
        assertCondition("Duplicate emails detected count == 5",
                totalDuplicadosDetectados == 5 && eliminados == 5,
                "Envíos duplicados detectados/eliminados: " + eliminados + " (esperado: 5)");

        int totalRestante = gestor.getCantidadTitulados();
        assertCondition("Total titulados tras depuración", totalRestante == 143,
                "Total registros: 148 - 5 = " + totalRestante + " (esperado: 143)");

        System.out.println("\n--- Validación de Parseo de Salarios (Tabla 7) ---");
        Double sal1 = Estadistica.parsearNumeroOIntervalo("Menor a Bs. 2500");
        Double sal2 = Estadistica.parsearNumeroOIntervalo("menor a bs. 2.500");
        Double sal3 = Estadistica.parsearNumeroOIntervalo("Menor a Bs 2500");
        assertCondition("Parseo 'Menor a Bs. 2500' -> 2000.0",
                sal1 != null && Math.abs(sal1 - 2000.0) < 1e-9 &&
                sal2 != null && Math.abs(sal2 - 2000.0) < 1e-9 &&
                sal3 != null && Math.abs(sal3 - 2000.0) < 1e-9,
                "sal1 = " + sal1 + ", sal2 = " + sal2 + ", sal3 = " + sal3);

        System.out.println("\n--- Validación de Escala Likert 1-4 (Tabla 3 y Sección 5) ---");
        // Recargar para tener dataset completo de 148 registros
        gestor.cargarCSV(archivo, true);
        ArrayList<String> rawSat = gestor.obtenerColumna(true, "SatisfaccionGlobal");
        ArrayList<Double> numSat = Estadistica.extraerValoresNumericos(rawSat);
        double mediaSat = Estadistica.media(numSat);
        String mediaSatStr = String.format(Locale.US, "%.2f", mediaSat);
        assertCondition("SatisfaccionGlobal x̄ = 2.78/4 (n=147)",
                numSat.size() == 147 && mediaSatStr.equals("2.78"),
                "N = " + numSat.size() + ", Media = " + mediaSatStr + " (esperado: 2.78)");

        ArrayList<String> rawCon = gestor.obtenerColumna(true, "ConcordanciaMercado");
        ArrayList<Double> numCon = Estadistica.extraerValoresNumericos(rawCon);
        double mediaCon = Estadistica.media(numCon);
        String mediaConStr = String.format(Locale.US, "%.2f", mediaCon);
        assertCondition("ConcordanciaMercado x̄ = 2.63/4 (n=147)",
                numCon.size() == 147 && mediaConStr.equals("2.63"),
                "N = " + numCon.size() + ", Media = " + mediaConStr + " (esperado: 2.63)");

        System.out.println("\n--- Validación de Tabla 1: Frecuencias por Intervalos (Sturges k=6) ---");
        Object[][] tabla1 = Estadistica.generarTablaFrecuenciasIntervalos(valores, 6);
        int[] fiEsperados = {72, 19, 31, 15, 7, 1};
        boolean fiMatch = true;
        for (int i = 0; i < 6; i++) {
            int obs = (int) tabla1[i][2];
            if (obs != fiEsperados[i]) fiMatch = false;
        }
        assertCondition("Tabla 1 Intervalos fi = [72, 19, 31, 15, 7, 1]",
                tabla1.length == 6 && fiMatch,
                "Frecuencias observadas: [" + tabla1[0][2] + ", " + tabla1[1][2] + ", " + tabla1[2][2] + ", " +
                        tabla1[3][2] + ", " + tabla1[4][2] + ", " + tabla1[5][2] + "]");

        System.out.println("\n--- Validación de Tabla 7: Verificaciones de Calidad de Datos ---");
        List<String> reportCalidad = gestor.validarCalidadDatos(true);
        assertCondition("Reporte Calidad Datos Titulados generado",
                reportCalidad.size() >= 4,
                "Total reglas evaluadas: " + reportCalidad.size());

        System.out.println("\n--- Validación de Tasa de Inserción Laboral (Tabla 2) ---");
        double tasaInsercion = gestor.calcularTasaInsercion();
        String tasaStr = String.format(Locale.US, "%.1f%%", tasaInsercion);
        assertCondition("Tasa de Inserción Laboral == 83.1% (Tabla 2)",
                tasaStr.equals("83.1%"),
                "Tasa calculada: " + tasaStr + " (esperado: 83.1%, 123/148 ocupados)");

        System.out.println("\n===============================================================================");
        System.out.printf("  RESUMEN: %d pruebas PASARON, %d pruebas FALLARON.%n", testsPassed, testsFailed);
        System.out.println("===============================================================================");

        if (testsFailed > 0) {
            System.err.println("ALERTA: Se encontraron pruebas fallidas.");
            System.exit(1);
        } else {
            System.out.println("TODAS LAS VERIFICACIONES ESTADÍSTICAS Y DE LIMPIEZA PASARON CON ÉXITO.");
        }
    }
}
