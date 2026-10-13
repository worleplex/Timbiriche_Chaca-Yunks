package vista;

import controlador.Controlador;
import modelo.Cuadrado;
import modelo.Jugador;
import modelo.Linea;
import modelo.Punto;
import modelovisual.IConsultaModeloVisual;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * este dibuja el tablero y recibe los clics osea se adapta al tamaño
 * disponible y como solo dibuja lo que le dice el modeloVisual y avisa
 * los clics al controlador y yap
 * @author julian izaguirre
 */
class PanelTablero extends JPanel {

    private IConsultaModeloVisual modeloVisual;
    private Controlador controlador;
    private Punto puntoBajoMouse; // solo para resaltar el punto sobre el que esta el cursor

    PanelTablero() {

        setOpaque(false);

        MouseAdapter raton = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (modeloVisual == null || controlador == null || modeloVisual.isJuegoTerminado()) {
                    return;
                }
                Punto punto = puntoMasCercano(e.getX(), e.getY());
                if (punto != null) {
                    controlador.clicEnPunto(punto);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (modeloVisual == null) {
                    return;
                }
                Punto nuevo = modeloVisual.isJuegoTerminado() ? null : puntoMasCercano(e.getX(), e.getY());
                if (nuevo == null ? puntoBajoMouse != null : !nuevo.equals(puntoBajoMouse)) {
                    puntoBajoMouse = nuevo;
                    setCursor(Cursor.getPredefinedCursor(nuevo == null ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (puntoBajoMouse != null) {
                    puntoBajoMouse = null;
                    repaint();
                }
            }
        };
        addMouseListener(raton);
        addMouseMotionListener(raton);
    }

    void setModeloVisual(IConsultaModeloVisual modeloVisual) {
        this.modeloVisual = modeloVisual;
    }

    void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    // ------------------------------------------
    // Geometria (se recalcula con el tamano actual del panel)
    // ------------------------------------------

    private double lado() {
        double margen = Math.min(getWidth(), getHeight()) * 0.045;
        return Math.min(getWidth(), getHeight()) - 2 * margen;
    }

    private double origenX() {
        return (getWidth() - lado()) / 2;
    }

    private double origenY() {
        return (getHeight() - lado()) / 2;
    }

    // Separacion en pixeles entre dos puntos vecinos.
    private double celda() {
        return lado() / (modeloVisual.getTamano() - 1);
    }

    private double coordenadaX(int columna) {
        return origenX() + columna * celda();
    }

    private double coordenadaY(int fila) {
        return origenY() + fila * celda();
    }

    // Se busca el PUNTO mas cercano al cursor (el flujo es clic en un punto,
    // clic en otro punto).
    private Punto puntoMasCercano(int x, int y) {

        int tamano = modeloVisual.getTamano();
        double tolerancia = Math.min(celda() / 2, 22);
        double mejorDistancia = tolerancia;
        Punto mejor = null;

        for (int fila = 0; fila < tamano; fila++) {
            for (int columna = 0; columna < tamano; columna++) {

                double distancia = Math.hypot(x - coordenadaX(columna), y - coordenadaY(fila));

                if (distancia < mejorDistancia) {
                    mejorDistancia = distancia;
                    mejor = new Punto(fila, columna);
                }
            }
        }

        return mejor;
    }

    // ------------------------------------------
    // Dibujo
    // ------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        Tema.suavizar(g2);

        double arco = Math.min(getWidth(), getHeight()) * 0.05;
        g2.setColor(Tema.PANEL);
        g2.fill(Tema.redondeado(0, 0, getWidth(), getHeight(), arco));

        if (modeloVisual != null) {
            dibujarCuadrosCompletados(g2);
            dibujarLineas(g2);
            dibujarPuntos(g2);

            if (modeloVisual.isJuegoTerminado()) {
                dibujarResultado(g2, arco);
            }
        }

        g2.dispose();
    }

    private void dibujarCuadrosCompletados(Graphics2D g2) {

        for (Cuadrado cuadrado : modeloVisual.getCuadros()) {

            Punto esquina = cuadrado.getEsquinaSupIzq();
            double x = coordenadaX(esquina.getColumna());
            double y = coordenadaY(esquina.getFila());

            g2.setColor(Tema.conAlfa(modeloVisual.getColor(cuadrado.getJugador()), 115));
            g2.fill(new java.awt.geom.Rectangle2D.Double(x, y, celda(), celda()));
        }
    }

    private void dibujarLineas(Graphics2D g2) {

        float grosor = (float) Math.max(2, Math.min(7, celda() * 0.14));
        g2.setStroke(new BasicStroke(grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (Linea linea : modeloVisual.getLineas()) {

            g2.setColor(modeloVisual.getColor(linea.getJugador()));
            g2.draw(new java.awt.geom.Line2D.Double(
                    coordenadaX(linea.getPuntoA().getColumna()), coordenadaY(linea.getPuntoA().getFila()),
                    coordenadaX(linea.getPuntoB().getColumna()), coordenadaY(linea.getPuntoB().getFila())));
        }
    }

    // Puntos normales en lavanda; el punto bajo el cursor crece un poco; el
    // seleccionado lleva un aro blanco y sus vecinos validos se resaltan con
    // el color del jugador en turno (los "puntos remarcados").
    private void dibujarPuntos(Graphics2D g2) {

        int tamano = modeloVisual.getTamano();
        Punto seleccionado = modeloVisual.getPuntoSeleccionado();
        List<Punto> resaltados = modeloVisual.getPuntosResaltados();
        Color colorTurno = modeloVisual.isJuegoTerminado()
                ? Tema.PUNTO : modeloVisual.getColor(modeloVisual.getJugadorActual());

        double radio = Math.max(1.6, Math.min(4.5, celda() * 0.1));

        for (int fila = 0; fila < tamano; fila++) {
            for (int columna = 0; columna < tamano; columna++) {

                Punto actual = new Punto(fila, columna);
                double x = coordenadaX(columna);
                double y = coordenadaY(fila);

                if (actual.equals(seleccionado)) {
                    circulo(g2, x, y, radio * 3.1, Tema.conAlfa(colorTurno, 80));
                    circulo(g2, x, y, radio * 2.3, Tema.TEXTO);
                    circulo(g2, x, y, radio * 1.6, colorTurno);
                } else if (resaltados.contains(actual)) {
                    circulo(g2, x, y, radio * 3.0, Tema.conAlfa(colorTurno, 70));
                    circulo(g2, x, y, radio * 1.9, colorTurno);
                } else if (actual.equals(puntoBajoMouse)) {
                    circulo(g2, x, y, radio * 1.9, Tema.TEXTO);
                } else {
                    circulo(g2, x, y, radio, Tema.PUNTO);
                }
            }
        }
    }

    private void circulo(Graphics2D g2, double x, double y, double radio, Color color) {
        g2.setColor(color);
        g2.fill(new Ellipse2D.Double(x - radio, y - radio, radio * 2, radio * 2));
    }

    // Fin del juego: tabla de posiciones con los puntajes (el PDF la pide
    // sin importar como termine la partida).
    private void dibujarResultado(Graphics2D g2, double arcoTablero) {

        int w = getWidth();
        int h = getHeight();
        double u = Math.min(w, h) / 600.0; // unidad de escala

        g2.setColor(new Color(0, 0, 0, 175));
        g2.fill(Tema.redondeado(0, 0, w, h, arcoTablero));

        List<Jugador> ganadores = modeloVisual.getGanadores();
        List<Jugador> posiciones = new ArrayList<>(modeloVisual.getJugadores());
        posiciones.sort(Comparator.comparingInt(Jugador::getPuntaje).reversed());

        double anchoCaja = 400 * u;
        double altoCaja = (150 + 40 * posiciones.size()) * u;
        double x = (w - anchoCaja) / 2;
        double y = (h - altoCaja) / 2;

        g2.setColor(Tema.PANEL);
        g2.fill(Tema.redondeado(x, y, anchoCaja, altoCaja, 26 * u));
        g2.setColor(Tema.BORDE_AVATAR);
        g2.setStroke(new BasicStroke((float) (2 * u)));
        g2.draw(Tema.redondeado(x, y, anchoCaja, altoCaja, 26 * u));

        centrado(g2, "¡FIN DEL JUEGO!", Tema.fuente(java.awt.Font.BOLD, 30 * u), Tema.TEXTO, w / 2.0, y + 52 * u);

        String subtitulo = ganadores.size() == 1
                ? "Gana " + ganadores.get(0).getNombre()
                : "Empate entre " + nombres(ganadores);
        Color colorSub = ganadores.size() == 1 ? modeloVisual.getColor(ganadores.get(0)) : Tema.PUNTO;
        centrado(g2, subtitulo, Tema.fuente(java.awt.Font.BOLD, 18 * u), colorSub, w / 2.0, y + 86 * u);

        double fila = y + 128 * u;
        for (Jugador jugador : posiciones) {

            int posicion = 1;
            for (Jugador otro : posiciones) {
                if (otro.getPuntaje() > jugador.getPuntaje()) {
                    posicion++;
                }
            }

            Color color = modeloVisual.getColor(jugador);
            double izquierda = x + 36 * u;

            g2.setColor(Tema.conAlfa(color, 255));
            g2.fill(Tema.redondeado(izquierda, fila - 20 * u, 8 * u, 26 * u, 6 * u));

            g2.setFont(Tema.fuente(java.awt.Font.BOLD, 18 * u));
            g2.setColor(Tema.TEXTO);
            g2.drawString(posicion + ".", (float) (izquierda + 20 * u), (float) fila);
            g2.setFont(Tema.fuente(java.awt.Font.PLAIN, 18 * u));
            g2.drawString(jugador.getNombre(), (float) (izquierda + 52 * u), (float) fila);

            String puntos = jugador.getPuntaje() + (jugador.getPuntaje() == 1 ? " punto" : " puntos");
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(puntos, (float) (x + anchoCaja - 36 * u - fm.stringWidth(puntos)), (float) fila);

            fila += 40 * u;
        }
    }

    private String nombres(List<Jugador> jugadores) {

        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < jugadores.size(); i++) {
            if (i > 0) {
                texto.append(", ");
            }
            texto.append(jugadores.get(i).getNombre());
        }
        return texto.toString();
    }

    private void centrado(Graphics2D g2, String texto, java.awt.Font fuente, Color color, double centroX, double base) {
        g2.setFont(fuente);
        g2.setColor(color);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, (float) (centroX - fm.stringWidth(texto) / 2.0), (float) base);
    }
}

