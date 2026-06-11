package it.unicam.cs.mpgc.rpg126114.engine;

import it.unicam.cs.mpgc.rpg126114.model.regole.Regola;
import it.unicam.cs.mpgc.rpg126114.model.regole.RegolaAnzianita;
import it.unicam.cs.mpgc.rpg126114.model.regole.RegolaBilancioKarmico;
import it.unicam.cs.mpgc.rpg126114.model.regole.RegolaContraddizioni;
import it.unicam.cs.mpgc.rpg126114.model.regole.RegolaPentimento;
import it.unicam.cs.mpgc.rpg126114.model.regole.RegolaRaccomandazione;
import it.unicam.cs.mpgc.rpg126114.model.regole.Regolamento;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Il calendario dei regolamenti: stabilisce quali regole entrano in
 * vigore in ciascuna giornata di carriera.
 *
 * <p>E' separato dal {@link GiornataService} perche' configurare il
 * regolamento e orchestrare la giornata sono responsabilita' diverse:
 * per aggiungere o spostare una regola si tocca solo questa classe, e
 * per cambiare l'intera progressione basta iniettare nel servizio un
 * calendario differente.</p>
 */
public class CalendarioRegolamenti {

    public static final int SOGLIA_PARADISO = 5;
    public static final int SOGLIA_INFERNO = -5;
    public static final int ANNO_ANIME_ANTICHE = 1700;

    /**
     * Il regolamento si arricchisce con il passare delle giornate: ogni
     * giorno di carriera introduce un nuovo criterio di giudizio.
     *
     * @param numeroGiornata il numero della giornata, almeno 1
     * @return il regolamento in vigore quel giorno
     * @throws IllegalArgumentException se il numero non e' valido
     */
    public Regolamento regolamentoPer(int numeroGiornata) {
        if (numeroGiornata < 1) {
            throw new IllegalArgumentException("Numero di giornata non valido: " + numeroGiornata);
        }
        List<Regola> regole = new ArrayList<>();
        regole.add(new RegolaBilancioKarmico(SOGLIA_PARADISO, SOGLIA_INFERNO));
        if (numeroGiornata >= 2) {
            regole.add(new RegolaContraddizioni());
        }
        if (numeroGiornata >= 3) {
            regole.add(new RegolaRaccomandazione());
        }
        if (numeroGiornata >= 4) {
            regole.add(new RegolaAnzianita(ANNO_ANIME_ANTICHE));
        }
        if (numeroGiornata >= 5) {
            regole.add(new RegolaPentimento());
        }
        return new Regolamento(regole);
    }

    /**
     * La regola che entra in vigore nella giornata indicata: per la
     * prima giornata e' la regola di base, per le successive l'eventuale
     * regola aggiunta rispetto al giorno prima. Alimenta le "circolari
     * di servizio" mostrate al giocatore.
     *
     * @param numeroGiornata il numero della giornata, almeno 1
     * @return la regola introdotta quel giorno, se ce n'e' una
     * @throws IllegalArgumentException se il numero non e' valido
     */
    public Optional<Regola> nuovaRegolaPer(int numeroGiornata) {
        List<Regola> regoleDiOggi = regolamentoPer(numeroGiornata).getRegole();
        if (numeroGiornata == 1) {
            return Optional.of(regoleDiOggi.get(regoleDiOggi.size() - 1));
        }
        List<Regola> regoleDiIeri = regolamentoPer(numeroGiornata - 1).getRegole();
        if (regoleDiOggi.size() > regoleDiIeri.size()) {
            return Optional.of(regoleDiOggi.get(regoleDiOggi.size() - 1));
        }
        return Optional.empty();
    }
}
