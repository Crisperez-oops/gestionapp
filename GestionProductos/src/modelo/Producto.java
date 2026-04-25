package modelo; // Defino el paquete 'modelo', donde manejo las clases de datos del sistema. Uso la palabra reservada 'package'.

import java.io.Serializable; // Importo Serializable para permitir guardar objetos en archivos (serialización). Uso 'import'.

/**
 * Clase Producto que extiende Entidad.
 * Representa un producto con código, nombre, descripción y precio.
 * @author Cristian Perez
 * @version 3.0
 */ // Comentario tipo Javadoc.
public class Producto extends Entidad implements Serializable { // Declaro la clase Producto que hereda de Entidad y además implementa Serializable. Uso 'public', 'class', 'extends', 'implements'.

    private static final long serialVersionUID = 2L; // Identificador de versión para serialización. Uso 'private', 'static', 'final', tipo 'long' y sufijo 'L'.

    // Atributos privados adicionales - Encapsulamiento
    private String descripcion; // Atributo privado para guardar la descripción del producto.
    private double precio; // Atributo privado tipo double para el precio del producto.

    /**
     * Constructor completo del Producto.
     * @param codigo      Identificador único del producto
     * @param nombre      Nombre del producto
     * @param descripcion Descripción detallada del producto
     * @param precio      Precio del producto
     */ // Comentario Javadoc.
    public Producto(String codigo, String nombre, String descripcion, double precio) { // Constructor con parámetros. Uso 'public'.
        super(codigo, nombre); // Llamo al constructor de la superclase Entidad usando 'super'.
        this.descripcion = descripcion; // Asigno la descripción al atributo usando 'this'.
        this.precio = precio; // Asigno el precio al atributo correspondiente.
    }

    // GETTERS Y SETTERS 

    /**
     * Retorna la descripción del producto.
     * @return descripcion
     */ // Comentario Javadoc.
    public String getDescripcion() { // Método getter que retorna la descripción. Uso 'public'.
        return descripcion; // Devuelvo el valor del atributo descripcion.
    }

    /**
     * Establece la descripción del producto.
     * @param descripcion nueva descripción
     */ // Comentario Javadoc.
    public void setDescripcion(String descripcion) { // Método setter.
        this.descripcion = descripcion; // Actualizo el atributo usando 'this'.
    }

    /**
     * Retorna el precio del producto.
     * @return precio
     */ // Comentario Javadoc.
    public double getPrecio() { // Getter del precio.
        return precio; // Retorno el valor del atributo precio.
    }

    /**
     * Establece el precio del producto.
     * @param precio nuevo precio
     */ // Comentario Javadoc.
    public void setPrecio(double precio) { // Setter del precio.
        this.precio = precio; // Actualizo el valor del atributo.
    }

    /**
     * Implementación del método abstracto de Entidad.
     * Retorna una cadena con toda la información del producto.
     * @return String con los datos del producto
     */ // Comentario Javadoc.
    @Override // Indico que estoy sobrescribiendo un método de la superclase. Uso '@Override'.
    public String obtenerInfo() { // Implemento el método abstracto definido en Entidad.
        return "Código: " + getCodigo() // Concateno texto con el código usando '+'.
                + " | Nombre: " + getNombre() // Concateno el nombre del producto.
                + " | Descripción: " + descripcion // Concateno la descripción directamente.
                + " | Precio: S/." + String.format("%.2f", precio); // Formateo el precio a 2 decimales con String.format.
    }

    /**
     * Representación en texto del objeto Producto.
     * @return String legible del producto
     */ // Comentario Javadoc.
    @Override // Indico que sobrescribo el método toString() de la clase Object.
    public String toString() { // Método toString que retorna una representación en texto.
        return obtenerInfo(); // Retorno la información del producto reutilizando el método anterior.
    }
}