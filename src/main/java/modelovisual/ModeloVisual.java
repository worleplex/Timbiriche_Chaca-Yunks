package modelovisual;

import modelo.IConsultaModelo;
import modelo.IOperacionesModelo;
import modelo.Linea;
import modelo.Punto;
import utilerias.Observador;
import utilerias.SujetoObservado;

import java.util.ArrayList;
import java.util.List;

// Modelo Visual (segun el bosquejo del profe): NO es una copia de los
// datos del juego, es el estado que le pertenece a ESTA vista/jugador y
// que nunca deberia compartirse con los demas jugadores -- por ejemplo,
// que punto ya seleccione pero todavia no confirmo con un segundo clic.
//
// Es Observador del Modelo General de Juego (para saber cuando redibujar
// porque alguien ya jugo) y a la vez es SujetoObservado de la Vista (para
// avisarle tanto de esos cambios como de cambios puramente visuales, como
// una seleccion, que al modelo general no le interesan en absoluto).
public class ModeloVisual extends SujetoObservado implements Observador {

    private final IConsultaModelo consultaModeloGeneral;
    private final IOperacionesModelo operacionesModeloGeneral;
    private Punto puntoSeleccionado;

    public ModeloVisual(IConsultaModelo consultaModeloGeneral, IOperacionesModelo operacionesModeloGeneral) {
        this.consultaModeloGeneral = consultaModeloGeneral;
        this.operacionesModeloGeneral = operacionesModeloGeneral;
    }

    // Este es el "Control Modelo" del bosquejo: la Vista/Controlador le
    // entrega un punto ya traducido a coordenadas de cuadricula, y aqui
    // se decide si es una seleccion (1er clic) o una jugada (2do clic).
    // Es el unico lugar del programa que conoce AMBOS lados: el estado
    // visual (la seleccion) y las operaciones del Modelo General de Juego.
    public void clicEnPunto(Punto punto) {

        if (!haySeleccion()) {
            seleccionarPunto(punto);
            return;
        }

        Punto anterior = puntoSeleccionado;

        if (anterior.equals(punto)) {
            seleccionarPunto(punto); // mismo punto otra vez: cancela
            return;
        }

        boolean jugadaValida = operacionesModeloGeneral.trazarLinea(
                anterior.getFila(), anterior.getColumna(),
                punto.getFila(), punto.getColumna());

        if (!jugadaValida) {
            // no eran adyacentes o la linea ya existia: el nuevo punto
            // se vuelve la seleccion, para poder intentar de nuevo
            seleccionarPunto(punto);
        }

        // si la jugada SI fue valida, no hace falta hacer nada mas: el
        // Modelo General notifica -> actualizar() (abajo) limpia la
        // seleccion automaticamente.
    }

    // El Modelo General de Juego notifica que aplico una jugada valida.
    @Override
    public void actualizar() {
        puntoSeleccionado = null; // la jugada ya se confirmo, se limpia la seleccion
        notificarObservadores();  // avisa a la Vista que hay que redibujar
    }

    public Punto getPuntoSeleccionado() {
        return puntoSeleccionado;
    }

    public boolean haySeleccion() {
        return puntoSeleccionado != null;
    }

    // Selecciona un punto. Si ya estaba seleccionado ese mismo punto,
    // lo deselecciona (el usuario se arrepintio).
    public void seleccionarPunto(Punto punto) {

        if (punto.equals(puntoSeleccionado)) {
            puntoSeleccionado = null;
        } else {
            puntoSeleccionado = punto;
        }

        notificarObservadores();
    }

    // Puntos vecinos al seleccionado que todavia se pueden conectar con
    // una linea (para remarcarlos, como en el bosquejo). Es informacion
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
