package ensamblador;

import controlador.Controlador;
import modelo.FachadaModelo;
import modelo.Jugador;
import modelo.Tablero;
import modelovisual.FachadaModeloVisual;
import vista.DialogoNumeroJugadores;
import vista.VistaTablero;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

/**
 *
 * @author julian izaguirre
 */
public class Ensamblador {

    public void ensamblar() {

        // ========================================
        // 1. Crear los objetos
        // ========================================

        // El Ensamblador no tiene codigo de Swing: la pregunta la hace la Vista.
        OptionalInt numJugadores = new DialogoNumeroJugadores()
                .pedirNumeroDeJugadores(Tablero.MIN_JUGADORES, Tablero.MAX_JUGADORES);

        if (!numJugadores.isPresent()) {
            return; // la persona cancelo: no hay nada que armar
        }

        List<Jugador> jugadores = new ArrayList<>();
        for (int i = 0; i < numJugadores.getAsInt(); i++) {
            jugadores.add(new Jugador("Jugador " + (i + 1)));
        }

        // El tamano del tablero lo decide el Modelo General segun los jugadores.
        FachadaModelo modeloGeneral = new FachadaModelo(jugadores);

        // FachadaModelo (fachada del Modelo General) implementa ambas interfaces
        // (consulta y operaciones); el Modelo Visual recibe las dos por
        // separado -- solo usa cada una para lo que le corresponde.
        FachadaModeloVisual modeloVisual = new FachadaModeloVisual(modeloGeneral, modeloGeneral);

        Controlador controlador = new Controlador();

        VistaTablero vista = new VistaTablero();

        // ========================================
        // 2. Establecer las asociaciones
        // ========================================

        // Controlador -> Modelo Visual (solo a traves de IOperacionesModeloVisual)
        controlador.setModeloVisual(modeloVisual);

        // Vista -> Controlador
        vista.setControlador(controlador);

        // Vista -> Modelo Visual (solo a traves de IConsultaModeloVisual).
        // La Vista ya NO conoce el Modelo General: todo lo que necesita,
        // datos del juego y datos visuales, se lo pide al Modelo Visual.
        vista.setModeloVisual(modeloVisual);

        // Modelo General de Juego -> Modelo Visual (observer)
        modeloGeneral.agregarObservador(modeloVisual);

        // Modelo Visual -> Vista (observer)
        modeloVisual.agregarObservador(vista);

        // ========================================
        // 3. Mostrar el estado inicial y la vista
        // ========================================

        vista.actualizar();
        vista.setVisible(true);
    }
}