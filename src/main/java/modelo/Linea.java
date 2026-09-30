package modelo;

public class Linea {

    private final Punto puntoA;
    private final Punto puntoB;
    private final Jugador jugador;

    public Linea(Punto puntoA, Punto puntoB, Jugador jugador) {
        this.puntoA = puntoA;
        this.puntoB = puntoB;
        this.jugador = jugador;
    }

    public Punto getPuntoA() {
        return puntoA;
    }

    public Punto getPuntoB() {
        return puntoB;
    }

    public Jugador getJugador() {
        return jugador;
    }

    // Dos lineas son la misma si conectan los mismos dos puntos,
    // sin importar el orden ni quien la dibujo.
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Linea)) {
            return false;
        }

        Linea otra = (Linea) obj;

        boolean mismoOrden = puntoA.equals(otra.puntoA) && puntoB.equals(otra.puntoB);
        boolean ordenInvertido = puntoA.equals(otra.puntoB) && puntoB.equals(otra.puntoA);

        return mismoOrden || ordenInvertido;
    }

    @Override
    public int hashCode() {
        return puntoA.hashCode() + puntoB.hashCode();
    }
}
