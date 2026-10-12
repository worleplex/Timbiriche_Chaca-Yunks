package modelo;

import java.util.Objects;

/**
 *
 * @author julian izaguirre
 */
public class Punto {

    private final int fila;
    private final int columna;

    public Punto(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Punto)) {
            return false;
        }

        Punto otro = (Punto) obj;

        return fila == otro.fila && columna == otro.columna;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fila, columna);
    }
}
