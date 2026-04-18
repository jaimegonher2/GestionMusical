package com.gestionmusical.view;

import com.gestionmusical.dao.ClienteDAO;
import com.gestionmusical.model.Cliente;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/*Panel de gestión de clientes.
 Permite listar, buscar, crear, editar y desactivar clientes.*/
public class ClientePanel extends JPanel {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;

    public ClientePanel() {
        initComponents();
        cargarClientes();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Barra superior
        JPanel barraTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));

        campoBusqueda = new JTextField();
        campoBusqueda.setPreferredSize(new Dimension(220, 32));
        campoBusqueda.putClientProperty("JTextField.placeholderText", "Buscar cliente...");
        campoBusqueda.addActionListener(e -> buscar());

        JButton btnBuscar = new JButton("Buscar");
        JButton btnTodos = new JButton("Mostrar todos");
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnDesactivar = new JButton("Desactivar");

        btnBuscar.addActionListener(e -> buscar());
        btnTodos.addActionListener(e -> cargarClientes());
        btnNuevo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> editarSeleccionado());
        btnDesactivar.addActionListener(e -> desactivarSeleccionado());

        barraTop.add(campoBusqueda);
        barraTop.add(btnBuscar);
        barraTop.add(btnTodos);
        barraTop.add(btnNuevo);
        barraTop.add(btnEditar);
        barraTop.add(btnDesactivar);

        // Tabla de clientes
        String[] columnas = {"ID", "Apellidos", "Nombre", "Teléfono", "Email", "Dirección"};
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

    // Cargar todos los clientes activos
    private void cargarClientes() {
        List<Cliente> clientes = clienteDAO.listarTodos();
        rellenarTabla(clientes);
    }

    // Buscar por nombre o apellido
    private void buscar() {
        String texto = campoBusqueda.getText().trim();
        if (texto.isEmpty()) {
            cargarClientes();
            return;
        }
        List<Cliente> clientes = clienteDAO.buscarPorNombre(texto);
        rellenarTabla(clientes);
    }

    // Rellenar tabla
    private void rellenarTabla(List<Cliente> clientes) {
        modeloTabla.setRowCount(0);
        for (Cliente c : clientes) {
            modeloTabla.addRow(new Object[]{
                c.getIdCliente(),
                c.getApellidos(),
                c.getNombre(),
                c.getTelefono(),
                c.getEmail(),
                c.getDireccion()
            });
        }
    }

    // Editar un cliente seleccionado
    private void editarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente para editar.");
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        Cliente c = clienteDAO.buscarPorId(id);
        abrirFormulario(c);
    }

    // Desactivar un cliente seleccionado
    private void desactivarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente para desactivar.");
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        String nombre = modeloTabla.getValueAt(fila, 2) + " " + modeloTabla.getValueAt(fila, 1);

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Desactivar al cliente \"" + nombre + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            clienteDAO.desactivar(id);
            cargarClientes();
        }
    }

    // Abrir formulario nuevo o editar un cliente
    private void abrirFormulario(Cliente cliente) {
        JTextField txtNombre = new JTextField(cliente != null ? cliente.getNombre() : "");
        JTextField txtApellidos = new JTextField(cliente != null ? cliente.getApellidos() : "");
        JTextField txtTelefono = new JTextField(cliente != null ? cliente.getTelefono() : "");
        JTextField txtEmail = new JTextField(cliente != null ? cliente.getEmail() : "");
        JTextField txtDireccion = new JTextField(cliente != null ? cliente.getDireccion() : "");
        JTextField txtObservaciones = new JTextField(cliente != null ? cliente.getObservaciones() : "");

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Nombre:"));
        form.add(txtNombre);
        form.add(new JLabel("Apellidos:"));
        form.add(txtApellidos);
        form.add(new JLabel("Teléfono:"));
        form.add(txtTelefono);
        form.add(new JLabel("Email:"));
        form.add(txtEmail);
        form.add(new JLabel("Dirección:"));
        form.add(txtDireccion);
        form.add(new JLabel("Observaciones:"));
        form.add(txtObservaciones);

        String titulo = (cliente == null) ? "Nuevo cliente" : "Editar cliente";
        int resultado = JOptionPane.showConfirmDialog(this, form, titulo, JOptionPane.OK_CANCEL_OPTION);

        if (resultado == JOptionPane.OK_OPTION) {
            String nombre = txtNombre.getText().trim();
            String apellidos = txtApellidos.getText().trim();

            if (nombre.isEmpty() || apellidos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre y apellidos son obligatorios.");
                return;
            }

            Cliente c = (cliente != null) ? cliente : new Cliente();
            c.setNombre(nombre);
            c.setApellidos(apellidos);
            c.setTelefono(txtTelefono.getText().trim());
            c.setEmail(txtEmail.getText().trim());
            c.setDireccion(txtDireccion.getText().trim());
            c.setObservaciones(txtObservaciones.getText().trim());

            if (cliente == null) {
                clienteDAO.insertar(c);
            } else {
                clienteDAO.actualizar(c);
            }
            cargarClientes();
        }
    }
}
