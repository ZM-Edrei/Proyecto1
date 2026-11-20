package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import logica.SistemaControlEscolar;

// Ventana principal: contiene un encabezado y las 4 pestañas.
// Crea UN SOLO SistemaControlEscolar y se lo entrega a todos los paneles,
// así todos trabajan con los mismos datos.
public class VentanaPrincipal extends JFrame implements ChangeListener {

    private SistemaControlEscolar sistema;
    private JTabbedPane pestanas;

    private PanelAlumnos panelAlumnos;
    private PanelMaterias panelMaterias;
    private PanelCalificaciones panelCalificaciones;
    private PanelResultados panelResultados;

    public VentanaPrincipal() {
        sistema = new SistemaControlEscolar();

        setTitle("Sistema de Control Escolar");
        setSize(950, 620);
        setLocationRelativeTo(null); // centrar en pantalla
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Encabezado
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(Estilo.COLOR_PRINCIPAL);
        encabezado.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel titulo = new JLabel("Sistema de Control Escolar");
        titulo.setFont(Estilo.FUENTE_TITULO);
        titulo.setForeground(Color.WHITE);
        encabezado.add(titulo, BorderLayout.WEST);
        add(encabezado, BorderLayout.NORTH);

        // Paneles
        panelAlumnos = new PanelAlumnos(sistema);
        panelMaterias = new PanelMaterias(sistema);
        panelCalificaciones = new PanelCalificaciones(sistema);
        panelResultados = new PanelResultados(sistema);

        // Pestañas
        pestanas = new JTabbedPane();
        pestanas.setFont(Estilo.FUENTE_NEGRITA);
        pestanas.addTab("Alumnos", panelAlumnos);
        pestanas.addTab("Materias", panelMaterias);
        pestanas.addTab("Calificaciones", panelCalificaciones);
        pestanas.addTab("Resultados", panelResultados);
        pestanas.addChangeListener(this);
        add(pestanas, BorderLayout.CENTER);
    }

    // Se ejecuta cada vez que el usuario cambia de pestaña:
    // se refrescan los paneles para mostrar los datos más recientes.
    @Override
    public void stateChanged(ChangeEvent e) {
        panelAlumnos.actualizar();
        panelMaterias.actualizar();
        panelCalificaciones.actualizar();
        panelResultados.actualizar();
    }
}
