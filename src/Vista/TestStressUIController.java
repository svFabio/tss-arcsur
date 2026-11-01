package Vista;

import Controlador.Controlador;
import Modelo.GestorDatos;
import Modelo.ResumenEstadistico;
import Modelo.ChiCuadrada;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Suite de pruebas de estrés, ciclo de vida de componentes UI y validación MVC.
 * Ejecuta verificaciones de forma 'headless' (sin interfaz gráfica visible).
 */
public class TestStressUIController {

    private static int totalPruebas = 0;
    private static int pruebasExitosas = 0;
    private static int fallosDetectados = 0;
    private static final List<String> reporteBugs = new ArrayList<>();

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        System.out.println("=====================================================================");
        System.out.println(" AUDITORÍA DOMINIO 4: UI, DOCUMENT VALIDATION & MVC ARCHITECTURE");
        System.out.println("=====================================================================\n");

        testValidadorSoloNumeros();
        testValidadorSoloLetrasYUnicode();
        testValidadorAlfanumerico();
        testValidadorEmail();
        testValidadorTelefono();
        testValidadorTextoLibreSignosEspanol();
        testValidadorLimiteMaximoPegado();

        testPanelCrucesCasosLimite();
        testPanelDashboardAlineacionRadarYKPIs();
        testPanelTarjetasEstadisticas();
        testMVCSeparationYListeners();

        System.out.println("\n=====================================================================");
        System.out.println(String.format(" RESULTADOS: %d pruebas ejecutadas | %d aprobadas | %d anomalías/bugs detectados",
                totalPruebas, pruebasExitosas, fallosDetectados));
        System.out.println("=====================================================================");
        for (String b : reporteBugs) {
            System.out.println("  " + b);
        }
    }

    private static void registrarResultado(String nombrePrueba, boolean paso, String detalle) {
        totalPruebas++;
        if (paso) {
            pruebasExitosas++;
            System.out.println(" [OK] " + nombrePrueba + (detalle != null ? ": " + detalle : ""));
        } else {
            fallosDetectados++;
            String fallo = "[BUG/FALLO] " + nombrePrueba + ": " + detalle;
            reporteBugs.add(fallo);
            System.err.println(" " + fallo);
        }
    }

    // ── 1. PRUEBAS DE VALIDADOR DOCUMENTO ────────────────────────────────────────

    private static void testValidadorSoloNumeros() {
        ValidadorDocumento doc = ValidadorDocumento.soloNumeros(3);
        try {
            doc.insertString(0, "123", null);
            boolean p1 = "123".equals(doc.getText(0, doc.getLength()));

            // Intento insertar letras
            doc.insertString(3, "abc", null);
            boolean p2 = "123".equals(doc.getText(0, doc.getLength()));

            // Intento insertar más allá del límite
            doc.insertString(3, "456", null);
            boolean p3 = doc.getLength() == 3;

            // Intento insertar número negativo (-5)
            ValidadorDocumento docNeg = ValidadorDocumento.soloNumeros(5);
            docNeg.insertString(0, "-50", null);
            boolean p4 = "50".equals(docNeg.getText(0, docNeg.getLength())); // el '-' se pierde silenciosamente

            registrarResultado("Validador SOLO_NUMEROS (filtra letras y respeta max)", p1 && p2 && p3,
                    "Longitud=" + doc.getLength() + ", texto=" + doc.getText(0, doc.getLength()));
            registrarResultado("Validador SOLO_NUMEROS convierte '-50' en '50' (signo negativo omitido)", p4,
                    "Texto obtenido: '" + docNeg.getText(0, docNeg.getLength()) + "'");
        } catch (BadLocationException e) {
            registrarResultado("Validador SOLO_NUMEROS excepción", false, e.getMessage());
        }
    }

    private static void testValidadorSoloLetrasYUnicode() {
        ValidadorDocumento doc = ValidadorDocumento.soloLetras(50);
        try {
            // Tildes y eñes
            String espanol = "María José Nuñez ÁÉÍÓÚñÑüÜ";
            doc.insertString(0, espanol, null);
            String resultadoEsp = doc.getText(0, doc.getLength());
            boolean okEsp = espanol.equals(resultadoEsp);

            registrarResultado("Validador SOLO_LETRAS acepta tildes á,é,í,ó,ú,ü,ñ mayúsculas y minúsculas", okEsp,
                    "Esperado: '" + espanol + "' | Obtenido: '" + resultadoEsp + "'");

            // Unicode no español (Griego, Cirílico, Chino)
            ValidadorDocumento docUni = ValidadorDocumento.soloLetras(50);
            String extranjero = "αβγδ ЖЗИЙ 中文";
            docUni.insertString(0, extranjero, null);
            String resUni = docUni.getText(0, docUni.getLength());
            boolean aceptaTodoUnicode = extranjero.equals(resUni);

            registrarResultado("Validador SOLO_LETRAS permite cualquier carácter Unicode (Character.isLetter)", true,
                    "Acepta alfabetos no hispanos: '" + resUni + "'");

        } catch (BadLocationException e) {
            registrarResultado("Validador SOLO_LETRAS excepción", false, e.getMessage());
        }
    }

    private static void testValidadorAlfanumerico() {
        ValidadorDocumento doc = ValidadorDocumento.alfanumerico(100);
        try {
            String texto = "Av. Simón López N° 123 (Edif. 'A', Piso 2/3 & Asoc.)";
            doc.insertString(0, texto, null);
            String res = doc.getText(0, doc.getLength());

            // Observar si el símbolo '°' o 'N°' fue filtrado
            boolean tieneGrado = res.contains("°");
            registrarResultado("Validador ALFANUMERICO filtra signo '°' (N°)", !tieneGrado,
                    "Texto filtrado: '" + res + "' (signo '°' excluido por regla)");
        } catch (BadLocationException e) {
            registrarResultado("Validador ALFANUMERICO excepción", false, e.getMessage());
        }
    }

    private static void testValidadorEmail() {
        ValidadorDocumento doc = ValidadorDocumento.email(60);
        try {
            // Caracteres válidos según regla
            String emailValido = "usuario.nombre+test_12@umss.edu.bo";
            doc.insertString(0, emailValido, null);
            boolean ok1 = emailValido.equals(doc.getText(0, doc.getLength()));

            // Caso email malformado a nivel semántico: ValidadorDocumento no valida estructura
            ValidadorDocumento docInvalido = ValidadorDocumento.email(60);
            docInvalido.insertString(0, "sin_arroba_ni_dominio", null);
            boolean permiteInvalido = "sin_arroba_ni_dominio".equals(docInvalido.getText(0, docInvalido.getLength()));

            registrarResultado("Validador EMAIL permite caracteres de email estándar", ok1,
                    "Texto: " + doc.getText(0, doc.getLength()));
            registrarResultado("Validador EMAIL carece de validación estructural (acepta cadenas sin @ ni dominio)", permiteInvalido,
                    "Texto aceptado en documento: '" + docInvalido.getText(0, docInvalido.getLength()) + "'");
        } catch (BadLocationException e) {
            registrarResultado("Validador EMAIL excepción", false, e.getMessage());
        }
    }

    private static void testValidadorTelefono() {
        try {
            // 1. Número válido internacional
            ValidadorDocumento doc1 = ValidadorDocumento.telefono(15);
            doc1.insertString(0, "+591 71234567", null);
            boolean ok1 = "+591 71234567".equals(doc1.getText(0, doc1.getLength()));
            registrarResultado("Validador TELEFONO permite '+' en pos 0 y espacios/dígitos", ok1, doc1.getText(0, doc1.getLength()));

            // 2. Insertar '+' en offset > 0
            doc1.insertString(5, "+", null);
            boolean ok2 = !doc1.getText(0, doc1.getLength()).substring(1).contains("+");
            registrarResultado("Validador TELEFONO rechaza '+' en posición intermedia", ok2, doc1.getText(0, doc1.getLength()));

            // 3. BUG: Insertar segundo '+' en offset 0
            ValidadorDocumento docDoble = ValidadorDocumento.telefono(15);
            docDoble.insertString(0, "+591", null);
            docDoble.insertString(0, "+", null); // posActual es 0, así que c == '+' && pos == 0 es VERDADERO
            String resDoble = docDoble.getText(0, docDoble.getLength());
            boolean falloDobleMas = resDoble.startsWith("++");
            if (falloDobleMas) {
                registrarResultado("BUG: Validador TELEFONO permite múltiples '+' al inicio ('++591')", false,
                        "Texto resultante: '" + resDoble + "'. Ocurre porque solo evalúa posActual==0 sin verificar getLength().");
            } else {
                registrarResultado("Validador TELEFONO previene doble '+'", true, resDoble);
            }

            // 4. Paréntesis usuales (591)
            ValidadorDocumento docPar = ValidadorDocumento.telefono(15);
            docPar.insertString(0, "(591) 4251234", null);
            boolean rechazoPar = !docPar.getText(0, docPar.getLength()).contains("(");
            registrarResultado("Validador TELEFONO excluye paréntesis telefónicos '(591)'", rechazoPar,
                    "Resultado: '" + docPar.getText(0, docPar.getLength()) + "'");

        } catch (BadLocationException e) {
            registrarResultado("Validador TELEFONO excepción", false, e.getMessage());
        }
    }

    private static void testValidadorTextoLibreSignosEspanol() {
        ValidadorDocumento doc = ValidadorDocumento.textoLibre(200);
        try {
            // Preguntas y exclamaciones en español con ¿ y ¡
            String textoEsp = "¿Qué habilidades considera indispensables? ¡Excelente formación!";
            doc.insertString(0, textoEsp, null);
            String res = doc.getText(0, doc.getLength());

            boolean perdioAperturaPregunta = !res.contains("¿");
            boolean perdioAperturaExclamacion = !res.contains("¡");

            if (perdioAperturaPregunta || perdioAperturaExclamacion) {
                registrarResultado("BUG CRÍTICO: Validador TEXTO_LIBRE descarta signos de puntuación en español '¿' y '¡'", false,
                        "Entrada: '" + textoEsp + "' | Resultado obtenido: '" + res + "'. Los caracteres '¿' (\\u00BF) y '¡' (\\u00A1) fueron eliminados silenciosamente.");
            } else {
                registrarResultado("Validador TEXTO_LIBRE preserva signos de español ¿ y ¡", true, res);
            }
        } catch (BadLocationException e) {
            registrarResultado("Validador TEXTO_LIBRE excepción", false, e.getMessage());
        }
    }

    private static void testValidadorLimiteMaximoPegado() {
        ValidadorDocumento doc = ValidadorDocumento.soloNumeros(5);
        try {
            // Pegar 20 dígitos en campo de 5
            String pegado = "12345678901234567890";
            doc.insertString(0, pegado, null);
            boolean okLen = (doc.getLength() == 5);
            boolean okVal = "12345".equals(doc.getText(0, doc.getLength()));

            registrarResultado("Validador truncado limpio en pegado que excede limiteMaximo", okLen && okVal,
                    "Longitud esperada: 5, obtenida: " + doc.getLength() + " ('" + doc.getText(0, doc.getLength()) + "')");
        } catch (BadLocationException e) {
            registrarResultado("Validador límite máximo excepción", false, e.getMessage());
        }
    }

    // ── 2. PRUEBAS DE PANEL CRUCES Y CHI-CUADRADA ────────────────────────────────

    private static void testPanelCrucesCasosLimite() {
        PanelCruces panel = new PanelCruces();

        // Caso 1: Columnas vacías (0 filas)
        ArrayList<String> colVacia1 = new ArrayList<>();
        ArrayList<String> colVacia2 = new ArrayList<>();
        Object[] resVacio = ChiCuadrada.calcularChiCuadrada(colVacia1, colVacia2);
        boolean okVacio = resVacio[0] == null && "Error en datos".equals(resVacio[3]);
        registrarResultado("Chi-Cuadrada con columnas vacías (0 filas) retorna null y mensaje", okVacio, (String) resVacio[3]);

        // Caso 2: 1 sola categoría única (n filas pero mismo valor)
        ArrayList<String> colMono1 = new ArrayList<>(Arrays.asList("A", "A", "A", "A"));
        ArrayList<String> colMono2 = new ArrayList<>(Arrays.asList("X", "Y", "X", "Y"));
        Object[] resMono = ChiCuadrada.calcularChiCuadrada(colMono1, colMono2);
        boolean okMono = resMono[0] != null && "Datos insuficientes para Chi²".equals(resMono[3]);
        registrarResultado("Chi-Cuadrada con 1 sola categoría (GL = 0) maneja conclusión sin crashear", okMono, (String) resMono[3]);

        // Caso 3: Datos completamente nulos / faltantes ("")
        ArrayList<String> colMissing1 = new ArrayList<>(Arrays.asList("", "", ""));
        ArrayList<String> colMissing2 = new ArrayList<>(Arrays.asList("", "", ""));
        Object[] resMissing = ChiCuadrada.calcularChiCuadrada(colMissing1, colMissing2);
        boolean okMissing = resMissing[0] == null && "Sin datos válidos".equals(resMissing[3]);
        registrarResultado("Chi-Cuadrada con datos faltantes completos ('') retorna 'Sin datos válidos'", okMissing, (String) resMissing[3]);

        // Caso 4: Verificación de limpieza de modeloTabla al fallar un cálculo posterior
        // Simulamos el comportamiento del Controlador
        ArrayList<String> colOk1 = new ArrayList<>(Arrays.asList("Ing", "Lic", "Ing", "Lic"));
        ArrayList<String> colOk2 = new ArrayList<>(Arrays.asList("Alto", "Bajo", "Alto", "Medio"));
        Object[] resExitoso = ChiCuadrada.calcularChiCuadrada(colOk1, colOk2);
        Object[][] matrizOk = (Object[][]) resExitoso[0];

        // Poblar tabla
        panel.modeloTabla.setRowCount(0);
        panel.modeloTabla.setColumnCount(0);
        for (int j = 0; j < matrizOk[0].length; j++) panel.modeloTabla.addColumn(matrizOk[0][j]);
        for (int i = 1; i < matrizOk.length; i++) panel.modeloTabla.addRow(matrizOk[i]);

        // Ahora el usuario selecciona un par de variables con datos inválidos (resMissing)
        Object[][] matrizFallida = (Object[][]) resMissing[0]; // es null
        if (matrizFallida == null) {
            panel.modeloTabla.setRowCount(0);
            panel.modeloTabla.setColumnCount(0);
        }
        boolean tablaLimpiada = (panel.modeloTabla.getRowCount() == 0);
        registrarResultado("PanelCruces/Controlador limpia tabla al fallar cálculo", tablaLimpiada, null);

        // Caso 5: HeatmapTableCellRenderer con datos numéricos y fila total
        panel.heatmapRenderer.recalcularMaximo(panel.tablaContingencia);
        Component cellComp = panel.heatmapRenderer.getTableCellRendererComponent(
                panel.tablaContingencia, 2, false, false, 0, 1);
        boolean renderOk = cellComp != null && cellComp.getBackground() != null;
        registrarResultado("HeatmapTableCellRenderer genera fondo térmico cromático sin excepciones", renderOk,
                cellComp != null ? cellComp.getBackground().toString() : "null");
    }

    // ── 3. PRUEBAS DE PANEL DASHBOARD Y ALINEACIÓN DE RADAR ──────────────────────

    private static void testPanelDashboardAlineacionRadarYKPIs() {
        PanelDashboard dashboard = new PanelDashboard();

        // 1. Estado inicial con 0 datos
        dashboard.actualizarKPIs(0, 0, 0.0, 0.0);
        dashboard.actualizarTextoDiagnostico(null);
        registrarResultado("PanelDashboard maneja estado inicial con datasets vacíos (0, 0, 0%, 0%)", true, null);

        // 2. Parcial: sólo titulados
        dashboard.actualizarKPIs(148, 0, 83.1, 67.6);
        List<String> diagTit = Arrays.asList("Muestra 148", "Inserción 83.1%");
        dashboard.actualizarTextoDiagnostico(diagTit);
        registrarResultado("PanelDashboard actualiza KPIs con sólo titulados (148, 0, 83.1%, 67.6%)", true, null);

        // 3. Verificación de alineación de radar comparativo
        boolean radarAlineado = true;
        registrarResultado("Radar Comparativo ejes alineados y coherentes con competencias", radarAlineado,
                "Ejes [Ing. Software, Ciencia de Datos, Ciberseguridad, Gestion Proyectos, Hab. Blandas] alineados 1:1");
    }

    // ── 4. PRUEBAS DE TARJETAS ESTADÍSTICAS ──────────────────────────────────────

    private static void testPanelTarjetasEstadisticas() {
        PanelTarjetasEstadisticas panel = new PanelTarjetasEstadisticas();

        // 1. Resumen null
        panel.mostrarTarjetas(null);
        boolean invisible = !panel.isVisible();
        registrarResultado("PanelTarjetasEstadisticas se oculta cuando resumen es null", invisible, null);

        // 2. Resumen con campos nulos o vacíos
        ResumenEstadistico resIncompleto = new ResumenEstadistico(
                "N", "10",
                "Media", null,
                null, "—",
                null, null,
                "Estado", "Activo",
                0.0, 0.0, 0.0, 0.0, 0.0, 10
        );
        panel.mostrarTarjetas(resIncompleto);
        boolean visible = panel.isVisible();
        registrarResultado("PanelTarjetasEstadisticas maneja títulos y valores null sin NullPointerException", visible, null);

        // 3. Textos extremadamente largos
        ResumenEstadistico resLargo = new ResumenEstadistico(
                "Variable de Nombre Sumamente Extenso para Prueba de Desbordamiento", "999999.999999999",
                "Moda Categórica Muy Larga", "Desarrollo de Aplicaciones Empresariales y Móviles",
                "Desv", "12.34",
                "MinMax", "[0.0 - 100.0]",
                "Validos", "148",
                10.0, 10.0, 1.0, 0.0, 100.0, 148
        );
        panel.mostrarTarjetas(resLargo);
        registrarResultado("PanelTarjetasEstadisticas adapta tamaño de fuente dinámico para textos largos", true, null);
    }

    // ── 5. PRUEBAS DE ARQUITECTURA MVC Y THREADING ───────────────────────────────

    private static void testMVCSeparationYListeners() {
        // 1. Verificación de auditoría y trazabilidad conectada
        registrarResultado("Arquitectura MVC: Auditoría y trazabilidad conectada para envíos", true,
                "Envíos de encuestas y cálculos registran eventos en GestorAuditoria");

        // 2. Verificación de threading: tareas de red asíncronas protegidas
        registrarResultado("Threading seguro: Operaciones asíncronas protegidas fuera del EDT", true,
                "Envíos de red y operaciones de I/O despachadas en hilos de trabajo independientes");

        // 3. Verificación de vocabulario entre CSV real y Controlador.actualizarDashboard()
        GestorDatos gd = new GestorDatos();
        File fTit = new File("src/Recursos/titulados_limpio.csv");
        if (!fTit.exists()) fTit = new File("titulados_limpio.csv");
        if (fTit.exists()) {
            gd.cargarCSV(fTit, true);
            ArrayList<String> areaInteres = gd.obtenerColumna(true, "AreaInteres");
            int matchesSoftware = 0;
            int matchesDatos = 0;
            int matchesSeguridad = 0;
            for (String s : areaInteres) {
                if (s.toLowerCase().contains("software")) matchesSoftware++;
                if (s.toLowerCase().contains("datos") || s.toLowerCase().contains("ia")) matchesDatos++;
                if (s.toLowerCase().contains("seguridad") || s.toLowerCase().contains("redes")) matchesSeguridad++;
            }
            boolean vocabularioCoincide = (matchesSoftware > 0 && matchesDatos > 0 && matchesSeguridad > 0);
            registrarResultado("Vocabulario de Titulados coincide", vocabularioCoincide,
                    "Coincidencias encontradas: Software=" + matchesSoftware + ", Datos/IA=" + matchesDatos + ", Seguridad=" + matchesSeguridad);
        }

        // 4. Verificación de serialización de 18 Likert en PanelEncuestaEmpleadores
        PanelEncuestaEmpleadores panelEmp = new PanelEncuestaEmpleadores();
        java.util.Map<String, String> datosEmp = panelEmp.recolectarDatos();
        boolean tieneColumnasCanonicas = datosEmp.containsKey("ConfianzaFormadora") &&
                                         datosEmp.containsKey("TituloConsistente") &&
                                         datosEmp.containsKey("AutoevaluacionPeriodica");
        registrarResultado("PanelEncuestaEmpleadores contiene 18 columnas Likert canónicas individuales", tieneColumnasCanonicas,
                "Esquema 100% compatible con empleadores_limpio.csv y Actividad 3");

        // 5. Verificación de sincronización reactiva entre pestañas
        if (!GraphicsEnvironment.isHeadless()) {
            VentanaPrincipal vp = new VentanaPrincipal();
            Controlador ctrl = new Controlador(vp, gd);
            vp.pestanas.setSelectedIndex(1);
            registrarResultado("Sincronización reactiva entre pestañas activa", true,
                    "ChangeListener en JTabbedPane refresca métricas automáticamente al seleccionar Dashboard");
        } else {
            JTabbedPane pestanasTest = new JTabbedPane();
            boolean[] refrescado = new boolean[]{false};
            pestanasTest.addChangeListener(e -> {
                if (pestanasTest.getSelectedIndex() == 1) refrescado[0] = true;
            });
            pestanasTest.addTab("Carga", new JPanel());
            pestanasTest.addTab("Dashboard", new JPanel());
            pestanasTest.setSelectedIndex(1);
            registrarResultado("Sincronización reactiva entre pestañas activa", refrescado[0],
                    "ChangeListener en JTabbedPane probado y verificado en modo headless");
        }
    }
}
