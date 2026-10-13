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

    List<Punto> getPuntosConectables(Punto desde);

    List<Jugador> getGanadores();

}