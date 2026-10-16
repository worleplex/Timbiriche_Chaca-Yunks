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

    private final TraductorCoordenadas traductor;
    private final List<CoordenadaTablero> puntosTablero;

    private CoordenadaTablero seleccionJuego;
    private List<CoordenadaTablero> adyacentesJuego = Collections.emptyList();

    private CoordenadaTablero seleccionPintada;
    private Set<CoordenadaTablero> adyacentesPintados = Collections.emptySet();
    private CoordenadaTablero hoverPintado;

    private CoordenadaTablero puntoBajoMouse;

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
        this.traductor = new TraductorCoordenadas(consultaModeloGeneral.getTamano());
        this.puntosTablero = Collections.unmodifiableList(traductor.todosLosPuntos());

        this.colores = new HashMap<>();
        this.nombresColor = new HashMap<>();
        List<Jugador> jugadores = consultaModeloGeneral.getJugadores();
        for (int i = 0; i < jugadores.size(); i++) {
            colores.put(jugadores.get(i), PALETA[i % PALETA.length]);
            nombresColor.put(jugadores.get(i), NOMBRES_COLOR[i % NOMBRES_COLOR.length]);
        }
    }

    public void clic(int x, int y, int anchoPanel, int altoPanel) {

        CoordenadaTablero punto = getPuntoEnCoordenadas(x, y, anchoPanel, altoPanel);

        if (punto == null || !esMiTurno()) {
            return;
        }

        Punto seleccionadoJuego = consultaModeloGeneral.getPuntoSeleccionado();
        CoordenadaTablero seleccionado = seleccionadoJuego == null ? null : traducir(seleccionadoJuego);

        if (seleccionado == null) {
            operacionesModeloGeneral.seleccionarPunto(traductor.aFila(punto), traductor.aColumna(punto));
            return;
        }

        if (seleccionado.equals(punto)) {
            operacionesModeloGeneral.deseleccionarPunto();
            return;
        }

        boolean jugadaValida = operacionesModeloGeneral.trazarLinea(
                traductor.aFila(seleccionado), traductor.aColumna(seleccionado),
                traductor.aFila(punto), traductor.aColumna(punto));

        if (!jugadaValida) {
            operacionesModeloGeneral.seleccionarPunto(traductor.aFila(punto), traductor.aColumna(punto));
        }
    }

    private CoordenadaTablero traducir(Punto punto) {
        return traductor.aTablero(punto.getFila(), punto.getColumna());
    }

    public void mover(int x, int y, int anchoPanel, int altoPanel) {

        CoordenadaTablero nuevo = esMiTurno() ? getPuntoEnCoordenadas(x, y, anchoPanel, altoPanel) : null;

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
            CoordenadaTablero a = traducir(linea.getPuntoA());
            CoordenadaTablero b = traducir(linea.getPuntoB());
            gestorVisual.guardarLinea(new LineaVisual(
                    a.getX(), a.getY(), b.getX(), b.getY(), getColor(linea.getJugador())));
        }

        for (Cuadrado cuadrado : contenedorJuego.getCuadrosNuevos()) {
            CoordenadaTablero esquina = traducir(cuadrado.getEsquinaSupIzq());
            gestorVisual.guardarCuadro(new CuadroVisual(
                    esquina.getX(), esquina.getY(), traductor.getSeparacion(),
                    getColor(cuadrado.getJugador())));
        }

        if (contenedorJuego.hayCambioSeleccion()) {
            Punto seleccion = contenedorJuego.getPuntoSeleccionado();
            seleccionJuego = seleccion == null ? null : traducir(seleccion);
            List<CoordenadaTablero> adyacentes = new ArrayList<>();
            for (Punto adyacente : contenedorJuego.getPuntosAdyacentes()) {
                adyacentes.add(traducir(adyacente));
            }
            adyacentesJuego = adyacentes;
        }

        boolean hoverCambio = reconciliarPuntos();

        if (hoverCambio || gestorVisual.hayCambios()) {
            gestorVisual.notificarCambios();
        }
    }

    private boolean reconciliarPuntos() {

        boolean miTurno = esMiTurno();

        CoordenadaTablero seleccionDeseada = seleccionJuego;
        Set<CoordenadaTablero> adyacentesDeseados = seleccionDeseada == null
                ? Collections.<CoordenadaTablero>emptySet() : new LinkedHashSet<>(adyacentesJuego);
        CoordenadaTablero hoverDeseado = miTurno ? puntoBajoMouse : null;

        Set<CoordenadaTablero> afectados = new LinkedHashSet<>();
        agregarSiNoNulo(afectados, seleccionPintada);
        afectados.addAll(adyacentesPintados);
        agregarSiNoNulo(afectados, hoverPintado);
        agregarSiNoNulo(afectados, seleccionDeseada);
        afectados.addAll(adyacentesDeseados);
        agregarSiNoNulo(afectados, hoverDeseado);

        Map<CoordenadaTablero, EstadoPunto> antes = new LinkedHashMap<>();
        for (CoordenadaTablero punto : afectados) {
            antes.put(punto, estadoPintado(punto));
        }

        boolean hoverCambio = !Objects.equals(hoverPintado, hoverDeseado);

        seleccionPintada = seleccionDeseada;
        adyacentesPintados = adyacentesDeseados;
        hoverPintado = hoverDeseado;

        for (CoordenadaTablero punto : afectados) {
            EstadoPunto despues = estadoPintado(punto);
            EstadoPunto anterior = antes.get(punto);
            if (despues != anterior) {
                gestorVisual.guardarPunto(new PuntoVisual(
                        punto.getX(), punto.getY(), despues, anterior));
            }
        }

        return hoverCambio;
    }

    private void agregarSiNoNulo(Set<CoordenadaTablero> conjunto, CoordenadaTablero punto) {
        if (punto != null) {
            conjunto.add(punto);
        }
    }

    private EstadoPunto estadoPintado(CoordenadaTablero punto) {

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

    private CoordenadaTablero getPuntoEnCoordenadas(int x, int y, int anchoPanel, int altoPanel) {

        double escala = getEscala(anchoPanel, altoPanel);
        double xTablero = (x - getDesplazamientoX(anchoPanel, altoPanel)) / escala;
        double yTablero = (y - getDesplazamientoY(anchoPanel, altoPanel)) / escala;

        // Misma tolerancia que antes: media celda, con tope de 22 pixeles.
        double tolerancia = Math.min(traductor.getSeparacion() / 2, 22 / escala);

        CoordenadaTablero cercano = null;
        double mejor = tolerancia;
        for (CoordenadaTablero punto : puntosTablero) {
            double distancia = Math.hypot(xTablero - punto.getX(), yTablero - punto.getY());
            if (distancia < mejor) {
                mejor = distancia;
                cercano = punto;
            }
        }
        return cercano;
    }

    public List<CoordenadaTablero> getPuntosTablero() {
        return puntosTablero;
    }

    public double getLadoTablero() {
        return TraductorCoordenadas.LADO;
    }

    public double getSeparacionPuntos() {
        return traductor.getSeparacion();
    }

    public double getEscala(int anchoPanel, int altoPanel) {
        return Math.min(anchoPanel, altoPanel) / TraductorCoordenadas.LADO;
    }

    public double getDesplazamientoX(int anchoPanel, int altoPanel) {
        return (anchoPanel - TraductorCoordenadas.LADO * getEscala(anchoPanel, altoPanel)) / 2;
    }

    public double getDesplazamientoY(int anchoPanel, int altoPanel) {
        return (altoPanel - TraductorCoordenadas.LADO * getEscala(anchoPanel, altoPanel)) / 2;
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
