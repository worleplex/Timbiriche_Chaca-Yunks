package modelovisual;

import modelo.Cuadrado;
import modelo.IConsultaModelo;
import modelo.IOperacionesModelo;
import modelo.Jugador;
import modelo.Linea;
import modelo.Punto;
import utilerias.Observador;
import utilerias.SujetoObservado;

import java.util.List;

// Fachada del Modelo Visual (igual que Tablero lo es del Modelo General):
// es la unica clase del Modelo Visual que conocen las demas capas, pero
// SOLO a traves de sus interfaces:
//
//   Controlador -> IOperacionesModeloVisual
//   Vista       -> IConsultaModeloVisual
//
// Es Observador del Modelo General de Juego (para saber cuando redibujar
// porque alguien ya jugo) y a la vez es SujetoObservado de la Vista (para
// avisarle tanto de esos cambios como de cambios puramente visuales, como
// una seleccion, que al modelo general no le interesan en absoluto).
// Ella sola decide CUANDO notificar; ControlModeloVisual no sabe que
// existen observadores.
public class ModeloVisual extends SujetoObservado
        implements IOperacionesModeloVisual, IConsultaModeloVisual, Observador {

    private final IConsultaModelo consultaModeloGeneral;
    private final ControlModeloVisual control;

    public ModeloVisual(IConsultaModelo consultaModeloGeneral,
                        IOperacionesModelo operacionesModeloGeneral) {
        this.consultaModeloGeneral = consultaModeloGeneral;
        this.control = new ControlModeloVisual(consultaModeloGeneral, operacionesModeloGeneral);
    }

    // ------------------------------------------
    // IOperacionesModeloVisual (la usa el Controlador)
    // ------------------------------------------

    @Override
    public void clicEnPunto(Punto punto) {

        boolean cambioVisual = control.clicEnPunto(punto);

        if (cambioVisual) {
            notificarObservadores();
        }
    }

    // ------------------------------------------
    // Observador del Modelo General de Juego
    // ------------------------------------------

    // El Modelo General notifica que aplico una jugada valida.
    @Override
    public void actualizar() {
        control.limpiarSeleccion(); // la jugada ya se confirmo
        notificarObservadores();    // avisa a la Vista que hay que redibujar
    }

    // ------------------------------------------
    // IConsultaModeloVisual (la usa la Vista) -- datos visuales
    // ------------------------------------------

    @Override
    public boolean haySeleccion() {
        return control.haySeleccion();
    }

    @Override
    public Punto getPuntoSeleccionado() {
        return control.getPuntoSeleccionado();
    }

    @Override
    public List<Punto> getPuntosResaltados() {
        return control.getPuntosResaltados();
    }

    // ------------------------------------------
    // IConsultaModeloVisual (la usa la Vista) -- datos del juego,
    // obtenidos del Modelo General (el "get()" del bosquejo)
    // ------------------------------------------

    @Override
    public int getTamano() {
        return consultaModeloGeneral.getTamano();
    }

    @Override
    public List<Linea> getLineas() {
        return consultaModeloGeneral.getLineas();
    }

    @Override
    public List<Cuadrado> getCuadros() {
        return consultaModeloGeneral.getCuadros();
    }

    @Override
    public List<Jugador> getJugadores() {
        return consultaModeloGeneral.getJugadores();
    }

    @Override
    public Jugador getJugadorActual() {
        return consultaModeloGeneral.getJugadorActual();
    }

    @Override
    public boolean isJuegoTerminado() {
        return consultaModeloGeneral.isJuegoTerminado();
    }

    @Override
    public List<Jugador> getGanadores() {
        return consultaModeloGeneral.getGanadores();
    }
}
