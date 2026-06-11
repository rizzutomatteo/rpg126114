package it.unicam.cs.mpgc.rpg126114.model.regole;

import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;

/**
 * L'esito prodotto da una regola applicabile: una destinazione suggerita,
 * un peso che ne misura la forza e la motivazione da mettere a verbale.
 */
public final class Esito {

    public static final int PESO_MINIMO = 1;

    /** Peso di un semplice indizio. */
    public static final int PESO_INDIZIO = 1;

    /** Peso di un'indicazione forte. */
    public static final int PESO_FORTE = 2;

    /** Peso di una prova determinante. */
    public static final int PESO_DETERMINANTE = 3;

    private final Destinazione destinazione;
    private final int peso;
    private final String motivazione;

    /**
     * @param destinazione destinazione suggerita, non null
     * @param peso         forza del suggerimento, almeno {@link #PESO_MINIMO}
     * @param motivazione  motivazione a verbale, non vuota
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    public Esito(Destinazione destinazione, int peso, String motivazione) {
        if (destinazione == null) {
            throw new IllegalArgumentException("La destinazione di un esito non puo' essere null");
        }
        if (peso < PESO_MINIMO) {
            throw new IllegalArgumentException("Il peso di un esito deve essere positivo: " + peso);
        }
        if (motivazione == null || motivazione.isBlank()) {
            throw new IllegalArgumentException("La motivazione di un esito non puo' essere vuota");
        }
        this.destinazione = destinazione;
        this.peso = peso;
        this.motivazione = motivazione;
    }

    public Destinazione getDestinazione() {
        return destinazione;
    }

    public int getPeso() {
        return peso;
    }

    public String getMotivazione() {
        return motivazione;
    }

    @Override
    public String toString() {
        return destinazione.getEtichetta() + " (peso " + peso + "): " + motivazione;
    }
}
