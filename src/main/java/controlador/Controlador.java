package controlador;

import modelo.Punto;
import modelovisual.IOperacionesModeloVisual;

/**
 *
 * @author julian izaguirre
 */
public class Controlador {

    private IOperacionesModeloVisual modeloVisual;

    public void setModeloVisual(IOperacionesModeloVisual modeloVisual) {
        this.modeloVisual = modeloVisual;
    }

    public void clicEnPunto(Punto punto) {
        modeloVisual.clicEnPunto(punto);
    }
}
