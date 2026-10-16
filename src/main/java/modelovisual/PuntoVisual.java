package modelovisual;

public class PuntoVisual {

    private final double x;
    private final double y;
    private final EstadoPunto estado;
    private final EstadoPunto estadoAnterior;

    public PuntoVisual(double x, double y, EstadoPunto estado, EstadoPunto estadoAnterior) {
        this.x = x;
        this.y = y;
        this.estado = estado;
        this.estadoAnterior = estadoAnterior;
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

    public EstadoPunto getEstadoAnterior() {
        return estadoAnterior;
    }
}