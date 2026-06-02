package it.unicam.cs.mpgc.rpg126114.model.anima;

import java.util.Objects;

/**
 * Oggetto valore che rappresenta una virtu' praticata in vita da un'anima.
 * Il merito e' espresso su una scala da 1 (modesto) a 10 (eroico).
 */
public final class Virtu {

    public static final int MERITO_MINIMO = 1;
    public static final int MERITO_MASSIMO = 10;

    private final String descrizione;
    private final int merito;

    /**
     * @param descrizione descrizione della virtu', non vuota
     * @param merito      merito compreso tra {@link #MERITO_MINIMO} e {@link #MERITO_MASSIMO}
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    public Virtu(String descrizione, int merito) {
        if (descrizione == null || descrizione.isBlank()) {
            throw new IllegalArgumentException("La descrizione della virtu' non puo' essere vuota");
        }
        if (merito < MERITO_MINIMO || merito > MERITO_MASSIMO) {
            throw new IllegalArgumentException("Merito fuori scala: " + merito);
        }
        this.descrizione = descrizione;
        this.merito = merito;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public int getMerito() {
        return merito;
    }

    @Override
    public boolean equals(Object oggetto) {
        if (this == oggetto) {
            return true;
        }
        if (!(oggetto instanceof Virtu)) {
            return false;
        }
        Virtu altra = (Virtu) oggetto;
        return merito == altra.merito && descrizione.equals(altra.descrizione);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descrizione, merito);
    }

    @Override
    public String toString() {
        return descrizione + " (merito " + merito + ")";
    }
}
