package archivo; // Defino el paquete 'archivo' para organizar la clase dentro del proyecto. Uso la palabra reservada 'package'.

import modelo.Producto; // Importo la clase Producto desde el paquete modelo para poder trabajar con objetos de ese tipo. Uso 'import'.
import java.io.*; // Importo todo el paquete java.io (Input/Output) para manejo de archivos y streams. Uso '*' como comodín.
import java.util.ArrayList; // Importo ArrayList para crear listas dinámicas. Uso 'import'.
import java.util.List; // Importo la interfaz List para trabajar de forma más general con listas.

 /**
  * Clase ManejadorArchivo: responsable de toda la lógica de
  * lectura y escritura secuencial de objetos Producto en disco.
  *
  * DECISIÓN DE DISEÑO: Se eligió ObjectOutputStream / ObjectInputStream
  * porque permite serializar objetos Java completos
  * directamente, sin necesidad de convertir manualmente a texto.
  * Esto garantiza integridad de datos, tipos correctos y extensibilidad.
  *
  * @author Cristian Perez
  * @version 4.0
  */
public class ManejadorArchivo { // Declaro la clase pública ManejadorArchivo. Uso 'public' y 'class'.

    // Nombre del archivo donde se almacenan los productos
    private String rutaArchivo; // Atributo privado que guarda la ruta del archivo. Uso 'private'.

    /**
     * Constructor que recibe la ruta del archivo a gestionar.
     * @param rutaArchivo ruta y nombre del archivo (ej: "productos.dat")
     */ // Comentario Javadoc.
    public ManejadorArchivo(String rutaArchivo) { // Constructor de la clase. Uso 'public'.
        this.rutaArchivo = rutaArchivo; // Asigno el valor recibido al atributo usando 'this'.
    }

    /**
     * Retorna la ruta del archivo actual.
     * @return rutaArchivo
     */ // Comentario Javadoc.
    public String getRutaArchivo() { // Método getter que retorna un String. Uso 'public'.
        return rutaArchivo; // Devuelvo la ruta del archivo. Uso 'return'.
    }

    /**
     * Establece una nueva ruta de archivo.
     * @param rutaArchivo nueva ruta
     */ // Comentario Javadoc.
    public void setRutaArchivo(String rutaArchivo) { // Método setter que no retorna nada (void).
        this.rutaArchivo = rutaArchivo; // Actualizo la ruta usando 'this'.
    }

    // ESCRITURA SECUENCIAL 

    /**
     * Guarda una lista completa de productos en el archivo de forma secuencial.
     * Sobrescribe el archivo con la lista actual.
     *
     * @param productos Lista de productos a guardar
     * @throws IOException Si ocurre un error de escritura
     */ // Comentario Javadoc.
    public void guardarTodos(List<Producto> productos) throws IOException { // Método que puede lanzar IOException. Uso 'throws'.

        // FileOutputStream crea/sobreescribe el archivo
        // ObjectOutputStream envuelve el stream para serialización
        try (ObjectOutputStream oos = new ObjectOutputStream( // Uso try-with-resources para cerrar automáticamente el stream.
                new BufferedOutputStream( // Buffer para mejorar rendimiento.
                        new FileOutputStream(rutaArchivo)))) { // Abro el archivo en la ruta indicada.

            // Escritura SECUENCIAL: recorre la lista y escribe objeto por objeto
            for (Producto p : productos) { // Uso for-each para recorrer la lista.
                oos.writeObject(p); // Serializo cada objeto Producto en el archivo.
            }
            oos.flush(); // Fuerzo la escritura completa en disco.
        }
    }

    // LECTURA SECUENCIAL 

    /**
     * Lee todos los productos del archivo de forma secuencial.
     * @return 
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public List<Producto> leerTodos() throws IOException, ClassNotFoundException { // Método que retorna una lista y lanza excepciones.
        List<Producto> productos = new ArrayList<>(); // Creo una lista vacía para almacenar los productos leídos.
        File archivo = new File(rutaArchivo); // Creo un objeto File para verificar el archivo.

        // Si el archivo no existe, retornamos lista vacía
        if (!archivo.exists()) { // Verifico si el archivo no existe usando '!'.
            return productos; // Retorno lista vacía.
        }

        // ObjectInputStream lee objetos serializados
        try (ObjectInputStream ois = new ObjectInputStream( // Try-with-resources.
                new BufferedInputStream( // Buffer para lectura eficiente.
                        new FileInputStream(rutaArchivo)))) { // Abro el archivo para lectura.

            // Lectura SECUENCIAL
            while (true) { // Bucle infinito controlado por excepción EOF.
                try {
                    Producto p = (Producto) ois.readObject(); // Leo objeto y hago casting a Producto.
                    productos.add(p); // Agrego el producto a la lista.
                } catch (EOFException eof) { // Capturo fin de archivo.
                    break; // Rompo el bucle cuando ya no hay más datos.
                }
            }
        }
        return productos; // Retorno la lista completa.
    }

    // OPERACIONES ADICIONALES 

    public void agregar(Producto producto) throws IOException, ClassNotFoundException { // Método para agregar un producto.
        List<Producto> lista = leerTodos(); // Leo todos los productos actuales.
        lista.add(producto); // Agrego el nuevo producto al final.
        guardarTodos(lista); // Reescribo todo el archivo.
    }

    public boolean eliminar(String codigo) throws IOException, ClassNotFoundException { // Método que elimina por código.
        List<Producto> lista = leerTodos(); // Leo todos los productos.
        boolean encontrado = false; // Variable bandera.
        List<Producto> nuevaLista = new ArrayList<>(); // Nueva lista sin el eliminado.

        for (Producto p : lista) { // Recorro la lista.
            if (p.getCodigo().equalsIgnoreCase(codigo)) { // Comparo ignorando mayúsculas/minúsculas.
                encontrado = true; // Marco que lo encontré.
            } else {
                nuevaLista.add(p); // Mantengo los demás productos.
            }
        }

        if (encontrado) { // Si se encontró el producto.
            guardarTodos(nuevaLista); // Reescribo el archivo sin ese producto.
        }
        return encontrado; // Retorno true o false.
    }

    public boolean actualizar(Producto productoActualizado) throws IOException, ClassNotFoundException { // Método para actualizar.
        List<Producto> lista = leerTodos(); // Leo todos los productos.
        boolean encontrado = false; // Bandera.

        for (int i = 0; i < lista.size(); i++) { // For clásico con índice.
            if (lista.get(i).getCodigo().equalsIgnoreCase(productoActualizado.getCodigo())) { // Comparo códigos.
                lista.set(i, productoActualizado); // Reemplazo el objeto en esa posición.
                encontrado = true; // Marco como encontrado.
                break; // Salgo del bucle.
            }
        }

        if (encontrado) { // Si se encontró.
            guardarTodos(lista); // Reescribo el archivo actualizado.
        }
        return encontrado; // Retorno resultado.
    }

    public Producto buscarPorCodigo(String codigo) throws IOException, ClassNotFoundException { // Método de búsqueda.
        List<Producto> lista = leerTodos(); // Leo todos.

        for (Producto p : lista) { // Búsqueda secuencial.
            if (p.getCodigo().equalsIgnoreCase(codigo)) { // Comparo códigos.
                return p; // Retorno el producto encontrado.
            }
        }
        return null; // Si no lo encuentra retorno null.
    }

    public boolean existeCodigo(String codigo) throws IOException, ClassNotFoundException { // Método que valida existencia.
        return buscarPorCodigo(codigo) != null; // Retorna true si encuentra, usando operador '!='.
    }
}