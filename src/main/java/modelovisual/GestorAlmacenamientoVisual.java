package modelovisual;

import utilerias.SujetoObservado;

public class GestorAlmacenamientoVisual extends SujetoObservado {

    private final ContenedorVisual contenedor = new ContenedorVisual();

    public IConsultaContenedor getConsultaContenedor() {
        return contenedor;
    }

    void iniciarCambios() {
        contenedor.limpiar();
    }

    void guardarPunto(PuntoVisual punto) {
        contenedor.agregarPunto(punto);
    }

    void guardarLinea(LineaVisual linea) {
        contenedor.agregarLinea(linea);
    }

    void guardarCuadro(CuadroVisual cuadro) {
        contenedor.agregarCuadro(cuadro);
    }

    boolean hayCambios() {
        return !contenedor.isVacio();
    }

    void notificarCambios() {
        notificarObservadores();
    }
}
