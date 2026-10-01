package modelo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// Es el "Control Juego" del bosquejo, y cumple el mismo papel que
// ControlNombres en el proyecto del separador de nombres: aqui vive la
// logica y los datos reales del juego. No extiende SujetoObservado ni
// notifica nada -- eso es responsabilidad de la fachada (Tablero), que
// es la unica que el resto del programa conoce.
public class ControlJuego {

    private final int tamano; // puntos por lado (tamano-1 x tamano-1 cuadros)
    private final Set<Linea> lineas;
    private final List<Cuadrado> cuadros; // solo cuadros YA completados
    private final List<Jugador> jugadores;
    private int turnoActual;

    public ControlJuego(int tamano, List<Jugador> jugadores) {
        this.tamano = tamano;
        this.jugadores = jugadores;
        this.lineas = new LinkedHashSet<>();
        this.cuadros = new ArrayList<>();
        this.turnoActual = 0;
        // No se pre-crean los cuadros: se crean uno a uno dentro de
        // completarCuadros(), solo cuando de verdad se completan.
    }

    // ------------------------------------------
    // Operaciones
    // ------------------------------------------

    public boolean trazarLinea(int fila1, int columna1, int fila2, int columna2) {

        if (isJuegoTerminado()) {
            return false;
        }

        if (!sonAdyacentes(fila1, columna1, fila2, columna2)) {
            return false;
        }

        Punto puntoA = new Punto(fila1, columna1);
        Punto puntoB = new Punto(fila2, columna2);
        Jugador jugadorActual = getJugadorActual();

        Linea nuevaLinea = new Linea(puntoA, puntoB, jugadorActual);

        if (lineas.contains(nuevaLinea)) {
            return false;
        }

        lineas.add(nuevaLinea);

        int cuadrosCompletados = completarCuadros(puntoA, puntoB, jugadorActual);

        // Si no se completo ningun cuadro, pasa el turno.
        // Si se completo al menos uno, el mismo jugador vuelve a jugar.
        if (cuadrosCompletados == 0) {
            turnoActual = (turnoActual + 1) % jugadores.size();
        }

        return true;
    }

    private boolean sonAdyacentes(int fila1, int columna1, int fila2, int columna2) {

        boolean dentroDelTablero =
                fila1 >= 0 && fila1 < tamano && columna1 >= 0 && columna1 < tamano
                        && fila2 >= 0 && fila2 < tamano && columna2 >= 0 && columna2 < tamano;

        if (!dentroDelTablero) {
            return false;
        }

        int distanciaFila = Math.abs(fila1 - fila2);
        int distanciaColumna = Math.abs(columna1 - columna2);

        return (distanciaFila == 1 && distanciaColumna == 0)
                || (distanciaFila == 0 && distanciaColumna == 1);
    }

    // Una linea nueva puede completar, como maximo, los 2 cuadros que la
    // tienen como lado (el de "arriba/izquierda" y el de "abajo/derecha").
    // No hace falta revisar los demas cuadros del tablero.
    private int completarCuadros(Punto puntoA, Punto puntoB, Jugador jugador) {

        int completados = 0;

        for (Punto esquina : esquinasCandidatas(puntoA, puntoB)) {

            if (yaCompletado(esquina)) {
                continue;
            }

            if (cuadradoCompleto(esquina)) {
                cuadros.add(construirCuadrado(esquina, jugador));
                jugador.sumarPunto();
                completados++;
            }
        }

        return completados;
    }

    // Dada la linea recien trazada, calcula la esquina superior izquierda
    // de los cuadros que podrian haberse completado con ella (0, 1 o 2).
    private List<Punto> esquinasCandidatas(Punto puntoA, Punto puntoB) {

        List<Punto> candidatas = new ArrayList<>();

        if (puntoA.getFila() == puntoB.getFila()) {

            // Linea horizontal: puede ser el lado de abajo del cuadro de
            // arriba, y el lado de arriba del cuadro de abajo.
            int fila = puntoA.getFila();
            int columna = Math.min(puntoA.getColumna(), puntoB.getColumna());

            if (fila - 1 >= 0) {
                candidatas.add(new Punto(fila - 1, columna)); // cuadro de arriba
            }
            if (fila < tamano - 1) {
                candidatas.add(new Punto(fila, columna)); // cuadro de abajo
            }

        } else {

            // Linea vertical: puede ser el lado derecho del cuadro de la
            // izquierda, y el lado izquierdo del cuadro de la derecha.
            int columna = puntoA.getColumna();
            int fila = Math.min(puntoA.getFila(), puntoB.getFila());

            if (columna - 1 >= 0) {
                candidatas.add(new Punto(fila, columna - 1)); // cuadro de la izquierda
            }
            if (columna < tamano - 1) {
                candidatas.add(new Punto(fila, columna)); // cuadro de la derecha
            }
        }

        return candidatas;
    }

    private boolean yaCompletado(Punto esquinaSupIzq) {

        for (Cuadrado cuadrado : cuadros) {
            if (cuadrado.getEsquinaSupIzq().equals(esquinaSupIzq)) {
                return true;
            }
        }

        return false;
    }

    private boolean cuadradoCompleto(Punto esquinaSupIzq) {

        for (Linea lado : ladosDe(esquinaSupIzq)) {
            if (!lineas.contains(lado)) {
                return false;
            }
        }

        return true;
    }

    // cuadradoCompleto() ya confirmo que los 4 lados existen; aqui los
    // recuperamos del Set con su jugador real (el lado "de prueba" que
    // arma ladosDe() trae jugador=null, no sirve para guardarlo tal cual).
    // La esquina se guarda aparte y explicita en el Cuadrado -- NO se
    // puede volver a derivar de linea1, porque el orden de los puntos
    // dentro de una Linea real depende del orden en que se clickeo, no
    // de cual lado del cuadro representa.
    private Cuadrado construirCuadrado(Punto esquinaSupIzq, Jugador jugadorQueCompleta) {

        List<Linea> lados = ladosDe(esquinaSupIzq);

        Linea arriba = buscarLineaReal(lados.get(0));
        Linea abajo = buscarLineaReal(lados.get(1));
        Linea izquierda = buscarLineaReal(lados.get(2));
        Linea derecha = buscarLineaReal(lados.get(3));

        return new Cuadrado(esquinaSupIzq, arriba, abajo, izquierda, derecha, jugadorQueCompleta);
    }

    private Linea buscarLineaReal(Linea posible) {

        for (Linea linea : lineas) {
            if (linea.equals(posible)) {
                return linea;
            }
        }

        // No deberia pasar: solo se llama despues de confirmar con
        // cuadradoCompleto() que las 4 lineas ya estan en el Set.
        throw new IllegalStateException("Se esperaba encontrar la linea " + posible);
    }

    private List<Linea> ladosDe(Punto esquinaSupIzq) {

        int fila = esquinaSupIzq.getFila();
        int columna = esquinaSupIzq.getColumna();

        Punto supIzq = new Punto(fila, columna);
        Punto supDer = new Punto(fila, columna + 1);
        Punto infIzq = new Punto(fila + 1, columna);
        Punto infDer = new Punto(fila + 1, columna + 1);

        List<Linea> lados = new ArrayList<>();
        lados.add(new Linea(supIzq, supDer, null)); // arriba
        lados.add(new Linea(infIzq, infDer, null)); // abajo
        lados.add(new Linea(supIzq, infIzq, null)); // izquierda
        lados.add(new Linea(supDer, infDer, null)); // derecha

        return lados;
    }

    // ------------------------------------------
    // Consultas
    // ------------------------------------------

    public int getTamano() {
        return tamano;
    }

    public List<Linea> getLineas() {
        return new ArrayList<>(lineas);
    }

    // Se regresan copias: quien consulte no puede modificar el estado
    // interno del juego (ni tampoco falla si el juego avanza mientras
    // la Vista esta recorriendo la lista).
    public List<Cuadrado> getCuadros() {
        return new ArrayList<>(cuadros);
    }

    public List<Jugador> getJugadores() {
        return new ArrayList<>(jugadores);
    }

    public Jugador getJugadorActual() {
        return jugadores.get(turnoActual);
    }

    public boolean isJuegoTerminado() {
        int totalCuadros = (tamano - 1) * (tamano - 1);
        return cuadros.size() == totalCuadros;
    }

    // Todos los jugadores que tengan el puntaje mas alto: si hay empate
    // regresa mas de uno (antes la Vista solo mostraba al primero).
    public List<Jugador> getGanadores() {

        int maximo = 0;
        for (Jugador jugador : jugadores) {
            maximo = Math.max(maximo, jugador.getPuntaje());
        }

        List<Jugador> ganadores = new ArrayList<>();
        for (Jugador jugador : jugadores) {
            if (jugador.getPuntaje() == maximo) {
                ganadores.add(jugador);
            }
        }

        return ganadores;
    }
}
