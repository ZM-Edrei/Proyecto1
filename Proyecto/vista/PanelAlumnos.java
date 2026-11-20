package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
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

// Pestaña de alumnos: formulario + búsqueda + tabla + botones.
// Implementa ActionListener (para los botones) y ListSelectionListener (para la tabla).
public class PanelAlumnos extends JPanel implements ActionListener, ListSelectionListener {

    private SistemaControlEscolar sistema;

    private JTextField txtMatricula;
    private JTextField txtNombre;
    private JTextField txtBuscar;

    private JButton btnAgregar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnBuscar;
    private JButton btnMostrarTodos;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public PanelAlumnos(SistemaControlEscolar sistema) {
        this.sistema = sistema;
        setLayout(new BorderLayout(10, 10));
        setBackground(Estilo.COLOR_FONDO);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        crearComponentes();
        actualizar();
    }

    private void crearComponentes() {
        // ----- Parte de arriba: título, formulario y búsqueda -----
        JPanel arriba = new JPanel(new GridLayout(3, 1, 5, 5));
        arriba.setOpaque(false);

        arriba.add(Estilo.crearTitulo("Administración de alumnos"));

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        formulario.setOpaque(false);
        txtMatricula = new JTextField(10);
        txtNombre = new JTextField(22);
        formulario.add(new JLabel("Matrícula:"));
        formulario.add(txtMatricula);
        formulario.add(new JLabel("Nombre completo:"));
        formulario.add(txtNombre);
        arriba.add(formulario);

        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        busqueda.setOpaque(false);
        txtBuscar = new JTextField(18);
        btnBuscar = Estilo.crearBoton("Buscar", Estilo.COLOR_AZUL);
        btnMostrarTodos = Estilo.crearBoton("Mostrar todos", Estilo.COLOR_GRIS);
        busqueda.add(new JLabel("Buscar por nombre o matrícula:"));
        busqueda.add(txtBuscar);
        busqueda.add(btnBuscar);
        busqueda.add(btnMostrarTodos);
        arriba.add(busqueda);

        add(arriba, BorderLayout.NORTH);

        // ----- Centro: tabla -----
        String[] columnas = {"Matrícula", "Nombre"};
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

        // Conectar los botones con este panel (this)
        btnAgregar.addActionListener(this);
        btnModificar.addActionListener(this);
        btnEliminar.addActionListener(this);
        btnLimpiar.addActionListener(this);
        btnBuscar.addActionListener(this);
        btnMostrarTodos.addActionListener(this);
    }

    // Se ejecuta automáticamente cuando se presiona cualquier botón
    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource(); // ¿qué botón fue?

        if (origen == btnAgregar) {
            agregar();
        } else if (origen == btnModificar) {
            modificar();
        } else if (origen == btnEliminar) {
            eliminar();
        } else if (origen == btnLimpiar) {
            limpiarFormulario();
        } else if (origen == btnBuscar) {
            buscar();
        } else if (origen == btnMostrarTodos) {
            txtBuscar.setText("");
            actualizar();
        }
    }

    // Se ejecuta cuando el usuario selecciona una fila de la tabla
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            txtMatricula.setText(modeloTabla.getValueAt(fila, 0).toString());
            txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        }
    }

    private void agregar() {
        String error = sistema.agregarAlumno(txtMatricula.getText(), txtNombre.getText());
        if (error != null) {
            mostrarAviso(error);
        } else {
            limpiarFormulario();
            actualizar();
        }
    }

    private void modificar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarAviso("Selecciona un alumno de la tabla.");
            return;
        }
        // La matrícula original está en la fila seleccionada
        String matriculaActual = modeloTabla.getValueAt(fila, 0).toString();
        String error = sistema.modificarAlumno(matriculaActual, txtMatricula.getText(), txtNombre.getText());
        if (error != null) {
            mostrarAviso(error);
        } else {
            limpiarFormulario();
            actualizar();
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarAviso("Selecciona un alumno de la tabla.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar al alumno y todas sus calificaciones?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            String matricula = modeloTabla.getValueAt(fila, 0).toString();
            sistema.eliminarAlumno(matricula);
            limpiarFormulario();
            actualizar();
        }
    }

    private void buscar() {
        String texto = txtBuscar.getText();
        ArrayList<Alumno> encontrados = sistema.buscarAlumnos(texto);
        llenarTabla(encontrados);
        if (encontrados.size() == 0) {
            mostrarAviso("No se encontraron alumnos.");
        }
    }

    private void limpiarFormulario() {
        txtMatricula.setText("");
        txtNombre.setText("");
        tabla.clearSelection();
    }

    // Llena la tabla con la lista que reciba
    private void llenarTabla(ArrayList<Alumno> lista) {
        modeloTabla.setRowCount(0); // borra las filas actuales
        for (int i = 0; i < lista.size(); i++) {
            Alumno a = lista.get(i);
            Object[] fila = {a.getMatricula(), a.getNombre()};
            modeloTabla.addRow(fila);
        }
    }

    // Muestra todos los alumnos (la ventana principal también lo llama al cambiar de pestaña)
    public void actualizar() {
        llenarTabla(sistema.getAlumnos());
    }

    private void mostrarAviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}
