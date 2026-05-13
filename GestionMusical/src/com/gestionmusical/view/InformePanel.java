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

        JButton btnExportarPDF = new JButton("Exportar PDF");
        JButton btnExportarExcel = new JButton("Exportar Excel");

        btnExportarPDF.addActionListener(e -> exportarPDF());
        btnExportarExcel.addActionListener(e -> exportarExcel());

        panelFiltro.add(btnExportarPDF);
        panelFiltro.add(btnExportarExcel);

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

        // Panel inferior con botones de exportación
        JPanel panelExportar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        btnExportarPDF.addActionListener(e -> exportarPDF());
        btnExportarExcel.addActionListener(e -> exportarExcel());
        panelExportar.add(btnExportarPDF);
        panelExportar.add(btnExportarExcel);

        add(panelNorte, BorderLayout.NORTH);
        add(pestanyas, BorderLayout.CENTER);
        add(panelExportar, BorderLayout.SOUTH);
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

    // Exportar informe de ventas a PDF
    private void exportarPDF() {
        String fechaInicio = campoFechaInicio.getText().trim();
        String fechaFin = campoFechaFin.getText().trim();

        if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Introduce las fechas antes de exportar.");
            return;
        }

        // Selector de archivo donde guardar el PDF
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new java.io.File("informe_ventas.pdf"));
        int opcion = selector.showSaveDialog(this);
        if (opcion != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String ruta = selector.getSelectedFile().getAbsolutePath();
        if (!ruta.endsWith(".pdf")) {
            ruta += ".pdf";
        }

        try {
            com.itextpdf.text.Document documento = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(documento, new java.io.FileOutputStream(ruta));
            documento.open();

            // Título
            com.itextpdf.text.Font fuenteTitulo = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 16,
                    com.itextpdf.text.Font.BOLD);
            documento.add(new com.itextpdf.text.Paragraph(
                    "Informe de ventas — " + fechaInicio + " a " + fechaFin, fuenteTitulo));
            documento.add(new com.itextpdf.text.Paragraph(" "));

            // Tabla de ventas
            com.itextpdf.text.pdf.PdfPTable tabla = new com.itextpdf.text.pdf.PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.addCell("ID Venta");
            tabla.addCell("Fecha");
            tabla.addCell("Total");
            tabla.addCell("Forma de pago");

            for (int i = 0; i < modeloVentas.getRowCount(); i++) {
                tabla.addCell(modeloVentas.getValueAt(i, 0).toString());
                tabla.addCell(modeloVentas.getValueAt(i, 1).toString());
                tabla.addCell(modeloVentas.getValueAt(i, 2).toString());
                tabla.addCell(modeloVentas.getValueAt(i, 4).toString());
            }

            documento.add(tabla);
            documento.close();

            JOptionPane.showMessageDialog(this, "PDF exportado correctamente:\n" + ruta);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al exportar PDF: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

// Exportar informe de ventas a Excel
    private void exportarExcel() {
        String fechaInicio = campoFechaInicio.getText().trim();
        String fechaFin = campoFechaFin.getText().trim();

        if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Introduce las fechas antes de exportar.");
            return;
        }

        // Selector de archivo donde guardar el Excel
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new java.io.File("informe_ventas.xlsx"));
        int opcion = selector.showSaveDialog(this);
        if (opcion != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String ruta = selector.getSelectedFile().getAbsolutePath();
        if (!ruta.endsWith(".xlsx")) {
            ruta += ".xlsx";
        }

        try {
            org.apache.poi.xssf.usermodel.XSSFWorkbook libro
                    = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet hoja = libro.createSheet("Ventas");

            // Cabecera
            org.apache.poi.ss.usermodel.Row cabecera = hoja.createRow(0);
            cabecera.createCell(0).setCellValue("ID Venta");
            cabecera.createCell(1).setCellValue("Fecha");
            cabecera.createCell(2).setCellValue("Total");
            cabecera.createCell(3).setCellValue("Forma de pago");

            // Datos
            for (int i = 0; i < modeloVentas.getRowCount(); i++) {
                org.apache.poi.ss.usermodel.Row fila = hoja.createRow(i + 1);
                fila.createCell(0).setCellValue(modeloVentas.getValueAt(i, 0).toString());
                fila.createCell(1).setCellValue(modeloVentas.getValueAt(i, 1).toString());
                fila.createCell(2).setCellValue(modeloVentas.getValueAt(i, 2).toString());
                fila.createCell(3).setCellValue(modeloVentas.getValueAt(i, 4).toString());
            }

            // Ajustar ancho de columnas
            for (int i = 0; i < 4; i++) {
                hoja.autoSizeColumn(i);
            }

            // Guardar archivo
            java.io.FileOutputStream salida = new java.io.FileOutputStream(ruta);
            libro.write(salida);
            salida.close();
            libro.close();

            JOptionPane.showMessageDialog(this, "Excel exportado correctamente:\n" + ruta);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al exportar Excel: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
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
