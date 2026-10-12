package modelovisual;

import modelo.IConsultaModelo;
import modelo.Punto;

import java.util.List;

/**
 *
 * @author julian izaguirre
 */
public interface IConsultaModeloVisual extends IConsultaModelo {

    // Datos visuales: no existen en el Modelo General de Juego.

    boolean haySeleccion();

    Punto getPuntoSeleccionado();

    // Vecinos del punto seleccionado que todavia se pueden conectar.
    List<Punto> getPuntosResaltados();

}
