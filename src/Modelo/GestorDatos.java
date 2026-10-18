package Modelo;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

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

            String[] columnas = linea.split(",");
            for (int i = 0; i < columnas.length; i++) {
                columnas[i] = columnas[i].trim();
            }

            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] valores = linea.split(",", -1);
                HashMap<String, String> fila = new HashMap<>();
                for (int i = 0; i < columnas.length && i < valores.length; i++) {
                    fila.put(columnas[i], valores[i].trim());
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
    public String[] getColumnasTitulados() { return columnasTitulados; }
    public String[] getColumnasEmpleadores() { return columnasEmpleadores; }
    public boolean hayDatosTitulados() { return !datosTitulados.isEmpty(); }
    public boolean hayDatosEmpleadores() { return !datosEmpleadores.isEmpty(); }
}
