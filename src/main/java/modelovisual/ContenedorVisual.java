package modelovisual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ContenedorVisual implements IConsultaContenedor {

    private final Map<CoordenadaTablero, PuntoVisual> puntos = new LinkedHashMap<>();
    private final List<LineaVisual> lineas = new ArrayList<>();
    private final List<CuadroVisual> cuadros = new ArrayList<>();

    ContenedorVisual() {
    }

    void limpiar() {
        puntos.clear();
        lineas.clear();
        cuadros.clear();
    }

    boolean isVacio() {
        return puntos.isEmpty() && lineas.isEmpty() && cuadros.isEmpty();
    }

    void agregarPunto(PuntoVisual nuevo) {

        CoordenadaTablero clave = new CoordenadaTablero(nuevo.getX(), nuevo.getY());
        PuntoVisual previo = puntos.get(clave);

        if (previo != null) {
            nuevo = new PuntoVisual(nuevo.getX(), nuevo.getY(),
                    nuevo.getEstado(), previo.getEstadoAnterior());
        }

        puntos.put(clave, nuevo);
    }

    void agregarLinea(LineaVisual linea) {
        lineas.add(linea);
    }

    void agregarCuadro(CuadroVisual cuadro) {
        cuadros.add(cuadro);
    }

    @Override
    public List<PuntoVisual> getPuntosCambiados() {
        return Collections.unmodifiableList(new ArrayList<>(puntos.values()));
    }

    @Override
    public List<LineaVisual> getLineasNuevas() {
        return Collections.unmodifiableList(new ArrayList<>(lineas));
    }

    @Override
    public List<CuadroVisual> getCuadrosNuevos() {
        return Collections.unmodifiableList(new ArrayList<>(cuadros));
    }

    @Override
    public EstadoPunto getEstadoAnterior(double x, double y) {
        PuntoVisual punto = puntos.get(new CoordenadaTablero(x, y));
        return punto == null ? EstadoPunto.NORMAL : punto.getEstadoAnterior();
    }
}