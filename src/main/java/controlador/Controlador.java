package controlador;

import modelo.Punto;
import modelovisual.ModeloVisual;

// El Controlador ya NO conoce el Modelo General de Juego. Solo sabe
// traducir "el usuario hizo clic en este punto" y entregarselo al
// Modelo Visual, que es quien decide que significa ese clic.
public class Controlador {

    private ModeloVisual modeloVisual;

    public void setModeloVisual(ModeloVisual modeloVisual) {
        this.modeloVisual = modeloVisual;
    }

    public void clicEnPunto(Punto punto) {
        modeloVisual.clicEnPunto(punto);
    }
}
