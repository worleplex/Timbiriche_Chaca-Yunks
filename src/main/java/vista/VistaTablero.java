package vista;

import controlador.Controlador;
import modelo.Jugador;
import modelovisual.IConsultaModeloVisual;
import utilerias.Observador;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;

/**
 * es la venta principal a la izquierda el tablero, a la derecha la tarjeta del jugador en turno, 
 * los demas jugadores y los botones de accion, la ventana se puede redimensionar y todo se adapta
 * observa al Modelo Visual y se redibuja cuando este avisa que algo cambio y manda los clics 
 * al Controlador
 *
 * @author julian izaguirre
 */
public class VistaTablero extends JFrame implements Observador {

    private Controlador controlador;
    private IConsultaModeloVisual modeloVisual;
    private Jugador jugadorAsignado;

    private final PanelTablero panelTablero = new PanelTablero();
    private final PanelLateral panelLateral = new PanelLateral();
    private final CartelTurno cartelTurno = new CartelTurno();

    // Ultimo jugador al que se le anuncio el turno
    private Jugador ultimoAnunciado;
    
    public VistaTablero() {
        this(1);
    }

    public VistaTablero(int numeroVista) {
        setTitle("Timbiriche - Vista " + numeroVista);
        // Cerrar una ventana no debe cerrar las demás vistas del mismo juego
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contenido = new JPanel(new GridBagLayout());
        contenido.setBackground(Tema.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setContentPane(contenido);

        // Tablero ~62% del ancho, barra lateral ~38%
        GridBagConstraints tablero = new GridBagConstraints();
        tablero.gridx = 0;
        tablero.weightx = 0.62;
        tablero.weighty = 1;
        tablero.fill = GridBagConstraints.BOTH;
        tablero.insets = new Insets(0, 0, 0, 14);
        contenido.add(panelTablero, tablero);

        GridBagConstraints lateral = new GridBagConstraints();
        lateral.gridx = 1;
        lateral.weightx = 0.38;
        lateral.weighty = 1;
        lateral.fill = GridBagConstraints.BOTH;
        contenido.add(panelLateral, lateral);

        panelTablero.setMinimumSize(new Dimension(300, 300));
        panelLateral.setMinimumSize(new Dimension(230, 300));

        setGlassPane(cartelTurno);

        // Tamano inicial segun la pantalla (proporcion 16:9 aprox), pero
        // siempre se puede cambiar
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int ancho = (int) Math.min(1180, pantalla.width * 0.8);
        int alto = (int) Math.min(ancho / 1.65, pantalla.height * 0.85);
        setSize(ancho, alto);
        setMinimumSize(new Dimension(760, 460));
        setLocationRelativeTo(null);
    }

    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
        panelTablero.setControlador(controlador);
    }

    public void setModeloVisual(IConsultaModeloVisual modeloVisual) {
        this.modeloVisual = modeloVisual;
        panelTablero.setModeloVisual(modeloVisual);
        panelLateral.setModeloVisual(modeloVisual);
        jugadorAsignado = modeloVisual.getJugadorLocal();
        panelLateral.setJugadorAsignado(jugadorAsignado);
        setTitle("Timbiriche - " + jugadorAsignado.getNombre() + " (" + nombreColor(modeloVisual.getColor(jugadorAsignado)) + ")");
        cartelTurno.setModeloVisual(modeloVisual);
    }

    public void setJugadorAsignado(Jugador jugador) {
        this.jugadorAsignado = jugador;
        if (modeloVisual != null) panelLateral.setJugadorAsignado(jugador);
        if (jugador != null && modeloVisual != null) {
            Color color = modeloVisual.getColor(jugador);
            setTitle("Timbiriche - " + jugador.getNombre() + " (" + nombreColor(color) + ")");
        }
    }

    private String nombreColor(Color color) {
        if (Color.BLUE.equals(color)) return "Azul";
        if (Color.RED.equals(color)) return "Rojo";
        if (new Color(0, 150, 0).equals(color)) return "Verde";
        if (new Color(220, 140, 0).equals(color)) return "Naranja";
        return "Personalizado";
    }

    // Lo llama el Modelo Visual cuando algo cambia patron Observer
    @Override
    public void actualizar() {

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::actualizar);
            return;
        }

        if (modeloVisual == null) {
            return;
        }

        // Cartel "TURNO DE ..." cada vez que cambia el jugador en turno
        // si completa un cuadro y repite turno, no cambia y no se repite
        if (!modeloVisual.isJuegoTerminado()) {
            Jugador actual = modeloVisual.getJugadorActual();
            if (actual != ultimoAnunciado) {
                ultimoAnunciado = actual;
                cartelTurno.anunciar("¡TURNO DE " + actual.getNombre().toUpperCase() + "!", panelTablero);
            }
        }

        panelTablero.repaint();
        panelLateral.repaint();
    }

    // ------------------------------------------
    // Cartel que oscurece la pantalla un instante al empezar cada turno
    // (el "TU TURNO!!!" del storyboard) se pinta en el glass pane
    // ------------------------------------------

    private static class CartelTurno extends JComponent {

        private static final long DURACION_MS = 1300;

        private IConsultaModeloVisual modeloVisual;
    private Jugador jugadorAsignado;
        private String texto = "";
        private JComponent referencia; // el cartel se centra sobre el tablero
        private long inicio;
        private final Timer reloj = new Timer(25, e -> avanzar());

        CartelTurno() {
            setOpaque(false);
            setVisible(false);
        }

        void setModeloVisual(IConsultaModeloVisual modeloVisual) {
            this.modeloVisual = modeloVisual;
        }

        void anunciar(String texto, JComponent referencia) {
            this.texto = texto;
            this.referencia = referencia;
            this.inicio = System.nanoTime();
            setVisible(true);
            reloj.restart();
        }

        private double progreso() {
            return Math.min(1.0, (System.nanoTime() - inicio) / 1_000_000.0 / DURACION_MS);
        }

        private void avanzar() {
            if (progreso() >= 1.0) {
                reloj.stop();
                setVisible(false);
            } else {
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {

            double t = progreso();
            // Aparece rapido, se queda y se desvanece al final
            double opacidad = t < 0.1 ? t / 0.1 : (t > 0.7 ? Math.max(0, (1 - t) / 0.3) : 1);
            double entrada = 1 - Math.pow(1 - Math.min(1, t / 0.2), 3); // 0 -> 1 suave

            Graphics2D g2 = (Graphics2D) g.create();
            Tema.suavizar(g2);

            g2.setColor(new Color(0, 0, 0, (int) (165 * opacidad)));
            g2.fillRect(0, 0, getWidth(), getHeight());

            Rectangle zona = referencia == null
                    ? new Rectangle(0, 0, getWidth(), getHeight())
                    : SwingUtilities.convertRectangle(referencia.getParent(), referencia.getBounds(), this);

            // Se mide el texto real y se reduce lo necesario para que quepa en el tablero
            double tamano = zona.height * 0.12;
            g2.setFont(Tema.fuente(Font.BOLD, tamano));
            double ancho = g2.getFontMetrics().stringWidth(texto);
            if (ancho > zona.width * 0.9) {
                tamano *= zona.width * 0.9 / ancho;
            }
            tamano *= 0.88 + 0.12 * entrada; // pequeno efecto de entrada
            g2.setFont(Tema.fuente(Font.BOLD, tamano));
            FontMetrics fm = g2.getFontMetrics();

            Color color = Tema.TEXTO;
            if (modeloVisual != null && !modeloVisual.isJuegoTerminado()) {
                color = Tema.mezclar(modeloVisual.getColor(modeloVisual.getJugadorActual()), Color.WHITE, 0.55);
            }
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (255 * opacidad)));
            g2.drawString(texto,
                    (float) (zona.getCenterX() - fm.stringWidth(texto) / 2.0),
                    (float) (zona.getCenterY() + (fm.getAscent() - fm.getDescent()) / 2.0));

            g2.dispose();
        }
    }
}
