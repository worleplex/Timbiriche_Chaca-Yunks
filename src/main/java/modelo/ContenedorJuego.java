package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ContenedorJuego implements IConsultaContenedorJuego {

    private boolean cambioSeleccion;
    private Punto puntoSeleccionado;
    private final List<Punto> puntosAdyacentes = new ArrayList<>();
    private final List<Linea> lineasNuevas = new ArrayList<>();
    private final List<Cuadrado> cuadrosNuevos = new ArrayList<>();
    private boolean cambioEstadoJugadores;
    private Jugador jugadorActual;
    private final Map<Jugador, Integer> puntajes = new LinkedHashMap<>();
    private boolean juegoTerminado;
    private final List<Jugador> ganadores = new ArrayList<>();

    ContenedorJuego() {
    }

    void limpiar() {
        cambioSeleccion = false;
        puntoSeleccionado = null;
        puntosAdyacentes.clear();
        lineasNuevas.clear();
        cuadrosNuevos.clear();
        cambioEstadoJugadores = false;
        jugadorActual = null;
        puntajes.clear();
        juegoTerminado = false;
        ganadores.clear();
    }

    void registrarSeleccion(Punto seleccionado, List<Punto> adyacentes) {
        cambioSeleccion = true;
        puntoSeleccionado = seleccionado;
        puntosAdyacentes.clear();
        puntosAdyacentes.addAll(adyacentes);
    }

    void registrarLinea(Linea linea) {
        lineasNuevas.add(linea);
    }

    void registrarCuadro(Cuadrado cuadro) {
        cuadrosNuevos.add(cuadro);
    }

    void registrarEstadoJugadores(Jugador actual, Map<Jugador, Integer> nuevosPuntajes,
                                  boolean terminado, List<Jugador> nuevosGanadores) {
        cambioEstadoJugadores = true;
        jugadorActual = actual;
        puntajes.clear();
        puntajes.putAll(nuevosPuntajes);
        juegoTerminado = terminado;
        ganadores.clear();
        ganadores.addAll(nuevosGanadores);
    }

    boolean isVacio() {
        return !cambioSeleccion && lineasNuevas.isEmpty() && cuadrosNuevos.isEmpty()
                && !cambioEstadoJugadores;
    }

    @Override
    public boolean hayCambioSeleccion() {
        return cambioSeleccion;
    }

    @Override
    public Punto getPuntoSeleccionado() {
        return puntoSeleccionado;
    }

    @Override
    public List<Punto> getPuntosAdyacentes() {
        return Collections.unmodifiableList(new ArrayList<>(puntosAdyacentes));
    }

    @Override
    public List<Linea> getLineasNuevas() {
        return Collections.unmodifiableList(new ArrayList<>(lineasNuevas));
    }

    @Override
    public List<Cuadrado> getCuadrosNuevos() {
        return Collections.unmodifiableList(new ArrayList<>(cuadrosNuevos));
    }

    @Override
    public boolean hayCambioEstadoJugadores() {
        return cambioEstadoJugadores;
    }

    @Override
    public Jugador getJugadorActual() {
        return jugadorActual;
    }

    @Override
    public Map<Jugador, Integer> getPuntajes() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(puntajes));
    }

    @Override
    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }

    @Override
    public List<Jugador> getGanadores() {
        return Collections.unmodifiableList(new ArrayList<>(ganadores));
    }
}
