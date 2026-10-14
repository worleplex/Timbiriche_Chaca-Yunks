package modelovisual;

import java.awt.Color;

public class FilaResultado {

    private final int posicion;
    private final String nombre;
    private final String textoPuntaje;
    private final Color color;

    public FilaResultado(int posicion, String nombre, String textoPuntaje, Color color) {
        this.posicion = posicion;
        this.nombre = nombre;
        this.textoPuntaje = textoPuntaje;
        this.color = color;
    }

    public int getPosicion() {
        return posicion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTextoPuntaje() {
        return textoPuntaje;
    }

    public Color getColor() {
        return color;
    }
}
