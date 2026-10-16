package modelo;

import java.util.List;

public interface IConsultaContenedorJuego {

    boolean hayCambioSeleccion();

    Punto getPuntoSeleccionado();

    List<Punto> getPuntosAdyacentes();

    List<Linea> getLineasNuevas();

    List<Cuadrado> getCuadrosNuevos();
}
