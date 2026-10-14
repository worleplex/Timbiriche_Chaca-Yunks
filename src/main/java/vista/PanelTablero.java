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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

class PanelTablero extends JPanel {

    private IConsultaModeloVisual modeloVisual;
    private Controlador controlador;

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

    private double celda() {
        return modeloVisual.getTamanoCelda(getWidth(), getHeight());
    }

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

            ResultadoPartida resultado = modeloVisual.getResultado();
            if (resultado != null) {
                dibujarResultado(g2, arco, resultado);
            }
        }

        g2.dispose();
    }

    private void dibujarCuadrosCompletados(Graphics2D g2) {

        for (CuadroVisual cuadro : modeloVisual.getCuadros(getWidth(), getHeight())) {

            g2.setColor(Tema.conAlfa(cuadro.getColor(), 115));
            g2.fill(new Rectangle2D.Double(cuadro.getX(), cuadro.getY(), cuadro.getLado(), cuadro.getLado()));
        }
    }

    private void dibujarLineas(Graphics2D g2) {

        float grosor = (float) Math.max(2, Math.min(7, celda() * 0.14));
        g2.setStroke(new BasicStroke(grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (LineaVisual linea : modeloVisual.getLineas(getWidth(), getHeight())) {

            g2.setColor(linea.getColor());
            g2.draw(new Line2D.Double(linea.getX1(), linea.getY1(), linea.getX2(), linea.getY2()));
        }
    }

    private void dibujarPuntos(Graphics2D g2) {

        Color colorTurno = modeloVisual.getColorTurno();
        if (colorTurno == null) {
            colorTurno = Tema.PUNTO;
        }

        double radio = Math.max(1.6, Math.min(4.5, celda() * 0.1));

        for (PuntoVisual punto : modeloVisual.getPuntos(getWidth(), getHeight())) {

            double x = punto.getX();
            double y = punto.getY();

            switch (punto.getEstado()) {
                case SELECCIONADO:
                    circulo(g2, x, y, radio * 3.1, Tema.conAlfa(colorTurno, 80));
                    circulo(g2, x, y, radio * 2.3, Tema.TEXTO);
                    circulo(g2, x, y, radio * 1.6, colorTurno);
                    break;
                case RESALTADO:
                    circulo(g2, x, y, radio * 3.0, Tema.conAlfa(colorTurno, 70));
                    circulo(g2, x, y, radio * 1.9, colorTurno);
                    break;
                case HOVER:
                    circulo(g2, x, y, radio * 1.9, Tema.TEXTO);
                    break;
                default:
                    circulo(g2, x, y, radio, Tema.PUNTO);
                    break;
            }
        }
    }

    private void circulo(Graphics2D g2, double x, double y, double radio, Color color) {
        g2.setColor(color);
        g2.fill(new Ellipse2D.Double(x - radio, y - radio, radio * 2, radio * 2));
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

