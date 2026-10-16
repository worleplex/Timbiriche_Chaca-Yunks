package modelovisual;

import java.awt.Color;

public class LineaVisual {

    private final double x1;
    private final double y1;
    private final double x2;
    private final double y2;
    private final Color color;

    public LineaVisual(double x1, double y1, double x2, double y2, Color color) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
    }

    public double getX1() {
        return x1;
    }

    public double getY1() {
        return y1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }

    public Color getColor() {
        return color;
    }
}