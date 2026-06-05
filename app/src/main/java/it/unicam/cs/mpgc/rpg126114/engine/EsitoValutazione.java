package it.unicam.cs.mpgc.rpg126114.engine;

import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Il risultato della valutazione di un verdetto: se era corretto, quanto
 * karma ha fruttato e le motivazioni del regolamento da mostrare al
 * giocatore come riscontro.
 */
public final class EsitoValutazione {

    private final boolean corretto;
    private final int deltaKarma;
    private final Destinazione destinazioneAttesa;
    private final List<String> motivazioni;

    /**
     * @param corretto           true se il verdetto coincideva con il regolamento
     * @param deltaKarma         la variazione di karma applicata
     * @param destinazioneAttesa la destinazione prevista dal regolamento, non null
     * @param motivazioni        le motivazioni a verbale, non null
     * @throws IllegalArgumentException se destinazione o motivazioni sono null
     */
    public EsitoValutazione(boolean corretto, int deltaKarma,
                            Destinazione destinazioneAttesa, List<String> motivazioni) {
        if (destinazioneAttesa == null || motivazioni == null) {
            throw new IllegalArgumentException("Destinazione attesa e motivazioni sono obbligatorie");
        }
        this.corretto = corretto;
        this.deltaKarma = deltaKarma;
        this.destinazioneAttesa = destinazioneAttesa;
        this.motivazioni = new ArrayList<>(motivazioni);
    }

    public boolean isCorretto() {
        return corretto;
    }

    public int getDeltaKarma() {
        return deltaKarma;
    }

    public Destinazione getDestinazioneAttesa() {
        return destinazioneAttesa;
    }

    /**
     * @return vista non modificabile delle motivazioni del regolamento
     */
    public List<String> getMotivazioni() {
        return Collections.unmodifiableList(motivazioni);
    }
}
