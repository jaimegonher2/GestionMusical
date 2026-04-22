package com.gestionmusical.view;

import com.gestionmusical.dao.DevolucionDAO;
import com.gestionmusical.dao.ProductoDAO;
import com.gestionmusical.dao.VentaDAO;
import com.gestionmusical.model.Devolucion;
import com.gestionmusical.model.Producto;
import com.gestionmusical.model.Usuario;
import com.gestionmusical.model.Venta;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/*Panel de gestión de devoluciones.
 Permite buscar una venta por ID y registrar la devolución de uno o varios productos de esa venta.*/

public class DevolucionPanel extends JPanel {

    // daos
    private final DevolucionDAO devolucionDAO = new DevolucionDAO();
    private final VentaDAO ventaDAO = new VentaDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    // Usuario activo
    private final Usuario usuarioActivo;

    
    private JTextField campoIdVenta;
    private JLabel etiquetaInfoVenta;

   
    private JTable tablaDevoluciiones;
    private DefaultTableModel modeloTabla;

    
    private Venta ventaCargada = null;

    public DevolucionPanel(Usuario usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
        initComponents();
        cargarDevoluciones();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

       // panel superior, busqueda por ID
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

        panelBusqueda.add(new JLabel("ID de venta:"));
        campoIdVenta = new JTextField(8);
        campoIdVenta.addActionListener(e -> buscarVenta());
        panelBusqueda.add(campoIdVenta);

        JButton btnBuscarVenta = new JButton("Buscar venta");
        btnBuscarVenta.addActionListener(e -> buscarVenta());
        panelBusqueda.add(btnBuscarVenta);

        // Etiqueta que muestra información de la venta encontrada
        etiquetaInfoVenta = new JLabel("Introduce el ID de la venta para registrar una devolución.");
        etiquetaInfoVenta.setForeground(Color.GRAY);
        panelBusqueda.add(etiquetaInfoVenta);

        JButton btnNuevaDevolucion = new JButton("Registrar devolución");
        btnNuevaDevolucion.addActionListener(e -> registrarDevolucion());
        panelBusqueda.add(btnNuevaDevolucion);

        // Tabla de devoluciones registradas
        String[] columnas = {"ID", "ID Venta", "Producto", "Cantidad", "Motivo", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablaDevoluciiones = new JTable(modeloTabla);
        tablaDevoluciiones.setRowHeight(26);
        tablaDevoluciiones.getColumnModel().getColumn(0).setMaxWidth(50);
        tablaDevoluciiones.getColumnModel().getColumn(1).setMaxWidth(80);
        tablaDevoluciiones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDevoluciiones.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tablaDevoluciiones);

        add(panelBusqueda, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    // Cargar todas las devoluciones
    private void cargarDevoluciones() {
        List<Devolucion> devoluciones = devolucionDAO.listarTodas();
        modeloTabla.setRowCount(0);
        for (Devolucion d : devoluciones) {
            // Obtener el nombre del producto para mostrarlo en lugar del ID
            Producto producto = productoDAO.buscarPorId(d.getIdProducto());
            String nombreProducto;
            if (producto != null) {
                nombreProducto = producto.getNombre();
            } else {
                nombreProducto = "ID: " + d.getIdProducto();
            }

            modeloTabla.addRow(new Object[]{
                d.getIdDevolucion(),
                d.getIdVenta(),
                nombreProducto,
                d.getCantidad(),
                d.getMotivo(),
                d.getFechaHora()
            });
        }
    }

    // BUscar una venta por ID para hacer una devolución
    private void buscarVenta() {
        String texto = campoIdVenta.getText().trim();
        if (texto.isEmpty()) {
            return;
        }

        try {
            int idVenta = Integer.parseInt(texto);
            ventaCargada = ventaDAO.buscarPorId(idVenta);

            if (ventaCargada != null) {
                // Mostrar información básica de la venta encontrada
                etiquetaInfoVenta.setText(
                        "Venta #" + ventaCargada.getIdVenta()
                        + " | " + ventaCargada.getFechaHora()
                        + " | Total: " + String.format("%.2f €", ventaCargada.getTotal())
                );
                etiquetaInfoVenta.setForeground(new Color(0, 120, 0));
            } else {
                etiquetaInfoVenta.setText("No se encontró ninguna venta con ese ID.");
                etiquetaInfoVenta.setForeground(Color.RED);
                ventaCargada = null;
            }
        } catch (NumberFormatException e) {
            // El ID introducido no es un número válido
            etiquetaInfoVenta.setText("El ID de venta debe ser un número.");
            etiquetaInfoVenta.setForeground(Color.RED);
        }
    }

    // Registrar nueva devolucion
    private void registrarDevolucion() {
        // Verificar que hay una venta cargada
        if (ventaCargada == null) {
            JOptionPane.showMessageDialog(this,
                    "Primero busca una venta válida.");
            return;
        }

        // Formulario para introducir los datos de la devolución
        JTextField campoCantidad = new JTextField("1");
        JTextField campoMotivo = new JTextField();
        JTextField campoIdProducto = new JTextField();

        JPanel formulario = new JPanel(new GridLayout(0, 2, 8, 8));
        formulario.add(new JLabel("ID del producto:"));
        formulario.add(campoIdProducto);
        formulario.add(new JLabel("Cantidad:"));
        formulario.add(campoCantidad);
        formulario.add(new JLabel("Motivo:"));
        formulario.add(campoMotivo);

        int resultado = JOptionPane.showConfirmDialog(this, formulario,
                "Registrar devolución — Venta #" + ventaCargada.getIdVenta(),
                JOptionPane.OK_CANCEL_OPTION);

        if (resultado != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int idProducto = Integer.parseInt(campoIdProducto.getText().trim());
            int cantidad = Integer.parseInt(campoCantidad.getText().trim());
            String motivo = campoMotivo.getText().trim();

            // Verificar que el producto existe
            Producto producto = productoDAO.buscarPorId(idProducto);
            if (producto == null) {
                JOptionPane.showMessageDialog(this, "No existe ningún producto con ese ID.");
                return;
            }

            // Crear y guardar la devolución
            Devolucion devolucion = new Devolucion();
            devolucion.setIdVenta(ventaCargada.getIdVenta());
            devolucion.setIdProducto(idProducto);
            devolucion.setCantidad(cantidad);
            devolucion.setMotivo(motivo);
            devolucion.setIdUsuario(usuarioActivo.getIdUsuario());

            devolucionDAO.insertar(devolucion);

            // Devolver el stock al producto
            int nuevoStock = producto.getStockActual() + cantidad;
            productoDAO.actualizarStock(idProducto, nuevoStock);

            JOptionPane.showMessageDialog(this,
                    "Devolución registrada. Stock de \"" + producto.getNombre()
                    + "\" actualizado a " + nuevoStock + " unidades.");

            // Recargar la tabla de devoluciones
            cargarDevoluciones();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "El ID de producto y la cantidad deben ser números válidos.");
        }
    }
}