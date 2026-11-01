package Modelo;

import java.io.File;
import java.util.*;

/**
 * TestStressEstadistica: Dedicated QA & Statistical Stress Testing Harness
 * Covers Domain 1: Mathematical & Statistical Engine
 * Tests edge cases, nullability, precision, division by zero, statistical validity.
 */
public class TestStressEstadistica {

    private static int passed = 0;
    private static int failed = 0;
    private static final List<String> issuesFound = new ArrayList<>();

    private static void logPass(String testName, String detail) {
        System.out.printf("  [PASS] %-48s -> %s%n", testName, detail);
        passed++;
    }

    private static void logFail(String testName, String detail, String severity) {
        System.err.printf("  [FAIL - %s] %-40s -> %s%n", severity, testName, detail);
        failed++;
        issuesFound.add(String.format("[%s] %s: %s", severity, testName, detail));
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("  AUDITORÍA QA Y PRUEBAS DE ESTRÉS: MOTOR MATEMÁTICO Y ESTADÍSTICO");
        System.out.println("===============================================================================");

        testEstadisticaMedia();
        testEstadisticaMediana();
        testEstadisticaVarianzaYDesv();
        testEstadisticaCV();
        testEstadisticaModa();
        testEstadisticaPercentilYCuartiles();
        testEstadisticaTukeyAtipicos();
        testEstadisticaTablaFrecuenciasSturges();
        testEstadisticaParseoYLikert();
        testEstadisticaResumenUniversal();
        testChiCuadrada();
        testResumenEstadistico();
        testDatosEjemplo();

        System.out.println("\n===============================================================================");
        System.out.printf("  RESUMEN DE ESTRÉS: %d PRUEBAS PASARON, %d PRUEBAS DETECTARON COMPORTAMIENTOS CRÍTICOS/FALLOS.%n", passed, failed);
        System.out.println("===============================================================================");

        if (!issuesFound.isEmpty()) {
            System.out.println("\nHALLAZGOS DE AUDITORÍA DETECTADOS:");
            for (String issue : issuesFound) {
                System.out.println("  • " + issue);
            }
        }
    }

    // ── 1. Estadistica.media ────────────────────────────────────────────────
    private static void testEstadisticaMedia() {
        System.out.println("\n--- [1] Estadistica.media ---");

        // Null list
        try {
            double res = Estadistica.media(null);
            if (res == 0.0) logPass("media(null)", "Retorna 0.0 de forma segura");
            else logFail("media(null)", "Retornó " + res + " en vez de 0.0", "LOW");
        } catch (Exception e) {
            logFail("media(null)", "Lanzó excepción: " + e, "HIGH");
        }

        // Empty list
        try {
            double res = Estadistica.media(new ArrayList<>());
            if (res == 0.0) logPass("media(empty)", "Retorna 0.0 de forma segura");
            else logFail("media(empty)", "Retornó " + res + " en vez de 0.0", "LOW");
        } catch (Exception e) {
            logFail("media(empty)", "Lanzó excepción: " + e, "HIGH");
        }

        // Single element
        ArrayList<Double> single = new ArrayList<>(List.of(42.5));
        double resSingle = Estadistica.media(single);
        if (Math.abs(resSingle - 42.5) < 1e-9) logPass("media(single)", "Retorna el único valor = 42.5");
        else logFail("media(single)", "Retornó " + resSingle, "HIGH");

        // All identical
        ArrayList<Double> ident = new ArrayList<>(List.of(7.0, 7.0, 7.0, 7.0));
        double resIdent = Estadistica.media(ident);
        if (Math.abs(resIdent - 7.0) < 1e-9) logPass("media(identical)", "Retorna 7.0");
        else logFail("media(identical)", "Retornó " + resIdent, "HIGH");

        // Negative numbers
        ArrayList<Double> negs = new ArrayList<>(List.of(-10.0, -20.0, -30.0));
        double resNeg = Estadistica.media(negs);
        if (Math.abs(resNeg - (-20.0)) < 1e-9) logPass("media(negatives)", "Retorna -20.0");
        else logFail("media(negatives)", "Retornó " + resNeg, "HIGH");

        // Null element inside list
        ArrayList<Double> withNull = new ArrayList<>();
        withNull.add(10.0);
        withNull.add(null);
        withNull.add(20.0);
        try {
            double resWithNull = Estadistica.media(withNull);
            logPass("media(with null element)", "Manejó null sin crashear = " + resWithNull);
        } catch (NullPointerException npe) {
            logFail("media(with null element)", "Crash por unboxing de null (NullPointerException en línea 12)", "HIGH");
        } catch (Exception e) {
            logFail("media(with null element)", "Lanzó excepción: " + e, "HIGH");
        }

        // Numerical overflow with massive numbers
        ArrayList<Double> massive = new ArrayList<>(List.of(1e308, 1e308));
        double resMassive = Estadistica.media(massive);
        if (Double.isInfinite(resMassive)) {
            logFail("media(massive overflow)", "Suma excede Double.MAX_VALUE y produce Infinity (overflow en acumulador)", "MEDIUM");
        } else {
            logPass("media(massive overflow)", "Maneja números masivos");
        }
    }

    // ── 2. Estadistica.mediana ──────────────────────────────────────────────
    private static void testEstadisticaMediana() {
        System.out.println("\n--- [2] Estadistica.mediana ---");

        // Null & Empty
        if (Estadistica.mediana(null) == 0.0) logPass("mediana(null)", "Retorna 0.0");
        else logFail("mediana(null)", "No retornó 0.0", "LOW");

        if (Estadistica.mediana(new ArrayList<>()) == 0.0) logPass("mediana(empty)", "Retorna 0.0");
        else logFail("mediana(empty)", "No retornó 0.0", "LOW");

        // Single element
        if (Estadistica.mediana(new ArrayList<>(List.of(99.0))) == 99.0) logPass("mediana(single)", "Retorna 99.0");
        else logFail("mediana(single)", "Falló para 1 elemento", "HIGH");

        // Even vs Odd counts
        ArrayList<Double> odd = new ArrayList<>(List.of(5.0, 1.0, 9.0));
        if (Estadistica.mediana(odd) == 5.0) logPass("mediana(odd count)", "Retorna valor central = 5.0");
        else logFail("mediana(odd count)", "Falló", "HIGH");

        ArrayList<Double> even = new ArrayList<>(List.of(1.0, 3.0, 5.0, 7.0));
        if (Estadistica.mediana(even) == 4.0) logPass("mediana(even count)", "Retorna promedio central = 4.0");
        else logFail("mediana(even count)", "Falló", "HIGH");

        // List with null inside
        ArrayList<Double> withNull = new ArrayList<>();
        withNull.add(5.0);
        withNull.add(null);
        try {
            Estadistica.mediana(withNull);
            logPass("mediana(with null)", "Maneja null");
        } catch (NullPointerException npe) {
            logFail("mediana(with null)", "Collections.sort lanza NullPointerException si hay elementos null", "HIGH");
        }
    }

    // ── 3. Estadistica.varianza y desviacionEstandar ─────────────────────────
    private static void testEstadisticaVarianzaYDesv() {
        System.out.println("\n--- [3] Estadistica.varianza y desviacionEstandar ---");

        // Null & Empty
        if (Estadistica.varianza(null) == 0.0 && Estadistica.desviacionEstandar(null) == 0.0) {
            logPass("var & desv(null)", "Retornan 0.0 de forma segura");
        } else {
            logFail("var & desv(null)", "No retornaron 0.0", "LOW");
        }

        // Single element (N=1 Bessel divisor N-1=0)
        ArrayList<Double> single = new ArrayList<>(List.of(15.0));
        if (Estadistica.varianza(single) == 0.0 && Estadistica.desviacionEstandar(single) == 0.0) {
            logPass("var & desv(single)", "N=1 retorna 0.0 de forma segura (evita división por cero N-1=0)");
        } else {
            logFail("var & desv(single)", "División por cero no controlada en N=1", "CRITICAL");
        }

        // Identical values (variance = 0)
        ArrayList<Double> ident = new ArrayList<>(List.of(8.0, 8.0, 8.0, 8.0));
        if (Estadistica.varianza(ident) == 0.0 && Estadistica.desviacionEstandar(ident) == 0.0) {
            logPass("var & desv(identical)", "Varianza es exactamente 0.0");
        } else {
            logFail("var & desv(identical)", "Varianza distinta de 0 para valores idénticos", "HIGH");
        }

        // Bessel correction precision: N=2, values [2.0, 4.0]. Mean=3.0. Diffs: [-1, 1]. Sum squares = 2.0. N-1 = 1. Var = 2.0. Desv = sqrt(2).
        ArrayList<Double> n2 = new ArrayList<>(List.of(2.0, 4.0));
        double var = Estadistica.varianza(n2);
        double s = Estadistica.desviacionEstandar(n2);
        if (Math.abs(var - 2.0) < 1e-9 && Math.abs(s - Math.sqrt(2.0)) < 1e-9) {
            logPass("var & desv(Bessel N-1)", "Cálculo exacto con corrección de Bessel: s²=2.0, s=√2");
        } else {
            logFail("var & desv(Bessel N-1)", "Fórmula incorrecta: var=" + var + ", s=" + s, "CRITICAL");
        }

        // Negative values
        ArrayList<Double> negs = new ArrayList<>(List.of(-10.0, -20.0));
        double sNeg = Estadistica.desviacionEstandar(negs);
        if (Math.abs(sNeg - Math.sqrt(50.0)) < 1e-9) {
            logPass("var & desv(negatives)", "Calcula correctamente con valores negativos");
        } else {
            logFail("var & desv(negatives)", "Falló con negativos: " + sNeg, "HIGH");
        }

        // Null element inside list
        ArrayList<Double> withNull = new ArrayList<>();
        withNull.add(10.0);
        withNull.add(null);
        withNull.add(20.0);
        try {
            Estadistica.varianza(withNull);
            logPass("varianza(with null)", "Maneja null");
        } catch (NullPointerException npe) {
            logFail("varianza(with null)", "Crash por unboxing de null (NullPointerException en línea 42)", "HIGH");
        }
    }

    // ── 4. Estadistica.coeficienteVariacion ──────────────────────────────────
    private static void testEstadisticaCV() {
        System.out.println("\n--- [4] Estadistica.coeficienteVariacion ---");

        // Mean = 0 (Division by zero guard)
        ArrayList<Double> zeroMean = new ArrayList<>(List.of(-5.0, 5.0));
        try {
            double cvZero = Estadistica.coeficienteVariacion(zeroMean);
            if (cvZero == 0.0) {
                logPass("CV(mean == 0)", "Protección contra división por cero activa (retorna 0.0)");
            } else {
                logFail("CV(mean == 0)", "Retornó " + cvZero + " para media 0", "HIGH");
            }
        } catch (Exception e) {
            logFail("CV(mean == 0)", "Lanzó excepción: " + e, "CRITICAL");
        }

        // Negative mean
        ArrayList<Double> negMean = new ArrayList<>(List.of(-10.0, -20.0)); // mean = -15, s = 7.071
        double cvNeg = Estadistica.coeficienteVariacion(negMean);
        if (cvNeg < 0) {
            logFail("CV(negative mean)", "CV resultó negativo (" + cvNeg + "%). El CV debe usar |media| o advertir escala no positiva", "MEDIUM");
        } else {
            logPass("CV(negative mean)", "CV es positivo");
        }

        // Null & Empty
        if (Estadistica.coeficienteVariacion(null) == 0.0 && Estadistica.coeficienteVariacion(new ArrayList<>()) == 0.0) {
            logPass("CV(null & empty)", "Retorna 0.0 de forma segura");
        } else {
            logFail("CV(null & empty)", "No retornó 0.0", "LOW");
        }
    }

    // ── 5. Estadistica.moda ─────────────────────────────────────────────────
    private static void testEstadisticaModa() {
        System.out.println("\n--- [5] Estadistica.moda ---");

        if (Estadistica.moda(null) == null) logPass("moda(null)", "Retorna null");
        else logFail("moda(null)", "No retornó null", "LOW");

        if (Estadistica.moda(new ArrayList<>()) == null) logPass("moda(empty)", "Retorna null");
        else logFail("moda(empty)", "No retornó null", "LOW");

        // List of all nulls
        ArrayList<Double> allNulls = new ArrayList<>();
        allNulls.add(null);
        allNulls.add(null);
        try {
            Double mNulls = Estadistica.moda(allNulls);
            if (mNulls == null) logPass("moda(all nulls)", "Retorna null de forma segura");
            else logFail("moda(all nulls)", "Retornó " + mNulls, "LOW");
        } catch (Exception e) {
            logFail("moda(all nulls)", "Lanzó excepción: " + e, "HIGH");
        }

        // List with null and values
        ArrayList<Double> withNull = new ArrayList<>();
        withNull.add(10.0);
        withNull.add(null);
        withNull.add(10.0);
        withNull.add(20.0);
        try {
            Double m = Estadistica.moda(withNull);
            if (m != null && m == 10.0) logPass("moda(with null)", "Filtra nulls y calcula moda = 10.0");
            else logFail("moda(with null)", "Falló moda con nulls: " + m, "HIGH");
        } catch (Exception e) {
            logFail("moda(with null)", "Lanzó excepción: " + e, "HIGH");
        }
    }

    // ── 6. Estadistica.percentil y cuartiles ─────────────────────────────────
    private static void testEstadisticaPercentilYCuartiles() {
        System.out.println("\n--- [6] Estadistica.percentil y cuartiles ---");

        // Null & Empty
        if (Estadistica.percentil(null, 0.5) == 0.0 && Estadistica.percentil(new ArrayList<>(), 0.5) == 0.0) {
            logPass("percentil(null & empty)", "Retorna 0.0 de forma segura");
        } else {
            logFail("percentil(null & empty)", "No retornó 0.0", "LOW");
        }

        // Single element (N=1)
        ArrayList<Double> single = new ArrayList<>(List.of(77.0));
        if (Estadistica.percentil(single, 0.25) == 77.0 && Estadistica.percentil(single, 0.75) == 77.0) {
            logPass("percentil(single N=1)", "Retorna el único valor para cualquier p");
        } else {
            logFail("percentil(single N=1)", "Falló con N=1", "HIGH");
        }

        // Out of bounds p (< 0 or > 1)
        ArrayList<Double> data = new ArrayList<>(List.of(10.0, 20.0, 30.0));
        double pMin = Estadistica.percentil(data, -0.5);
        double pMax = Estadistica.percentil(data, 1.5);
        if (pMin == 10.0 && pMax == 30.0) {
            logPass("percentil(p < 0 y p > 1)", "Clampeado correcto a los límites mínimo y máximo");
        } else {
            logFail("percentil(p < 0 y p > 1)", "No clampeó correctamente: pMin=" + pMin + ", pMax=" + pMax, "HIGH");
        }

        // p = NaN
        double pNaN = Estadistica.percentil(data, Double.NaN);
        if (Double.isNaN(pNaN)) {
            logFail("percentil(p=NaN)", "Propaga NaN", "LOW");
        } else {
            logPass("percentil(p=NaN)", "Retornó " + pNaN + " sin crashear");
        }

        // Cuartiles: check Q1, Q2, Q3 formulas
        double[] q = Estadistica.cuartiles(data);
        if (q != null && q.length == 3 && q[0] == 15.0 && q[1] == 20.0 && q[2] == 25.0) {
            logPass("cuartiles(interpolación lineal)", "Cuartiles Q1=15.0, Q2=20.0, Q3=25.0 correctos");
        } else {
            logFail("cuartiles(interpolación lineal)", "Cuartiles inesperados: " + Arrays.toString(q), "HIGH");
        }
    }

    // ── 7. Estadistica.limitesTukey y obtenerAtipicos ────────────────────────
    private static void testEstadisticaTukeyAtipicos() {
        System.out.println("\n--- [7] Estadistica.limitesTukey y obtenerAtipicos ---");

        // Identical values (IQR = 0)
        ArrayList<Double> ident = new ArrayList<>(List.of(5.0, 5.0, 5.0, 5.0, 5.0));
        double[] limites = Estadistica.limitesTukey(ident);
        ArrayList<Double> atipicosIdent = Estadistica.obtenerAtipicos(ident);
        if (limites[0] == 5.0 && limites[1] == 5.0 && atipicosIdent.isEmpty()) {
            logPass("Tukey(identical values, IQR=0)", "LI=5.0, LS=5.0, cero atípicos detectados");
        } else {
            logFail("Tukey(identical values, IQR=0)", "Falló: LI=" + limites[0] + ", LS=" + limites[1], "HIGH");
        }

        // Clear outliers test
        ArrayList<Double> withOutlier = new ArrayList<>(List.of(-50.0, 10.0, 11.0, 12.0, 13.0, 14.0, 100.0));
        ArrayList<Double> atip = Estadistica.obtenerAtipicos(withOutlier);
        if (atip.contains(-50.0) && atip.contains(100.0) && atip.size() == 2) {
            logPass("Tukey(outliers inferior y superior)", "Detecta correctamente -50.0 y 100.0");
        } else {
            logFail("Tukey(outliers inferior y superior)", "Falló al detectar atípicos: " + atip, "HIGH");
        }

        // Null list
        if (Estadistica.obtenerAtipicos(null).isEmpty()) {
            logPass("obtenerAtipicos(null)", "Retorna lista vacía segura");
        } else {
            logFail("obtenerAtipicos(null)", "No retornó lista vacía", "LOW");
        }
    }

    // ── 8. Estadistica.generarTablaFrecuenciasIntervalos ─────────────────────
    private static void testEstadisticaTablaFrecuenciasSturges() {
        System.out.println("\n--- [8] Estadistica.generarTablaFrecuenciasIntervalos (Sturges) ---");

        // Null & Empty
        if (Estadistica.generarTablaFrecuenciasIntervalos(null, 6).length == 0 &&
            Estadistica.generarTablaFrecuenciasIntervalos(new ArrayList<>(), 6).length == 0) {
            logPass("tablaFrecuencias(null & empty)", "Retorna matriz 0x0 segura");
        } else {
            logFail("tablaFrecuencias(null & empty)", "Falló con null o empty", "LOW");
        }

        // numClases <= 0
        if (Estadistica.generarTablaFrecuenciasIntervalos(new ArrayList<>(List.of(1.0, 2.0)), 0).length == 0) {
            logPass("tablaFrecuencias(numClases <= 0)", "Retorna matriz 0x0 segura");
        } else {
            logFail("tablaFrecuencias(numClases <= 0)", "No controló numClases <= 0", "MEDIUM");
        }

        // Edge case: min == max (Single unique value or single element)
        ArrayList<Double> singleVal = new ArrayList<>(List.of(10.0, 10.0, 10.0));
        try {
            Object[][] tablaSingle = Estadistica.generarTablaFrecuenciasIntervalos(singleVal, 5);
            // Verify if amplitude was 0, resulting in NaN division (d - min)/amplitud
            double amplitud = (10.0 - 10.0) / 5;
            if (amplitud == 0.0) {
                // Check if intervals look degenerate [10.0 - 10.0]
                String int0 = (String) tablaSingle[0][0];
                int f0 = (int) tablaSingle[0][2];
                if (f0 == 3) {
                    logFail("tablaFrecuencias(min == max)", "Amplitud es 0.0 generando división 0/0 (NaN). Todos los datos caen en clase 0: " + int0 + " con intervalos degenerados", "HIGH");
                } else {
                    logPass("tablaFrecuencias(min == max)", "Manejó min==max");
                }
            }
        } catch (Exception e) {
            logFail("tablaFrecuencias(min == max)", "Lanzó excepción: " + e, "CRITICAL");
        }

        // Edge case: List with null values
        ArrayList<Double> withNulls = new ArrayList<>();
        withNulls.add(2.0);
        withNulls.add(null);
        withNulls.add(8.0);
        try {
            Object[][] tablaNull = Estadistica.generarTablaFrecuenciasIntervalos(withNulls, 2);
            int totalFi = (int) tablaNull[0][2] + (int) tablaNull[1][2];
            String fiPctFinal = (String) tablaNull[1][5]; // accumulated %
            if (totalFi == 2 && !fiPctFinal.equals("100.0%")) {
                logFail("tablaFrecuencias(con nulls)", "n = datos.size() incluye nulls (n=3), por lo que fiPct acumulado da " + fiPctFinal + " en lugar de 100.0%", "HIGH");
            } else {
                logPass("tablaFrecuencias(con nulls)", "Manejó conteo de nulls");
            }
        } catch (Exception e) {
            logFail("tablaFrecuencias(con nulls)", "Lanzó excepción: " + e, "HIGH");
        }

        // Edge case: All null elements in list
        ArrayList<Double> allNulls = new ArrayList<>();
        allNulls.add(null);
        allNulls.add(null);
        try {
            Object[][] tablaAllNull = Estadistica.generarTablaFrecuenciasIntervalos(allNulls, 3);
            if (tablaAllNull == null || tablaAllNull.length == 0) {
                logPass("tablaFrecuencias(todos nulls)", "Maneja todos nulls retornando matriz vacía segura");
            } else {
                String limStr = (String) tablaAllNull[0][0];
                if (limStr.contains("Infinity") || limStr.contains("1.8e+308") || limStr.contains("17976931348623157")) {
                    logFail("tablaFrecuencias(todos nulls)", "Genera intervalos con Double.MAX_VALUE / -Infinity por falta de datos válidos: " + limStr, "HIGH");
                } else {
                    logPass("tablaFrecuencias(todos nulls)", "Maneja todos nulls");
                }
            }
        } catch (Exception e) {
            logFail("tablaFrecuencias(todos nulls)", "Lanzó excepción: " + e, "HIGH");
        }
    }

    // ── 9. Estadistica.parsearNumeroOIntervalo y mapearLikert ────────────────
    private static void testEstadisticaParseoYLikert() {
        System.out.println("\n--- [9] Estadistica.parsearNumeroOIntervalo y mapearLikert ---");

        // 1. Literal "NaN" and "Infinity" injection
        Double pNaN = Estadistica.parsearNumeroOIntervalo("NaN");
        if (pNaN != null && Double.isNaN(pNaN)) {
            logFail("parsearNumero('NaN')", "Double.parseDouble parsea la cadena 'NaN' a Double.NaN, propagando NaNs silenciosamente", "CRITICAL");
        } else {
            logPass("parsearNumero('NaN')", "Filtró 'NaN'");
        }

        Double pInf = Estadistica.parsearNumeroOIntervalo("Infinity");
        if (pInf != null && Double.isInfinite(pInf)) {
            logFail("parsearNumero('Infinity')", "Double.parseDouble parsea 'Infinity' a Double.POSITIVE_INFINITY", "CRITICAL");
        } else {
            logPass("parsearNumero('Infinity')", "Filtró 'Infinity'");
        }

        // 2. All branches of mapearLikert
        String[][] likertCases = {
                {"Totalmente en desacuerdo", "1.0"},
                {"Totalmente desacuerdo", "1.0"},
                {"muy insatisfecho", "1.0"},
                {"muy malo", "1.0"},
                {"Totalmente de acuerdo", "4.0"},
                {"muy de acuerdo", "4.0"},
                {"muy satisfecho", "4.0"},
                {"excelente", "4.0"},
                {"En desacuerdo", "2.0"},
                {"desacuerdo", "2.0"},
                {"insatisfecho", "2.0"},
                {"malo", "2.0"},
                {"Parcialmente de acuerdo", "3.0"},
                {"de acuerdo", "3.0"},
                {"satisfecho", "3.0"},
                {"bueno", "3.0"},
                {"neutral", "2.5"},
                {"ni de acuerdo", "2.5"},
                {"regular", "2.5"},
                {"algo satisfecho", "2.5"},
                {"No sabe", "null"}
        };

        boolean likertAllPass = true;
        for (String[] tc : likertCases) {
            Double val = Estadistica.mapearLikert(tc[0]);
            String expected = tc[1];
            if (expected.equals("null")) {
                if (val != null) {
                    likertAllPass = false;
                    logFail("mapearLikert('" + tc[0] + "')", "Esperaba null pero obtuvo " + val, "HIGH");
                }
            } else {
                double expVal = Double.parseDouble(expected);
                if (val == null || Math.abs(val - expVal) > 1e-9) {
                    likertAllPass = false;
                    logFail("mapearLikert('" + tc[0] + "')", "Esperaba " + expVal + " pero obtuvo " + val, "HIGH");
                }
            }
        }
        if (likertAllPass) {
            logPass("mapearLikert(21 ramas y sinónimos)", "Todas las ramas Likert mapearon exactamente a los valores esperados (1.0, 2.0, 2.5, 3.0, 4.0, null)");
        }

        // 3. Complex number parsing
        Double s1 = Estadistica.parsearNumeroOIntervalo("Menor a Bs. 2500");
        Double s2 = Estadistica.parsearNumeroOIntervalo("Entre Bs. 2.500 - Bs. 3.500");
        Double s3 = Estadistica.parsearNumeroOIntervalo("Entre Bs. 3.501 - Bs. 5.000");
        Double s4 = Estadistica.parsearNumeroOIntervalo("Entre Bs. 5.001 - Bs. 7.000");
        Double s5 = Estadistica.parsearNumeroOIntervalo("Entre Bs. 7.001 - Bs. 10.000");
        Double s6 = Estadistica.parsearNumeroOIntervalo("Más de Bs. 10.000");

        if (s1 == 2000.0 && s2 == 3000.0 && s3 == 4250.0 && s4 == 6000.0 && s5 == 8500.0 && s6 == 12000.0) {
            logPass("parsearNumero(escalas salariales Bs.)", "Mapeo salarial completo verificado");
        } else {
            logFail("parsearNumero(escalas salariales Bs.)", "Fallo en escalas salariales: " + List.of(s1, s2, s3, s4, s5, s6), "HIGH");
        }

        // 4. Age ranges and general intervals
        Double r1 = Estadistica.parsearNumeroOIntervalo("23 - 26 años");
        Double r2 = Estadistica.parsearNumeroOIntervalo("35 años o más");
        Double r3 = Estadistica.parsearNumeroOIntervalo("menos de 1 año");
        Double r4 = Estadistica.parsearNumeroOIntervalo("1 a 2 años");
        Double r5 = Estadistica.parsearNumeroOIntervalo("ya trabajaba antes");

        if (r1 == 24.5 && r2 == 37.0 && r3 == 0.5 && r4 == 1.5 && r5 == 0.0) {
            logPass("parsearNumero(intervalos temporales/edad)", "Mapeo de intervalos temporales correcto");
        } else {
            logFail("parsearNumero(intervalos temporales/edad)", "Falló en intervalos temporales", "HIGH");
        }

        // 5. Weird input strings
        Double w1 = Estadistica.parsearNumeroOIntervalo("   ");
        Double w2 = Estadistica.parsearNumeroOIntervalo("Texto Sin Numeros");
        Double w3 = Estadistica.parsearNumeroOIntervalo("!@#$%^&*()");
        if (w1 == null && w2 == null && w3 == null) {
            logPass("parsearNumero(weird strings)", "Retorna null de forma segura ante basura textual");
        } else {
            logFail("parsearNumero(weird strings)", "No retornó null para basura textual", "MEDIUM");
        }
    }

    // ── 10. Estadistica.calcularResumenColumna ──────────────────────────────
    private static void testEstadisticaResumenUniversal() {
        System.out.println("\n--- [10] Estadistica.calcularResumenColumna (Resumen Universal) ---");

        // Null & Empty
        ResumenEstadistico rNull = Estadistica.calcularResumenColumna(null, "Col");
        if (rNull.n == 0) logPass("resumenColumna(null)", "Retorna ResumenEstadistico N=0 seguro");
        else logFail("resumenColumna(null)", "Falló con null", "LOW");

        // Edge case: Multiple semicolon string that splits into empty strings: [";;;"]
        ArrayList<Double> crashCheck = new ArrayList<>();
        ArrayList<String> semiOnly = new ArrayList<>(List.of(";;;"));
        try {
            ResumenEstadistico rSemi = Estadistica.calcularResumenColumna(semiOnly, "ColSemicolon");
            logPass("resumenColumna(';;;') nominal", "Maneja split con strings vacíos sin crash");
        } catch (IndexOutOfBoundsException iobe) {
            logFail("resumenColumna(';;;') nominal", "IndexOutOfBoundsException en línea 399 al intentar lista.get(0) en frecuencias vacías", "HIGH");
        } catch (Exception e) {
            logFail("resumenColumna(';;;') nominal", "Lanzó excepción: " + e, "HIGH");
        }

        // Single categorical value
        ArrayList<String> singleCat = new ArrayList<>(List.of("Privado", "Privado", "Privado"));
        ResumenEstadistico rSingleCat = Estadistica.calcularResumenColumna(singleCat, "TipoOrg");
        if (rSingleCat != null && rSingleCat.n == 3) {
            logPass("resumenColumna(single category)", "Genera resumen categórico con 1 categoría correctamente");
        } else {
            logFail("resumenColumna(single category)", "Falló con 1 categoría", "HIGH");
        }

        // Binary 2 categories
        ArrayList<String> binCat = new ArrayList<>(List.of("SI", "NO", "SI", "SI"));
        ResumenEstadistico rBin = Estadistica.calcularResumenColumna(binCat, "RespBinaria");
        if (rBin != null && rBin.titulo3.equals("Desv. Binomial") && rBin.n == 4) {
            logPass("resumenColumna(binario)", "Detecta binario y calcula desviación binomial");
        } else {
            logFail("resumenColumna(binario)", "No reconoció formato binario", "HIGH");
        }
    }

    // ── 11. ChiCuadrada ─────────────────────────────────────────────────────
    private static void testChiCuadrada() {
        System.out.println("\n--- [11] ChiCuadrada.calcularChiCuadrada ---");

        // Null lists check
        try {
            ChiCuadrada.calcularChiCuadrada(null, new ArrayList<>());
            logPass("ChiCuadrada(null)", "Maneja null");
        } catch (NullPointerException npe) {
            logFail("ChiCuadrada(null)", "NullPointerException en línea 10: falta if (var1 == null || var2 == null)", "HIGH");
        }

        // Empty lists check
        Object[] rEmpty = ChiCuadrada.calcularChiCuadrada(new ArrayList<>(), new ArrayList<>());
        if (rEmpty != null && rEmpty[3].equals("Error en datos")) {
            logPass("ChiCuadrada(empty)", "Retorna 'Error en datos' correctamente");
        } else {
            logFail("ChiCuadrada(empty)", "No retornó error en datos", "LOW");
        }

        // Unequal size lists check
        ArrayList<String> v1 = new ArrayList<>(List.of("A", "B"));
        ArrayList<String> v2 = new ArrayList<>(List.of("X"));
        Object[] rUnequal = ChiCuadrada.calcularChiCuadrada(v1, v2);
        if (rUnequal[3].equals("Error en datos")) {
            logPass("ChiCuadrada(unequal sizes)", "Detecta tamaños desiguales y retorna error");
        } else {
            logFail("ChiCuadrada(unequal sizes)", "No detectó tamaños desiguales", "LOW");
        }

        // List containing null strings
        ArrayList<String> vNull1 = new ArrayList<>();
        vNull1.add("A");
        vNull1.add(null);
        ArrayList<String> vNull2 = new ArrayList<>();
        vNull2.add("X");
        vNull2.add("Y");
        try {
            ChiCuadrada.calcularChiCuadrada(vNull1, vNull2);
            logPass("ChiCuadrada(with null string)", "Maneja null");
        } catch (NullPointerException npe) {
            logFail("ChiCuadrada(with null string)", "NullPointerException en obtenerUnicos() por item.isEmpty() cuando item es null", "HIGH");
        }

        // 1x1 contingency table (single category in both variables)
        ArrayList<String> s1 = new ArrayList<>(List.of("A", "A", "A"));
        ArrayList<String> s2 = new ArrayList<>(List.of("X", "X", "X"));
        Object[] r1x1 = ChiCuadrada.calcularChiCuadrada(s1, s2);
        int gl1x1 = (int) r1x1[2];
        String conc1x1 = (String) r1x1[3];
        if (gl1x1 == 0 && conc1x1.contains("Datos insuficientes para Chi²")) {
            logPass("ChiCuadrada(1x1 df=0)", "GL=0 detectado y concluye 'Datos insuficientes para Chi²'");
        } else {
            logFail("ChiCuadrada(1x1 df=0)", "No manejó tabla 1x1: GL=" + gl1x1 + ", conclusión: " + conc1x1, "HIGH");
        }

        // 2x2 Valid Chi-Square calculation
        // Row 1: 20 A-X, 10 A-Y (Total A = 30)
        // Row 2: 10 B-X, 20 B-Y (Total B = 30)
        // Col X: 30, Col Y: 30. Total N = 60.
        // Expected for all cells = (30 * 30)/60 = 15.0.
        // Chi² = (20-15)²/15 + (10-15)²/15 + (10-15)²/15 + (20-15)²/15 = 25/15 * 4 = 100/15 = 6.6667
        // GL = (2-1)*(2-1) = 1.
        // Cramer's V = sqrt(6.6667 / (60 * 1)) = sqrt(0.1111) = 0.3333 -> "Asociación moderada"
        ArrayList<String> testV1 = new ArrayList<>();
        ArrayList<String> testV2 = new ArrayList<>();
        for (int i = 0; i < 20; i++) { testV1.add("A"); testV2.add("X"); }
        for (int i = 0; i < 10; i++) { testV1.add("A"); testV2.add("Y"); }
        for (int i = 0; i < 10; i++) { testV1.add("B"); testV2.add("X"); }
        for (int i = 0; i < 20; i++) { testV1.add("B"); testV2.add("Y"); }

        Object[] rValid = ChiCuadrada.calcularChiCuadrada(testV1, testV2);
        double chiVal = (double) rValid[1];
        int glVal = (int) rValid[2];
        String concVal = (String) rValid[3];

        if (Math.abs(chiVal - 6.6667) < 0.01 && glVal == 1 && concVal.contains("0.333")) {
            logPass("ChiCuadrada(2x2 exact test)", "Chi² = 6.67, GL = 1, V de Cramér = 0.333 (Asociación moderada)");
        } else {
            logFail("ChiCuadrada(2x2 exact test)", "Cálculo incorrecto: Chi²=" + chiVal + ", GL=" + glVal + ", Conc=" + concVal, "CRITICAL");
        }

        // Statistical Validity: Cochran's Rule Check (Expected frequencies < 5)
        // If sample size is very small, e.g., N=10 in 2x2 table:
        ArrayList<String> smallV1 = new ArrayList<>(List.of("A", "A", "A", "A", "A", "B", "B", "B", "B", "B"));
        ArrayList<String> smallV2 = new ArrayList<>(List.of("X", "X", "Y", "Y", "Y", "X", "Y", "Y", "Y", "Y"));
        Object[] rSmall = ChiCuadrada.calcularChiCuadrada(smallV1, smallV2);
        String smallConc = (String) rSmall[3];
        if (!smallConc.toLowerCase().contains("cochran") && !smallConc.toLowerCase().contains("advertencia") && !smallConc.toLowerCase().contains("< 5")) {
            logFail("ChiCuadrada(Regla de Cochran)", "No advierte cuando frecuencias esperadas son menores a 5 (Regla de Cochran no implementada)", "MEDIUM");
        } else {
            logPass("ChiCuadrada(Regla de Cochran)", "Advierte regla de Cochran");
        }

        // Statistical Validity: Check column marginal totals in matrizResultados
        Object[][] mat = (Object[][]) rValid[0];
        // Rows: header row + 2 data rows = 3 rows.
        // Are column marginal totals included in the bottom row?
        int numRows = mat.length;
        if (numRows == 3) {
            logFail("ChiCuadrada(Marginales de Columna)", "matrizResultados solo tiene filas de datos y marginales de fila; falta la fila final de Totales Marginales de Columna", "LOW");
        } else {
            logPass("ChiCuadrada(Marginales de Columna)", "Marginales de columna presentes");
        }
    }

    // ── 12. ResumenEstadistico ──────────────────────────────────────────────
    private static void testResumenEstadistico() {
        System.out.println("\n--- [12] ResumenEstadistico ---");

        // Factory crearCuantitativo
        ResumenEstadistico cuan = ResumenEstadistico.crearCuantitativo(10.0, 8.0, 2.5, 5.0, 20.0, 100);
        if (cuan.media == 10.0 && cuan.n == 100 && cuan.titulo1.contains("Media")) {
            logPass("ResumenEstadistico.crearCuantitativo", "Campos cuantitativos asignados correctamente");
        } else {
            logFail("ResumenEstadistico.crearCuantitativo", "Falló constructor cuantitativo", "HIGH");
        }

        // Factory crearLikert
        ResumenEstadistico lik = ResumenEstadistico.crearLikert(3.2, 3.0, 0.8, 1.0, 4.0, 50, 4.0);
        if (lik.valor1.contains("/ 4") && lik.desviacion == 0.8) {
            logPass("ResumenEstadistico.crearLikert", "Puntaje medio formateado como '3.20 / 4'");
        } else {
            logFail("ResumenEstadistico.crearLikert", "Falló formato Likert: " + lik.valor1, "HIGH");
        }

        // Factory crearBinario: check negative square root prevention
        // If countA > n (invalid counts passed)
        try {
            ResumenEstadistico binInvalid = ResumenEstadistico.crearBinario("SI", 15, "NO", 0, 10);
            if (Double.isNaN(binInvalid.desviacion)) {
                logFail("ResumenEstadistico.crearBinario(p > 1)", "p = 15/10 = 1.5 produce p*(1-p) < 0, generando NaN en Math.sqrt", "MEDIUM");
            } else {
                logPass("ResumenEstadistico.crearBinario(p > 1)", "Protegido contra NaN en raíz");
            }
        } catch (Exception e) {
            logFail("ResumenEstadistico.crearBinario", "Excepción: " + e, "HIGH");
        }

        // Factory crearNominal
        ResumenEstadistico nom = ResumenEstadistico.crearNominal("Privado", 40, "Público", 13, 2, 13, 40, 53);
        if (nom.valor1.contains("Privado") && nom.valor1.contains("75.5%") && nom.n == 53) {
            logPass("ResumenEstadistico.crearNominal", "Resumen nominal con porcentajes correcto");
        } else {
            logFail("ResumenEstadistico.crearNominal", "Fallo en resumen nominal: " + nom.valor1, "HIGH");
        }
    }

    // ── 13. DatosEjemplo ────────────────────────────────────────────────────
    private static void testDatosEjemplo() {
        System.out.println("\n--- [13] DatosEjemplo ---");

        // Null gestor check
        try {
            DatosEjemplo.cargarDatos(null);
            logPass("DatosEjemplo(null)", "Maneja null");
        } catch (NullPointerException npe) {
            logFail("DatosEjemplo(null)", "NullPointerException al llamar gestor.cargarDatosEjemplo() con gestor == null", "LOW");
        }

        // Integrity test with a real GestorDatos instance
        try {
            GestorDatos gestor = new GestorDatos();
            DatosEjemplo.cargarDatos(gestor);
            int titCount = gestor.getCantidadTitulados();
            int empCount = gestor.getCantidadEmpleadores();

            if (titCount == 150 && empCount == 50) {
                logPass("DatosEjemplo.cargarDatos(gestor)", "Cargó exitosamente 150 titulados y 50 empleadores simulados");
            } else {
                logFail("DatosEjemplo.cargarDatos(gestor)", "Conteos inesperados: titulados=" + titCount + ", empleadores=" + empCount, "HIGH");
            }

            // Verify if numeric columns generated in DatosEjemplo parse without crashing
            ArrayList<String> anios = gestor.obtenerColumna(true, "AniosProfesional");
            ArrayList<Double> aniosNum = Estadistica.extraerValoresNumericos(anios);
            if (aniosNum.size() == 150) {
                logPass("DatosEjemplo(parseo AniosProfesional)", "Los 150 registros numéricos se parsearon limpiamente");
            } else {
                logFail("DatosEjemplo(parseo AniosProfesional)", "Parseó " + aniosNum.size() + " de 150", "HIGH");
            }
        } catch (Exception e) {
            logFail("DatosEjemplo.cargarDatos", "Excepción durante ejecución: " + e, "HIGH");
        }
    }
}
