package it.unicam.cs.mpgc.rpg126114.model.documenti;

/**
 * Lettera con cui un personaggio influente raccomanda l'anima.
 * Il peso conta solo se la firma e' autentica: una raccomandazione
 * falsa e' un'aggravante.
 */
public class LetteraRaccomandazione extends Documento {

    public static final int PESO_MINIMO = 1;
    public static final int PESO_MASSIMO = 5;

    private final String firmatario;
    private final int peso;

    /**
     * @param firmatario  chi firma la lettera, non vuoto
     * @param peso        influenza del firmatario tra {@link #PESO_MINIMO} e {@link #PESO_MASSIMO}
     * @param attendibile true se la firma e' autentica
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    public LetteraRaccomandazione(String firmatario, int peso, boolean attendibile) {
        super("Lettera di Raccomandazione", attendibile);
        if (firmatario == null || firmatario.isBlank()) {
            throw new IllegalArgumentException("Il firmatario non puo' essere vuoto");
        }
        if (peso < PESO_MINIMO || peso > PESO_MASSIMO) {
            throw new IllegalArgumentException("Peso della raccomandazione fuori scala: " + peso);
        }
        this.firmatario = firmatario;
        this.peso = peso;
    }

    public String getFirmatario() {
        return firmatario;
    }

    public int getPeso() {
        return peso;
    }

    @Override
    public String contenuto() {
        return "\"Chi scrive garantisce per l'anima qui presente.\" Firmato: " + firmatario + ".";
    }
}
