/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;


import Vista.Login;  // Importar el panel Login desde el paquete Vista
import javax.swing.JFrame;

import javax.swing.JPanel;
import java.awt.Component;
import javax.swing.SwingUtilities;
/**
 *
 * @author Andres Gordillo
 */
public class PapeleriaLau {

   public static Login log; // Declarar la variable estática para el panel Login
    
    public static void main(String[] args) {
        // Asegurarse de que el código de la interfaz gráfica se ejecute en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Crear un JFrame para contener el panel Login
                JFrame frame = new JFrame("Login");
                
                // Crear una instancia del panel Login
                log = new Login();
                
                // Configurar el JFrame
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setContentPane(log); // Agregar el panel Login al JFrame
                
                // Ajustar el tamaño del JFrame al tamaño preferido del panel Login
                frame.pack(); // Ajusta el tamaño del JFrame para que se ajuste al panel Login
                
                frame.setLocationRelativeTo(null); // Centrar el JFrame en la pantalla
                frame.setVisible(true); // Hacer visible el JFrame
            }
        });
    }
}
