package modelo;

/**
 *
 * @author julian izaguirre
 */
public interface IOperacionesModelo {

    // Intenta trazar una linea entre dos puntos adyacentes del jugador en turno.
    // Regresa true si la jugada fue valida y se aplico.
    boolean trazarLinea(int fila1, int columna1, int fila2, int columna2);

}
