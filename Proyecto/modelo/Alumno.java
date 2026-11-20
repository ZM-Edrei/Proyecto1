package modelo;

// Representa a un alumno del grupo.
public class Alumno {

    // Atributos (privados: solo se acceden con getters y setters)
    private String matricula;
    private String nombre;

    // Constructor: se ejecuta al crear un alumno con "new Alumno(...)"
    public Alumno(String matricula, String nombre) {
        this.matricula = matricula;
        this.nombre = nombre;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Define cómo se muestra el alumno en un JComboBox
    @Override
    public String toString() {
        return matricula + " - " + nombre;
    }
}
