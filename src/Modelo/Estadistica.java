package Modelo;

import java.util.*;

public class Estadistica {

    public static double media(ArrayList<Double> datos) {
        double suma = 0;
        for (double d : datos) suma += d;
        return suma / datos.size();
    }

    public static double mediana(ArrayList<Double> datos) {
        ArrayList<Double> copia = new ArrayList<>(datos);
        Collections.sort(copia);
        int n = copia.size();
        if (n % 2 == 0) {
            return (copia.get(n / 2 - 1) + copia.get(n / 2)) / 2.0;
        }
        return copia.get(n / 2);
    }

    public static double desviacionEstandar(ArrayList<Double> datos) {
        double m = media(datos);
        double suma = 0;
        for (double d : datos) {
            suma += (d - m) * (d - m);
        }
        return Math.sqrt(suma / datos.size());
    }

    public static LinkedHashMap<String, Integer> frecuenciaAbsoluta(ArrayList<String> datos) {
        boolean esMultiple = false;
        for (String d : datos) {
            if (d != null && d.contains(";")) {
                esMultiple = true;
                break;
            }
        }

        HashMap<String, Integer> conteo = new HashMap<>();
        for (String d : datos) {
            if (d == null) continue;
            String trimmed = d.trim();
            if (trimmed.isEmpty() || trimmed.equalsIgnoreCase("N/a") || trimmed.equalsIgnoreCase("null")) continue;

            if (esMultiple) {
                String[] partes = trimmed.split(";");
                for (String p : partes) {
                    String clean = p.trim();
                    if (!clean.isEmpty() && !clean.equalsIgnoreCase("N/a") && !clean.equalsIgnoreCase("null")) {
                        conteo.put(clean, conteo.getOrDefault(clean, 0) + 1);
                    }
                }
            } else {
                conteo.put(trimmed, conteo.getOrDefault(trimmed, 0) + 1);
            }
        }

        // Ordenar descendentemente por frecuencia y luego alfabéticamente
        List<Map.Entry<String, Integer>> lista = new ArrayList<>(conteo.entrySet());
        lista.sort((a, b) -> {
            int comp = b.getValue().compareTo(a.getValue());
            if (comp != 0) return comp;
            return a.getKey().compareToIgnoreCase(b.getKey());
        });

        LinkedHashMap<String, Integer> frecuencias = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : lista) {
            frecuencias.put(entry.getKey(), entry.getValue());
        }
        return frecuencias;
    }

    public static LinkedHashMap<String, Double> frecuenciaRelativa(ArrayList<String> datos) {
        LinkedHashMap<String, Integer> absoluta = frecuenciaAbsoluta(datos);
        LinkedHashMap<String, Double> relativa = new LinkedHashMap<>();
        int total = 0;
        for (int v : absoluta.values()) total += v;
        for (Map.Entry<String, Integer> e : absoluta.entrySet()) {
            relativa.put(e.getKey(), total > 0 ? ((double) e.getValue() / total * 100) : 0.0);
        }
        return relativa;
    }
}
