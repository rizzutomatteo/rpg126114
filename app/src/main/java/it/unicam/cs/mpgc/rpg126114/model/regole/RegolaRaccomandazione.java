package it.unicam.cs.mpgc.rpg126114.model.regole;

import it.unicam.cs.mpgc.rpg126114.model.documenti.Fascicolo;
import it.unicam.cs.mpgc.rpg126114.model.documenti.LetteraRaccomandazione;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;

import java.util.Optional;

/**
 * Valuta le lettere di raccomandazione: una firma autentica spinge verso
 * il Paradiso con la forza del firmatario, una firma contraffatta e'
 * un'aggravante infernale.
 */
public class RegolaRaccomandazione implements Regola {

    @Override
    public Optional<Esito> valuta(Fascicolo fascicolo) {
        return fascicolo.trova(LetteraRaccomandazione.class).map(this::esitoPer);
    }

    private Esito esitoPer(LetteraRaccomandazione lettera) {
        if (lettera.isAttendibile()) {
            return new Esito(Destinazione.PARADISO, lettera.getPeso(),
                    "raccomandazione autentica di " + lettera.getFirmatario());
        }
        return new Esito(Destinazione.INFERNO, 2,
                "raccomandazione contraffatta a nome di " + lettera.getFirmatario());
    }

    @Override
    public String descrizione() {
        return "Regola della Raccomandazione";
    }
}
