package com.mycompany.timbiriche;

import ensamblador.Ensamblador;

//import javax.swing.SwingUtilities; para después con la red

/**
 *
 * @author julian izaguirre
 */
public class Timbiriche {

    public static void main(String[] args) {
        
        Ensamblador ensamblador = new Ensamblador();
        
        ensamblador.ensamblar();
        
        // Swing debe crearse en el hilo de eventos (EDT)
//        SwingUtilities.invokeLater(() -> new Ensamblador().ensamblar());
    }

}
