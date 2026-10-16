package modelo;

public interface IOperacionesModelo {

    boolean trazarLinea(int fila1, int columna1, int fila2, int columna2);

    boolean seleccionarPunto(int fila, int columna);

    boolean deseleccionarPunto();

}
