package modelovisual;

import java.util.Objects;

public final class CoordenadaTablero {

    private final double x;
    private final double y;

    public CoordenadaTablero(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof CoordenadaTablero)) {
            return false;
        }

        CoordenadaTablero otra = (CoordenadaTablero) obj;

        return Double.compare(x, otra.x) == 0 && Double.compare(y, otra.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
