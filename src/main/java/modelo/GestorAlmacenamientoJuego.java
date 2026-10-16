package modelo;

import utilerias.SujetoObservado;

import java.util.List;

public class GestorAlmacenamientoJuego extends SujetoObservado {

    private final ContenedorJuego contenedor = new ContenedorJuego();

    public IConsultaContenedorJuego getConsultaContenedor() {
        return contenedor;
    }

    void iniciarAccion() {
        contenedor.limpiar();
    }

    void guardarSeleccion(Punto seleccionado, List<Punto> adyacentes) {
        contenedor.registrarSeleccion(seleccionado, adyacentes);
    }

    void guardarLinea(Linea linea) {
        contenedor.registrarLinea(linea);
    }

    void guardarCuadro(Cuadrado cuadro) {
        contenedor.registrarCuadro(cuadro);
    }

    void publicarCambios() {
        if (!contenedor.isVacio()) {
            notificarObservadores();
        }
    }
}
