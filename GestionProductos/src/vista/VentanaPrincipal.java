package vista;

/*
 * =============================================================================
 * IMPORTACIONES DE PAQUETES
 * =============================================================================
 * Se importan las clases necesarias para la interfaz grafica (Swing/AWT),
 * el manejo de eventos, las estructuras de datos y las clases propias del
 * sistema (modelo y archivo).
 *
 * POO - MODULARIDAD: Al separar la logica en paquetes distintos (vista,
 * modelo, archivo), se aplica el principio de separacion de responsabilidades
 * (SRP - Single Responsibility Principle), base del diseno orientado a objetos.
 * =============================================================================
 */

// Importacion de la clase de negocio encargada de gestionar productos en archivo
import archivo.GestorProductos;

// Importacion de la clase modelo que representa la entidad Producto
import modelo.Producto;

// Paquete javax.swing: componentes de la interfaz grafica de alto nivel (Swing)
import javax.swing.*;

// Bordes para paneles: vacio y con titulo
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

// Clases para personalizar la apariencia de las celdas de la tabla
import javax.swing.table.DefaultTableCellRenderer;

// Modelo de datos para la JTable: permite agregar, quitar y leer filas
import javax.swing.table.DefaultTableModel;

// Clases de AWT: layout managers y utilidades graficas
import java.awt.*;

// Clases para manejar eventos de mouse y foco de teclado
import java.awt.event.*;

// Importacion de la interfaz List para manejar colecciones de productos
import java.util.List;

/**
 * =============================================================================
 * CLASE VentanaPrincipal
 * =============================================================================
 *
 * Esta clase representa la VISTA principal del sistema de gestion de productos.
 * Es la interfaz grafica con la que interactua directamente el usuario.
 *
 * HERENCIA (extends JFrame):
 *   VentanaPrincipal hereda de JFrame, obteniendo toda la funcionalidad de
 *   una ventana de escritorio: titulo, bordes, botones de minimizar/maximizar/
 *   cerrar, y la capacidad de contener componentes Swing.
 *   Esto es un ejemplo clasico de HERENCIA en POO.
 *
 * RELACION DE AGREGACION con GestorProductos:
 *   La vista TIENE UN gestor (agregacion), pero no es duena de el.
 *   El gestor puede existir y funcionar sin que la vista exista.
 *
 * PATRON MVC SIMPLIFICADO aplicado en este proyecto:
 *   - Vista (View):       VentanaPrincipal  --> muestra datos al usuario
 *   - Modelo (Model):     Producto          --> entidad de datos
 *   - Controlador (Ctrl): GestorProductos   --> logica de negocio
 *                         ManejadorArchivo  --> acceso a archivos secuenciales
 *
 * ENCAPSULAMIENTO:
 *   Todos los atributos (campos, botones, tabla, etc.) son privados (private).
 *   Solo se exponen comportamientos a traves de metodos. Esto protege el
 *   estado interno de la clase de modificaciones externas no controladas.
 *
 * @author Cristian Perez
 * @version 1.7.2
 */
public class VentanaPrincipal extends JFrame {

    //SECCION: COMPONENTES GUI
    /*
     * Declaracion de atributos privados que representan los componentes
     * visuales de la interfaz. Al ser privados (encapsulamiento), solo
     * esta clase puede acceder y modificar su estado.
     *
     * NOTA: En Swing, todos los componentes son objetos. Esto demuestra
     * que la GUI tambien sigue los principios de POO.
     */

    /*
     * CAMPOS DEL FORMULARIO (JTextField):
     * Son los cuadros de texto donde el usuario ingresa los datos del producto.
     * JTextField hereda de JTextComponent, que a su vez hereda de JComponent.
     * Esto es un ejemplo de jerarquia de herencia en Swing.
     */
    private JTextField txtCodigo;       // Campo para ingresar el codigo del producto
    private JTextField txtNombre;       // Campo para ingresar el nombre del producto
    private JTextField txtDescripcion;  // Campo para ingresar la descripcion del producto
    private JTextField txtPrecio;       // Campo para ingresar el precio del producto
    private JTextField txtBuscar;       // Campo para ingresar el codigo de busqueda

    /*
     * BOTONES DE ACCION (JButton):
     * Cada boton dispara una accion especifica del sistema.
     * Los ActionListeners vinculan cada boton con un metodo de accion
     * (patron de diseno Observer: el boton "notifica" cuando se hace clic).
     */
    private JButton btnGuardar;         // Boton para guardar o actualizar un registro
    private JButton btnMostrar;         // Boton para recargar y mostrar todos los registros
    private JButton btnEliminar;        // Boton para eliminar el registro seleccionado
    private JButton btnEditar;          // Boton para editar el registro seleccionado
    private JButton btnBuscar;          // Boton para buscar un producto por codigo
    private JButton btnLimpiar;         // Boton para limpiar todos los campos del formulario
    private JButton btnCancelarEdicion; // Boton visible solo en modo edicion, cancela la operacion

    /*
     * TABLA DE REGISTROS (JTable + DefaultTableModel):
     * JTable es el componente avanzado de Swing para mostrar datos tabulares.
     * DECISION DE DISENO: Se usa JTable en lugar de JTextArea para lograr
     * una presentacion profesional con filas, columnas, encabezados y
     * seleccion de filas. Esto es comparable a una tabla de base de datos visual.
     *
     * DefaultTableModel es el modelo de datos de la tabla: almacena las filas
     * y columnas como una estructura de tipo matriz. Sigue el patron MVC interno
     * de Swing (el modelo de la tabla es independiente del componente visual).
     */
    private JTable tablaProductos;      // Componente visual que muestra los datos en formato tabla
    private DefaultTableModel modeloTabla; // Modelo de datos subyacente de la tabla

    /*
     * ETIQUETAS DE INFORMACION (JLabel):
     * Componentes de solo lectura que muestran texto al usuario.
     */
    private JLabel lblEstado;  // Barra de estado: muestra mensajes de retroalimentacion al usuario
    private JLabel lblTitulo;  // Etiqueta del titulo principal en el encabezado de la ventana

    //SECCION: LOGICA DE NEGOCI

    /*
     * RELACION DE AGREGACION: VentanaPrincipal usa (tiene) un GestorProductos.
     * La vista delega toda la logica de persistencia y validacion al gestor.
     * Esto separa responsabilidades: la vista SOLO se encarga de mostrar
     * datos y capturar eventos. El gestor se encarga de procesar y almacenar.
     *
     * NOTA: Si el GestorProductos fuera instanciado DENTRO de la vista
     * (composicion fuerte), la relacion seria de composicion. Al ser un
     * atributo que podria recibirse desde afuera, es agregacion.
     */
    private GestorProductos gestorProductos; // Objeto que gestiona el CRUD sobre el archivo secuencial

    /*
     * VARIABLES DE ESTADO DE EDICION:
     * Controlan si la vista esta en modo "nuevo registro" o "edicion de registro".
     * Este patron de flag booleano es comun en interfaces con formularios duales.
     */
    private boolean modoEdicion = false;  // true = formulario en modo edicion, false = modo nuevo registro
    private String codigoEnEdicion = "";  // Almacena el codigo del producto que esta siendo editado

    //SECCION: CONSTANTES DE ESTILO
    /*
     * CONSTANTES DE COLOR Y FUENTE:
     * Se declaran como static final (constantes de clase) para:
     *   1. No crear nuevos objetos Color/Font en cada uso (eficiencia de memoria)
     *   2. Centralizar los valores del tema visual (mantenibilidad)
     *   3. Garantizar consistencia visual en toda la interfaz
     *
     * "static" -> pertenecen a la clase, no a cada instancia
     * "final"  -> no pueden ser reasignadas (son constantes)
     *
     * POO - BUENA PRACTICA: Centralizar constantes evita "magic numbers" y
     * facilita cambiar el tema visual en un solo lugar.
     */

    // Color azul marino oscuro: usado en encabezados y barra de estado
    private static final Color COLOR_PRIMARIO   = new Color(25, 55, 109);

    // Color azul medio: usado en botones secundarios y textos de estado
    private static final Color COLOR_SECUNDARIO = new Color(41, 98, 180);

    // Color verde oscuro: indica operaciones exitosas
    private static final Color COLOR_EXITO      = new Color(27, 94, 32);

    // Color rojo oscuro: indica errores o advertencias criticas
    private static final Color COLOR_ERROR      = new Color(183, 28, 28);

    // Color gris azulado claro: fondo general de la ventana
    private static final Color COLOR_FONDO      = new Color(236, 239, 245);

    // Color blanco: fondo de paneles de contenido
    private static final Color COLOR_PANEL      = Color.WHITE;

    // Color negro casi puro: texto principal visible sobre fondos claros
    private static final Color COLOR_TEXTO      = new Color(20, 20, 20);

    // Fuente para el titulo principal del sistema (grande y negrita)
    private static final Font FONT_TITULO       = new Font("Arial", Font.BOLD, 20);

    // Fuente para las etiquetas del formulario (tamano normal)
    private static final Font FONT_LABEL        = new Font("Arial", Font.PLAIN, 13);

    // Fuente para el texto dentro de los campos del formulario
    private static final Font FONT_CAMPO        = new Font("Arial", Font.PLAIN, 13);

    // Fuente para el texto de los botones (negrita para mejor legibilidad)
    private static final Font FONT_BOTON        = new Font("Arial", Font.BOLD, 12);

    // Fuente para los encabezados de columna de la tabla
    private static final Font FONT_TABLA_HEADER = new Font("Arial", Font.BOLD, 13);

    // Fuente para el contenido de las celdas de la tabla
    private static final Font FONT_TABLA_CELDAS = new Font("Arial", Font.PLAIN, 13);

    /**
     * CONSTRUCTOR: VentanaPrincipal()
     *
     * El constructor es el punto de entrada para crear una instancia de esta
     * clase. En POO, el constructor inicializa el estado del objeto.
     *
     * SECUENCIA DE INICIALIZACION:
     *   1. Crea el GestorProductos (logica de negocio / acceso a archivos)
     *   2. Construye e inicializa todos los componentes graficos (initComponents)
     *   3. Configura las propiedades de la ventana JFrame (tamanio, titulo, etc.)
     *   4. Carga los datos existentes en la tabla al abrir el programa
     *
     * Esta secuencia garantiza que cuando la ventana sea visible, ya tenga
     * todos sus componentes listos y los datos actualizados.
     */
    public VentanaPrincipal() {
        // Instanciar el gestor: AGREGACION. La ventana "tiene un" gestor de productos.
        gestorProductos = new GestorProductos();

        // Construir y organizar todos los componentes graficos en la ventana
        initComponents();

        // Configurar propiedades del JFrame (tamanio, titulo, comportamiento al cerrar)
        configurarVentana();

        // Leer el archivo secuencial y mostrar los registros existentes en la tabla
        cargarTabla();
    }

    // SECCION: INICIALIZACION DE COMPONENTES
    /**
     * METODO: initComponents()
     *
     * Metodo central de construccion de la interfaz. Crea y organiza todos
     * los paneles y componentes principales usando el patron de composicion
     * de Swing: cada panel contiene otros paneles o componentes.
     *
     * PATRON DE DISENO COMPOSITE:
     *   El arbol de componentes de Swing sigue el patron Composite:
     *   - JFrame contiene paneles
     *   - Paneles contienen otros paneles o componentes
     *   - Todos se tratan uniformemente como JComponent
     *
     * LAYOUT MANAGER (BorderLayout):
     *   Divide la ventana en 5 zonas: NORTH, SOUTH, EAST, WEST, CENTER.
     *   - NORTH:  Panel de titulo (encabezado azul oscuro)
     *   - CENTER: Panel central con formulario y tabla
     *   - SOUTH:  Barra de estado
     *
     * Acceso: privado. Solo se llama desde el constructor, garantizando
     * que la GUI se construya una sola vez al inicializar el objeto.
     */
    private void initComponents() {
        // Establece BorderLayout como gestor de distribucion con espaciado de 10px
        setLayout(new BorderLayout(10, 10));

        // Aplica el color de fondo a toda la ventana
        getContentPane().setBackground(COLOR_FONDO);

        // Agrega el panel del encabezado en la zona superior (NORTH)
        add(crearPanelTitulo(), BorderLayout.NORTH);

        /*
         * Panel central: contiene el formulario (NORTH) y la tabla (CENTER).
         * Se usa un segundo BorderLayout anidado para organizar estos dos
         * sub-paneles verticalmente dentro del area central de la ventana.
         */
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBackground(COLOR_FONDO);

        // Padding externo: 5px arriba/abajo, 15px izquierda/derecha
        panelCentral.setBorder(new EmptyBorder(5, 15, 5, 15));

        // El formulario ocupa la parte superior del panel central
        panelCentral.add(crearPanelFormulario(), BorderLayout.NORTH);

        // La tabla ocupa el area restante (crece con el tamanio de la ventana)
        panelCentral.add(crearPanelTabla(),      BorderLayout.CENTER);

        // Agregar el panel central al contenedor principal de la ventana
        add(panelCentral, BorderLayout.CENTER);

        // Agregar la barra de estado en la zona inferior (SOUTH)
        add(crearBarraEstado(), BorderLayout.SOUTH);
    }

    /**
     * METODO: crearPanelTitulo()
     *
     * Construye el panel de encabezado que aparece en la parte superior
     * de la ventana. Contiene el nombre del sistema y una descripcion
     * academica del contexto del proyecto.
     *
     * DECISION DE DISENO:
     *   Se usa un JPanel con GridLayout(2,1) para los textos superpuestos
     *   (titulo grande + subtitulo pequenio), embebido dentro del panel
     *   principal con BorderLayout.WEST para alinear a la izquierda.
     *
     * @return JPanel configurado y listo para agregarse al JFrame
     */
    private JPanel crearPanelTitulo() {
        // Crear panel con BorderLayout para contener los textos del titulo
        JPanel panel = new JPanel(new BorderLayout());

        // Fondo azul oscuro (COLOR_PRIMARIO) para el area del encabezado
        panel.setBackground(COLOR_PRIMARIO);

        // Padding interno: 15px vertical, 20px horizontal
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        /*
         * lblTitulo es un atributo de instancia (no local) porque otros
         * metodos lo modifican en tiempo de ejecucion (por ejemplo, al
         * activar el modo edicion se actualiza su texto).
         */
        lblTitulo = new JLabel("Sistema de Gestion de Productos", JLabel.LEFT);
        lblTitulo.setFont(FONT_TITULO);          // Fuente grande y negrita
        lblTitulo.setForeground(Color.WHITE);    // Texto blanco sobre fondo azul oscuro

        // Etiqueta del subtitulo: solo informativa, no se modifica en tiempo de ejecucion
        JLabel lblSubtitulo = new JLabel(
                "Programacion Orientada a Objetos II  |  Archivos de Acceso Secuencial",
                JLabel.LEFT);
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 12)); // Fuente pequenoa e italica
        lblSubtitulo.setForeground(new Color(180, 200, 230));    // Azul claro para contraste suave

        /*
         * Panel interno de textos: GridLayout(2,1) ubica titulo y subtitulo
         * en dos filas de una sola columna, con 3px de espacio vertical entre ellos.
         * setOpaque(false): hace que este panel no dibuje su propio fondo,
         * permitiendo que se vea el fondo azul del panel padre.
         */
        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 3));
        textos.setOpaque(false); // Transparente: hereda el fondo del panel padre
        textos.add(lblTitulo);
        textos.add(lblSubtitulo);

        // Agregar el sub-panel de textos alineado a la izquierda del encabezado
        panel.add(textos, BorderLayout.WEST);

        return panel;
    }

    /**
     * METODO: crearPanelFormulario()
     *
     * Construye el panel que contiene el formulario de entrada de datos.
     * Incluye:
     *   - Campos de texto para codigo, nombre, descripcion y precio
     *   - Campo y boton de busqueda por codigo
     *   - Panel de botones de accion (guardar, editar, eliminar, etc.)
     *
     * LAYOUT MANAGER (GridBagLayout):
     *   Es el mas flexible de los LayoutManagers en Swing. Permite ubicar
     *   componentes en una cuadricula con pesos (weightx, weighty) que
     *   controlan como se distribuye el espacio extra al redimensionar.
     *
     * DECISION DE DISENO:
     *   Se organiza en 3 filas:
     *     Fila 0: Codigo + Nombre
     *     Fila 1: Descripcion + Precio
     *     Fila 2: Campo Buscar + Boton Buscar
     *   Cada fila tiene etiqueta (label) + campo (textfield) en pares.
     *
     * @return JPanel con el formulario completo
     */
    private JPanel crearPanelFormulario() {
        // Panel exterior con BorderLayout para separar campos (CENTER) de botones (SOUTH)
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_PANEL); // Fondo blanco para el formulario

        /*
         * Borde compuesto: linea exterior delgada gris + padding interno.
         * createCompoundBorder combina dos bordes: el exterior es la linea,
         * el interior es el espacio vacio (EmptyBorder).
         */
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 225), 1), // Borde linea gris
                new EmptyBorder(15, 20, 15, 20)  // Padding interno
        ));

        // Panel de campos con GridBagLayout para control preciso de posicionamiento
        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setBackground(COLOR_PANEL);

        /*
         * TitledBorder: borde decorativo con texto "Formulario de Registro".
         * Se crea con createEmptyBorder (sin linea visible) y se le aplica el
         * titulo en color azul marino y fuente negrita.
         * Este borde sirve como encabezado visual de la seccion del formulario.
         */
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(),        // Sin borde exterior visible
                "Formulario de Registro",                 // Texto del titulo del borde
                TitledBorder.LEFT, TitledBorder.TOP,      // Posicion: izquierda, arriba
                new Font("Arial", Font.BOLD, 13),         // Fuente del titulo
                COLOR_PRIMARIO                            // Color del titulo: azul oscuro
        );
        panelCampos.setBorder(titledBorder);

        /*
         * GridBagConstraints: objeto que define las restricciones de
         * posicionamiento para cada componente en el GridBagLayout.
         * Se reutiliza el mismo objeto modificando sus propiedades antes
         * de agregar cada componente.
         *
         * gbc.insets: margen externo alrededor de cada celda
         * gbc.fill:   como crece el componente en su celda
         */
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8); // Espaciado: 6px vertical, 8px horizontal
        gbc.fill   = GridBagConstraints.HORIZONTAL; // Componentes se expanden horizontalmente

        /*
         * FILA 0: Campos de CODIGO y NOMBRE
         * Estructura: [Label Codigo] [txtCodigo] [Label Nombre] [txtNombre]
         */

        // Columna 0: etiqueta "Codigo *:"
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; // No expande: peso 0
        panelCampos.add(crearLabel("Codigo *:"), gbc);

        // Columna 1: campo de texto para el codigo
        gbc.gridx = 1; gbc.weightx = 0.3; // Expande ligeramente: peso 0.3
        txtCodigo = crearCampo("Ej: P001"); // Metodo helper que crea campo con placeholder
        panelCampos.add(txtCodigo, gbc);

        // Columna 2: etiqueta "Nombre *:"
        gbc.gridx = 2; gbc.weightx = 0;
        panelCampos.add(crearLabel("Nombre *:"), gbc);

        // Columna 3: campo de texto para el nombre (mas ancho que codigo)
        gbc.gridx = 3; gbc.weightx = 0.7; // Expande mas que el campo codigo: peso 0.7
        txtNombre = crearCampo("Nombre del producto");
        panelCampos.add(txtNombre, gbc);

        /*
         * FILA 1: Campos de DESCRIPCION y PRECIO
         * Estructura: [Label Descripcion] [txtDescripcion] [Label Precio] [txtPrecio]
         */

        // Columna 0, Fila 1: etiqueta "Descripcion *:"
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelCampos.add(crearLabel("Descripcion *:"), gbc);

        // Columna 1, Fila 1: campo de texto para la descripcion
        gbc.gridx = 1; gbc.weightx = 0.3;
        txtDescripcion = crearCampo("Descripcion del producto");
        panelCampos.add(txtDescripcion, gbc);

        // Columna 2, Fila 1: etiqueta "Precio *:"
        gbc.gridx = 2; gbc.weightx = 0;
        panelCampos.add(crearLabel("Precio *:"), gbc);

        // Columna 3, Fila 1: campo de texto para el precio
        gbc.gridx = 3; gbc.weightx = 0.7;
        txtPrecio = crearCampo("Ej: 29.90");
        panelCampos.add(txtPrecio, gbc);

        /*
         * FILA 2: Campo y boton de BUSQUEDA POR CODIGO
         * gridwidth = 2: el boton ocupa dos columnas (columnas 2 y 3)
         * para dar mas espacio visual al boton de busqueda.
         */

        // Columna 0, Fila 2: etiqueta "Buscar codigo:"
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panelCampos.add(crearLabel("Buscar codigo:"), gbc);

        // Columna 1, Fila 2: campo de texto para el codigo a buscar
        gbc.gridx = 1; gbc.weightx = 0.3;
        txtBuscar = crearCampo("Codigo a buscar");
        panelCampos.add(txtBuscar, gbc);

        // Columnas 2-3, Fila 2: boton de busqueda que ocupa 2 columnas
        gbc.gridx = 2; gbc.weightx = 0; gbc.gridwidth = 2; // Ocupa 2 columnas
        btnBuscar = crearBoton("Buscar por Codigo", COLOR_SECUNDARIO);

        /*
         * LAMBDA (expresion lambda): forma concisa de implementar la interfaz
         * funcional ActionListener. "e -> accionBuscar()" equivale a crear
         * una clase anonima que implementa actionPerformed llamando a accionBuscar().
         * Disponible desde Java 8.
         */
        btnBuscar.addActionListener(e -> accionBuscar());
        panelCampos.add(btnBuscar, gbc);

        // Restaurar gridwidth a 1 para no afectar componentes siguientes
        gbc.gridwidth = 1;

        // Agregar el panel de campos en el centro del panel de formulario
        panel.add(panelCampos,      BorderLayout.CENTER);

        // Agregar el panel de botones de accion en la parte inferior del formulario
        panel.add(crearPanelBotones(), BorderLayout.SOUTH);

        return panel;
    }

    /**
     * METODO: crearPanelBotones()
     *
     * Construye el panel con los botones principales de accion del sistema:
     * Guardar, Mostrar, Editar, Eliminar, Limpiar y Cancelar Edicion.
     *
     * PATRON OBSERVER (Listeners):
     *   Cada boton registra un ActionListener mediante expresiones lambda.
     *   Cuando el usuario hace clic, el EventDispatchThread (EDT) de Swing
     *   notifica al listener y ejecuta el metodo de accion correspondiente.
     *   Este es el patron Observer aplicado a la interfaz grafica.
     *
     * DECISION DE DISENO - btnCancelarEdicion:
     *   Este boton se crea como invisible (setVisible(false)) porque solo
     *   debe mostrarse cuando la vista entra en modo edicion. Esto evita
     *   confundir al usuario con botones inutiles en el estado normal.
     *
     * FlowLayout.CENTER: distribuye los botones centrados con espaciado uniforme.
     *
     * @return JPanel con todos los botones de accion configurados
     */
    private JPanel crearPanelBotones() {
        // FlowLayout centra los botones con 10px horizontal y 8px vertical de separacion
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        panel.setBackground(COLOR_PANEL);

        /*
         * BOTON GUARDAR: color verde oscuro indica accion primaria constructiva.
         * Su listener llama a accionGuardar() que maneja tanto nuevos registros
         * como actualizaciones (segun el estado de modoEdicion).
         */
        btnGuardar = crearBoton("Guardar", new Color(27, 94, 32)); // Verde oscuro
        btnGuardar.addActionListener(e -> accionGuardar());

        /*
         * BOTON MOSTRAR: color azul secundario, accion de consulta.
         * Recarga la tabla leyendo secuencialmente el archivo de datos.
         */
        btnMostrar = crearBoton("Mostrar Registros", COLOR_SECUNDARIO);
        btnMostrar.addActionListener(e -> cargarTabla());

        /*
         * BOTON EDITAR: color naranja, indica modificacion de datos existentes.
         * Requiere que haya una fila seleccionada en la tabla.
         */
        btnEditar = crearBoton("Editar Seleccionado", new Color(180, 95, 0)); // Naranja oscuro
        btnEditar.addActionListener(e -> accionEditar());

        /*
         * BOTON ELIMINAR: color rojo, indica accion destructiva (con confirmacion).
         * Requiere que haya una fila seleccionada en la tabla.
         */
        btnEliminar = crearBoton("Eliminar Seleccionado", new Color(183, 28, 28)); // Rojo oscuro
        btnEliminar.addActionListener(e -> accionEliminar());

        /*
         * BOTON LIMPIAR: color gris, accion neutral de reset del formulario.
         * No modifica datos: solo limpia los campos de entrada.
         */
        btnLimpiar = crearBoton("Limpiar Formulario", new Color(80, 80, 80)); // Gris oscuro
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        /*
         * BOTON CANCELAR EDICION: color rojo bordo, solo visible en modo edicion.
         * Se inicializa oculto (setVisible(false)) y se hace visible al entrar
         * en modo edicion. Al hacer clic, cancela los cambios y restaura el
         * formulario al estado inicial.
         */
        btnCancelarEdicion = crearBoton("Cancelar Edicion", new Color(120, 50, 50)); // Rojo bordo
        btnCancelarEdicion.addActionListener(e -> cancelarEdicion());
        btnCancelarEdicion.setVisible(false); // Oculto por defecto, visible solo en modo edicion

        // Agregar todos los botones al panel en el orden deseado
        panel.add(btnGuardar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnMostrar);
        panel.add(btnLimpiar);
        panel.add(btnCancelarEdicion); // Este boton solo aparece en modo edicion

        return panel;
    }

    /**
     * METODO: crearPanelTabla()
     *
     * Construye el panel que contiene la JTable para la visualizacion
     * de todos los registros almacenados en el archivo secuencial.
     *
     * DECISION DE DISENO - JTable vs JTextArea:
     *   Se eligio JTable (nivel avanzado) en lugar de JTextArea simple porque:
     *   - Permite organizacion tabular con columnas y encabezados
     *   - Soporta seleccion de filas para operaciones de edicion/eliminacion
     *   - Permite renderizadores personalizados para colores alternados
     *   - Es la representacion estandar profesional de datos tabulares
     *
     * INNER CLASS ANONIMA - DefaultTableModel:
     *   Se sobreescribe el metodo isCellEditable() para devolver siempre false,
     *   haciendo que la tabla sea de SOLO LECTURA. Esto protege la integridad
     *   de los datos: el usuario no puede editar directamente en la celda,
     *   sino que debe usar el formulario con sus validaciones.
     *
     * RENDERER DE CELDAS - DefaultTableCellRenderer:
     *   PROBLEMA resuelto: en algunos sistemas operativos, el Look and Feel
     *   nativo sobreescribe el color del texto a blanco en celdas seleccionadas,
     *   haciendo el texto invisible. La solucion es un renderer personalizado
     *   que fuerza el color de texto en TODOS los estados (seleccionado y normal).
     *
     * EVENTO MouseListener (doble clic):
     *   Al hacer doble clic en una fila, se carga esa fila en el formulario.
     *   Esto es una forma de interaccion directa que mejora la usabilidad.
     *
     * @return JPanel con la tabla de productos configurada
     */
    private JPanel crearPanelTabla() {
        // Panel contenedor con BorderLayout para organizar titulo, tabla y ayuda
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PANEL);

        // Borde similar al formulario: linea exterior + padding interno
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 225), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        // Etiqueta de titulo de la seccion de la tabla
        JLabel lblTabla = new JLabel("Registros Almacenados en Archivo Secuencial", JLabel.LEFT);
        lblTabla.setFont(new Font("Arial", Font.BOLD, 13));
        lblTabla.setForeground(COLOR_PRIMARIO);             // Azul oscuro para consistencia
        lblTabla.setBorder(new EmptyBorder(0, 0, 8, 0));    // Espacio inferior antes de la tabla
        panel.add(lblTabla, BorderLayout.NORTH);

        /*
         * DEFINICION DE COLUMNAS DE LA TABLA:
         * El array de Strings define los encabezados de las 5 columnas.
         * Corresponden a: numero de fila, y los 4 atributos del objeto Producto.
         */
        String[] columnas = {"N", "Codigo", "Nombre", "Descripcion", "Precio (S/.)"};

        /*
         * INNER CLASS ANONIMA de DefaultTableModel:
         * Se usa una clase anonima para sobreescribir isCellEditable().
         * Al retornar siempre false, se bloquea la edicion directa en celdas.
         * ENCAPSULAMIENTO: el usuario solo puede modificar datos a traves
         * del formulario controlado, que incluye validaciones.
         */
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Tabla de solo lectura: la edicion se hace desde el formulario
                return false; // Ningun campo de la tabla es editable directamente
            }
        };

        // Crear la JTable usando el modelo de datos personalizado
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setFont(FONT_TABLA_CELDAS);                           // Fuente de celdas
        tablaProductos.setRowHeight(28);                                     // Altura de cada fila en pixeles
        tablaProductos.setGridColor(new Color(220, 225, 235));               // Color de la cuadricula
        tablaProductos.setShowVerticalLines(true);                           // Mostrar lineas verticales
        tablaProductos.setShowHorizontalLines(true);                         // Mostrar lineas horizontales
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo una fila seleccionada a la vez

        /*
         * RENDERER PERSONALIZADO DE CELDAS
         * PROBLEMA A RESOLVER: En algunos Look and Feel de Windows/Linux, el
         * color del texto en celdas seleccionadas se vuelve blanco, haciendo
         * el texto invisible contra el fondo blanco.
         *
         * SOLUCION: Crear un DefaultTableCellRenderer anonimo que sobreescribe
         * getTableCellRendererComponent() para controlar manualmente los colores
         * de fondo y texto en TODOS los estados de la celda.
         *
         * HERENCIA Y POLIMORFISMO:
         *   DefaultTableCellRenderer implementa TableCellRenderer.
         *   Al sobreescribir getTableCellRendererComponent, se aplica
         *   polimorfismo: la JTable llama a ESTE renderer, no al original.
         *
         * FILAS ALTERNADAS (zebra striping):
         *   Las filas pares tienen fondo blanco puro y las impares tienen
         *   un gris muy claro (245, 248, 253). Esto mejora la legibilidad
         *   cuando hay muchas filas de datos.
         */
        DefaultTableCellRenderer rendererCeldas = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                // Paso 1: Llamar al renderer padre para inicializar el componente base
                // (establece texto, bordes por defecto, etc.)
                super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    /*
                     * ESTADO SELECCIONADO:
                     * Fondo azul claro (197, 220, 255) para indicar seleccion activa.
                     * Texto azul oscuro (COLOR_PRIMARIO) para contraste sobre azul claro.
                     * Esto da retroalimentacion visual clara al usuario de cual fila
                     * esta seleccionada.
                     */
                    setBackground(new Color(197, 220, 255)); // Azul claro de seleccion
                    setForeground(COLOR_PRIMARIO);           // Texto azul oscuro sobre azul claro
                } else {
                    /*
                     * ESTADO NORMAL (no seleccionado):
                     * Filas pares (0, 2, 4...): fondo blanco puro (Color.WHITE)
                     * Filas impares (1, 3, 5...): fondo gris muy claro (245, 248, 253)
                     * Esto crea el efecto "zebra striping" para facilitar la lectura.
                     */
                    if (row % 2 == 0) {
                        setBackground(Color.WHITE);               // Filas pares: blanco
                    } else {
                        setBackground(new Color(245, 248, 253));  // Filas impares: gris muy claro
                    }
                    // TEXTO NEGRO en todos los casos normales - clave para visibilidad
                    setForeground(COLOR_TEXTO); // Negro casi puro (20, 20, 20)
                }

                // Padding interno de la celda: 6px horizontal para espaciar el texto de los bordes
                setBorder(new EmptyBorder(0, 6, 0, 6));
                return this; // Retornar el componente JLabel configurado
            }
        };

        /*
         * Aplicar el renderer personalizado a TODAS las columnas de la tabla.
         * Se itera sobre cada columna para asegurarse de que ningun look and
         * feel del sistema sobreescriba el renderer.
         */
        for (int col = 0; col < columnas.length; col++) {
            tablaProductos.getColumnModel().getColumn(col)
                    .setCellRenderer(rendererCeldas); // Asignar renderer personalizado a cada columna
        }

        /*
         * RENDERER DEL ENCABEZADO (JTableHeader)
         * Similar al renderer de celdas, pero aplicado a la fila de encabezados.
         * Garantiza que el encabezado sea siempre azul oscuro con texto blanco,
         * independientemente del Look and Feel del sistema operativo.
         *
         * setHorizontalAlignment(JLabel.CENTER): centra el texto del encabezado.
         */
        tablaProductos.getTableHeader().setFont(FONT_TABLA_HEADER);          // Fuente negrita para encabezados
        tablaProductos.getTableHeader().setBackground(COLOR_PRIMARIO);       // Fondo azul oscuro
        tablaProductos.getTableHeader().setForeground(Color.WHITE);          // Texto blanco
        tablaProductos.getTableHeader().setOpaque(true);                     // Dibuja su propio fondo
        tablaProductos.getTableHeader().setPreferredSize(new Dimension(0, 36)); // Altura del encabezado: 36px

        // Renderer personalizado del encabezado de columnas
        DefaultTableCellRenderer rendererHeader = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                // Inicializar componente base del renderer de encabezado
                super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                setBackground(COLOR_PRIMARIO);          // Siempre azul oscuro
                setForeground(Color.WHITE);             // Siempre texto blanco
                setFont(FONT_TABLA_HEADER);             // Fuente negrita
                setHorizontalAlignment(JLabel.CENTER);  // Texto centrado horizontalmente
                setBorder(new EmptyBorder(4, 6, 4, 6)); // Padding: 4px vertical, 6px horizontal
                return this;
            }
        };

        // Aplicar renderer de encabezado a todas las columnas
        for (int col = 0; col < columnas.length; col++) {
            tablaProductos.getColumnModel().getColumn(col)
                    .setHeaderRenderer(rendererHeader); // Renderer de encabezado personalizado
        }

        /*
         * CONFIGURACION DE ANCHOS DE COLUMNAS:
         * Se asignan anchos preferidos y fijos segun el tipo de datos.
         * La columna "N" (numero de fila) es estrecha y fija.
         * La columna "Descripcion" es la mas ancha porque puede contener mas texto.
         *
         * setMaxWidth / setMinWidth: dimensiones absolutas (columna N)
         * setPreferredWidth: tamanio sugerido (las demas columnas)
         */
        tablaProductos.getColumnModel().getColumn(0).setMaxWidth(50);         // Columna N: maximo 50px
        tablaProductos.getColumnModel().getColumn(0).setMinWidth(40);         // Columna N: minimo 40px
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(90);   // Columna Codigo: 90px
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(180);  // Columna Nombre: 180px
        tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(280);  // Columna Descripcion: 280px
        tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(110);  // Columna Precio: 110px

        /*
         * EVENTO DE DOBLE CLIC EN TABLA:
         * Se agrega un MouseListener (clase anonima) a la tabla.
         * Solo responde al doble clic (getClickCount() == 2).
         * Al hacer doble clic en una fila, se llama a cargarFilaEnFormulario()
         * para transferir los datos de la fila al formulario y activar el
         * modo edicion de forma rapida e intuitiva.
         *
         * MouseAdapter es una clase adaptadora que implementa MouseListener
         * con cuerpos vacios. Al extenderla, solo se sobreescriben los
         * metodos necesarios (en este caso, solo mouseClicked).
         */
        tablaProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Verificar que fue doble clic
                    cargarFilaEnFormulario();  // Cargar la fila seleccionada en el formulario
                }
            }
        });

        /*
         * JScrollPane: contenedor que agrega barras de desplazamiento automaticas
         * a la tabla cuando hay mas filas que las que caben en la pantalla.
         * DECISION DE DISENO: sin JScrollPane, los registros que excedan la
         * altura visible simplemente no se mostrarian.
         */
        JScrollPane scroll = new JScrollPane(tablaProductos);
        scroll.setPreferredSize(new Dimension(0, 250)); // Altura preferida del area de la tabla
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 225))); // Borde gris suave
        panel.add(scroll, BorderLayout.CENTER); // La tabla ocupa el area central del panel

        // Etiqueta de ayuda para el usuario: instrucciones de uso de la tabla
        JLabel lblAyuda = new JLabel(
                "Doble clic en una fila para cargar sus datos en el formulario y editarlos.",
                JLabel.LEFT);
        lblAyuda.setFont(new Font("Arial", Font.ITALIC, 11)); // Fuente italica y pequenoa
        lblAyuda.setForeground(new Color(100, 100, 120));     // Color gris azulado suave
        lblAyuda.setBorder(new EmptyBorder(6, 0, 0, 0));      // Espacio superior para separar de la tabla
        panel.add(lblAyuda, BorderLayout.SOUTH);              // Ubicar debajo de la tabla

        return panel;
    }

    /**
     * METODO: crearBarraEstado()
     *
     * Construye la barra de estado que aparece en la parte inferior de la
     * ventana. Muestra mensajes de retroalimentacion al usuario sobre el
     * resultado de cada operacion (exito, error, info).
     *
     * PATRON DE RETROALIMENTACION:
     *   La barra de estado es una herramienta de UX (experiencia de usuario)
     *   que informa al usuario sobre el estado actual del sistema sin
     *   interrumpir el flujo de trabajo con ventanas emergentes (JOptionPane).
     *   Los mensajes de la barra complementan a los dialogos emergentes.
     *
     * lblEstado es un atributo de instancia porque se actualiza desde
     * el metodo mostrarEstado() en tiempo de ejecucion.
     *
     * @return JPanel con la barra de estado configurada
     */
    private JPanel crearBarraEstado() {
        // Panel con BorderLayout para ubicar el mensaje (WEST) y la version (EAST)
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PRIMARIO); // Mismo azul oscuro que el encabezado
        panel.setBorder(new EmptyBorder(6, 15, 6, 15)); // Padding interno

        /*
         * lblEstado: etiqueta dinamica que muestra el ultimo mensaje de estado.
         * Se inicializa con el mensaje de bienvenida/instruccion inicial.
         * Este es un atributo de instancia para ser accesible desde mostrarEstado().
         */
        lblEstado = new JLabel(
                "Sistema listo. Ingrese los datos y presione Guardar.",
                JLabel.LEFT); // Alineacion izquierda
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 12));
        lblEstado.setForeground(new Color(200, 215, 235)); // Azul muy claro sobre fondo oscuro
        panel.add(lblEstado, BorderLayout.WEST); // Mensaje a la izquierda

        // Etiqueta de version del sistema: solo informativa, no cambia en tiempo de ejecucion
        JLabel lblVersion = new JLabel(
                "v1.7.2 | Cris Perez |  POO II  |  UNT ",
                JLabel.RIGHT); // Alineacion derecha
        lblVersion.setFont(new Font("Arial", Font.PLAIN, 11));
        lblVersion.setForeground(new Color(150, 170, 200)); // Gris azulado suave
        panel.add(lblVersion, BorderLayout.EAST); // Version a la derecha

        return panel;
    }

    //SECCION: HELPERS DE COMPONENTES
    /*
     * Metodos auxiliares (helpers) para crear componentes con estilo
     * estandarizado. Estos metodos siguen el PRINCIPIO DRY (Don't Repeat
     * Yourself): en lugar de repetir la misma configuracion de estilo en
     * cada componente, se centraliza en metodos reutilizables.
     *
     * Todos son privados porque son de uso interno de la clase.
     */

    /**
     * METODO: crearLabel(String texto)
     *
     * Metodo factory simple para crear JLabel con el estilo estandar del
     * sistema. Centraliza la configuracion visual de todas las etiquetas
     * del formulario para mantener consistencia.
     *
     * PRINCIPIO DRY: sin este metodo, la misma configuracion de fuente y
     * color tendria que repetirse para cada etiqueta del formulario.
     *
     * JLabel.RIGHT: alinea el texto a la derecha para que quede junto al
     * campo de texto al que precede (estetica de formulario estandar).
     *
     * @param texto Texto a mostrar en la etiqueta
     * @return JLabel configurado con el estilo estandar del sistema
     */
    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto, JLabel.RIGHT); // Texto alineado a la derecha
        lbl.setFont(FONT_LABEL);                      // Fuente estandar de etiquetas
        lbl.setForeground(new Color(50, 60, 80));     // Color gris azulado oscuro
        return lbl;
    }

    /**
     * METODO: crearCampo(String placeholder)
     *
     * Crea un JTextField con comportamiento de TEXTO PLACEHOLDER (texto de
     * ayuda que desaparece al hacer clic en el campo y reaparece si se deja
     * vacio al perder el foco).
     *
     * IMPLEMENTACION DEL PLACEHOLDER:
     *   JTextField de Java no tiene soporte nativo para placeholder, por lo
     *   que se simula mediante un FocusListener:
     *   - focusGained: si el campo tiene el texto del placeholder, se borra
     *     y el color del texto cambia a negro (color de escritura real).
     *   - focusLost: si el campo queda vacio, se restaura el texto placeholder
     *     en color gris para indicar que es ayuda visual, no contenido real.
     *
     * PATRON OBSERVER: FocusAdapter es otra aplicacion del patron Observer.
     *   El campo de texto "observa" eventos de foco y reacciona ante ellos.
     *
     * FocusAdapter: clase adaptadora que implementa FocusListener con
     * cuerpos vacios, similar a MouseAdapter.
     *
     * @param placeholder Texto de ayuda que se muestra cuando el campo esta vacio
     * @return JTextField configurado con placeholder y estilo estandar
     */
    private JTextField crearCampo(String placeholder) {
        JTextField campo = new JTextField();
        campo.setFont(FONT_CAMPO);                       // Fuente de texto
        campo.setForeground(new Color(150, 150, 160));  // Gris: indica que es texto placeholder
        campo.setText(placeholder);                      // Inicializar con texto de ayuda
        campo.setPreferredSize(new Dimension(180, 32));  // Tamanio preferido: 180px ancho, 32px alto

        /*
         * Borde compuesto:
         *   - Exterior: linea delgada gris azulado (marco del campo)
         *   - Interior: EmptyBorder de 8px horizontal para separar el texto del borde
         */
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 195, 220)), // Marco gris-azulado
                new EmptyBorder(3, 8, 3, 8)                               // Padding interno
        ));

        /*
         * FocusListener para simular comportamiento de placeholder.
         * Se usa FocusAdapter (clase abstracta con implementacion vacia)
         * para solo sobreescribir los metodos necesarios.
         */
        campo.addFocusListener(new FocusAdapter() {

            /**
             * Se ejecuta cuando el campo RECIBE el foco (usuario hace clic o usa Tab).
             * Si el campo contiene el texto placeholder, lo borra y cambia el color
             * del texto a negro para indicar que ahora se puede escribir.
             */
            @Override
            public void focusGained(FocusEvent e) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");                  // Borrar texto placeholder
                    campo.setForeground(COLOR_TEXTO);  // Cambiar a negro para escritura real
                }
            }

            /**
             * Se ejecuta cuando el campo PIERDE el foco (usuario hace clic en otro lugar).
             * Si el campo quedo vacio, restaura el texto placeholder en color gris.
             * Esto le indica al usuario que el campo esta vacio y cual es su proposito.
             */
            @Override
            public void focusLost(FocusEvent e) {
                if (campo.getText().isEmpty()) {
                    campo.setText(placeholder);                  // Restaurar placeholder
                    campo.setForeground(new Color(150, 150, 160)); // Color gris de placeholder
                }
            }
        });

        return campo;
    }

    /**
     * METODO: crearBoton(String texto, Color color)
     *
     * Metodo factory que crea JButton con estilo visual profesional y
     * efecto hover (oscurecimiento al pasar el mouse).
     *
     * EFECTO HOVER:
     *   Al pasar el mouse sobre el boton, su color de fondo se oscurece
     *   ligeramente usando color.darker(). Al salir el mouse, vuelve al
     *   color original. Esto mejora la retroalimentacion visual (UX).
     *
     * CONFIGURACION DE BOTON PLANO:
     *   - setFocusPainted(false): elimina el recuadro punteado de foco
     *   - setBorderPainted(false): elimina el borde nativo del boton
     *   - setOpaque(true): garantiza que el fondo personalizado sea visible
     *   Estos ajustes son necesarios para que el color de fondo personalizado
     *   se vea correctamente en diferentes Look and Feel.
     *
     * CURSOR DE MANO (HAND_CURSOR):
     *   Al posicionar el mouse sobre el boton, el cursor cambia a manita,
     *   indicando al usuario que es un elemento clickeable.
     *
     * @param texto Texto que muestra el boton
     * @param color Color de fondo base del boton
     * @return JButton configurado con estilo y efecto hover
     */
    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(FONT_BOTON);                                    // Fuente negrita para botones
        btn.setBackground(color);                                   // Color de fondo personalizado
        btn.setForeground(Color.WHITE);                             // Texto blanco sobre fondo de color
        btn.setFocusPainted(false);                                 // Sin recuadro de foco (aspecto limpio)
        btn.setBorderPainted(false);                                // Sin borde nativo (aspecto plano)
        btn.setOpaque(true);                                        // Dibuja su propio fondo (necesario para colores)
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));              // Cursor de mano al pasar el mouse
        btn.setPreferredSize(new Dimension(180, 36));               // Tamanio estandar de botones

        /*
         * EFECTO HOVER mediante MouseAdapter.
         * colorHover = color.darker() crea una version mas oscura del color base.
         * Se captura en variable local final para ser accesible desde la clase anonima.
         *
         * mouseEntered: oscurecer al entrar el mouse
         * mouseExited:  restaurar al salir el mouse
         */
        Color colorHover = color.darker(); // Version mas oscura del color para el efecto hover

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(colorHover); // Oscurecer al pasar el mouse (efecto hover)
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color); // Restaurar color original al salir el mouse
            }
        });

        return btn;
    }

    //SECCION: CONFIGURACION DE VENTANA

    /**
     * METODO: configurarVentana()
     *
     * Configura las propiedades generales del JFrame principal:
     * titulo, tamanio, comportamiento al cerrar, y posicion en pantalla.
     *
     * HERENCIA APLICADA:
     *   Todos los metodos llamados aqui (setTitle, setDefaultCloseOperation,
     *   etc.) son metodos HEREDADOS de JFrame, los cuales a su vez los hereda
     *   de Window > Container > Component.
     *   VentanaPrincipal los usa directamente sin necesidad de "super." porque
     *   hereda todos los miembros publicos y protegidos.
     *
     * setLocationRelativeTo(null): centra la ventana en la pantalla.
     *   Cuando se pasa null, la ventana se centra respecto a la pantalla completa.
     *
     * setMinimumSize(): evita que el usuario reduzca la ventana a un tamanio
     *   tan pequenio que los componentes queden ilegibles o invisibles.
     */
    private void configurarVentana() {
        // Titulo que aparece en la barra de titulo del sistema operativo
        setTitle("Sistema de Gestion de Productos  |  POO II");

        /*
         * EXIT_ON_CLOSE: cuando el usuario cierra la ventana, la JVM termina.
         * Otras opciones: DISPOSE_ON_CLOSE (solo cierra la ventana),
         * HIDE_ON_CLOSE (la oculta), DO_NOTHING_ON_CLOSE (no hace nada).
         */
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Tamanio inicial de la ventana: 950px ancho x 680px alto
        setSize(950, 680);

        // Tamanio minimo permitido al redimensionar: evita que se vea mal
        setMinimumSize(new Dimension(800, 600));

        // Centrar la ventana en la pantalla del usuario
        setLocationRelativeTo(null);

        // Permitir al usuario redimensionar la ventana libremente
        setResizable(true);
    }

    // SECCION: ACCIONES DE BOTONES
    /*
     * Metodos de accion: cada uno corresponde a un boton de la interfaz.
     * Siguen el patron de manejo de eventos de Swing:
     *   1. Leer y validar la entrada del usuario
     *   2. Delegar la operacion al gestor (logica de negocio)
     *   3. Evaluar el resultado
     *   4. Mostrar retroalimentacion al usuario (barra de estado + dialogo)
     *   5. Actualizar la vista (recargar tabla si fue necesario)
     *
     * SEPARACION DE CAPAS (MVC):
     *   La vista (VentanaPrincipal) NO contiene logica de negocio ni acceso
     *   a archivos. Solo delega al gestor y muestra resultados.
     */

    /**
     * METODO: accionGuardar()
     *
     * Accion ejecutada al presionar el boton "Guardar" (o "Actualizar Registro"
     * en modo edicion).
     *
     * FLUJO DE OPERACION:
     *   1. Leer los valores reales de cada campo del formulario
     *   2. Validar que ningun campo este vacio (primera capa de validacion)
     *   3. Segun el estado modoEdicion:
     *      - false: registrar nuevo producto (crear en archivo)
     *      - true:  actualizar producto existente (reescribir en archivo)
     *   4. Evaluar el String resultado que retorna el gestor
     *   5. Mostrar mensaje de exito o error al usuario
     *   6. Limpiar formulario y recargar tabla si fue exitoso
     *
     * VALIDACION EN CAPAS:
     *   - Capa 1 (Vista): valida que los campos no esten vacios
     *   - Capa 2 (Gestor): valida formato del precio, codigo duplicado, etc.
     *   La vista solo hace validacion superficial; la logica profunda es del gestor.
     *
     * MODO EDICION vs MODO NUEVO:
     *   modoEdicion == false -> gestorProductos.registrarProducto()
     *   modoEdicion == true  -> gestorProductos.actualizarProducto()
     *   El mismo boton maneja ambos casos usando polimorfismo de control (if/else).
     */
    private void accionGuardar() {
        /*
         * Leer los valores reales de cada campo, ignorando los textos placeholder.
         * obtenerTextoReal() retorna "" si el campo solo contiene el placeholder.
         */
        String codigo      = obtenerTextoReal(txtCodigo,      "Ej: P001");
        String nombre      = obtenerTextoReal(txtNombre,      "Nombre del producto");
        String descripcion = obtenerTextoReal(txtDescripcion, "Descripcion del producto");
        String precio      = obtenerTextoReal(txtPrecio,      "Ej: 29.90");

        /*
         * PRIMERA CAPA DE VALIDACION (en la Vista):
         * Verifica que todos los campos tengan contenido real.
         * Si alguno esta vacio, se muestra error y se aborta la operacion.
         * DECISION: validar en la vista evita llamadas innecesarias al gestor
         * cuando el formulario esta claramente incompleto.
         */
        if (codigo.isEmpty() || nombre.isEmpty()
                || descripcion.isEmpty() || precio.isEmpty()) {

            // Mostrar mensaje de error en barra de estado
            mostrarEstado("ERROR: Todos los campos son obligatorios.", COLOR_ERROR);

            // Mostrar dialogo de advertencia con informacion adicional
            JOptionPane.showMessageDialog(this,
                    "Por favor complete todos los campos marcados con *",
                    "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return; // Abortar operacion: salir del metodo sin guardar
        }

        /*
         * DELEGACION AL GESTOR:
         * Segun el estado modoEdicion, se llama al metodo correspondiente.
         * El gestor retorna un String que describe el resultado de la operacion.
         * La vista evalua ese String para decidir si mostrar exito o error.
         *
         * SEPARACION DE RESPONSABILIDADES:
         *   La vista no sabe COMO se guarda o actualiza. Solo sabe QUE hacer
         *   segun el resultado. La logica de acceso a archivos esta en el gestor.
         */
        String resultado;
        if (modoEdicion) {
            // MODO EDICION: actualizar registro existente en el archivo secuencial
            resultado = gestorProductos.actualizarProducto(
                    codigo, nombre, descripcion, precio);
        } else {
            // MODO NUEVO: registrar un nuevo producto en el archivo secuencial
            resultado = gestorProductos.registrarProducto(
                    codigo, nombre, descripcion, precio);
        }

        /*
         * EVALUACION DEL RESULTADO:
         * El gestor retorna Strings que contienen palabras clave para indicar
         * el resultado. "EXITO" o "correctamente" indican operacion exitosa.
         * Cualquier otro String indica un error.
         */
        if (resultado.startsWith("EXITO") || resultado.contains("correctamente")) {
            // Operacion exitosa: mostrar confirmacion
            mostrarEstado("OK: " + resultado, COLOR_EXITO); // Verde en barra de estado

            JOptionPane.showMessageDialog(this,
                    resultado, "Operacion exitosa",
                    JOptionPane.INFORMATION_MESSAGE); // Dialogo informativo

            limpiarFormulario(); // Limpiar el formulario para el siguiente registro
            cargarTabla();       // Recargar la tabla para reflejar el cambio

        } else {
            // Operacion fallida: mostrar error al usuario
            mostrarEstado("ERROR: " + resultado, COLOR_ERROR); // Rojo en barra de estado

            JOptionPane.showMessageDialog(this,
                    resultado, "Error al guardar",
                    JOptionPane.ERROR_MESSAGE); // Dialogo de error
        }
    }

    /**
     * METODO: accionEditar()
     *
     * Accion ejecutada al presionar el boton "Editar Seleccionado".
     * Verifica que el usuario haya seleccionado una fila en la tabla antes
     * de activar el modo edicion. Si no hay seleccion, muestra una advertencia.
     *
     * getSelectedRow(): retorna el indice de la fila seleccionada, o -1 si
     * no hay ninguna fila seleccionada. Este es el patron de verificacion
     * estandar para operaciones en JTable.
     *
     * Si hay seleccion, delega a cargarFilaEnFormulario() que activa el
     * modo edicion y transfiere los datos a los campos del formulario.
     */
    private void accionEditar() {
        // Obtener el indice de la fila seleccionada (-1 si no hay seleccion)
        int fila = tablaProductos.getSelectedRow();

        if (fila == -1) {
            // No hay fila seleccionada: informar al usuario
            JOptionPane.showMessageDialog(this,
                    "Seleccione un registro de la tabla para editar.\n"
                    + "(Haga clic en una fila primero)",
                    "Sin seleccion", JOptionPane.WARNING_MESSAGE);
            return; // Salir sin hacer nada
        }

        // Hay una fila seleccionada: cargar sus datos en el formulario
        cargarFilaEnFormulario();
    }

    /**
     * METODO: accionEliminar()
     *
     * Accion ejecutada al presionar el boton "Eliminar Seleccionado".
     * Implementa un flujo de seguridad en dos pasos:
     *   1. Verificar que haya una fila seleccionada
     *   2. Pedir confirmacion explica al usuario (dialogo SI/NO)
     *   3. Ejecutar la eliminacion solo si el usuario confirma
     *
     * PATRON DE CONFIRMACION:
     *   Las operaciones destructivas (eliminar, sobrescribir) siempre deben
     *   pedir confirmacion. Esto previene perdidas accidentales de datos.
     *   JOptionPane.showConfirmDialog() muestra un dialogo con opciones
     *   SI/NO y retorna JOptionPane.YES_OPTION si el usuario confirma.
     *
     * ELIMINACION EN ARCHIVO SECUENCIAL:
     *   La eliminacion en archivos secuenciales es compleja: se debe
     *   reescribir todo el archivo omitiendo el registro eliminado.
     *   Esta logica esta encapsulada en el gestor, no en la vista.
     */
    private void accionEliminar() {
        // Verificar que haya una fila seleccionada
        int fila = tablaProductos.getSelectedRow();

        if (fila == -1) {
            // No hay seleccion: mostrar advertencia
            JOptionPane.showMessageDialog(this,
                    "Seleccione un registro de la tabla para eliminar.\n"
                    + "(Haga clic en una fila primero)",
                    "Sin seleccion", JOptionPane.WARNING_MESSAGE);
            return; // Salir sin hacer nada
        }

        // Obtener el codigo y nombre del producto a eliminar desde el modelo de la tabla
        String codigo = (String) modeloTabla.getValueAt(fila, 1); // Columna 1: Codigo
        String nombre = (String) modeloTabla.getValueAt(fila, 2); // Columna 2: Nombre

        /*
         * DIALOGO DE CONFIRMACION:
         * Muestra el codigo y nombre del producto a eliminar para que el
         * usuario pueda verificar que es el correcto antes de confirmar.
         * showConfirmDialog retorna YES_OPTION (0) si el usuario hace clic en "Si".
         */
        int confirmar = JOptionPane.showConfirmDialog(this,
                "Esta seguro de eliminar el siguiente producto?\n\n"
                + "Codigo: " + codigo + "\n"
                + "Nombre: " + nombre,
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION,      // Opciones: Si / No
                JOptionPane.WARNING_MESSAGE);   // Icono de advertencia

        if (confirmar == JOptionPane.YES_OPTION) {
            // Usuario confirmo: ejecutar la eliminacion delegando al gestor
            String resultado = gestorProductos.eliminarProducto(codigo);

            if (resultado.contains("correctamente") || resultado.contains("eliminado")) {
                // Eliminacion exitosa
                mostrarEstado("OK: " + resultado, COLOR_EXITO);

                JOptionPane.showMessageDialog(this,
                        resultado, "Registro eliminado",
                        JOptionPane.INFORMATION_MESSAGE);

                cargarTabla(); // Recargar tabla para reflejar la eliminacion

            } else {
                // Error al eliminar
                mostrarEstado("ERROR: " + resultado, COLOR_ERROR);

                JOptionPane.showMessageDialog(this,
                        resultado, "Error al eliminar",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        // Si el usuario elige "No", se sale del metodo sin hacer nada
    }

    /**
     * METODO: accionBuscar()
     *
     * Accion ejecutada al presionar el boton "Buscar por Codigo".
     * Realiza una BUSQUEDA SECUENCIAL en el archivo a traves del gestor.
     * Si se encuentra el producto, resalta su fila en la tabla.
     *
     * BUSQUEDA SECUENCIAL:
     *   En archivos de acceso secuencial, la busqueda recorre los registros
     *   desde el inicio hasta encontrar el codigo buscado o llegar al final.
     *   Esta es la unica forma de buscar en archivos secuenciales no indexados.
     *   La logica de la busqueda esta en el gestor; la vista solo muestra el resultado.
     *
     * RESALTADO EN TABLA:
     *   Si se encuentra el producto, se busca su fila en el modelo de la tabla
     *   y se selecciona visualmente usando setRowSelectionInterval().
     *   scrollRectToVisible() hace scroll automatico para que la fila sea visible.
     *
     * equalsIgnoreCase(): compara codigos ignorando mayusculas/minusculas,
     *   mejorando la experiencia del usuario (no tiene que recordar el case exacto).
     * =========================================================================
     */
    private void accionBuscar() {
        // Leer el codigo ingresado en el campo de busqueda
        String codigo = obtenerTextoReal(txtBuscar, "Codigo a buscar");

        if (codigo.isEmpty()) {
            // Campo de busqueda vacio: informar al usuario
            JOptionPane.showMessageDialog(this,
                    "Ingrese el codigo del producto a buscar.",
                    "Campo vacio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        /*
         * DELEGACION DE BUSQUEDA AL GESTOR:
         * El gestor realiza la busqueda secuencial en el archivo y retorna
         * el objeto Producto encontrado, o null si no existe.
         * La vista recibe el resultado y decide como mostrarlo.
         */
        Producto encontrado = gestorProductos.buscarProducto(codigo);

        if (encontrado != null) {
            /*
             * PRODUCTO ENCONTRADO:
             * Recorrer las filas del modelo de la tabla para encontrar
             * cual corresponde al codigo buscado y seleccionarla visualmente.
             */
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                if (modeloTabla.getValueAt(i, 1).toString()
                        .equalsIgnoreCase(codigo)) { // Comparacion sin distincion de mayusculas

                    // Seleccionar la fila encontrada en la tabla
                    tablaProductos.setRowSelectionInterval(i, i);

                    // Hacer scroll automatico para que la fila sea visible
                    tablaProductos.scrollRectToVisible(
                            tablaProductos.getCellRect(i, 0, true));
                    break; // Salir del bucle: ya se encontro y selecciono la fila
                }
            }

            // Mostrar confirmacion con el nombre del producto en la barra de estado
            mostrarEstado("Producto encontrado: " + encontrado.getNombre(), COLOR_EXITO);

            // Dialogo informativo con todos los datos del producto encontrado
            JOptionPane.showMessageDialog(this,
                    "Producto encontrado:\n\n" + encontrado.obtenerInfo(),
                    "Resultado de busqueda",
                    JOptionPane.INFORMATION_MESSAGE);

        } else {
            // PRODUCTO NO ENCONTRADO: mostrar mensaje de error
            mostrarEstado(
                    "No se encontro producto con codigo: " + codigo.toUpperCase(),
                    COLOR_ERROR); // Rojo en barra de estado

            JOptionPane.showMessageDialog(this,
                    "No se encontro ningun producto con codigo: "
                    + codigo.toUpperCase(),
                    "No encontrado", JOptionPane.WARNING_MESSAGE);
        }
    }

    //SECCION: OPERACIONES DE TABLA Y FORMULARIO 

    /**
     * METODO: cargarTabla()
     *
     * Lee todos los registros del archivo secuencial a traves del gestor
     * y los carga en el modelo de la JTable para su visualizacion.
     *
     * LECTURA SECUENCIAL DEL ARCHIVO:
     *   Se leen TODOS los registros de inicio a fin del archivo en cada llamada.
     *   Esto es caracteristico del acceso secuencial: no hay saltos directos.
     *
     * RECARGA COMPLETA DE LA TABLA:
     *   Antes de cargar, se limpia la tabla (setRowCount(0)).
     *   Luego se recorren todos los productos con un bucle for-each.
     *   Cada producto se agrega como una nueva fila (addRow).
     *   Esto garantiza que la tabla siempre refleje el estado actual del archivo.
     *
     * String.format("%.2f", precio):
     *   Formatea el precio con exactamente 2 decimales para consistencia visual.
     *   Ejemplo: 29.9 -> "29.90"
     *
     * Este metodo se llama:
     *   - Al iniciar el sistema (constructor)
     *   - Despues de guardar, editar o eliminar un registro
     *   - Al presionar el boton "Mostrar Registros"
     */
    private void cargarTabla() {
        // Limpiar todas las filas existentes en el modelo de la tabla
        // (necesario para evitar duplicados al recargar)
        modeloTabla.setRowCount(0);

        /*
         * DELEGACION DE LECTURA AL GESTOR:
         * El gestor lee el archivo secuencial y retorna una lista de objetos Producto.
         * La vista recibe la lista y la convierte en filas de la tabla.
         * SEPARACION MVC: la vista no sabe como se leen los archivos.
         */
        List<Producto> productos = gestorProductos.obtenerTodos();

        if (productos.isEmpty()) {
            // No hay registros: mostrar mensaje informativo
            mostrarEstado(
                    "No hay registros almacenados. Use el formulario para agregar productos.",
                    COLOR_SECUNDARIO);
            return; // Salir sin agregar filas
        }

        /*
         * ITERACION SECUENCIAL sobre la lista de productos:
         * Cada producto se convierte en un array de Object[] que representa
         * una fila de la tabla. El orden de los elementos debe coincidir
         * con el orden de las columnas definidas en el modelo.
         *
         * numero: contador auxiliar para el numero de fila (columna "N")
         */
        int numero = 1; // Contador para el numero de fila

        for (Producto p : productos) {
            // Agregar una nueva fila al modelo con los datos del producto
            modeloTabla.addRow(new Object[]{
                numero++,                              // Columna "N": numero secuencial
                p.getCodigo(),                         // Columna "Codigo": getter del objeto Producto
                p.getNombre(),                         // Columna "Nombre"
                p.getDescripcion(),                    // Columna "Descripcion"
                String.format("%.2f", p.getPrecio())   // Columna "Precio": formateado con 2 decimales
            });
        }

        // Mostrar confirmacion de cuantos registros se leyeron
        mostrarEstado(
                productos.size() + " registro(s) leido(s) del archivo correctamente.",
                COLOR_EXITO);
    }

    /**
     * METODO: cargarFilaEnFormulario()
     * Transfiere los datos de la fila seleccionada en la tabla al formulario
     * de entrada de datos y activa el MODO EDICION.
     *
     * MODO EDICION - Cambios en la UI:
     *   1. Los datos de la fila se cargan en los campos del formulario
     *   2. El campo txtCodigo se bloquea (setEditable(false)) porque el codigo
     *      es la clave identificadora del registro y no puede cambiar
     *   3. El boton "Guardar" cambia su texto a "Actualizar Registro"
     *   4. El boton "Cancelar Edicion" se hace visible
     *   5. El titulo de la ventana indica el registro en edicion
     *
     * RAZON DE BLOQUEAR EL CODIGO EN EDICION:
     *   En archivos secuenciales, el codigo es la clave de busqueda para
     *   localizar el registro. Si el codigo cambiara, no se podria encontrar
     *   el registro original para actualizarlo. Por eso se bloquea.
     *
     * Acceso a datos de la tabla: modeloTabla.getValueAt(fila, columna)
     *   retorna el Object de la celda. Se convierte a String con toString().
     */
    private void cargarFilaEnFormulario() {
        // Verificar nuevamente que haya fila seleccionada
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) return; // Proteccion: salir si no hay fila seleccionada

        /*
         * Leer los datos de cada columna de la fila seleccionada.
         * Los indices de columna corresponden al orden definido en el array "columnas":
         *   0 = N (numero de fila, no se usa)
         *   1 = Codigo
         *   2 = Nombre
         *   3 = Descripcion
         *   4 = Precio
         */
        String codigo      = modeloTabla.getValueAt(fila, 1).toString(); // Columna Codigo
        String nombre      = modeloTabla.getValueAt(fila, 2).toString(); // Columna Nombre
        String descripcion = modeloTabla.getValueAt(fila, 3).toString(); // Columna Descripcion
        String precio      = modeloTabla.getValueAt(fila, 4).toString(); // Columna Precio

        /*
         * Transferir los datos al formulario usando ponerTextoReal().
         * Este metodo establece el texto con color negro (no placeholder),
         * indicando que son datos reales cargados, no texto de ayuda.
         */
        ponerTextoReal(txtCodigo,      codigo);
        ponerTextoReal(txtNombre,      nombre);
        ponerTextoReal(txtDescripcion, descripcion);
        ponerTextoReal(txtPrecio,      precio);

        /*
         * ACTIVAR MODO EDICION:
         * modoEdicion = true: indica a accionGuardar() que debe actualizar, no crear
         * codigoEnEdicion: guarda el codigo del registro que se esta editando
         * (necesario para que el gestor pueda localizar el registro en el archivo)
         */
        modoEdicion     = true;     // Flag: estamos editando, no creando
        codigoEnEdicion = codigo;   // Guardar codigo para la operacion de actualizacion

        // Bloquear el campo codigo: no puede cambiarse en modo edicion
        txtCodigo.setEditable(false);
        txtCodigo.setBackground(new Color(225, 230, 245)); // Fondo azul muy claro: indica campo bloqueado

        // Mostrar el boton de cancelacion que normalmente esta oculto
        btnCancelarEdicion.setVisible(true);

        // Cambiar el texto del boton guardar para reflejar la operacion actual
        btnGuardar.setText("Actualizar Registro");

        // Actualizar el titulo de la ventana indicando el codigo en edicion
        lblTitulo.setText("Sistema de Gestion  |  EDITANDO: " + codigo);

        // Mostrar mensaje de estado indicando que se activo el modo edicion
        mostrarEstado(
                "Modo edicion activado para: " + codigo + " - " + nombre,
                new Color(150, 80, 0)); // Color naranja oscuro para modo edicion
    }

    /**
     * METODO: cancelarEdicion()
     *
     * Cancela la operacion de edicion en curso y restaura la interfaz al
     * estado inicial (modo "nuevo registro").
     *
     * Se llama al presionar el boton "Cancelar Edicion" que solo es visible
     * cuando el sistema esta en modo edicion.
     *
     * RESTAURACION DEL ESTADO:
     *   1. modoEdicion = false
     *   2. codigoEnEdicion = "" (limpiar referencia)
     *   3. limpiarFormulario() restaura todos los campos y controles visuales
     *   4. El titulo de la ventana vuelve a ser el original
     *
     * DECISION DE DISENO: se llama a limpiarFormulario() porque ese metodo
     * ya contiene toda la logica de restauracion de la UI (ocultar boton
     * cancelar, restaurar texto de boton guardar, etc.).
     */
    private void cancelarEdicion() {
        // Desactivar modo edicion
        modoEdicion     = false;
        codigoEnEdicion = "";

        // Limpiar formulario y restaurar todos los controles visuales al estado inicial
        limpiarFormulario();

        // Restaurar el titulo original de la ventana
        lblTitulo.setText("Sistema de Gestion de Productos");

        // Confirmar al usuario que la edicion fue cancelada
        mostrarEstado("Edicion cancelada. Formulario listo.", COLOR_SECUNDARIO);
    }

    /**
     * METODO: limpiarFormulario()
     *
     * Limpia todos los campos del formulario y restaura la interfaz al
     * estado inicial completo: campos con placeholders, boton guardar con
     * texto original, boton cancelar oculto, modo edicion desactivado.
     *
     * Se llama desde:
     *   - accionGuardar() despues de una operacion exitosa
     *   - cancelarEdicion() al cancelar una edicion
     *   - Boton "Limpiar Formulario" directamente
     *
     * RESTAURACION COMPLETA:
     *   - Desbloquea el campo codigo (editable = true, fondo blanco)
     *   - Restaura todos los campos con sus textos placeholder en color gris
     *   - Oculta el boton "Cancelar Edicion"
     *   - Restaura el texto del boton "Guardar"
     *   - Resetea las variables de estado modoEdicion y codigoEnEdicion
     *   - Restaura el titulo original de la ventana
     */
    private void limpiarFormulario() {
        // Desbloquear el campo codigo para el siguiente nuevo registro
        txtCodigo.setEditable(true);
        txtCodigo.setBackground(Color.WHITE); // Fondo blanco: campo editable normal

        /*
         * Restaurar todos los campos a sus placeholders usando restaurarPlaceholder().
         * Cada llamada recibe el campo y el texto placeholder que le corresponde.
         */
        restaurarPlaceholder(txtCodigo,      "Ej: P001");
        restaurarPlaceholder(txtNombre,      "Nombre del producto");
        restaurarPlaceholder(txtDescripcion, "Descripcion del producto");
        restaurarPlaceholder(txtPrecio,      "Ej: 29.90");
        restaurarPlaceholder(txtBuscar,      "Codigo a buscar"); // Tambien limpiar el campo de busqueda

        // Ocultar el boton de cancelacion (solo visible en modo edicion)
        btnCancelarEdicion.setVisible(false);

        // Restaurar el texto del boton guardar a su texto por defecto
        btnGuardar.setText("Guardar");

        // Desactivar modo edicion y limpiar el codigo de referencia
        modoEdicion     = false;
        codigoEnEdicion = "";

        // Restaurar el titulo de la ventana al texto original
        lblTitulo.setText("Sistema de Gestion de Productos");

        // Informar al usuario que el formulario esta listo para nuevo registro
        mostrarEstado("Formulario limpiado. Listo para un nuevo registro.",
                COLOR_SECUNDARIO);
    }

    // SECCION: UTILIDADES
    /*
     * Metodos de utilidad privados que encapsulan operaciones comunes
     * de manipulacion de campos de texto y mensajes de estado.
     *
     * ENCAPSULAMIENTO: estos metodos estan marcados como privados porque
     * son implementacion interna de la clase. No forman parte de la
     * interfaz publica de VentanaPrincipal.
     */

    /**
     * METODO: obtenerTextoReal(JTextField campo, String placeholder)
     *
     * Retorna el texto real ingresado en un campo, ignorando el texto
     * de placeholder que se usa como ayuda visual.
     *
     * PROBLEMA QUE RESUELVE:
     *   Los campos de texto se inicializan con texto de ayuda (placeholder).
     *   Si el usuario no escribe nada, el campo sigue teniendo ese texto.
     *   Al intentar guardar, el sistema interpretaria el placeholder como
     *   un dato real (por ejemplo, "Ej: P001" como codigo del producto).
     *   Este metodo evita ese problema: si el campo tiene el placeholder, retorna "".
     *
     * trim(): elimina espacios en blanco al inicio y final del texto,
     *   previniendo que espacios accidentales sean tratados como datos validos.
     *
     * @param campo       El JTextField del que se lee el texto
     * @param placeholder El texto de ayuda a ignorar
     * @return El texto real ingresado, o "" si el campo solo tiene el placeholder
     */
    private String obtenerTextoReal(JTextField campo, String placeholder) {
        String texto = campo.getText().trim(); // Leer y quitar espacios en blanco

        // Si el texto es igual al placeholder, el usuario no ingreso nada real
        return texto.equals(placeholder) ? "" : texto; // Operador ternario: retorna "" o el texto real
    }

    /**
     * METODO: ponerTextoReal(JTextField campo, String texto)
     *
     * Establece un texto real (no placeholder) en un campo del formulario,
     * configurando el color del texto a negro (COLOR_TEXTO) para indicar
     * que es contenido real, no texto de ayuda.
     *
     * Se usa cuando se cargan datos desde la tabla al formulario (modo edicion)
     * para asegurar que el texto se vea con el color correcto (negro).
     *
     * @param campo El JTextField donde se establece el texto
     * @param texto El texto real a mostrar en el campo
     */
    private void ponerTextoReal(JTextField campo, String texto) {
        campo.setText(texto);             // Establecer el texto en el campo
        campo.setForeground(COLOR_TEXTO); // Color negro: indica que es contenido real, no placeholder
    }

    /**
     * METODO: restaurarPlaceholder(JTextField campo, String placeholder)
     *
     * Restaura el texto de ayuda (placeholder) en un campo del formulario,
     * con el color gris que indica que es texto de ayuda y no contenido real.
     *
     * Es el metodo inverso de ponerTextoReal(): mientras uno establece datos
     * reales en negro, este establece textos de ayuda en gris.
     *
     * Se usa al limpiar el formulario (limpiarFormulario()) para restaurar
     * el estado visual inicial de todos los campos.
     *
     * @param campo       El JTextField a restaurar
     * @param placeholder El texto de ayuda a mostrar
     */
    private void restaurarPlaceholder(JTextField campo, String placeholder) {
        campo.setText(placeholder);                      // Establecer el texto placeholder
        campo.setForeground(new Color(150, 150, 160));   // Color gris: indica que es texto de ayuda
    }

    /**
     * METODO: mostrarEstado(String mensaje, Color color)
     *
     * Actualiza el mensaje de la barra de estado en la parte inferior de
     * la ventana con el texto y color especificados.
     *
     * PATRON DE RETROALIMENTACION:
     *   La barra de estado proporciona retroalimentacion inmediata sobre el
     *   ultimo evento del sistema sin bloquear el flujo de trabajo del usuario
     *   (a diferencia de JOptionPane que requiere hacer clic en "Aceptar").
     *
     * color.brighter(): aclara el color dado para mejorar la legibilidad
     *   sobre el fondo oscuro (COLOR_PRIMARIO) de la barra de estado.
     *   Por ejemplo, el COLOR_EXITO (verde oscuro) se vuelve verde brillante
     *   para ser legible sobre el fondo azul marino.
     *
     * @param mensaje Texto del mensaje de estado a mostrar
     * @param color   Color del texto del mensaje (verde=exito, rojo=error, azul=info)
     */
    private void mostrarEstado(String mensaje, Color color) {
        lblEstado.setText(mensaje);          // Actualizar el texto de la etiqueta de estado
        lblEstado.setForeground(color.brighter()); // Color aclarado para legibilidad sobre fondo oscuro
    }
}