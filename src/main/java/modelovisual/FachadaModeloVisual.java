package modelovisual;

import modelo.IConsultaContenedorJuego;
import modelo.IConsultaModelo;
import modelo.IOperacionesModelo;
import modelo.Jugador;
import utilerias.Observador;

import java.awt.Color;
import java.util.List;

public class FachadaModeloVisual
        implements IOperacionesModeloVisual, IConsultaModeloVisual, Observador {

    private final ControlModeloVisual control;

    public FachadaModeloVisual(IConsultaModelo consultaModeloGeneral,
                               IOperacionesModelo operacionesModeloGeneral,
                               IConsultaContenedorJuego contenedorJuego,
                               GestorAlmacenamientoVisual gestorVisual,
                               Jugador jugadorLocal) {
        this.control = new ControlModeloVisual(consultaModeloGeneral, operacionesModeloGeneral,
                contenedorJuego, gestorVisual, jugadorLocal);
    }

    @Override
    public void clic(int x, int y, int anchoPanel, int altoPanel) {
        control.clic(x, y, anchoPanel, altoPanel);
    }

    @Override
    public void mover(int x, int y, int anchoPanel, int altoPanel) {
        control.mover(x, y, anchoPanel, altoPanel);
    }

    @Override
    public void salirDelTablero() {
        control.salirDelTablero();
    }

    @Override
    public void actualizar() {
        control.procesarCambiosDelJuego();
    }

    @Override
    public String getTituloVentana() {
        return control.getTituloVentana();
    }

    @Override
    public String tomarAnuncioTurno() {
        return control.tomarAnuncioTurno();
    }

    @Override
    public Color getColorTurno() {
        return control.getColorTurno();
    }

    @Override
    public boolean hayPuntoBajoMouse() {
        return control.hayPuntoBajoMouse();
    }

    @Override
    public int getTamanoTablero() {
        return control.getTamanoTablero();
    }

    @Override
    public double getTamanoCelda(int anchoPanel, int altoPanel) {
        return control.getTamanoCelda(anchoPanel, altoPanel);
    }

    @Override
    public double getCoordenadaX(int columna, int anchoPanel, int altoPanel) {
        return control.getCoordenadaX(columna, anchoPanel, altoPanel);
    }

    @Override
    public double getCoordenadaY(int fila, int anchoPanel, int altoPanel) {
        return control.getCoordenadaY(fila, anchoPanel, altoPanel);
    }

    @Override
    public ResultadoPartida getResultado() {
        return control.getResultado();
    }

    @Override
    public TarjetaJugador getTarjetaPrincipal() {
        return control.getTarjetaPrincipal();
    }

    @Override
    public List<TarjetaJugador> getTarjetasRivales() {
        return control.getTarjetasRivales();
    }
}
