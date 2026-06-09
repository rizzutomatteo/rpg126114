package it.unicam.cs.mpgc.rpg126114.gui;

import it.unicam.cs.mpgc.rpg126114.engine.CodaArrivi;
import it.unicam.cs.mpgc.rpg126114.engine.GeneratoreAnime;
import it.unicam.cs.mpgc.rpg126114.engine.GeneratoreFascicoli;
import it.unicam.cs.mpgc.rpg126114.engine.GiornataService;
import it.unicam.cs.mpgc.rpg126114.engine.ReportGiornata;
import it.unicam.cs.mpgc.rpg126114.engine.ValutatoreVerdetti;
import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;
import it.unicam.cs.mpgc.rpg126114.model.carriera.Funzionario;
import it.unicam.cs.mpgc.rpg126114.model.carriera.Partita;
import it.unicam.cs.mpgc.rpg126114.persistence.RepositoryPartita;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * La facciata che la GUI usa per parlare con il resto dell'applicazione:
 * crea o carica la partita, costruisce il {@link GiornataService} con i
 * suoi collaboratori e coordina i salvataggi.
 *
 * <p>I controller JavaFX dipendono solo da questa classe e dal servizio:
 * non conoscono regole, generatori o dettagli di persistenza.</p>
 */
public class ContestoGioco {

    /** Percorso predefinito del salvataggio nella home dell'utente. */
    public static final Path FILE_SALVATAGGIO_PREDEFINITO =
            Path.of(System.getProperty("user.home"), ".pratiche-infernali", "salvataggio.json");

    private final RepositoryPartita repository;
    private final List<Anima> poolAnime;

    private Partita partita;
    private GiornataService servizio;
    private ReportGiornata ultimoReport;

    /**
     * @param repository il repository dei salvataggi, non null
     * @param poolAnime  il pool delle anime di gioco, non vuoto
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    public ContestoGioco(RepositoryPartita repository, List<Anima> poolAnime) {
        if (repository == null) {
            throw new IllegalArgumentException("Il repository dei salvataggi e' obbligatorio");
        }
        if (poolAnime == null || poolAnime.isEmpty()) {
            throw new IllegalArgumentException("Il pool delle anime non puo' essere vuoto");
        }
        this.repository = repository;
        this.poolAnime = new ArrayList<>(poolAnime);
    }

    /**
     * Inizia una nuova carriera.
     *
     * @param nomeFunzionario il nome scelto dal giocatore, non vuoto
     */
    public void nuovaPartita(String nomeFunzionario) {
        partita = new Partita(new Funzionario(nomeFunzionario));
        creaServizio();
    }

    /**
     * Riprende la carriera salvata, se esiste.
     *
     * @return true se una partita e' stata caricata
     * @throws it.unicam.cs.mpgc.rpg126114.persistence.PersistenzaException
     *         se il salvataggio esiste ma e' illeggibile
     */
    public boolean caricaPartita() {
        return repository.carica()
                .map(caricata -> {
                    partita = caricata;
                    creaServizio();
                    return true;
                })
                .orElse(false);
    }

    private void creaServizio() {
        servizio = new GiornataService(partita,
                new GeneratoreAnime(poolAnime, new Random(),
                        GeneratoreAnime.PROBABILITA_IMPOSTORE_PREDEFINITA),
                new GeneratoreFascicoli(new Random()),
                new ValutatoreVerdetti(),
                new CodaArrivi());
        ultimoReport = null;
    }

    /**
     * Salva lo stato attuale della carriera.
     *
     * @throws IllegalStateException se non c'e' una partita in corso
     */
    public void salva() {
        richiediPartita();
        repository.salva(partita);
    }

    /**
     * Chiude la giornata, salva e conserva il report per la schermata
     * di fine turno.
     *
     * @return il report della giornata conclusa
     */
    public ReportGiornata chiudiGiornataESalva() {
        richiediPartita();
        ultimoReport = servizio.chiudiGiornata();
        repository.salva(partita);
        return ultimoReport;
    }

    public boolean esisteSalvataggio() {
        return repository.esisteSalvataggio();
    }

    /**
     * @return il servizio della giornata in corso
     * @throws IllegalStateException se non c'e' una partita in corso
     */
    public GiornataService getServizio() {
        richiediPartita();
        return servizio;
    }

    /**
     * @return la partita in corso
     * @throws IllegalStateException se non c'e' una partita in corso
     */
    public Partita getPartita() {
        richiediPartita();
        return partita;
    }

    /**
     * @return il report dell'ultima giornata chiusa, o null se non ancora disponibile
     */
    public ReportGiornata getUltimoReport() {
        return ultimoReport;
    }

    private void richiediPartita() {
        if (partita == null) {
            throw new IllegalStateException("Nessuna partita in corso");
        }
    }
}
