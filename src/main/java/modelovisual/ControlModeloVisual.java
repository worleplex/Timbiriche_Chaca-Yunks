package modelovisual;

import modelo.Cuadrado;
import modelo.IConsultaContenedorJuego;
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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ControlModeloVisual {

    private static final Color[] PALETA = {
        Color.BLUE, Color.RED, new Color(0, 150, 0), new Color(220, 140, 0)
    };

    private static final String[] NOMBRES_COLOR = {"Azul", "Rojo", "Verde", "Naranja"};

    private static final String NOMBRE_COLOR_PERSONALIZADO = "Personalizado";

    private final IConsultaModelo consultaModeloGeneral;
    private final IOperacionesModelo operacionesModeloGeneral;
    private final IConsultaContenedorJuego contenedorJuego;
    private final GestorAlmacenamientoVisual gestorVisual;
    private final Jugador jugadorLocal;
    private Jugador ultimoAnunciado;
    private final Map<Jugador, Color> colores;
    private final Map<Jugador, String> nombresColor;

    private Punto seleccionJuego;
    private List<Punto> adyacentesJuego = Collections.emptyList();

    private Punto seleccionPintada;
    private Set<Punto> adyacentesPintados = Collections.emptySet();
    private Punto hoverPintado;

    private Punto puntoBajoMouse;

    public ControlModeloVisual(IConsultaModelo consultaModeloGeneral,
                               IOperacionesModelo operacionesModeloGeneral,
                               IConsultaContenedorJuego contenedorJuego,
                               GestorAlmacenamientoVisual gestorVisual,
                               Jugador jugadorLocal) {
        this.jugadorLocal = jugadorLocal;
        this.consultaModeloGeneral = consultaModeloGeneral;
        this.operacionesModeloGeneral = operacionesModeloGeneral;
        this.contenedorJuego = contenedorJuego;
        this.gestorVisual = gestorVisual;

        this.colores = new HashMap<>();
        this.nombresColor = new HashMap<>();
        List<Jugador> jugadores = consultaModeloGeneral.getJugadores();
        for (int i = 0; i < jugadores.size(); i++) {
            colores.put(jugadores.get(i), PALETA[i % PALETA.length]);
            nombresColor.put(jugadores.get(i), NOMBRES_COLOR[i % NOMBRES_COLOR.length]);
        }
    }

    public void clic(int x, int y, int anchoPanel, int altoPanel) {

        Punto punto = getPuntoEnCoordenadas(x, y, anchoPanel, altoPanel);

        if (punto == null || !esMiTurno()) {
            return;
        }

        Punto seleccionado = consultaModeloGeneral.getPuntoSeleccionado();

        if (seleccionado == null) {
            operacionesModeloGeneral.seleccionarPunto(punto.getFila(), punto.getColumna());
            return;
        }

        if (seleccionado.equals(punto)) {
            operacionesModeloGeneral.deseleccionarPunto();
            return;
        }

        boolean jugadaValida = operacionesModeloGeneral.trazarLinea(
                seleccionado.getFila(), seleccionado.getColumna(),
                punto.getFila(), punto.getColumna());

        if (!jugadaValida) {
            operacionesModeloGeneral.seleccionarPunto(punto.getFila(), punto.getColumna());
        }
    }

    public void mover(int x, int y, int anchoPanel, int altoPanel) {

        Punto nuevo = esMiTurno() ? getPuntoEnCoordenadas(x, y, anchoPanel, altoPanel) : null;

        if (Objects.equals(nuevo, puntoBajoMouse)) {
            return;
        }

        puntoBajoMouse = nuevo;
        publicarCambiosLocales();
    }

    public void salirDelTablero() {

        if (puntoBajoMouse == null) {
            return;
        }

        puntoBajoMouse = null;
        publicarCambiosLocales();
    }

    private void publicarCambiosLocales() {
        gestorVisual.iniciarCambios();
        boolean hoverCambio = reconciliarPuntos();
        if (hoverCambio || gestorVisual.hayCambios()) {
            gestorVisual.notificarCambios();
        }
    }

    public void procesarCambiosDelJuego() {

        gestorVisual.iniciarCambios();

        for (Linea linea : contenedorJuego.getLineasNuevas()) {
            gestorVisual.guardarLinea(new LineaVisual(
                    linea.getPuntoA().getFila(), linea.getPuntoA().getColumna(),
                    linea.getPuntoB().getFila(), linea.getPuntoB().getColumna(),
                    getColor(linea.getJugador())));
        }

        for (Cuadrado cuadrado : contenedorJuego.getCuadrosNuevos()) {
            Punto esquina = cuadrado.getEsquinaSupIzq();
            gestorVisual.guardarCuadro(new CuadroVisual(
                    esquina.getFila(), esquina.getColumna(), getColor(cuadrado.getJugador())));
        }

        if (contenedorJuego.hayCambioSeleccion()) {
            seleccionJuego = contenedorJuego.getPuntoSeleccionado();
            adyacentesJuego = contenedorJuego.getPuntosAdyacentes();
        }

        boolean hoverCambio = reconciliarPuntos();

        if (hoverCambio || gestorVisual.hayCambios()) {
            gestorVisual.notificarCambios();
        }
    }

    private boolean reconciliarPuntos() {

        boolean miTurno = esMiTurno();

        Punto seleccionDeseada = seleccionJuego;
        Set<Punto> adyacentesDeseados = seleccionDeseada == null
                ? Collections.<Punto>emptySet() : new LinkedHashSet<>(adyacentesJuego);
        Punto hoverDeseado = miTurno ? puntoBajoMouse : null;

        Set<Punto> afectados = new LinkedHashSet<>();
        agregarSiNoNulo(afectados, seleccionPintada);
        afectados.addAll(adyacentesPintados);
        agregarSiNoNulo(afectados, hoverPintado);
        agregarSiNoNulo(afectados, seleccionDeseada);
        afectados.addAll(adyacentesDeseados);
        agregarSiNoNulo(afectados, hoverDeseado);

        Map<Punto, EstadoPunto> antes = new LinkedHashMap<>();
        for (Punto punto : afectados) {
            antes.put(punto, estadoPintado(punto));
        }

        boolean hoverCambio = !Objects.equals(hoverPintado, hoverDeseado);

        seleccionPintada = seleccionDeseada;
        adyacentesPintados = adyacentesDeseados;
        hoverPintado = hoverDeseado;

        for (Punto punto : afectados) {
            EstadoPunto despues = estadoPintado(punto);
            EstadoPunto anterior = antes.get(punto);
            if (despues != anterior) {
                gestorVisual.guardarPunto(new PuntoVisual(
                        punto.getFila(), punto.getColumna(), despues, anterior));
            }
        }

        return hoverCambio;
    }

    private void agregarSiNoNulo(Set<Punto> conjunto, Punto punto) {
        if (punto != null) {
            conjunto.add(punto);
        }
    }

    private EstadoPunto estadoPintado(Punto punto) {

        if (punto.equals(seleccionPintada)) {
            return EstadoPunto.SELECCIONADO;
        }

        if (adyacentesPintados.contains(punto)) {
            return EstadoPunto.RESALTADO;
        }

        if (punto.equals(hoverPintado)) {
            return EstadoPunto.HOVER;
        }

        return EstadoPunto.NORMAL;
    }

    public Jugador getJugadorLocal() {
        return jugadorLocal;
    }

    public boolean esMiTurno() {
        return !consultaModeloGeneral.isJuegoTerminado()
                && jugadorLocal.equals(consultaModeloGeneral.getJugadorActual());
    }

    public boolean hayPuntoBajoMouse() {
        return hoverPintado != null;
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
        double celda = getTamanoCelda(anchoPanel, altoPanel);
        double tolerancia = Math.min(celda / 2, 22);

        int columna = (int) Math.round((x - origenX(anchoPanel, altoPanel)) / celda);
        int fila = (int) Math.round((y - origenY(anchoPanel, altoPanel)) / celda);

        if (columna < 0 || columna >= tamano || fila < 0 || fila >= tamano) {
            return null;
        }

        double distancia = Math.hypot(
                x - getCoordenadaX(columna, anchoPanel, altoPanel),
                y - getCoordenadaY(fila, anchoPanel, altoPanel));

        return distancia < tolerancia ? new Punto(fila, columna) : null;
    }

    public int getTamanoTablero() {
        return consultaModeloGeneral.getTamano();
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
