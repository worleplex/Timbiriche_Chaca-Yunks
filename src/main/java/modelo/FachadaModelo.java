package modelo;

import java.util.List;

public class FachadaModelo implements IOperacionesModelo, IConsultaModelo {

    private final Tablero control;

    public FachadaModelo(List<Jugador> jugadores, GestorAlmacenamientoJuego gestor) {
        control = new Tablero(jugadores, gestor);
    }

    @Override
    public boolean trazarLinea(int fila1, int columna1, int fila2, int columna2) {
        return control.trazarLinea(fila1, columna1, fila2, columna2);
    }

    @Override
    public boolean seleccionarPunto(int fila, int columna) {
        return control.seleccionarPunto(fila, columna);
    }

    @Override
    public boolean deseleccionarPunto() {
        return control.deseleccionarPunto();
    }

    @Override
    public boolean clicPunto(Jugador jugador, int fila, int columna) {
        return control.clicPunto(jugador, fila, columna);
    }

    @Override
    public Punto getPuntoSeleccionado() {
        return control.getPuntoSeleccionado();
    }

    @Override
    public int getTamano() {
        return control.getTamano();
    }

    @Override
    public List<Linea> getLineas() {
        return control.getLineas();
    }

    @Override
    public List<Cuadrado> getCuadros() {
        return control.getCuadros();
    }

    @Override
    public List<Jugador> getJugadores() {
        return control.getJugadores();
    }

    @Override
    public Jugador getJugadorActual() {
        return control.getJugadorActual();
    }

    @Override
    public boolean isJuegoTerminado() {
        return control.isJuegoTerminado();
    }

    @Override
    public List<Punto> getPuntosConectables(Punto desde) {
        return control.getPuntosConectables(desde);
    }

    @Override
    public List<Jugador> getGanadores() {
        return control.getGanadores();
    }
}
