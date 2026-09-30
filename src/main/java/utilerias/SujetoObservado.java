package utilerias;

import java.util.ArrayList;
import java.util.List;

public abstract class SujetoObservado {

    private final List<Observador> observadores;

    public SujetoObservado() {
        observadores = new ArrayList<>();
    }

    public void agregarObservador(Observador observador) {
        observadores.add(observador);
    }

    public void eliminarObservador(Observador observador) {
        observadores.remove(observador);
    }

    public void notificarObservadores() {

        for (Observador observador : observadores) {
            observador.actualizar();
        }
    }
}
