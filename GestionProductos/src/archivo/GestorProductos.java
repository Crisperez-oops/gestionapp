package archivo; // Declara el paquete llamado 'archivo' donde se organiza esta clase. Usa la palabra reservada 'package'.

import modelo.Producto; // Importa la clase 'Producto' desde el paquete 'modelo'. Usa la palabra reservada 'import'.
import java.io.IOException; // Importa la clase IOException para manejar errores de entrada/salida. Usa 'import'.
import java.util.List; // Importa la interfaz List para manejar listas de objetos. Usa 'import'.

/**
 *
 * @author Cristian Perez
 * @version 4.2
 */
public class GestorProductos { // Declara una clase pública llamada GestorProductos. Usa 'public' y 'class'.

    // COMPOSICIÓN: GestorProductos contiene un ManejadorArchivo
    private ManejadorArchivo manejadorArchivo; // Declara un atributo privado de tipo ManejadorArchivo. Usa 'private'.

    /**
     * Constructor: crea el ManejadorArchivo con el archivo por defecto.
     * ARCHIVO: productos.dat en el directorio de ejecución del proyecto.
     */ // Comentario Javadoc.
    public GestorProductos() { // Constructor público de la clase, mismo nombre que la clase. Usa 'public'.
        // Composición: instancia interna de ManejadorArchivo
        this.manejadorArchivo = new ManejadorArchivo("productos.dat"); // Crea un objeto ManejadorArchivo usando 'new' y asigna el archivo "productos.dat". Usa 'this'.
    }

    /**
     * Retorna el manejador de archivo (para acceso desde la vista si se necesita).
     * @return manejadorArchivo
     */ // Comentario Javadoc.
    public ManejadorArchivo getManejadorArchivo() { // Método público que retorna un ManejadorArchivo. Usa 'public'.
        return manejadorArchivo; // Retorna la variable manejadorArchivo. Usa 'return'.
    }

    //  OPERACIONES DE NEGOCIO 
    /**
     * Registra un nuevo producto validando que el código no exista.
     *
     * @param codigo      Código único del producto
     * @param nombre      Nombre del producto
     * @param descripcion Descripción del producto
     * @param precio      Precio del producto
     * @return Mensaje de resultado (éxito o error)
     */ // Comentario Javadoc.
    public String registrarProducto(String codigo, String nombre, String descripcion, String precio) { // Método público que retorna String y recibe 4 parámetros
        try { // Bloque try para manejar posibles excepciones. Usa 'try'.
            // Validación de campos vacíos (segunda capa, la primera es en la vista)
            if (codigo.trim().isEmpty() || nombre.trim().isEmpty() // Condicional if que verifica si algún campo está vacío usando operadores ||
                    || descripcion.trim().isEmpty() || precio.trim().isEmpty()) { // Continúa condición OR con más validaciones
                return "ERROR: Todos los campos son obligatorios."; // Retorna mensaje de error si algún campo está vacío.
            }

            // Validación de precio numérico
            double precioDouble; // Declara una variable tipo double para almacenar el precio numérico
            try { // Segundo bloque try anidado para conversión numérica.
                precioDouble = Double.parseDouble(precio.trim()); // Convierte String a double usando parseDouble.
                if (precioDouble < 0) { // Verifica si el precio es negativo
                    return "ERROR: El precio no puede ser negativo."; // Retorna error si el precio es menor a 0
                }
            } catch (NumberFormatException e) { // Captura excepción si el formato no es numérico.
                return "ERROR: El precio debe ser un número válido (ej: 25.50)."; // Retorna error de formato
            }

            // Verificar que el código no esté duplicado
            if (manejadorArchivo.existeCodigo(codigo.trim())) { // Llama método existeCodigo para validar duplicado.
                return "ERROR: Ya existe un producto con el código '" + codigo.trim() + "'."; // Retorna error concatenando String
            }

            // Crear objeto Producto y guardarlo
            Producto producto = new Producto( // Declara e instancia un objeto Producto usando 'new'
                    codigo.trim().toUpperCase(), // Convierte el código a mayúsculas con toUpperCase()
                    nombre.trim(), // Elimina espacios del nombre con trim().
                    descripcion.trim(), // Elimina espacios de la descripción
                    precioDouble // Asigna el precio ya convertido
            );
            manejadorArchivo.agregar(producto); // Llama método agregar para guardar el producto.
            return "ÉXITO: Producto '" + nombre.trim() + "' registrado correctamente."; // Retorna mensaje de éxito

        } catch (IOException e) { // Captura errores de entrada/salida.
            return "ERROR de archivo: " + e.getMessage(); // Retorna mensaje con detalle del error.
        } catch (ClassNotFoundException e) { // Captura error si no se encuentra una clase
            return "ERROR interno: " + e.getMessage(); // Retorna mensaje del error interno
        }
    }

    /**
     * Obtiene todos los productos almacenados en el archivo.
     *
     * @return Lista de productos o lista vacía si no hay registros
     */ // Comentario Javadoc.
    public List<Producto> obtenerTodos() { // Método público que retorna una lista de productos.
        try { // Bloque try
            return manejadorArchivo.leerTodos(); // Retorna todos los productos llamando leerTodos().
        } catch (IOException | ClassNotFoundException e) { // Captura múltiples excepciones usando '|'
            // Retornar lista vacía en caso de error
            return new java.util.ArrayList<>(); // Retorna una lista vacía usando generics <>
        }
    }

    /**
     * Elimina un producto por su código.
     *
     * @param codigo Código del producto a eliminar
     * @return Mensaje de resultado
     */ // Comentario Javadoc.
    public String eliminarProducto(String codigo) { // Método público que elimina producto.
        try { // Bloque try.
            if (codigo == null || codigo.trim().isEmpty()) { // Verifica si el código es null o vacío
                return "ERROR: Debe ingresar el código del producto a eliminar."; // Retorna error.
            }
            boolean resultado = manejadorArchivo.eliminar(codigo.trim().toUpperCase()); // Llama eliminar y guarda resultado booleano
            if (resultado) { // Si el resultado es true.
                return "ÉXITO: Producto con código '" + codigo.trim().toUpperCase() + "' eliminado."; // Retorna éxito.
            } else { // Si el resultado es false.
                return "ERROR: No se encontró producto con código '" + codigo.trim().toUpperCase() + "'."; // Retorna error.
            }
        } catch (IOException | ClassNotFoundException e) { // Manejo de excepciones múltiples.
            return "ERROR de archivo: " + e.getMessage(); // Retorna mensaje de error.
        }
    }

    /**
     * Actualiza los datos de un producto existente.
     *
     * @param codigo      Código del producto a actualizar (no cambia)
     * @param nombre      Nuevo nombre
     * @param descripcion Nueva descripción
     * @param precio      Nuevo precio como texto
     * @return Mensaje de resultado
     */ // Comentario Javadoc.
    public String actualizarProducto(String codigo, String nombre, String descripcion, String precio) { // Método para actualizar producto.
        try { // Bloque try.
            if (codigo.trim().isEmpty() || nombre.trim().isEmpty() // Validación de campos vacíos.
                    || descripcion.trim().isEmpty() || precio.trim().isEmpty()) { // Continuación de validación.
                return "ERROR: Todos los campos son obligatorios para actualizar."; // Retorna error.
            }

            double precioDouble; // Variable double para el precio.
            try { // Try interno.
                precioDouble = Double.parseDouble(precio.trim()); // Convierte precio a double.
                if (precioDouble < 0) { // Verifica si es negativo.
                    return "ERROR: El precio no puede ser negativo."; // Retorna error.
                }
            } catch (NumberFormatException e) { // Captura error de formato.
                return "ERROR: El precio debe ser un número válido."; // Retorna error.
            }

            Producto actualizado = new Producto( // Crea objeto Producto actualizado.
                    codigo.trim().toUpperCase(), // Código en mayúsculas.
                    nombre.trim(), // Nombre limpio.
                    descripcion.trim(), // Descripción limpia.
                    precioDouble // Precio numérico.
            );
            boolean resultado = manejadorArchivo.actualizar(actualizado); // Llama método actualizar.
            if (resultado) { // Si se actualizó correctamente.
                return "ÉXITO: Producto actualizado correctamente."; // Mensaje de éxito.
            } else { // Si no se encontró el producto.
                return "ERROR: No se encontró producto con código '" + codigo.trim().toUpperCase() + "'."; // Mensaje error.
            }
        } catch (IOException | ClassNotFoundException e) { // Manejo de excepciones.
            return "ERROR de archivo: " + e.getMessage(); // Retorna error.
        }
    }

    /**
     * Busca un producto por su código (búsqueda secuencial).
     *
     * @param codigo Código a buscar
     * @return Producto encontrado o null
     */ // Comentario Javadoc.
    public Producto buscarProducto(String codigo) { // Método que retorna un Producto.
        try { // Bloque try.
            if (codigo == null || codigo.trim().isEmpty()) { // Valida si código es null o vacío.
                return null; // Retorna null si no hay código válido.
            }
            return manejadorArchivo.buscarPorCodigo(codigo.trim().toUpperCase()); // Retorna el producto encontrado.
        } catch (IOException | ClassNotFoundException e) { // Manejo de excepciones.
            return null; // Retorna null si ocurre error.
        }
    }
} // Cierre de la clase GestorProductos.