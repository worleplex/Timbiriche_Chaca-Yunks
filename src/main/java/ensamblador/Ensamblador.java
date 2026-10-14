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

public class Ensamblador {

    public void ensamblar() {

        OptionalInt numJugadores = new DialogoNumeroJugadores()
                .pedirNumeroDeJugadores(Tablero.MIN_JUGADORES, Tablero.MAX_JUGADORES);

        if (!numJugadores.isPresent()) {
            return;
        }

        List<Jugador> jugadores = new ArrayList<>();
        for (int i = 0; i < numJugadores.getAsInt(); i++) {
            jugadores.add(new Jugador("Jugador " + (i + 1)));
        }

        FachadaModelo modeloGeneral = new FachadaModelo(jugadores);

        List<VistaTablero> vistas = new ArrayList<>();

        for (int i = 0; i < jugadores.size(); i++) {

            Jugador jugador = jugadores.get(i);

            FachadaModeloVisual modeloVisual = new FachadaModeloVisual(modeloGeneral, modeloGeneral, jugador);
            Controlador controlador = new Controlador();
            VistaTablero vista = new VistaTablero(i + 1);

            controlador.setModeloVisual(modeloVisual);
            vista.setControlador(controlador);
            vista.setModeloVisual(modeloVisual);

            modeloGeneral.agregarObservador(modeloVisual);
            modeloVisual.agregarObservador(vista);

            vistas.add(vista);
        }

        for (VistaTablero vista : vistas) {
            vista.actualizar();
            vista.setVisible(true);
        }
    }
}