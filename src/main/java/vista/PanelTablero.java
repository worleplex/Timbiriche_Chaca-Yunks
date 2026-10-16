package vista;

import controlador.Controlador;
import modelovisual.CuadroVisual;
import modelovisual.FilaResultado;
import modelovisual.IConsultaModeloVisual;
import modelovisual.LineaVisual;
import modelovisual.PuntoVisual;
import modelovisual.ResultadoPartida;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

class PanelTablero extends JPanel {

    private IConsultaModeloVisual modeloVisual;
    private Controlador controlador;
    private LienzoTablero lienzo;
    private ResultadoPartida resultado;

    PanelTablero() {

        setOpaque(false);

        MouseAdapter raton = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (modeloVisual == null || controlador == null) {
                    return;
                }
                controlador.clic(e.getX(), e.getY(), getWidth(), getHeight());
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (modeloVisual == null || controlador == null) {
                    return;
                }
                controlador.mover(e.getX(), e.getY(), getWidth(), getHeight());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (modeloVisual == null || controlador == null) {
                    return;
                }
                controlador.salirDelTablero();
            }
        };
        addMouseListener(raton);
        addMouseMotionListener(raton);
    }

    void setModeloVisual(IConsultaModeloVisual modeloVisual) {
        this.modeloVisual = modeloVisual;
        this.lienzo = new LienzoTablero(modeloVisual);
    }

    void aplicarCambios(List<PuntoVisual> puntos, List<LineaVisual> lineas, List<CuadroVisual> cuadros) {

        if (lienzo == null) {
            return;
        }

        for (Rectangle zona : lienzo.aplicar(puntos, lineas, cuadros, getWidth(), getHeight())) {
            repaint(zona);
        }
    }

    void setResultado(ResultadoPartida nuevo) {

        boolean cambio = (nuevo == null) != (resultado == null);
        resultado = nuevo;

        if (cambio) {
            repaint();
        }
    }

    void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    void actualizarCursor() {
        if (modeloVisual == null) {
            return;
        }
        setCursor(Cursor.getPredefinedCursor(
                modeloVisual.hayPuntoBajoMouse() ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (lienzo == null || getWidth() <= 0 || getHeight() <= 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        Tema.suavizar(g2);

        lienzo.dibujar(g2, getWidth(), getHeight());

        if (resultado != null) {
            dibujarResultado(g2, Math.min(getWidth(), getHeight()) * 0.05, resultado);
        }

        g2.dispose();
    }

    private void dibujarResultado(Graphics2D g2, double arcoTablero, ResultadoPartida resultado) {

        int w = getWidth();
        int h = getHeight();
        double u = Math.min(w, h) / 600.0;

        g2.setColor(new Color(0, 0, 0, 175));
        g2.fill(Tema.redondeado(0, 0, w, h, arcoTablero));

        List<FilaResultado> filas = resultado.getFilas();

        double anchoCaja = 400 * u;
        double altoCaja = (150 + 40 * filas.size()) * u;
        double x = (w - anchoCaja) / 2;
        double y = (h - altoCaja) / 2;

        g2.setColor(Tema.PANEL);
        g2.fill(Tema.redondeado(x, y, anchoCaja, altoCaja, 26 * u));
        g2.setColor(Tema.BORDE_AVATAR);
        g2.setStroke(new BasicStroke((float) (2 * u)));
        g2.draw(Tema.redondeado(x, y, anchoCaja, altoCaja, 26 * u));

        centrado(g2, resultado.getTitulo(), Tema.fuente(Font.BOLD, 30 * u), Tema.TEXTO, w / 2.0, y + 52 * u);

        Color colorSub = resultado.getColorSubtitulo() != null ? resultado.getColorSubtitulo() : Tema.PUNTO;
        centrado(g2, resultado.getSubtitulo(), Tema.fuente(Font.BOLD, 18 * u), colorSub, w / 2.0, y + 86 * u);

        double fila = y + 128 * u;
        for (FilaResultado filaResultado : filas) {

            double izquierda = x + 36 * u;

            g2.setColor(Tema.conAlfa(filaResultado.getColor(), 255));
            g2.fill(Tema.redondeado(izquierda, fila - 20 * u, 8 * u, 26 * u, 6 * u));

            g2.setFont(Tema.fuente(Font.BOLD, 18 * u));
            g2.setColor(Tema.TEXTO);
            g2.drawString(filaResultado.getPosicion() + ".", (float) (izquierda + 20 * u), (float) fila);
            g2.setFont(Tema.fuente(Font.PLAIN, 18 * u));
            g2.drawString(filaResultado.getNombre(), (float) (izquierda + 52 * u), (float) fila);

            String puntos = filaResultado.getTextoPuntaje();
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(puntos, (float) (x + anchoCaja - 36 * u - fm.stringWidth(puntos)), (float) fila);

            fila += 40 * u;
        }
    }

    private void centrado(Graphics2D g2, String texto, Font fuente, Color color, double centroX, double base) {
        g2.setFont(fuente);
        g2.setColor(color);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, (float) (centroX - fm.stringWidth(texto) / 2.0), (float) base);
    }
}

