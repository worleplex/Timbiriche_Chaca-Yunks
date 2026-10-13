package modelo;

import utilerias.SujetoObservado;

import java.util.List;

/**
 *
 * @author julian izaguirre
 */
public class FachadaModelo extends SujetoObservado
        implements IOperacionesModelo, IConsultaModelo {

    private final Tablero control;
    
    // La fachada ya no decide el tamano: eso es una regla del juego y la
    // aplica el Tablero a partir de los jugadores.
    public FachadaModelo(List<Jugador> jugadores) {
        control = new Tablero(jugadores);
    }

    @Override
    public boolean trazarLinea(int fila1, int columna1, int fila2, int columna2) {

        boolean jugadaValida = control.trazarLinea(fila1, columna1, fila2, columna2);

        if (jugadaValida) {
            notificarObservadores();
        }

        return jugadaValida;
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