package it.unicam.cs.mpgc.rpg126114.gui.controller;

import it.unicam.cs.mpgc.rpg126114.gui.ContestoGioco;
import it.unicam.cs.mpgc.rpg126114.gui.SceneRouter;
import it.unicam.cs.mpgc.rpg126114.persistence.PersistenzaException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller del menu principale: avvia una nuova carriera, riprende
 * quella salvata o chiude l'applicazione.
 */
public class MenuController {

    private static final String NOME_PREDEFINITO = "Funzionario Senzanome";

    private final ContestoGioco contesto;
    private final SceneRouter router;

    @FXML
    private TextField campoNome;
    @FXML
    private Button btnCarica;
    @FXML
    private Label lblSalvataggio;

    public MenuController(ContestoGioco contesto, SceneRouter router) {
        this.contesto = contesto;
        this.router = router;
    }

    @FXML
    private void initialize() {
        btnCarica.setDisable(!contesto.esisteSalvataggio());
        lblSalvataggio.setText(contesto.descrizioneSalvataggio().orElse(""));
    }

    @FXML
    private void onManuale() {
        router.vai("manuale");
    }

    @FXML
    private void onNuovaPartita() {
        String nome = campoNome.getText();
        if (nome == null || nome.isBlank()) {
            nome = NOME_PREDEFINITO;
        }
        contesto.nuovaPartita(nome.trim());
        router.vai("scrivania");
    }

    @FXML
    private void onCaricaPartita() {
        try {
            if (contesto.caricaPartita()) {
                router.vai("scrivania");
            }
        } catch (PersistenzaException errore) {
            Alert avviso = new Alert(Alert.AlertType.ERROR);
            avviso.setHeaderText("Salvataggio illeggibile");
            avviso.setContentText(errore.getMessage());
            avviso.showAndWait();
        }
    }

    @FXML
    private void onEsci() {
        Platform.exit();
    }
}
