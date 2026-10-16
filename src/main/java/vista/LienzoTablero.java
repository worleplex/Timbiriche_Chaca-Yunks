package vista;

import modelovisual.CoordenadaTablero;
import modelovisual.CuadroVisual;
import modelovisual.EstadoPunto;
import modelovisual.IConsultaModeloVisual;
import modelovisual.LineaVisual;
import modelovisual.PuntoVisual;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class LienzoTablero {

    private final IConsultaModeloVisual modeloVisual;
    private final Map<CoordenadaTablero, EstadoPunto> puntos = new LinkedHashMap<>();
    private final List<LineaVisual> lineas = new ArrayList<>();
    private final List<CuadroVisual> cuadros = new ArrayList<>();

    private BufferedImage imagen;
    private int ancho;
    private int alto;
    private double escala;

    private double pxEscala;
    private double pxX;
    private double pxY;

    LienzoTablero(IConsultaModeloVisual modeloVisual) {
        this.modeloVisual = modeloVisual;
        for (CoordenadaTablero punto : modeloVisual.getPuntosTablero()) {
            puntos.put(punto, EstadoPunto.NORMAL);
        }
    }

    private void calibrar(int w, int h) {
        pxEscala = modeloVisual.getEscala(w, h);
        pxX = modeloVisual.getDesplazamientoX(w, h);
        pxY = modeloVisual.getDesplazamientoY(w, h);
    }

    private double px(double x) {
        return pxX + x * pxEscala;
    }

    private double py(double y) {
        return pxY + y * pxEscala;
    }

    private double celdaPx() {
        return modeloVisual.getSeparacionPuntos() * pxEscala;
    }

    List<Rectangle> aplicar(List<PuntoVisual> puntosCambiados, List<LineaVisual> lineasNuevas,
                            List<CuadroVisual> cuadrosNuevos, int w, int h) {

        calibrar(w, h);

        List<Rectangle2D> zonas = new ArrayList<>();
        double celda = celdaPx();
        double radio = radioPunto(celda);
        double grosor = grosorLinea(celda);

        for (CuadroVisual cuadro : cuadrosNuevos) {
            cuadros.add(cuadro);
            double x = px(cuadro.getX());
            double y = py(cuadro.getY());
            double lado = cuadro.getLado() * pxEscala;
            zonas.add(expandido(x, y, x + lado, y + lado, grosor / 2));
        }

        for (LineaVisual linea : lineasNuevas) {
            lineas.add(linea);
            double x1 = px(linea.getX1());
            double y1 = py(linea.getY1());
            double x2 = px(linea.getX2());
            double y2 = py(linea.getY2());
            zonas.add(expandido(Math.min(x1, x2), Math.min(y1, y2),
                    Math.max(x1, x2), Math.max(y1, y2), grosor / 2));
        }

        for (PuntoVisual punto : puntosCambiados) {
            puntos.put(new CoordenadaTablero(punto.getX(), punto.getY()), punto.getEstado());
            double reach = radio * Math.max(factor(punto.getEstadoAnterior()), factor(punto.getEstado()));
            double x = px(punto.getX());
            double y = py(punto.getY());
            zonas.add(expandido(x, y, x, y, reach));
        }

        List<Rectangle> repintar = new ArrayList<>();

        if (!imagenVigente(w, h, escala)) {
            return repintar;
        }

        Graphics2D g2 = crearGraficos();
        for (Rectangle2D zona : zonas) {
            Rectangle entera = aEntero(zona, w, h);
            if (!entera.isEmpty()) {
                recomponer(g2, entera, w, h);
                repintar.add(entera);
            }
        }
        g2.dispose();

        return repintar;
    }

    void dibujar(Graphics2D destino, int w, int h) {

        double escalaActual = Math.max(1.0, destino.getTransform().getScaleX());

        if (!imagenVigente(w, h, escalaActual)) {
            reconstruir(w, h, escalaActual);
        }

        destino.drawImage(imagen, 0, 0, w, h, null);
    }

    private boolean imagenVigente(int w, int h, double esc) {
        return imagen != null && ancho == w && alto == h && escala == esc;
    }

    private void reconstruir(int w, int h, double esc) {

        ancho = w;
        alto = h;
        escala = esc;
        imagen = new BufferedImage(
                Math.max(1, (int) Math.ceil(w * esc)), Math.max(1, (int) Math.ceil(h * esc)),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = crearGraficos();
        recomponer(g2, new Rectangle(0, 0, w, h), w, h);
        g2.dispose();
    }

    private Graphics2D crearGraficos() {
        Graphics2D g2 = imagen.createGraphics();
        g2.scale(escala, escala);
        Tema.suavizar(g2);
        return g2;
    }

    private void recomponer(Graphics2D g2, Rectangle zona, int w, int h) {

        calibrar(w, h);

        double celda = celdaPx();
        double radio = radioPunto(celda);
        double grosor = grosorLinea(celda);
        double margen = Math.max(radio * 3.1, grosor / 2) + 2;

        Rectangle2D alcance = new Rectangle2D.Double(zona.getMinX() - margen, zona.getMinY() - margen,
                zona.getWidth() + 2 * margen, zona.getHeight() + 2 * margen);

        g2.setClip(zona);

        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(zona.x, zona.y, zona.width, zona.height);
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setColor(Tema.PANEL);
        g2.fill(Tema.redondeado(0, 0, w, h, Math.min(w, h) * 0.05));

        for (CuadroVisual cuadro : cuadros) {
            double x = px(cuadro.getX());
            double y = py(cuadro.getY());
            double lado = cuadro.getLado() * pxEscala;
            if (alcance.intersects(x, y, lado, lado)) {
                g2.setColor(Tema.conAlfa(cuadro.getColor(), 115));
                g2.fill(new Rectangle2D.Double(x, y, lado, lado));
            }
        }

        g2.setStroke(new BasicStroke((float) grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (LineaVisual linea : lineas) {
            double x1 = px(linea.getX1());
            double y1 = py(linea.getY1());
            double x2 = px(linea.getX2());
            double y2 = py(linea.getY2());
            if (alcance.intersects(Math.min(x1, x2), Math.min(y1, y2),
                    Math.abs(x2 - x1), Math.abs(y2 - y1))
                    || alcance.intersectsLine(x1, y1, x2, y2)) {
                g2.setColor(linea.getColor());
                g2.draw(new Line2D.Double(x1, y1, x2, y2));
            }
        }

        Color colorTurno = modeloVisual.getColorTurno();
        if (colorTurno == null) {
            colorTurno = Tema.PUNTO;
        }

        for (Map.Entry<CoordenadaTablero, EstadoPunto> punto : puntos.entrySet()) {
            double x = px(punto.getKey().getX());
            double y = py(punto.getKey().getY());
            if (alcance.contains(x, y)) {
                dibujarPunto(g2, x, y, radio, colorTurno, punto.getValue());
            }
        }

        g2.setClip(null);
    }

    private void dibujarPunto(Graphics2D g2, double x, double y, double radio,
                              Color colorTurno, EstadoPunto estado) {
        switch (estado) {
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

    private void circulo(Graphics2D g2, double x, double y, double radio, Color color) {
        g2.setColor(color);
        g2.fill(new Ellipse2D.Double(x - radio, y - radio, radio * 2, radio * 2));
    }

    private double radioPunto(double celda) {
        return Math.max(1.6, Math.min(4.5, celda * 0.1));
    }

    private double grosorLinea(double celda) {
        return Math.max(2, Math.min(7, celda * 0.14));
    }

    private double factor(EstadoPunto estado) {
        switch (estado) {
            case SELECCIONADO:
                return 3.1;
            case RESALTADO:
                return 3.0;
            case HOVER:
                return 1.9;
            default:
                return 1.0;
        }
    }

    private Rectangle2D expandido(double x1, double y1, double x2, double y2, double margen) {
        return new Rectangle2D.Double(x1 - margen, y1 - margen,
                (x2 - x1) + 2 * margen, (y2 - y1) + 2 * margen);
    }

    private Rectangle aEntero(Rectangle2D zona, int w, int h) {
        int x0 = Math.max(0, (int) Math.floor(zona.getMinX()) - 2);
        int y0 = Math.max(0, (int) Math.floor(zona.getMinY()) - 2);
        int x1 = Math.min(w, (int) Math.ceil(zona.getMaxX()) + 2);
        int y1 = Math.min(h, (int) Math.ceil(zona.getMaxY()) + 2);
        return new Rectangle(x0, y0, Math.max(0, x1 - x0), Math.max(0, y1 - y0));
    }
}
