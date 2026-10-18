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
        LinkedHashMap<String, Integer> frecuencias = new LinkedHashMap<>();
        for (String d : datos) {
            if (!d.isEmpty()) {
                frecuencias.put(d, frecuencias.getOrDefault(d, 0) + 1);
            }
        }
        return frecuencias;
    }

    public static LinkedHashMap<String, Double> frecuenciaRelativa(ArrayList<String> datos) {
        LinkedHashMap<String, Integer> absoluta = frecuenciaAbsoluta(datos);
        LinkedHashMap<String, Double> relativa = new LinkedHashMap<>();
        int total = 0;
        for (int v : absoluta.values()) total += v;
        for (Map.Entry<String, Integer> e : absoluta.entrySet()) {
            relativa.put(e.getKey(), (double) e.getValue() / total * 100);
        }
        return relativa;
    }
}
