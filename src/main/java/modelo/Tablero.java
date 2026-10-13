package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author julian izaguirre
 */
public class Tablero {

    // Reglas del juego (segun el PDF): juegan de 2 a 4 personas y el tamano
    // del tablero depende de cuantas sean: 2 -> 10x10, 3 -> 20x20 y
    // 4 -> 30x30 puntos. Esta regla vive aqui, en el Modelo General.
    public static final int MIN_JUGADORES = 2;
    public static final int MAX_JUGADORES = 4;
    private static final int PUNTOS_POR_JUGADOR_EXTRA = 10;

    private final int tamano; // puntos por lado (tamano-1 x tamano-1 cuadros)
    private final Set<Linea> lineas;
    private final List<Cuadrado> cuadros; // solo cuadros YA completados
    private final List<Jugador> jugadores;
    private int turnoActual;

    // El tamano lo decide el Tablero a partir de la cantidad de jugadores.
    public Tablero(List<Jugador> jugadores) {
        this(tamanoParaJugadores(jugadores), jugadores);
    }

    // Sin "public": solo se ve dentro del paquete modelo. Sirve para
    // pruebas con un tablero chico (por ejemplo 4 puntos = 3x3 cuadros).
    Tablero(int tamano, List<Jugador> jugadores) {

        if (tamano < 2) {
            throw new IllegalArgumentException("El tablero necesita al menos 2 puntos por lado");
        }
        if (jugadores == null || jugadores.isEmpty()) {
            throw new IllegalArgumentException("Se necesita al menos un jugador");
        }

        this.tamano = tamano;
        this.jugadores = new ArrayList<>(jugadores); // copia: la lista de afuera ya no nos afecta
        this.lineas = new LinkedHashSet<>();
        this.cuadros = new ArrayList<>();
        this.turnoActual = 0;
        // No se pre-crean los cuadros: se crean uno a uno dentro de
        // completarCuadros(), solo cuando de verdad se completan.
    }

    private static int tamanoParaJugadores(List<Jugador> jugadores) {

        if (jugadores == null || jugadores.size() < MIN_JUGADORES || jugadores.size() > MAX_JUGADORES) {
            throw new IllegalArgumentException(
                    "Se necesitan de " + MIN_JUGADORES + " a " + MAX_JUGADORES + " jugadores");
        }

        return (jugadores.size() - 1) * PUNTOS_POR_JUGADOR_EXTRA;
    }

    // ------------------------------------------
    // Operaciones
    // ------------------------------------------

    public boolean trazarLinea(int fila1, int columna1, int fila2, int columna2) {

        if (isJuegoTerminado()) {
            return false;
        }

        Punto puntoA = new Punto(fila1, columna1);
        Punto puntoB = new Punto(fila2, columna2);

        if (!sePuedeTrazar(puntoA, puntoB)) {
            return false;
        }

        Jugador jugadorActual = getJugadorActual();

        lineas.add(new Linea(puntoA, puntoB, jugadorActual));

        int cuadrosCompletados = completarCuadros(puntoA, puntoB, jugadorActual);

        // Si no se completo ningun cuadro, pasa el turno.
        // Si se completo al menos uno, el mismo jugador vuelve a jugar.
        if (cuadrosCompletados == 0) {
            turnoActual = (turnoActual + 1) % jugadores.size();
        }

        return true;
    }

    // UNICA regla de "se puede trazar esta linea": la usan tanto
    // trazarLinea() como getPuntosConectables(), asi no se repite.
    // Debe ser entre puntos del tablero, vecinos en horizontal o vertical
    // (nada de diagonales) y que esa linea no exista ya.
    private boolean sePuedeTrazar(Punto puntoA, Punto puntoB) {

        return estaDentroDelTablero(puntoA)
                && estaDentroDelTablero(puntoB)
                && sonAdyacentes(puntoA, puntoB)
                && !lineas.contains(new Linea(puntoA, puntoB, null));
    }

    private boolean estaDentroDelTablero(Punto punto) {

        return punto.getFila() >= 0 && punto.getFila() < tamano
                && punto.getColumna() >= 0 && punto.getColumna() < tamano;
    }

    private boolean sonAdyacentes(Punto puntoA, Punto puntoB) {

        int distanciaFila = Math.abs(puntoA.getFila() - puntoB.getFila());
        int distanciaColumna = Math.abs(puntoA.getColumna() - puntoB.getColumna());

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

    // Todas las consultas regresan listas de SOLO LECTURA (copias): quien
    // consulte no puede modificar el estado interno del juego, ni falla
    // si el juego avanza mientras la Vista recorre la lista.
    public List<Linea> getLineas() {
        return Collections.unmodifiableList(new ArrayList<>(lineas));
    }

    public List<Cuadrado> getCuadros() {
        return Collections.unmodifiableList(new ArrayList<>(cuadros));
    }

    public List<Jugador> getJugadores() {
        return Collections.unmodifiableList(new ArrayList<>(jugadores));
    }

    public Jugador getJugadorActual() {
        return jugadores.get(turnoActual);
    }

    public boolean isJuegoTerminado() {
        int totalCuadros = (tamano - 1) * (tamano - 1);
        return cuadros.size() == totalCuadros;
    }

    // Puntos a los que se puede unir "desde" con una linea valida: los
    // vecinos de arriba, abajo, izquierda y derecha que esten dentro del
    // tablero y cuya linea todavia no exista. Regresa vacia si el juego
    // ya termino o si "desde" no es un punto del tablero.
    public List<Punto> getPuntosConectables(Punto desde) {

        List<Punto> conectables = new ArrayList<>();

        if (desde == null || isJuegoTerminado() || !estaDentroDelTablero(desde)) {
            return Collections.unmodifiableList(conectables);
        }

        int[][] direcciones = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] direccion : direcciones) {

            Punto candidato = new Punto(
                    desde.getFila() + direccion[0], desde.getColumna() + direccion[1]);

            if (sePuedeTrazar(desde, candidato)) {
                conectables.add(candidato);
            }
        }

        return Collections.unmodifiableList(conectables);
    }

    // Todos los jugadores que tengan el puntaje mas alto: si hay empate
    // regresa mas de uno.
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

        return Collections.unmodifiableList(ganadores);
    }
}