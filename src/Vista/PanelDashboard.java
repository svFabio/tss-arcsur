package Vista;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelDashboard extends JPanel {

    public JButton btnReporteHTML;
    public JButton btnExportarDatosDepurados;
    public JButton btnAuditoria;
    public JButton btnActualizar;

    private JLabel lblValTitulados;
    private JLabel lblValEmpleadores;
    private JLabel lblValInsercion;
    private JLabel lblValPosgrado;

    public GraficoRadar graficoRadar;
    private JEditorPane txtDiagnostico;

    public PanelDashboard() {
        setLayout(new BorderLayout(12, 12));
        setBackground(ConfiguracionColores.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        // ── 1. Barra Superior: Fila 1 (Títulos) y Fila 2 (Botones) ─────
        JPanel panelNorte = new JPanel(new BorderLayout(0, 8));
        panelNorte.setOpaque(false);

        JPanel panelTitulos = new JPanel(new GridLayout(2, 1, 0, 2));
        panelTitulos.setOpaque(false);
        JLabel lblTitulo = new JLabel("Dashboard Analitico Ejecutivo — Acreditacion ARCUSUR");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblTitulo.setForeground(new Color(27, 79, 138));

        JLabel lblSubtitulo = new JLabel("Sintesis exploratoria computacional, matrices y toma de decisiones estrategicas (HU5)");
        lblSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(100, 116, 139));
        panelTitulos.add(lblTitulo);
        panelTitulos.add(lblSubtitulo);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelAcciones.setOpaque(false);

        btnReporteHTML = new JButton("Generar Informe Ejecutivo (HTML)");
        estilizarBoton(btnReporteHTML, new Color(37, 99, 235), Color.WHITE);

        btnExportarDatosDepurados = new JButton("Exportar CSV Depurado");
        estilizarBoton(btnExportarDatosDepurados, new Color(13, 148, 136), Color.WHITE);

        btnAuditoria = new JButton("Trazabilidad / Auditoria");
        estilizarBoton(btnAuditoria, new Color(71, 85, 105), Color.WHITE);

        btnActualizar = new JButton("Actualizar");
        estilizarBoton(btnActualizar, new Color(241, 245, 249), new Color(30, 41, 59));

        panelAcciones.add(btnActualizar);
        panelAcciones.add(btnAuditoria);
        panelAcciones.add(btnExportarDatosDepurados);
        panelAcciones.add(btnReporteHTML);

        panelNorte.add(panelTitulos, BorderLayout.NORTH);
        panelNorte.add(panelAcciones, BorderLayout.SOUTH);

        // ── 2. Cinta de Tarjetas KPI ──────────────────────────────────
        JPanel panelKPIs = new JPanel(new GridLayout(1, 4, 12, 0));
        panelKPIs.setOpaque(false);
        panelKPIs.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        lblValTitulados = new JLabel("0", SwingConstants.CENTER);
        lblValEmpleadores = new JLabel("0", SwingConstants.CENTER);
        lblValInsercion = new JLabel("0.0%", SwingConstants.CENTER);
        lblValPosgrado = new JLabel("0.0%", SwingConstants.CENTER);

        panelKPIs.add(crearTarjetaKPI("MUESTRA TITULADOS", lblValTitulados, "Egresados encuestados", new Color(37, 99, 235)));
        panelKPIs.add(crearTarjetaKPI("MUESTRA EMPLEADORES", lblValEmpleadores, "Empresas e industrias", new Color(13, 148, 136)));
        panelKPIs.add(crearTarjetaKPI("INSERCION LABORAL", lblValInsercion, "Ocupacion activa formal", new Color(22, 163, 74)));
        panelKPIs.add(crearTarjetaKPI("INTERES EN POSGRADO", lblValPosgrado, "Demanda formacion continua", new Color(217, 119, 6)));

        JPanel contenedorSuperior = new JPanel(new BorderLayout());
        contenedorSuperior.setOpaque(false);
        contenedorSuperior.add(panelNorte, BorderLayout.NORTH);
        contenedorSuperior.add(panelKPIs, BorderLayout.SOUTH);
        add(contenedorSuperior, BorderLayout.NORTH);

        // ── 3. Panel Central: Radar Multidimensional + Diagnóstico ────
        JSplitPane splitCentral = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitCentral.setResizeWeight(0.52);
        splitCentral.setBorder(null);
        splitCentral.setOpaque(false);

        // Panel Izquierdo: Gráfico de Radar
        JPanel panelRadarContenedor = new JPanel(new BorderLayout());
        panelRadarContenedor.setBackground(Color.WHITE);
        panelRadarContenedor.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        graficoRadar = new GraficoRadar();
        graficoRadar.setTitulo("Brechas de Competencias y Areas Tematicas (Titulados vs Empleadores)");
        panelRadarContenedor.add(graficoRadar, BorderLayout.CENTER);

        // Panel Derecho: Diagnóstico y Recomendaciones Automáticas
        JPanel panelDiagContenedor = new JPanel(new BorderLayout());
        panelDiagContenedor.setBackground(Color.WHITE);
        panelDiagContenedor.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        JLabel lblTituloDiag = new JLabel("Diagnostico Analitico y Lineas de Accion Estrategica");
        lblTituloDiag.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTituloDiag.setForeground(new Color(30, 41, 59));
        lblTituloDiag.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        txtDiagnostico = new JEditorPane();
        txtDiagnostico.setContentType("text/html");
        txtDiagnostico.setEditable(false);
        txtDiagnostico.setBackground(new Color(248, 250, 252));
        actualizarTextoDiagnostico(null);

        JScrollPane scrollDiag = new JScrollPane(txtDiagnostico);
        scrollDiag.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        panelDiagContenedor.add(lblTituloDiag, BorderLayout.NORTH);
        panelDiagContenedor.add(scrollDiag, BorderLayout.CENTER);

        splitCentral.setLeftComponent(panelRadarContenedor);
        splitCentral.setRightComponent(panelDiagContenedor);

        add(splitCentral, BorderLayout.CENTER);
    }

    private JPanel crearTarjetaKPI(String etiqueta, JLabel lblValor, String subtitulo, Color colorBorde) {
        JPanel tarjeta = new JPanel(new BorderLayout(4, 4));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(3, 0, 0, 0, colorBorde),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            )
        ));

        JLabel lblTit = new JLabel(etiqueta);
        lblTit.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblTit.setForeground(new Color(100, 116, 139));

        lblValor.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblValor.setForeground(new Color(30, 41, 59));

        JLabel lblSub = new JLabel(subtitulo, SwingConstants.CENTER);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblSub.setForeground(new Color(148, 163, 184));

        tarjeta.add(lblTit, BorderLayout.NORTH);
        tarjeta.add(lblValor, BorderLayout.CENTER);
        tarjeta.add(lblSub, BorderLayout.SOUTH);
        return tarjeta;
    }

    private void estilizarBoton(JButton btn, Color fondo, Color texto) {
        btn.setBackground(fondo);
        btn.setForeground(texto);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fondo.darker(), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    public void actualizarKPIs(int muestraTitulados, int muestraEmpleadores,
                               double tasaInsercion, double interesPosgrado) {
        lblValTitulados.setText(String.valueOf(muestraTitulados));
        lblValEmpleadores.setText(String.valueOf(muestraEmpleadores));
        lblValInsercion.setText(String.format("%.1f%%", tasaInsercion));
        lblValPosgrado.setText(String.format("%.1f%%", interesPosgrado));
    }

    public void actualizarTextoDiagnostico(List<String> conclusiones) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:sans-serif; color:#334155; font-size:11px; padding:6px;'>");

        sb.append("<div style='background-color:#EFF6FF; border:1px solid #BFDBFE; padding:8px; border-radius:4px; margin-bottom:10px;'>");
        sb.append("<b>Sintesis para el Comite de Autoevaluacion:</b><br>");
        sb.append("Analisis exploratorio computacional automatizado segun criterios de calidad ARCUSUR.");
        sb.append("</div>");

        sb.append("<h4 style='color:#1E3A8A; margin:6px 0;'>Hallazgos Principales:</h4>");
        sb.append("<ul style='margin-top:4px; padding-left:18px;'>");

        if (conclusiones != null && !conclusiones.isEmpty()) {
            for (String c : conclusiones) {
                sb.append("<li style='margin-bottom:6px;'>").append(c).append("</li>");
            }
        } else {
            sb.append("<li>Cargue los datasets de Titulados y Empleadores para activar el motor analitico de diagnostico automatico.</li>");
        }
        sb.append("</ul>");

        sb.append("<h4 style='color:#1E3A8A; margin:10px 0 4px 0;'>Compromisos de Mejora Continua:</h4>");
        sb.append("<ol style='margin-top:4px; padding-left:18px;'>");
        sb.append("<li style='margin-bottom:4px;'><b>Actualizacion Curricular:</b> Ajustar asignaturas electivas a ramas de alta demanda computacional (IA, Cloud y Ciberseguridad).</li>");
        sb.append("<li style='margin-bottom:4px;'><b>Alianzas Estrategicas:</b> Fomentar convenios B2B y programas de pasantias de insercion temprana.</li>");
        sb.append("<li style='margin-bottom:4px;'><b>Oferta de Posgrado Modular:</b> Formalizar diplomados y especialidades con modalidades hibridas y financiamiento bancario.</li>");
        sb.append("</ol>");

        sb.append("</body></html>");
        txtDiagnostico.setText(sb.toString());
        txtDiagnostico.setCaretPosition(0);
    }
}
