package it.unicam.cs.mpgc.rpg126114.gui.controller;

import it.unicam.cs.mpgc.rpg126114.engine.ReportGiornata;
import it.unicam.cs.mpgc.rpg126114.gui.ContestoGioco;
import it.unicam.cs.mpgc.rpg126114.gui.SceneRouter;
import it.unicam.cs.mpgc.rpg126114.model.carriera.Funzionario;
import javafx.fxml.FXML;
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
    private Label lblPunti;
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
        router.vai("scrivania");
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
