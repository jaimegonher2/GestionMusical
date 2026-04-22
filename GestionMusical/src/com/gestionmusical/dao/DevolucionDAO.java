package com.gestionmusical.dao;

import com.gestionmusical.database.DatabaseManager;
import com.gestionmusical.model.Devolucion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*Acceso a datos de la entidad Devolucion.*/
public class DevolucionDAO {

    private final Connection con;

    public DevolucionDAO() {
        this.con = DatabaseManager.getInstance().getConnection();
    }

    // Listar devoluciones
    public List<Devolucion> listarTodas() {
        List<Devolucion> lista = new ArrayList<>();
        String sql = "SELECT * FROM DEVOLUCION ORDER BY fecha_hora DESC";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar devoluciones: " + e.getMessage());
        }
        return lista;
    }

    // Listar por venta
    public List<Devolucion> listarPorVenta(int idVenta) {
        List<Devolucion> lista = new ArrayList<>();
        String sql = "SELECT * FROM DEVOLUCION WHERE id_venta = ? ORDER BY fecha_hora DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar devoluciones por venta: " + e.getMessage());
        }
        return lista;
    }

    // Listar por producto
    public List<Devolucion> listarPorProducto(int idProducto) {
        List<Devolucion> lista = new ArrayList<>();
        String sql = "SELECT * FROM DEVOLUCION WHERE id_producto = ? ORDER BY fecha_hora DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar devoluciones por producto: " + e.getMessage());
        }
        return lista;
    }

    // Buscar por ID
    public Devolucion buscarPorId(int idDevolucion) {
        String sql = "SELECT * FROM DEVOLUCION WHERE id_devolucion = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idDevolucion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar devolucion: " + e.getMessage());
        }
        return null;
    }

    // Insertasr
    public boolean insertar(Devolucion d) {
        String sql = """
            INSERT INTO DEVOLUCION (id_venta, id_producto, cantidad, motivo, id_usuario)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt   (1, d.getIdVenta());
            ps.setInt   (2, d.getIdProducto());
            ps.setInt   (3, d.getCantidad());
            ps.setString(4, d.getMotivo());
            ps.setInt   (5, d.getIdUsuario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar devolucion: " + e.getMessage());
        }
        return false;
    }

    //  Mapear el resultset
    private Devolucion mapear(ResultSet rs) throws SQLException {
        return new Devolucion(
            rs.getInt   ("id_devolucion"),
            rs.getInt   ("id_venta"),
            rs.getInt   ("id_producto"),
            rs.getInt   ("cantidad"),
            rs.getString("motivo"),
            rs.getString("fecha_hora"),
            rs.getInt   ("id_usuario")
        );
    }
}