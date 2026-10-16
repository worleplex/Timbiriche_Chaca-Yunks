package modelovisual;

import java.util.List;

public interface IConsultaContenedor {

    List<PuntoVisual> getPuntosCambiados();

    List<LineaVisual> getLineasNuevas();

    List<CuadroVisual> getCuadrosNuevos();

    EstadoPunto getEstadoAnterior(int fila, int columna);
}
