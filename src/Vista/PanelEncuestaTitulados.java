package Vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

public class PanelEncuestaTitulados extends JPanel {

    private JPanel contenedor;

    // Secciones principales
    private JPanel pnlGenerales;
    private JPanel pnlPosgrado;
    private JPanel pnlInteres;
    private JPanel pnlLaboral;
    private JPanel pnlFormacion;
    private JPanel pnlRed;

    // 0. Datos Generales
    private JComboBox<String> comboEdad;
    private JComboBox<String> comboGenero;

    // 1. Formación Complementaria (Bifurcación)
    private JRadioButton rbPosgradoSi;
    private JRadioButton rbPosgradoNo;
    private ButtonGroup grupoPosgrado;
    private JPanel pnlSubPosgrado;
    private JComboBox<String> comboNivelPosgrado;
    private JComboBox<String> comboLugarPosgrado;
    private JComboBox<String> comboFinanciamientoPosgrado;
    private JTextField txtNombrePrograma;

    // 2. Interés de Posgrado (Bifurcación)
    private JRadioButton rbInteresSi;
    private JRadioButton rbInteresNo;
    private ButtonGroup grupoInteres;
    private JPanel pnlSubInteres;
    private JComboBox<String> comboAreaInteres;
    private JComboBox<String> comboInstInteres;
    private JPanel pnlFilaOpinionFcyt;
    private JComboBox<String> comboOpinionFcyt;

    // 3. Trayectoria y Situación Laboral
    private JTextField txtAniosProfesional;
    private JTextField txtTiempoDesempleo;
    private JComboBox<String> comboSituacionLaboral;

    // 3.A Bloque Organización
    private JPanel pnlSubOrganizacion;
    private JTextField txtNombreEmpresa;
    private JComboBox<String> comboRubroOrg;
    private JTextField txtOtroRubroOrg;
    private JComboBox<String> comboSectorOrg;
    private JComboBox<String> comboDepartamento;
    private JComboBox<String> comboAreaTrabajo;
    private JTextField txtOtraAreaTrabajo;
    private JComboBox<String> comboCargo;
    private JTextField txtOtroCargo;
    private JComboBox<String> comboPertinenciaLaboral;
    private JComboBox<String> comboAntiguedadTrabajo;
    private JComboBox<String> comboMedioObtencion;
    private JComboBox<String> comboRemuneracion;

    // 3.B Bloque Desempleado
    private JPanel pnlSubDesempleado;
    private JComboBox<String> comboRazonDesempleo;
    private JRadioButton rbTrabajoAntesSi;
    private JRadioButton rbTrabajoAntesNo;
    private ButtonGroup grupoTrabajoAntes;

    // 3.C Bloque Emprendimiento
    private JPanel pnlSubEmprendimiento;
    private JComboBox<String> comboOrigenEmprendimiento;
    private JComboBox<String> comboEntregableEmprendimiento;
    private JComboBox<String> comboFinanciamientoEmprendimiento;
    private JComboBox<String> comboSatisfaccionEmprendimiento;
    private JComboBox<String> comboImportanciaFormacion;

    // Comunes trayectoria
    private JPanel pnlSubInsercionComun;
    private JRadioButton rbPrimerEmpleoSi;
    private JRadioButton rbPrimerEmpleoNo;
    private ButtonGroup grupoPrimerEmpleo;
    private JComboBox<String> comboTiempoConseguirEmpleo;
    private JComboBox<String> comboCantidadEmpleos;

    // 4. Formación de Grado
    private JComboBox<String> comboSatisfaccionGlobal;
    private JComboBox<String> comboConcordanciaMercado;
    private ArrayList<JCheckBox> chkUtilidad;
    private ArrayList<JCheckBox> chkMejoras;
    private ArrayList<JCheckBox> chkVentajas;
    private ArrayList<JCheckBox> chkMenorUtilidad;

    // 5. Red de Contactos (Bifurcación)
    private JRadioButton rbRedContactosSi;
    private JRadioButton rbRedContactosNo;
    private ButtonGroup grupoRedContactos;
    private JPanel pnlSubRedContactos;
    private JTextField txtEmail;
    private JTextField txtTelefono;
    private JTextField txtLinkedin;

    public JButton btnEnviar;

    public PanelEncuestaTitulados() {
        setLayout(new BorderLayout());
        setBackground(ConfiguracionColores.COLOR_FONDO);

        contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBorder(new EmptyBorder(15, 25, 25, 25));
        contenedor.setBackground(ConfiguracionColores.COLOR_FONDO);

        // Encabezado
        contenedor.add(crearEncabezado("ENCUESTA DE OPINIÓN A TITULADOS DE INGENIERÍA EN SISTEMAS",
                "Autoevaluación para Plan de Desarrollo Estratégico (ARCUSUR - UMSS)"));
        contenedor.add(Box.createRigidArea(new Dimension(0, 15)));

        // 0. Datos Generales
        pnlGenerales = crearSeccion("0. Datos Generales");
        comboEdad = new JComboBox<>(new String[]{"Seleccione su rango...", "15 - 18 años", "19 - 22 años", "23 - 26 años", "27 - 30 años", "31 - 34 años", "35 años o más"});
        pnlGenerales.add(crearFila("Edad que tiene actualmente:", comboEdad));
        comboGenero = new JComboBox<>(new String[]{"Seleccione género...", "Hombre", "Mujer"});
        pnlGenerales.add(crearFila("Género:", comboGenero));
        contenedor.add(pnlGenerales);
        contenedor.add(Box.createRigidArea(new Dimension(0, 15)));

        // 1. Formación Complementaria
        pnlPosgrado = crearSeccion("1. Formación Complementaria y Posgrado");
        rbPosgradoSi = new JRadioButton("SI");
        rbPosgradoNo = new JRadioButton("NO");
        grupoPosgrado = new ButtonGroup();
        grupoPosgrado.add(rbPosgradoSi);
        grupoPosgrado.add(rbPosgradoNo);

        JPanel pnlRbPos = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlRbPos.setOpaque(false);
        pnlRbPos.add(rbPosgradoSi);
        pnlRbPos.add(rbPosgradoNo);
        pnlPosgrado.add(crearFila("¿Ha realizado o realiza formación complementaria?:", pnlRbPos));

        // Subpanel condicional de Posgrado (oculto de inicio)
        pnlSubPosgrado = new JPanel();
        pnlSubPosgrado.setLayout(new BoxLayout(pnlSubPosgrado, BoxLayout.Y_AXIS));
        pnlSubPosgrado.setOpaque(false);
        pnlSubPosgrado.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlSubPosgrado.setVisible(false);

        comboNivelPosgrado = new JComboBox<>(new String[]{"Seleccione...", "DIPLOMADO", "ESPECIALIDAD", "MAESTRIA", "DOCTORADO", "POS DOCTORADO"});
        pnlSubPosgrado.add(crearFila("Programa de mayor nivel concluido o en curso:", comboNivelPosgrado));

        comboLugarPosgrado = new JComboBox<>(new String[]{"Seleccione...", "ESCUELA UNIVERSITARIA DE POSGRADO (EUPG)", "DIRECCIÓN DE POSGRADO FCyT", "IBNORCA", "UNIVERSIDADES INTERNACIONALES", "UNIVERSIDADES PÚBLICAS NACIONALES", "UNIVERSIDADES PRIVADAS NACIONALES", "OTRO"});
        pnlSubPosgrado.add(crearFila("¿Dónde ha cursado el programa indicado?:", comboLugarPosgrado));

        comboFinanciamientoPosgrado = new JComboBox<>(new String[]{"Seleccione...", "FINANCIADO CON RECURSOS PROPIOS", "TOTAL FINANCIAMIENTO POR MEDIO DE UNA BECA", "FINANCIADO PARCIALMENTE POR MEDIO DE UNA BECA"});
        pnlSubPosgrado.add(crearFila("Fuente de financiamiento del programa:", comboFinanciamientoPosgrado));

        txtNombrePrograma = new JTextField(25);
        txtNombrePrograma.setDocument(ValidadorDocumento.alfanumerico(80));
        pnlSubPosgrado.add(crearFila("Indique el nombre del programa:", txtNombrePrograma));

        pnlPosgrado.add(pnlSubPosgrado);
        contenedor.add(pnlPosgrado);
        contenedor.add(Box.createRigidArea(new Dimension(0, 15)));

        // 2. Interés en Posgrados (oculto de inicio hasta responder Sección 1)
        pnlInteres = crearSeccion("2. Preferencias e Interés en Posgrado");
        pnlInteres.setVisible(false);

        rbInteresSi = new JRadioButton("SI");
        rbInteresNo = new JRadioButton("NO");
        grupoInteres = new ButtonGroup();
        grupoInteres.add(rbInteresSi);
        grupoInteres.add(rbInteresNo);

        JPanel pnlRbInt = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlRbInt.setOpaque(false);
        pnlRbInt.add(rbInteresSi);
        pnlRbInt.add(rbInteresNo);
        pnlInteres.add(crearFila("¿Interesado en posgrados dentro de Ing. Sistemas?:", pnlRbInt));

        pnlSubInteres = new JPanel();
        pnlSubInteres.setLayout(new BoxLayout(pnlSubInteres, BoxLayout.Y_AXIS));
        pnlSubInteres.setOpaque(false);
        pnlSubInteres.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlSubInteres.setVisible(false);

        comboAreaInteres = new JComboBox<>(new String[]{"Seleccione...", "IA", "Robótica", "Ingenieria de Software", "Base de Datos", "Redes de Datos y Seguridad"});
        pnlSubInteres.add(crearFila("Área prioritaria de interés:", comboAreaInteres));

        comboInstInteres = new JComboBox<>(new String[]{"Seleccione...", "ESCUELA UNIVERSITARIA DE POSGRADO (EUPG)", "DIRECCIÓN DE POSGRADO FCyT", "IBNORCA", "UNIVERSIDADES INTERNACIONALES", "UNIVERSIDADES PÚBLICAS NACIONALES", "UNIVERSIDADES PRIVADAS NACIONALES", "OTRO"});
        pnlSubInteres.add(crearFila("Organización educativa por la que optaría:", comboInstInteres));

        pnlInteres.add(pnlSubInteres);

        comboOpinionFcyt = new JComboBox<>(new String[]{"Seleccione...", "Totalmente de Acuerdo", "De acuerdo", "En desacuerdo", "Totalmente en desacuerdo"});
        pnlFilaOpinionFcyt = crearFila("¿Existen posgrados afines en la FCyT?:", comboOpinionFcyt);
        pnlFilaOpinionFcyt.setVisible(false);
        pnlInteres.add(pnlFilaOpinionFcyt);

        contenedor.add(pnlInteres);
        contenedor.add(Box.createRigidArea(new Dimension(0, 15)));

        // Eventos Bifurcación Posgrados Previos
        rbPosgradoSi.addActionListener(e -> {
            pnlSubPosgrado.setVisible(true);
            pnlInteres.setVisible(true);
            refrescarVista();
        });
        rbPosgradoNo.addActionListener(e -> {
            pnlSubPosgrado.setVisible(false);
            limpiarSubPosgrado();
            pnlInteres.setVisible(true);
            refrescarVista();
        });

        // 3. Trayectoria y Situación Laboral (oculto de inicio hasta responder Sección 2)
        pnlLaboral = crearSeccion("3. Trayectoria y Situación Laboral");
        pnlLaboral.setVisible(false);

        txtAniosProfesional = new JTextField(10);
        txtAniosProfesional.setDocument(ValidadorDocumento.soloNumeros(2));
        pnlLaboral.add(crearFila("Años de vida profesional:", txtAniosProfesional));

        txtTiempoDesempleo = new JTextField(10);
        txtTiempoDesempleo.setDocument(ValidadorDocumento.soloNumeros(3));
        pnlLaboral.add(crearFila("Tiempo total desempleado (años/meses):", txtTiempoDesempleo));

        comboSituacionLaboral = new JComboBox<>(new String[]{
                "Seleccione situación laboral...",
                "Actualmente me encuentro trabajando un una organización",
                "Actualmente NO trabajo",
                "Actualmente dedico mi tiempo COMPLETO a un emprendimiento propio"
        });
        pnlLaboral.add(crearFila("Situación laboral actual:", comboSituacionLaboral));

        // 3.A Subpanel Organización
        pnlSubOrganizacion = new JPanel();
        pnlSubOrganizacion.setLayout(new BoxLayout(pnlSubOrganizacion, BoxLayout.Y_AXIS));
        pnlSubOrganizacion.setOpaque(false);
        pnlSubOrganizacion.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlSubOrganizacion.setVisible(false);

        txtNombreEmpresa = new JTextField(25);
        txtNombreEmpresa.setDocument(ValidadorDocumento.alfanumerico(80));
        pnlSubOrganizacion.add(crearFila("Nombre de la empresa u organización:", txtNombreEmpresa));

        comboRubroOrg = new JComboBox<>(new String[]{"Seleccione...", "Telecomunicaciones", "Educación", "Desarrollo de Software", "Redes de Computadoras", "Salud", "Banca", "Otro"});
        pnlSubOrganizacion.add(crearFila("Rubro de la organización:", comboRubroOrg));

        txtOtroRubroOrg = new JTextField(20);
        txtOtroRubroOrg.setDocument(ValidadorDocumento.soloLetras(50));
        pnlSubOrganizacion.add(crearFila("Otro rubro (si aplica):", txtOtroRubroOrg));

        comboSectorOrg = new JComboBox<>(new String[]{"Seleccione...", "PÚBLICO", "PRIVADO"});
        pnlSubOrganizacion.add(crearFila("Sector al que corresponde:", comboSectorOrg));

        comboDepartamento = new JComboBox<>(new String[]{"Seleccione...", "COCHABAMBA", "LA PAZ", "SANTA CRUZ", "CHUQUISACA", "TARIJA", "ORURO", "POTOSI", "BENI", "PANDO", "EXTRANJERO"});
        pnlSubOrganizacion.add(crearFila("Departamento de Bolivia donde trabaja:", comboDepartamento));

        comboAreaTrabajo = new JComboBox<>(new String[]{"Seleccione...", "Desarrollo", "Cómputo", "Administración", "Contabilidad", "R.R.H.H.", "Otra"});
        pnlSubOrganizacion.add(crearFila("Área donde desarrolla sus labores:", comboAreaTrabajo));

        txtOtraAreaTrabajo = new JTextField(20);
        txtOtraAreaTrabajo.setDocument(ValidadorDocumento.soloLetras(50));
        pnlSubOrganizacion.add(crearFila("Otra área (si aplica):", txtOtraAreaTrabajo));

        comboCargo = new JComboBox<>(new String[]{"Seleccione...", "Desarrollo", "Técnico", "Help Desk", "Ejecutivo", "Otro"});
        pnlSubOrganizacion.add(crearFila("Cargo que desempeña:", comboCargo));

        txtOtroCargo = new JTextField(20);
        txtOtroCargo.setDocument(ValidadorDocumento.soloLetras(50));
        pnlSubOrganizacion.add(crearFila("Otro cargo (si aplica):", txtOtroCargo));

        comboPertinenciaLaboral = new JComboBox<>(new String[]{"Seleccione...", "Totalmente de Acuerdo", "De acuerdo", "En desacuerdo", "Totalmente en desacuerdo"});
        pnlSubOrganizacion.add(crearFila("Labores pertinentes a formación en Sistemas:", comboPertinenciaLaboral));

        comboAntiguedadTrabajo = new JComboBox<>(new String[]{"Seleccione...", "Menos de 1 año", "Entre 1 a 2 años", "Entre 2 a 5 años", "Entre 5 a 8 años", "Entre 8 a 11 años", "Entre 11 a 15 años", "Entre 15 a 20 años", "más de 25 años"});
        pnlSubOrganizacion.add(crearFila("Antigüedad en su actual trabajo:", comboAntiguedadTrabajo));

        comboMedioObtencion = new JComboBox<>(new String[]{"Seleccione...", "INTERNET Y REDES SOCIALES", "POSTULACIÓN COMPETITIVA", "RECOMENDACIÓN DE TERCEROS", "MEDIOS ESCRITOS O AUDIOVISUALES", "OTROS"});
        pnlSubOrganizacion.add(crearFila("Medio por el que obtuvo el trabajo:", comboMedioObtencion));

        comboRemuneracion = new JComboBox<>(new String[]{"Seleccione...", "Menor a Bs. 2500", "Entre Bs. 2.500 - Bs. 3.500", "Entre Bs. 3.501 - Bs. 5.000", "Entre Bs. 5.001 - Bs. 7.000", "Entre Bs. 7.001 - Bs. 10.000", "Más de Bs. 10.000"});
        pnlSubOrganizacion.add(crearFila("Remuneración promedio mensual:", comboRemuneracion));
        pnlLaboral.add(pnlSubOrganizacion);

        // 3.B Subpanel Desempleado
        pnlSubDesempleado = new JPanel();
        pnlSubDesempleado.setLayout(new BoxLayout(pnlSubDesempleado, BoxLayout.Y_AXIS));
        pnlSubDesempleado.setOpaque(false);
        pnlSubDesempleado.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlSubDesempleado.setVisible(false);

        comboRazonDesempleo = new JComboBox<>(new String[]{
                "Seleccione razón...",
                "Demanda laboral inexistente.",
                "Incumplimiento de requisitos de experiencia previa.",
                "Incumplimiento de requisitos en habilidades específicas (idiomas, software, etc).",
                "Red de contactos limitada.",
                "Problemas de presentación del CV.",
                "Habilidades deficientes en las entrevistas.",
                "Ofertas laborales con remuneración muy bajas.",
                "Otro"
        });
        pnlSubDesempleado.add(crearFila("Razón por la que no trabaja en el área:", comboRazonDesempleo));

        rbTrabajoAntesSi = new JRadioButton("SI");
        rbTrabajoAntesNo = new JRadioButton("NO");
        grupoTrabajoAntes = new ButtonGroup();
        grupoTrabajoAntes.add(rbTrabajoAntesSi);
        grupoTrabajoAntes.add(rbTrabajoAntesNo);
        JPanel pnlRbAntes = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlRbAntes.setOpaque(false);
        pnlRbAntes.add(rbTrabajoAntesSi);
        pnlRbAntes.add(rbTrabajoAntesNo);
        pnlSubDesempleado.add(crearFila("¿Ha tenido algún trabajo antes?:", pnlRbAntes));

        rbTrabajoAntesSi.addActionListener(e -> {
            if (comboSituacionLaboral.getSelectedIndex() == 2) {
                pnlSubInsercionComun.setVisible(true);
                refrescarVista();
            }
        });
        rbTrabajoAntesNo.addActionListener(e -> {
            if (comboSituacionLaboral.getSelectedIndex() == 2) {
                pnlSubInsercionComun.setVisible(false);
                limpiarSubInsercion();
                refrescarVista();
            }
        });

        pnlLaboral.add(pnlSubDesempleado);

        // 3.C Subpanel Emprendimiento
        pnlSubEmprendimiento = new JPanel();
        pnlSubEmprendimiento.setLayout(new BoxLayout(pnlSubEmprendimiento, BoxLayout.Y_AXIS));
        pnlSubEmprendimiento.setOpaque(false);
        pnlSubEmprendimiento.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlSubEmprendimiento.setVisible(false);

        comboOrigenEmprendimiento = new JComboBox<>(new String[]{"Seleccione...", "Idea propia.", "Idea conjunta con compañeros.", "StartUp", "Spin Off (derivado de proyecto UMSS)", "Otro."});
        pnlSubEmprendimiento.add(crearFila("Origen del emprendimiento:", comboOrigenEmprendimiento));

        comboEntregableEmprendimiento = new JComboBox<>(new String[]{"Seleccione...", "Producto", "Servicio", "Ambos"});
        pnlSubEmprendimiento.add(crearFila("Entregable que genera el negocio:", comboEntregableEmprendimiento));

        comboFinanciamientoEmprendimiento = new JComboBox<>(new String[]{"Seleccione...", "No, financiamiento propio.", "Si, mediante préstamos bancarios.", "Si, mediante inversores."});
        pnlSubEmprendimiento.add(crearFila("¿Ha requerido financiamiento externo?:", comboFinanciamientoEmprendimiento));

        comboSatisfaccionEmprendimiento = new JComboBox<>(new String[]{"Seleccione...", "Satisfecho", "Algo satisfecho", "Insatisfecho"});
        pnlSubEmprendimiento.add(crearFila("Satisfacción con rendimiento de negocio:", comboSatisfaccionEmprendimiento));

        comboImportanciaFormacion = new JComboBox<>(new String[]{"Seleccione...", "Muy importante", "Importante", "Poco Importante", "No es importante"});
        pnlSubEmprendimiento.add(crearFila("Importancia de formación en Sistemas:", comboImportanciaFormacion));
        pnlLaboral.add(pnlSubEmprendimiento);

        // Subpanel común de Inserción Laboral
        pnlSubInsercionComun = new JPanel();
        pnlSubInsercionComun.setLayout(new BoxLayout(pnlSubInsercionComun, BoxLayout.Y_AXIS));
        pnlSubInsercionComun.setOpaque(false);
        pnlSubInsercionComun.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlSubInsercionComun.setVisible(false);

        pnlSubInsercionComun.add(crearEspacio(8));
        pnlSubInsercionComun.add(crearSeparador());
        pnlSubInsercionComun.add(crearEspacio(8));
        pnlSubInsercionComun.add(crearTituloBloque("Inserción Laboral y Primer Empleo:"));
        pnlSubInsercionComun.add(crearEspacio(6));

        rbPrimerEmpleoSi = new JRadioButton("SI");
        rbPrimerEmpleoNo = new JRadioButton("NO");
        grupoPrimerEmpleo = new ButtonGroup();
        grupoPrimerEmpleo.add(rbPrimerEmpleoSi);
        grupoPrimerEmpleo.add(rbPrimerEmpleoNo);
        JPanel pnlRbPri = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlRbPri.setOpaque(false);
        pnlRbPri.add(rbPrimerEmpleoSi);
        pnlRbPri.add(rbPrimerEmpleoNo);
        pnlSubInsercionComun.add(crearFila("¿El trabajo actual es su primer empleo?:", pnlRbPri));

        comboTiempoConseguirEmpleo = new JComboBox<>(new String[]{"Seleccione...", "Ya trabajaba antes de titularse", "Menos de 1 mes", "Entre 1 - 4 meses", "Entre 4 - 8 meses", "Entre 8 - 12 meses", "Más de 12 meses"});
        pnlSubInsercionComun.add(crearFila("Tiempo en conseguir primer trabajo:", comboTiempoConseguirEmpleo));

        comboCantidadEmpleos = new JComboBox<>(new String[]{"Seleccione...", "1", "2", "3", "Más de 3"});
        pnlSubInsercionComun.add(crearFila("Cantidad de empleos desempeñados:", comboCantidadEmpleos));
        pnlLaboral.add(pnlSubInsercionComun);

        contenedor.add(pnlLaboral);
        contenedor.add(Box.createRigidArea(new Dimension(0, 15)));

        // Eventos Bifurcación Interés en Posgrado
        rbInteresSi.addActionListener(e -> {
            pnlSubInteres.setVisible(true);
            pnlFilaOpinionFcyt.setVisible(true);
            pnlLaboral.setVisible(true);
            refrescarVista();
        });
        rbInteresNo.addActionListener(e -> {
            pnlSubInteres.setVisible(false);
            limpiarSubInteres();
            pnlFilaOpinionFcyt.setVisible(true);
            pnlLaboral.setVisible(true);
            refrescarVista();
        });

        // 4. Formación de Grado y Asignaturas (oculto de inicio hasta definir Situación Laboral)
        pnlFormacion = crearSeccion("4. Evaluación de la Carrera y Asignaturas");
        pnlFormacion.setVisible(false);

        comboSatisfaccionGlobal = new JComboBox<>(new String[]{"Seleccione...", "Totalmente de Acuerdo", "De acuerdo", "En desacuerdo", "Totalmente en desacuerdo"});
        pnlFormacion.add(crearFila("Satisfacción global con formación recibida:", comboSatisfaccionGlobal));
        pnlFormacion.add(crearEspacio(5));

        comboConcordanciaMercado = new JComboBox<>(new String[]{"Seleccione...", "Totalmente de Acuerdo", "De acuerdo", "En desacuerdo", "Totalmente en desacuerdo"});
        pnlFormacion.add(crearFila("Concordancia entre formación y mercado:", comboConcordanciaMercado));

        pnlFormacion.add(crearEspacio(12));
        pnlFormacion.add(crearSeparador());
        pnlFormacion.add(crearEspacio(10));

        pnlFormacion.add(crearTituloBloque("Aspectos de la Carrera que le resultaron de bastante utilidad:"));
        pnlFormacion.add(crearEspacio(6));
        chkUtilidad = new ArrayList<>();
        String[] utilidades = {
                "La aplicación práctica de conceptos teóricos en proyectos reales.",
                "La oportunidad de realizar pasantías o prácticas profesionales.",
                "La distribución de los horarios de clases.",
                "El desarrollo de habilidades analíticas y de resolución de problemas.",
                "La colaboración de equipos multidisciplinarios para resolver problemas complejos.",
                "La enseñanza de herramientas y software específico de la industria."
        };
        JPanel pnlChkUtil = new JPanel(new GridLayout(0, 2, 5, 5));
        pnlChkUtil.setOpaque(false);
        pnlChkUtil.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlChkUtil.setMaximumSize(new Dimension(950, 32767));
        for (String u : utilidades) {
            JCheckBox cb = new JCheckBox(u);
            cb.setOpaque(false);
            chkUtilidad.add(cb);
            pnlChkUtil.add(cb);
        }
        pnlFormacion.add(pnlChkUtil);

        pnlFormacion.add(crearEspacio(12));
        pnlFormacion.add(crearSeparador());
        pnlFormacion.add(crearEspacio(10));

        pnlFormacion.add(crearTituloBloque("Aspectos de la Carrera que considera que pueden mejorarse:"));
        pnlFormacion.add(crearEspacio(6));
        chkMejoras = new ArrayList<>();
        String[] mejoras = {
                "Mayor integración de la teoría con aplicaciones prácticas.",
                "Mayor relacionamiento del estudiante con empresas por pasantías.",
                "Distribución de horarios de clases (virtual / trabajadores).",
                "Actualización constante del plan de estudios según tendencias.",
                "Incremento de actividades extracurriculares.",
                "Ofrecimiento de más opciones de especialización."
        };
        JPanel pnlChkMej = new JPanel(new GridLayout(0, 2, 5, 5));
        pnlChkMej.setOpaque(false);
        pnlChkMej.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlChkMej.setMaximumSize(new Dimension(950, 32767));
        for (String m : mejoras) {
            JCheckBox cb = new JCheckBox(m);
            cb.setOpaque(false);
            chkMejoras.add(cb);
            pnlChkMej.add(cb);
        }
        pnlFormacion.add(pnlChkMej);

        pnlFormacion.add(crearEspacio(12));
        pnlFormacion.add(crearSeparador());
        pnlFormacion.add(crearEspacio(10));

        pnlFormacion.add(crearTituloBloque("Asignaturas que le brindaron ventaja competitiva:"));
        pnlFormacion.add(crearEspacio(6));
        chkVentajas = new ArrayList<>();
        String[] materiasVentaja = {
                "Introducción", "Base de Datos", "Inteligencia Artificial",
                "Redes de Computadora", "Electivas", "T.I.S.", "Básicas (Física, Calculo, Algebras)",
                "Planificación de Proyectos y Gestión", "Tecnologia Redes Avanzadas"
        };
        JPanel pnlChkVent = new JPanel(new GridLayout(0, 3, 5, 5));
        pnlChkVent.setOpaque(false);
        pnlChkVent.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlChkVent.setMaximumSize(new Dimension(950, 32767));
        for (String mv : materiasVentaja) {
            JCheckBox cb = new JCheckBox(mv);
            cb.setOpaque(false);
            chkVentajas.add(cb);
            pnlChkVent.add(cb);
        }
        pnlFormacion.add(pnlChkVent);

        pnlFormacion.add(crearEspacio(12));
        pnlFormacion.add(crearSeparador());
        pnlFormacion.add(crearEspacio(10));

        pnlFormacion.add(crearTituloBloque("Asignaturas que no le resultaron de mucha utilidad:"));
        pnlFormacion.add(crearEspacio(6));
        chkMenorUtilidad = new ArrayList<>();
        String[] materiasMenos = {
                "Básicas (Física, Calculo, Algebras)", "Aplic. Interactivas para TV Digital", "Telefonía IP",
                "Web Semánticas", "Graficación por Computadora", "Robótica",
                "Taller de Programación en Bajo Nivel", "Teoría de Autómatas y Leng. Formales",
                "Programación Funcional", "Teoría de Grafos", "Contabilidad Básica"
        };
        JPanel pnlChkMenos = new JPanel(new GridLayout(0, 3, 5, 5));
        pnlChkMenos.setOpaque(false);
        pnlChkMenos.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlChkMenos.setMaximumSize(new Dimension(950, 32767));
        for (String mm : materiasMenos) {
            JCheckBox cb = new JCheckBox(mm);
            cb.setOpaque(false);
            chkMenorUtilidad.add(cb);
            pnlChkMenos.add(cb);
        }
        pnlFormacion.add(pnlChkMenos);
        contenedor.add(pnlFormacion);
        contenedor.add(Box.createRigidArea(new Dimension(0, 15)));

        // 5. Red de Contactos (oculto de inicio hasta definir Situación Laboral)
        pnlRed = crearSeccion("5. Red de Contactos Oficial");
        pnlRed.setVisible(false);

        rbRedContactosSi = new JRadioButton("SI");
        rbRedContactosNo = new JRadioButton("NO");
        grupoRedContactos = new ButtonGroup();
        grupoRedContactos.add(rbRedContactosSi);
        grupoRedContactos.add(rbRedContactosNo);

        JPanel pnlRbRed = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlRbRed.setOpaque(false);
        pnlRbRed.add(rbRedContactosSi);
        pnlRbRed.add(rbRedContactosNo);
        pnlRed.add(crearFila("¿Desea formar parte de la red de contactos oficial?:", pnlRbRed));

        pnlSubRedContactos = new JPanel();
        pnlSubRedContactos.setLayout(new BoxLayout(pnlSubRedContactos, BoxLayout.Y_AXIS));
        pnlSubRedContactos.setOpaque(false);
        pnlSubRedContactos.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlSubRedContactos.setVisible(false);

        pnlSubRedContactos.add(crearEspacio(8));
        pnlSubRedContactos.add(crearSeparador());
        pnlSubRedContactos.add(crearEspacio(8));
        pnlSubRedContactos.add(crearTituloBloque("Datos de Contacto para la Red:"));
        pnlSubRedContactos.add(crearEspacio(6));

        txtEmail = new JTextField(25);
        txtEmail.setDocument(ValidadorDocumento.email(60));
        pnlSubRedContactos.add(crearFila("Email de contacto:", txtEmail));

        txtTelefono = new JTextField(25);
        txtTelefono.setDocument(ValidadorDocumento.telefono(15));
        pnlSubRedContactos.add(crearFila("Teléfono (con WhatsApp):", txtTelefono));

        txtLinkedin = new JTextField(25);
        txtLinkedin.setDocument(ValidadorDocumento.enlace(120));
        pnlSubRedContactos.add(crearFila("LINK de LinkedIn:", txtLinkedin));

        pnlRed.add(pnlSubRedContactos);
        contenedor.add(pnlRed);
        contenedor.add(Box.createRigidArea(new Dimension(0, 20)));

        rbRedContactosSi.addActionListener(e -> {
            pnlSubRedContactos.setVisible(true);
            refrescarVista();
        });
        rbRedContactosNo.addActionListener(e -> {
            pnlSubRedContactos.setVisible(false);
            limpiarSubRedContactos();
            refrescarVista();
        });

        // Botón Enviar (oculto de inicio)
        btnEnviar = new JButton("Registrar Respuesta de Titulado");
        btnEnviar.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnEnviar.setBackground(ConfiguracionColores.COLOR_PRIMARIO);
        btnEnviar.setForeground(Color.WHITE);
        btnEnviar.setFocusPainted(false);
        btnEnviar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEnviar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEnviar.setPreferredSize(new Dimension(320, 45));
        btnEnviar.setMaximumSize(new Dimension(320, 45));
        btnEnviar.setVisible(false);

        btnEnviar.addActionListener(e -> {
            btnEnviar.setEnabled(false);
            btnEnviar.setText("Registrando en la nube (Supabase)...");
            java.util.Map<String, String> datos = recolectarDatos();

            new Thread(() -> {
                boolean exito = Modelo.ServicioSupabase.insertarRegistro("encuestas_titulados", datos);
                SwingUtilities.invokeLater(() -> {
                    btnEnviar.setEnabled(true);
                    btnEnviar.setText("Registrar Respuesta de Titulado");
                    if (exito) {
                        JOptionPane.showMessageDialog(this,
                                "¡Respuesta registrada exitosamente en Supabase!\nGracias por aportar a la acreditación de la Carrera.",
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

        // Evento Bifurcación Situación Laboral
        comboSituacionLaboral.addActionListener(e -> {
            int sel = comboSituacionLaboral.getSelectedIndex();
            // 0: Seleccione..., 1: Organización, 2: NO trabajo, 3: Emprendimiento

            pnlSubOrganizacion.setVisible(sel == 1);
            pnlSubDesempleado.setVisible(sel == 2);
            pnlSubEmprendimiento.setVisible(sel == 3);

            if (sel == 1 || sel == 3) {
                pnlSubInsercionComun.setVisible(true);
            } else if (sel == 2) {
                pnlSubInsercionComun.setVisible(rbTrabajoAntesSi.isSelected());
            } else {
                pnlSubInsercionComun.setVisible(false);
            }

            // Mostrar el resto del formulario solo si se eligió una opción válida
            boolean seleccionadoValido = (sel > 0);
            pnlFormacion.setVisible(seleccionadoValido);
            pnlRed.setVisible(seleccionadoValido);
            btnEnviar.setVisible(seleccionadoValido);

            // Limpieza estricta de datos fantasma de las ramas no activas
            if (sel != 1) limpiarSubOrganizacion();
            if (sel != 2) limpiarSubDesempleado();
            if (sel != 3) limpiarSubEmprendimiento();
            if (sel == 0 || (sel == 2 && !rbTrabajoAntesSi.isSelected())) limpiarSubInsercion();

            refrescarVista();
        });

        JScrollPane scroll = new JScrollPane(contenedor);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    private void refrescarVista() {
        contenedor.revalidate();
        contenedor.repaint();
    }

    private void limpiarSubPosgrado() {
        comboNivelPosgrado.setSelectedIndex(0);
        comboLugarPosgrado.setSelectedIndex(0);
        comboFinanciamientoPosgrado.setSelectedIndex(0);
        txtNombrePrograma.setText("");
    }

    private void limpiarSubInteres() {
        comboAreaInteres.setSelectedIndex(0);
        comboInstInteres.setSelectedIndex(0);
    }

    private void limpiarSubOrganizacion() {
        txtNombreEmpresa.setText("");
        comboRubroOrg.setSelectedIndex(0);
        txtOtroRubroOrg.setText("");
        comboSectorOrg.setSelectedIndex(0);
        comboDepartamento.setSelectedIndex(0);
        comboAreaTrabajo.setSelectedIndex(0);
        txtOtraAreaTrabajo.setText("");
        comboCargo.setSelectedIndex(0);
        txtOtroCargo.setText("");
        comboPertinenciaLaboral.setSelectedIndex(0);
        comboAntiguedadTrabajo.setSelectedIndex(0);
        comboMedioObtencion.setSelectedIndex(0);
        comboRemuneracion.setSelectedIndex(0);
    }

    private void limpiarSubDesempleado() {
        comboRazonDesempleo.setSelectedIndex(0);
        grupoTrabajoAntes.clearSelection();
    }

    private void limpiarSubEmprendimiento() {
        comboOrigenEmprendimiento.setSelectedIndex(0);
        comboEntregableEmprendimiento.setSelectedIndex(0);
        comboFinanciamientoEmprendimiento.setSelectedIndex(0);
        comboSatisfaccionEmprendimiento.setSelectedIndex(0);
        comboImportanciaFormacion.setSelectedIndex(0);
    }

    private void limpiarSubInsercion() {
        grupoPrimerEmpleo.clearSelection();
        comboTiempoConseguirEmpleo.setSelectedIndex(0);
        comboCantidadEmpleos.setSelectedIndex(0);
    }

    private void limpiarSubRedContactos() {
        txtEmail.setText("");
        txtTelefono.setText("");
        txtLinkedin.setText("");
    }

    public void limpiarFormulario() {
        comboEdad.setSelectedIndex(0);
        comboGenero.setSelectedIndex(0);

        grupoPosgrado.clearSelection();
        pnlSubPosgrado.setVisible(false);
        limpiarSubPosgrado();

        pnlInteres.setVisible(false);
        grupoInteres.clearSelection();
        pnlSubInteres.setVisible(false);
        limpiarSubInteres();
        pnlFilaOpinionFcyt.setVisible(false);
        comboOpinionFcyt.setSelectedIndex(0);

        pnlLaboral.setVisible(false);
        txtAniosProfesional.setText("");
        txtTiempoDesempleo.setText("");
        comboSituacionLaboral.setSelectedIndex(0);
        pnlSubOrganizacion.setVisible(false);
        limpiarSubOrganizacion();
        pnlSubDesempleado.setVisible(false);
        limpiarSubDesempleado();
        pnlSubEmprendimiento.setVisible(false);
        limpiarSubEmprendimiento();
        pnlSubInsercionComun.setVisible(false);
        limpiarSubInsercion();

        pnlFormacion.setVisible(false);
        comboSatisfaccionGlobal.setSelectedIndex(0);
        comboConcordanciaMercado.setSelectedIndex(0);

        for (JCheckBox cb : chkUtilidad) cb.setSelected(false);
        for (JCheckBox cb : chkMejoras) cb.setSelected(false);
        for (JCheckBox cb : chkVentajas) cb.setSelected(false);
        for (JCheckBox cb : chkMenorUtilidad) cb.setSelected(false);

        pnlRed.setVisible(false);
        grupoRedContactos.clearSelection();
        pnlSubRedContactos.setVisible(false);
        limpiarSubRedContactos();

        btnEnviar.setVisible(false);

        refrescarVista();
    }

    private java.util.Map<String, String> recolectarDatos() {
        java.util.Map<String, String> m = new java.util.HashMap<>();
        m.put("edad", comboEdad.getSelectedIndex() > 0 ? (String) comboEdad.getSelectedItem() : "");
        m.put("genero", comboGenero.getSelectedIndex() > 0 ? (String) comboGenero.getSelectedItem() : "");

        boolean tienePos = rbPosgradoSi.isSelected();
        m.put("realizo_posgrado", tienePos ? "SI" : (rbPosgradoNo.isSelected() ? "NO" : ""));
        m.put("nivel_posgrado", (tienePos && comboNivelPosgrado.getSelectedIndex() > 0) ? (String) comboNivelPosgrado.getSelectedItem() : "");
        m.put("lugar_posgrado", (tienePos && comboLugarPosgrado.getSelectedIndex() > 0) ? (String) comboLugarPosgrado.getSelectedItem() : "");
        m.put("financiamiento_posgrado", (tienePos && comboFinanciamientoPosgrado.getSelectedIndex() > 0) ? (String) comboFinanciamientoPosgrado.getSelectedItem() : "");
        m.put("nombre_programa", tienePos ? txtNombrePrograma.getText().trim() : "");

        boolean tieneInt = rbInteresSi.isSelected();
        m.put("interes_posgrado", tieneInt ? "SI" : (rbInteresNo.isSelected() ? "NO" : ""));
        m.put("area_interes", (tieneInt && comboAreaInteres.getSelectedIndex() > 0) ? (String) comboAreaInteres.getSelectedItem() : "");
        m.put("institucion_interes", (tieneInt && comboInstInteres.getSelectedIndex() > 0) ? (String) comboInstInteres.getSelectedItem() : "");
        m.put("opinion_fcyt", comboOpinionFcyt.getSelectedIndex() > 0 ? (String) comboOpinionFcyt.getSelectedItem() : "");

        m.put("anios_profesional", txtAniosProfesional.getText().trim());
        m.put("tiempo_desempleo", txtTiempoDesempleo.getText().trim());

        int selLab = comboSituacionLaboral.getSelectedIndex();
        m.put("situacion_laboral", selLab > 0 ? (String) comboSituacionLaboral.getSelectedItem() : "");

        if (selLab == 1) {
            m.put("nombre_empresa", txtNombreEmpresa.getText().trim());
            m.put("rubro_org", comboRubroOrg.getSelectedIndex() > 0 ? (String) comboRubroOrg.getSelectedItem() : "");
            m.put("otro_rubro_org", txtOtroRubroOrg.getText().trim());
            m.put("sector_org", comboSectorOrg.getSelectedIndex() > 0 ? (String) comboSectorOrg.getSelectedItem() : "");
            m.put("departamento", comboDepartamento.getSelectedIndex() > 0 ? (String) comboDepartamento.getSelectedItem() : "");
            m.put("area_trabajo", comboAreaTrabajo.getSelectedIndex() > 0 ? (String) comboAreaTrabajo.getSelectedItem() : "");
            m.put("otra_area_trabajo", txtOtraAreaTrabajo.getText().trim());
            m.put("cargo", comboCargo.getSelectedIndex() > 0 ? (String) comboCargo.getSelectedItem() : "");
            m.put("otro_cargo", txtOtroCargo.getText().trim());
            m.put("pertinencia_laboral", comboPertinenciaLaboral.getSelectedIndex() > 0 ? (String) comboPertinenciaLaboral.getSelectedItem() : "");
            m.put("antiguedad_trabajo", comboAntiguedadTrabajo.getSelectedIndex() > 0 ? (String) comboAntiguedadTrabajo.getSelectedItem() : "");
            m.put("medio_obtencion", comboMedioObtencion.getSelectedIndex() > 0 ? (String) comboMedioObtencion.getSelectedItem() : "");
            m.put("remuneracion", comboRemuneracion.getSelectedIndex() > 0 ? (String) comboRemuneracion.getSelectedItem() : "");

            m.put("primer_empleo", rbPrimerEmpleoSi.isSelected() ? "SI" : (rbPrimerEmpleoNo.isSelected() ? "NO" : ""));
            m.put("tiempo_primer_empleo", comboTiempoConseguirEmpleo.getSelectedIndex() > 0 ? (String) comboTiempoConseguirEmpleo.getSelectedItem() : "");
            m.put("cantidad_empleos", comboCantidadEmpleos.getSelectedIndex() > 0 ? (String) comboCantidadEmpleos.getSelectedItem() : "");
        } else if (selLab == 2) {
            m.put("razon_desempleo", comboRazonDesempleo.getSelectedIndex() > 0 ? (String) comboRazonDesempleo.getSelectedItem() : "");
            m.put("trabajo_antes", rbTrabajoAntesSi.isSelected() ? "SI" : (rbTrabajoAntesNo.isSelected() ? "NO" : ""));
        } else if (selLab == 3) {
            m.put("origen_emprendimiento", comboOrigenEmprendimiento.getSelectedIndex() > 0 ? (String) comboOrigenEmprendimiento.getSelectedItem() : "");
            m.put("entregable_negocio", comboEntregableEmprendimiento.getSelectedIndex() > 0 ? (String) comboEntregableEmprendimiento.getSelectedItem() : "");
            m.put("financiamiento_negocio", comboFinanciamientoEmprendimiento.getSelectedIndex() > 0 ? (String) comboFinanciamientoEmprendimiento.getSelectedItem() : "");
            m.put("satisfaccion_negocio", comboSatisfaccionEmprendimiento.getSelectedIndex() > 0 ? (String) comboSatisfaccionEmprendimiento.getSelectedItem() : "");
            m.put("importancia_formacion", comboImportanciaFormacion.getSelectedIndex() > 0 ? (String) comboImportanciaFormacion.getSelectedItem() : "");

            m.put("primer_empleo", rbPrimerEmpleoSi.isSelected() ? "SI" : (rbPrimerEmpleoNo.isSelected() ? "NO" : ""));
            m.put("tiempo_primer_empleo", comboTiempoConseguirEmpleo.getSelectedIndex() > 0 ? (String) comboTiempoConseguirEmpleo.getSelectedItem() : "");
            m.put("cantidad_empleos", comboCantidadEmpleos.getSelectedIndex() > 0 ? (String) comboCantidadEmpleos.getSelectedItem() : "");
        }

        m.put("satisfaccion_global", comboSatisfaccionGlobal.getSelectedIndex() > 0 ? (String) comboSatisfaccionGlobal.getSelectedItem() : "");
        m.put("concordancia_mercado", comboConcordanciaMercado.getSelectedIndex() > 0 ? (String) comboConcordanciaMercado.getSelectedItem() : "");

        m.put("aspectos_utiles", unirSeleccionados(chkUtilidad));
        m.put("aspectos_mejorar", unirSeleccionados(chkMejoras));
        m.put("materias_ventaja", unirSeleccionados(chkVentajas));
        m.put("materias_menor_utilidad", unirSeleccionados(chkMenorUtilidad));

        boolean tieneRed = rbRedContactosSi.isSelected();
        m.put("red_contactos", tieneRed ? "SI" : (rbRedContactosNo.isSelected() ? "NO" : ""));
        m.put("email", tieneRed ? txtEmail.getText().trim() : "");
        m.put("telefono", tieneRed ? txtTelefono.getText().trim() : "");
        m.put("linkedin", tieneRed ? txtLinkedin.getText().trim() : "");

        return m;
    }

    private String unirSeleccionados(ArrayList<JCheckBox> lista) {
        StringBuilder sb = new StringBuilder();
        for (JCheckBox cb : lista) {
            if (cb.isSelected()) {
                if (sb.length() > 0) sb.append("; ");
                sb.append(cb.getText());
            }
        }
        return sb.toString();
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
