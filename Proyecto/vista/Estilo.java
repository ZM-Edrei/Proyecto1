package vista;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

// Colores y métodos de apoyo para que todas las ventanas se vean igual.
public class Estilo {

    public static final Color COLOR_PRINCIPAL = new Color(44, 62, 80);
    public static final Color COLOR_FONDO = new Color(245, 247, 250);
    public static final Color COLOR_AZUL = new Color(41, 128, 185);
    public static final Color COLOR_VERDE = new Color(39, 174, 96);
    public static final Color COLOR_NARANJA = new Color(230, 126, 34);
    public static final Color COLOR_ROJO = new Color(192, 57, 43);
    public static final Color COLOR_GRIS = new Color(127, 140, 141);

    public static final Font FUENTE_NORMAL = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FUENTE_NEGRITA = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FUENTE_TITULO = new Font("SansSerif", Font.BOLD, 20);

    // Crea un botón con color de fondo y texto blanco
    public static JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFont(FUENTE_NEGRITA);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setOpaque(true);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return boton;
    }

    // Crea el título de cada panel
    public static JLabel crearTitulo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(FUENTE_TITULO);
        etiqueta.setForeground(COLOR_PRINCIPAL);
        return etiqueta;
    }

    // Da formato a una tabla: encabezado oscuro, filas más altas, sin edición directa
    public static void configurarTabla(JTable tabla) {
        tabla.setRowHeight(26);
        tabla.setFont(FUENTE_NORMAL);
        tabla.setSelectionBackground(COLOR_AZUL);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setDefaultEditor(Object.class, null); // las celdas no se editan a mano
        tabla.getTableHeader().setFont(FUENTE_NEGRITA);
        tabla.getTableHeader().setBackground(COLOR_PRINCIPAL);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setReorderingAllowed(false);
    }
}
