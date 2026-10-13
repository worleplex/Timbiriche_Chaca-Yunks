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

        List<VistaTablero> vistas = new ArrayList<>();

        // ========================================
        // 2. Establecer las asociaciones
        // ========================================

        controlador.setModeloVisual(modeloVisual);
        modeloGeneral.agregarObservador(modeloVisual);
        
        for (int i = 0; i < numJugadores.getAsInt(); i++) {
            VistaTablero vista = new VistaTablero(i + 1);
            vista.setControlador(controlador);
            vista.setModeloVisual(modeloVisual);
            vista.setJugadorAsignado(jugadores.get(i));
            modeloVisual.agregarObservador(vista);
            vistas.add(vista);
        }

        // ========================================
        // 3. Mostrar el estado inicial y la vista
        // ========================================

        for (VistaTablero vista : vistas) {
            vista.actualizar();
            vista.setVisible(true);
        }
    }
}