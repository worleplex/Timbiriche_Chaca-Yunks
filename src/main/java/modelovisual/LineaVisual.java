package modelovisual;

import java.awt.Color;

public class LineaVisual {

    private final int fila1;
    private final int columna1;
    private final int fila2;
    private final int columna2;
    private final Color color;

    public LineaVisual(int fila1, int columna1, int fila2, int columna2, Color color) {
        this.fila1 = fila1;
        this.columna1 = columna1;
        this.fila2 = fila2;
        this.columna2 = columna2;
        this.color = color;
    }

    public int getFila1() {
        return fila1;
    }

    public int getColumna1() {
        return columna1;
    }

    public int getFila2() {
        return fila2;
    }

    public int getColumna2() {
        return columna2;
    }

    public Color getColor() {
        return color;
    }
}
