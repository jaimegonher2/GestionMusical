package com.gestionmusical.view;

import com.gestionmusical.dao.ClienteDAO;
import com.gestionmusical.dao.LineaVentaDAO;
import com.gestionmusical.dao.ProductoDAO;
import com.gestionmusical.dao.VentaDAO;
import com.gestionmusical.model.Cliente;
import com.gestionmusical.model.LineaVenta;
import com.gestionmusical.model.Producto;
import com.gestionmusical.model.Usuario;
import com.gestionmusical.model.Venta;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/*Panel de punto de venta (TPV).
 Permite buscar productos, añadirlos al ticket,
 seleccionar forma de pago y registrar la venta.*/
public class VentaPanel extends JPanel {

    // daos
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final VentaDAO ventaDAO = new VentaDAO();
    private final LineaVentaDAO lineaVentaDAO = new LineaVentaDAO();

    // usuario activo
    private final Usuario usuarioActivo;

    // busqueda de productos
    private JTextField campoBusquedaProducto;
    private JList<String> listaProductos;
    private DefaultListModel<String> modeloLista;
    private JSpinner spinnerCantidad;

    // componentes del ticket
    private JTable tablaTicket;
    private DefaultTableModel modeloTicket;
    private JLabel etiquetaTotal;

    // cobro
    private JComboBox<String> comboPago;
    private JComboBox<Cliente> comboCliente;


    private final List<LineaVenta> lineasEnCurso = new ArrayList<>();

    
    private List<Producto> productosEncontrados = new ArrayList<>();

    public VentaPanel(Usuario usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
        initComponents();
    }

  
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel izquierdo: búsqueda de productos
        JPanel panelBusqueda = crearPanelBusqueda();

        // Panel derecho: ticket de la venta
        JPanel panelTicket = crearPanelTicket();

        // Panel inferior: cobro
        JPanel panelCobro = crearPanelCobro();

        // Dividir la pantalla en izquierda y derecha
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                panelBusqueda, panelTicket);
        splitPane.setDividerLocation(320);
        splitPane.setResizeWeight(0.35);

        add(splitPane, BorderLayout.CENTER);
        add(panelCobro, BorderLayout.SOUTH);
    }

    // Panel izquierdo, busqueda y selección de productos
    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(new TitledBorder("Buscar producto"));

        // Campo de búsqueda
        campoBusquedaProducto = new JTextField();
        campoBusquedaProducto.putClientProperty("JTextField.placeholderText", "Nombre del producto...");
        campoBusquedaProducto.addActionListener(e -> buscarProductos());

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarProductos());

        JPanel panelBuscar = new JPanel(new BorderLayout(4, 0));
        panelBuscar.add(campoBusquedaProducto, BorderLayout.CENTER);
        panelBuscar.add(btnBuscar, BorderLayout.EAST);

        // Lista de resultados de busqueda
        modeloLista = new DefaultListModel<>();
        listaProductos = new JList<>(modeloLista);
        listaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollLista = new JScrollPane(listaProductos);

        // Cantidad
        JPanel panelCantidad = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCantidad.add(new JLabel("Cantidad:"));
        spinnerCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        panelCantidad.add(spinnerCantidad);

        JButton btnAnyadir = new JButton("Añadir al ticket");
        btnAnyadir.addActionListener(e -> anyadirAlTicket());
        panelCantidad.add(btnAnyadir);

        panel.add(panelBuscar, BorderLayout.NORTH);
        panel.add(scrollLista, BorderLayout.CENTER);
        panel.add(panelCantidad, BorderLayout.SOUTH);

        return panel;
    }

    // Panel derecho, ticket de la venta en curso
    private JPanel crearPanelTicket() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(new TitledBorder("Ticket"));

        // Tabla del ticket
        String[] columnas = {"Producto", "Cant.", "Precio u.", "Subtotal"};
        modeloTicket = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablaTicket = new JTable(modeloTicket);
        tablaTicket.setRowHeight(26);
        tablaTicket.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollTicket = new JScrollPane(tablaTicket);

        // Botón para eliminar línea seleccionada del ticket
        JButton btnEliminarLinea = new JButton("Eliminar línea");
        btnEliminarLinea.addActionListener(e -> eliminarLineaSeleccionada());

        // Etiqueta del total
        etiquetaTotal = new JLabel("Total: 0,00 €");
        etiquetaTotal.setFont(new Font("SansSerif", Font.BOLD, 16));
        etiquetaTotal.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(btnEliminarLinea, BorderLayout.WEST);
        panelSur.add(etiquetaTotal, BorderLayout.EAST);

        panel.add(scrollTicket, BorderLayout.CENTER);
        panel.add(panelSur, BorderLayout.SOUTH);

        return panel;
    }

    // Panel inferior, forma de pago y cobro.
    private JPanel crearPanelCobro() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        panel.setBorder(new TitledBorder("Cobro"));

        // Selector de cliente (opcional)
        panel.add(new JLabel("Cliente:"));
        comboCliente = new JComboBox<>();
        comboCliente.addItem(null); // anonimo
        for (Cliente c : clienteDAO.listarTodos()) {
            comboCliente.addItem(c);
        }
        // Mostrar nombre completo en el combo
        comboCliente.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cliente) {
                    setText(((Cliente) value).getNombreCompleto());
                } else {
                    setText("— Venta anónima —");
                }
                return this;
            }
        });
        comboCliente.setPreferredSize(new Dimension(200, 30));
        panel.add(comboCliente);

        // Selector de forma de pago
        panel.add(new JLabel("Forma de pago:"));
        comboPago = new JComboBox<>(new String[]{"efectivo", "tarjeta", "otros"});
        panel.add(comboPago);
        JButton btnCancelar = new JButton("Cancelar venta");
        JButton btnCobrar = new JButton("Cobrar");
        btnCobrar.setBackground(new Color(40, 140, 40));
        btnCobrar.setForeground(Color.WHITE);

        btnCancelar.addActionListener(e -> cancelarVenta());
        btnCobrar.addActionListener(e -> cobrar());

        panel.add(btnCancelar);
        panel.add(btnCobrar);

        return panel;
    }

    // Buscar producto por nombre
    private void buscarProductos() {
        String texto = campoBusquedaProducto.getText().trim();
        if (texto.isEmpty()) {
            return;
        }

        // Buscar en la BD y guardar los resultados
        productosEncontrados = productoDAO.buscarPorNombre(texto);
        modeloLista.clear();

        for (Producto p : productosEncontrados) {
            // Mostrar nombre, stock actual y precio de venta
            modeloLista.addElement(p.getNombre()
                    + " | Stock: " + p.getStockActual()
                    + " | " + String.format("%.2f €", p.getPrecioVenta()));
        }
    }

    // Añadir producto al ticket
    private void anyadirAlTicket() {
        int indice = listaProductos.getSelectedIndex();
        if (indice < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto de la lista.");
            return;
        }

        Producto producto = productosEncontrados.get(indice);
        int cantidad = (int) spinnerCantidad.getValue();

        // Comprobar que hay stock suficiente
        if (producto.getStockActual() < cantidad) {
            JOptionPane.showMessageDialog(this,
                    "Stock insuficiente. Disponible: " + producto.getStockActual());
            return;
        }

        // Calcular subtotal con el precio actual del producto
        double subtotal = producto.getPrecioVenta() * cantidad;

        // Crear la línea de venta temporal (id_venta = 0 hasta que se confirme el cobro)
        LineaVenta linea = new LineaVenta();
        linea.setIdProducto(producto.getIdProducto());
        linea.setCantidad(cantidad);
        linea.setPrecioUnitario(producto.getPrecioVenta());
        linea.setSubtotal(subtotal);

        // Añadir a la lista temporal y a la tabla visual
        lineasEnCurso.add(linea);
        modeloTicket.addRow(new Object[]{
            producto.getNombre(),
            cantidad,
            String.format("%.2f €", producto.getPrecioVenta()),
            String.format("%.2f €", subtotal)
        });

        actualizarTotal();
    }

    // Eliminar linea del ticket
    private void eliminarLineaSeleccionada() {
        int fila = tablaTicket.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una línea para eliminar.");
            return;
        }
        // Eliminar de la lista temporal y de la tabla visual
        lineasEnCurso.remove(fila);
        modeloTicket.removeRow(fila);
        actualizarTotal();
    }

    // Recalcular y mostrar el total del ticket
    private void actualizarTotal() {
        double total = 0;
        for (LineaVenta linea : lineasEnCurso) {
            total += linea.getSubtotal();
        }
        etiquetaTotal.setText(String.format("Total: %.2f €", total));
    }

    // Cancelar venta
    private void cancelarVenta() {
        if (lineasEnCurso.isEmpty()) {
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Cancelar la venta en curso?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            limpiarTicket();
        }
    }

    // Registrar el cobro y guardar en la base de datos
    private void cobrar() {
        if (lineasEnCurso.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El ticket está vacío.");
            return;
        }

        // Calcular el total final
        double total = 0;
        for (LineaVenta linea : lineasEnCurso) {
            total += linea.getSubtotal();
        }

        // Obtener el cliente seleccionado (puede ser null si es venta anónima)
        Cliente clienteSeleccionado = (Cliente) comboCliente.getSelectedItem();
        int idCliente = (clienteSeleccionado != null) ? clienteSeleccionado.getIdCliente() : 0;

        // Crear el objeto Venta
        Venta venta = new Venta();
        venta.setTotal(total);
        venta.setDescuento(0);
        venta.setFormaPago((String) comboPago.getSelectedItem());
        venta.setIdUsuario(usuarioActivo.getIdUsuario());
        venta.setIdCliente(idCliente);

        // Insertar la venta y obtener el ID generado
        int idVenta = ventaDAO.insertar(venta);
        if (idVenta < 0) {
            JOptionPane.showMessageDialog(this, "Error al registrar la venta.");
            return;
        }

        // Insertar cada línea de venta y actualizar el stock
        for (LineaVenta linea : lineasEnCurso) {
            linea.setIdVenta(idVenta);
            lineaVentaDAO.insertar(linea);

            // Obtener el producto actual y reducir su stock
            Producto producto = productoDAO.buscarPorId(linea.getIdProducto());
            int nuevoStock = producto.getStockActual() - linea.getCantidad();
            productoDAO.actualizarStock(linea.getIdProducto(), nuevoStock);
        }

        // Actualizar el total en la tabla venta
        ventaDAO.actualizarTotal(idVenta, total);

        JOptionPane.showMessageDialog(this,
                String.format("Venta registrada correctamente.\nTotal cobrado: %.2f €", total));

        limpiarTicket();
    }

    // Limpiar el ticket para poder hacer nuevas ventas
    private void limpiarTicket() {
        lineasEnCurso.clear();
        modeloTicket.setRowCount(0);
        etiquetaTotal.setText("Total: 0,00 €");
        campoBusquedaProducto.setText("");
        modeloLista.clear();
        spinnerCantidad.setValue(1);
        comboCliente.setSelectedIndex(0);
    }
}
