package Vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

public class PanelEncuestaEmpleadores extends JPanel {

    // 1. Datos de la empresa
    private JTextField txtCorreo;
    private JTextField txtNombreEmpresa;
    private JComboBox<String> comboTipoOrganizacion;
    private JComboBox<String> comboTamanio;
    private JComboBox<String> comboRubro;

    // 2. Empleabilidad
    private JRadioButton rbContratoSi;
    private JRadioButton rbContratoNo;
    private ButtonGroup grupoContrato;
    private JComboBox<String> comboProyeccionContratacion;
    private JComboBox<String> comboNivelAcademicoDemandado;
    private JComboBox<String> comboMedioConvocatoria;
    private ArrayList<JCheckBox> chkCargos;

    // 3. Tendencias
    private JTextArea txtAreasEmergentes;
    private JTextArea txtHerramientasTecnologicas;
    private JTextArea txtHabilidadesFundamentales;

    // 4. Formación y Perfil (Likert)
    private ArrayList<JComboBox<String>> combosLikert;

    public JButton btnEnviar;

    public PanelEncuestaEmpleadores() {
        setLayout(new BorderLayout());
        setBackground(ConfiguracionColores.COLOR_FONDO);

        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBorder(new EmptyBorder(15, 25, 25, 25));
        contenedor.setBackground(ConfiguracionColores.COLOR_FONDO);

        // Encabezado
        contenedor.add(crearEncabezado("ENCUESTA A EMPLEADORES DEL SECTOR PRODUCTIVO",
                "Autoevaluación de la Carrera de Ingeniería de Sistemas - UMSS"));
        contenedor.add(Box.createRigidArea(new Dimension(0, 15)));

        // 1. Datos de la Empresa
        JPanel pnlEmpresa = crearSeccion("1. Identificación de la Empresa o Institución");
        txtCorreo = new JTextField(25);
        txtCorreo.setDocument(ValidadorDocumento.email(60));
        pnlEmpresa.add(crearFila("Correo electrónico de contacto:", txtCorreo));

        txtNombreEmpresa = new JTextField(25);
        txtNombreEmpresa.setDocument(ValidadorDocumento.alfanumerico(80));
        pnlEmpresa.add(crearFila("Nombre de la Empresa o Institución:", txtNombreEmpresa));

        comboTipoOrganizacion = new JComboBox<>(new String[]{"Seleccione tipo...", "Público", "Privado"});
        pnlEmpresa.add(crearFila("Tipo de organización:", comboTipoOrganizacion));

        comboTamanio = new JComboBox<>(new String[]{
                "Seleccione tamaño...",
                "Grande (100 funcionarios o más)",
                "Mediana (entre 31 a 99 funcionarios)",
                "Pequeña (entre 11 a 30 funcionarios)",
                "Micro (hasta 10 funcionarios)"
        });
        pnlEmpresa.add(crearFila("Tamaño de la organización:", comboTamanio));

        comboRubro = new JComboBox<>(new String[]{
                "Seleccione rubro...",
                "Desarrollo de software",
                "Servicios de tecnologías de información (TI)",
                "Telecomunicaciones y redes",
                "Soporte técnico y mantenimiento de sistemas",
                "Consultoría en sistemas / transformación digital",
                "Banca y servicios financieros",
                "Instituciones públicas / gobierno",
                "Educación e investigación",
                "Industria y manufactura",
                "Comercio y comercio electrónico",
                "Salud",
                "Seguridad informática / ciberseguridad",
                "Análisis de datos / inteligencia de negocios",
                "Startups o emprendimientos tecnológicos",
                "Otros"
        });
        pnlEmpresa.add(crearFila("Rubro o sector principal:", comboRubro));
        contenedor.add(pnlEmpresa);
        contenedor.add(Box.createRigidArea(new Dimension(0, 15)));

        // 2. Empleabilidad
        JPanel pnlEmpleabilidad = crearSeccion("2. Empleabilidad y Demanda Laboral");
        rbContratoSi = new JRadioButton("Sí");
        rbContratoNo = new JRadioButton("No");
        grupoContrato = new ButtonGroup();
        grupoContrato.add(rbContratoSi);
        grupoContrato.add(rbContratoNo);
        JPanel pnlRbContrato = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlRbContrato.setOpaque(false);
        pnlRbContrato.add(rbContratoSi);
        pnlRbContrato.add(rbContratoNo);
        pnlEmpleabilidad.add(crearFila("¿Ha contratado titulados de Sistemas (últimos 5 años)?:", pnlRbContrato));

        comboProyeccionContratacion = new JComboBox<>(new String[]{
                "Seleccione probabilidad...",
                "Sí, existe una alta probabilidad de contratación",
                "Sí, dependiendo del crecimiento o nuevos proyectos",
                "No se tiene prevista la contratación actualmente",
                "No requiere profesionales del área de Sistemas"
        });
        pnlEmpleabilidad.add(crearFila("Proyección de contratación para próximos años:", comboProyeccionContratacion));

        comboNivelAcademicoDemandado = new JComboBox<>(new String[]{
                "Seleccione nivel...",
                "Licenciatura",
                "Diplomado",
                "Especialidad",
                "Maestría",
                "Doctorado",
                "Post-doctorado"
        });
        pnlEmpleabilidad.add(crearFila("Nivel de formación académica demandado:", comboNivelAcademicoDemandado));

        comboMedioConvocatoria = new JComboBox<>(new String[]{
                "Seleccione medio...",
                "Internet y Redes sociales",
                "Postulación competitiva (convocatorias)",
                "Oferta profesional personal",
                "Recomendación de terceros",
                "Medios escritos y audiovisuales",
                "Otros"
        });
        pnlEmpleabilidad.add(crearFila("Medio de convocatoria utilizado:", comboMedioConvocatoria));

        pnlEmpleabilidad.add(crearEspacio(12));
        pnlEmpleabilidad.add(crearSeparador());
        pnlEmpleabilidad.add(crearEspacio(10));

        pnlEmpleabilidad.add(crearTituloBloque("Cargos que desempeñan los Ingenieros de Sistemas en su organización:"));
        pnlEmpleabilidad.add(crearEspacio(6));
        chkCargos = new ArrayList<>();
        String[] cargos = {
                "De apoyo / asistencia tecnológica",
                "Técnico (desarrollo de software, bases de datos, redes, soporte)",
                "Analista (sistemas, datos, procesos o soluciones)",
                "Especialista (ciberseguridad, arquitectura, infraestructura)",
                "Supervisor / Coordinador de área tecnológica",
                "Jefatura o Gerencial (proyectos, dirección de TI)",
                "Consultor tecnológico"
        };
        JPanel pnlChkCargos = new JPanel(new GridLayout(0, 2, 5, 5));
        pnlChkCargos.setOpaque(false);
        pnlChkCargos.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlChkCargos.setMaximumSize(new Dimension(950, 32767));
        for (String c : cargos) {
            JCheckBox cb = new JCheckBox(c);
            cb.setOpaque(false);
            chkCargos.add(cb);
            pnlChkCargos.add(cb);
        }
        pnlEmpleabilidad.add(pnlChkCargos);
        contenedor.add(pnlEmpleabilidad);
        contenedor.add(Box.createRigidArea(new Dimension(0, 15)));

        // 3. Requerimientos y Tendencias
        JPanel pnlTendencias = crearSeccion("3. Tendencias y Requerimientos del Sector");
        txtAreasEmergentes = new JTextArea(2, 20);
        txtAreasEmergentes.setDocument(ValidadorDocumento.textoLibre(500));
        txtAreasEmergentes.setLineWrap(true);
        txtAreasEmergentes.setWrapStyleWord(true);
        pnlTendencias.add(crearFilaArea("Nuevas áreas de conocimiento o tendencias emergentes clave:", new JScrollPane(txtAreasEmergentes)));

        pnlTendencias.add(crearEspacio(10));
        pnlTendencias.add(crearSeparador());
        pnlTendencias.add(crearEspacio(10));

        txtHerramientasTecnologicas = new JTextArea(2, 20);
        txtHerramientasTecnologicas.setDocument(ValidadorDocumento.textoLibre(500));
        txtHerramientasTecnologicas.setLineWrap(true);
        txtHerramientasTecnologicas.setWrapStyleWord(true);
        pnlTendencias.add(crearFilaArea("Nuevas herramientas y tecnologías que debería dominar:", new JScrollPane(txtHerramientasTecnologicas)));

        pnlTendencias.add(crearEspacio(10));
        pnlTendencias.add(crearSeparador());
        pnlTendencias.add(crearEspacio(10));

        txtHabilidadesFundamentales = new JTextArea(2, 20);
        txtHabilidadesFundamentales.setDocument(ValidadorDocumento.textoLibre(500));
        txtHabilidadesFundamentales.setLineWrap(true);
        txtHabilidadesFundamentales.setWrapStyleWord(true);
        pnlTendencias.add(crearFilaArea("Habilidades blandas y destrezas fundamentales requeridas:", new JScrollPane(txtHabilidadesFundamentales)));
        contenedor.add(pnlTendencias);
        contenedor.add(Box.createRigidArea(new Dimension(0, 15)));

        // 4. Formación y Perfil Profesional (Likert)
        JPanel pnlLikert = crearSeccion("4. Formación y Perfil Profesional (Valoración)");
        String[] criterios = {
                "1. La Carrera de Ing. Sistemas brinda confianza como formadora de profesionales:",
                "2. El título profesional es consistente con las competencias demostradas:",
                "3. Las autoridades consultan regularmente su opinión como empleadores:",
                "4. Conoce el perfil profesional y competencias de egreso declaradas:",
                "5. El perfil profesional declarado es coherente con el desempeño laboral real:",
                "6. La formación permite al graduado alcanzar un desempeño destacado integral:",
                "7. La Carrera considera opinión de empleadores para prácticas y pasantías:",
                "8. Demuestran competencias como comunicación, resolución de problemas y equipo:",
                "9. Demuestran valores éticos, responsabilidad social y ambiental:",
                "10. Ha participado en procesos de recopilación o evaluación de la Carrera:",
                "11. Mantiene vínculos con organizaciones que generan oportunidades laborales:",
                "12. Ha participado en actividades de vinculación laboral y tecnológica (FCyT):",
                "13. Ha participado en difusión de oportunidades laborales y pasantías:",
                "14. Vinculación activa entre profesionales, empleadores, docentes y estudiantes:",
                "15. Consultado sobre desempeño para retroalimentar el plan de estudios:",
                "16. Consultado sobre la pertinencia de la formación y el sector laboral:",
                "17. Recurre a la UMSS / Ing. Sistemas como fuente de búsqueda de talento:",
                "18. Consultado periódicamente en procesos de autoevaluación y mejora:"
        };

        String[] opcionesLikert = {"Seleccione...", "Totalmente de acuerdo", "Parcialmente de acuerdo", "Totalmente desacuerdo", "No sabe"};
        combosLikert = new ArrayList<>();

        for (String crit : criterios) {
            JComboBox<String> cmb = new JComboBox<>(opcionesLikert);
            combosLikert.add(cmb);
            pnlLikert.add(crearFila(crit, cmb));
            pnlLikert.add(crearEspacio(4));
        }
        contenedor.add(pnlLikert);
        contenedor.add(Box.createRigidArea(new Dimension(0, 20)));

        // Botón Enviar
        btnEnviar = new JButton("Registrar Respuesta de Empleador");
        btnEnviar.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnEnviar.setBackground(ConfiguracionColores.COLOR_PRIMARIO);
        btnEnviar.setForeground(Color.WHITE);
        btnEnviar.setFocusPainted(false);
        btnEnviar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEnviar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEnviar.setPreferredSize(new Dimension(320, 45));
        btnEnviar.setMaximumSize(new Dimension(320, 45));

        btnEnviar.addActionListener(e -> {
            btnEnviar.setEnabled(false);
            btnEnviar.setText("Registrando en la nube (Supabase)...");
            java.util.Map<String, String> datos = recolectarDatos();

            new Thread(() -> {
                boolean exito = Modelo.ServicioSupabase.insertarRegistro("encuestas_empleadores", datos);
                SwingUtilities.invokeLater(() -> {
                    btnEnviar.setEnabled(true);
                    btnEnviar.setText("Registrar Respuesta de Empleador");
                    if (exito) {
                        JOptionPane.showMessageDialog(this,
                                "¡Respuesta registrada exitosamente en Supabase!\nGracias por aportar con la visión del sector empleador.",
                                "Registro Exitoso",
                                JOptionPane.INFORMATION_MESSAGE);
                        limpiarFormulario();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "No se pudo guardar en la base de datos central.\nVerifique su conexión a internet.",
                                "Error de Conexión",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
            }).start();
        });

        contenedor.add(btnEnviar);
        contenedor.add(Box.createRigidArea(new Dimension(0, 25)));

        JScrollPane scroll = new JScrollPane(contenedor);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    private java.util.Map<String, String> recolectarDatos() {
        java.util.Map<String, String> m = new java.util.HashMap<>();
        m.put("correo", txtCorreo.getText().trim());
        m.put("nombre_empresa", txtNombreEmpresa.getText().trim());
        m.put("tipo_organizacion", comboTipoOrganizacion.getSelectedIndex() > 0 ? (String) comboTipoOrganizacion.getSelectedItem() : "");
        m.put("tamanio", comboTamanio.getSelectedIndex() > 0 ? (String) comboTamanio.getSelectedItem() : "");
        m.put("rubro", comboRubro.getSelectedIndex() > 0 ? (String) comboRubro.getSelectedItem() : "");

        m.put("contrato_titulados", rbContratoSi.isSelected() ? "SI" : (rbContratoNo.isSelected() ? "NO" : ""));
        m.put("proyeccion_contratacion", comboProyeccionContratacion.getSelectedIndex() > 0 ? (String) comboProyeccionContratacion.getSelectedItem() : "");
        m.put("nivel_academico_demandado", comboNivelAcademicoDemandado.getSelectedIndex() > 0 ? (String) comboNivelAcademicoDemandado.getSelectedItem() : "");
        m.put("medio_convocatoria", comboMedioConvocatoria.getSelectedIndex() > 0 ? (String) comboMedioConvocatoria.getSelectedItem() : "");

        StringBuilder sbCargos = new StringBuilder();
        for (JCheckBox cb : chkCargos) {
            if (cb.isSelected()) {
                if (sbCargos.length() > 0) sbCargos.append("; ");
                sbCargos.append(cb.getText());
            }
        }
        m.put("cargos_desempenados", sbCargos.toString());

        m.put("areas_emergentes", txtAreasEmergentes.getText().trim());
        m.put("herramientas_tecnologicas", txtHerramientasTecnologicas.getText().trim());
        m.put("habilidades_fundamentales", txtHabilidadesFundamentales.getText().trim());

        StringBuilder sbLikert = new StringBuilder();
        for (int i = 0; i < combosLikert.size(); i++) {
            JComboBox<String> cmb = combosLikert.get(i);
            if (i > 0) sbLikert.append("; ");
            sbLikert.append("Crit ").append(i + 1).append(": ").append(cmb.getSelectedItem());
        }
        m.put("criterios_likert", sbLikert.toString());

        return m;
    }

    public void limpiarFormulario() {
        txtCorreo.setText("");
        txtNombreEmpresa.setText("");
        comboTipoOrganizacion.setSelectedIndex(0);
        comboTamanio.setSelectedIndex(0);
        comboRubro.setSelectedIndex(0);

        grupoContrato.clearSelection();
        comboProyeccionContratacion.setSelectedIndex(0);
        comboNivelAcademicoDemandado.setSelectedIndex(0);
        comboMedioConvocatoria.setSelectedIndex(0);

        for (JCheckBox cb : chkCargos) cb.setSelected(false);

        txtAreasEmergentes.setText("");
        txtHerramientasTecnologicas.setText("");
        txtHabilidadesFundamentales.setText("");

        for (JComboBox<String> cmb : combosLikert) {
            cmb.setSelectedIndex(0);
        }
    }

    private JPanel crearSeccion(String titulo) {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(ConfiguracionColores.COLOR_PRIMARIO, 1, true),
                        titulo,
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("SansSerif", Font.BOLD, 14),
                        ConfiguracionColores.COLOR_PRIMARIO
                ),
                new EmptyBorder(10, 15, 15, 15)
        ));
        pnl.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnl.setMaximumSize(new Dimension(1000, 32767));
        return pnl;
    }

    private JPanel crearFila(String etiqueta, Component componente) {
        JPanel fila = new JPanel(new BorderLayout(15, 5));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(950, 35));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setPreferredSize(new Dimension(380, 25));

        fila.add(lbl, BorderLayout.WEST);
        fila.add(componente, BorderLayout.CENTER);
        return fila;
    }

    private JPanel crearFilaArea(String etiqueta, Component componente) {
        JPanel fila = new JPanel(new BorderLayout(15, 5));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(950, 65));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setPreferredSize(new Dimension(380, 25));

        fila.add(lbl, BorderLayout.WEST);
        fila.add(componente, BorderLayout.CENTER);
        return fila;
    }

    private JLabel crearTituloBloque(String texto) {
        JLabel lbl = new JLabel("<html><b>" + texto + "</b></html>");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(ConfiguracionColores.COLOR_PRIMARIO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(950, 26));
        return lbl;
    }

    private JComponent crearSeparador() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setMaximumSize(new Dimension(950, 2));
        sep.setForeground(new Color(215, 225, 235));
        sep.setBackground(Color.WHITE);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sep;
    }

    private Component crearEspacio(int alto) {
        Component c = Box.createRigidArea(new Dimension(0, alto));
        ((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    private JPanel crearEncabezado(String titulo, String subtitulo) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(15, 15, 15, 15));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setMaximumSize(new Dimension(1000, 100));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblTitulo.setForeground(ConfiguracionColores.COLOR_PRIMARIO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(new Font("SansSerif", Font.ITALIC, 13));
        lblSubtitulo.setForeground(ConfiguracionColores.COLOR_TEXTO);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(lblTitulo);
        header.add(Box.createRigidArea(new Dimension(0, 5)));
        header.add(lblSubtitulo);
        return header;
    }
}
