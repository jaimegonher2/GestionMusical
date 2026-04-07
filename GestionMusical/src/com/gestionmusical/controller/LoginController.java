package com.gestionmusical.controller;

import com.gestionmusical.dao.UsuarioDAO;
import com.gestionmusical.model.Usuario;
import com.gestionmusical.view.LoginView;
import com.gestionmusical.view.PanelPrincipal;

//

public class LoginController {

    private final UsuarioDAO usuarioDAO;

    public LoginController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /*Autentica al usuario. Si las credenciales son correctas abre
     el Panel principal y cierra la ventana de login.*/
    public boolean login(String nombreUsuario, String contrasena, LoginView loginView) {
        Usuario usuario = usuarioDAO.login(nombreUsuario, contrasena);
        System.out.println("Usuario encontrado: " + usuario);

        if (usuario != null) {
            PanelPrincipal panelPrincipal = new PanelPrincipal(usuario);
            panelPrincipal.setVisible(true);
            loginView.dispose();
            return true;
        }

        return false;
    }
}