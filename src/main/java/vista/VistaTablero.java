package vista;

import controlador.Controlador;
import modelo.Cuadrado;
import modelo.IConsultaModelo;
import modelo.Jugador;
import modelo.Linea;
import modelo.Punto;
import modelovisual.ModeloVisual;
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

// La Vista ahora solo se suscribe al Modelo Visual (no directamente al
// Modelo General de Juego). El Modelo Visual es quien decide cuando la
// Vista debe redibujar, ya sea porque el juego avanzo o porque cambio
// algo puramente de interfaz (una seleccion).
public class VistaTablero extends JFrame implements Observador {

    private static final int MARGEN = 50;
    private static final int TAMANO_CELDA = 80;
    private static final int RADIO_PUNTO = 6;
    private static final int TOLERANCIA_CLIC = 16;

    private Controlador controlador;
    private IConsultaModelo modelo;
    private ModeloVisual modeloVisual;

    private final PanelTablero panelTablero;
    private final JLabel lblEstado;

    public VistaTablero() {

        setTitle("Timbiriche");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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

    public void setModelo(IConsultaModelo modelo) {

        this.modelo = modelo;

        int tamano = modelo.getTamano();
        int lado = MARGEN * 2 + TAMANO_CELDA * (tamano - 1);
        panelTablero.setPreferredSize(new Dimension(lado, lado));
        pack();
    }

    public void setModeloVisual(ModeloVisual modeloVisual) {
        this.modeloVisual = modeloVisual;
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

        if (modelo.isJuegoTerminado()) {
            lblEstado.setText("Juego terminado. Gana " + textoGanador());
            return;
        }

        StringBuilder texto = new StringBuilder("Turno de ");
        texto.append(modelo.getJugadorActual().getNombre());
        texto.append("     ");

        for (Jugador jugador : modelo.getJugadores()) {
            texto.append(jugador.getNombre())
                    .append(": ")
                    .append(jugador.getPuntaje())
                    .append("     ");
        }

        lblEstado.setText(texto.toString());
    }

    private String textoGanador() {

        Jugador ganador = modelo.getJugadores().get(0);

        for (Jugador jugador : modelo.getJugadores()) {
            if (jugador.getPuntaje() > ganador.getPuntaje()) {
                ganador = jugador;
            }
        }

        return ganador.getNombre() + " (" + ganador.getPuntaje() + " cuadros)";
    }

    // ------------------------------------------
    // Coordenadas de pantalla
    // ------------------------------------------

    private int coordenadaX(int columna) {
        return MARGEN + columna * TAMANO_CELDA;
    }

    private int coordenadaY(int fila) {
        return MARGEN + fila * TAMANO_CELDA;
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

        // Ahora se busca el PUNTO mas cercano al clic (no la arista mas
        // cercana): el flujo es clic en un punto, clic en otro punto.
        private void manejarClic(int x, int y) {

            if (modelo == null || controlador == null || modelo.isJuegoTerminado()) {
                return;
            }

            Punto masCercano = puntoMasCercano(x, y);

            if (masCercano != null) {
                controlador.clicEnPunto(masCercano);
            }
        }

        private Punto puntoMasCercano(int x, int y) {

            int tamano = modelo.getTamano();
            Punto mejor = null;
            double mejorDistancia = TOLERANCIA_CLIC;

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

            if (modelo == null) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            dibujarCuadrosCompletados(g2);
            dibujarLineas(g2);
            dibujarPuntos(g2);
        }

        private void dibujarCuadrosCompletados(Graphics2D g2) {

            for (Cuadrado cuadrado : modelo.getCuadros()) {

                int fila = cuadrado.getEsquinaSupIzq().getFila();
                int columna = cuadrado.getEsquinaSupIzq().getColumna();

                g2.setColor(colorSuave(cuadrado.getJugador().getColor()));
                g2.fillRect(coordenadaX(columna), coordenadaY(fila), TAMANO_CELDA, TAMANO_CELDA);
            }
        }

        private void dibujarLineas(Graphics2D g2) {

            g2.setStroke(new BasicStroke(4));

            for (Linea linea : modelo.getLineas()) {

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

            int tamano = modelo.getTamano();
            Punto seleccionado = modeloVisual.getPuntoSeleccionado();
            List<Punto> resaltados = modeloVisual.getPuntosResaltados();

            for (int fila = 0; fila < tamano; fila++) {
                for (int columna = 0; columna < tamano; columna++) {

                    Punto actual = new Punto(fila, columna);
                    int x = coordenadaX(columna);
                    int y = coordenadaY(fila);

                    if (actual.equals(seleccionado)) {
                        g2.setColor(Color.ORANGE);
                        int radio = RADIO_PUNTO + 2;
                        g2.fillOval(x - radio, y - radio, radio * 2, radio * 2);
                    } else if (resaltados.contains(actual)) {
                        g2.setColor(new Color(60, 180, 60));
                        g2.fillOval(x - RADIO_PUNTO, y - RADIO_PUNTO, RADIO_PUNTO * 2, RADIO_PUNTO * 2);
                    } else {
                        g2.setColor(Color.DARK_GRAY);
                        g2.fillOval(x - RADIO_PUNTO, y - RADIO_PUNTO, RADIO_PUNTO * 2, RADIO_PUNTO * 2);
                    }
                }
            }
        }

        private Color colorSuave(Color color) {
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), 90);
        }
    }
}
