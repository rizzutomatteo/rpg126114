package it.unicam.cs.mpgc.rpg126114.gui.controller;

import it.unicam.cs.mpgc.rpg126114.engine.ReportGiornata;
import it.unicam.cs.mpgc.rpg126114.engine.ValutatoreVerdetti;
import it.unicam.cs.mpgc.rpg126114.gui.ContestoGioco;
import it.unicam.cs.mpgc.rpg126114.gui.SceneRouter;
import it.unicam.cs.mpgc.rpg126114.model.carriera.Funzionario;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * Controller del rendiconto di fine giornata: mostra il report, lo stato
 * del Funzionario e permette di spendere i punti abilita' guadagnati.
 */
public class ReportController {

    private final ContestoGioco contesto;
    private final SceneRouter router;

    @FXML
    private Label lblFunzionario;
    @FXML
    private Label lblCircolare;
    @FXML
    private Label lblPunti;
    @FXML
    private Label lblSpiegazioneAbilita;
    @FXML
    private TextArea areaReport;
    @FXML
    private Button btnIntuito;
    @FXML
    private Button btnPazienza;

    public ReportController(ContestoGioco contesto, SceneRouter router) {
        this.contesto = contesto;
        this.router = router;
    }

    @FXML
    private void initialize() {
        ReportGiornata report = contesto.getUltimoReport();
        if (report != null) {
            areaReport.setText(report.riepilogo());
        }
        contesto.getServizio().nuovaRegolaPer(contesto.getPartita().getGiornataCorrente())
                .ifPresentOrElse(
                        regola -> lblCircolare.setText("Circolare: da domani entra in vigore "
                                + regola.descrizione() + " - " + regola.spiegazione()),
                        () -> lblCircolare.setText(""));
        int rimaste = contesto.getServizio().animeFrescheRimaste();
        areaReport.appendText("\n\nAnime ancora in attesa di giudizio negli archivi: "
                + rimaste + (rimaste == 0 ? " - domani scatta il pensionamento!" : "."));
        lblSpiegazioneAbilita.setText("Intuito: +" + ValutatoreVerdetti.PREMIO_INTUITO
                + " karma su ogni verdetto conforme. Pazienza: +1 colloquio al giorno. "
                + "Una promozione ogni " + Funzionario.KARMA_PER_LIVELLO
                + " karma frutta un punto abilita'.");
        aggiornaStato();
    }

    @FXML
    private void onPotenziaIntuito() {
        contesto.getPartita().getFunzionario().potenziaIntuito();
        contesto.salva();
        aggiornaStato();
    }

    @FXML
    private void onPotenziaPazienza() {
        contesto.getPartita().getFunzionario().potenziaPazienza();
        contesto.salva();
        aggiornaStato();
    }

    @FXML
    private void onProssimaGiornata() {
        if (contesto.getServizio().animeFrescheRimaste() == 0) {
            mostraPensionamento();
            return;
        }
        router.vai("scrivania");
    }

    private void mostraPensionamento() {
        Alert finestra = new Alert(Alert.AlertType.INFORMATION);
        finestra.setHeaderText("Pensionamento con onore");
        finestra.setContentText("Non ci sono piu' anime da giudicare: il Funzionario "
                + contesto.getPartita().getFunzionario().getNome()
                + " ha meritato il riposo eterno (quello buono).");
        finestra.showAndWait();
        router.vai("menu");
    }

    @FXML
    private void onArchivio() {
        router.vai("archivio");
    }

    @FXML
    private void onMenu() {
        router.vai("menu");
    }

    private void aggiornaStato() {
        Funzionario funzionario = contesto.getPartita().getFunzionario();
        lblFunzionario.setText(funzionario.getNome()
                + " - Livello " + funzionario.getLivello()
                + " - Karma " + funzionario.getKarma()
                + " - Intuito " + funzionario.getIntuito()
                + " - Pazienza " + funzionario.getPazienza());
        lblPunti.setText("Punti abilita' da spendere: " + funzionario.getPuntiAbilita());
        boolean senzaPunti = funzionario.getPuntiAbilita() <= 0;
        btnIntuito.setDisable(senzaPunti);
        btnPazienza.setDisable(senzaPunti);
    }
}
