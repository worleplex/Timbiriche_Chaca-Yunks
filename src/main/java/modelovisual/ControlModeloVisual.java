package modelovisual;

import modelo.Cuadrado;
import modelo.IConsultaModelo;
import modelo.IOperacionesModelo;
import modelo.Jugador;
import modelo.Linea;
import modelo.Punto;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ControlModeloVisual {

    private static final Color[] PALETA = {
        Color.BLUE, Color.RED, new Color(0, 150, 0), new Color(220, 140, 0)
    };

    private static final String[] NOMBRES_COLOR = {"Azul", "Rojo", "Verde", "Naranja"};

    private static final String NOMBRE_COLOR_PERSONALIZADO = "Personalizado";

    private final IConsultaModelo consultaModeloGeneral;
    private final IOperacionesModelo operacionesModeloGeneral;
    private final Jugador jugadorLocal;
    private Punto puntoSeleccionado;
    private Punto puntoBajoMouse;
    private Jugador ultimoAnunciado;
    private final Map<Jugador, Color> colores;
    private final Map<Jugador, String> nombresColor;

    public ControlModeloVisual(IConsultaModelo consultaModeloGeneral,
                               IOperacionesModelo operacionesModeloGeneral,
                               Jugador jugadorLocal) {
        this.jugadorLocal = jugadorLocal;
        this.consultaModeloGeneral = consultaModeloGeneral;
        this.operacionesModeloGeneral = operacionesModeloGeneral;

        this.colores = new HashMap<>();
        this.nombresColor = new HashMap<>();
        List<Jugador> jugadores = consultaModeloGeneral.getJugadores();
        for (int i = 0; i < jugadores.size(); i++) {
            colores.put(jugadores.get(i), PALETA[i % PALETA.length]);
            nombresColor.put(jugadores.get(i), NOMBRES_COLOR[i % NOMBRES_COLOR.length]);
        }
    }

    public boolean clic(int x, int y, int anchoPanel, int altoPanel) {

        Punto punto = getPuntoEnCoordenadas(x, y, anchoPanel, altoPanel);

        if (punto == null) {
            return false;
        }

        return clicEnPunto(punto);
    }

    private boolean clicEnPunto(Punto punto) {

        if (!esMiTurno()) {
            return false;
        }

        if (!haySeleccion()) {
            alternarSeleccion(punto);
            return true;
        }

        if (puntoSeleccionado.equals(punto)) {
            alternarSeleccion(punto);
            return true;
        }

        boolean jugadaValida = operacionesModeloGeneral.trazarLinea(
                puntoSeleccionado.getFila(), puntoSeleccionado.getColumna(),
                punto.getFila(), punto.getColumna());

        if (jugadaValida) {
            return false;
        }

        puntoSeleccionado = punto;
        return true;
    }

    public boolean mover(int x, int y, int anchoPanel, int altoPanel) {

        Punto nuevo = esMiTurno() ? getPuntoEnCoordenadas(x, y, anchoPanel, altoPanel) : null;

        if (Objects.equals(nuevo, puntoBajoMouse)) {
            return false;
        }

        puntoBajoMouse = nuevo;
        return true;
    }

    public boolean salirDelTablero() {

        if (puntoBajoMouse == null) {
            return false;
        }

        puntoBajoMouse = null;
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

    public Jugador getJugadorLocal() {
        return jugadorLocal;
    }

    public boolean esMiTurno() {
        return !consultaModeloGeneral.isJuegoTerminado()
                && jugadorLocal.equals(consultaModeloGeneral.getJugadorActual());
    }

    public boolean haySeleccion() {
        return puntoSeleccionado != null;
    }

    public Punto getPuntoSeleccionado() {
        return puntoSeleccionado;
    }

    public boolean hayPuntoBajoMouse() {
        return puntoBajoMouse != null && esMiTurno();
    }

    public List<Punto> getPuntosResaltados() {

        if (puntoSeleccionado == null) {
            return Collections.emptyList();
        }

        return consultaModeloGeneral.getPuntosConectables(puntoSeleccionado);
    }

    public Color getColor(Jugador jugador) {
        return colores.getOrDefault(jugador, Color.GRAY);
    }

    public Color getColorTurno() {

        if (consultaModeloGeneral.isJuegoTerminado()) {
            return null;
        }

        return getColor(consultaModeloGeneral.getJugadorActual());
    }

    public String getTituloVentana() {
        return "Timbiriche - " + jugadorLocal.getNombre()
                + " (" + nombresColor.getOrDefault(jugadorLocal, NOMBRE_COLOR_PERSONALIZADO) + ")";
    }

    public String tomarAnuncioTurno() {

        if (consultaModeloGeneral.isJuegoTerminado()) {
            return null;
        }

        Jugador actual = consultaModeloGeneral.getJugadorActual();

        if (actual.equals(ultimoAnunciado)) {
            return null;
        }

        ultimoAnunciado = actual;
        return "¡TURNO DE " + actual.getNombre().toUpperCase() + "!";
    }

    public Punto getPuntoEnCoordenadas(int x, int y, int anchoPanel, int altoPanel) {

        int tamano = consultaModeloGeneral.getTamano();
        double tolerancia = Math.min(getTamanoCelda(anchoPanel, altoPanel) / 2, 22);
        double mejorDistancia = tolerancia;
        Punto mejor = null;

        for (int fila = 0; fila < tamano; fila++) {
            for (int columna = 0; columna < tamano; columna++) {

                double distancia = Math.hypot(
                        x - getCoordenadaX(columna, anchoPanel, altoPanel),
                        y - getCoordenadaY(fila, anchoPanel, altoPanel));

                if (distancia < mejorDistancia) {
                    mejorDistancia = distancia;
                    mejor = new Punto(fila, columna);
                }
            }
        }

        return mejor;
    }

    public double getCoordenadaX(int columna, int anchoPanel, int altoPanel) {
        return origenX(anchoPanel, altoPanel) + columna * getTamanoCelda(anchoPanel, altoPanel);
    }

    public double getCoordenadaY(int fila, int anchoPanel, int altoPanel) {
        return origenY(anchoPanel, altoPanel) + fila * getTamanoCelda(anchoPanel, altoPanel);
    }

    public double getTamanoCelda(int anchoPanel, int altoPanel) {
        return lado(anchoPanel, altoPanel) / (consultaModeloGeneral.getTamano() - 1);
    }

    private double lado(int anchoPanel, int altoPanel) {
        double margen = Math.min(anchoPanel, altoPanel) * 0.045;
        return Math.min(anchoPanel, altoPanel) - 2 * margen;
    }

    private double origenX(int anchoPanel, int altoPanel) {
        return (anchoPanel - lado(anchoPanel, altoPanel)) / 2;
    }

    private double origenY(int anchoPanel, int altoPanel) {
        return (altoPanel - lado(anchoPanel, altoPanel)) / 2;
    }

    public List<CuadroVisual> getCuadros(int anchoPanel, int altoPanel) {

        double lado = getTamanoCelda(anchoPanel, altoPanel);
        List<CuadroVisual> cuadros = new ArrayList<>();

        for (Cuadrado cuadrado : consultaModeloGeneral.getCuadros()) {

            Punto esquina = cuadrado.getEsquinaSupIzq();
            cuadros.add(new CuadroVisual(
                    getCoordenadaX(esquina.getColumna(), anchoPanel, altoPanel),
                    getCoordenadaY(esquina.getFila(), anchoPanel, altoPanel),
                    lado,
                    getColor(cuadrado.getJugador())));
        }

        return cuadros;
    }

    public List<LineaVisual> getLineas(int anchoPanel, int altoPanel) {

        List<LineaVisual> lineas = new ArrayList<>();

        for (Linea linea : consultaModeloGeneral.getLineas()) {

            lineas.add(new LineaVisual(
                    getCoordenadaX(linea.getPuntoA().getColumna(), anchoPanel, altoPanel),
                    getCoordenadaY(linea.getPuntoA().getFila(), anchoPanel, altoPanel),
                    getCoordenadaX(linea.getPuntoB().getColumna(), anchoPanel, altoPanel),
                    getCoordenadaY(linea.getPuntoB().getFila(), anchoPanel, altoPanel),
                    getColor(linea.getJugador())));
        }

        return lineas;
    }

    public List<PuntoVisual> getPuntos(int anchoPanel, int altoPanel) {

        int tamano = consultaModeloGeneral.getTamano();
        List<Punto> resaltados = getPuntosResaltados();
        Punto bajoMouse = hayPuntoBajoMouse() ? puntoBajoMouse : null;
        List<PuntoVisual> puntos = new ArrayList<>();

        for (int fila = 0; fila < tamano; fila++) {
            for (int columna = 0; columna < tamano; columna++) {

                Punto actual = new Punto(fila, columna);
                puntos.add(new PuntoVisual(
                        getCoordenadaX(columna, anchoPanel, altoPanel),
                        getCoordenadaY(fila, anchoPanel, altoPanel),
                        estadoDe(actual, resaltados, bajoMouse)));
            }
        }

        return puntos;
    }

    private EstadoPunto estadoDe(Punto punto, List<Punto> resaltados, Punto bajoMouse) {

        if (punto.equals(puntoSeleccionado)) {
            return EstadoPunto.SELECCIONADO;
        }

        if (resaltados.contains(punto)) {
            return EstadoPunto.RESALTADO;
        }

        if (punto.equals(bajoMouse)) {
            return EstadoPunto.HOVER;
        }

        return EstadoPunto.NORMAL;
    }

    public ResultadoPartida getResultado() {

        if (!consultaModeloGeneral.isJuegoTerminado()) {
            return null;
        }

        List<Jugador> ganadores = consultaModeloGeneral.getGanadores();
        List<Jugador> posiciones = new ArrayList<>(consultaModeloGeneral.getJugadores());
        posiciones.sort(Comparator.comparingInt(Jugador::getPuntaje).reversed());

        String subtitulo;
        Color colorSubtitulo;
        if (ganadores.size() == 1) {
            subtitulo = "Gana " + ganadores.get(0).getNombre();
            colorSubtitulo = getColor(ganadores.get(0));
        } else {
            subtitulo = "Empate entre " + nombresDe(ganadores);
            colorSubtitulo = null;
        }

        List<FilaResultado> filas = new ArrayList<>();
        for (Jugador jugador : posiciones) {

            int posicion = 1;
            for (Jugador otro : posiciones) {
                if (otro.getPuntaje() > jugador.getPuntaje()) {
                    posicion++;
                }
            }

            int puntaje = jugador.getPuntaje();
            filas.add(new FilaResultado(posicion, jugador.getNombre(),
                    puntaje + (puntaje == 1 ? " punto" : " puntos"), getColor(jugador)));
        }

        return new ResultadoPartida("¡FIN DEL JUEGO!", subtitulo, colorSubtitulo, filas);
    }

    private String nombresDe(List<Jugador> jugadores) {

        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < jugadores.size(); i++) {
            if (i > 0) {
                texto.append(", ");
            }
            texto.append(jugadores.get(i).getNombre());
        }
        return texto.toString();
    }

    public TarjetaJugador getTarjetaPrincipal() {

        boolean terminado = consultaModeloGeneral.isJuegoTerminado();
        boolean esTurno = esMiTurno();

        String subtitulo;
        if (terminado) {
            subtitulo = consultaModeloGeneral.getGanadores().contains(jugadorLocal)
                    ? "Ganador" : "Partida terminada";
        } else {
            subtitulo = esTurno ? "Tu turno" : "Tu jugador";
        }

        return crearTarjeta(jugadorLocal, esTurno, subtitulo);
    }

    public List<TarjetaJugador> getTarjetasRivales() {

        List<Jugador> todos = consultaModeloGeneral.getJugadores();
        int inicio = Math.max(0, todos.indexOf(jugadorLocal));
        boolean terminado = consultaModeloGeneral.isJuegoTerminado();
        Jugador actual = consultaModeloGeneral.getJugadorActual();

        List<TarjetaJugador> tarjetas = new ArrayList<>();
        for (int i = 1; i < todos.size(); i++) {
            Jugador rival = todos.get((inicio + i) % todos.size());
            tarjetas.add(crearTarjeta(rival, !terminado && rival.equals(actual), null));
        }
        return tarjetas;
    }

    private TarjetaJugador crearTarjeta(Jugador jugador, boolean activa, String subtitulo) {
        int puntos = jugador.getPuntaje();
        return new TarjetaJugador(jugador.getNombre(), puntos,
                puntos == 1 ? "punto" : "puntos",
                getColor(jugador), activa, subtitulo);
    }
}
