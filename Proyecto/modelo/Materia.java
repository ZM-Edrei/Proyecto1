package modelo;

// Representa una materia del grupo.
public class Materia {

    private String nombre;

    public Materia(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Define cómo se muestra la materia en un JComboBox
    @Override
    public String toString() {
        return nombre;
    }
}
