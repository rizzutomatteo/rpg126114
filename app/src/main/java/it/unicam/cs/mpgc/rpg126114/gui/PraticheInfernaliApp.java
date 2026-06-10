package it.unicam.cs.mpgc.rpg126114.gui;

import it.unicam.cs.mpgc.rpg126114.persistence.CaricatorePool;
import it.unicam.cs.mpgc.rpg126114.persistence.GsonRepositoryPartita;
import it.unicam.cs.mpgc.rpg126114.persistence.RepositoryPartita;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * Punto di ingresso JavaFX di Pratiche Infernali.
 *
 * <p>Qui vengono composte le dipendenze concrete (repository JSON e pool
 * delle anime dalle risorse) e consegnate al {@link ContestoGioco}: e'
 * l'unico punto dell'applicazione che conosce le implementazioni.</p>
 */
public class PraticheInfernaliApp extends Application {

    @Override
    public void start(Stage stage) {
        RepositoryPartita repository =
                new GsonRepositoryPartita(ContestoGioco.FILE_SALVATAGGIO_PREDEFINITO);
        ContestoGioco contesto =
                new ContestoGioco(repository, new CaricatorePool().caricaAnime());
        SceneRouter router = new SceneRouter(stage, contesto);

        stage.setTitle("Pratiche Infernali - Ufficio Smistamento Anime");
        stage.setOnCloseRequest(evento -> {
            if (!contesto.isGiornataInCorso()) {
                return;
            }
            Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
            conferma.setHeaderText("Giornata in corso");
            conferma.setContentText("I progressi della giornata non sono salvati "
                    + "e andranno persi. Uscire comunque?");
            if (conferma.showAndWait().filter(scelta -> scelta == ButtonType.OK).isEmpty()) {
                evento.consume();
            }
        });
        router.vai("menu");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
