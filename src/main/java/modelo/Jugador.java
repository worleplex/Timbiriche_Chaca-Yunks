package modelo;

/**
 *
 * @author julian izaguirre
 */
public class Jugador {

    private final String nombre;
    private int puntaje;

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.puntaje = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntaje() {
        return puntaje;
    }

    void sumarPunto() {
        puntaje++;
    }
}