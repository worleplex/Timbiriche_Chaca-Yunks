package modelo;

import java.util.List;

public interface IConsultaModelo {

    int getTamano();

    List<Linea> getLineas();

    List<Cuadrado> getCuadros();

    List<Jugador> getJugadores();

    Jugador getJugadorActual();

    boolean isJuegoTerminado();

    List<Punto> getPuntosConectables(Punto desde);

    Punto getPuntoSeleccionado();

    List<Jugador> getGanadores();

}