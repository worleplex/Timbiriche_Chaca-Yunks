package modelovisual;

import java.awt.Color;
import java.util.List;

public interface IConsultaModeloVisual {

    String getTituloVentana();

    String tomarAnuncioTurno();

    Color getColorTurno();

    boolean hayPuntoBajoMouse();

    int getTamanoTablero();

    double getTamanoCelda(int anchoPanel, int altoPanel);

    double getCoordenadaX(int columna, int anchoPanel, int altoPanel);

    double getCoordenadaY(int fila, int anchoPanel, int altoPanel);

    ResultadoPartida getResultado();

    TarjetaJugador getTarjetaPrincipal();

    List<TarjetaJugador> getTarjetasRivales();
}
