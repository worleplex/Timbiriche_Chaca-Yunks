package modelo;

public class Cuadrado {

    private final Punto esquinaSupIzq;

    private final Linea linea1;
    private final Linea linea2;
    private final Linea linea3;
    private final Linea linea4;
    private final Jugador jugador;

    public Cuadrado(Punto esquinaSupIzq, Linea linea1, Linea linea2, Linea linea3, Linea linea4, Jugador jugador) {
        this.esquinaSupIzq = esquinaSupIzq;
        this.linea1 = linea1;
        this.linea2 = linea2;
        this.linea3 = linea3;
        this.linea4 = linea4;
        this.jugador = jugador;
    }

    public Linea getLinea1() {
        return linea1;
    }

    public Linea getLinea2() {
        return linea2;
    }

    public Linea getLinea3() {
        return linea3;
    }

    public Linea getLinea4() {
        return linea4;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public Punto getEsquinaSupIzq() {
        return esquinaSupIzq;
    }
}
