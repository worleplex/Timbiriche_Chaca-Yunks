package vista;

import controlador.Controlador;
import modelovisual.CuadroVisual;
import modelovisual.IConsultaContenedor;
import modelovisual.IConsultaModeloVisual;
import modelovisual.LineaVisual;
import modelovisual.PuntoVisual;
import utilerias.Observador;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.List;

public class VistaTablero extends JFrame implements Observador {

    private Controlador controlador;
    private IConsultaModeloVisual modeloVisual;
    private IConsultaContenedor consultaContenedor;

    private final PanelTablero panelTablero = new PanelTablero();
    private final PanelLateral panelLateral = new PanelLateral();

    public VistaTablero() {
        this(1);
    }

    public VistaTablero(int numeroVista) {
        setTitle("Timbiriche - Vista " + numeroVista);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contenido = new JPanel(new GridBagLayout());
        contenido.setBackground(Tema.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setContentPane(contenido);

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
        setTitle(modeloVisual.getTituloVentana());
    }

    public void setConsultaContenedor(IConsultaContenedor consultaContenedor) {
        this.consultaContenedor = consultaContenedor;
        panelTablero.setConsultaContenedor(consultaContenedor);
        panelLateral.setConsultaContenedor(consultaContenedor);
    }

    @Override
    public void actualizar() {

        if (modeloVisual == null) {
            return;
        }

        List<PuntoVisual> puntos = consultaContenedor.getPuntosCambiados();
        List<LineaVisual> lineas = consultaContenedor.getLineasNuevas();
        List<CuadroVisual> cuadros = consultaContenedor.getCuadrosNuevos();

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> aplicarCambios(puntos, lineas, cuadros));
            return;
        }

        aplicarCambios(puntos, lineas, cuadros);
    }

    private void aplicarCambios(List<PuntoVisual> puntos, List<LineaVisual> lineas,
                                List<CuadroVisual> cuadros) {

        panelTablero.aplicarCambios(puntos, lineas, cuadros);
        panelTablero.setResultado(consultaContenedor.getResultado());

        panelTablero.actualizarCursor();
        panelLateral.repaint();
    }
}
