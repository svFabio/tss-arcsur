package Modelo;

/**
 * DTO (Data Transfer Object) que encapsula los resultados de estadísticas
 * descriptivas univariadas para cualquier tipo de variable:
 * - Cuantitativas continuas/discretas (Media, Mediana, Desviación, Rango, N)
 * - Escalas Likert / Ordinales (Puntaje Medio, Mediana, Desviación, Rango, N)
 * - Binarias / Bernoulli (Tasa Afirmativa %, Moda, Desv. Binomial, Rango [0-1], N)
 * - Cualitativas Nominales (Moda Mayoritaria %, 2do Lugar %, Categorías Únicas, Rango Frecuencias, N)
 */
public class ResumenEstadistico {

    public final String titulo1, valor1;
    public final String titulo2, valor2;
    public final String titulo3, valor3;
    public final String titulo4, valor4;
    public final String titulo5, valor5;

    public final double media;
    public final double mediana;
    public final double desviacion;
    public final double min;
    public final double max;
    public final int n;

    /**
     * Constructor general con títulos y valores personalizados.
     */
    public ResumenEstadistico(String t1, String v1,
                             String t2, String v2,
                             String t3, String v3,
                             String t4, String v4,
                             String t5, String v5,
                             double media, double mediana, double desviacion,
                             double min, double max, int n) {
        this.titulo1 = t1; this.valor1 = v1;
        this.titulo2 = t2; this.valor2 = v2;
        this.titulo3 = t3; this.valor3 = v3;
        this.titulo4 = t4; this.valor4 = v4;
        this.titulo5 = t5; this.valor5 = v5;
        this.media = media;
        this.mediana = mediana;
        this.desviacion = desviacion;
        this.min = min;
        this.max = max;
        this.n = n;
    }

    /**
     * Constructor compatible con versiones anteriores (numérico estándar).
     */
    public ResumenEstadistico(double media, double mediana, double desviacion,
                              double min, double max, int n) {
        this("Media (x̄)",                String.format("%.2f", media),
             "Mediana (Me)",               String.format("%.2f", mediana),
             "Desv. Estándar (s)",         "±" + String.format("%.2f", desviacion),
             "Rango [Mín - Máx]",          String.format("%.2f", min) + " - " + String.format("%.2f", max),
             "Muestra Válida (N)",          String.valueOf(n),
             media, mediana, desviacion, min, max, n);
    }

    // ── Métodos de Fábrica (Factory Methods) ─────────────────────────────────

    public static ResumenEstadistico crearCuantitativo(double med, double mdn, double desv,
                                                      double min, double max, int n) {
        return new ResumenEstadistico(
                "Media (x̄)",                String.format("%.2f", med),
                "Mediana (Me)",               String.format("%.2f", mdn),
                "Desv. Estándar (s)",         "±" + String.format("%.2f", desv),
                "Rango [Mín - Máx]",          String.format("%.2f", min) + " - " + String.format("%.2f", max),
                "Muestra Válida (N)",          String.valueOf(n),
                med, mdn, desv, min, max, n
        );
    }

    public static ResumenEstadistico crearLikert(double med, double mdn, double desv,
                                                double min, double max, int n, double escalaMax) {
        String mediaStr = String.format("%.2f / %.0f", med, escalaMax);
        return new ResumenEstadistico(
                "Puntaje Medio (x̄)",          mediaStr,
                "Mediana (Me)",               String.format("%.2f", mdn),
                "Desv. Estándar (s)",         "±" + String.format("%.2f", desv),
                "Rango [Mín - Máx]",          String.format("%.0f - %.0f", min, max),
                "Muestra Válida (N)",          String.valueOf(n),
                med, mdn, desv, min, max, n
        );
    }

    public static ResumenEstadistico crearBinario(String catA, int countA,
                                                 String catB, int countB,
                                                 int n) {
        String mayoritaria = (countA >= countB) ? catA : catB;
        int countMayor = Math.max(countA, countB);
        double propMayor = n > 0 ? ((double) countMayor / n) : 0.0;
        double p = n > 0 ? ((double) countA / n) : 0.0;
        double desvBinomial = Math.sqrt(p * (1.0 - p));

        String val1 = String.format("%.1f%% %s", propMayor * 100.0, mayoritaria);
        String val2 = mayoritaria;
        String val3 = "±" + String.format("%.2f", desvBinomial);
        String val4 = catA + " / " + catB;
        String val5 = String.valueOf(n);

        return new ResumenEstadistico(
                "Tasa Dominante (%)",          val1,
                "Moda / Mayoritaria",          val2,
                "Desv. Binomial",              val3,
                "Categorías",                  val4,
                "Muestra Válida (N)",          val5,
                p, (countA >= countB ? 1.0 : 0.0), desvBinomial, 0.0, 1.0, n
        );
    }

    public static ResumenEstadistico crearNominal(String top1Nombre, int top1Count,
                                                 String top2Nombre, int top2Count,
                                                 int totalCategorias, int minFrec, int maxFrec,
                                                 int n) {
        double pct1 = n > 0 ? (top1Count * 100.0 / n) : 0;
        double pct2 = n > 0 ? (top2Count * 100.0 / n) : 0;

        String val1 = top1Nombre + String.format(" (%.1f%%)", pct1);
        String val2 = (top2Nombre != null && !top2Nombre.isEmpty()) ?
                top2Nombre + String.format(" (%.1f%%)", pct2) : "N/A";
        String val3 = totalCategorias + " categorías";
        String val4 = minFrec + " - " + maxFrec + " resp.";
        String val5 = String.valueOf(n);

        return new ResumenEstadistico(
                "Moda (Mayoritaria)",          val1,
                "2da Preferencia",             val2,
                "Diversidad Categórica",       val3,
                "Rango Frecuencias",           val4,
                "Muestra Válida (N)",          val5,
                0, 0, 0, minFrec, maxFrec, n
        );
    }

    // ── Métodos de acceso a cadenas de texto ─────────────────────────────────

    public String getMediaStr() {
        return valor1;
    }

    public String getMedianaStr() {
        return valor2;
    }

    public String getDesviacionStr() {
        return valor3;
    }

    public String getMinStr() {
        return String.format("%.2f", min);
    }

    public String getMaxStr() {
        return String.format("%.2f", max);
    }

    public String getNStr() {
        return valor5;
    }
}
