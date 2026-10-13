package vista;

import javax.swing.JOptionPane;
import java.util.OptionalInt;

/**
 *
 * @author julian izaguirre
 */
public class DialogoNumeroJugadores {
    public OptionalInt pedirNumeroDeJugadores(int minimo, int maximo) {

        Integer[] opciones = new Integer[maximo - minimo + 1];
        for (int i = 0; i < opciones.length; i++) {
            opciones[i] = minimo + i;
        }

        Object elegido = JOptionPane.showInputDialog(
                null,
                "Numero de jugadores:",
                "Timbiriche",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (elegido == null) {
            return OptionalInt.empty();
        }

        return OptionalInt.of((Integer) elegido);
    }
}
