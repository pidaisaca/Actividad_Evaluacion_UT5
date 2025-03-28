package Main;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Controlador.validador;
import Vista.Gestor;
import Vista.Login;
import Vista.CreadorUsuario;




/**
 *
 * @author Daniel Ramos Montoya
 */


 // Llamada al metodo main, para iniciar el programa
public class Main {

    public static void main(String[] args) {
        Vista.Login login = new Login();
        login.setVisible(true);
    }

}

