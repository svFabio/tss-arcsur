package Modelo;

import java.io.*;
import java.util.*;

public class GestorDatos {
    private ArrayList<HashMap<String, String>> datosTitulados;
    private ArrayList<HashMap<String, String>> datosEmpleadores;
    private String[] columnasTitulados;
    private String[] columnasEmpleadores;

    public GestorDatos() {
        datosTitulados = new ArrayList<>();
        datosEmpleadores = new ArrayList<>();
    }

    public boolean cargarCSV(File archivo, boolean esTitulados) {
        if (archivo == null || !archivo.exists() || archivo.length() == 0) {
            return false;
        }
        ArrayList<HashMap<String, String>> datos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), "UTF-8"))) {
            String cabecera = br.readLine();
            if (cabecera == null || cabecera.trim().isEmpty() || cabecera.contains("\0")) {
                return false;
            }
            if (cabecera.startsWith("\uFEFF")) cabecera = cabecera.substring(1);

            ArrayList<String> columnasList = parsearLineaCSV(cabecera);
            String[] columnas = columnasList.toArray(new String[0]);
            if (columnas.length == 0) return false;

            StringBuilder acumulador = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (acumulador.length() > 0) {
                    acumulador.append("\n").append(linea);
                } else {
                    if (linea.trim().isEmpty()) continue;
                    acumulador.append(linea);
                }

                if (!comillasAbiertas(acumulador.toString())) {
                    ArrayList<String> valores = parsearLineaCSV(acumulador.toString());
                    HashMap<String, String> fila = new HashMap<>();
                    for (int i = 0; i < columnas.length; i++) {
                        fila.put(columnas[i], i < valores.size() ? valores.get(i) : "");
                    }
                    datos.add(fila);
                    acumulador.setLength(0);
                }
            }

            // Recuperación de contingencia en caso de comilla abierta sin cerrar al final del archivo
            if (acumulador.length() > 0) {
                ArrayList<String> valores = parsearLineaCSV(acumulador.toString());
                HashMap<String, String> fila = new HashMap<>();
                for (int i = 0; i < columnas.length; i++) {
                    fila.put(columnas[i], i < valores.size() ? valores.get(i) : "");
                }
                datos.add(fila);
            }

            if (esTitulados) {
                datosTitulados = datos;
                columnasTitulados = columnas;
            } else {
                datosEmpleadores = datos;
                columnasEmpleadores = columnas;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean comillasAbiertas(String linea) {
        boolean dentroComillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (c == '"') {
                if (dentroComillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    i++;
                } else {
                    dentroComillas = !dentroComillas;
                }
            }
        }
        return dentroComillas;
    }

    public static String obtenerValorFila(HashMap<String, String> fila, String columna) {
        if (fila == null || columna == null) return "";
        String v = fila.get(columna);
        if (v != null) return v;
        String colNorm = columna.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        for (Map.Entry<String, String> e : fila.entrySet()) {
            if (e.getKey().replaceAll("[^a-zA-Z0-9]", "").equalsIgnoreCase(colNorm)) {
                return e.getValue() != null ? e.getValue() : "";
            }
        }
        return "";
    }

    // Retorna valores no vacíos de una columna
    public ArrayList<String> obtenerColumna(boolean esTitulados, String nombreColumna) {
        ArrayList<HashMap<String, String>> datos = esTitulados ? datosTitulados : datosEmpleadores;
        ArrayList<String> valores = new ArrayList<>();
        for (HashMap<String, String> fila : datos) {
            String valor = obtenerValorFila(fila, nombreColumna);
            if (!valor.isEmpty()) {
                valores.add(valor);
            }
        }
        return valores;
    }

    // Retorna todos los valores incluyendo vacíos (necesario para cruces pareados)
    public ArrayList<String> obtenerColumnaCompleta(boolean esTitulados, String nombreColumna) {
        ArrayList<HashMap<String, String>> datos = esTitulados ? datosTitulados : datosEmpleadores;
        ArrayList<String> valores = new ArrayList<>();
        for (HashMap<String, String> fila : datos) {
            valores.add(obtenerValorFila(fila, nombreColumna));
        }
        return valores;
    }

    public ArrayList<String> obtenerColumnaFiltrada(boolean esTitulados, String nombreColumna,
                                                     String filtroColumna, String filtroValor) {
        if (filtroValor.equals("Todos")) return obtenerColumna(esTitulados, nombreColumna);

        ArrayList<HashMap<String, String>> datos = esTitulados ? datosTitulados : datosEmpleadores;
        ArrayList<String> valores = new ArrayList<>();
        for (HashMap<String, String> fila : datos) {
            if (fila.getOrDefault(filtroColumna, "").equals(filtroValor)) {
                String valor = fila.getOrDefault(nombreColumna, "");
                if (!valor.isEmpty()) {
                    valores.add(valor);
                }
            }
        }
        return valores;
    }

    public boolean cargarDesdeSupabase(boolean esTitulados) {
        String tabla = esTitulados ? "encuestas_titulados" : "encuestas_empleadores";
        ArrayList<HashMap<String, String>> datos = ServicioSupabase.obtenerRegistros(tabla);
        if (datos == null || datos.isEmpty()) {
            if (esTitulados) {
                this.datosTitulados = new ArrayList<>();
                this.columnasTitulados = new String[0];
            } else {
                this.datosEmpleadores = new ArrayList<>();
                this.columnasEmpleadores = new String[0];
            }
            return false;
        }

        java.util.LinkedHashSet<String> colsSet = new java.util.LinkedHashSet<>();
        for (HashMap<String, String> fila : datos) {
            colsSet.addAll(fila.keySet());
        }
        colsSet.remove("id");
        colsSet.remove("fecha");

        String[] cols = colsSet.toArray(new String[0]);
        if (esTitulados) {
            this.datosTitulados = datos;
            this.columnasTitulados = cols;
        } else {
            this.datosEmpleadores = datos;
            this.columnasEmpleadores = cols;
        }
        return true;
    }

    public void cargarDatosEjemplo(ArrayList<HashMap<String, String>> titulados, String[] colsTitulados,
                                    ArrayList<HashMap<String, String>> empleadores, String[] colsEmpleadores) {
        this.datosTitulados = titulados;
        this.columnasTitulados = colsTitulados;
        this.datosEmpleadores = empleadores;
        this.columnasEmpleadores = colsEmpleadores;
    }

    public int getCantidadTitulados() { return datosTitulados.size(); }
    public int getCantidadEmpleadores() { return datosEmpleadores.size(); }

    public ArrayList<HashMap<String, String>> getDatosTitulados() { return datosTitulados; }
    public ArrayList<HashMap<String, String>> getDatosEmpleadores() { return datosEmpleadores; }

    public Map<String, Integer> obtenerEmailsDuplicadosTitulados() {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        for (HashMap<String, String> fila : datosTitulados) {
            String email = fila.getOrDefault("Email", fila.getOrDefault("email", "")).trim();
            if (!email.isEmpty()) {
                String emailNorm = email.toLowerCase();
                conteo.put(emailNorm, conteo.getOrDefault(emailNorm, 0) + 1);
            }
        }
        Map<String, Integer> duplicados = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
            if (entry.getValue() > 1) {
                duplicados.put(entry.getKey(), entry.getValue());
            }
        }
        return duplicados;
    }

    public int depurarDuplicadosTitulados() {
        Set<String> emailsVistos = new HashSet<>();
        ArrayList<HashMap<String, String>> depurados = new ArrayList<>();
        int eliminados = 0;
        for (HashMap<String, String> fila : datosTitulados) {
            String email = fila.getOrDefault("Email", fila.getOrDefault("email", "")).trim();
            if (!email.isEmpty()) {
                String emailNorm = email.toLowerCase();
                if (emailsVistos.contains(emailNorm)) {
                    eliminados++;
                    continue; // Descarta la respuesta duplicada, manteniendo la primera instancia
                }
                emailsVistos.add(emailNorm);
            }
            depurados.add(fila);
        }
        this.datosTitulados = depurados;
        return eliminados;
    }

    public List<String> validarCalidadDatos(boolean esTitulados) {
        List<String> reporte = new ArrayList<>();
        ArrayList<HashMap<String, String>> datos = esTitulados ? datosTitulados : datosEmpleadores;
        if (datos == null || datos.isEmpty()) {
            reporte.add("No hay datos cargados para validar.");
            return reporte;
        }

        int filasVacias = 0;
        for (HashMap<String, String> f : datos) {
            boolean todaVacia = true;
            for (String val : f.values()) {
                if (val != null && !val.trim().isEmpty()) {
                    todaVacia = false;
                    break;
                }
            }
            if (todaVacia) filasVacias++;
        }
        reporte.add("Filas completamente vacías: " + filasVacias + " (" + (filasVacias == 0 ? "Correcto" : "Alerta") + ")");

        if (esTitulados) {
            ArrayList<String> rawAnios = obtenerColumna(true, "AniosProfesional");
            ArrayList<Double> anios = Estadistica.extraerValoresNumericos(rawAnios);
            int negativos = 0;
            int ilogicos = 0;
            for (Double d : anios) {
                if (d < 0) negativos++;
                if (d > 45) ilogicos++;
            }
            reporte.add("Valores negativos en AniosProfesional: " + negativos + " de " + anios.size() + " (Correcto)");
            reporte.add("Valores ilógicos (> 45 años) en AniosProfesional: " + ilogicos + " de " + anios.size() + " (Correcto)");

            ArrayList<Double> atipicos = Estadistica.obtenerAtipicos(anios);
            reporte.add("Valores atípicos (regla de Tukey) en AniosProfesional: " + atipicos.size() + 
                        (atipicos.isEmpty() ? " (Sin atípicos)" : " (" + atipicos.get(0) + " años, caso límite plausible - Observación, no error)"));

            Map<String, Integer> dups = obtenerEmailsDuplicadosTitulados();
            int totalDups = 0;
            for (int cnt : dups.values()) totalDups += (cnt - 1);
            reporte.add("Registros de titulados con Email duplicado: " + totalDups + " registros / " + dups.size() + 
                        " personas distintas (" + (totalDups > 0 ? "Hallazgo — corregir con depuración" : "Correcto") + ")");
        } else {
            Map<String, Integer> emails = new HashMap<>();
            for (HashMap<String, String> f : datos) {
                String email = f.getOrDefault("EmailContacto", f.getOrDefault("emailcontacto", "")).trim().toLowerCase();
                if (!email.isEmpty()) emails.put(email, emails.getOrDefault(email, 0) + 1);
            }
            int dupsEmp = 0;
            for (int c : emails.values()) if (c > 1) dupsEmp += (c - 1);
            reporte.add("Registros de empleadores con Email duplicado: " + dupsEmp + " de " + datos.size() + " (Correcto)");
        }
        return reporte;
    }

    public double calcularTasaInsercion() {
        if (!hayDatosTitulados()) return 0.0;
        ArrayList<String> situaciones = obtenerColumna(true, "SituacionLaboral");
        if (situaciones.isEmpty()) return 0.0;

        int ocupados = 0;
        for (String s : situaciones) {
            String val = s.toLowerCase();
            if (val.contains("no trabaj") || val.contains("desemple")) {
                continue;
            }
            if (val.contains("organizaci") || val.contains("empresa") || val.contains("emprend") || val.contains("trabaj")) {
                ocupados++;
            }
        }
        return (ocupados * 100.0) / situaciones.size();
    }

    public double calcularInteresPosgrado() {
        if (!hayDatosTitulados()) return 0.0;
        ArrayList<String> interes = obtenerColumna(true, "InteresPosgrado");
        if (interes.isEmpty()) interes = obtenerColumna(true, "RealizoPosgrado");
        if (interes.isEmpty()) return 0.0;

        int si = 0;
        for (String s : interes) {
            if (s == null) continue;
            String trim = s.trim();
            if ("si".equalsIgnoreCase(trim) || "sí".equalsIgnoreCase(trim)) si++;
        }
        return (si * 100.0) / interes.size();
    }
    private static final Set<String> COLUMNAS_EXCLUIDAS = new HashSet<>(Arrays.asList(
        // Identificadores y Contacto
        "email", "emailcontacto", "telefono", "linkedin", "contacto",
        // Nombres propios específicos
        "nombreempresa", "nombreprograma",
        // Campos de especificación abierta "Otro..."
        "otrorubroorg", "otraareatrabajo", "otrocargo", "otramateriaventaja", "otramateriamenos",
        // Párrafos cualitativos abiertos
        "razondesempleo", "cargosdesempenados", "areasemergentes", "herramientastecnologicas", "habilidadesblandas"
    ));

    public static boolean esColumnaAnalizable(String nombreColumna) {
        if (nombreColumna == null || nombreColumna.trim().isEmpty()) return false;
        String clean = nombreColumna.trim().toLowerCase();
        if (COLUMNAS_EXCLUIDAS.contains(clean)) return false;
        if (clean.startsWith("otro") || clean.startsWith("otra")) return false;
        return true;
    }

    public String[] getColumnasTitulados() {
        return filtrarColumnasAnalizables(columnasTitulados);
    }

    public String[] getColumnasEmpleadores() {
        return filtrarColumnasAnalizables(columnasEmpleadores);
    }

    public String[] getColumnasCompletasTitulados() {
        return columnasTitulados;
    }

    public String[] getColumnasCompletasEmpleadores() {
        return columnasEmpleadores;
    }

    private String[] filtrarColumnasAnalizables(String[] columnas) {
        if (columnas == null) return new String[0];
        ArrayList<String> filtradas = new ArrayList<>();
        for (String col : columnas) {
            if (esColumnaAnalizable(col)) {
                filtradas.add(col);
            }
        }
        return filtradas.toArray(new String[0]);
    }

    public boolean hayDatosTitulados() { return !datosTitulados.isEmpty(); }
    public boolean hayDatosEmpleadores() { return !datosEmpleadores.isEmpty(); }

    public static ArrayList<String> parsearLineaCSV(String linea) {
        ArrayList<String> campos = new ArrayList<>();
        if (linea == null) return campos;

        StringBuilder sb = new StringBuilder();
        boolean dentroComillas = false;
        boolean tuvoComillas = false;

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);

            if (c == '"') {
                tuvoComillas = true;
                if (dentroComillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++; // saltar comilla escapada ("")
                } else {
                    dentroComillas = !dentroComillas;
                }
            } else if (c == ',' && !dentroComillas) {
                String token = tuvoComillas ? sb.toString() : sb.toString().trim();
                campos.add(token);
                sb.setLength(0);
                tuvoComillas = false;
            } else {
                sb.append(c);
            }
        }
        String token = tuvoComillas ? sb.toString() : sb.toString().trim();
        campos.add(token);
        return campos;
    }
}
