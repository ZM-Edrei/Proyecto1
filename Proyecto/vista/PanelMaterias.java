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
import modelo.Materia;

// Pestaña de materias: formulario + tabla + botones.
public class PanelMaterias extends JPanel implements ActionListener, ListSelectionListener {

    private SistemaControlEscolar sistema;

    private JTextField txtNombre;
    private JButton btnAgregar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public PanelMaterias(SistemaControlEscolar sistema) {
        this.sistema = sistema;
        setLayout(new BorderLayout(10, 10));
        setBackground(Estilo.COLOR_FONDO);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        crearComponentes();
        actualizar();
    }

    private void crearComponentes() {
        // ----- Arriba: título y formulario -----
        JPanel arriba = new JPanel(new GridLayout(2, 1, 5, 5));
        arriba.setOpaque(false);
        arriba.add(Estilo.crearTitulo("Administración de materias"));

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        formulario.setOpaque(false);
        txtNombre = new JTextField(25);
        formulario.add(new JLabel("Nombre de la materia:"));
        formulario.add(txtNombre);
        arriba.add(formulario);
        add(arriba, BorderLayout.NORTH);

        // ----- Centro: tabla -----
        String[] columnas = {"Materia"};
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
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            txtNombre.setText(modeloTabla.getValueAt(fila, 0).toString());
        }
    }

    private void agregar() {
        String error = sistema.agregarMateria(txtNombre.getText());
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
            mostrarAviso("Selecciona una materia de la tabla.");
            return;
        }
        String nombreActual = modeloTabla.getValueAt(fila, 0).toString();
        String error = sistema.modificarMateria(nombreActual, txtNombre.getText());
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
            mostrarAviso("Selecciona una materia de la tabla.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la materia y todas sus calificaciones?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            String nombre = modeloTabla.getValueAt(fila, 0).toString();
            sistema.eliminarMateria(nombre);
            limpiarFormulario();
            actualizar();
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        tabla.clearSelection();
    }

    public void actualizar() {
        modeloTabla.setRowCount(0);
        ArrayList<Materia> lista = sistema.getMaterias();
        for (int i = 0; i < lista.size(); i++) {
            Object[] fila = {lista.get(i).getNombre()};
            modeloTabla.addRow(fila);
        }
    }

    private void mostrarAviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}
