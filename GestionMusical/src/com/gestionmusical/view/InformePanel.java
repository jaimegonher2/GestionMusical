package com.gestionmusical.view;

import com.gestionmusical.dao.ProductoDAO;
import com.gestionmusical.dao.VentaDAO;
import com.gestionmusical.dao.LineaVentaDAO;
import com.gestionmusical.model.Producto;
import com.gestionmusical.model.Venta;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Panel de informes. Muestra ventas por período, ranking de productos más
 * vendidos y listado de productos con stock bajo mínimo.
 */
public class InformePanel extends JPanel {

    // DAOs necesarios
    private final VentaDAO ventaDAO = new VentaDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final LineaVentaDAO lineaVentaDAO = new LineaVentaDAO();

    // Componentes de filtro por fechas
    private JTextField campoFechaInicio;
    private JTextField campoFechaFin;

    // Tablas de resultados
    private JTable tablaVentas;
    private DefaultTableModel modeloVentas;

    private JTable tablaRanking;
    private DefaultTableModel modeloRanking;

    private JTable tablaStockBajo;
    private DefaultTableModel modeloStockBajo;

    // Etiqueta de resumen
    private JLabel etiquetaResumen;

    public InformePanel() {
        initComponents();
        cargarInformesPorDefecto();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel de filtro de fechas
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelFiltro.setBorder(new TitledBorder("Filtro por período"));

        panelFiltro.add(new JLabel("Desde (YYYY-MM-DD):"));
        campoFechaInicio = new JTextField(12);
        panelFiltro.add(campoFechaInicio);

        panelFiltro.add(new JLabel("Hasta (YYYY-MM-DD):"));
        campoFechaFin = new JTextField(12);
        panelFiltro.add(campoFechaFin);

        JButton btnFiltrar = new JButton("Generar informe");
        btnFiltrar.addActionListener(e -> generarInforme());
        panelFiltro.add(btnFiltrar);

        // Etiqueta de resumen en su propia fila
        etiquetaResumen = new JLabel(" ");
        etiquetaResumen.setFont(new Font("SansSerif", Font.BOLD, 13));
        etiquetaResumen.setBorder(new EmptyBorder(4, 4, 4, 4));

        // Panel norte que agrupa filtro y resumen en dos filas
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelFiltro, BorderLayout.NORTH);
        panelNorte.add(etiquetaResumen, BorderLayout.SOUTH);

        // Panel central: tres pestañas
        JTabbedPane pestanyas = new JTabbedPane();

        // Pestaña 1: Ventas del período
        String[] columnasVentas = {"ID", "Fecha", "Total", "Descuento", "Forma pago", "ID Cliente"};
        modeloVentas = new DefaultTableModel(columnasVentas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaVentas = new JTable(modeloVentas);
        tablaVentas.setRowHeight(26);
        tablaVentas.getTableHeader().setReorderingAllowed(false);
        pestanyas.addTab("Ventas del período", new JScrollPane(tablaVentas));

        // Pestaña 2: Ranking de productos más vendidos
        String[] columnasRanking = {"Producto", "Unidades vendidas", "Total facturado"};
        modeloRanking = new DefaultTableModel(columnasRanking, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaRanking = new JTable(modeloRanking);
        tablaRanking.setRowHeight(26);
        tablaRanking.getTableHeader().setReorderingAllowed(false);
        pestanyas.addTab("Ranking de productos", new JScrollPane(tablaRanking));

        // Pestaña 3: Stock bajo mínimo
        String[] columnasStock = {"ID", "Nombre", "Stock actual", "Stock mínimo", "Diferencia"};
        modeloStockBajo = new DefaultTableModel(columnasStock, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaStockBajo = new JTable(modeloStockBajo);
        tablaStockBajo.setRowHeight(26);
        tablaStockBajo.getTableHeader().setReorderingAllowed(false);
        pestanyas.addTab("Stock bajo mínimo", new JScrollPane(tablaStockBajo));

        add(panelNorte, BorderLayout.NORTH);
        add(pestanyas, BorderLayout.CENTER);
    }

    // Cargar informes con el mes actual al abrir el panel
    private void cargarInformesPorDefecto() {
        LocalDate hoy = LocalDate.now();
        LocalDate primerDia = hoy.withDayOfMonth(1);
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        campoFechaInicio.setText(primerDia.format(formato));
        campoFechaFin.setText(hoy.format(formato));

        generarInforme();
    }

    // Generar el informe con las fechas introducidas
    private void generarInforme() {
        String fechaInicio = campoFechaInicio.getText().trim();
        String fechaFin = campoFechaFin.getText().trim();

        if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Introduce las fechas de inicio y fin.");
            return;
        }

        cargarVentasPorPeriodo(fechaInicio, fechaFin);
        cargarRankingProductos(fechaInicio, fechaFin);
        cargarStockBajoMinimo();
    }

    // Cargar ventas del período en la tabla
    private void cargarVentasPorPeriodo(String fechaInicio, String fechaFin) {
        List<Venta> ventas = ventaDAO.listarPorFechas(
                fechaInicio + " 00:00:00",
                fechaFin + " 23:59:59"
        );

        modeloVentas.setRowCount(0);
        double totalPeriodo = 0;

        for (Venta v : ventas) {
            String idCliente;
            if (v.getIdCliente() > 0) {
                idCliente = String.valueOf(v.getIdCliente());
            } else {
                idCliente = "Anónima";
            }
            modeloVentas.addRow(new Object[]{
                v.getIdVenta(),
                v.getFechaHora(),
                String.format("%.2f €", v.getTotal()),
                String.format("%.2f €", v.getDescuento()),
                v.getFormaPago(),
                idCliente
            });
            totalPeriodo += v.getTotal();
        }

        etiquetaResumen.setText(
                ventas.size() + " ventas | Total: " + String.format("%.2f €", totalPeriodo)
        );
    }

    // Calcular ranking de productos más vendidos
    private void cargarRankingProductos(String fechaInicio, String fechaFin) {
        List<Venta> ventas = ventaDAO.listarPorFechas(
                fechaInicio + " 00:00:00",
                fechaFin + " 23:59:59"
        );

        Map<Integer, Integer> unidadesPorProducto = new HashMap<>();
        Map<Integer, Double> facturacionPorProducto = new HashMap<>();

        for (Venta v : ventas) {
            var lineas = lineaVentaDAO.listarPorVenta(v.getIdVenta());
            for (var linea : lineas) {
                int idProd = linea.getIdProducto();

                int unidades = unidadesPorProducto.getOrDefault(idProd, 0);
                unidadesPorProducto.put(idProd, unidades + linea.getCantidad());

                double facturacion = facturacionPorProducto.getOrDefault(idProd, 0.0);
                facturacionPorProducto.put(idProd, facturacion + linea.getSubtotal());
            }
        }

        modeloRanking.setRowCount(0);
        unidadesPorProducto.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(entry -> {
                    int idProd = entry.getKey();
                    Producto producto = productoDAO.buscarPorId(idProd);
                    String nombre;
                    if (producto != null) {
                        nombre = producto.getNombre();
                    } else {
                        nombre = "ID: " + idProd;
                    }
                    modeloRanking.addRow(new Object[]{
                        nombre,
                        entry.getValue(),
                        String.format("%.2f €", facturacionPorProducto.get(idProd))
                    });
                });
    }

    // Cargar productos con stock bajo mínimo
    private void cargarStockBajoMinimo() {
        List<Producto> productos = productoDAO.listarBajoMinimo();
        modeloStockBajo.setRowCount(0);
        for (Producto p : productos) {
            int diferencia = p.getStockMinimo() - p.getStockActual();
            modeloStockBajo.addRow(new Object[]{
                p.getIdProducto(),
                p.getNombre(),
                p.getStockActual(),
                p.getStockMinimo(),
                diferencia
            });
        }
    }
}

