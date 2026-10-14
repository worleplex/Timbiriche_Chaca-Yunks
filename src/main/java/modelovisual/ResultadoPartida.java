package modelovisual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultadoPartida {

    private final String titulo;
    private final String subtitulo;
    private final Color colorSubtitulo;
    private final List<FilaResultado> filas;

    public ResultadoPartida(String titulo, String subtitulo, Color colorSubtitulo, List<FilaResultado> filas) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.colorSubtitulo = colorSubtitulo;
        this.filas = Collections.unmodifiableList(new ArrayList<>(filas));
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public Color getColorSubtitulo() {
        return colorSubtitulo;
    }

    public List<FilaResultado> getFilas() {
        return filas;
    }
}
