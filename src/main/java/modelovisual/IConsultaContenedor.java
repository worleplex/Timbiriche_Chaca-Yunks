package modelovisual;

import java.awt.Color;
import java.util.List;

public interface IConsultaContenedor {

    List<PuntoVisual> getPuntosCambiados();

    List<LineaVisual> getLineasNuevas();

    List<CuadroVisual> getCuadrosNuevos();

    EstadoPunto getEstadoAnterior(double x, double y);

    TarjetaJugador getTarjetaPrincipal();

    List<TarjetaJugador> getTarjetasRivales();

    Color getColorTurno();

    ResultadoPartida getResultado();
}
