package com.gestionmusical.dao;

import com.gestionmusical.database.DatabaseManager;
import com.gestionmusical.model.Usuario;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.UnsupportedEncodingException;

/**
 * Acceso a datos de la entidad Usuario.
 * Incluye autenticación con hash SHA-256.
 */
public class UsuarioDAO {

    private final Connection con;

    public UsuarioDAO() {
        con = DatabaseManager.getInstance().getConnection();
    }

    //  SHA-256
    public static String hashSHA256(String texto) {
    try {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(texto.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
        throw new RuntimeException("Error al generar hash SHA-256", e);
    }
}
    

    // Login
    public Usuario login(String nombreUsuario, String contrasena) {
        
        String sql = "SELECT * FROM USUARIO WHERE nombre_usuario = ? AND contrasena_hash = ? AND activo = 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, hashSHA256(contrasena));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error en login: " + e.getMessage());
        }
        return null;
    }

    // Listar los usuarios de la base de datos
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM USUARIO ORDER BY nombre_completo";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    // Buscar usuarios por ID
    public Usuario buscarPorId(int idUsuario) {
        String sql = "SELECT * FROM USUARIO WHERE id_usuario = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
        }
        return null;
    }

    // Insertar usuario
    public boolean insertar(Usuario u) {
        String sql = """
            INSERT INTO USUARIO (nombre_usuario, contrasena_hash, nombre_completo, rol, activo)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, hashSHA256(u.getContrasenaHash()));
            ps.setString(3, u.getNombreCompleto());
            ps.setString(4, u.getRol());
            ps.setInt   (5, u.getActivo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
        }
        return false;
    }

    // Actualizar usuario
    public boolean actualizar(Usuario u) {
        String sql = """
            UPDATE USUARIO SET nombre_usuario = ?, nombre_completo = ?, rol = ?, activo = ?
            WHERE id_usuario = ?
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getNombreCompleto());
            ps.setString(3, u.getRol());
            ps.setInt   (4, u.getActivo());
            ps.setInt   (5, u.getIdUsuario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
        }
        return false;
    }

    // Cambiar contraseña
    public boolean cambiarContrasena(int idUsuario, String nuevaContrasena) {
        String sql = "UPDATE USUARIO SET contrasena_hash = ? WHERE id_usuario = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hashSHA256(nuevaContrasena));
            ps.setInt   (2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar contraseña: " + e.getMessage());
        }
        return false;
    }

    // Desactivar usuario
    public boolean desactivar(int idUsuario) {
        String sql = "UPDATE USUARIO SET activo = 0 WHERE id_usuario = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al desactivar usuario: " + e.getMessage());
        }
        return false;
    }

    // Mapear el Resulset con los datos de usuario para reutilizacióon del código en el resto de métodos
    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt   ("id_usuario"),
            rs.getString("nombre_usuario"),
            rs.getString("contrasena_hash"),
            rs.getString("nombre_completo"),
            rs.getString("rol"),
            rs.getInt   ("activo"),
            rs.getString("fecha_creacion")
        );
    }
}