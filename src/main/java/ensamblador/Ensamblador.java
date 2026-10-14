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

        List<VistaTablero> vistas = new ArrayList<>();

        // ========================================
        // 2. Establecer las asociaciones
        // ========================================

        for (int i = 0; i < jugadores.size(); i++) {

            Jugador jugador = jugadores.get(i);

            FachadaModeloVisual modeloVisual = new FachadaModeloVisual(modeloGeneral, modeloGeneral, jugador);
            Controlador controlador = new Controlador();
            VistaTablero vista = new VistaTablero(i + 1);

            controlador.setModeloVisual(modeloVisual);   // Controlador -> Modelo Visual
            vista.setControlador(controlador);           // Vista -> Controlador
            vista.setModeloVisual(modeloVisual);         // Vista -> Modelo Visual

            modeloGeneral.agregarObservador(modeloVisual); // Modelo General -> Modelo Visual
            modeloVisual.agregarObservador(vista);         // Modelo Visual -> Vista

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