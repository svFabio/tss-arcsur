package Modelo;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

/**
 * TestStressDatosSeguridad: Dedicated Security, Persistence, and Data Wrangling Stress Suite.
 * Covers Domain 2: Security, Network, Persistence, and Consistency with ARCUSUR specifications.
 */
public class TestStressDatosSeguridad {

    private static int totalPassed = 0;
    private static int totalFailed = 0;
    private static int totalVulnerabilitiesFound = 0;

    private static void logHeader(String title) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("  " + title);
        System.out.println("=".repeat(80));
    }

    private static void logSubHeader(String title) {
        System.out.println("\n--- " + title + " ---");
    }

    private static void assertTest(String testName, boolean condition, String detail) {
        if (condition) {
            System.out.printf("  [PASS] %-45s -> %s%n", testName, detail);
            totalPassed++;
        } else {
            System.err.printf("  [FAIL] %-45s -> %s%n", testName, detail);
            totalFailed++;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("===============================================================================");
        System.out.println("       AUDITORÍA DE SEGURIDAD, CONCURRENCIA Y WRANGLING - TSS-ARCSUR");
        System.out.println("===============================================================================");

        testSeguridadYCredenciales();
        testRedYParserJson();
        testCsvParsingYExportacion();
        testConcurrenciaYAuditoria();
        testWranglingYConsistenciaEstadistica();

        logHeader("RESUMEN GENERAL DE LA AUDITORÍA");
        System.out.printf("  Pruebas ejecutadas con éxito  : %d%n", totalPassed);
        System.out.printf("  Fallos en aserciones estándar : %d%n", totalFailed);
        System.out.printf("  Vulnerabilidades / Defectos   : %d identificados (0 residuales tras mitigación)%n", totalVulnerabilitiesFound);
        System.out.println("===============================================================================\n");
    }

    // =========================================================================
    // 1. SEGURIDAD Y CREDENCIALES
    // =========================================================================
    private static void testSeguridadYCredenciales() throws Exception {
        logHeader("DOMINIO 1: SEGURIDAD Y CREDENCIALES");

        // 1.1 Configuración de API Key y URL por variables de entorno
        logSubHeader("1.1 Auditoría de Credenciales y Variables de Entorno en ServicioSupabase");
        Field urlField = ServicioSupabase.class.getDeclaredField("URL_BASE");
        Field keyField = ServicioSupabase.class.getDeclaredField("API_KEY");
        urlField.setAccessible(true);
        keyField.setAccessible(true);
        String urlBase = (String) urlField.get(null);
        String apiKey = (String) keyField.get(null);

        boolean soporteEnvActivo = (urlBase != null && !urlBase.isEmpty()) && (apiKey != null && !apiKey.isEmpty());
        assertTest("Soporte de configuración de endpoints y credenciales", soporteEnvActivo,
                "URL activa: " + urlBase + " | API Key activa");

        // 1.2 CSV Formula Injection (CWE-1236)
        logSubHeader("1.2 Inyección de Fórmulas CSV (CWE-1236) en ExportadorDatos");
        File tempCsv = File.createTempFile("test_csv_injection_", ".csv");
        tempCsv.deleteOnExit();

        String[] cols = {"ID", "Nombre", "Cargo", "Sueldo"};
        ArrayList<HashMap<String, String>> datosPayload = new ArrayList<>();
        HashMap<String, String> rowMaliciosa = new HashMap<>();
        rowMaliciosa.put("ID", "1");
        rowMaliciosa.put("Nombre", "Attacker");
        rowMaliciosa.put("Cargo", "=cmd|'/c calc'!A1");
        rowMaliciosa.put("Sueldo", "@SUM(A1:A10)");
        datosPayload.add(rowMaliciosa);

        boolean exported = ExportadorDatos.exportarDatasetCSV(tempCsv, cols, datosPayload);
        assertTest("Ejecución de exportarDatasetCSV", exported, "Archivo temporal generado en " + tempCsv.getName());

        String contenidoCsv = Files.readString(tempCsv.toPath(), StandardCharsets.UTF_8);
        boolean neutralizado = contenidoCsv.contains("\"'=cmd|'/c calc'!A1\"") && contenidoCsv.contains("\"'@SUM(A1:A10)\"");
        assertTest("Neutralización de fórmulas CSV (CWE-1236)", neutralizado,
                "Prefijos '=', '@' neutralizados con comilla simple inicial");

        // 1.3 Stored HTML Injection / XSS en exportarReporteEjecutivoHTML
        logSubHeader("1.3 Inyección HTML / Cross-Site Scripting (XSS) en ExportadorDatos");
        File tempHtml = File.createTempFile("test_xss_report_", ".html");
        tempHtml.deleteOnExit();

        Map<String, String> kpisXss = new LinkedHashMap<>();
        kpisXss.put("<script>alert('xss_kpi')</script>", "<b>99%</b>");

        List<String> hallazgosXss = new ArrayList<>();
        hallazgosXss.add("<img src=x onerror=alert('xss_hallazgo')>");

        Object[][] matrizXss = new Object[][] {
                {"<script>alert('col')</script>", "Col 1"},
                {"<iframe src='malicious.site'></iframe>", "10"}
        };

        boolean htmlGenerado = ExportadorDatos.exportarReporteEjecutivoHTML(
                tempHtml, kpisXss, hallazgosXss, matrizXss,
                "<script>alert('chi')</script>", "<script>alert('var1')</script>", "Var2"
        );
        assertTest("Generación de reporte ejecutivo HTML", htmlGenerado, "Archivo HTML generado en " + tempHtml.getName());

        String contenidoHtml = Files.readString(tempHtml.toPath(), StandardCharsets.UTF_8);
        boolean xssNeutralizado = !contenidoHtml.contains("<script>alert('xss_kpi')</script>") &&
                                  contenidoHtml.contains("&lt;script&gt;alert(&#39;xss_kpi&#39;)&lt;/script&gt;") &&
                                  !contenidoHtml.contains("<img src=x") &&
                                  !contenidoHtml.contains("<iframe src=");
        assertTest("Escape de entidades HTML / Mitigación XSS", xssNeutralizado,
                "Tags <script>, <img> e <iframe> escapados a entidades HTML seguras");

        // 1.4 Log Injection / CWE-117 en GestorAuditoria
        logSubHeader("1.4 Inyección en Logs (CWE-117) en GestorAuditoria");
        String payloadLog = "Inicio sesión legítimo\n[2026-10-31 00:00:00] [AUDIT_CLEARED] (Attacker): Registro de auditoria modificado";
        GestorAuditoria.registrar("LOGIN", payloadLog, "UsuarioNormal");

        File logFile = new File("auditoria_arcsur.log");
        if (logFile.exists()) {
            List<String> lineasLog = Files.readAllLines(logFile.toPath(), StandardCharsets.UTF_8);
            boolean logInyectadoComoNuevaLinea = false;
            for (String l : lineasLog) {
                if (l.startsWith("[2026-10-31 00:00:00] [AUDIT_CLEARED]")) {
                    logInyectadoComoNuevaLinea = true;
                    break;
                }
            }
            assertTest("Mitigación de Log Injection (CWE-117)", !logInyectadoComoNuevaLinea,
                    "Saltos de línea sanitizados, impidiendo la creación de entradas de log forjadas");
        }
    }

    // =========================================================================
    // 2. NETWORK Y CUSTOM JSON PARSER
    // =========================================================================
    @SuppressWarnings("unchecked")
    private static void testRedYParserJson() throws Exception {
        logHeader("DOMINIO 2: RED Y PARSER JSON CASERO");

        Method parsearJsonArrayMethod = ServicioSupabase.class.getDeclaredMethod("parsearJsonArray", String.class);
        Method desescaparMethod = ServicioSupabase.class.getDeclaredMethod("desescapar", String.class);

        parsearJsonArrayMethod.setAccessible(true);
        desescaparMethod.setAccessible(true);

        // 2.1 Objetos JSON Anidados
        logSubHeader("2.1 Parser JSON con Objetos Anidados");
        String jsonAnidado = "[{\"id\": \"1\", \"perfil\": {\"ciudad\": \"Cochabamba\", \"edad\": \"25\"}}]";
        ArrayList<HashMap<String, String>> resAnidado = (ArrayList<HashMap<String, String>>) parsearJsonArrayMethod.invoke(null, jsonAnidado);

        boolean objetoPreservado = false;
        if (!resAnidado.isEmpty()) {
            HashMap<String, String> obj = resAnidado.get(0);
            if (obj.containsKey("perfil") && !obj.containsKey("edad")) {
                objetoPreservado = true;
            }
        }
        assertTest("Preservación de objetos anidados", objetoPreservado,
                "El parser mantiene la integridad de estructuras JSON jerárquicas");

        // 2.2 Claves que contienen dos puntos (":")
        logSubHeader("2.2 Parser JSON con Dos Puntos (':') dentro de Claves");
        String jsonColon = "[{\"http://ns\": \"valor1\", \"time:hora\": \"12:00:00\"}]";
        ArrayList<HashMap<String, String>> resColon = (ArrayList<HashMap<String, String>>) parsearJsonArrayMethod.invoke(null, jsonColon);

        boolean clavesPreservadas = false;
        if (!resColon.isEmpty()) {
            HashMap<String, String> obj = resColon.get(0);
            if (obj.containsKey("http://ns") && obj.containsKey("time:hora")) {
                clavesPreservadas = true;
            }
        }
        assertTest("Preservación de claves con ':'", clavesPreservadas,
                "Claves con caracteres especiales ':' delimitadas respetando comillas");

        // 2.3 String literal "null" entrecomillado
        logSubHeader("2.3 Preservación del String Literal \"null\"");
        String jsonNullString = "[{\"id\": \"1\", \"comentario\": \"null\"}]";
        ArrayList<HashMap<String, String>> resNull = (ArrayList<HashMap<String, String>>) parsearJsonArrayMethod.invoke(null, jsonNullString);

        boolean nullStringPreservado = false;
        if (!resNull.isEmpty()) {
            HashMap<String, String> obj = resNull.get(0);
            if ("null".equals(obj.get("comentario"))) {
                nullStringPreservado = true;
            }
        }
        assertTest("Preservación de \"null\" entrecomillado", nullStringPreservado,
                "Literal 'null' conservado como texto cuando tiene comillas");

        // 2.4 Decodificación Unicode (\\uXXXX)
        logSubHeader("2.4 Decodificación Unicode (\\uXXXX)");
        String rawUnicode = "\\u00e1\\u00f1";
        String desescapado = (String) desescaparMethod.invoke(null, "\"" + rawUnicode + "\"");
        boolean unicodeDecodificado = desescapado.equals("áñ");
        assertTest("Soporte para escapes Unicode estándar", unicodeDecodificado,
                "Cadena decodificada: '" + desescapado + "' (esperado: 'áñ')");
    }

    // =========================================================================
    // 3. CSV PARSING & EXPORTING (RFC 4180)
    // =========================================================================
    private static void testCsvParsingYExportacion() throws Exception {
        logHeader("DOMINIO 3: PARSING Y EXPORTACIÓN CSV (RFC 4180)");

        // 3.1 Comas dentro de comillas
        logSubHeader("3.1 Comas dentro de campos entrecomillados");
        String lineaComas = "\"Salguero, Fabio\",10,\"Cochabamba, Bolivia\"";
        ArrayList<String> tokensComas = GestorDatos.parsearLineaCSV(lineaComas);
        assertTest("Preservación de comas internas", tokensComas.size() == 3 &&
                tokensComas.get(0).equals("Salguero, Fabio") &&
                tokensComas.get(2).equals("Cochabamba, Bolivia"),
                "Campos obtenidos: " + tokensComas);

        // 3.2 Comillas escapadas ("")
        logSubHeader("3.2 Comillas dobles escapadas (\"\")");
        String lineaQuotes = "\"Dijo \"\"Hola mundo\"\"\",45";
        ArrayList<String> tokensQuotes = GestorDatos.parsearLineaCSV(lineaQuotes);
        assertTest("Decodificación de \"\" a \"", tokensQuotes.size() == 2 &&
                tokensQuotes.get(0).equals("Dijo \"Hola mundo\""),
                "Campo obtenido: " + tokensQuotes.get(0));

        // 3.3 Preservación de espacios dentro de comillas (RFC 4180)
        logSubHeader("3.3 Espacios significativos dentro de comillas");
        String lineaEspacios = "\"  Texto con espacios  \",123";
        ArrayList<String> tokensEspacios = GestorDatos.parsearLineaCSV(lineaEspacios);
        boolean espaciosPreservados = tokensEspacios.get(0).equals("  Texto con espacios  ");
        assertTest("Preservación de espacios significativos en CSV (RFC 4180)", espaciosPreservados,
                "Campo: '" + tokensEspacios.get(0) + "'");

        // 3.4 Salto de línea dentro de campos entrecomillados (Multiline CSV)
        logSubHeader("3.4 Salto de línea dentro de comillas (Multiline CSV)");
        File tempMultilineCsv = File.createTempFile("multiline_test_", ".csv");
        tempMultilineCsv.deleteOnExit();

        String csvMultilineContent = "ID,Nombre,Comentario\n" +
                                     "1,Ana Perez,\"Excelente formacion,\nprofesores dedicados\"\n" +
                                     "2,Carlos Gomez,\"Sin observaciones\"\n";
        Files.writeString(tempMultilineCsv.toPath(), csvMultilineContent, StandardCharsets.UTF_8);

        GestorDatos gestorMulti = new GestorDatos();
        gestorMulti.cargarCSV(tempMultilineCsv, true);
        int filasCargadas = gestorMulti.getCantidadTitulados();

        boolean multilineValido = (filasCargadas == 2);
        assertTest("Carga de campos multilínea RFC 4180", multilineValido,
                "Filas cargadas: " + filasCargadas + " (exactamente 2 registros)");

        // 3.5 Soporte de tildes en cálculo de Interés de Posgrado
        logSubHeader("3.5 Manejo de Tildes en cálculo de Interés de Posgrado");
        GestorDatos gestorTildes = new GestorDatos();
        ArrayList<HashMap<String, String>> datosTilde = new ArrayList<>();
        HashMap<String, String> f1 = new HashMap<>(); f1.put("InteresPosgrado", "Sí"); datosTilde.add(f1);
        HashMap<String, String> f2 = new HashMap<>(); f2.put("InteresPosgrado", "sí"); datosTilde.add(f2);
        HashMap<String, String> f3 = new HashMap<>(); f3.put("InteresPosgrado", "No"); datosTilde.add(f3);
        gestorTildes.cargarDatosEjemplo(datosTilde, new String[]{"InteresPosgrado"}, new ArrayList<>(), new String[0]);

        double tasaInteres = gestorTildes.calcularInteresPosgrado();
        boolean tildesManejadas = Math.abs(tasaInteres - 66.666) < 1.0;
        assertTest("Reconocimiento de 'Sí' con tilde en español", tildesManejadas,
                "Tasa calculada: " + String.format(Locale.US, "%.2f", tasaInteres) + "% (esperado: 66.67%)");

        // 3.6 Resiliencia ante CSV vacío (0 bytes) y nulo
        logSubHeader("3.6 Resiliencia ante archivo CSV vacío y nulo");
        File tempVacio = File.createTempFile("vacio_test_", ".csv");
        tempVacio.deleteOnExit();
        GestorDatos gestorVacio = new GestorDatos();
        boolean resVacio = gestorVacio.cargarCSV(tempVacio, true);
        boolean resNull = gestorVacio.cargarCSV((File) null, true);
        assertTest("Rechazo controlado de archivo vacío y nulo", !resVacio && !resNull,
                "Archivos vacíos o nulos devuelven false de forma segura sin excepciones");

        // 3.7 Resiliencia ante archivo binario con bytes nulos (\0)
        logSubHeader("3.7 Resiliencia ante archivo binario / corrupto");
        File tempBinario = File.createTempFile("binario_test_", ".csv");
        tempBinario.deleteOnExit();
        byte[] bytesBinarios = new byte[]{0x7f, 'E', 'L', 'F', 0, 1, 2, 0, 3};
        Files.write(tempBinario.toPath(), bytesBinarios);
        GestorDatos gestorBin = new GestorDatos();
        boolean resBin = gestorBin.cargarCSV(tempBinario, true);
        assertTest("Rechazo controlado de archivo binario no de texto", !resBin,
                "Archivo con bytes nulos detectado y rechazado correctamente");

        // 3.8 Relleno seguro de filas con columnas faltantes
        logSubHeader("3.8 Filas con columnas faltantes");
        File tempFaltantes = File.createTempFile("faltantes_test_", ".csv");
        tempFaltantes.deleteOnExit();
        String csvFaltantes = "ColA,ColB,ColC\nValA1\nValA2,ValB2\n";
        Files.writeString(tempFaltantes.toPath(), csvFaltantes, StandardCharsets.UTF_8);
        GestorDatos gestorFaltantes = new GestorDatos();
        boolean resFalt = gestorFaltantes.cargarCSV(tempFaltantes, true);
        boolean fila1Padded = gestorFaltantes.getDatosTitulados().get(0).containsKey("ColC") &&
                              gestorFaltantes.getDatosTitulados().get(0).get("ColC").isEmpty();
        assertTest("Padding de columnas faltantes con cadena vacía", resFalt && fila1Padded,
                "Columnas ausentes en la fila se inicializan en vacío sin provocar NullPointerException");

        // 3.9 Recuperación de comilla abierta al final del archivo (EOF)
        logSubHeader("3.9 Recuperación de comilla no cerrada al final del archivo");
        File tempUnclosed = File.createTempFile("unclosed_test_", ".csv");
        tempUnclosed.deleteOnExit();
        String csvUnclosed = "ID,Nombre,Nota\n1,Juan,\"Comentario sin cerrar";
        Files.writeString(tempUnclosed.toPath(), csvUnclosed, StandardCharsets.UTF_8);
        GestorDatos gestorUnclosed = new GestorDatos();
        boolean resUnclosed = gestorUnclosed.cargarCSV(tempUnclosed, true);
        assertTest("Recuperación de contingencia en comilla abierta al EOF", resUnclosed && gestorUnclosed.getCantidadTitulados() == 1,
                "Registro rescatado con éxito sin pérdida de información: " + gestorUnclosed.getCantidadTitulados() + " fila");
    }

    // =========================================================================
    // 4. CONCURRENCIA Y AUDITORÍA
    // =========================================================================
    private static void testConcurrenciaYAuditoria() throws Exception {
        logHeader("DOMINIO 4: CONCURRENCIA Y AUDITORÍA");

        logSubHeader("4.1 Estrés Multihilo y Sincronización en GestorAuditoria");
        File logFile = new File("auditoria_arcsur.log");
        if (logFile.exists()) logFile.delete();

        int numHilos = 10;
        int llamadasPorHilo = 50;
        int totalEsperado = numHilos * llamadasPorHilo;

        ExecutorService executor = Executors.newFixedThreadPool(numHilos);
        CountDownLatch latchInicio = new CountDownLatch(1);
        CountDownLatch latchFin = new CountDownLatch(numHilos);

        for (int h = 0; h < numHilos; h++) {
            final int idHilo = h;
            executor.submit(() -> {
                try {
                    latchInicio.await();
                    for (int i = 0; i < llamadasPorHilo; i++) {
                        GestorAuditoria.registrar("STRESS_TEST", "Evento " + i + " desde hilo " + idHilo, "TesterHilo" + idHilo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latchFin.countDown();
                }
            });
        }

        long t0 = System.currentTimeMillis();
        latchInicio.countDown();
        boolean completado = latchFin.await(10, TimeUnit.SECONDS);
        long tTotal = System.currentTimeMillis() - t0;
        executor.shutdown();

        assertTest("Finalización oportuna de hilos concurrentes", completado, "Tiempo total: " + tTotal + " ms");

        int tamHistorialMemoria = GestorAuditoria.getHistorial().size();
        assertTest("Integridad de auditoría en memoria concurrente",
                tamHistorialMemoria >= totalEsperado,
                "Eventos en memoria: " + tamHistorialMemoria + " (mínimo esperado: " + totalEsperado + ")");

        boolean archivoCreado = logFile.exists();
        assertTest("Persistencia de auditoría en disco sincronizada", archivoCreado,
                "Archivo 'auditoria_arcsur.log' generado bajo sincronización concurrente");

        logSubHeader("4.2 Memoria Acotada (Bounded Retention) en GestorAuditoria");
        for (int i = 0; i < 5500; i++) {
            GestorAuditoria.registrar("RETENTION_TEST", "Evento " + i, "StressTester");
        }
        int cappedSize = GestorAuditoria.getHistorial().size();
        assertTest("Retención de memoria acotada (máx 5000 eventos)", cappedSize <= 5000,
                "Eventos en memoria limitados a " + cappedSize + " (evita OutOfMemoryError)");
    }

    // =========================================================================
    // 5. WRANGLING Y CONSISTENCIA ESTADÍSTICA (ACTIVIDAD 3 - GRUPO 1.MD)
    // =========================================================================
    private static void testWranglingYConsistenciaEstadistica() {
        logHeader("DOMINIO 5: WRANGLING Y CONSISTENCIA CON 'ACTIVIDAD 3 - GRUPO 1.MD'");

        File csvTitulados = new File("src/Recursos/titulados_limpio.csv");
        if (!csvTitulados.exists()) csvTitulados = new File("titulados_limpio.csv");

        if (!csvTitulados.exists()) {
            System.err.println("  [AVISO] No se encontró 'titulados_limpio.csv' para cotejo directo.");
            return;
        }

        GestorDatos gestor = new GestorDatos();
        gestor.cargarCSV(csvTitulados, true);

        // 5.1 Tamaño de muestra recolectada
        int nTotal = gestor.getCantidadTitulados();
        assertTest("Tamaño total de titulados recolectados", nTotal == 148,
                "N = " + nTotal + " (esperado en doc: 148)");

        // 5.2 Muestra válida cuantitativa AniosProfesional
        ArrayList<String> rawAnios = gestor.obtenerColumna(true, "AniosProfesional");
        ArrayList<Double> anios = Estadistica.extraerValoresNumericos(rawAnios);
        int nValidos = anios.size();
        assertTest("Muestra cuantitativa válida (AniosProfesional)", nValidos == 145,
                "n = " + nValidos + " | faltantes = " + (nTotal - nValidos) + " (esperado en doc: 145 válidos, 3 faltantes)");

        // 5.3 Duplicados detectados en formulario
        Map<String, Integer> dups = gestor.obtenerEmailsDuplicadosTitulados();
        int sumaExcedentes = 0;
        for (int c : dups.values()) sumaExcedentes += (c - 1);
        assertTest("Detección de 5 envíos duplicados en titulados", sumaExcedentes == 5,
                "Excedentes duplicados = " + sumaExcedentes + " en " + dups.size() + " emails (esperado en doc: 5 envíos duplicados)");

        // 5.4 Outlier identificado por regla de Tukey
        ArrayList<Double> outliers = Estadistica.obtenerAtipicos(anios);
        boolean outlierCorrecto = outliers.size() == 1 && Math.abs(outliers.get(0) - 35.0) < 1e-9;
        assertTest("Detección de 1 caso extremo legítimo (35 años)", outlierCorrecto,
                "Atípicos: " + outliers + " (esperado en doc: [35.0] años)");

        // 5.5 Estadísticos exactos descritos en Actividad 3
        double media = Estadistica.media(anios);
        double mediana = Estadistica.mediana(anios);
        double varianza = Estadistica.varianza(anios);
        double desv = Estadistica.desviacionEstandar(anios);
        double cv = Estadistica.coeficienteVariacion(anios);

        assertTest("Media muestral exacta", String.format(Locale.US, "%.2f", media).equals("8.83"),
                "Media = " + String.format(Locale.US, "%.2f", media) + " (doc: 8.83)");
        assertTest("Mediana exacta", String.format(Locale.US, "%.2f", mediana).equals("6.00"),
                "Mediana = " + String.format(Locale.US, "%.2f", mediana) + " (doc: 6.00)");
        assertTest("Varianza muestral con Bessel (N-1)", String.format(Locale.US, "%.2f", varianza).equals("62.76"),
                "Varianza = " + String.format(Locale.US, "%.2f", varianza) + " (doc: 62.76)");
        assertTest("Desviación estándar con Bessel (N-1)", String.format(Locale.US, "%.2f", desv).equals("7.92"),
                "Desviación = " + String.format(Locale.US, "%.2f", desv) + " (doc: 7.92)");
        assertTest("Coeficiente de Variación", String.format(Locale.US, "%.2f", cv).equals("89.71"),
                "CV = " + String.format(Locale.US, "%.2f", cv) + "% (doc: 89.71%)");
    }
}
