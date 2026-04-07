package com.gestionmusical.view;

import com.gestionmusical.controller.LoginController;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/*Pantalla de inicio de sesión.*/
public class LoginView extends JFrame {

    private JTextField     campoUsuario;
    private JPasswordField campoClave;
    private JButton        botonEntrar;
    private JLabel         etiquetaError;

    public LoginView() {
        initComponents();
    }

    private void initComponents() {
        setTitle("GestionMusical — Acceso");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Panel principal con margen interno
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(40, 50, 40, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(4, 0, 4, 0);

        // Título
        JLabel titulo = new JLabel("GestionMusical", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        gbc.gridy = 0;
        panel.add(titulo, gbc);

        // Subtítulo
        JLabel subtitulo = new JLabel("Inicia sesión para continuar", SwingConstants.CENTER);
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(Color.GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 24, 0);
        panel.add(subtitulo, gbc);

        // Etiqueta usuario
        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel.add(lblUsuario, gbc);

        // Campo usuario
        campoUsuario = new JTextField();
        campoUsuario.setPreferredSize(new Dimension(260, 36));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 12, 0);
        panel.add(campoUsuario, gbc);

        // Etiqueta contraseña
        JLabel lblClave = new JLabel("Contraseña");
        lblClave.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel.add(lblClave, gbc);

        // Campo contraseña
        campoClave = new JPasswordField();
        campoClave.setPreferredSize(new Dimension(260, 36));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(campoClave, gbc);

        // Etiqueta error
        etiquetaError = new JLabel(" ", SwingConstants.CENTER);
        etiquetaError.setForeground(Color.RED);
        etiquetaError.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(etiquetaError, gbc);

        // Botón entrar
        botonEntrar = new JButton("Entrar");
        botonEntrar.setPreferredSize(new Dimension(260, 38));
        botonEntrar.addActionListener(e -> autenticar());
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(botonEntrar, gbc);

        // Pulsr enter en el teclado tambien funciona como pulsar el botón
        campoClave.addActionListener(e -> autenticar());

        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
    }

    private void autenticar() {
        String usuario = campoUsuario.getText().trim();
        String clave   = new String(campoClave.getPassword());

        if (usuario.isEmpty() || clave.isEmpty()) {
            etiquetaError.setText("Introduce usuario y contraseña.");
            return;
        }

        LoginController controller = new LoginController();
        boolean ok = controller.login(usuario, clave, this);

        if (!ok) {
            etiquetaError.setText("Usuario o contraseña incorrectos.");
            campoClave.setText("");
            campoClave.requestFocus();
        }
    }
}