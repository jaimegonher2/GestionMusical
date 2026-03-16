package com.gestionmusical.dao;

import com.gestionmusical.database.DatabaseManager;
import com.gestionmusical.model.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la entidad Categoria.
 */
public class CategoriaDAO {

    private final Connection con;

    public CategoriaDAO() {
        this.con = DatabaseManager.getInstance().getConnection();
    }

    // Listar todas las categorías
    public List<Categoria> listarTodas() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM CATEGORIA ORDER BY nombre";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar categorías: " + e.getMessage());
        }
        return lista;
    }

    // Buscar categoría por ID.
    public Categoria buscarPorId(int idCategoria) {
        String sql = "SELECT * FROM CATEGORIA WHERE id_categoria = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar categoría: " + e.getMessage());
        }
        return null;
    }

    // Insertar una categoría.
    public boolean insertar(Categoria c) {
        String sql = "INSERT INTO CATEGORIA (nombre, descripcion) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar categoría: " + e.getMessage());
        }
        return false;
    }

    // Actualizar una categoría
    public boolean actualizar(Categoria c) {
        String sql = "UPDATE CATEGORIA SET nombre = ?, descripcion = ? WHERE id_categoria = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setInt   (3, c.getIdCategoria());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar categoría: " + e.getMessage());
        }
        return false;
    }

    // Eliminar una categoría
    public boolean eliminar(int idCategoria) {
        String sql = "DELETE FROM CATEGORIA WHERE id_categoria = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar categoría: " + e.getMessage());
        }
        return false;
    }

    // Mapear los campos que devuelve el resulset.
    private Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(
            rs.getInt   ("id_categoria"),
            rs.getString("nombre"),
            rs.getString("descripcion")
        );
    }
}