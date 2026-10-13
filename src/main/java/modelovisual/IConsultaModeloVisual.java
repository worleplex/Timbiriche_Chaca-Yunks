package modelovisual;

import modelo.IConsultaModelo;
import modelo.Jugador;
import modelo.Punto;

import java.awt.Color;
import java.util.List;

/**
 *
 * @author julian izaguirre
 */
public interface IConsultaModeloVisual extends IConsultaModelo {
    boolean haySeleccion();

    Punto getPuntoSeleccionado();

    List<Punto> getPuntosResaltados();

    Color getColor(Jugador jugador);

}