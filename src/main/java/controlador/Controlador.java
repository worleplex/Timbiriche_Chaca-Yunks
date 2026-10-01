package controlador;

import modelo.Punto;
import modelovisual.IOperacionesModeloVisual;

// El Controlador NO conoce el Modelo General de Juego ni la clase
// concreta del Modelo Visual: solo sabe traducir "el usuario hizo clic en
// este punto" y entregarselo a la interfaz de operaciones del Modelo
// Visual, que es quien decide que significa ese clic.
public class Controlador {

    private IOperacionesModeloVisual modeloVisual;

    public void setModeloVisual(IOperacionesModeloVisual modeloVisual) {
        this.modeloVisual = modeloVisual;
    }

    public void clicEnPunto(Punto punto) {
        modeloVisual.clicEnPunto(punto);
    }
}
