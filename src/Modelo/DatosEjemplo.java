package Modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class DatosEjemplo {

    public static void cargarDatos(GestorDatos gestor) {
        Random r = new Random();

        // 1. Datos de Titulados con bifurcaciones realistas
        ArrayList<HashMap<String, String>> titulados = new ArrayList<>();
        String[] colsTit = {
                "Edad", "Genero", "RealizoPosgrado", "NivelPosgrado", "LugarPosgrado",
                "FinanciamientoPosgrado", "InteresPosgrado", "AreaInteres", "InstitucionInteres",
                "OpinionFCyT", "AniosProfesional", "TiempoDesempleo", "SituacionLaboral",
                "NombreEmpresa", "RubroOrg", "SectorOrg", "Departamento", "AreaTrabajo",
                "Cargo", "PertinenciaLaboral", "AntiguedadTrabajo", "MedioObtencion",
                "Remuneracion", "RazonDesempleo", "TrabajoAntes", "OrigenEmprendimiento",
                "EntregableNegocio", "FinanciamientoNegocio", "SatisfaccionNegocio",
                "PrimerEmpleo", "TiempoPrimerEmpleo", "CantidadEmpleos",
                "SatisfaccionGlobal", "ConcordanciaMercado"
        };

        String[] edades = {"23 - 26 años", "27 - 30 años", "31 - 34 años", "35 años o más"};
        String[] nivelesPosgrado = {"DIPLOMADO", "ESPECIALIDAD", "MAESTRIA", "DOCTORADO"};
        String[] lugaresPosgrado = {"DIRECCIÓN DE POSGRADO FCyT", "ESCUELA UNIVERSITARIA DE POSGRADO (EUPG)", "UNIVERSIDADES PRIVADAS NACIONALES", "UNIVERSIDADES INTERNACIONALES"};
        String[] financiamientos = {"FINANCIADO CON RECURSOS PROPIOS", "TOTAL FINANCIAMIENTO POR MEDIO DE UNA BECA", "FINANCIADO PARCIALMENTE POR MEDIO DE UNA BECA"};
        String[] areasInteres = {"IA", "Robótica", "Ingenieria de Software", "Base de Datos", "Redes de Datos y Seguridad"};
        String[] opinionesLikert = {"Totalmente de Acuerdo", "De acuerdo", "En desacuerdo"};

        String[] empresas = {"Jalasoft", "AssureSoft", "Banco Unión", "Entel", "Agetic", "Truenorth", "Mojix"};
        String[] rubrosOrg = {"Desarrollo de Software", "Telecomunicaciones", "Banca", "Educación", "Salud"};
        String[] departamentos = {"COCHABAMBA", "LA PAZ", "SANTA CRUZ"};
        String[] cargos = {"Desarrollo", "Técnico", "Help Desk", "Ejecutivo"};
        String[] antiguedades = {"Menos de 1 año", "Entre 1 a 2 años", "Entre 2 a 5 años", "Entre 5 a 8 años"};
        String[] medios = {"INTERNET Y REDES SOCIALES", "POSTULACIÓN COMPETITIVA", "RECOMENDACIÓN DE TERCEROS"};
        String[] sueldos = {"Entre Bs. 2.500 - Bs. 3.500", "Entre Bs. 3.501 - Bs. 5.000", "Entre Bs. 5.001 - Bs. 7.000", "Entre Bs. 7.001 - Bs. 10.000", "Más de Bs. 10.000"};

        String[] razonesDesempleo = {
                "Incumplimiento de requisitos de experiencia previa.",
                "Demanda laboral inexistente.",
                "Ofertas laborales con remuneración muy bajas.",
                "Problemas de presentación del CV."
        };

        String[] origenesEmpr = {"Idea propia.", "Idea conjunta con compañeros.", "StartUp"};
        String[] entregables = {"Producto", "Servicio", "Ambos"};
        String[] finEmpr = {"No, financiamiento propio.", "Si, mediante préstamos bancarios."};
        String[] tiemposPrimerEmpleo = {"Ya trabajaba antes de titularse", "Menos de 1 mes", "Entre 1 - 4 meses", "Entre 4 - 8 meses"};

        for (int i = 0; i < 150; i++) {
            HashMap<String, String> f = new HashMap<>();
            f.put("Edad", edades[r.nextInt(edades.length)]);
            f.put("Genero", r.nextBoolean() ? "Hombre" : "Mujer");

            // Bifurcación 1: Posgrado previo (40% sí, 60% no)
            boolean tienePosgrado = (r.nextInt(100) < 40);
            f.put("RealizoPosgrado", tienePosgrado ? "SI" : "NO");
            if (tienePosgrado) {
                f.put("NivelPosgrado", nivelesPosgrado[r.nextInt(nivelesPosgrado.length)]);
                f.put("LugarPosgrado", lugaresPosgrado[r.nextInt(lugaresPosgrado.length)]);
                f.put("FinanciamientoPosgrado", financiamientos[r.nextInt(financiamientos.length)]);
            } else {
                f.put("NivelPosgrado", "No aplica");
                f.put("LugarPosgrado", "No aplica");
                f.put("FinanciamientoPosgrado", "No aplica");
            }

            // Bifurcación 2: Interés futuro en posgrado (75% sí, 25% no)
            boolean tieneInteres = (r.nextInt(100) < 75);
            f.put("InteresPosgrado", tieneInteres ? "SI" : "NO");
            if (tieneInteres) {
                f.put("AreaInteres", areasInteres[r.nextInt(areasInteres.length)]);
                f.put("InstitucionInteres", lugaresPosgrado[r.nextInt(lugaresPosgrado.length)]);
            } else {
                f.put("AreaInteres", "No aplica");
                f.put("InstitucionInteres", "No aplica");
            }
            f.put("OpinionFCyT", opinionesLikert[r.nextInt(opinionesLikert.length)]);

            int anios = 1 + r.nextInt(10);
            f.put("AniosProfesional", String.valueOf(anios));
            f.put("TiempoDesempleo", String.valueOf(r.nextInt(Math.max(1, anios * 6))) + " meses");

            // Bifurcación 3: Situación Laboral (65% Organización, 15% Desempleado, 20% Emprendimiento)
            int tipoLab = r.nextInt(100);
            if (tipoLab < 65) {
                // Rama Organización
                f.put("SituacionLaboral", "Actualmente me encuentro trabajando un una organización");
                f.put("NombreEmpresa", empresas[r.nextInt(empresas.length)]);
                f.put("RubroOrg", rubrosOrg[r.nextInt(rubrosOrg.length)]);
                f.put("SectorOrg", r.nextBoolean() ? "PRIVADO" : "PÚBLICO");
                f.put("Departamento", departamentos[r.nextInt(departamentos.length)]);
                f.put("AreaTrabajo", "Desarrollo");
                f.put("Cargo", cargos[r.nextInt(cargos.length)]);
                f.put("PertinenciaLaboral", opinionesLikert[r.nextInt(opinionesLikert.length)]);
                f.put("AntiguedadTrabajo", antiguedades[r.nextInt(antiguedades.length)]);
                f.put("MedioObtencion", medios[r.nextInt(medios.length)]);
                f.put("Remuneracion", sueldos[r.nextInt(sueldos.length)]);

                f.put("PrimerEmpleo", r.nextBoolean() ? "SI" : "NO");
                f.put("TiempoPrimerEmpleo", tiemposPrimerEmpleo[r.nextInt(tiemposPrimerEmpleo.length)]);
                f.put("CantidadEmpleos", String.valueOf(1 + r.nextInt(3)));

                f.put("RazonDesempleo", "No aplica");
                f.put("TrabajoAntes", "No aplica");
                f.put("OrigenEmprendimiento", "No aplica");
                f.put("EntregableNegocio", "No aplica");
                f.put("FinanciamientoNegocio", "No aplica");
                f.put("SatisfaccionNegocio", "No aplica");
            } else if (tipoLab < 80) {
                // Rama Desempleado
                f.put("SituacionLaboral", "Actualmente NO trabajo");
                f.put("RazonDesempleo", razonesDesempleo[r.nextInt(razonesDesempleo.length)]);
                f.put("TrabajoAntes", r.nextBoolean() ? "SI" : "NO");

                f.put("NombreEmpresa", "No aplica");
                f.put("RubroOrg", "No aplica");
                f.put("SectorOrg", "No aplica");
                f.put("Departamento", "No aplica");
                f.put("AreaTrabajo", "No aplica");
                f.put("Cargo", "No aplica");
                f.put("PertinenciaLaboral", "No aplica");
                f.put("AntiguedadTrabajo", "No aplica");
                f.put("MedioObtencion", "No aplica");
                f.put("Remuneracion", "No aplica");
                f.put("PrimerEmpleo", "No aplica");
                f.put("TiempoPrimerEmpleo", "No aplica");
                f.put("CantidadEmpleos", "0");
                f.put("OrigenEmprendimiento", "No aplica");
                f.put("EntregableNegocio", "No aplica");
                f.put("FinanciamientoNegocio", "No aplica");
                f.put("SatisfaccionNegocio", "No aplica");
            } else {
                // Rama Emprendimiento
                f.put("SituacionLaboral", "Actualmente dedico mi tiempo COMPLETO a un emprendimiento propio");
                f.put("OrigenEmprendimiento", origenesEmpr[r.nextInt(origenesEmpr.length)]);
                f.put("EntregableNegocio", entregables[r.nextInt(entregables.length)]);
                f.put("FinanciamientoNegocio", finEmpr[r.nextInt(finEmpr.length)]);
                f.put("SatisfaccionNegocio", r.nextBoolean() ? "Satisfecho" : "Algo satisfecho");

                f.put("PrimerEmpleo", "NO");
                f.put("TiempoPrimerEmpleo", "Ya trabajaba antes de titularse");
                f.put("CantidadEmpleos", "2");

                f.put("NombreEmpresa", "No aplica");
                f.put("RubroOrg", "No aplica");
                f.put("SectorOrg", "No aplica");
                f.put("Departamento", "No aplica");
                f.put("AreaTrabajo", "No aplica");
                f.put("Cargo", "No aplica");
                f.put("PertinenciaLaboral", "No aplica");
                f.put("AntiguedadTrabajo", "No aplica");
                f.put("MedioObtencion", "No aplica");
                f.put("Remuneracion", "No aplica");
                f.put("RazonDesempleo", "No aplica");
                f.put("TrabajoAntes", "No aplica");
            }

            f.put("SatisfaccionGlobal", opinionesLikert[r.nextInt(opinionesLikert.length)]);
            f.put("ConcordanciaMercado", opinionesLikert[r.nextInt(opinionesLikert.length)]);

            titulados.add(f);
        }

        // 2. Datos de Empleadores
        ArrayList<HashMap<String, String>> empleadores = new ArrayList<>();
        String[] colsEmp = {
                "NombreEmpresa", "TipoOrganizacion", "Tamanio", "Rubro",
                "ContratoSistemas", "ProyeccionContratacion", "NivelAcademicoDemandado",
                "MedioConvocatoria",
                "ConfianzaFormadora", "TituloConsistente", "ConsultaEmpleadores",
                "ConocePerfil", "PerfilCoherente", "DesempenoIntegral",
                "ConsideraPasantias", "CompetenciasLaborales", "ValoresEticos",
                "ProcesosEvaluacion", "VinculosOportunidades", "VinculacionTecnologica",
                "DifusionPasantias", "IntercambioConocimientos", "RetroalimentaPlanEstudios",
                "PertinenciaFormacion", "BusquedaTalentoUMSS", "AutoevaluacionPeriodica"
        };
        String[] tamanios = {"Grande (100 funcionarios o más)", "Mediana (entre 31 a 99 funcionarios)", "Pequeña (entre 11 a 30 funcionarios)", "Micro (hasta 10 funcionarios)"};
        String[] nivelesDem = {"Licenciatura", "Diplomado", "Maestría"};
        String[] mediosConv = {"Internet y Redes sociales", "Postulación competitiva (convocatorias)", "Recomendación de terceros"};
        String[] proyecciones = {
                "Sí, existe una alta probabilidad de contratación",
                "Sí, dependiendo del crecimiento o nuevos proyectos",
                "No se tiene prevista la contratación actualmente"
        };
        String[] opcionesLikertEmp = {"Totalmente de acuerdo", "Parcialmente de acuerdo", "Totalmente desacuerdo", "No sabe"};
        String[] colsLikertEmp = {
                "ConfianzaFormadora", "TituloConsistente", "ConsultaEmpleadores",
                "ConocePerfil", "PerfilCoherente", "DesempenoIntegral",
                "ConsideraPasantias", "CompetenciasLaborales", "ValoresEticos",
                "ProcesosEvaluacion", "VinculosOportunidades", "VinculacionTecnologica",
                "DifusionPasantias", "IntercambioConocimientos", "RetroalimentaPlanEstudios",
                "PertinenciaFormacion", "BusquedaTalentoUMSS", "AutoevaluacionPeriodica"
        };

        for (int i = 0; i < 50; i++) {
            HashMap<String, String> f = new HashMap<>();
            f.put("NombreEmpresa", empresas[r.nextInt(empresas.length)]);
            f.put("TipoOrganizacion", r.nextBoolean() ? "Privado" : "Público");
            f.put("Tamanio", tamanios[r.nextInt(tamanios.length)]);
            f.put("Rubro", rubrosOrg[r.nextInt(rubrosOrg.length)]);
            f.put("ContratoSistemas", r.nextBoolean() ? "SI" : "No");
            f.put("ProyeccionContratacion", proyecciones[r.nextInt(proyecciones.length)]);
            f.put("NivelAcademicoDemandado", nivelesDem[r.nextInt(nivelesDem.length)]);
            f.put("MedioConvocatoria", mediosConv[r.nextInt(mediosConv.length)]);
            for (String colLikert : colsLikertEmp) {
                f.put(colLikert, opcionesLikertEmp[r.nextInt(opcionesLikertEmp.length)]);
            }
            empleadores.add(f);
        }

        gestor.cargarDatosEjemplo(titulados, colsTit, empleadores, colsEmp);
    }
}
