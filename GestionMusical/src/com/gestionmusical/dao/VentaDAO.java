package com.gestionmusical.dao;

import com.gestionmusical.database.DatabaseManager;
import com.gestionmusical.model.Venta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


// Acceso a datos de la entidad Venta.
 
public class VentaDAO {

    private final Connection con;

    public VentaDAO() {
        this.con = DatabaseManager.getInstance().getConnection();
    }

    // Mostrar todas las ventas
    public List<Venta> listarTodas() {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT * FROM VENTA ORDER BY fecha_hora DESC";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar ventas: " + e.getMessage());
        }
        return lista;
    }

    // Mostrar ventas por usuario
    public List<Venta> listarPorUsuario(int idUsuario) {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT * FROM VENTA WHERE id_usuario = ? ORDER BY fecha_hora DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar ventas por usuario: " + e.getMessage());
        }
        return lista;
    }

    // Mostrar ventas por cliente
    public List<Venta> listarPorCliente(int idCliente) {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT * FROM VENTA WHERE id_cliente = ? ORDER BY fecha_hora DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar ventas por cliente: " + e.getMessage());
        }
        return lista;
    }

    // Mostrar ventas por rango fecha
    public List<Venta> listarPorFechas(String fechaInicio, String fechaFin) {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT * FROM VENTA WHERE fecha_hora BETWEEN ? AND ? ORDER BY fecha_hora DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar ventas por fechas: " + e.getMessage());
        }
        return lista;
    }

    // buscar por ID
    public Venta buscarPorId(int idVenta) {
        String sql = "SELECT * FROM VENTA WHERE id_venta = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar venta: " + e.getMessage());
        }
        return null;
    }

    // Insertas una nueva venta
    public int insertar(Venta v) {
        String sql = """
            INSERT INTO VENTA (total, descuento, forma_pago, id_usuario, id_cliente, observaciones)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, v.getTotal());
            ps.setDouble(2, v.getDescuento());
            ps.setString(3, v.getFormaPago());
            ps.setInt   (4, v.getIdUsuario());
            if (v.getIdCliente() > 0) {
                ps.setInt(5, v.getIdCliente());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setString(6, v.getObservaciones());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error al insertar venta: " + e.getMessage());
        }
        return -1;
    }

    // Actualizar las ventas
    public boolean actualizarTotal(int idVenta, double total) {
        String sql = "UPDATE VENTA SET total = ? WHERE id_venta = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, total);
            ps.setInt   (2, idVenta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar total de venta: " + e.getMessage());
        }
        return false;
    }

    // mapear salida del resulset
    private Venta mapear(ResultSet rs) throws SQLException {
        return new Venta(
            rs.getInt   ("id_venta"),
            rs.getString("fecha_hora"),
            rs.getDouble("total"),
            rs.getDouble("descuento"),
            rs.getString("forma_pago"),
            rs.getInt   ("id_usuario"),
            rs.getInt   ("id_cliente"),
            rs.getString("observaciones")
        );
    }
}