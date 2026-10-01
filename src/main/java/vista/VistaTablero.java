package vista;

import controlador.Controlador;
import modelo.Cuadrado;
import modelo.Jugador;
import modelo.Linea;
import modelo.Punto;
import modelovisual.IConsultaModeloVisual;
import utilerias.Observador;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

// La Vista solo conoce al Modelo Visual (a traves de IConsultaModeloVisual)
// y al Controlador. Se suscribe como observador del Modelo Visual, que es
// quien decide cuando la Vista debe redibujar, ya sea porque el juego
// avanzo o porque cambio algo puramente de interfaz (una seleccion).
public class VistaTablero extends JFrame implements Observador {

    // El area que ocupa el tablero en pantalla es SIEMPRE la misma,
    // sin importar cuantos puntos tenga (el tamano de cada celda se
    // calcula a partir de ella).
    private static final int AREA_TABLERO = 480;
    private static final int MARGEN = 50;
    private static final int RADIO_PUNTO_MAXIMO = 6;
    private static final int TOLERANCIA_CLIC_MAXIMA = 16;

    private Controlador controlador;
    private IConsultaModeloVisual modeloVisual;

    private final PanelTablero panelTablero;
    private final JLabel lblEstado;

    public VistaTablero() {

        setTitle("Timbiriche");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panelTablero = new PanelTablero();

        lblEstado = new JLabel("", SwingConstants.CENTER);
        lblEstado.setFont(lblEstado.getFont().deriveFont(Font.BOLD, 15f));
        lblEstado.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        setLayout(new BorderLayout());
        add(lblEstado, BorderLayout.NORTH);
        add(panelTablero, BorderLayout.CENTER);
    }

    // ------------------------------------------
    // Asociaciones
    // ------------------------------------------

    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    public void setModeloVisual(IConsultaModeloVisual modeloVisual) {

        this.modeloVisual = modeloVisual;

        int lado = MARGEN * 2 + AREA_TABLERO;
        panelTablero.setPreferredSize(new Dimension(lado, lado));
        pack();
        setLocationRelativeTo(null); // despues de pack(), para que si quede centrada
    }

    // ------------------------------------------
    // Observer (notificado por el Modelo Visual)
    // ------------------------------------------

    @Override
    public void actualizar() {
        actualizarEtiquetaEstado();
        panelTablero.repaint();
    }

    private void actualizarEtiquetaEstado() {

        if (modeloVisual.isJuegoTerminado()) {
            lblEstado.setForeground(Color.BLACK);
            lblEstado.setText(textoResultado());
            return;
        }

        Jugador actual = modeloVisual.getJugadorActual();

        StringBuilder texto = new StringBuilder("Turno de ");
        texto.append(actual.getNombre());
        texto.append("     ");

        for (Jugador jugador : modeloVisual.getJugadores()) {
            texto.append(jugador.getNombre())
                    .append(": ")
                    .append(jugador.getPuntaje())
                    .append("     ");
        }

        lblEstado.setForeground(actual.getColor());
        lblEstado.setText(texto.toString());
    }

    private String textoResultado() {

        List<Jugador> ganadores = modeloVisual.getGanadores();
        int cuadros = ganadores.get(0).getPuntaje();

        if (ganadores.size() == 1) {
            return "Juego terminado. Gana " + ganadores.get(0).getNombre()
                    + " (" + cuadros + " cuadros)";
        }

        StringBuilder nombres = new StringBuilder();
        for (int i = 0; i < ganadores.size(); i++) {
            if (i > 0) {
                nombres.append(", ");
            }
            nombres.append(ganadores.get(i).getNombre());
        }

        return "Juego terminado. Empate entre " + nombres + " (" + cuadros + " cuadros c/u)";
    }

    // ------------------------------------------
    // Coordenadas de pantalla
    // ------------------------------------------

    // Separacion en pixeles entre dos puntos vecinos.
    private double tamanoCelda() {
        return (double) AREA_TABLERO / (modeloVisual.getTamano() - 1);
    }

    private int coordenadaX(int columna) {
        return MARGEN + (int) Math.round(columna * tamanoCelda());
    }

    private int coordenadaY(int fila) {
        return MARGEN + (int) Math.round(fila * tamanoCelda());
    }

    // ------------------------------------------
    // Panel de dibujo
    // ------------------------------------------

    private class PanelTablero extends JPanel {

        PanelTablero() {

            setBackground(Color.WHITE);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    manejarClic(e.getX(), e.getY());
                }
            });
        }

        // Se busca el PUNTO mas cercano al clic (no la arista mas
        // cercana): el flujo es clic en un punto, clic en otro punto.
        private void manejarClic(int x, int y) {

            if (modeloVisual == null || controlador == null || modeloVisual.isJuegoTerminado()) {
                return;
            }

            Punto masCercano = puntoMasCercano(x, y);

            if (masCercano != null) {
                controlador.clicEnPunto(masCercano);
            }
        }

        private Punto puntoMasCercano(int x, int y) {

            int tamano = modeloVisual.getTamano();
            Punto mejor = null;
            double mejorDistancia = Math.min(TOLERANCIA_CLIC_MAXIMA, tamanoCelda() / 2);

            for (int fila = 0; fila < tamano; fila++) {
                for (int columna = 0; columna < tamano; columna++) {

                    double dx = x - coordenadaX(columna);
                    double dy = y - coordenadaY(fila);
                    double distancia = Math.sqrt(dx * dx + dy * dy);

                    if (distancia < mejorDistancia) {
                        mejorDistancia = distancia;
                        mejor = new Punto(fila, columna);
                    }
                }
            }

            return mejor;
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            if (modeloVisual == null) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            dibujarCuadrosCompletados(g2);
            dibujarLineas(g2);
            dibujarPuntos(g2);
        }

        private void dibujarCuadrosCompletados(Graphics2D g2) {

            for (Cuadrado cuadrado : modeloVisual.getCuadros()) {

                int fila = cuadrado.getEsquinaSupIzq().getFila();
                int columna = cuadrado.getEsquinaSupIzq().getColumna();

                int x = coordenadaX(columna);
                int y = coordenadaY(fila);
                int ancho = coordenadaX(columna + 1) - x;
                int alto = coordenadaY(fila + 1) - y;

                g2.setColor(colorSuave(cuadrado.getJugador().getColor()));
                g2.fillRect(x, y, ancho, alto);
            }
        }

        private void dibujarLineas(Graphics2D g2) {

            float grosor = (float) Math.max(2, Math.min(4, tamanoCelda() / 10));
            g2.setStroke(new BasicStroke(grosor));

            for (Linea linea : modeloVisual.getLineas()) {

                g2.setColor(linea.getJugador().getColor());

                g2.drawLine(
                        coordenadaX(linea.getPuntoA().getColumna()), coordenadaY(linea.getPuntoA().getFila()),
                        coordenadaX(linea.getPuntoB().getColumna()), coordenadaY(linea.getPuntoB().getFila()));
            }
        }

        // Pinta los puntos normales en gris, el punto seleccionado en
        // naranja (mas grande) y sus vecinos validos en verde -- igual
        // que "aparecen los puntos remarcados" del bosquejo.
        private void dibujarPuntos(Graphics2D g2) {

            int tamano = modeloVisual.getTamano();
            Punto seleccionado = modeloVisual.getPuntoSeleccionado();
            List<Punto> resaltados = modeloVisual.getPuntosResaltados();

            int radioPunto = (int) Math.max(2, Math.min(RADIO_PUNTO_MAXIMO, tamanoCelda() / 8));

            for (int fila = 0; fila < tamano; fila++) {
                for (int columna = 0; columna < tamano; columna++) {

                    Punto actual = new Punto(fila, columna);
                    int x = coordenadaX(columna);
                    int y = coordenadaY(fila);

                    if (actual.equals(seleccionado)) {
                        g2.setColor(Color.ORANGE);
                        int radio = radioPunto + 2;
                        g2.fillOval(x - radio, y - radio, radio * 2, radio * 2);
                    } else if (resaltados.contains(actual)) {
                        g2.setColor(new Color(60, 180, 60));
                        g2.fillOval(x - radioPunto, y - radioPunto, radioPunto * 2, radioPunto * 2);
                    } else {
                        g2.setColor(Color.DARK_GRAY);
                        g2.fillOval(x - radioPunto, y - radioPunto, radioPunto * 2, radioPunto * 2);
                    }
                }
            }
        }

        private Color colorSuave(Color color) {
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), 90);
        }
    }
}
