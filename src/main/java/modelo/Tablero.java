package modelo;

import utilerias.SujetoObservado;

import java.util.List;

// Fachada del Modelo General de Juego, igual que FachadaModeloNombres en
// el proyecto del separador de nombres: es la unica clase que conocen el
// resto de las capas (Controlador via ModeloVisual, y ModeloVisual). Ella
// sola decide CUANDO notificar a los observadores; ControlJuego ni sabe
// que existen observadores.
public class Tablero extends SujetoObservado
        implements IOperacionesModelo, IConsultaModelo {

    private final ControlJuego control;
    
    public Tablero(List<Jugador> jugadores) {
        int cantidad = (jugadores.size() - 1) * 10;
        control = new ControlJuego(cantidad, jugadores);
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
    public List<Jugador> getGanadores() {
        return control.getGanadores();
    }
}
