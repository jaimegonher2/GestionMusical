package com.gestionmusical.dao;

import com.gestionmusical.database.DatabaseManager;
import com.gestionmusical.model.Backup;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Acceso a los datos de la entidad Backup

public class BackupDAO {

    private final Connection con;

    public BackupDAO() {
        this.con = DatabaseManager.getInstance().getConnection();
    }

    // Listar todos los bacups por fecha de creacion del mas reciente al mas antiguo
    public List<Backup> listarTodos() {
        List<Backup> lista = new ArrayList<>();
        String sql = "SELECT * FROM BACKUP ORDER BY fecha_creacion DESC";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar backups: " + e.getMessage());
        }
        return lista;
    }

    // Buscar por ID
    
    public Backup buscarPorId(int idBackup) {
        String sql = "SELECT * FROM BACKUP WHERE id_backup = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idBackup);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar backup: " + e.getMessage());
        }
        return null;
    }

    // Insertar
    
    public boolean insertar(Backup b) {
        String sql = """
            INSERT INTO BACKUP (ruta_archivo, id_usuario, descripcion)
            VALUES (?, ?, ?)
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, b.getRutaArchivo());
            ps.setInt   (2, b.getIdUsuario());
            ps.setString(3, b.getDescripcion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar backup: " + e.getMessage());
        }
        return false;
    }

    // Mapear resulset
    private Backup mapear(ResultSet rs) throws SQLException {
        return new Backup(
            rs.getInt   ("id_backup"),
            rs.getString("ruta_archivo"),
            rs.getString("fecha_creacion"),
            rs.getInt   ("id_usuario"),
            rs.getString("descripcion")
        );
    }
}