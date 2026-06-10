package it.unicam.cs.mpgc.rpg126114.model.regole;

import it.unicam.cs.mpgc.rpg126114.model.anima.Peccato;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Confessione;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Fascicolo;
import it.unicam.cs.mpgc.rpg126114.model.documenti.FedinaKarmica;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;

import java.util.Optional;

/**
 * Premia il pentimento sincero: chi confessa qualcosa e non tace nessun
 * peccato capitale merita la possibilita' di purificarsi in Purgatorio.
 */
public class RegolaPentimento implements Regola {

    @Override
    public Optional<Esito> valuta(Fascicolo fascicolo) {
        Optional<Confessione> confessione = fascicolo.trova(Confessione.class);
        Optional<FedinaKarmica> fedina = fascicolo.trova(FedinaKarmica.class);
        if (confessione.isEmpty() || fedina.isEmpty()) {
            return Optional.empty();
        }
        if (confessione.get().getPeccatiConfessati().isEmpty()) {
            return Optional.empty();
        }
        boolean sincera = fedina.get().getPeccatiRegistrati().stream()
                .filter(Peccato::isCapitale)
                .noneMatch(confessione.get()::omette);
        if (!sincera) {
            return Optional.empty();
        }
        return Optional.of(new Esito(Destinazione.PURGATORIO, 2,
                "confessione sincera, nessun peccato capitale taciuto"));
    }

    @Override
    public String descrizione() {
        return "Regola del Pentimento";
    }

    @Override
    public String spiegazione() {
        return "Chi confessa qualcosa senza tacere alcun peccato capitale mostra "
                + "pentimento sincero e merita la purificazione del Purgatorio.";
    }
}
