package modelovisual;

import java.awt.Color;

public class CuadroVisual {

    private final double x;
    private final double y;
    private final double lado;
    private final Color color;

    public CuadroVisual(double x, double y, double lado, Color color) {
        this.x = x;
        this.y = y;
        this.lado = lado;
        this.color = color;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getLado() {
        return lado;
    }

    public Color getColor() {
        return color;
    }
}