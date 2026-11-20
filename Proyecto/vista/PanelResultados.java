package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import logica.SistemaControlEscolar;
import modelo.Alumno;
import modelo.Calificacion;

// Pestaña de resultados: promedio, estado y materias cursadas de cada alumno.
// Aquí solo se consulta; no se modifica nada.
public class PanelResultados extends JPanel implements ActionListener, ListSelectionListener {

    private SistemaControlEscolar sistema;

    private JComboBox<String> cmbFiltro;
    private JButton btnActualizar;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextArea areaDetalle;
    private JLabel lblResumen;

    public PanelResultados(SistemaControlEscolar sistema) {
        this.sistema = sistema;
        setLayout(new BorderLayout(10, 10));
        setBackground(Estilo.COLOR_FONDO);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        crearComponentes();
        actualizar();
    }

    private void crearComponentes() {
        // ----- Arriba: título y filtro -----
        JPanel arriba = new JPanel(new GridLayout(2, 1, 5, 5));
        arriba.setOpaque(false);
        arriba.add(Estilo.crearTitulo("Consulta de resultados"));

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        filtro.setOpaque(false);
        String[] opciones = {"Todos", "Aprobados", "Reprobados"};
        cmbFiltro = new JComboBox<String>(opciones);
        btnActualizar = Estilo.crearBoton("Actualizar", Estilo.COLOR_AZUL);
        filtro.add(new JLabel("Mostrar:"));
        filtro.add(cmbFiltro);
        filtro.add(btnActualizar);
        arriba.add(filtro);
        add(arriba, BorderLayout.NORTH);

        // ----- Centro: tabla de resultados (arriba) y detalle (abajo) -----
        JPanel centro = new JPanel(new GridLayout(2, 1, 10, 10));
        centro.setOpaque(false);

        String[] columnas = {"Matrícula", "Alumno", "Materias cursadas", "Promedio", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modeloTabla);
        Estilo.configurarTabla(tabla);
        tabla.getSelectionModel().addListSelectionListener(this);
        centro.add(new JScrollPane(tabla));

        areaDetalle = new JTextArea();
        areaDetalle.setEditable(false);
        areaDetalle.setFont(Estilo.FUENTE_NORMAL);
        areaDetalle.setText("Selecciona un alumno de la tabla para ver sus materias.");
        centro.add(new JScrollPane(areaDetalle));

        add(centro, BorderLayout.CENTER);

        // ----- Abajo: resumen -----
        lblResumen = new JLabel(" ");
        lblResumen.setFont(Estilo.FUENTE_NEGRITA);
        lblResumen.setForeground(Estilo.COLOR_PRINCIPAL);
        add(lblResumen, BorderLayout.SOUTH);

        btnActualizar.addActionListener(this);
        cmbFiltro.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Tanto el botón como el cambio de filtro vuelven a llenar la tabla
        actualizar();
    }

    // Al seleccionar un alumno, se muestran sus materias y calificaciones
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        String matricula = modeloTabla.getValueAt(fila, 0).toString();
        Alumno alumno = sistema.buscarAlumnoPorMatricula(matricula);
        if (alumno == null) {
            return;
        }

        ArrayList<Calificacion> lista = sistema.getCalificacionesDeAlumno(alumno);
        String texto = "Materias cursadas de " + alumno.getNombre() + ":\n\n";

        if (lista.size() == 0) {
            texto = texto + "(Este alumno aún no tiene calificaciones)";
        }
        for (int i = 0; i < lista.size(); i++) {
            Calificacion c = lista.get(i);
            String estado = "Reprobada";
            if (c.getValor() >= SistemaControlEscolar.CALIFICACION_MINIMA) {
                estado = "Aprobada";
            }
            texto = texto + "  - " + c.getMateria().getNombre()
                    + ": " + String.format("%.1f", c.getValor())
                    + " (" + estado + ")\n";
        }
        areaDetalle.setText(texto);
    }

    // Recalcula todo y llena la tabla según el filtro elegido
    public void actualizar() {
        modeloTabla.setRowCount(0);
        String filtro = cmbFiltro.getSelectedItem().toString();

        int aprobados = 0;
        int reprobados = 0;
        int sinCalificaciones = 0;

        ArrayList<Alumno> alumnos = sistema.getAlumnos();
        for (int i = 0; i < alumnos.size(); i++) {
            Alumno a = alumnos.get(i);
            int cantidad = sistema.contarMaterias(a);

            String promedioTexto = "-";
            String estado = "Sin calificaciones";

            if (cantidad > 0) {
                promedioTexto = String.format("%.2f", sistema.calcularPromedio(a));
                if (sistema.estaAprobado(a)) {
                    estado = "Aprobado";
                    aprobados++;
                } else {
                    estado = "Reprobado";
                    reprobados++;
                }
            } else {
                sinCalificaciones++;
            }

            // Decidir si este alumno se muestra según el filtro
            boolean mostrar = false;
            if (filtro.equals("Todos")) {
                mostrar = true;
            } else if (filtro.equals("Aprobados") && estado.equals("Aprobado")) {
                mostrar = true;
            } else if (filtro.equals("Reprobados") && estado.equals("Reprobado")) {
                mostrar = true;
            }

            if (mostrar) {
                Object[] fila = {a.getMatricula(), a.getNombre(), cantidad, promedioTexto, estado};
                modeloTabla.addRow(fila);
            }
        }

        lblResumen.setText("Total de alumnos: " + alumnos.size()
                + "   |   Aprobados: " + aprobados
                + "   |   Reprobados: " + reprobados
                + "   |   Sin calificaciones: " + sinCalificaciones);
    }
}
