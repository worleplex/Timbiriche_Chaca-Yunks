package modelovisual;

import modelo.Punto;

// Interfaz de operaciones del Modelo Visual: es lo UNICO que el
// Controlador conoce. El Controlador solo avisa "el usuario hizo clic en
// este punto"; que eso signifique seleccionar o jugar lo decide el
// Modelo Visual.
public interface IOperacionesModeloVisual {

    void clicEnPunto(Punto punto);

}
