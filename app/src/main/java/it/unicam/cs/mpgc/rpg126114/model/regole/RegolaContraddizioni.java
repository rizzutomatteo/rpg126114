package it.unicam.cs.mpgc.rpg126114.model.regole;

import it.unicam.cs.mpgc.rpg126114.model.anima.Peccato;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Confessione;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Fascicolo;
import it.unicam.cs.mpgc.rpg126114.model.documenti.FedinaKarmica;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Testamento;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;

import java.util.Optional;

/**
 * Smaschera le bugie nel fascicolo: un testamento redatto dopo la morte
 * o una confessione che tace un peccato capitale registrato in fedina
 * sono contraddizioni che condannano.
 */
public class RegolaContraddizioni implements Regola {

    @Override
    public Optional<Esito> valuta(Fascicolo fascicolo) {
        Optional<Esito> testamentoPostumo = controllaTestamento(fascicolo);
        if (testamentoPostumo.isPresent()) {
            return testamentoPostumo;
        }
        return controllaConfessione(fascicolo);
    }

    private Optional<Esito> controllaTestamento(Fascicolo fascicolo) {
        return fascicolo.trova(Testamento.class)
                .filter(t -> t.getAnnoRedazione() > fascicolo.getAnima().getAnnoMorte())
                .map(t -> new Esito(Destinazione.INFERNO, 3,
                        "testamento redatto nel " + t.getAnnoRedazione()
                                + ", dopo la morte del " + fascicolo.getAnima().getAnnoMorte()));
    }

    private Optional<Esito> controllaConfessione(Fascicolo fascicolo) {
        Optional<Confessione> confessione = fascicolo.trova(Confessione.class);
        Optional<FedinaKarmica> fedina = fascicolo.trova(FedinaKarmica.class);
        if (confessione.isEmpty() || fedina.isEmpty()) {
            return Optional.empty();
        }
        Optional<Peccato> capitaleTaciuto = fedina.get().getPeccatiRegistrati().stream()
                .filter(Peccato::isCapitale)
                .filter(confessione.get()::omette)
                .findFirst();
        return capitaleTaciuto.map(peccato -> new Esito(Destinazione.INFERNO, 3,
                "ha taciuto un peccato capitale: " + peccato.getDescrizione()));
    }

    @Override
    public String descrizione() {
        return "Regola delle Contraddizioni";
    }

    @Override
    public String spiegazione() {
        return "Chi mente e' perduto: un testamento redatto dopo la morte o un "
                + "peccato capitale taciuto in confessione condannano all'Inferno.";
    }
}
