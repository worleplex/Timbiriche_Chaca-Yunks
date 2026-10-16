package modelovisual;

public class PuntoVisual {

    private final int fila;
    private final int columna;
    private final EstadoPunto estado;
    private final EstadoPunto estadoAnterior;

    public PuntoVisual(int fila, int columna, EstadoPunto estado, EstadoPunto estadoAnterior) {
        this.fila = fila;
        this.columna = columna;
        this.estado = estado;
        this.estadoAnterior = estadoAnterior;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public EstadoPunto getEstado() {
        return estado;
    }

    public EstadoPunto getEstadoAnterior() {
        return estadoAnterior;
    }
}
