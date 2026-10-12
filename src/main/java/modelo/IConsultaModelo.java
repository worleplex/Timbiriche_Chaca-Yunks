package modelo;

import java.util.List;

/**
 *
 * @author julian izaguirre
 */
public interface IConsultaModelo {

    int getTamano(); // puntos por lado

    List<Linea> getLineas();

    List<Cuadrado> getCuadros();

    List<Jugador> getJugadores();

    Jugador getJugadorActual();

    boolean isJuegoTerminado();

    // Jugadores con el mayor puntaje (mas de uno si hay empate).
    List<Jugador> getGanadores();

}
