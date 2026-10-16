package modelovisual;

import java.awt.Color;
import java.util.List;

public interface IConsultaModeloVisual {

    String getTituloVentana();

    String tomarAnuncioTurno();

    Color getColorTurno();

    boolean hayPuntoBajoMouse();

    List<CoordenadaTablero> getPuntosTablero();

    double getLadoTablero();

    double getSeparacionPuntos();

    double getEscala(int anchoPanel, int altoPanel);

    double getDesplazamientoX(int anchoPanel, int altoPanel);

    double getDesplazamientoY(int anchoPanel, int altoPanel);

    ResultadoPartida getResultado();

    TarjetaJugador getTarjetaPrincipal();

    List<TarjetaJugador> getTarjetasRivales();
}
