package it.unicam.cs.mpgc.rpg126114.engine;

import it.unicam.cs.mpgc.rpg126114.model.carriera.Funzionario;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Fascicolo;
import it.unicam.cs.mpgc.rpg126114.model.regole.Regolamento;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Verdetto;

/**
 * Confronta il verdetto del Funzionario con quello previsto dal
 * regolamento e calcola la variazione di karma.
 *
 * <p>Il karma base viene aumentato dall'Intuito nei verdetti corretti e
 * moltiplicato dal timbro usato, nel bene e nel male: timbri prestigiosi
 * significano gloria o rovina.</p>
 */
public class ValutatoreVerdetti {

    public static final int KARMA_BASE = 10;
    public static final int PREMIO_INTUITO = 2;

    /**
     * Valuta un verdetto rispetto al regolamento in vigore.
     *
     * @param verdetto    il verdetto emesso, riferito alla stessa anima del fascicolo
     * @param regolamento il regolamento in vigore
     * @param fascicolo   il fascicolo giudicato
     * @param funzionario il Funzionario che ha emesso il verdetto
     * @return l'esito della valutazione con il karma maturato
     * @throws IllegalArgumentException se il verdetto riguarda un'anima diversa
     *                                  da quella del fascicolo
     */
    public EsitoValutazione valuta(Verdetto verdetto, Regolamento regolamento,
                                   Fascicolo fascicolo, Funzionario funzionario) {
        if (!verdetto.getAnima().equals(fascicolo.getAnima())) {
            throw new IllegalArgumentException(
                    "Il verdetto riguarda " + verdetto.getAnima()
                            + " ma il fascicolo e' di " + fascicolo.getAnima());
        }
        Destinazione attesa = regolamento.destinazioneAttesa(fascicolo);
        boolean corretto = attesa == verdetto.getDestinazione();
        int delta = karmaPer(corretto, funzionario) * verdetto.getTimbro().getMoltiplicatore();
        return new EsitoValutazione(corretto, delta, attesa, regolamento.motivazioni(fascicolo));
    }

    private int karmaPer(boolean corretto, Funzionario funzionario) {
        if (corretto) {
            return KARMA_BASE + funzionario.getIntuito() * PREMIO_INTUITO;
        }
        return -KARMA_BASE;
    }
}
