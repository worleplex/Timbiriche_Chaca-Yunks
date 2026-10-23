package modelo;

import java.util.List;
import java.util.Map;

public interface IConsultaContenedorJuego {

    boolean hayCambioSeleccion();

    Punto getPuntoSeleccionado();

    List<Punto> getPuntosAdyacentes();

    List<Linea> getLineasNuevas();

    List<Cuadrado> getCuadrosNuevos();

    boolean hayCambioEstadoJugadores();

    Jugador getJugadorActual();

    Map<Jugador, Integer> getPuntajes();

    boolean isJuegoTerminado();

    List<Jugador> getGanadores();
}
