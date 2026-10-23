package modelovisual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ContenedorVisual implements IConsultaContenedor {

    private final Map<CoordenadaTablero, PuntoVisual> puntos = new LinkedHashMap<>();
    private final List<LineaVisual> lineas = new ArrayList<>();
    private final List<CuadroVisual> cuadros = new ArrayList<>();
    private boolean cambioEstadoJugadores;
    private TarjetaJugador tarjetaPrincipal;
    private List<TarjetaJugador> tarjetasRivales = Collections.emptyList();
    private Color colorTurno;
    private ResultadoPartida resultado;

    ContenedorVisual() {
    }

    void limpiar() {
        puntos.clear();
        lineas.clear();
        cuadros.clear();
        cambioEstadoJugadores = false;
    }

    boolean isVacio() {
        return puntos.isEmpty() && lineas.isEmpty() && cuadros.isEmpty() && !cambioEstadoJugadores;
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

    void registrarEstadoJugadores(TarjetaJugador principal, List<TarjetaJugador> rivales,
                                  Color turno, ResultadoPartida resultadoPartida) {
        cambioEstadoJugadores = true;
        tarjetaPrincipal = principal;
        tarjetasRivales = Collections.unmodifiableList(new ArrayList<>(rivales));
        colorTurno = turno;
        resultado = resultadoPartida;
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

    @Override
    public TarjetaJugador getTarjetaPrincipal() {
        return tarjetaPrincipal;
    }

    @Override
    public List<TarjetaJugador> getTarjetasRivales() {
        return tarjetasRivales;
    }

    @Override
    public Color getColorTurno() {
        return colorTurno;
    }

    @Override
    public ResultadoPartida getResultado() {
        return resultado;
    }
}