package ensamblador;

import controlador.Controlador;
import modelo.Jugador;
import modelo.Tablero;
import modelovisual.ModeloVisual;
import vista.VistaTablero;

import javax.swing.JOptionPane;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Ensamblador {

    private static final Color[] COLORES = {
        Color.BLUE, Color.RED, new Color(0, 150, 0), new Color(220, 140, 0)
    };

    public void ensamblar() {

        // ========================================
        // 1. Crear los objetos
        // ========================================

        int numJugadores = pedirNumeroDeJugadores();

        List<Jugador> jugadores = new ArrayList<>();
        for (int i = 0; i < numJugadores; i++) {
            jugadores.add(new Jugador("Jugador " + (i + 1), COLORES[i]));
        }

        Tablero modeloGeneral = new Tablero(4, jugadores); // tablero de 3x3 cuadros

        // Tablero implementa ambas interfaces (consulta y operaciones),
        // el Modelo Visual recibe las dos por separado -- solo usa cada
        // una para lo que le corresponde.
        ModeloVisual modeloVisual = new ModeloVisual(modeloGeneral, modeloGeneral);

        Controlador controlador = new Controlador();

        VistaTablero vista = new VistaTablero();

        // ========================================
        // 2. Establecer las asociaciones
        // ========================================

        // Controlador -> Modelo Visual (y solo a el)
        controlador.setModeloVisual(modeloVisual);

        // Vista -> Controlador
        vista.setControlador(controlador);

        // Vista -> Modelo General de Juego (para leer datos del juego)
        vista.setModelo(modeloGeneral);

        // Vista -> Modelo Visual (para leer estado de seleccion)
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

    private int pedirNumeroDeJugadores() {

        while (true) {

            String texto = JOptionPane.showInputDialog(
                    null, "Numero de jugadores (2 a 4):", "Timbiriche", JOptionPane.QUESTION_MESSAGE);

            if (texto == null) {
                System.exit(0); // el usuario cancelo
            }

            try {
                int numero = Integer.parseInt(texto.trim());
                if (numero >= 2 && numero <= 4) {
                    return numero;
                }
            } catch (NumberFormatException ignorado) {
                // se vuelve a pedir
            }
        }
    }
}
