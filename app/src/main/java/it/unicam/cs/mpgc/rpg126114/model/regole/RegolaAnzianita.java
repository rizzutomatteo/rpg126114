package it.unicam.cs.mpgc.rpg126114.model.regole;

import it.unicam.cs.mpgc.rpg126114.model.documenti.Fascicolo;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;

import java.util.Optional;

/**
 * Le anime morte prima dell'anno limite hanno atteso troppo a lungo per
 * essere giudicate con i criteri moderni: vanno smistate al Limbo.
 */
public class RegolaAnzianita implements Regola {

    private final int annoLimite;

    /**
     * @param annoLimite anno prima del quale un'anima e' considerata antica
     */
    public RegolaAnzianita(int annoLimite) {
        this.annoLimite = annoLimite;
    }

    @Override
    public Optional<Esito> valuta(Fascicolo fascicolo) {
        int annoMorte = fascicolo.getAnima().getAnnoMorte();
        if (annoMorte >= annoLimite) {
            return Optional.empty();
        }
        return Optional.of(new Esito(Destinazione.LIMBO,
                "in attesa dal " + annoMorte + ", prima dell'anno limite " + annoLimite));
    }

    @Override
    public String descrizione() {
        return "Regola dell'Anzianita'";
    }

    @Override
    public String spiegazione() {
        return "Morto prima del " + annoLimite + ": va al Limbo, "
                + "qualunque cosa dica il resto.";
    }
}
