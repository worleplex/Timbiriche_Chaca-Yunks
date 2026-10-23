package ensamblador;

import controlador.Controlador;
import modelo.FachadaModelo;
import modelo.GestorAlmacenamientoJuego;
import modelo.Jugador;
import modelo.Tablero;
import modelovisual.ControlModeloVisual;
import modelovisual.FachadaModeloVisual;
import modelovisual.GestorAlmacenamientoVisual;
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

        GestorAlmacenamientoJuego gestorJuego = new GestorAlmacenamientoJuego();
        FachadaModelo modeloGeneral = new FachadaModelo(jugadores, gestorJuego);

        List<VistaTablero> vistas = new ArrayList<>();

        for (int i = 0; i < jugadores.size(); i++) {

            Jugador jugador = jugadores.get(i);

            GestorAlmacenamientoVisual gestorVisual = new GestorAlmacenamientoVisual();
            ControlModeloVisual controlVisual = new ControlModeloVisual(modeloGeneral, modeloGeneral,
                    gestorJuego.getConsultaContenedor(), gestorVisual, jugador);
            FachadaModeloVisual modeloVisual = new FachadaModeloVisual(controlVisual);
            Controlador controlador = new Controlador();
            VistaTablero vista = new VistaTablero(i + 1);

            controlador.setModeloVisual(modeloVisual);
            vista.setControlador(controlador);
            vista.setModeloVisual(modeloVisual);
            vista.setConsultaContenedor(gestorVisual.getConsultaContenedor());

            gestorJuego.agregarObservador(controlVisual);
            gestorVisual.agregarObservador(vista);

            vistas.add(vista);
        }

        for (VistaTablero vista : vistas) {
            vista.setVisible(true);
        }
    }
}
