package modelovisual;

import modelo.IConsultaModelo;
import modelo.IOperacionesModelo;
import modelo.Jugador;
import utilerias.Observador;
import utilerias.SujetoObservado;

import java.awt.Color;
import java.util.List;

public class FachadaModeloVisual extends SujetoObservado
        implements IOperacionesModeloVisual, IConsultaModeloVisual, Observador {

    private final ControlModeloVisual control;

    public FachadaModeloVisual(IConsultaModelo consultaModeloGeneral,
                        IOperacionesModelo operacionesModeloGeneral,
                        Jugador jugadorLocal) {
        this.control = new ControlModeloVisual(consultaModeloGeneral, operacionesModeloGeneral, jugadorLocal);
    }

    @Override
    public void clic(int x, int y, int anchoPanel, int altoPanel) {

        boolean cambioVisual = control.clic(x, y, anchoPanel, altoPanel);

        if (cambioVisual) {
            notificarObservadores();
        }
    }

    @Override
    public void mover(int x, int y, int anchoPanel, int altoPanel) {

        boolean cambioVisual = control.mover(x, y, anchoPanel, altoPanel);

        if (cambioVisual) {
            notificarObservadores();
        }
    }

    @Override
    public void salirDelTablero() {

        boolean cambioVisual = control.salirDelTablero();

        if (cambioVisual) {
            notificarObservadores();
        }
    }

    @Override
    public void actualizar() {
        control.limpiarSeleccion();
        notificarObservadores();
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
    public double getTamanoCelda(int anchoPanel, int altoPanel) {
        return control.getTamanoCelda(anchoPanel, altoPanel);
    }

    @Override
    public List<CuadroVisual> getCuadros(int anchoPanel, int altoPanel) {
        return control.getCuadros(anchoPanel, altoPanel);
    }

    @Override
    public List<LineaVisual> getLineas(int anchoPanel, int altoPanel) {
        return control.getLineas(anchoPanel, altoPanel);
    }

    @Override
    public List<PuntoVisual> getPuntos(int anchoPanel, int altoPanel) {
        return control.getPuntos(anchoPanel, altoPanel);
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
