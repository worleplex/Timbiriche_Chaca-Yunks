package modelovisual;

import java.awt.Color;

public class TarjetaJugador {

    private final String nombre;
    private final int puntos;
    private final String textoPuntos;
    private final Color color;
    private final boolean activa;
    private final String subtitulo;

    public TarjetaJugador(String nombre, int puntos, String textoPuntos,
                          Color color, boolean activa, String subtitulo) {
        this.nombre = nombre;
        this.puntos = puntos;
        this.textoPuntos = textoPuntos;
        this.color = color;
        this.activa = activa;
        this.subtitulo = subtitulo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntos() {
        return puntos;
    }

    public String getTextoPuntos() {
        return textoPuntos;
    }

    public Color getColor() {
        return color;
    }

    public boolean isActiva() {
        return activa;
    }

    public String getSubtitulo() {
        return subtitulo;
    }
}
