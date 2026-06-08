package it.unicam.cs.mpgc.rpg126114.gui.controller;

import it.unicam.cs.mpgc.rpg126114.gui.ContestoGioco;
import it.unicam.cs.mpgc.rpg126114.gui.SceneRouter;
import it.unicam.cs.mpgc.rpg126114.model.carriera.ArchivioAnime;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Verdetto;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.stream.Collectors;

/**
 * Controller dell'archivio: consultazione e ricerca dei verdetti emessi
 * nella carriera, con il conteggio degli smistamenti per destinazione.
 */
public class ArchivioController {

    private final ContestoGioco contesto;
    private final SceneRouter router;

    @FXML
    private TextField campoFiltro;
    @FXML
    private Label lblConteggi;
    @FXML
    private ListView<String> listaVerdetti;

    public ArchivioController(ContestoGioco contesto, SceneRouter router) {
        this.contesto = contesto;
        this.router = router;
    }

    @FXML
    private void initialize() {
        campoFiltro.textProperty()
                .addListener((osservato, vecchio, nuovo) -> aggiornaLista(nuovo));
        aggiornaLista("");
        aggiornaConteggi();
    }

    @FXML
    private void onIndietro() {
        router.vai("report");
    }

    private void aggiornaLista(String filtro) {
        listaVerdetti.getItems().setAll(
                contesto.getPartita().getArchivio().cercaPerNome(filtro).stream()
                        .map(Verdetto::toString)
                        .toList());
    }

    private void aggiornaConteggi() {
        ArchivioAnime archivio = contesto.getPartita().getArchivio();
        String conteggi = archivio.conteggioPerDestinazione().entrySet().stream()
                .map(voce -> voce.getKey().getEtichetta() + ": " + voce.getValue())
                .collect(Collectors.joining("   "));
        lblConteggi.setText(conteggi.isEmpty()
                ? "Nessun verdetto in archivio"
                : conteggi);
    }
}
