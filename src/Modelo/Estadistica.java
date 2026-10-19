package Modelo;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Estadistica {

    public static double media(ArrayList<Double> datos) {
        if (datos == null || datos.isEmpty()) return 0.0;
        double suma = 0;
        for (double d : datos) suma += d;
        return suma / datos.size();
    }

    public static double mediana(ArrayList<Double> datos) {
        if (datos == null || datos.isEmpty()) return 0.0;
        ArrayList<Double> copia = new ArrayList<>(datos);
        Collections.sort(copia);
        int n = copia.size();
        if (n % 2 == 0) {
            return (copia.get(n / 2 - 1) + copia.get(n / 2)) / 2.0;
        }
        return copia.get(n / 2);
    }

    public static double desviacionEstandar(ArrayList<Double> datos) {
        if (datos == null || datos.size() < 2) return 0.0;
        double m = media(datos);
        double suma = 0;
        for (double d : datos) {
            suma += (d - m) * (d - m);
        }
        // Desviación MUESTRAL: divide por N-1 (corrección de Bessel)
        return Math.sqrt(suma / (datos.size() - 1));
    }

    // ── Métodos cuantitativos y escalas Likert (HU1 y HU4) ──────────────────

    public static ArrayList<Double> extraerValoresNumericos(ArrayList<String> datos) {
        ArrayList<Double> resultado = new ArrayList<>();
        if (datos == null) return resultado;

        for (String raw : datos) {
            if (raw == null) continue;
            String s = raw.trim();
            if (s.isEmpty() || s.equalsIgnoreCase("N/a") || s.equalsIgnoreCase("null")
                    || s.equalsIgnoreCase("No aplica")) continue;

            Double val = parsearNumeroOIntervalo(s);
            if (val != null) {
                resultado.add(val);
            }
        }
        return resultado;
    }

    /**
     * Parsea un valor individual admitiendo:
     * - Números directos (enteros o decimales)
     * - Intervalos numéricos de edad ("23 - 26 años" -> 24.5)
     * - Intervalos salariales en Bs. ("Entre Bs. 2.500 - Bs. 3.500" -> 3000.0)
     * - Intervalos temporales ("Entre 1 a 2 años" -> 1.5, "Entre 1 - 4 meses" -> 2.5)
     * - Escalas Likert estándar (1 a 5)
     */
    public static Double parsearNumeroOIntervalo(String s) {
        if (s == null) return null;
        String trim = s.trim();
        if (trim.isEmpty() || trim.equalsIgnoreCase("N/a") || trim.equalsIgnoreCase("null")
                || trim.equalsIgnoreCase("No aplica")) return null;

        // 1. Parseo directo de número
        try {
            return Double.parseDouble(trim.replace(",", "."));
        } catch (NumberFormatException ignored) {}

        String lower = trim.toLowerCase();

        // 2. Mapeo Likert
        Double likert = mapearLikert(lower);
        if (likert != null) return likert;

        // 3. Rangos salariales (Bs.)
        if (lower.contains("bs")) {
            if (lower.contains("2.500") && lower.contains("3.500")) return 3000.0;
            if (lower.contains("3.501") && lower.contains("5.000")) return 4250.0;
            if (lower.contains("5.001") && lower.contains("7.000")) return 6000.0;
            if (lower.contains("7.001") && lower.contains("10.000")) return 8500.0;
            if (lower.contains("10.000")) return 12000.0;
        }

        // 4. Rangos de tiempo (Años / Meses)
        if (lower.contains("menos de 1 año") || lower.contains("menos de 1 mes")) return 0.5;
        if (lower.contains("1 a 2 años") || lower.contains("1 - 2 años")) return 1.5;
        if (lower.contains("2 a 5 años") || lower.contains("2 - 5 años")) return 3.5;
        if (lower.contains("5 a 8 años") || lower.contains("5 - 8 años")) return 6.5;
        if (lower.contains("1 - 4 meses") || lower.contains("1 a 4 meses")) return 2.5;
        if (lower.contains("4 - 8 meses") || lower.contains("4 a 8 meses")) return 6.0;
        if (lower.contains("ya trabajaba antes")) return 0.0;

        // 5. Rangos de edad o numéricos generales: "A - B" -> (A+B)/2
        Pattern rangePattern = Pattern.compile("(\\d+)\\s*(?:-|–|a)\\s*(\\d+)");
        Matcher mr = rangePattern.matcher(trim);
        if (mr.find()) {
            try {
                double a = Double.parseDouble(mr.group(1));
                double b = Double.parseDouble(mr.group(2));
                return (a + b) / 2.0;
            } catch (Exception ignored) {}
        }

        // 6. "35 años o más" / "35+"
        if (lower.contains("35") && (lower.contains("más") || lower.contains("+"))) {
            return 37.0;
        }

        // 7. Primer número encontrado en strings mixtos ("5 años", "12 meses")
        Pattern numPattern = Pattern.compile("^[+-]?\\d+([.,]\\d+)?");
        Matcher mn = numPattern.matcher(trim);
        if (mn.find()) {
            try {
                return Double.parseDouble(mn.group().replace(",", "."));
            } catch (Exception ignored) {}
        }

        return null;
    }

    /** Mapea etiquetas Likert a su valor numérico estándar (1 a 5). */
    public static Double mapearLikert(String lower) {
        if (lower.contains("totalmente de acuerdo") || lower.contains("muy de acuerdo")
                || lower.contains("muy satisfecho") || lower.contains("excelente")) {
            return 5.0;
        }
        if (lower.contains("parcialmente de acuerdo")) {
            return 3.0;
        }
        if (lower.contains("de acuerdo") || lower.contains("satisfecho") || lower.contains("bueno")) {
            return 4.0;
        }
        if (lower.contains("neutral") || lower.contains("ni de acuerdo") || lower.contains("regular")
                || lower.contains("algo satisfecho")) {
            return 3.0;
        }
        if (lower.contains("totalmente desacuerdo") || lower.contains("totalmente en desacuerdo")
                || lower.contains("muy insatisfecho") || lower.contains("muy malo")) {
            return 1.0;
        }
        if (lower.contains("en desacuerdo") || lower.contains("desacuerdo")
                || lower.contains("insatisfecho") || lower.contains("malo")) {
            return 2.0;
        }
        return null; // "no sabe" u otros
    }

    public static boolean esColumnaCuantitativa(ArrayList<String> datos) {
        if (datos == null || datos.isEmpty()) return false;
        int noVacios = 0;
        int numericos = 0;

        for (String raw : datos) {
            if (raw == null) continue;
            String s = raw.trim();
            if (s.isEmpty() || s.equalsIgnoreCase("N/a") || s.equalsIgnoreCase("null")
                    || s.equalsIgnoreCase("No aplica")) continue;
            noVacios++;
            if (parsearNumeroOIntervalo(s) != null) {
                numericos++;
            }
        }
        if (noVacios == 0) return false;
        return (double) numericos / noVacios >= 0.60;
    }

    /**
     * Método Universal de Cálculo Descriptivo:
     * Calcula y retorna el ResumenEstadistico para CUALQUIER columna, detectando
     * inteligentemente si es cuantitativa, ordinal Likert, binaria o nominal.
     */
    public static ResumenEstadistico calcularResumenColumna(ArrayList<String> datosCrudos, String nombreColumna) {
        if (datosCrudos == null || datosCrudos.isEmpty()) {
            return new ResumenEstadistico(0, 0, 0, 0, 0, 0);
        }

        // Filtrar registros no válidos
        ArrayList<String> datosValidos = new ArrayList<>();
        for (String d : datosCrudos) {
            if (d == null) continue;
            String t = d.trim();
            if (!t.isEmpty() && !t.equalsIgnoreCase("N/a") && !t.equalsIgnoreCase("null")
                    && !t.equalsIgnoreCase("No aplica")) {
                datosValidos.add(t);
            }
        }

        if (datosValidos.isEmpty()) {
            return new ResumenEstadistico(
                    "Sin Datos", "—", "Sin Datos", "—", "Sin Datos", "—",
                    "Sin Datos", "—", "Muestra Válida (N)", "0",
                    0, 0, 0, 0, 0, 0
            );
        }

        // 1. Evaluar si es cuantitativa o Likert
        if (esColumnaCuantitativa(datosValidos)) {
            ArrayList<Double> valores = extraerValoresNumericos(datosValidos);
            if (!valores.isEmpty()) {
                double med  = media(valores);
                double mdn  = mediana(valores);
                double desv = desviacionEstandar(valores);
                double min  = Collections.min(valores);
                double max  = Collections.max(valores);
                int n       = valores.size();

                // Detectar si fue mapeado desde Likert
                boolean esLikert = false;
                for (String s : datosValidos) {
                    if (mapearLikert(s.toLowerCase()) != null) {
                        esLikert = true;
                        break;
                    }
                }

                if (esLikert) {
                    double escalaMax = max <= 4.0 ? 4.0 : 5.0;
                    return ResumenEstadistico.crearLikert(med, mdn, desv, min, max, n, escalaMax);
                } else {
                    return ResumenEstadistico.crearCuantitativo(med, mdn, desv, min, max, n);
                }
            }
        }

        // 2. Evaluar si es Binaria / Dicotómica (2 categorías únicas, ej. Hombre/Mujer, SI/NO)
        LinkedHashMap<String, Integer> frecuencias = frecuenciaAbsoluta(datosValidos);
        if (frecuencias.size() == 2) {
            Iterator<Map.Entry<String, Integer>> it = frecuencias.entrySet().iterator();
            Map.Entry<String, Integer> e1 = it.next();
            Map.Entry<String, Integer> e2 = it.next();
            return ResumenEstadistico.crearBinario(e1.getKey(), e1.getValue(),
                    e2.getKey(), e2.getValue(), datosValidos.size());
        }

        // 3. Cualitativa Nominal Pura (Enfoque A: Estadísticos Categóricos)
        List<Map.Entry<String, Integer>> lista = new ArrayList<>(frecuencias.entrySet());
        String top1Nombre = lista.get(0).getKey();
        int top1Count = lista.get(0).getValue();
        String top2Nombre = lista.size() > 1 ? lista.get(1).getKey() : "";
        int top2Count = lista.size() > 1 ? lista.get(1).getValue() : 0;
        int totalCategorias = frecuencias.size();
        int minFrec = lista.get(lista.size() - 1).getValue();
        int maxFrec = top1Count;

        return ResumenEstadistico.crearNominal(top1Nombre, top1Count,
                top2Nombre, top2Count, totalCategorias, minFrec, maxFrec, datosValidos.size());
    }

    public static ResumenEstadistico calcularResumen(ArrayList<Double> valores) {
        if (valores == null || valores.isEmpty()) {
            return new ResumenEstadistico(0, 0, 0, 0, 0, 0);
        }
        double med   = media(valores);
        double mdn   = mediana(valores);
        double desv  = desviacionEstandar(valores);
        double min   = Collections.min(valores);
        double max   = Collections.max(valores);
        return new ResumenEstadistico(med, mdn, desv, min, max, valores.size());
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
            if (trimmed.isEmpty() || trimmed.equalsIgnoreCase("N/a") || trimmed.equalsIgnoreCase("null")
                    || trimmed.equalsIgnoreCase("No aplica")) continue;

            if (esMultiple) {
                String[] partes = trimmed.split(";");
                for (String p : partes) {
                    String clean = p.trim();
                    if (!clean.isEmpty() && !clean.equalsIgnoreCase("N/a") && !clean.equalsIgnoreCase("null")
                            && !clean.equalsIgnoreCase("No aplica")) {
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
