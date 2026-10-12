package modelo;

/**
 *
 * @author julian izaguirre
 */
public class Cuadrado {

    // Se guarda explicita: NO se puede confiar en linea1.getPuntoA()
    // para obtenerla, porque el orden de los puntos dentro de una Linea
    // depende del orden en que el jugador hizo clic, no de cual lado del
    // cuadro representa. (Ese era el bug: a veces linea1.getPuntoA()
    // caia en la esquina de al lado, y el relleno se pintaba corrido.)
    private final Punto esquinaSupIzq;

    private final Linea linea1; // arriba
    private final Linea linea2; // abajo
    private final Linea linea3; // izquierda
    private final Linea linea4; // derecha
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
