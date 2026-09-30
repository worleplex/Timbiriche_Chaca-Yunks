package modelo;

import java.util.List;

public interface IConsultaModelo {

    int getTamano(); // puntos por lado

    List<Linea> getLineas();

    List<Cuadrado> getCuadros();

    List<Jugador> getJugadores();

    Jugador getJugadorActual();

    boolean isJuegoTerminado();

}
