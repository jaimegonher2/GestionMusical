package com.gestionmusical.view;

import com.gestionmusical.dao.DevolucionDAO;
import com.gestionmusical.dao.LineaVentaDAO;
import com.gestionmusical.dao.ProductoDAO;
import com.gestionmusical.dao.VentaDAO;
import com.gestionmusical.model.Devolucion;
import com.gestionmusical.model.LineaVenta;
import com.gestionmusical.model.Producto;
import com.gestionmusical.model.Usuario;
import com.gestionmusical.model.Venta;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/*Panel de gestión de devoluciones.
 Muestra la lista de ventas, al seleccionar una carga sus líneas
 y permite registrar la devolución de cualquier producto de esa venta.*/
public class DevolucionPanel extends JPanel {

    // DAOs
    private final DevolucionDAO devolucionDAO = new DevolucionDAO();
    private final VentaDAO ventaDAO = new VentaDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final LineaVentaDAO lineaVentaDAO = new LineaVentaDAO();

    // Usuario activo
    private final Usuario usuarioActivo;

    // Tabla de ventas
    private JTable tablaVentas;
    private DefaultTableModel modeloVentas;

    // Tabla de líneas de la venta seleccionada
    private JTable tablaLineas;
    private DefaultTableModel modeloLineas;

    // Tabla de devoluciones registradas
    private JTable tablaDevoluciiones;
    private DefaultTableModel modeloTabla;

    // Venta actualmente seleccionada
    private Venta ventaCargada = null;

    public DevolucionPanel(Usuario usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
        initComponents();
        cargarVentas();
        cargarDevoluciones();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Tabla de ventas
        String[] columnasVentas = {"ID", "Fecha", "Total", "Forma de pago"};
        modeloVentas = new DefaultTableModel(columnasVentas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaVentas = new JTable(modeloVentas);
        tablaVentas.setRowHeight(26);
        tablaVentas.getColumnModel().getColumn(0).setMaxWidth(50);
        tablaVentas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaVentas.getTableHeader().setReorderingAllowed(false);

        // Al seleccionar una venta cargar sus líneas automáticamente
        tablaVentas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaVentas.getSelectedRow();
                if (fila >= 0) {
                    int idVenta = (int) modeloVentas.getValueAt(fila, 0);
                    ventaCargada = ventaDAO.buscarPorId(idVenta);
                    cargarLineasVenta(idVenta);
                }
            }
        });

        JPanel panelVentas = new JPanel(new BorderLayout());
        panelVentas.setBorder(new TitledBorder("Ventas — selecciona una para ver sus líneas"));
        panelVentas.add(new JScrollPane(tablaVentas), BorderLayout.CENTER);

        // Tabla de líneas de la venta seleccionada
        String[] columnasLineas = {"ID Producto", "Producto", "Cantidad", "Precio unit.", "Subtotal"};
        modeloLineas = new DefaultTableModel(columnasLineas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaLineas = new JTable(modeloLineas);
        tablaLineas.setRowHeight(26);
        tablaLineas.getColumnModel().getColumn(0).setMaxWidth(90);
        tablaLineas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaLineas.getTableHeader().setReorderingAllowed(false);

        JButton btnRegistrar = new JButton("Registrar devolución");
        btnRegistrar.addActionListener(e -> registrarDevolucion());

        JPanel panelLineas = new JPanel(new BorderLayout());
        panelLineas.setBorder(new TitledBorder("Líneas de la venta seleccionada"));
        panelLineas.add(new JScrollPane(tablaLineas), BorderLayout.CENTER);
        panelLineas.add(btnRegistrar, BorderLayout.SOUTH);

        // Tabla de devoluciones registradas
        String[] columnas = {"ID", "ID Venta", "Producto", "Cantidad", "Motivo", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaDevoluciiones = new JTable(modeloTabla);
        tablaDevoluciiones.setRowHeight(26);
        tablaDevoluciiones.getColumnModel().getColumn(0).setMaxWidth(50);
        tablaDevoluciiones.getColumnModel().getColumn(1).setMaxWidth(80);
        tablaDevoluciiones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDevoluciiones.getTableHeader().setReorderingAllowed(false);

        JPanel panelDevoluciones = new JPanel(new BorderLayout());
        panelDevoluciones.setBorder(new TitledBorder("Historial de devoluciones"));
        panelDevoluciones.add(new JScrollPane(tablaDevoluciiones), BorderLayout.CENTER);

        // Panel izquierdo: ventas arriba, líneas abajo
        JSplitPane splitIzquierda = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                panelVentas, panelLineas);
        splitIzquierda.setDividerLocation(200);
        splitIzquierda.setResizeWeight(0.5);

        // Panel principal: izquierda ventas/líneas, derecha historial devoluciones
        JSplitPane splitPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                splitIzquierda, panelDevoluciones);
        splitPrincipal.setDividerLocation(400);
        splitPrincipal.setResizeWeight(0.55);

        add(splitPrincipal, BorderLayout.CENTER);
    }

    // Cargar todas las ventas en la tabla
    private void cargarVentas() {
        List<Venta> ventas = ventaDAO.listarTodas();
        modeloVentas.setRowCount(0);
        for (Venta v : ventas) {
            modeloVentas.addRow(new Object[]{
                v.getIdVenta(),
                v.getFechaHora(),
                String.format("%.2f €", v.getTotal()),
                v.getFormaPago()
            });
        }
    }

    // Cargar las líneas de la venta seleccionada
    private void cargarLineasVenta(int idVenta) {
        modeloLineas.setRowCount(0);
        List<LineaVenta> lineas = lineaVentaDAO.listarPorVenta(idVenta);
        for (LineaVenta l : lineas) {
            Producto producto = productoDAO.buscarPorId(l.getIdProducto());
            String nombreProducto;
            if (producto != null) {
                nombreProducto = producto.getNombre();
            } else {
                nombreProducto = "ID: " + l.getIdProducto();
            }
            modeloLineas.addRow(new Object[]{
                l.getIdProducto(),
                nombreProducto,
                l.getCantidad(),
                String.format("%.2f €", l.getPrecioUnitario()),
                String.format("%.2f €", l.getSubtotal())
            });
        }
    }

    // Cargar todas las devoluciones registradas
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

    // Registrar nueva devolución
    private void registrarDevolucion() {
        // Verificar que hay una venta seleccionada
        if (ventaCargada == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una venta de la tabla.");
            return;
        }

        // Verificar que hay una línea seleccionada
        int filaLinea = tablaLineas.getSelectedRow();
        if (filaLinea < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona el producto a devolver en las líneas de la venta.");
            return;
        }

        // Obtener el producto de la línea seleccionada
        int idProducto = (int) modeloLineas.getValueAt(filaLinea, 0);
        String nombreProducto = (String) modeloLineas.getValueAt(filaLinea, 1);

        // Formulario para introducir cantidad y motivo
        JTextField campoCantidad = new JTextField("1");
        JTextField campoMotivo   = new JTextField();

        JPanel formulario = new JPanel(new GridLayout(0, 2, 8, 8));
        formulario.add(new JLabel("Producto:"));
        formulario.add(new JLabel(nombreProducto));
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

            // Recargar tabla de devoluciones
            cargarDevoluciones();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número válido.");
        }
    }
}