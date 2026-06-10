package it.unicam.cs.mpgc.rpg126114.gui.controller;

import it.unicam.cs.mpgc.rpg126114.engine.GiornataService;
import it.unicam.cs.mpgc.rpg126114.engine.ValutatoreVerdetti;
import it.unicam.cs.mpgc.rpg126114.gui.ContestoGioco;
import it.unicam.cs.mpgc.rpg126114.gui.SceneRouter;
import it.unicam.cs.mpgc.rpg126114.model.carriera.Funzionario;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Timbro;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * Controller del Manuale del Funzionario: la guida in gioco che spiega
 * turno, procedure e carriera. I numeri sono presi dalle costanti del
 * motore, cosi' il manuale non puo' mentire al giocatore.
 */
public class ManualeController {

    private final SceneRouter router;

    @FXML
    private TextArea areaManuale;

    public ManualeController(ContestoGioco contesto, SceneRouter router) {
        this.router = router;
    }

    @FXML
    private void initialize() {
        areaManuale.setText(testoManuale());
    }

    @FXML
    private void onIndietro() {
        router.vai("menu");
    }

    private String testoManuale() {
        StringBuilder testo = new StringBuilder();
        testo.append("IL TURNO ALLO SPORTELLO\n");
        testo.append("Le anime arrivano in coda mentre lavori, una alla volta. ");
        testo.append("Esamina il fascicolo, concedi colloqui se servono, scegli ");
        testo.append("il timbro ed emetti il verdetto. La giornata si chiude ");
        testo.append("quando tutti i casi previsti sono stati risolti.\n\n");

        testo.append("IL FASCICOLO\n");
        testo.append("La Fedina Karmica, sempre presente e d'ufficio, riporta ");
        testo.append("virtu', peccati e bilancio karmico. Possono seguire ");
        testo.append("Testamento, Confessione e Lettera di Raccomandazione: ");
        testo.append("solo le lettere possono avere firme contraffatte (fonte ");
        testo.append("dubbia), ma anche i contenuti degli altri documenti ");
        testo.append("possono tradire chi mente.\n\n");

        testo.append("I COLLOQUI\n");
        testo.append("Hai ").append(Funzionario.COLLOQUI_BASE);
        testo.append(" colloquio al giorno, piu' uno per ogni punto di ");
        testo.append("Pazienza. Le anime erranti ricordano al piu' una ");
        testo.append("dichiarazione.\n\n");

        testo.append("GLI IMPOSTORI\n");
        testo.append("L'archivio ricorda ogni anima gia' giudicata: se la ");
        testo.append("stessa persona si ripresenta, denunciala (+");
        testo.append(GiornataService.KARMA_DENUNCIA_CORRETTA).append(" karma). ");
        testo.append("Una denuncia infondata costa ");
        testo.append(GiornataService.PENALE_DENUNCIA_ERRATA);
        testo.append("; giudicare un impostore senza accorgersene costa ");
        testo.append(GiornataService.PENALE_IMPOSTORE_GIUDICATO);
        testo.append(" e la pratica viene respinta. In dubbio, consulta ");
        testo.append("l'archivio direttamente dalla scrivania.\n\n");

        testo.append("VERDETTI E TIMBRI\n");
        testo.append("Il pannello del regolamento, sulla scrivania, elenca le ");
        testo.append("regole del giorno: ogni regola applicabile vota con un ");
        testo.append("peso e vince il peso totale piu' alto (a parita', la ");
        testo.append("destinazione piu' severa). Un verdetto conforme vale ");
        testo.append(ValutatoreVerdetti.KARMA_BASE).append(" karma piu' ");
        testo.append(ValutatoreVerdetti.PREMIO_INTUITO);
        testo.append(" per ogni punto di Intuito; un errore vale -");
        testo.append(ValutatoreVerdetti.KARMA_BASE).append(". I timbri ");
        testo.append("moltiplicano il karma nel bene e nel male:\n");
        for (Timbro timbro : Timbro.values()) {
            testo.append("- ").append(timbro.getEtichetta()).append(": karma x");
            testo.append(timbro.getMoltiplicatore()).append(", dal livello ");
            testo.append(timbro.getLivelloRichiesto()).append('\n');
        }
        testo.append('\n');

        testo.append("LA CARRIERA\n");
        testo.append("Ogni ").append(Funzionario.KARMA_PER_LIVELLO);
        testo.append(" karma scatta una promozione: +1 livello e +1 punto ");
        testo.append("abilita' da spendere a fine giornata su Intuito o ");
        testo.append("Pazienza. Se il karma scende a ");
        testo.append(Funzionario.SOGLIA_LICENZIAMENTO);
        testo.append(" vieni licenziato e la carriera riprende dall'ultimo ");
        testo.append("salvataggio. Quando le anime negli archivi finiscono, ");
        testo.append("ti aspetta il pensionamento con onore.\n\n");

        testo.append("IL SALVATAGGIO\n");
        testo.append("La carriera si salva da sola alla chiusura di ogni ");
        testo.append("giornata (e quando spendi punti abilita'). Uscire a ");
        testo.append("meta' giornata perde i progressi del giorno in corso.");
        return testo.toString();
    }
}
