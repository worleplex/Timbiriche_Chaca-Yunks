package com.mycompany.timbiriche;

import ensamblador.Ensamblador;

import javax.swing.SwingUtilities;

public class Timbiriche {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Ensamblador().ensamblar());
    }

}
