

import com.gestionmusical.database.DatabaseManager;
import com.gestionmusical.util.TemaInterfaz;
import javax.swing.SwingUtilities;


public class Main {

    public static void main(String[] args) {
       // Aplicación del Look and feel "Flatlaf"
        TemaInterfaz.aplicarTema();

        // Iniciar base de datos
        DatabaseManager.getInstance();

        // Lanzar la interfaz grafica
        SwingUtilities.invokeLater(() -> {
            System.out.println("GestionMusical arrancado correctamente.");
        });
    }
}