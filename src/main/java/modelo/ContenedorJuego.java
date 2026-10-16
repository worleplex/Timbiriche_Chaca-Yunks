package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContenedorJuego implements IConsultaContenedorJuego {

    private boolean cambioSeleccion;
    private Punto puntoSeleccionado;
    private final List<Punto> puntosAdyacentes = new ArrayList<>();
    private final List<Linea> lineasNuevas = new ArrayList<>();
    private final List<Cuadrado> cuadrosNuevos = new ArrayList<>();

    ContenedorJuego() {
    }

    void limpiar() {
        cambioSeleccion = false;
        puntoSeleccionado = null;
        puntosAdyacentes.clear();
        lineasNuevas.clear();
        cuadrosNuevos.clear();
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

    boolean isVacio() {
        return !cambioSeleccion && lineasNuevas.isEmpty() && cuadrosNuevos.isEmpty();
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
}
