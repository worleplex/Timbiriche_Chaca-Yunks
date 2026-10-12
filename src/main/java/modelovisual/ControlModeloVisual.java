package modelovisual;

import modelo.IConsultaModelo;
import modelo.IOperacionesModelo;
import modelo.Linea;
import modelo.Punto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author julian izaguirre
 */
public class ControlModeloVisual {

    private final IConsultaModelo consultaModeloGeneral;
    private final IOperacionesModelo operacionesModeloGeneral;
    private Punto puntoSeleccionado;

    public ControlModeloVisual(IConsultaModelo consultaModeloGeneral,
                               IOperacionesModelo operacionesModeloGeneral) {
        this.consultaModeloGeneral = consultaModeloGeneral;
        this.operacionesModeloGeneral = operacionesModeloGeneral;
    }

    // ------------------------------------------
    // Operaciones
    // ------------------------------------------

    // Regresa true si este clic cambio por si mismo el estado visual
    // (hay que avisarle a la Vista). Regresa false si el clic provoco una
    // jugada valida: en ese caso el Modelo General ya notifica y la
    // fachada recibe actualizar(), que limpia la seleccion y avisa.
    public boolean clicEnPunto(Punto punto) {

        if (consultaModeloGeneral.isJuegoTerminado()) {
            return false;
        }

        if (!haySeleccion()) {
            alternarSeleccion(punto);
            return true;
        }

        if (puntoSeleccionado.equals(punto)) {
            alternarSeleccion(punto); // mismo punto otra vez: cancela
            return true;
        }

        boolean jugadaValida = operacionesModeloGeneral.trazarLinea(
                puntoSeleccionado.getFila(), puntoSeleccionado.getColumna(),
                punto.getFila(), punto.getColumna());

        if (jugadaValida) {
            return false;
        }

        // no eran adyacentes o la linea ya existia: el nuevo punto se
        // vuelve la seleccion, para poder intentar de nuevo
        puntoSeleccionado = punto;
        return true;
    }

    public void limpiarSeleccion() {
        puntoSeleccionado = null;
    }

    private void alternarSeleccion(Punto punto) {

        if (punto.equals(puntoSeleccionado)) {
            puntoSeleccionado = null;
        } else {
            puntoSeleccionado = punto;
        }
    }

    // ------------------------------------------
    // Consultas
    // ------------------------------------------

    public boolean haySeleccion() {
        return puntoSeleccionado != null;
    }

    public Punto getPuntoSeleccionado() {
        return puntoSeleccionado;
    }

    // Puntos vecinos al seleccionado que todavia se pueden conectar con
    // una linea (los "puntos remarcados" del bosquejo). Es informacion
    // puramente de interfaz: se calcula aqui, no en el modelo general.
    public List<Punto> getPuntosResaltados() {

        List<Punto> resaltados = new ArrayList<>();

        if (puntoSeleccionado == null) {
            return resaltados;
        }

        int tamano = consultaModeloGeneral.getTamano();
        int fila = puntoSeleccionado.getFila();
        int columna = puntoSeleccionado.getColumna();

        int[][] vecinos = {
            {fila - 1, columna},
            {fila + 1, columna},
            {fila, columna - 1},
            {fila, columna + 1}
        };

        for (int[] vecino : vecinos) {

            int filaVecino = vecino[0];
            int columnaVecino = vecino[1];

            boolean dentroDelTablero =
                    filaVecino >= 0 && filaVecino < tamano
                            && columnaVecino >= 0 && columnaVecino < tamano;

            if (!dentroDelTablero) {
                continue;
            }

            Punto candidato = new Punto(filaVecino, columnaVecino);

            if (!lineaYaExiste(puntoSeleccionado, candidato)) {
                resaltados.add(candidato);
            }
        }

        return resaltados;
    }

    private boolean lineaYaExiste(Punto puntoA, Punto puntoB) {

        Linea posible = new Linea(puntoA, puntoB, null);

        for (Linea linea : consultaModeloGeneral.getLineas()) {
            if (linea.equals(posible)) {
                return true;
            }
        }

        return false;
    }
}
