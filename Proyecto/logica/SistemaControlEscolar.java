package logica;

import java.util.ArrayList;
import modelo.Alumno;
import modelo.Calificacion;
import modelo.Materia;

// Clase central: guarda los datos en ArrayList y contiene las reglas del sistema.
// Las ventanas NO guardan datos; le piden todo a esta clase.
//
// Convención: los métodos que pueden fallar devuelven un String:
//   - null  -> todo salió bien
//   - texto -> mensaje de error para mostrar al usuario
public class SistemaControlEscolar {

    // Calificación mínima para aprobar
    public static final double CALIFICACION_MINIMA = 70;

    private ArrayList<Alumno> alumnos;
    private ArrayList<Materia> materias;
    private ArrayList<Calificacion> calificaciones;

    public SistemaControlEscolar() {
        alumnos = new ArrayList<Alumno>();
        materias = new ArrayList<Materia>();
        calificaciones = new ArrayList<Calificacion>();
    }

    // ======================= ALUMNOS =======================

    public String agregarAlumno(String matricula, String nombre) {
        matricula = matricula.trim();
        nombre = nombre.trim();

        if (matricula.equals("") || nombre.equals("")) {
            return "Debes escribir la matrícula y el nombre.";
        }
        if (buscarAlumnoPorMatricula(matricula) != null) {
            return "Ya existe un alumno con esa matrícula.";
        }
        alumnos.add(new Alumno(matricula, nombre));
        return null;
    }

    // Devuelve el alumno con esa matrícula, o null si no existe
    public Alumno buscarAlumnoPorMatricula(String matricula) {
        for (int i = 0; i < alumnos.size(); i++) {
            Alumno a = alumnos.get(i);
            if (a.getMatricula().equalsIgnoreCase(matricula)) {
                return a;
            }
        }
        return null;
    }

    // Busca alumnos cuyo nombre o matrícula CONTENGAN el texto escrito
    public ArrayList<Alumno> buscarAlumnos(String texto) {
        ArrayList<Alumno> encontrados = new ArrayList<Alumno>();
        texto = texto.trim().toLowerCase();

        for (int i = 0; i < alumnos.size(); i++) {
            Alumno a = alumnos.get(i);
            String nombre = a.getNombre().toLowerCase();
            String matricula = a.getMatricula().toLowerCase();
            if (nombre.contains(texto) || matricula.contains(texto)) {
                encontrados.add(a);
            }
        }
        return encontrados;
    }

    public String modificarAlumno(String matriculaActual, String nuevaMatricula, String nuevoNombre) {
        nuevaMatricula = nuevaMatricula.trim();
        nuevoNombre = nuevoNombre.trim();

        if (nuevaMatricula.equals("") || nuevoNombre.equals("")) {
            return "Debes escribir la matrícula y el nombre.";
        }
        Alumno alumno = buscarAlumnoPorMatricula(matriculaActual);
        if (alumno == null) {
            return "No se encontró el alumno.";
        }
        // Si cambió la matrícula, verificar que no la tenga otro alumno
        if (!nuevaMatricula.equalsIgnoreCase(matriculaActual)) {
            if (buscarAlumnoPorMatricula(nuevaMatricula) != null) {
                return "Ya existe un alumno con esa matrícula.";
            }
        }
        alumno.setMatricula(nuevaMatricula);
        alumno.setNombre(nuevoNombre);
        return null;
    }

    // Elimina al alumno y también todas sus calificaciones
    public void eliminarAlumno(String matricula) {
        Alumno alumno = buscarAlumnoPorMatricula(matricula);
        if (alumno == null) {
            return;
        }
        // Se recorre al revés para poder borrar sin saltarse elementos
        for (int i = calificaciones.size() - 1; i >= 0; i--) {
            if (calificaciones.get(i).getAlumno() == alumno) {
                calificaciones.remove(i);
            }
        }
        alumnos.remove(alumno);
    }

    // ======================= MATERIAS =======================

    public String agregarMateria(String nombre) {
        nombre = nombre.trim();

        if (nombre.equals("")) {
            return "Debes escribir el nombre de la materia.";
        }
        if (buscarMateriaPorNombre(nombre) != null) {
            return "Ya existe una materia con ese nombre.";
        }
        materias.add(new Materia(nombre));
        return null;
    }

    public Materia buscarMateriaPorNombre(String nombre) {
        for (int i = 0; i < materias.size(); i++) {
            Materia m = materias.get(i);
            if (m.getNombre().equalsIgnoreCase(nombre)) {
                return m;
            }
        }
        return null;
    }

    public String modificarMateria(String nombreActual, String nuevoNombre) {
        nuevoNombre = nuevoNombre.trim();

        if (nuevoNombre.equals("")) {
            return "Debes escribir el nombre de la materia.";
        }
        Materia materia = buscarMateriaPorNombre(nombreActual);
        if (materia == null) {
            return "No se encontró la materia.";
        }
        if (!nuevoNombre.equalsIgnoreCase(nombreActual)) {
            if (buscarMateriaPorNombre(nuevoNombre) != null) {
                return "Ya existe una materia con ese nombre.";
            }
        }
        materia.setNombre(nuevoNombre);
        return null;
    }

    // Elimina la materia y todas las calificaciones que tenga
    public void eliminarMateria(String nombre) {
        Materia materia = buscarMateriaPorNombre(nombre);
        if (materia == null) {
            return;
        }
        for (int i = calificaciones.size() - 1; i >= 0; i--) {
            if (calificaciones.get(i).getMateria() == materia) {
                calificaciones.remove(i);
            }
        }
        materias.remove(materia);
    }

    // ======================= CALIFICACIONES =======================

    // Devuelve la calificación de ese alumno en esa materia, o null si no existe
    public Calificacion buscarCalificacion(Alumno alumno, Materia materia) {
        for (int i = 0; i < calificaciones.size(); i++) {
            Calificacion c = calificaciones.get(i);
            if (c.getAlumno() == alumno && c.getMateria() == materia) {
                return c;
            }
        }
        return null;
    }

    public String agregarCalificacion(Alumno alumno, Materia materia, double valor) {
        if (alumno == null) {
            return "Selecciona un alumno.";
        }
        if (materia == null) {
            return "Selecciona una materia.";
        }
        if (valor < 0 || valor > 100) {
            return "La calificación debe estar entre 0 y 100.";
        }
        if (buscarCalificacion(alumno, materia) != null) {
            return "Ese alumno ya tiene calificación en esa materia. Usa el botón Modificar.";
        }
        calificaciones.add(new Calificacion(alumno, materia, valor));
        return null;
    }

    public String modificarCalificacion(Alumno alumno, Materia materia, double valor) {
        if (alumno == null) {
            return "Selecciona un alumno.";
        }
        if (materia == null) {
            return "Selecciona una materia.";
        }
        if (valor < 0 || valor > 100) {
            return "La calificación debe estar entre 0 y 100.";
        }
        Calificacion c = buscarCalificacion(alumno, materia);
        if (c == null) {
            return "Ese alumno aún no tiene calificación en esa materia. Usa el botón Agregar.";
        }
        c.setValor(valor);
        return null;
    }

    public String eliminarCalificacion(Alumno alumno, Materia materia) {
        Calificacion c = buscarCalificacion(alumno, materia);
        if (c == null) {
            return "No existe esa calificación.";
        }
        calificaciones.remove(c);
        return null;
    }

    // Lista de calificaciones de un solo alumno
    public ArrayList<Calificacion> getCalificacionesDeAlumno(Alumno alumno) {
        ArrayList<Calificacion> lista = new ArrayList<Calificacion>();
        for (int i = 0; i < calificaciones.size(); i++) {
            if (calificaciones.get(i).getAlumno() == alumno) {
                lista.add(calificaciones.get(i));
            }
        }
        return lista;
    }

    // ======================= RESULTADOS =======================

    // Promedio del alumno (0 si no tiene calificaciones)
    public double calcularPromedio(Alumno alumno) {
        ArrayList<Calificacion> lista = getCalificacionesDeAlumno(alumno);
        if (lista.size() == 0) {
            return 0;
        }
        double suma = 0;
        for (int i = 0; i < lista.size(); i++) {
            suma = suma + lista.get(i).getValor();
        }
        return suma / lista.size();
    }

    // Cuántas materias tiene calificadas el alumno
    public int contarMaterias(Alumno alumno) {
        return getCalificacionesDeAlumno(alumno).size();
    }

    // Aprobado: tiene calificaciones y su promedio llega al mínimo
    public boolean estaAprobado(Alumno alumno) {
        if (contarMaterias(alumno) == 0) {
            return false;
        }
        return calcularPromedio(alumno) >= CALIFICACION_MINIMA;
    }

    // ======================= GETTERS DE LAS LISTAS =======================

    public ArrayList<Alumno> getAlumnos() {
        return alumnos;
    }

    public ArrayList<Materia> getMaterias() {
        return materias;
    }

    public ArrayList<Calificacion> getCalificaciones() {
        return calificaciones;
    }
}
