package vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import modelovisual.IConsultaModeloVisual;
import modelovisual.TarjetaJugador;

public class PanelLateral extends JPanel {
    private IConsultaModeloVisual modeloVisual;
    
    PanelLateral() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        agregar(new TarjetaPrincipal(), 0, 0.29, 10);
        agregar(new ListaRivales(), 1, 0.47, 10);
        agregar(new PanelAcciones(), 2, 0.14, 0);
    }
    
    public void agregar(JComponent componente, int fila, double pesoAltura, int separacion) {
        GridBagConstraints restriccion = new GridBagConstraints();
        restriccion.gridx = 0;
        restriccion.gridy = fila;
        restriccion.weightx = 1;
        restriccion.weighty = pesoAltura;
        restriccion.fill = GridBagConstraints.BOTH;
        restriccion.insets = new java.awt.Insets(0, 0, separacion, 0);
        add(componente, restriccion);
    }
    void setModeloVisual(IConsultaModeloVisual modeloVisual) {
        this.modeloVisual = modeloVisual;
    }

    private void dibujarTarjeta(Graphics2D g2, double x, double y, double ancho, double alto,
            TarjetaJugador tarjeta, boolean grande) {

        Color brillante = tarjeta.getColor();
        boolean activa = tarjeta.isActiva();

        double arco = alto * (grande ? 0.16 : 0.3);
        double margen = alto * (grande ? 0.09 : 0.1);
        double grosorBorde = Math.max(2, alto * 
        (grande ? 0.03 : 0.055));

        g2.setColor(Tema.cuerpo(brillante));
        g2.fill(Tema.redondeado(x, y, ancho, alto, arco));

        if (activa) {
            g2.setColor(brillante);
            g2.setStroke(new BasicStroke((float) grosorBorde));
            g2.draw(Tema.redondeado(x + grosorBorde / 2, y + 
            grosorBorde / 2,
                    ancho - grosorBorde, alto - grosorBorde, arco));
        }

        double ladoAvatar = alto - 2 * margen;
        Tema.dibujarAvatar(g2, x + margen, y + margen, ladoAvatar, 
        brillante, !activa);

        double placaX = x + margen * 2 + ladoAvatar;
        double placaAncho = x + ancho - margen - placaX;
        double placaAlto = alto - 2 * margen;
        g2.setColor(Tema.placa(brillante));
        g2.fill(Tema.redondeado(placaX, y + margen, placaAncho, 
        placaAlto, arco * 0.7));

        Color colorTexto = activa ? Tema.TEXTO : 
        Tema.mezclar(Tema.TEXTO, Tema.placa(brillante), 0.35);
        double interior = placaAlto * 0.12;
        double textoX = placaX + placaAncho * 0.07;
        double textoAncho = placaAncho * 0.88;

        String subtitulo = tarjeta.getSubtitulo();

        if (grande) {
            double tamNombre = alto * 0.17;
            escribir(g2, tarjeta.getNombre(), Tema.fuente(Font.PLAIN, 
            tamNombre), colorTexto,
                    textoX, y + margen + interior + tamNombre * 0.95, 
                    textoAncho);

            if (subtitulo != null) {
                escribir(g2, subtitulo, Tema.fuente(Font.BOLD, alto * 
                0.085), colorTexto,
                        textoX, y + margen + placaAlto - interior - 
                        alto * 0.17, textoAncho);
            }
            dibujarPuntos(g2, tarjeta, alto * 0.15, colorTexto, textoX, 
            y + margen + placaAlto - interior);
        } else {
            double tamNombre = alto * 0.27;
            escribir(g2, tarjeta.getNombre(), Tema.fuente(Font.PLAIN, 
            tamNombre), colorTexto,
                    textoX, y + margen + placaAlto * 0.46, textoAncho);
            dibujarPuntos(g2, tarjeta, alto * 0.2, colorTexto, textoX, 
            y + margen + placaAlto * 0.86);
        }
    }

    private void dibujarPuntos(Graphics2D g2, TarjetaJugador tarjeta, double tamano, Color color, double x, double base) {

        String numero = String.valueOf(tarjeta.getPuntos());
        Font fuenteNumero = Tema.fuente(Font.PLAIN, tamano * 1.15);
        g2.setFont(fuenteNumero);
        g2.setColor(color);
        g2.drawString(numero, (float) x, (float) base);

        double ancho = g2.getFontMetrics().stringWidth(numero);
        g2.setFont(Tema.fuente(Font.PLAIN, tamano * 0.78));
        g2.drawString(" " + tarjeta.getTextoPuntos(), (float) (x + ancho), (float) base);
    }

    private void escribir(Graphics2D g2, String texto, Font fuente, Color color, double x, double base, double anchoMaximo) {

        g2.setFont(fuente);
        g2.setColor(color);
        FontMetrics fm = g2.getFontMetrics();

        String visible = texto;
        while (visible.length() > 1 && fm.stringWidth(visible) > anchoMaximo) {
            visible = visible.substring(0, visible.length() - 1);
            if (fm.stringWidth(visible + "…") <= anchoMaximo) {
                visible = visible + "…";
                break;
            }
        }
        g2.drawString(visible, (float) x, (float) base);
    }

    private class TarjetaPrincipal extends JComponent {

        TarjetaPrincipal() {
            setPreferredSize(new Dimension(10, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {

            if (modeloVisual == null) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            Tema.suavizar(g2);

            dibujarTarjeta(g2, 0, 0, getWidth(), getHeight(),
                    modeloVisual.getTarjetaPrincipal(), true);

            g2.dispose();
        }
    }

    private class ListaRivales extends JComponent {

        ListaRivales() {
            setPreferredSize(new Dimension(10, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {

            if (modeloVisual == null) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            Tema.suavizar(g2);

            double w = getWidth();
            double h = getHeight();

            g2.setColor(Tema.PANEL);
            g2.fill(Tema.redondeado(0, 0, w, h, h * 0.045));

            double margen = h * 0.035;
            double separacion = h * 0.02;
            double altoFila = (h - 2 * margen - 2 * separacion) / 3;

            List<TarjetaJugador> rivales = modeloVisual.getTarjetasRivales();

            for (int i = 0; i < rivales.size(); i++) {
                dibujarTarjeta(g2, margen, margen + i * (altoFila + separacion),
                        w - 2 * margen, altoFila, rivales.get(i), false);
            }

            g2.dispose();
        }
    }

    private class PanelAcciones extends JPanel {

        PanelAcciones() {

            setOpaque(false);
            setPreferredSize(new Dimension(10, 10));
            setLayout(new GridLayout(2, 1, 0, 6));

            add(crearBoton("Terminar Partida"));
            add(crearBoton("Abandonar Partida"));
        }

        private JButton crearBoton(String texto) {

            JButton boton = new JButton(texto) {
                @Override
                protected void paintComponent(Graphics g) {

                    Graphics2D g2 = (Graphics2D) g.create();
                    Tema.suavizar(g2);

                    double w = getWidth();
                    double h = getHeight();
                    g2.setColor(Tema.BOTON);
                    g2.fill(Tema.redondeado(0, 0, w, h, h * 0.45));

                    g2.setFont(Tema.fuente(Font.PLAIN, h * 0.42));
                    g2.setColor(Tema.mezclar(Tema.TEXTO, Tema.BOTON, 0.3));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(getText(), (float) ((w - fm.stringWidth(getText())) / 2),
                            (float) ((h + fm.getAscent() - fm.getDescent()) / 2));

                    g2.dispose();
                }
            };

            boton.setEnabled(false);
            boton.setFocusable(false);
            boton.setOpaque(false);
            boton.setContentAreaFilled(false);
            boton.setBorderPainted(false);
            boton.setToolTipText("Disponible cuando el juego se pueda jugar en red");
            return boton;
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();
            Tema.suavizar(g2);
            g2.setColor(Tema.PANEL);
            g2.fill(Tema.redondeado(0, 0, getWidth(), getHeight(), getHeight() * 0.18));
            g2.dispose();
        }

        @Override
        public java.awt.Insets getInsets() {
            int margenY = (int) (getHeight() * 0.11);
            int margenX = (int) (getWidth() * 0.025) + 6;
            return new java.awt.Insets(margenY, margenX, margenY, margenX);
        }
    }
}