package Controlador;

import Modelo.ChiCuadrada;
import Modelo.DatosEjemplo;
import Modelo.Estadistica;
import Modelo.GestorDatos;
import Vista.PanelAnalisis;
import Vista.VentanaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Controlador implements ActionListener {
    private VentanaPrincipal vista;
    private GestorDatos modelo;

    public Controlador(VentanaPrincipal vista, GestorDatos modelo) {
        this.vista = vista;
        this.modelo = modelo;

        this.vista.panelCarga.btnCargarTitulados.addActionListener(this);
        this.vista.panelCarga.btnCargarEmpleadores.addActionListener(this);
        this.vista.panelCarga.btnDatosPrueba.addActionListener(this);
        this.vista.panelCarga.btnCargarNube.addActionListener(this);

        this.vista.panelTitulados.comboVariables.addActionListener(e -> actualizarAnalisisUnivariante(true));
        this.vista.panelEmpleadores.comboVariables.addActionListener(e -> actualizarAnalisisUnivariante(false));
        
        this.vista.panelCruces.btnCalcular.addActionListener(e -> calcularChiCuadrada());
    }

    public void iniciar() {
        vista.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.panelCarga.btnCargarTitulados) {
            cargarArchivo(true);
        } else if (e.getSource() == vista.panelCarga.btnCargarEmpleadores) {
            cargarArchivo(false);
        } else if (e.getSource() == vista.panelCarga.btnDatosPrueba) {
            DatosEjemplo.cargarDatos(modelo);
            vista.panelCarga.lblEstadoTitulados.setText("Titulados: Simulado (" + modelo.getCantidadTitulados() + " reg)");
            vista.panelCarga.lblEstadoEmpleadores.setText("Empleadores: Simulado (" + modelo.getCantidadEmpleadores() + " reg)");
            habilitarAnalisis();
            JOptionPane.showMessageDialog(vista, "Datos de prueba (con bifurcaciones coherentes) cargados correctamente.");
        } else if (e.getSource() == vista.panelCarga.btnCargarNube) {
            cargarDesdeNube();
        }
    }

    private void cargarDesdeNube() {
        vista.panelCarga.btnCargarNube.setEnabled(false);
        vista.panelCarga.btnCargarNube.setText("Descargando de Supabase...");
        new Thread(() -> {
            boolean okTit = modelo.cargarDesdeSupabase(true);
            boolean okEmp = modelo.cargarDesdeSupabase(false);
            SwingUtilities.invokeLater(() -> {
                vista.panelCarga.btnCargarNube.setEnabled(true);
                vista.panelCarga.btnCargarNube.setText("Descargar Respuestas de la Nube (Supabase)");
                if (okTit || okEmp) {
                    if (okTit) {
                        vista.panelCarga.lblEstadoTitulados.setText("Titulados: Nube (" + modelo.getCantidadTitulados() + " reg)");
                    } else {
                        vista.panelCarga.lblEstadoTitulados.setText("Titulados: 0 en Nube");
                    }
                    if (okEmp) {
                        vista.panelCarga.lblEstadoEmpleadores.setText("Empleadores: Nube (" + modelo.getCantidadEmpleadores() + " reg)");
                    } else {
                        vista.panelCarga.lblEstadoEmpleadores.setText("Empleadores: 0 en Nube");
                    }
                    habilitarAnalisis();
                    int cantTit = okTit ? modelo.getCantidadTitulados() : 0;
                    int cantEmp = okEmp ? modelo.getCantidadEmpleadores() : 0;
                    JOptionPane.showMessageDialog(vista, "Datos descargados exitosamente desde Supabase.\n" +
                            "Titulados: " + cantTit + " registros\n" +
                            "Empleadores: " + cantEmp + " registros");
                } else {
                    JOptionPane.showMessageDialog(vista, "Aún no hay registros en la base de datos o falló la conexión.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            });
        }).start();
    }

    private void cargarArchivo(boolean esTitulados) {
        JFileChooser chooser = new JFileChooser();
        int seleccion = chooser.showOpenDialog(vista);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivo = chooser.getSelectedFile();
            boolean exito = modelo.cargarCSV(archivo, esTitulados);
            if (exito) {
                if (esTitulados) {
                    vista.panelCarga.lblEstadoTitulados.setText("Titulados: " + archivo.getName() + " (" + modelo.getCantidadTitulados() + " reg)");
                } else {
                    vista.panelCarga.lblEstadoEmpleadores.setText("Empleadores: " + archivo.getName() + " (" + modelo.getCantidadEmpleadores() + " reg)");
                }
                habilitarAnalisis();
                JOptionPane.showMessageDialog(vista, "Archivo cargado correctamente.");
            } else {
                JOptionPane.showMessageDialog(vista, "Error al cargar el archivo. Verifique el formato CSV.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void habilitarAnalisis() {
        boolean hayTit = modelo.hayDatosTitulados();
        boolean hayEmp = modelo.hayDatosEmpleadores();

        vista.pestanas.setEnabledAt(3, hayTit);
        vista.pestanas.setEnabledAt(5, hayTit);
        if (hayTit) {
            vista.panelTitulados.actualizarVariables(modelo.getColumnasTitulados());
            vista.panelCruces.actualizarVariables(modelo.getColumnasTitulados());
        }

        vista.pestanas.setEnabledAt(4, hayEmp);
        if (hayEmp) {
            vista.panelEmpleadores.actualizarVariables(modelo.getColumnasEmpleadores());
        }
    }

    private void actualizarAnalisisUnivariante(boolean esTitulados) {
        PanelAnalisis panel = esTitulados ? vista.panelTitulados : vista.panelEmpleadores;
        String variableSeleccionada = (String) panel.comboVariables.getSelectedItem();
        
        if (variableSeleccionada != null) {
            ArrayList<String> datosColumna = modelo.obtenerColumna(esTitulados, variableSeleccionada);
            LinkedHashMap<String, Integer> frecuencias = Estadistica.frecuenciaAbsoluta(datosColumna);
            
            panel.modeloTabla.setRowCount(0);
            int sumaFrecuencias = 0;
            for (int val : frecuencias.values()) sumaFrecuencias += val;

            for (Map.Entry<String, Integer> entry : frecuencias.entrySet()) {
                double porcentaje = sumaFrecuencias > 0 ? (entry.getValue() * 100.0 / sumaFrecuencias) : 0;
                panel.modeloTabla.addRow(new Object[]{entry.getKey(), entry.getValue(), String.format("%.2f", porcentaje)});
            }
            
            panel.graficoBarras.setDatos(frecuencias, "Distribución de " + variableSeleccionada);
            panel.graficoPastel.setDatos(frecuencias, "Distribución de " + variableSeleccionada);
        }
    }

    private void calcularChiCuadrada() {
        String var1 = (String) vista.panelCruces.comboVar1.getSelectedItem();
        String var2 = (String) vista.panelCruces.comboVar2.getSelectedItem();

        if (var1 != null && var2 != null && !var1.equals(var2)) {
            ArrayList<String> col1 = modelo.obtenerColumnaCompleta(true, var1);
            ArrayList<String> col2 = modelo.obtenerColumnaCompleta(true, var2);

            Object[] resultados = ChiCuadrada.calcularChiCuadrada(col1, col2);
            Object[][] matriz = (Object[][]) resultados[0];
            
            if (matriz != null) {
                vista.panelCruces.modeloTabla.setRowCount(0);
                vista.panelCruces.modeloTabla.setColumnCount(0);
                
                for (int j = 0; j < matriz[0].length; j++) {
                    vista.panelCruces.modeloTabla.addColumn(matriz[0][j]);
                }
                
                for (int i = 1; i < matriz.length; i++) {
                    vista.panelCruces.modeloTabla.addRow(matriz[i]);
                }
                
                vista.panelCruces.lblResultadoChi.setText((String) resultados[3]);
            } else {
                vista.panelCruces.lblResultadoChi.setText("Error al calcular Chi-Cuadrada");
            }
        } else {
            JOptionPane.showMessageDialog(vista, "Seleccione dos variables distintas para el cruce.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GestorDatos modelo = new GestorDatos();
            VentanaPrincipal vista = new VentanaPrincipal();
            Controlador controlador = new Controlador(vista, modelo);
            controlador.iniciar();
        });
    }
}
