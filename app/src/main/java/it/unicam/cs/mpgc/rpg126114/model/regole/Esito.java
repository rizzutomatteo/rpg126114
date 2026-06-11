package it.unicam.cs.mpgc.rpg126114.model.regole;

import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;

/**
 * L'esito prodotto da una regola applicabile: la destinazione suggerita
 * e la motivazione da mettere a verbale.
 *
 * <p>Non c'e' un peso: tra regole diverse decide la piu' recente
 * (vedi {@link Regolamento}), non la somma di pesi.</p>
 */
public final class Esito {

    private final Destinazione destinazione;
    private final String motivazione;

    /**
     * @param destinazione destinazione suggerita, non null
     * @param motivazione  motivazione a verbale, non vuota
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    public Esito(Destinazione destinazione, String motivazione) {
        if (destinazione == null) {
            throw new IllegalArgumentException("La destinazione di un esito non puo' essere null");
        }
        if (motivazione == null || motivazione.isBlank()) {
            throw new IllegalArgumentException("La motivazione di un esito non puo' essere vuota");
        }
        this.destinazione = destinazione;
        this.motivazione = motivazione;
    }

    public Destinazione getDestinazione() {
        return destinazione;
    }

    public String getMotivazione() {
        return motivazione;
    }

    @Override
    public String toString() {
        return destinazione.getEtichetta() + ": " + motivazione;
    }
}
