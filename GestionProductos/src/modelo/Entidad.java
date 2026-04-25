package modelo; // Defino el paquete 'modelo', donde van las clases que representan la lógica de datos del sistema. Uso 'package'.

import java.io.Serializable; // Importo la interfaz Serializable para permitir que los objetos se puedan guardar en archivos. Uso 'import'.

/**
 * Clase abstracta base que representa una entidad genérica del sistema.
 * @author Cristian Perez
 * @version 3.6
 */ // Comentario tipo Javadoc que documenta la clase.
public abstract class Entidad implements Serializable { // Declaro una clase abstracta que implementa Serializable. Uso 'public', 'abstract', 'class' e 'implements'.

    // serialVersionUID requerido para serialización segura
    private static final long serialVersionUID = 1L; // Constante para control de versiones en serialización. Uso 'private', 'static', 'final', tipo 'long' y sufijo 'L'.

    // Atributos privados - Encapsulamiento
    private String codigo; // Atributo privado que almacena el código único. Uso 'private'.
    private String nombre; // Atributo privado que almacena el nombre. Uso 'private'.

    /**
     * Constructor con parámetros básicos de toda entidad.
     * @param codigo Identificador único
     * @param nombre Nombre de la entidad
     */ // Comentario Javadoc.
    public Entidad(String codigo, String nombre) { // Constructor de la clase. Uso 'public'.
        this.codigo = codigo; // Asigno el valor recibido al atributo usando 'this'.
        this.nombre = nombre; // Asigno el nombre al atributo correspondiente.
    }

    // GETTERS Y SETTERS 
    /**
     * Retorna el código único de la entidad.
     * @return codigo
     */ // Comentario Javadoc.
    public String getCodigo() { // Método getter que retorna el código. Uso 'public'.
        return codigo; // Devuelvo el valor del atributo codigo. Uso 'return'.
    }

    /**
     * Establece el código único de la entidad.
     * @param codigo nuevo código
     */ // Comentario Javadoc.
    public void setCodigo(String codigo) { // Método setter que no retorna nada (void).
        this.codigo = codigo; // Asigno el nuevo valor al atributo usando 'this'.
    }

    /**
     * Retorna el nombre de la entidad.
     * @return nombre
     */ // Comentario Javadoc.
    public String getNombre() { // Método getter del nombre.
        return nombre; // Retorno el valor del atributo nombre.
    }

    /**
     * Establece el nombre de la entidad.
     * @param nombre nuevo nombre
     */ // Comentario Javadoc.
    public void setNombre(String nombre) { // Método setter del nombre.
        this.nombre = nombre; // Actualizo el atributo con el nuevo valor.
    }

    /**
     * Método abstracto que fuerza a las subclases a proveer
     * una representación en texto de sus datos.
     * @return String con la información del objeto
     */ // Comentario Javadoc.
    public abstract String obtenerInfo(); // Declaro un método abstracto sin implementación. Uso 'abstract', obliga a las subclases a implementarlo.
}