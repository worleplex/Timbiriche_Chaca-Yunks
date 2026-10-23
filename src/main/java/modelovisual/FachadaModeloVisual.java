package modelovisual;

import java.util.List;

public class FachadaModeloVisual
        implements IOperacionesModeloVisual, IConsultaModeloVisual {

    private final ControlModeloVisual control;

    public FachadaModeloVisual(ControlModeloVisual control) {
        this.control = control;
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
    public String getTituloVentana() {
        return control.getTituloVentana();
    }

    @Override
    public boolean hayPuntoBajoMouse() {
        return control.hayPuntoBajoMouse();
    }

    @Override
    public List<CoordenadaTablero> getPuntosTablero() {
        return control.getPuntosTablero();
    }

    @Override
    public double getLadoTablero() {
        return control.getLadoTablero();
    }

    @Override
    public double getSeparacionPuntos() {
        return control.getSeparacionPuntos();
    }

    @Override
    public double getEscala(int anchoPanel, int altoPanel) {
        return control.getEscala(anchoPanel, altoPanel);
    }

    @Override
    public double getDesplazamientoX(int anchoPanel, int altoPanel) {
        return control.getDesplazamientoX(anchoPanel, altoPanel);
    }

    @Override
    public double getDesplazamientoY(int anchoPanel, int altoPanel) {
        return control.getDesplazamientoY(anchoPanel, altoPanel);
    }
}
