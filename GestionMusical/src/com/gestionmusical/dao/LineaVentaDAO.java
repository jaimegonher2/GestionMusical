package com.gestionmusical.dao;

import com.gestionmusical.database.DatabaseManager;
import com.gestionmusical.model.LineaVenta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Acceso a datos de la entidad LineaVenta.

public class LineaVentaDAO {

    private final Connection con;

    public LineaVentaDAO() {
        this.con = DatabaseManager.getInstance().getConnection();
    }

    // Listar lineas de venta
   
    public List<LineaVenta> listarPorVenta(int idVenta) {
        List<LineaVenta> lista = new ArrayList<>();
        String sql = "SELECT * FROM LINEA_VENTA WHERE id_venta = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar líneas de venta: " + e.getMessage());
        }
        return lista;
    }

    // Buscar por ID
    public LineaVenta buscarPorId(int idLinea) {
        String sql = "SELECT * FROM LINEA_VENTA WHERE id_linea = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idLinea);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar línea de venta: " + e.getMessage());
        }
        return null;
    }

    // Insertar una linea de venta
    public boolean insertar(LineaVenta lv) {
        String sql = """
            INSERT INTO LINEA_VENTA (id_venta, id_producto, cantidad, precio_unitario, subtotal)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt   (1, lv.getIdVenta());
            ps.setInt   (2, lv.getIdProducto());
            ps.setInt   (3, lv.getCantidad());
            ps.setDouble(4, lv.getPrecioUnitario());
            ps.setDouble(5, lv.getSubtotal());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar línea de venta: " + e.getMessage());
        }
        return false;
    }

    // Eliminar lineas de una venta
    public boolean eliminarPorVenta(int idVenta) {
        String sql = "DELETE FROM LINEA_VENTA WHERE id_venta = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar líneas de venta: " + e.getMessage());
        }
        return false;
    }

    // Calcular el total de una venta por su id
    public double calcularTotal(int idVenta) {
        String sql = "SELECT SUM(subtotal) FROM LINEA_VENTA WHERE id_venta = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("Error al calcular total de venta: " + e.getMessage());
        }
        return 0.0;
    }

    // Mapear los resultados
    private LineaVenta mapear(ResultSet rs) throws SQLException {
        return new LineaVenta(
            rs.getInt   ("id_linea"),
            rs.getInt   ("id_venta"),
            rs.getInt   ("id_producto"),
            rs.getInt   ("cantidad"),
            rs.getDouble("precio_unitario"),
            rs.getDouble("subtotal")
        );
    }
}