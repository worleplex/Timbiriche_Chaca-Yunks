package vista;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

final class Tema {
    static final Color FONDO = new Color(13, 11, 24);
    static final Color PANEL = new Color(22, 18, 40);
    static final Color BOTON = new Color(46, 35, 77);
    static final Color TEXTO = new Color(245, 245, 252);
    static final Color PUNTO = new Color(190, 192, 215);
    static final Color BORDE_AVATAR = new Color(121, 126, 147);

    private static final String FUENTE = "Segoe UI"; 

    private Tema() {
    }
    static Color mezclar(Color a, Color b, double t) {
        return new Color(
                (int) Math.round(a.getRed() + (b.getRed() - a.getRed()) * t),
                (int) Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int) Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }

    static Color conAlfa(Color color, int alfa) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alfa);
    }
    
    static Color cuerpo(Color brillante) {
        return new Color(
                (int) (brillante.getRed() * 0.55),
                (int) (brillante.getGreen() * 0.55),
                (int) (brillante.getBlue() * 0.55));
    }

    static Color placa(Color brillante) {
        return mezclar(cuerpo(brillante), FONDO, 0.5);
    }

    static Font fuente(int estilo, double tamano) {
        return new Font(FUENTE, estilo, 12).deriveFont(estilo, (float) Math.max(8, tamano));
    }

    static void suavizar(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    static Shape redondeado(double x, double y, double ancho, double alto, double arco) {
        return new RoundRectangle2D.Double(x, y, ancho, alto, arco, arco);
    }

    static void dibujarAvatar(Graphics2D g2, double x, double y, double lado, Color brillante, boolean atenuado) {

        Shape marco = redondeado(x, y, lado, lado, lado * 0.12);
        Color fondo = mezclar(brillante, FONDO, atenuado ? 0.75 : 0.55);
        Color silueta = mezclar(brillante, FONDO, atenuado ? 0.35 : 0.1);

        g2.setColor(fondo);
        g2.fill(marco);

        java.awt.Shape recorteAnterior = g2.getClip();
        g2.clip(marco);
        g2.setColor(silueta);
        double radioCabeza = lado * 0.17;
        g2.fill(new Ellipse2D.Double(x + lado / 2 - radioCabeza, y + lado * 0.36 - radioCabeza,
                radioCabeza * 2, radioCabeza * 2));
        g2.fill(new Ellipse2D.Double(x + lado * 0.14, y + lado * 0.62, lado * 0.72, lado * 0.7));
        g2.setClip(recorteAnterior);

        g2.setColor(BORDE_AVATAR);
        g2.setStroke(new java.awt.BasicStroke((float) Math.max(1.2, lado * 0.03)));
        g2.draw(marco);
    }
}
