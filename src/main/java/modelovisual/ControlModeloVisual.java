package modelovisual;

import modelo.IConsultaModelo;
import modelo.IOperacionesModelo;
import modelo.Jugador;
import modelo.Punto;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author julian izaguirre
 */
public class ControlModeloVisual {

    // Colores que se asignan por defecto, en el orden de los jugadores.
    private static final Color[] PALETA = {
        Color.BLUE, Color.RED, new Color(0, 150, 0), new Color(220, 140, 0)
    };

    private final IConsultaModelo consultaModeloGeneral;
    private final IOperacionesModelo operacionesModeloGeneral;
    private final Jugador jugadorLocal; // el jugador dueno de este tablero
    private Punto puntoSeleccionado;
    private final Map<Jugador, Color> colores; // dato visual: color de cada jugador en este tablero

    public ControlModeloVisual(IConsultaModelo consultaModeloGeneral,
                               IOperacionesModelo operacionesModeloGeneral,
                               Jugador jugadorLocal) {
        this.jugadorLocal = jugadorLocal;
        this.consultaModeloGeneral = consultaModeloGeneral;
        this.operacionesModeloGeneral = operacionesModeloGeneral;

        // Aqui despues entrara la configuracion de colores del PDF; por
        // ahora cada jugador recibe un color de la paleta.
        this.colores = new HashMap<>();
        List<Jugador> jugadores = consultaModeloGeneral.getJugadores();
        for (int i = 0; i < jugadores.size(); i++) {
            colores.put(jugadores.get(i), PALETA[i % PALETA.length]);
        }
    }

    // ------------------------------------------
    // Operaciones
    // ------------------------------------------
    
    public boolean clicEnPunto(Punto punto) {

        // Solo puede jugar el dueno de este tablero Y solo en su turno.
        if (!esMiTurno()) {
            return false;
        }

        if (!haySeleccion()) {
            alternarSeleccion(punto);
            return true;
        }

        if (puntoSeleccionado.equals(punto)) {
            alternarSeleccion(punto); // mismo punto otra vez: cancela
            return true;
        }

        boolean jugadaValida = operacionesModeloGeneral.trazarLinea(
                puntoSeleccionado.getFila(), puntoSeleccionado.getColumna(),
                punto.getFila(), punto.getColumna());

        if (jugadaValida) {
            return false;
        }

        puntoSeleccionado = punto;
        return true;
    }

    public void limpiarSeleccion() {
        puntoSeleccionado = null;
    }

    private void alternarSeleccion(Punto punto) {

        if (punto.equals(puntoSeleccionado)) {
            puntoSeleccionado = null;
        } else {
            puntoSeleccionado = punto;
        }
    }

    // ------------------------------------------
    // Consultas
    // ------------------------------------------

    public Jugador getJugadorLocal() {
        return jugadorLocal;
    }

    public boolean esMiTurno() {
        return !consultaModeloGeneral.isJuegoTerminado()
                && jugadorLocal.equals(consultaModeloGeneral.getJugadorActual());
    }

    public boolean haySeleccion() {
        return puntoSeleccionado != null;
    }

    public Punto getPuntoSeleccionado() {
        return puntoSeleccionado;
    }

    public List<Punto> getPuntosResaltados() {

        if (puntoSeleccionado == null) {
            return Collections.emptyList();
        }

        return consultaModeloGeneral.getPuntosConectables(puntoSeleccionado);
    }

    public Color getColor(Jugador jugador) {
        return colores.getOrDefault(jugador, Color.GRAY);
    }
}