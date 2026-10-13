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
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import modelo.Jugador;
import modelovisual.IConsultaModeloVisual;

/**
 *
 * @author julia
 */
public class PanelLateral extends JPanel {
    private enum Estado { ACTIVO, SIGUIENTE, ESPERA }
    private IConsultaModeloVisual modeloVisual;
    
    PanelLateral() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        agregar(new TarjetaPrincipal(), 0, 0.29, 10);
        agregar(new ListaRivales(), 1, 0.47, 10);
        agregar(new PanelAcciones(), 2, 0.14, 0);
    }
    
    public void agregar(JComponent componente, int fila, double pesoAltura, int separacion) {
        // uso el gridbagConstraints que define las restricciones y 
        // reglas de posicionamiento para los componentes gráficos que se
        // organizan utilizando un contenedor con el GridBagLayout de arriba que puse
        GridBagConstraints restriccion = new GridBagConstraints();
        restriccion.gridx = 0;
        restriccion.gridy = fila;
        restriccion.weightx = 1;
        restriccion.weighty = pesoAltura;
        restriccion.fill = GridBagConstraints.BOTH;
        restriccion.insets = new java.awt.Insets(0, 0, separacion, 0);
        add(componente, restriccion);
    }
    // ou
    void setModeloVisual(IConsultaModeloVisual modeloVisual) {
        this.modeloVisual = modeloVisual;
    }
    
    private Jugador jugadorDeLaTarjeta() {
        if (modeloVisual.isJuegoTerminado()) {
            return modeloVisual.getGanadores().get(0);
        }
        return modeloVisual.getJugadorActual();
    }
    
    private List<Jugador> rivales() {
        List<Jugador> todos = modeloVisual.getJugadores();
        int inicio = todos.indexOf(jugadorDeLaTarjeta());
        
        List<Jugador> lista = new ArrayList<>();
        for (int i = 1; i < todos.size(); i++) {
            lista.add(todos.get((inicio + i) % todos.size()));
        }
        return lista;
    }
    // pa usarla en esta class
    private static String textoPuntos(int puntos) {
        return puntos == 1 ? "punto" : "puntos";
    }
    // la tarjeta de un jugador va a estar largo
private void dibujarTarjeta(Graphics2D g2, double x,
    double y, double ancho, double alto,
    Jugador jugador, Estado estado, String subtitulo, 
    boolean grande) {

        Color brillante = modeloVisual.getColor(jugador);
        boolean activa = estado == Estado.ACTIVO;

        double arco = alto * (grande ? 0.16 : 0.3);
        double margen = alto * (grande ? 0.09 : 0.1);
        double grosorBorde = Math.max(2, alto * 
        (grande ? 0.03 : 0.055));

        g2.setColor(Tema.cuerpo(brillante));
        g2.fill(Tema.redondeado(x, y, ancho, alto, arco));

        if (estado != Estado.ESPERA) {
            g2.setColor(brillante);
            g2.setStroke(new BasicStroke((float) grosorBorde));
            g2.draw(Tema.redondeado(x + grosorBorde / 2, y + 
            grosorBorde / 2,
                    ancho - grosorBorde, alto - grosorBorde, arco));
        }

        double ladoAvatar = alto - 2 * margen;
        Tema.dibujarAvatar(g2, x + margen, y + margen, ladoAvatar, 
        brillante, !activa);

        // Placa oscura con el texto.
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

        int puntos = jugador.getPuntaje();

        if (grande) {
            double tamNombre = alto * 0.17;
            escribir(g2, jugador.getNombre(), Tema.fuente(Font.PLAIN, 
            tamNombre), colorTexto,
                    textoX, y + margen + interior + tamNombre * 0.95, 
                    textoAncho);

            if (subtitulo != null) {
                escribir(g2, subtitulo, Tema.fuente(Font.BOLD, alto * 
                0.085), colorTexto,
                        textoX, y + margen + placaAlto - interior - 
                        alto * 0.17, textoAncho);
            }
            dibujarPuntos(g2, puntos, alto * 0.15, colorTexto, textoX, 
            y + margen + placaAlto - interior);
        } else {
            double tamNombre = alto * 0.27;
            escribir(g2, jugador.getNombre(), Tema.fuente(Font.PLAIN, 
            tamNombre), colorTexto,
                    textoX, y + margen + placaAlto * 0.46, textoAncho);
            dibujarPuntos(g2, puntos, alto * 0.2, colorTexto, textoX, 
            y + margen + placaAlto * 0.86);
        }
    }

    // "12 puntos" con el numero mas grande que la palabra.
    private void dibujarPuntos(Graphics2D g2, int puntos, double tamano, Color color, double x, double base) {

        String numero = String.valueOf(puntos);
        Font fuenteNumero = Tema.fuente(Font.PLAIN, tamano * 1.15);
        g2.setFont(fuenteNumero);
        g2.setColor(color);
        g2.drawString(numero, (float) x, (float) base);

        double ancho = g2.getFontMetrics().stringWidth(numero);
        g2.setFont(Tema.fuente(Font.PLAIN, tamano * 0.78));
        g2.drawString(" " + textoPuntos(puntos), (float) (x + ancho), (float) base);
    }

    // Escribe el texto recortandolo con "..." si no cabe.
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

    // ------------------------------------------
    // Componentes internos
    // ------------------------------------------

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

            boolean terminado = modeloVisual.isJuegoTerminado();
            Jugador jugador = jugadorDeLaTarjeta();
            String subtitulo = terminado
                    ? (modeloVisual.getGanadores().size() == 1 ? "Ganador" : "Empate")
                    : "Tu turno";

            dibujarTarjeta(g2, 0, 0, getWidth(), getHeight(), jugador,
                    terminado ? Estado.ESPERA : Estado.ACTIVO, subtitulo, true);

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

            // Siempre hay lugar para 3 filas (4 jugadores como maximo), asi
            // las tarjetas miden lo mismo juegue quien juegue.
            double margen = h * 0.035;
            double separacion = h * 0.02;
            double altoFila = (h - 2 * margen - 2 * separacion) / 3;

            List<Jugador> rivales = rivales();
            boolean terminado = modeloVisual.isJuegoTerminado();

            for (int i = 0; i < rivales.size(); i++) {
                Estado estado = (i == 0 && !terminado) ? Estado.SIGUIENTE : Estado.ESPERA;
                dibujarTarjeta(g2, margen, margen + i * (altoFila + separacion),
                        w - 2 * margen, altoFila, rivales.get(i), estado, null, false);
            }

            g2.dispose();
        }
    }

    private class PanelAcciones extends JPanel {

        PanelAcciones() {

            setOpaque(false);
            setPreferredSize(new Dimension(10, 10));
            setLayout(new GridLayout(2, 1, 0, 6));

            // Estas acciones necesitan reglas que el Modelo General todavia no
            // tiene (ponerse de acuerdo entre jugadores, retirar a un jugador),
            // asi que por ahora se muestran pero desactivadas.
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

        // Margen interno proporcional al tamano actual del panel.
        @Override
        public java.awt.Insets getInsets() {
            int margenY = (int) (getHeight() * 0.11);
            int margenX = (int) (getWidth() * 0.025) + 6;
            return new java.awt.Insets(margenY, margenX, margenY, margenX);
        }
    }
}