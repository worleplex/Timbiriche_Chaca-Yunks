package modelovisual;

import modelo.IConsultaModelo;
import modelo.Punto;

import java.util.List;

// Interfaz "get()" del Modelo Visual: es lo UNICO que la Vista conoce.
// Segun el bosquejo del profe, la Vista le pide todo al Modelo Visual
// (ya no le pide nada directo al Modelo General de Juego).
//
// Extiende IConsultaModelo porque la Vista necesita los datos del juego
// (lineas, cuadros, jugadores, turno...) ademas de los datos puramente
// visuales; el Modelo Visual los obtiene del Modelo General y se los
// entrega a la Vista.
public interface IConsultaModeloVisual extends IConsultaModelo {

    // Datos visuales: no existen en el Modelo General de Juego.

    boolean haySeleccion();

    Punto getPuntoSeleccionado();

    // Vecinos del punto seleccionado que todavia se pueden conectar.
    List<Punto> getPuntosResaltados();

}
