package com.gestionmusical.dao;

import com.gestionmusical.database.DatabaseManager;
import com.gestionmusical.model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Acceso a los datos de la entidad cliente de la BBDD

public class ClienteDAO {

    private final Connection con;

    public ClienteDAO() {
        this.con = DatabaseManager.getInstance().getConnection();
    }

    // Listar clientes activos
    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM CLIENTE WHERE activo = 1 ORDER BY apellidos, nombre";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar clientes: " + e.getMessage());
        }
        return lista;
    }

    // buscar por ID
    public Cliente buscarPorId(int idCliente) {
        String sql = "SELECT * FROM CLIENTE WHERE id_cliente = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente: " + e.getMessage());
        }
        return null;
    }

    // Buscar por nombre y apellidos
    public List<Cliente> buscarPorNombre(String texto) {
        List<Cliente> lista = new ArrayList<>();
        String sql = """
            SELECT * FROM CLIENTE
            WHERE activo = 1
            AND (nombre LIKE ? OR apellidos LIKE ?)
            ORDER BY apellidos, nombre
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente por nombre: " + e.getMessage());
        }
        return lista;
    }

    // Buscar por email
    public Cliente buscarPorEmail(String email) {
        String sql = "SELECT * FROM CLIENTE WHERE email = ? AND activo = 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente por email: " + e.getMessage());
        }
        return null;
    }

    //  Insertar un cliente
    public boolean insertar(Cliente c) {
        String sql = """
            INSERT INTO CLIENTE (nombre, apellidos, telefono, email, direccion, observaciones)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellidos());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getDireccion());
            ps.setString(6, c.getObservaciones());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar cliente: " + e.getMessage());
        }
        return false;
    }

    // Actualizar cliente
    public boolean actualizar(Cliente c) {
        String sql = """
            UPDATE CLIENTE SET nombre = ?, apellidos = ?, telefono = ?,
                email = ?, direccion = ?, observaciones = ?
            WHERE id_cliente = ?
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellidos());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getDireccion());
            ps.setString(6, c.getObservaciones());
            ps.setInt   (7, c.getIdCliente());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
        }
        return false;
    }

    // Desactivacióna
    public boolean desactivar(int idCliente) {
        String sql = "UPDATE CLIENTE SET activo = 0 WHERE id_cliente = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al desactivar cliente: " + e.getMessage());
        }
        return false;
    }

    // Mapear el resultset
    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
            rs.getInt   ("id_cliente"),
            rs.getString("nombre"),
            rs.getString("apellidos"),
            rs.getString("telefono"),
            rs.getString("email"),
            rs.getString("direccion"),
            rs.getString("fecha_alta"),
            rs.getInt   ("activo"),
            rs.getString("observaciones")
        );
    }
}