package com.gestionmusical.util;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;

//Configura el Look and Feel de la aplicación.

public class TemaInterfaz {

    public static void aplicarTema() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Error al aplicar el tema FlatLaf: " + e.getMessage());
        }
    }
}