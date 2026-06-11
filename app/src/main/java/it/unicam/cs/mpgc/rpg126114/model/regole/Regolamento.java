package it.unicam.cs.mpgc.rpg126114.model.regole;

import it.unicam.cs.mpgc.rpg126114.model.documenti.Fascicolo;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Il regolamento in vigore in una giornata: un insieme immutabile di
 * regole, ordinate per anzianita' crescente (l'ultima e' la piu'
 * recente), che stabiliscono la destinazione attesa di un fascicolo.
 *
 * <p>Le regole piu' recenti hanno la precedenza: la regola piu' recente
 * che si applica al caso decide la destinazione; le altre applicabili
 * restano a verbale come pareri non vincolanti. Il motore dipende solo
 * dall'astrazione {@link Regola}: il regolamento non conosce le regole
 * concrete che contiene (principio di inversione delle dipendenze).</p>
 */
public class Regolamento {

    private final List<Regola> regole;

    /**
     * @param regole le regole attive, non null
     * @throws IllegalArgumentException se la lista e' null o contiene null
     */
    public Regolamento(List<Regola> regole) {
        if (regole == null || regole.stream().anyMatch(regola -> regola == null)) {
            throw new IllegalArgumentException("Il regolamento non ammette regole null");
        }
        this.regole = new ArrayList<>(regole);
    }

    /**
     * @return vista non modificabile delle regole attive
     */
    public List<Regola> getRegole() {
        return Collections.unmodifiableList(regole);
    }

    /**
     * Valuta il fascicolo e produce il verbale: tra le regole applicabili
     * decide la piu' recente (la regola di oggi sta sopra a tutte, quella
     * di ieri appena sotto, e cosi' via); le altre restano a verbale come
     * pareri non vincolanti. Se nessuna regola si applica, l'anima va in
     * Purgatorio per smistamento d'ufficio.
     *
     * @param fascicolo il fascicolo a giudizio
     * @return il verbale con destinazione prevista e motivazioni
     * @throws it.unicam.cs.mpgc.rpg126114.model.documenti.PraticaMalformataException
     *         se il fascicolo non e' valido
     */
    public Verbale verbale(Fascicolo fascicolo) {
        fascicolo.valida();
        List<String> motivazioni = new ArrayList<>();
        Destinazione decisione = null;
        for (int i = regole.size() - 1; i >= 0; i--) {
            Regola regola = regole.get(i);
            Optional<Esito> esito = regola.valuta(fascicolo);
            if (esito.isEmpty()) {
                continue;
            }
            String voce = regola.descrizione() + ": " + esito.get().getMotivazione();
            if (decisione == null) {
                decisione = esito.get().getDestinazione();
                motivazioni.add(voce + " (decide: regola piu' recente applicabile)");
            } else {
                motivazioni.add(voce + " (parere non vincolante)");
            }
        }
        if (decisione == null) {
            decisione = Destinazione.PURGATORIO;
            motivazioni.add("Nessuna regola si applica: Purgatorio per smistamento d'ufficio.");
        }
        return new Verbale(decisione, motivazioni);
    }

    /**
     * @param fascicolo il fascicolo a giudizio
     * @return la destinazione prevista dal regolamento
     */
    public Destinazione destinazioneAttesa(Fascicolo fascicolo) {
        return verbale(fascicolo).getDestinazione();
    }

    /**
     * @param fascicolo il fascicolo a giudizio
     * @return una motivazione per ogni regola applicabile, dalla piu' recente
     */
    public List<String> motivazioni(Fascicolo fascicolo) {
        return verbale(fascicolo).getMotivazioni();
    }

    @Override
    public String toString() {
        return "Regolamento con " + regole.size() + " regole attive";
    }
}
