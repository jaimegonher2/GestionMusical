package com.gestionmusical.view;

import com.gestionmusical.dao.BackupDAO;
import com.gestionmusical.model.Backup;
import com.gestionmusical.model.Usuario;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/*Panel de copias de seguridad. Permite generar una copia del archivo .db y ver el historial de backups*/

public class BackupPanel extends JPanel {

    // DAOS
    private final BackupDAO backupDAO;

    // Usuario activo
    private final Usuario usuarioActivo;

    // Tabla de historial de backups
    private JTable tablaBackups;
    private DefaultTableModel modeloTabla;

    public BackupPanel(Usuario usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
        this.backupDAO = new BackupDAO();
        initComponents();
        cargarBackups();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel superior con botón de generar backup
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

        JButton btnGenerarBackup = new JButton("Generar copia de seguridad");
        btnGenerarBackup.addActionListener(e -> generarBackup());
        panelSuperior.add(btnGenerarBackup);

        JLabel etiquetaInfo = new JLabel("Las copias se guardan en la carpeta 'backups' del directorio de la aplicación.");
        etiquetaInfo.setForeground(Color.GRAY);
        panelSuperior.add(etiquetaInfo);

        // Tabla del historial de backups
        String[] columnas = {"ID", "Ruta del archivo", "Fecha", "Usuario", "Descripción"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablaBackups = new JTable(modeloTabla);
        tablaBackups.setRowHeight(26);
        tablaBackups.getColumnModel().getColumn(0).setMaxWidth(50);
        tablaBackups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaBackups.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tablaBackups);

        add(panelSuperior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    // Cargar historial de backups en la tabla
    private void cargarBackups() {
        List<Backup> backups = backupDAO.listarTodos();
        modeloTabla.setRowCount(0);
        for (Backup b : backups) {
            modeloTabla.addRow(new Object[]{
                b.getIdBackup(),
                b.getRutaArchivo(),
                b.getFechaCreacion(),
                b.getIdUsuario(),
                b.getDescripcion()
            });
        }
    }

    // Generar una copia de seguridad del archivo .db
    private void generarBackup() {
        // Pedir descripción opcional al usuario
        String descripcion = JOptionPane.showInputDialog(this,
                "Descripción de la copia (opcional):",
                "Nueva copia de seguridad",
                JOptionPane.PLAIN_MESSAGE);

        // Si el usuario cancela el diálogo no se genera el backup
        if (descripcion == null) {
            return;
        }

        try {
            // Crear la carpeta backups si no existe
            Path carpetaBackup = Paths.get("backups");
            if (!Files.exists(carpetaBackup)) {
                Files.createDirectories(carpetaBackup);
            }

            // Nombre del archivo con fecha y hora para evitar duplicados
            String nombreArchivo = "backup_"
                    + java.time.LocalDateTime.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                    + ".db";

            Path origen = Paths.get("gestion_musical.db");
            Path destino = carpetaBackup.resolve(nombreArchivo);

            // Copiar el archivo de base de datos
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);

            // Registrar el backup en la base de datos
            Backup backup = new Backup();
            backup.setRutaArchivo(destino.toString());
            backup.setIdUsuario(usuarioActivo.getIdUsuario());
            backup.setDescripcion(descripcion.isEmpty() ? "Sin descripción" : descripcion);

            backupDAO.insertar(backup);

            JOptionPane.showMessageDialog(this,
                    "Copia de seguridad generada correctamente:\n" + destino.toString());

            cargarBackups();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar la copia de seguridad: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
