package com.gestionmusical.view;

import com.gestionmusical.dao.CategoriaDAO;
import com.gestionmusical.dao.ProductoDAO;
import com.gestionmusical.model.Categoria;
import com.gestionmusical.model.Producto;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/*Panel de gestión de productos.
 Muestra la lista de productos, permite añadir, editar y desactivar,
 y alerta de los productos con stock bajo mínimo.*/
public class ProductoPanel extends JPanel {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;
    private JLabel etiquetaAlerta;

    public ProductoPanel() {
        initComponents();
        cargarProductos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Barra superior con busqueda y botones
        JPanel barraTop = new JPanel(new BorderLayout(10, 0));

        campoBusqueda = new JTextField();
        campoBusqueda.setPreferredSize(new Dimension(220, 32));
        campoBusqueda.putClientProperty("JTextField.placeholderText", "Buscar producto...");
        campoBusqueda.addActionListener(e -> buscar());

        JButton btnBuscar = new JButton("Buscar");
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnDesactivar = new JButton("Desactivar");
        JButton btnTodos = new JButton("Mostrar todos");

        btnBuscar.addActionListener(e -> buscar());
        btnNuevo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> editarSeleccionado());
        btnDesactivar.addActionListener(e -> desactivarSeleccionado());
        btnTodos.addActionListener(e -> cargarProductos());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelBotones.add(campoBusqueda);
        panelBotones.add(btnBuscar);
        panelBotones.add(btnTodos);
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnDesactivar);

        // aleta de stock
        etiquetaAlerta = new JLabel(" ");
        etiquetaAlerta.setForeground(new Color(180, 60, 0));
        etiquetaAlerta.setFont(new Font("SansSerif", Font.BOLD, 12));

        barraTop.add(etiquetaAlerta, BorderLayout.WEST);
        barraTop.add(panelBotones, BorderLayout.EAST);

        // Tabla de productos
        String[] columnas = {"ID", "Nombre", "Categoría", "Precio venta", "Stock", "Stock mín.", "Proveedor"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(26);
        tabla.getColumnModel().getColumn(0).setMaxWidth(50);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tabla);

        add(barraTop, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    // Cargar los productos activos
    private void cargarProductos() {
        List<Producto> productos = productoDAO.listarTodos();
        rellenarTabla(productos);
        actualizarAlerta();
    }

    // Buscar por nombre de producto
    private void buscar() {
        String texto = campoBusqueda.getText().trim();
        if (texto.isEmpty()) {
            cargarProductos();
            return;
        }
        List<Producto> productos = productoDAO.buscarPorNombre(texto);
        rellenarTabla(productos);
    }

    // Rellenar tabla con lista de productos
    private void rellenarTabla(List<Producto> productos) {
        modeloTabla.setRowCount(0);
        for (Producto p : productos) {
            Categoria cat = categoriaDAO.buscarPorId(p.getIdCategoria());
            String nombreCat;
            if (cat != null) {
                nombreCat = cat.getNombre();
            } else {
                nombreCat = "—";
            }

            Object[] fila = {
                p.getIdProducto(),
                p.getNombre(),
                nombreCat,
                String.format("%.2f €", p.getPrecioVenta()),
                p.getStockActual(),
                p.getStockMinimo(),
                p.getProveedor()
            };
            modeloTabla.addRow(fila);

            // Colorear en naranja las filas con stock bajo mínimo
            if (p.stockBajoMinimo()) {
                int fila_ = modeloTabla.getRowCount() - 1;
                tabla.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable t, Object val,
                            boolean sel, boolean foc, int row, int col) {
                        Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                        int id = (int) t.getValueAt(row, 0);
                        Producto prod = productoDAO.buscarPorId(id);
                        if (prod != null && prod.stockBajoMinimo()) {
                            c.setBackground(sel ? new Color(220, 150, 80) : new Color(255, 235, 200));
                            c.setForeground(new Color(120, 40, 0));
                        } else {
                            c.setBackground(sel ? t.getSelectionBackground() : t.getBackground());
                            c.setForeground(sel ? t.getSelectionForeground() : t.getForeground());
                        }
                        return c;
                    }
                });
            }
        }
    }

    // Alerta cuando el stock está por debajo del mínimo
    private void actualizarAlerta() {
        List<Producto> bajos = productoDAO.listarBajoMinimo();
        if (!bajos.isEmpty()) {
            etiquetaAlerta.setText("⚠ " + bajos.size() + " producto(s) con stock bajo mínimo");
        } else {
            etiquetaAlerta.setText(" ");
        }
    }

    // Editar un producto seleccionado
    private void editarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para editar.");
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        Producto p = productoDAO.buscarPorId(id);
        abrirFormulario(p);
    }

    // Desactivar un producto
    private void desactivarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para desactivar.");
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        String nombre = (String) modeloTabla.getValueAt(fila, 1);

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Desactivar el producto \"" + nombre + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            productoDAO.desactivar(id);
            cargarProductos();
        }
    }

    // Formulario de nuevo producto
    private void abrirFormulario(Producto producto) {
        List<Categoria> categorias = categoriaDAO.listarTodas();

        JTextField txtNombre = new JTextField(producto != null ? producto.getNombre() : "");
        JTextField txtDescripcion = new JTextField(producto != null ? producto.getDescripcion() : "");
        JTextField txtProveedor = new JTextField(producto != null ? producto.getProveedor() : "");
        JTextField txtPrecioCompra = new JTextField(producto != null ? String.valueOf(producto.getPrecioCompra()) : "");
        JTextField txtPrecioVenta = new JTextField(producto != null ? String.valueOf(producto.getPrecioVenta()) : "");
        JTextField txtStockActual = new JTextField(producto != null ? String.valueOf(producto.getStockActual()) : "0");
        JTextField txtStockMinimo = new JTextField(producto != null ? String.valueOf(producto.getStockMinimo()) : "5");
        JComboBox<Categoria> cbCategoria = new JComboBox<>(categorias.toArray(new Categoria[0]));

        if (producto != null) {
            for (int i = 0; i < categorias.size(); i++) {
                if (categorias.get(i).getIdCategoria() == producto.getIdCategoria()) {
                    cbCategoria.setSelectedIndex(i);
                    break;
                }
            }
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Nombre:"));
        form.add(txtNombre);
        form.add(new JLabel("Descripción:"));
        form.add(txtDescripcion);
        form.add(new JLabel("Proveedor:"));
        form.add(txtProveedor);
        form.add(new JLabel("Precio compra:"));
        form.add(txtPrecioCompra);
        form.add(new JLabel("Precio venta:"));
        form.add(txtPrecioVenta);
        form.add(new JLabel("Stock actual:"));
        form.add(txtStockActual);
        form.add(new JLabel("Stock mínimo:"));
        form.add(txtStockMinimo);
        form.add(new JLabel("Categoría:"));
        form.add(cbCategoria);

        String titulo = (producto == null) ? "Nuevo producto" : "Editar producto";
        int resultado = JOptionPane.showConfirmDialog(this, form, titulo, JOptionPane.OK_CANCEL_OPTION);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                Producto p = (producto != null) ? producto : new Producto();
                p.setNombre(txtNombre.getText().trim());
                p.setDescripcion(txtDescripcion.getText().trim());
                p.setProveedor(txtProveedor.getText().trim());
                p.setPrecioCompra(Double.parseDouble(txtPrecioCompra.getText().trim()));
                p.setPrecioVenta(Double.parseDouble(txtPrecioVenta.getText().trim()));
                p.setStockActual(Integer.parseInt(txtStockActual.getText().trim()));
                p.setStockMinimo(Integer.parseInt(txtStockMinimo.getText().trim()));
                p.setIdCategoria(((Categoria) cbCategoria.getSelectedItem()).getIdCategoria());

                if (producto == null) {
                    productoDAO.insertar(p);
                } else {
                    productoDAO.actualizar(p);
                }
                cargarProductos();

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Los campos numéricos deben contener números válidos.");
            }
        }
    }
}
