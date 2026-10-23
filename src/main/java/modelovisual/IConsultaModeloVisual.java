package modelovisual;

import java.util.List;

public interface IConsultaModeloVisual {

    String getTituloVentana();

    boolean hayPuntoBajoMouse();

    List<CoordenadaTablero> getPuntosTablero();

    double getLadoTablero();

    double getSeparacionPuntos();

    double getEscala(int anchoPanel, int altoPanel);

    double getDesplazamientoX(int anchoPanel, int altoPanel);

    double getDesplazamientoY(int anchoPanel, int altoPanel);
}
