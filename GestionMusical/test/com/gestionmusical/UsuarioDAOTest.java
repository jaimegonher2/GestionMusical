package com.gestionmusical;

import com.gestionmusical.dao.UsuarioDAO;
import com.gestionmusical.database.DatabaseManager;
import com.gestionmusical.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*Tests para UsuarioDAO.
 Verifica el hash SHA-256 y el proceso de autenticación.*/
public class UsuarioDAOTest {

    private UsuarioDAO usuarioDAO;

    @BeforeEach
    void setUp() {
        // Inicializar la BD antes de cada test
        DatabaseManager.getInstance();
        usuarioDAO = new UsuarioDAO();
    }

    // Test 1: El hash SHA-256 siempre produce 64 caracteres
    @Test
    void hashSHA256_longitudCorrecta() {
        String hash = UsuarioDAO.hashSHA256("admin123");
        assertEquals(64, hash.length(),
            "El hash SHA-256 debe tener exactamente 64 caracteres hexadecimales");
    }

    // Test 2: El hash SHA-256 es determinista (misma entrada = misma salida)
    @Test
    void hashSHA256_esDeterminista() {
        String hash1 = UsuarioDAO.hashSHA256("admin123");
        String hash2 = UsuarioDAO.hashSHA256("admin123");
        assertEquals(hash1, hash2,
            "El mismo texto siempre debe producir el mismo hash");
    }

    // Test 3: Contraseñas distintas producen hashes distintos
    @Test
    void hashSHA256_distingueContrasenas() {
        String hash1 = UsuarioDAO.hashSHA256("admin123");
        String hash2 = UsuarioDAO.hashSHA256("admin456");
        assertNotEquals(hash1, hash2,
            "Contraseñas distintas deben producir hashes distintos");
    }

    // Test 4: Login correcto devuelve un Usuario válido
    @Test
    void login_credencialesCorrectas_devuelveUsuario() {
        Usuario usuario = usuarioDAO.login("admin", "admin123");
        assertNotNull(usuario,
            "El login con credenciales correctas debe devolver un Usuario");
    }

    // Test 5: Login correcto devuelve el usuario con rol admin
    @Test
    void login_credencialesCorrectas_rolCorrecto() {
        Usuario usuario = usuarioDAO.login("admin", "admin123");
        assertNotNull(usuario);
        assertEquals("admin", usuario.getRol(),
            "El usuario admin debe tener rol 'admin'");
    }

    // Test 6: Login con contraseña incorrecta devuelve null
    @Test
    void login_contrasenaIncorrecta_devuelveNull() {
        Usuario usuario = usuarioDAO.login("admin", "contrasenaErronea");
        assertNull(usuario,
            "El login con contraseña incorrecta debe devolver null");
    }

    // Test 7: Login con usuario inexistente devuelve null
    @Test
    void login_usuarioInexistente_devuelveNull() {
        Usuario usuario = usuarioDAO.login("usuarioQueNoExiste", "admin123");
        assertNull(usuario,
            "El login con usuario inexistente debe devolver null");
    }
}