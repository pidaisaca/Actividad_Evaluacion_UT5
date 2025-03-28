package Controlador;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class validador {

    private static final String USERS_FILE = Paths.get("src/Datos", "usuarios.txt").toAbsolutePath().toString();
    // End of variables declaration//GEN-END:variables

    public static boolean iniciarSesion(String email, String contraseña) {
        String hashedPassword = hashPassword(contraseña);
        try {
            List<String> lines = Files.readAllLines(Paths.get(USERS_FILE), StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].trim().equals(email) && parts[1].trim().equals(hashedPassword)) {
                    return true; // Usuario autenticado
                }
            }
        } catch (IOException e) {
            System.out.println("⚠ Error al leer el archivo de usuarios.");
            e.printStackTrace();
        }
        return false;
    }

    public static String hashPassword(String contraseña) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(contraseña.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash de contraseña.", e);
        }
    }
}
