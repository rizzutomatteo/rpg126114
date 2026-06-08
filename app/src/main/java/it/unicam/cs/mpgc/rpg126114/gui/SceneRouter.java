package it.unicam.cs.mpgc.rpg126114.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Gestisce la navigazione tra le scene FXML dell'applicazione.
 *
 * <p>Ogni controller viene istanziato dalla factory passando il
 * {@link ContestoGioco} e il router stesso: la vista resta dichiarativa
 * negli FXML e i controller ricevono le dipendenze dal costruttore.</p>
 */
public class SceneRouter {

    private static final double LARGHEZZA = 1000;
    private static final double ALTEZZA = 660;

    private final Stage stage;
    private final ContestoGioco contesto;

    /**
     * @param stage    lo stage principale dell'applicazione, non null
     * @param contesto il contesto di gioco condiviso, non null
     * @throws IllegalArgumentException se i parametri sono null
     */
    public SceneRouter(Stage stage, ContestoGioco contesto) {
        if (stage == null || contesto == null) {
            throw new IllegalArgumentException("Stage e contesto sono obbligatori");
        }
        this.stage = stage;
        this.contesto = contesto;
    }

    /**
     * Carica la scena FXML indicata e la mostra sullo stage.
     *
     * @param nomeScena il nome del file FXML senza estensione (es. "menu")
     * @throws IllegalStateException se la scena non puo' essere caricata
     */
    public void vai(String nomeScena) {
        try {
            FXMLLoader caricatore = new FXMLLoader(
                    getClass().getResource("/fxml/" + nomeScena + ".fxml"));
            caricatore.setControllerFactory(this::creaController);
            Parent radice = caricatore.load();
            Scene scena = new Scene(radice, LARGHEZZA, ALTEZZA);
            scena.getStylesheets().add(
                    getClass().getResource("/css/application.css").toExternalForm());
            stage.setScene(scena);
        } catch (IOException errore) {
            throw new IllegalStateException("Impossibile caricare la scena: " + nomeScena, errore);
        }
    }

    private Object creaController(Class<?> classe) {
        try {
            return classe.getConstructor(ContestoGioco.class, SceneRouter.class)
                    .newInstance(contesto, this);
        } catch (ReflectiveOperationException errore) {
            throw new IllegalStateException(
                    "Controller non istanziabile: " + classe.getName(), errore);
        }
    }
}
