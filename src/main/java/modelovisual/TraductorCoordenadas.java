package modelovisual;

final class TraductorCoordenadas {

    static final double LADO = 1000;
    private static final double MARGEN = LADO * 0.045;

    private final int tamano;
    private final double separacion;

    TraductorCoordenadas(int tamano) {
        this.tamano = tamano;
        this.separacion = (LADO - 2 * MARGEN) / (tamano - 1);
    }

    double getSeparacion() {
        return separacion;
    }

    CoordenadaTablero aTablero(int fila, int columna) {
        return new CoordenadaTablero(MARGEN + columna * separacion, MARGEN + fila * separacion);
    }

    int aFila(CoordenadaTablero coordenada) {
        return (int) Math.round((coordenada.getY() - MARGEN) / separacion);
    }

    int aColumna(CoordenadaTablero coordenada) {
        return (int) Math.round((coordenada.getX() - MARGEN) / separacion);
    }

    java.util.List<CoordenadaTablero> todosLosPuntos() {
        java.util.List<CoordenadaTablero> lista = new java.util.ArrayList<>();
        for (int f = 0; f < tamano; f++) {
            for (int c = 0; c < tamano; c++) {
                lista.add(aTablero(f, c));
            }
        }
        return lista;
    }
}
