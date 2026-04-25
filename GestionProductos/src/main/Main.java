package main; // Defino el paquete 'main', donde ubico la clase principal del sistema. Uso la palabra reservada 'package'.

import vista.VentanaPrincipal; // Importo la clase VentanaPrincipal desde el paquete 'vista' para poder instanciar la interfaz gráfica. Usé 'import'.
import javax.swing.SwingUtilities; // Importo SwingUtilities para manejar correctamente los hilos en aplicaciones Swing.

 /**
  * Clase Main: punto de entrada del sistema de gestión de productos.
  * @author Cristian Perez
  * @version 2.0
  */
public class Main { // Declaro la clase pública Main. Uso 'public' y 'class'.

    /**
     * Método principal que lanza la aplicación.
     * @param args Argumentos de línea de comandos (no utilizados)
     */ // Comentario Javadoc.
    public static void main(String[] args) { // Método principal obligatorio en Java. Uso 'public', 'static', 'void' y el parámetro String[].

        SwingUtilities.invokeLater(() -> { // Llamo a invokeLater usando una expresión lambda '() ->', lo que define una tarea que se ejecutará en el EDT.
            VentanaPrincipal ventana = new VentanaPrincipal(); // Creo una instancia de la ventana principal usando 'new'.
            ventana.setVisible(true); // Hago visible la ventana en pantalla con el método setVisible(true).
        }); // Cierro la lambda y la llamada a invokeLater.
    } // Fin del método main.
}