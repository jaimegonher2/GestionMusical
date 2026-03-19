package com.gestionmusical.dao;

import com.gestionmusical.database.DatabaseManager;
import com.gestionmusical.model.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Acceso a datos de ProductoDAO

public class ProductoDAO {

    private final Connection con;

    public ProductoDAO() {
        this.con = DatabaseManager.getInstance().getConnection();
    }

    // Mostrar productos activos
    
    public List<Producto> listarTodos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTO WHERE activo = 1 ORDER BY nombre";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage());
        }
        return lista;
    }

    // Mostrar stock bajo mínimo 
    
    public List<Producto> listarBajoMinimo() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTO WHERE activo = 1 AND stock_actual <= stock_minimo ORDER BY nombre";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar productos bajo mínimo: " + e.getMessage());
        }
        return lista;
    }

    // Mostrar por Categoría
    
    public List<Producto> listarPorCategoria(int idCategoria) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTO WHERE activo = 1 AND id_categoria = ? ORDER BY nombre";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar por categoría: " + e.getMessage());
        }
        return lista;
    }

    // Busqueda por ID
    
    public Producto buscarPorId(int idProducto) {
        String sql = "SELECT * FROM PRODUCTO WHERE id_producto = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar producto: " + e.getMessage());
        }
        return null;
    }

    // Busqueda por nombre
    
    public List<Producto> buscarPorNombre(String texto) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTO WHERE activo = 1 AND nombre LIKE ? ORDER BY nombre";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al buscar producto por nombre: " + e.getMessage());
        }
        return lista;
    }

    // Insertar Producto
    public boolean insertar(Producto p) {
        String sql = """
            INSERT INTO PRODUCTO (nombre, descripcion, proveedor, precio_compra,
                precio_venta, stock_actual, stock_minimo, id_categoria)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setString(3, p.getProveedor());
            ps.setDouble(4, p.getPrecioCompra());
            ps.setDouble(5, p.getPrecioVenta());
            ps.setInt   (6, p.getStockActual());
            ps.setInt   (7, p.getStockMinimo());
            ps.setInt   (8, p.getIdCategoria());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
        }
        return false;
    }

    // Actualizar producto
    public boolean actualizar(Producto p) {
        String sql = """
            UPDATE PRODUCTO SET nombre = ?, descripcion = ?, proveedor = ?,
                precio_compra = ?, precio_venta = ?, stock_actual = ?,
                stock_minimo = ?, id_categoria = ?
            WHERE id_producto = ?
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setString(3, p.getProveedor());
            ps.setDouble(4, p.getPrecioCompra());
            ps.setDouble(5, p.getPrecioVenta());
            ps.setInt   (6, p.getStockActual());
            ps.setInt   (7, p.getStockMinimo());
            ps.setInt   (8, p.getIdCategoria());
            ps.setInt   (9, p.getIdProducto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
        }
        return false;
    }

    // Actualizar stock
    public boolean actualizarStock(int idProducto, int nuevoStock) {
        String sql = "UPDATE PRODUCTO SET stock_actual = ? WHERE id_producto = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
        }
        return false;
    }

    // Desactivar producto
    public boolean desactivar(int idProducto) {
        String sql = "UPDATE PRODUCTO SET activo = 0 WHERE id_producto = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al desactivar producto: " + e.getMessage());
        }
        return false;
    }

    // Mostrar los campos de producto
    
    private Producto mapear(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getInt   ("id_producto"),
            rs.getString("nombre"),
            rs.getString("descripcion"),
            rs.getString("proveedor"),
            rs.getDouble("precio_compra"),
            rs.getDouble("precio_venta"),
            rs.getInt   ("stock_actual"),
            rs.getInt   ("stock_minimo"),
            rs.getInt   ("id_categoria"),
            rs.getString("fecha_alta"),
            rs.getInt   ("activo")
        );
    }
}