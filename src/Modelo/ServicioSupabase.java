package Modelo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServicioSupabase {

    private static final String URL_BASE = System.getenv().getOrDefault("ARCUSUR_SUPABASE_URL", "https://favjltrahhrlqvqfdcjh.supabase.co/rest/v1/");
    private static final String API_KEY = System.getenv().getOrDefault("ARCUSUR_SUPABASE_KEY", "sb_publishable_f6b7av8RchmIgoGFYnDxaQ_gFmJQPqY");

    public static boolean insertarRegistro(String tabla, Map<String, String> valores) {
        HttpURLConnection conn = null;
        try {
            URL url = java.net.URI.create(URL_BASE + tabla).toURL();
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("apikey", API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Prefer", "return=minimal");
            conn.setDoOutput(true);

            String jsonPayload = construirJson(valores);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int codigoRespuesta = conn.getResponseCode();
            return (codigoRespuesta >= 200 && codigoRespuesta < 300);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static ArrayList<HashMap<String, String>> obtenerRegistros(String tabla) {
        ArrayList<HashMap<String, String>> registros = new ArrayList<>();
        HttpURLConnection conn = null;
        try {
            URL url = java.net.URI.create(URL_BASE + tabla + "?select=*").toURL();
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("apikey", API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Accept", "application/json");

            int codigo = conn.getResponseCode();
            if (codigo >= 200 && codigo < 300) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder sb = new StringBuilder();
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        sb.append(linea);
                    }
                    registros = parsearJsonArray(sb.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return registros;
    }

    private static String construirJson(Map<String, String> valores) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean primero = true;
        for (Map.Entry<String, String> entry : valores.entrySet()) {
            if (!primero) sb.append(",");
            sb.append("\"").append(escapar(entry.getKey())).append("\":");
            String v = entry.getValue();
            if (v == null) {
                sb.append("null");
            } else {
                sb.append("\"").append(escapar(v)).append("\"");
            }
            primero = false;
        }
        sb.append("}");
        return sb.toString();
    }

    private static String escapar(String valor) {
        if (valor == null) return "";
        return valor.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    private static ArrayList<HashMap<String, String>> parsearJsonArray(String json) {
        ArrayList<HashMap<String, String>> lista = new ArrayList<>();
        if (json == null || json.trim().isEmpty()) return lista;

        String contenido = json.trim();
        if (contenido.startsWith("[") && contenido.endsWith("]")) {
            contenido = contenido.substring(1, contenido.length() - 1).trim();
        }

        if (contenido.isEmpty()) return lista;

        boolean dentroString = false;
        boolean escape = false;
        int llaves = 0;
        int inicioObjeto = -1;

        for (int i = 0; i < contenido.length(); i++) {
            char c = contenido.charAt(i);

            if (escape) {
                escape = false;
                continue;
            }

            if (c == '\\') {
                escape = true;
                continue;
            }

            if (c == '"') {
                dentroString = !dentroString;
                continue;
            }

            if (!dentroString) {
                if (c == '{') {
                    if (llaves == 0) inicioObjeto = i;
                    llaves++;
                } else if (c == '}') {
                    llaves--;
                    if (llaves == 0 && inicioObjeto != -1) {
                        String objJson = contenido.substring(inicioObjeto, i + 1);
                        HashMap<String, String> mapa = parsearObjetoJson(objJson);
                        if (!mapa.isEmpty()) {
                            lista.add(mapa);
                        }
                        inicioObjeto = -1;
                    }
                }
            }
        }
        return lista;
    }

    private static HashMap<String, String> parsearObjetoJson(String json) {
        HashMap<String, String> mapa = new HashMap<>();
        String str = json.trim();
        if (str.startsWith("{") && str.endsWith("}")) {
            str = str.substring(1, str.length() - 1).trim();
        }

        boolean dentroString = false;
        boolean escape = false;
        int braceDepth = 0;
        int bracketDepth = 0;
        int start = 0;
        ArrayList<String> pares = new ArrayList<>();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (escape) {
                escape = false;
                continue;
            }
            if (c == '\\') {
                escape = true;
                continue;
            }
            if (c == '"') {
                dentroString = !dentroString;
                continue;
            }
            if (!dentroString) {
                if (c == '{') braceDepth++;
                else if (c == '}') braceDepth--;
                else if (c == '[') bracketDepth++;
                else if (c == ']') bracketDepth--;
                else if (c == ',' && braceDepth == 0 && bracketDepth == 0) {
                    pares.add(str.substring(start, i).trim());
                    start = i + 1;
                }
            }
        }
        if (start < str.length()) {
            pares.add(str.substring(start).trim());
        }

        for (String par : pares) {
            int separador = encontrarSeparadorClaveValor(par);
            if (separador != -1) {
                String clave = desescapar(par.substring(0, separador).trim());
                String valor = desescapar(par.substring(separador + 1).trim());
                if (!clave.isEmpty()) {
                    mapa.put(clave, valor);
                }
            }
        }
        return mapa;
    }

    private static int encontrarSeparadorClaveValor(String par) {
        boolean dentroString = false;
        boolean escape = false;
        for (int i = 0; i < par.length(); i++) {
            char c = par.charAt(i);
            if (escape) {
                escape = false;
                continue;
            }
            if (c == '\\') {
                escape = true;
                continue;
            }
            if (c == '"') {
                dentroString = !dentroString;
                continue;
            }
            if (c == ':' && !dentroString) {
                return i;
            }
        }
        return -1;
    }

    private static String desescapar(String s) {
        if (s == null) return "";
        String res = s.trim();
        boolean estabaEntrecomillado = false;
        if (res.startsWith("\"") && res.endsWith("\"") && res.length() >= 2) {
            res = res.substring(1, res.length() - 1);
            estabaEntrecomillado = true;
        }
        if (!estabaEntrecomillado && res.equalsIgnoreCase("null")) return "";

        res = decodificarUnicode(res);

        return res.replace("\\\"", "\"")
                  .replace("\\\\", "\\")
                  .replace("\\n", "\n")
                  .replace("\\r", "\r")
                  .replace("\\t", "\t");
    }

    private static String decodificarUnicode(String input) {
        if (input == null || !input.contains("\\u")) return input;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            if (i + 5 < input.length() && input.charAt(i) == '\\' && input.charAt(i + 1) == 'u') {
                try {
                    int codePoint = Integer.parseInt(input.substring(i + 2, i + 6), 16);
                    sb.append((char) codePoint);
                    i += 6;
                    continue;
                } catch (NumberFormatException ignored) {}
            }
            sb.append(input.charAt(i));
            i++;
        }
        return sb.toString();
    }
}
