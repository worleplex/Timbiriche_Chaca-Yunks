package modelovisual;

import java.awt.Color;
import java.util.List;

public interface IConsultaModeloVisual {

    String getTituloVentana();

    String tomarAnuncioTurno();

    Color getColorTurno();

    boolean hayPuntoBajoMouse();

    double getTamanoCelda(int anchoPanel, int altoPanel);

    List<CuadroVisual> getCuadros(int anchoPanel, int altoPanel);

    List<LineaVisual> getLineas(int anchoPanel, int altoPanel);

    List<PuntoVisual> getPuntos(int anchoPanel, int altoPanel);

    ResultadoPartida getResultado();

    TarjetaJugador getTarjetaPrincipal();

    List<TarjetaJugador> getTarjetasRivales();
}
