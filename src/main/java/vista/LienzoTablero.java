package vista;

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
import java.util.List;

class LienzoTablero {

    private final IConsultaModeloVisual modeloVisual;
    private final int tamano;

    private final Color[][] lineasH;
    private final Color[][] lineasV;
    private final Color[][] cuadros;
    private final EstadoPunto[][] puntos;

    private BufferedImage imagen;
    private int ancho;
    private int alto;
    private double escala;

    LienzoTablero(IConsultaModeloVisual modeloVisual) {
        this.modeloVisual = modeloVisual;
        this.tamano = modeloVisual.getTamanoTablero();
        this.lineasH = new Color[tamano][tamano];
        this.lineasV = new Color[tamano][tamano];
        this.cuadros = new Color[tamano][tamano];
        this.puntos = new EstadoPunto[tamano][tamano];
    }

    List<Rectangle> aplicar(List<PuntoVisual> puntosCambiados, List<LineaVisual> lineasNuevas,
                            List<CuadroVisual> cuadrosNuevos, int w, int h) {

        List<Rectangle2D> zonas = new ArrayList<>();
        double celda = modeloVisual.getTamanoCelda(w, h);
        double radio = radioPunto(celda);
        double grosor = grosorLinea(celda);

        for (CuadroVisual cuadro : cuadrosNuevos) {
            cuadros[cuadro.getFila()][cuadro.getColumna()] = cuadro.getColor();
            double x = modeloVisual.getCoordenadaX(cuadro.getColumna(), w, h);
            double y = modeloVisual.getCoordenadaY(cuadro.getFila(), w, h);
            zonas.add(expandido(x, y, x + celda, y + celda, grosor / 2));
        }

        for (LineaVisual linea : lineasNuevas) {
            registrarLinea(linea);
            double x1 = modeloVisual.getCoordenadaX(linea.getColumna1(), w, h);
            double y1 = modeloVisual.getCoordenadaY(linea.getFila1(), w, h);
            double x2 = modeloVisual.getCoordenadaX(linea.getColumna2(), w, h);
            double y2 = modeloVisual.getCoordenadaY(linea.getFila2(), w, h);
            zonas.add(expandido(Math.min(x1, x2), Math.min(y1, y2),
                    Math.max(x1, x2), Math.max(y1, y2), grosor / 2));
        }

        for (PuntoVisual punto : puntosCambiados) {
            puntos[punto.getFila()][punto.getColumna()] = punto.getEstado();
            double reach = radio * Math.max(factor(punto.getEstadoAnterior()), factor(punto.getEstado()));
            double x = modeloVisual.getCoordenadaX(punto.getColumna(), w, h);
            double y = modeloVisual.getCoordenadaY(punto.getFila(), w, h);
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

    private void registrarLinea(LineaVisual linea) {

        int fila = Math.min(linea.getFila1(), linea.getFila2());
        int columna = Math.min(linea.getColumna1(), linea.getColumna2());

        if (linea.getFila1() == linea.getFila2()) {
            lineasH[fila][columna] = linea.getColor();
        } else {
            lineasV[fila][columna] = linea.getColor();
        }
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

        double celda = modeloVisual.getTamanoCelda(w, h);
        double radio = radioPunto(celda);
        double grosor = grosorLinea(celda);
        double margen = Math.max(radio * 3.1, grosor / 2) + 2;

        int cDesde = indice(zona.getMinX() - margen, true, celda, modeloVisual.getCoordenadaX(0, w, h));
        int cHasta = indice(zona.getMaxX() + margen, false, celda, modeloVisual.getCoordenadaX(0, w, h));
        int fDesde = indice(zona.getMinY() - margen, true, celda, modeloVisual.getCoordenadaY(0, w, h));
        int fHasta = indice(zona.getMaxY() + margen, false, celda, modeloVisual.getCoordenadaY(0, w, h));

        g2.setClip(zona);

        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(zona.x, zona.y, zona.width, zona.height);
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setColor(Tema.PANEL);
        g2.fill(Tema.redondeado(0, 0, w, h, Math.min(w, h) * 0.05));

        for (int f = fDesde; f <= Math.min(fHasta, tamano - 2); f++) {
            for (int c = cDesde; c <= Math.min(cHasta, tamano - 2); c++) {
                if (cuadros[f][c] != null) {
                    g2.setColor(Tema.conAlfa(cuadros[f][c], 115));
                    g2.fill(new Rectangle2D.Double(
                            modeloVisual.getCoordenadaX(c, w, h), modeloVisual.getCoordenadaY(f, w, h),
                            celda, celda));
                }
            }
        }

        g2.setStroke(new BasicStroke((float) grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int f = fDesde; f <= fHasta; f++) {
            for (int c = cDesde; c <= cHasta; c++) {
                double x = modeloVisual.getCoordenadaX(c, w, h);
                double y = modeloVisual.getCoordenadaY(f, w, h);
                if (c < tamano - 1 && lineasH[f][c] != null) {
                    g2.setColor(lineasH[f][c]);
                    g2.draw(new Line2D.Double(x, y, x + celda, y));
                }
                if (f < tamano - 1 && lineasV[f][c] != null) {
                    g2.setColor(lineasV[f][c]);
                    g2.draw(new Line2D.Double(x, y, x, y + celda));
                }
            }
        }

        Color colorTurno = modeloVisual.getColorTurno();
        if (colorTurno == null) {
            colorTurno = Tema.PUNTO;
        }

        for (int f = fDesde; f <= fHasta; f++) {
            for (int c = cDesde; c <= cHasta; c++) {
                dibujarPunto(g2, modeloVisual.getCoordenadaX(c, w, h),
                        modeloVisual.getCoordenadaY(f, w, h), radio, colorTurno,
                        puntos[f][c] == null ? EstadoPunto.NORMAL : puntos[f][c]);
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

    private int indice(double coordenada, boolean haciaAbajo, double celda, double origen) {
        double posicion = (coordenada - origen) / celda;
        int indice = haciaAbajo ? (int) Math.floor(posicion) : (int) Math.ceil(posicion);
        return Math.max(0, Math.min(tamano - 1, indice));
    }
}
