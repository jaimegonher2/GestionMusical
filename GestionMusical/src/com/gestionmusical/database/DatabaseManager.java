package com.gestionmusical.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;


public class DatabaseManager {

    private static final String BBDD = "gestion_musical.db";  //Impide borrar un usuario que tiene ventas
    private static final String BBDD_URL  = "jdbc:sqlite:" + BBDD;

    private static DatabaseManager instance;
    private Connection connection;

    
    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(BBDD_URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
            esquemaBBDD();
            System.out.println("Base de datos conectada: " + BBDD);
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    
    public Connection getConnection() {
        return connection;
    }

   
    public void cerrarConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    //crear esquema de la base de datos
    private void esquemaBBDD() throws SQLException {
       try( Statement st = connection.createStatement()){

        // USUARIO
        st.execute("""
            CREATE TABLE IF NOT EXISTS USUARIO (
                id_usuario       INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_usuario   TEXT    NOT NULL UNIQUE,
                contrasena_hash  TEXT    NOT NULL,
                nombre_completo  TEXT    NOT NULL,
                rol              TEXT    NOT NULL CHECK(rol IN ('admin','empleado')),
                activo           INTEGER NOT NULL DEFAULT 1,
                fecha_creacion   TEXT    NOT NULL DEFAULT (datetime('now'))
            )
        """);

        // CATEGORIA
        st.execute("""
            CREATE TABLE IF NOT EXISTS CATEGORIA (
                id_categoria  INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre        TEXT    NOT NULL UNIQUE,
                descripcion   TEXT
            )
        """);

        // PRODUCTO
        st.execute("""
            CREATE TABLE IF NOT EXISTS PRODUCTO (
                id_producto    INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre         TEXT    NOT NULL,
                descripcion    TEXT,
                proveedor      TEXT,
                precio_compra  REAL    NOT NULL,
                precio_venta   REAL    NOT NULL,
                stock_actual   INTEGER NOT NULL DEFAULT 0,
                stock_minimo   INTEGER NOT NULL DEFAULT 5,
                id_categoria   INTEGER REFERENCES CATEGORIA(id_categoria),
                fecha_alta     TEXT    NOT NULL DEFAULT (datetime('now')),
                activo         INTEGER NOT NULL DEFAULT 1
            )
        """);

        // CLIENTE
        st.execute("""
            CREATE TABLE IF NOT EXISTS CLIENTE (
                id_cliente   INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre       TEXT    NOT NULL,
                apellidos    TEXT    NOT NULL,
                telefono     TEXT,
                email        TEXT    UNIQUE,
                direccion    TEXT,
                fecha_alta   TEXT    NOT NULL DEFAULT (datetime('now')),
                activo       INTEGER NOT NULL DEFAULT 1,
                observaciones TEXT
            )
        """);

        // VENTA
        st.execute("""
            CREATE TABLE IF NOT EXISTS VENTA (
                id_venta    INTEGER PRIMARY KEY AUTOINCREMENT,
                fecha_hora  TEXT    NOT NULL DEFAULT (datetime('now')),
                total       REAL    NOT NULL DEFAULT 0,
                descuento   REAL    NOT NULL DEFAULT 0,
                forma_pago  TEXT    NOT NULL CHECK(forma_pago IN ('efectivo','tarjeta','otros')),
                id_usuario  INTEGER NOT NULL REFERENCES USUARIO(id_usuario),
                id_cliente  INTEGER REFERENCES CLIENTE(id_cliente),
                observaciones TEXT
            )
        """);

        // LINEA_VENTA
        st.execute("""
            CREATE TABLE IF NOT EXISTS LINEA_VENTA (
                id_linea        INTEGER PRIMARY KEY AUTOINCREMENT,
                id_venta        INTEGER NOT NULL REFERENCES VENTA(id_venta),
                id_producto     INTEGER NOT NULL REFERENCES PRODUCTO(id_producto),
                cantidad        INTEGER NOT NULL,
                precio_unitario REAL    NOT NULL,
                subtotal        REAL    NOT NULL
            )
        """);

        // DEVOLUCION
        st.execute("""
            CREATE TABLE IF NOT EXISTS DEVOLUCION (
                id_devolucion  INTEGER PRIMARY KEY AUTOINCREMENT,
                id_venta       INTEGER NOT NULL REFERENCES VENTA(id_venta),
                id_producto    INTEGER NOT NULL REFERENCES PRODUCTO(id_producto),
                cantidad       INTEGER NOT NULL,
                motivo         TEXT,
                fecha_hora     TEXT    NOT NULL DEFAULT (datetime('now')),
                id_usuario     INTEGER NOT NULL REFERENCES USUARIO(id_usuario)
            )
        """);

        // BACKUP
        st.execute("""
            CREATE TABLE IF NOT EXISTS BACKUP (
                id_backup      INTEGER PRIMARY KEY AUTOINCREMENT,
                ruta_archivo   TEXT    NOT NULL,
                fecha_creacion TEXT    NOT NULL DEFAULT (datetime('now')),
                id_usuario     INTEGER NOT NULL REFERENCES USUARIO(id_usuario),
                descripcion    TEXT
            )
        """);

        // ÍNDICES
        st.execute("CREATE INDEX IF NOT EXISTS idx_producto_categoria ON PRODUCTO(id_categoria)");
        st.execute("CREATE INDEX IF NOT EXISTS idx_producto_nombre    ON PRODUCTO(nombre)");
        st.execute("CREATE INDEX IF NOT EXISTS idx_venta_fecha        ON VENTA(fecha_hora)");
        st.execute("CREATE INDEX IF NOT EXISTS idx_venta_usuario      ON VENTA(id_usuario)");
        st.execute("CREATE INDEX IF NOT EXISTS idx_venta_cliente      ON VENTA(id_cliente)");
        st.execute("CREATE INDEX IF NOT EXISTS idx_linea_venta        ON LINEA_VENTA(id_venta)");
        st.execute("CREATE INDEX IF NOT EXISTS idx_linea_producto     ON LINEA_VENTA(id_producto)");
        st.execute("CREATE INDEX IF NOT EXISTS idx_devolucion_venta   ON DEVOLUCION(id_venta)");
        st.execute("CREATE INDEX IF NOT EXISTS idx_cliente_apellidos  ON CLIENTE(apellidos)");
            
       }
        // INsertar un usuario admin por defecto
        String sqlAdmin = """
        INSERT OR IGNORE INTO USUARIO (nombre_usuario, contrasena_hash, nombre_completo, rol)
        VALUES ('admin', ?, 'Administrador', 'admin')
        """;
        try (PreparedStatement ps = connection.prepareStatement(sqlAdmin)) {
            ps.setString(1, com.gestionmusical.dao.UsuarioDAO.hashSHA256("admin123"));
            ps.executeUpdate();
        }
        
        
    }
}