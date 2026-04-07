
import com.gestionmusical.database.DatabaseManager;
import com.gestionmusical.util.TemaInterfaz;
import javax.swing.SwingUtilities;
import com.gestionmusical.view.LoginView;


public class Main {

    public static void main(String[] args) {
       // Aplicación del Look and feel "Flatlaf"
        TemaInterfaz.aplicarTema();

        // Iniciar base de datos
        DatabaseManager.getInstance();
        
        // Cerrar conexión al salir según lo indicado por Raúl.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        DatabaseManager.getInstance().cerrarConexion();
        System.out.println("Conexión cerrada correctamente.");
        }));
        
        
        // Lanzar la interfaz grafica
         SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}