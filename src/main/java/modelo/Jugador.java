package modelo;

import java.awt.Color;

public class Jugador {

    private final String nombre;
    private final Color color;
    private int puntaje;

    public Jugador(String nombre, Color color) {
        this.nombre = nombre;
        this.color = color;
        this.puntaje = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public Color getColor() {
        return color;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void sumarPunto() {
        puntaje++;
    }
}
