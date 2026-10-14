package controlador;

import modelovisual.IOperacionesModeloVisual;

public class Controlador {

    private IOperacionesModeloVisual modeloVisual;

    public void setModeloVisual(IOperacionesModeloVisual modeloVisual) {
        this.modeloVisual = modeloVisual;
    }

    public void clic(int x, int y, int anchoPanel, int altoPanel) {
        modeloVisual.clic(x, y, anchoPanel, altoPanel);
    }

    public void mover(int x, int y, int anchoPanel, int altoPanel) {
        modeloVisual.mover(x, y, anchoPanel, altoPanel);
    }

    public void salirDelTablero() {
        modeloVisual.salirDelTablero();
    }
}
