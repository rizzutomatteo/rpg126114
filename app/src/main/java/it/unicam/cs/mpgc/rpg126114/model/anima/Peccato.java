package it.unicam.cs.mpgc.rpg126114.model.anima;

import java.util.Objects;

/**
 * Oggetto valore che rappresenta un peccato commesso in vita da un'anima.
 * La gravita' e' espressa su una scala da 1 (veniale) a 10 (capitale).
 */
public final class Peccato {

    public static final int GRAVITA_MINIMA = 1;
    public static final int GRAVITA_MASSIMA = 10;

    /** Soglia oltre la quale un peccato e' considerato capitale. */
    public static final int SOGLIA_CAPITALE = 7;

    private final String descrizione;
    private final int gravita;

    /**
     * @param descrizione descrizione del peccato, non vuota
     * @param gravita     gravita' compresa tra {@link #GRAVITA_MINIMA} e {@link #GRAVITA_MASSIMA}
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    public Peccato(String descrizione, int gravita) {
        if (descrizione == null || descrizione.isBlank()) {
            throw new IllegalArgumentException("La descrizione del peccato non puo' essere vuota");
        }
        if (gravita < GRAVITA_MINIMA || gravita > GRAVITA_MASSIMA) {
            throw new IllegalArgumentException("Gravita' fuori scala: " + gravita);
        }
        this.descrizione = descrizione;
        this.gravita = gravita;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public int getGravita() {
        return gravita;
    }

    /**
     * @return true se il peccato e' abbastanza grave da contare come capitale
     */
    public boolean isCapitale() {
        return gravita >= SOGLIA_CAPITALE;
    }

    @Override
    public boolean equals(Object oggetto) {
        if (this == oggetto) {
            return true;
        }
        if (!(oggetto instanceof Peccato)) {
            return false;
        }
        Peccato altro = (Peccato) oggetto;
        return gravita == altro.gravita && descrizione.equals(altro.descrizione);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descrizione, gravita);
    }

    @Override
    public String toString() {
        return descrizione + " (gravita' " + gravita + ")";
    }
}
