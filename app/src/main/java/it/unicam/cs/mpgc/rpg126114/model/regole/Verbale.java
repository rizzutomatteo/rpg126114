package it.unicam.cs.mpgc.rpg126114.model.regole;

import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Il verbale del regolamento su un fascicolo: la destinazione prevista
 * e le motivazioni di tutte le regole applicabili, prodotti in un'unica
 * valutazione delle regole.
 */
public final class Verbale {

    private final Destinazione destinazione;
    private final List<String> motivazioni;

    /**
     * @param destinazione la destinazione prevista, non null
     * @param motivazioni  le motivazioni a verbale, non null
     * @throws IllegalArgumentException se i parametri sono null
     */
    public Verbale(Destinazione destinazione, List<String> motivazioni) {
        if (destinazione == null || motivazioni == null) {
            throw new IllegalArgumentException("Un verbale richiede destinazione e motivazioni");
        }
        this.destinazione = destinazione;
        this.motivazioni = new ArrayList<>(motivazioni);
    }

    public Destinazione getDestinazione() {
        return destinazione;
    }

    /**
     * @return vista non modificabile delle motivazioni, in ordine di regola
     */
    public List<String> getMotivazioni() {
        return Collections.unmodifiableList(motivazioni);
    }
}
