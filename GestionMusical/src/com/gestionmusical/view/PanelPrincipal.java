package com.gestionmusical.view;

import com.gestionmusical.model.Usuario;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/*Ventana principal de la aplicación. Se muestra tras el login correcto.*/
public class PanelPrincipal extends JFrame {

    private final Usuario usuarioActivo;
    private JPanel panelContenido;

    public PanelPrincipal(Usuario usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
        initComponents();
    }

    private void initComponents() {
        setTitle("GestionMusical");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 680));
        setLocationRelativeTo(null);

        // Barra lateral izquierda.
        JPanel barraLateral = new JPanel();
        barraLateral.setLayout(new BoxLayout(barraLateral, BoxLayout.Y_AXIS));
        barraLateral.setPreferredSize(new Dimension(200, 0));
        barraLateral.setBorder(new EmptyBorder(20, 10, 20, 10));
        barraLateral.setBackground(new Color(40, 40, 40));

        // Campo nombre de usuario.
        JLabel lblUsuario = new JLabel(usuarioActivo.getNombreCompleto());
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblRol = new JLabel(usuarioActivo.getRol());
        lblRol.setForeground(Color.LIGHT_GRAY);
        lblRol.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblRol.setAlignmentX(Component.LEFT_ALIGNMENT);

        //Botones del menú
        JButton btnProductos = crearBotonMenu("Productos");
        JButton btnClientes = crearBotonMenu("Clientes");
        JButton btnVentas = crearBotonMenu("Ventas");
        JButton btnDevoluciones = crearBotonMenu("Devoluciones");
        JButton btnInformes = crearBotonMenu("Informes");
        JButton btnCerrarSesion = crearBotonMenu("Cerrar sesión");

        //acciones de los botones
        btnProductos.addActionListener(e -> mostrarPanel(new ProductoPanel()));
        btnClientes.addActionListener(e -> mostrarPanel(new ClientePanel()));
        btnVentas.addActionListener(e -> mostrarPanel(new VentaPanel(usuarioActivo)));
        btnDevoluciones.addActionListener(e -> mostrarPanel(new JLabel("Módulo Devoluciones")));
        btnInformes.addActionListener(e -> mostrarPanel(new JLabel("Módulo Informes")));
        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        // ocultar botones que son solo para el administrador
        btnInformes.setVisible(usuarioActivo.esAdmin());

        // añadir a la barra lateral
        barraLateral.add(lblUsuario);
        barraLateral.add(Box.createVerticalStrut(2));
        barraLateral.add(lblRol);
        barraLateral.add(Box.createVerticalStrut(24));
        barraLateral.add(btnProductos);
        barraLateral.add(Box.createVerticalStrut(6));
        barraLateral.add(btnClientes);
        barraLateral.add(Box.createVerticalStrut(6));
        barraLateral.add(btnVentas);
        barraLateral.add(Box.createVerticalStrut(6));
        barraLateral.add(btnDevoluciones);
        barraLateral.add(Box.createVerticalStrut(6));
        barraLateral.add(btnInformes);
        barraLateral.add(Box.createGlue());
        barraLateral.add(btnCerrarSesion);

        // Panel central
        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel bienvenida = new JLabel("Bienvenido, " + usuarioActivo.getNombreCompleto());
        bienvenida.setFont(new Font("SansSerif", Font.PLAIN, 16));
        bienvenida.setHorizontalAlignment(SwingConstants.CENTER);
        panelContenido.add(bienvenida, BorderLayout.CENTER);

        // Ventana
        setLayout(new BorderLayout());
        add(barraLateral, BorderLayout.WEST);
        add(panelContenido, BorderLayout.CENTER);

        pack();
    }

    // Cambiar el contenido del panel central
    private void mostrarPanel(JComponent componente) {
        panelContenido.removeAll();
        panelContenido.add(componente, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    // crear el botón del menu
    private JButton crearBotonMenu(String texto) {
        JButton boton = new JButton(texto);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setForeground(Color.WHITE);
        boton.setBackground(new Color(60, 60, 60));
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    // Cerrar sesion
    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que quieres cerrar sesión?",
                "Cerrar sesión",
                JOptionPane.YES_NO_OPTION
        );
        if (opcion == JOptionPane.YES_OPTION) {
            new LoginView().setVisible(true);
            dispose();
        }
    }
}
