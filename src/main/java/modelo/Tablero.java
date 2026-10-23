package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Tablero {

    public static final int MIN_JUGADORES = 2;
    public static final int MAX_JUGADORES = 4;
    private static final int PUNTOS_POR_JUGADOR_EXTRA = 10;

    private final int tamano;
    private final Set<Linea> lineas;
    private final List<Cuadrado> cuadros;
    private final List<Jugador> jugadores;
    private int turnoActual;
    private Punto puntoSeleccionado;
    private final GestorAlmacenamientoJuego gestor;

    public Tablero(List<Jugador> jugadores, GestorAlmacenamientoJuego gestor) {
        this(tamanoParaJugadores(jugadores), jugadores, gestor);
    }

    Tablero(int tamano, List<Jugador> jugadores, GestorAlmacenamientoJuego gestor) {

        if (tamano < 2) {
            throw new IllegalArgumentException("El tablero necesita al menos 2 puntos por lado");
        }
        if (jugadores == null || jugadores.isEmpty()) {
            throw new IllegalArgumentException("Se necesita al menos un jugador");
        }

        this.gestor = Objects.requireNonNull(gestor, "El gestor de almacenamiento es obligatorio");
        this.tamano = tamano;
        this.jugadores = new ArrayList<>(jugadores);
        this.lineas = new LinkedHashSet<>();
        this.cuadros = new ArrayList<>();
        this.turnoActual = 0;
    }

    private static int tamanoParaJugadores(List<Jugador> jugadores) {

        if (jugadores == null || jugadores.size() < MIN_JUGADORES || jugadores.size() > MAX_JUGADORES) {
            throw new IllegalArgumentException(
                    "Se necesitan de " + MIN_JUGADORES + " a " + MAX_JUGADORES + " jugadores");
        }

        return (jugadores.size() - 1) * PUNTOS_POR_JUGADOR_EXTRA;
    }

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

        gestor.iniciarAccion();

        Linea nueva = new Linea(puntoA, puntoB, jugadorActual);
        lineas.add(nueva);
        gestor.guardarLinea(nueva);

        int cuadrosCompletados = completarCuadros(puntoA, puntoB, jugadorActual);

        if (cuadrosCompletados == 0) {
            turnoActual = (turnoActual + 1) % jugadores.size();
        }

        if (puntoSeleccionado != null) {
            puntoSeleccionado = null;
            gestor.guardarSeleccion(null, Collections.<Punto>emptyList());
        }

        gestor.publicarCambios();

        return true;
    }

    public boolean clicPunto(Jugador jugador, int fila, int columna) {

        if (isJuegoTerminado() || jugador == null || !jugador.equals(getJugadorActual())) {
            return false;
        }

        Punto punto = new Punto(fila, columna);

        if (!estaDentroDelTablero(punto)) {
            return false;
        }

        if (puntoSeleccionado == null) {
            return seleccionarPunto(fila, columna);
        }

        if (puntoSeleccionado.equals(punto)) {
            return deseleccionarPunto();
        }

        if (trazarLinea(puntoSeleccionado.getFila(), puntoSeleccionado.getColumna(), fila, columna)) {
            return true;
        }

        return seleccionarPunto(fila, columna);
    }

    public boolean seleccionarPunto(int fila, int columna) {

        Punto punto = new Punto(fila, columna);

        if (isJuegoTerminado() || !estaDentroDelTablero(punto) || punto.equals(puntoSeleccionado)) {
            return false;
        }

        puntoSeleccionado = punto;

        gestor.iniciarAccion();
        gestor.guardarSeleccion(punto, getPuntosConectables(punto));
        gestor.publicarCambios();

        return true;
    }

    public boolean deseleccionarPunto() {

        if (puntoSeleccionado == null) {
            return false;
        }

        puntoSeleccionado = null;

        gestor.iniciarAccion();
        gestor.guardarSeleccion(null, Collections.<Punto>emptyList());
        gestor.publicarCambios();

        return true;
    }

    public Punto getPuntoSeleccionado() {
        return puntoSeleccionado;
    }

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

    private int completarCuadros(Punto puntoA, Punto puntoB, Jugador jugador) {

        int completados = 0;

        for (Punto esquina : esquinasCandidatas(puntoA, puntoB)) {

            if (yaCompletado(esquina)) {
                continue;
            }

            if (cuadradoCompleto(esquina)) {
                Cuadrado cuadrado = construirCuadrado(esquina, jugador);
                cuadros.add(cuadrado);
                jugador.sumarPunto();
                gestor.guardarCuadro(cuadrado);
                completados++;
            }
        }

        return completados;
    }

    private List<Punto> esquinasCandidatas(Punto puntoA, Punto puntoB) {

        List<Punto> candidatas = new ArrayList<>();

        if (puntoA.getFila() == puntoB.getFila()) {

            int fila = puntoA.getFila();
            int columna = Math.min(puntoA.getColumna(), puntoB.getColumna());

            if (fila - 1 >= 0) {
                candidatas.add(new Punto(fila - 1, columna));
            }
            if (fila < tamano - 1) {
                candidatas.add(new Punto(fila, columna));
            }

        } else {

            int columna = puntoA.getColumna();
            int fila = Math.min(puntoA.getFila(), puntoB.getFila());

            if (columna - 1 >= 0) {
                candidatas.add(new Punto(fila, columna - 1));
            }
            if (columna < tamano - 1) {
                candidatas.add(new Punto(fila, columna));
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
        lados.add(new Linea(supIzq, supDer, null));
        lados.add(new Linea(infIzq, infDer, null));
        lados.add(new Linea(supIzq, infIzq, null));
        lados.add(new Linea(supDer, infDer, null));

        return lados;
    }

    public int getTamano() {
        return tamano;
    }

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
