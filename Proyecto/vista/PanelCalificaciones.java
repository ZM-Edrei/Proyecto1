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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import logica.SistemaControlEscolar;
import modelo.Alumno;
import modelo.Calificacion;
import modelo.Materia;

// Pestaña de calificaciones: elegir alumno y materia, capturar la nota y ver la tabla.
public class PanelCalificaciones extends JPanel implements ActionListener, ListSelectionListener {

    private SistemaControlEscolar sistema;

    private JComboBox<Alumno> cmbAlumno;
    private JComboBox<Materia> cmbMateria;
    private JTextField txtCalificacion;

    private JButton btnAgregar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnVerAlumno;
    private JButton btnVerTodas;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public PanelCalificaciones(SistemaControlEscolar sistema) {
        this.sistema = sistema;
        setLayout(new BorderLayout(10, 10));
        setBackground(Estilo.COLOR_FONDO);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        crearComponentes();
        actualizar();
    }

    private void crearComponentes() {
        // ----- Arriba: título y formulario -----
        JPanel arriba = new JPanel(new GridLayout(3, 1, 5, 5));
        arriba.setOpaque(false);
        arriba.add(Estilo.crearTitulo("Administración de calificaciones"));

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        formulario.setOpaque(false);
        cmbAlumno = new JComboBox<Alumno>();
        cmbMateria = new JComboBox<Materia>();
        txtCalificacion = new JTextField(6);
        formulario.add(new JLabel("Alumno:"));
        formulario.add(cmbAlumno);
        formulario.add(new JLabel("Materia:"));
        formulario.add(cmbMateria);
        formulario.add(new JLabel("Calificación (0-100):"));
        formulario.add(txtCalificacion);
        arriba.add(formulario);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        filtros.setOpaque(false);
        btnVerAlumno = Estilo.crearBoton("Ver calificaciones del alumno", Estilo.COLOR_AZUL);
        btnVerTodas = Estilo.crearBoton("Ver todas", Estilo.COLOR_GRIS);
        filtros.add(btnVerAlumno);
        filtros.add(btnVerTodas);
        arriba.add(filtros);

        add(arriba, BorderLayout.NORTH);

        // ----- Centro: tabla -----
        String[] columnas = {"Matrícula", "Alumno", "Materia", "Calificación"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modeloTabla);
        Estilo.configurarTabla(tabla);
        tabla.getSelectionModel().addListSelectionListener(this);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // ----- Abajo: botones -----
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        abajo.setOpaque(false);
        btnAgregar = Estilo.crearBoton("Agregar", Estilo.COLOR_VERDE);
        btnModificar = Estilo.crearBoton("Modificar", Estilo.COLOR_NARANJA);
        btnEliminar = Estilo.crearBoton("Eliminar", Estilo.COLOR_ROJO);
        btnLimpiar = Estilo.crearBoton("Limpiar", Estilo.COLOR_GRIS);
        abajo.add(btnAgregar);
        abajo.add(btnModificar);
        abajo.add(btnEliminar);
        abajo.add(btnLimpiar);
        add(abajo, BorderLayout.SOUTH);

        btnAgregar.addActionListener(this);
        btnModificar.addActionListener(this);
        btnEliminar.addActionListener(this);
        btnLimpiar.addActionListener(this);
        btnVerAlumno.addActionListener(this);
        btnVerTodas.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();

        if (origen == btnAgregar) {
            agregar();
        } else if (origen == btnModificar) {
            modificar();
        } else if (origen == btnEliminar) {
            eliminar();
        } else if (origen == btnLimpiar) {
            limpiarFormulario();
        } else if (origen == btnVerAlumno) {
            verCalificacionesDelAlumno();
        } else if (origen == btnVerTodas) {
            llenarTabla(sistema.getCalificaciones());
        }
    }

    // Al seleccionar una fila, se cargan sus datos en el formulario
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            String matricula = modeloTabla.getValueAt(fila, 0).toString();
            String nombreMateria = modeloTabla.getValueAt(fila, 2).toString();

            cmbAlumno.setSelectedItem(sistema.buscarAlumnoPorMatricula(matricula));
            cmbMateria.setSelectedItem(sistema.buscarMateriaPorNombre(nombreMateria));
            txtCalificacion.setText(modeloTabla.getValueAt(fila, 3).toString());
        }
    }

    private void agregar() {
        Alumno alumno = (Alumno) cmbAlumno.getSelectedItem();
        Materia materia = (Materia) cmbMateria.getSelectedItem();
        if (!calificacionEsNumero()) {
            return;
        }
        double valor = leerCalificacion();

        String error = sistema.agregarCalificacion(alumno, materia, valor);
        if (error != null) {
            mostrarAviso(error);
        } else {
            limpiarFormulario();
            llenarTabla(sistema.getCalificaciones());
        }
    }

    private void modificar() {
        Alumno alumno = (Alumno) cmbAlumno.getSelectedItem();
        Materia materia = (Materia) cmbMateria.getSelectedItem();
        if (!calificacionEsNumero()) {
            return;
        }
        double valor = leerCalificacion();

        String error = sistema.modificarCalificacion(alumno, materia, valor);
        if (error != null) {
            mostrarAviso(error);
        } else {
            limpiarFormulario();
            llenarTabla(sistema.getCalificaciones());
        }
    }

    private void eliminar() {
        Alumno alumno = (Alumno) cmbAlumno.getSelectedItem();
        Materia materia = (Materia) cmbMateria.getSelectedItem();

        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar esta calificación?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            String error = sistema.eliminarCalificacion(alumno, materia);
            if (error != null) {
                mostrarAviso(error);
            } else {
                limpiarFormulario();
                llenarTabla(sistema.getCalificaciones());
            }
        }
    }

    private void verCalificacionesDelAlumno() {
        Alumno alumno = (Alumno) cmbAlumno.getSelectedItem();
        if (alumno == null) {
            mostrarAviso("Selecciona un alumno.");
            return;
        }
        llenarTabla(sistema.getCalificacionesDeAlumno(alumno));
    }

    // Revisa que el texto escrito sea un número. Si no, avisa al usuario.
    private boolean calificacionEsNumero() {
        try {
            Double.parseDouble(txtCalificacion.getText().trim().replace(",", "."));
            return true;
        } catch (NumberFormatException ex) {
            mostrarAviso("La calificación debe ser un número (por ejemplo 85 u 85.5).");
            return false;
        }
    }

    // Convierte el texto a número (usar solo después de calificacionEsNumero)
    private double leerCalificacion() {
        return Double.parseDouble(txtCalificacion.getText().trim().replace(",", "."));
    }

    private void limpiarFormulario() {
        txtCalificacion.setText("");
        tabla.clearSelection();
    }

    private void llenarTabla(ArrayList<Calificacion> lista) {
        modeloTabla.setRowCount(0);
        for (int i = 0; i < lista.size(); i++) {
            Calificacion c = lista.get(i);
            Object[] fila = {
                c.getAlumno().getMatricula(),
                c.getAlumno().getNombre(),
                c.getMateria().getNombre(),
                String.format("%.1f", c.getValor())
            };
            modeloTabla.addRow(fila);
        }
    }

    // Recarga las listas desplegables (alumnos y materias) y la tabla.
    // Se llama al cambiar de pestaña para ver los alumnos/materias nuevos.
    public void actualizar() {
        Alumno alumnoElegido = (Alumno) cmbAlumno.getSelectedItem();
        Materia materiaElegida = (Materia) cmbMateria.getSelectedItem();

        cmbAlumno.removeAllItems();
        for (int i = 0; i < sistema.getAlumnos().size(); i++) {
            cmbAlumno.addItem(sistema.getAlumnos().get(i));
        }
        cmbMateria.removeAllItems();
        for (int i = 0; i < sistema.getMaterias().size(); i++) {
            cmbMateria.addItem(sistema.getMaterias().get(i));
        }

        // Si lo que estaba elegido sigue existiendo, se vuelve a elegir
        if (alumnoElegido != null && sistema.getAlumnos().contains(alumnoElegido)) {
            cmbAlumno.setSelectedItem(alumnoElegido);
        }
        if (materiaElegida != null && sistema.getMaterias().contains(materiaElegida)) {
            cmbMateria.setSelectedItem(materiaElegida);
        }

        llenarTabla(sistema.getCalificaciones());
    }

    private void mostrarAviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}
