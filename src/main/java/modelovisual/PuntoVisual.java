package modelovisual;

public class PuntoVisual {

    private final double x;
    private final double y;
    private final EstadoPunto estado;

    public PuntoVisual(double x, double y, EstadoPunto estado) {
        this.x = x;
        this.y = y;
        this.estado = estado;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public EstadoPunto getEstado() {
        return estado;
    }
}
