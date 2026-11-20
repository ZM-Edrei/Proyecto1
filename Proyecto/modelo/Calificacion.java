package modelo;

// Une un alumno, una materia y su calificación (0 a 100).
public class Calificacion {

    private Alumno alumno;
    private Materia materia;
    private double valor;

    public Calificacion(Alumno alumno, Materia materia, double valor) {
        this.alumno = alumno;
        this.materia = materia;
        this.valor = valor;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public Materia getMateria() {
        return materia;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
