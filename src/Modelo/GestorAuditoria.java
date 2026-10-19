package Modelo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class GestorAuditoria {

    public static class EntradaAuditoria {
        private final String fechaHora;
        private final String tipoAccion;
        private final String descripcion;
        private final String usuario;

        public EntradaAuditoria(String tipoAccion, String descripcion, String usuario) {
            this.fechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            this.tipoAccion = tipoAccion;
            this.descripcion = descripcion;
            this.usuario = usuario != null ? usuario : "Comite Evaluador ARCUSUR";
        }

        public String getFechaHora() { return fechaHora; }
        public String getTipoAccion() { return tipoAccion; }
        public String getDescripcion() { return descripcion; }
        public String getUsuario() { return usuario; }

        @Override
        public String toString() {
            return String.format("[%s] [%s] (%s): %s", fechaHora, tipoAccion, usuario, descripcion);
        }
    }

    private static final List<EntradaAuditoria> historial = Collections.synchronizedList(new ArrayList<>());
    private static final String ARCHIVO_LOG = "auditoria_arcsur.log";

    static {
        registrar("INICIO_SISTEMA", "Sistema tss-arcsur iniciado. Sesion de auditoria activa.", "Sistema");
    }

    public static void registrar(String tipoAccion, String descripcion) {
        registrar(tipoAccion, descripcion, "Administrador ARCUSUR");
    }

    public static void registrar(String tipoAccion, String descripcion, String usuario) {
        EntradaAuditoria entrada = new EntradaAuditoria(tipoAccion, descripcion, usuario);
        historial.add(entrada);

        // Guardar en archivo log persistente
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ARCHIVO_LOG, true), StandardCharsets.UTF_8))) {
            bw.write(entrada.toString());
            bw.newLine();
        } catch (Exception ignored) {
            // No interrumpir ejecución si falla escritura a disco
        }
    }

    public static List<EntradaAuditoria> getHistorial() {
        synchronized (historial) {
            return new ArrayList<>(historial);
        }
    }

    public static String obtenerTextoCompleto() {
        StringBuilder sb = new StringBuilder();
        synchronized (historial) {
            for (EntradaAuditoria entrada : historial) {
                sb.append(entrada.toString()).append("\n");
            }
        }
        return sb.toString();
    }

    public static boolean exportarLog(File destino) {
        if (destino == null) return false;
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destino), StandardCharsets.UTF_8))) {
            bw.write("\uFEFF");
            bw.write("=== REGISTRO DE AUDITORIA Y TRAZABILIDAD - SISTEMA TSS-ARCSUR ===\n");
            bw.write("Generado: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
            bw.write("Total de eventos registrados: " + historial.size() + "\n\n");
            bw.write(obtenerTextoCompleto());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
