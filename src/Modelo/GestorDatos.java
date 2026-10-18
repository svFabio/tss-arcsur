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
        ArrayList<HashMap<String, String>> datos = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), "UTF-8"));
            String linea = br.readLine();
            if (linea == null) {
                br.close();
                return false;
            }
            if (linea.startsWith("\uFEFF")) linea = linea.substring(1);

            ArrayList<String> columnasList = parsearLineaCSV(linea);
            String[] columnas = columnasList.toArray(new String[0]);

            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                ArrayList<String> valores = parsearLineaCSV(linea);
                HashMap<String, String> fila = new HashMap<>();
                for (int i = 0; i < columnas.length && i < valores.size(); i++) {
                    fila.put(columnas[i], valores.get(i));
                }
                datos.add(fila);
            }
            br.close();

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

    // Retorna valores no vacíos de una columna
    public ArrayList<String> obtenerColumna(boolean esTitulados, String nombreColumna) {
        ArrayList<HashMap<String, String>> datos = esTitulados ? datosTitulados : datosEmpleadores;
        ArrayList<String> valores = new ArrayList<>();
        for (HashMap<String, String> fila : datos) {
            String valor = fila.getOrDefault(nombreColumna, "");
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
            valores.add(fila.getOrDefault(nombreColumna, ""));
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

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);

            if (c == '"') {
                if (dentroComillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++; // saltar comilla escapada ("")
                } else {
                    dentroComillas = !dentroComillas;
                }
            } else if (c == ',' && !dentroComillas) {
                campos.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        campos.add(sb.toString().trim());
        return campos;
    }
}
