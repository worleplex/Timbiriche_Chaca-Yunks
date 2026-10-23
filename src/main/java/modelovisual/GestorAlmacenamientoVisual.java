package modelovisual;

import utilerias.SujetoObservado;

import java.awt.Color;
import java.util.List;

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

    void guardarEstadoJugadores(TarjetaJugador principal, List<TarjetaJugador> rivales,
                                Color colorTurno, ResultadoPartida resultado) {
        contenedor.registrarEstadoJugadores(principal, rivales, colorTurno, resultado);
    }

    boolean hayCambios() {
        return !contenedor.isVacio();
    }

    void notificarCambios() {
        notificarObservadores();
    }
}
