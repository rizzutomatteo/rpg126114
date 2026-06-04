package it.unicam.cs.mpgc.rpg126114.model.regole;

import it.unicam.cs.mpgc.rpg126114.model.documenti.Fascicolo;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Il regolamento in vigore in una giornata: un insieme immutabile di
 * regole i cui esiti vengono combinati per stabilire la destinazione
 * attesa di un fascicolo.
 *
 * <p>Il motore dipende solo dall'astrazione {@link Regola}: il regolamento
 * non conosce le regole concrete che contiene (principio di inversione
 * delle dipendenze).</p>
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
     * Combina gli esiti delle regole applicabili: vince la destinazione
     * con il peso totale piu' alto; a parita' di peso prevale la piu'
     * severa. Se nessuna regola si applica, l'anima va in Purgatorio
     * per smistamento d'ufficio.
     *
     * @param fascicolo il fascicolo a giudizio
     * @return la destinazione prevista dal regolamento
     * @throws it.unicam.cs.mpgc.rpg126114.model.documenti.PraticaMalformataException
     *         se il fascicolo non e' valido
     */
    public Destinazione destinazioneAttesa(Fascicolo fascicolo) {
        fascicolo.valida();
        Map<Destinazione, Integer> pesiPerDestinazione = regole.stream()
                .map(regola -> regola.valuta(fascicolo))
                .flatMap(Optional::stream)
                .collect(Collectors.groupingBy(Esito::getDestinazione,
                        Collectors.summingInt(Esito::getPeso)));
        return pesiPerDestinazione.entrySet().stream()
                .max(Comparator
                        .comparingInt((Map.Entry<Destinazione, Integer> voce) -> voce.getValue())
                        .thenComparing(voce -> voce.getKey().ordinal()))
                .map(Map.Entry::getKey)
                .orElse(Destinazione.PURGATORIO);
    }

    /**
     * Le motivazioni a verbale di tutte le regole applicabili al fascicolo.
     *
     * @param fascicolo il fascicolo a giudizio
     * @return una motivazione per ogni regola applicabile, in ordine di regola
     */
    public List<String> motivazioni(Fascicolo fascicolo) {
        fascicolo.valida();
        return regole.stream()
                .map(regola -> regola.valuta(fascicolo)
                        .map(esito -> regola.descrizione() + ": " + esito.getMotivazione()))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Regolamento con " + regole.size() + " regole attive";
    }
}
