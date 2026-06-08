package it.unicam.cs.mpgc.rpg126114.gui.controller;

import it.unicam.cs.mpgc.rpg126114.engine.EsitoValutazione;
import it.unicam.cs.mpgc.rpg126114.engine.GiornataService;
import it.unicam.cs.mpgc.rpg126114.gui.ContestoGioco;
import it.unicam.cs.mpgc.rpg126114.gui.SceneRouter;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Documento;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Fascicolo;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Timbro;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.util.StringConverter;

import java.util.Optional;

/**
 * Controller della scrivania: la schermata principale dove il
 * Funzionario esamina il fascicolo, concede colloqui, denuncia gli
 * impostori ed emette i verdetti.
 *
 * <p>Il controller non contiene logica di gioco: traduce gli eventi
 * dell'interfaccia in chiamate al {@link GiornataService} e ne mostra
 * i risultati.</p>
 */
public class ScrivaniaController {

    private final ContestoGioco contesto;
    private final SceneRouter router;

    @FXML
    private Label lblGiornata;
    @FXML
    private Label lblKarma;
    @FXML
    private Label lblLivello;
    @FXML
    private Label lblRimanenti;
    @FXML
    private Label lblColloqui;
    @FXML
    private ListView<String> listaDocumenti;
    @FXML
    private TextArea areaDocumento;
    @FXML
    private TextArea areaColloquio;
    @FXML
    private ComboBox<Timbro> comboTimbro;
    @FXML
    private Button btnColloquio;

    public ScrivaniaController(ContestoGioco contesto, SceneRouter router) {
        this.contesto = contesto;
        this.router = router;
    }

    @FXML
    private void initialize() {
        comboTimbro.setConverter(new StringConverter<>() {
            @Override
            public String toString(Timbro timbro) {
                return timbro == null ? "" : timbro.getEtichetta();
            }

            @Override
            public Timbro fromString(String testo) {
                return null;
            }
        });
        listaDocumenti.getSelectionModel().selectedIndexProperty()
                .addListener((osservato, vecchio, nuovo) -> mostraDocumento(nuovo.intValue()));

        GiornataService servizio = contesto.getServizio();
        if (servizio.getGiornata() == null) {
            if (!servizio.avviaGiornata()) {
                mostraMessaggio(Alert.AlertType.INFORMATION, "Pensionamento con onore",
                        "Non ci sono piu' anime da giudicare: il Funzionario "
                                + contesto.getPartita().getFunzionario().getNome()
                                + " ha meritato il riposo eterno (quello buono).");
                router.vai("menu");
                return;
            }
        }
        if (servizio.getFascicoloCorrente() == null && !servizio.isGiornataConclusa()) {
            servizio.prossimaPratica();
        }
        aggiorna();
    }

    @FXML
    private void onColloquio() {
        Optional<String> dichiarazione = contesto.getServizio().colloquio();
        if (dichiarazione.isPresent()) {
            areaColloquio.appendText("\n\n— " + dichiarazione.get());
        } else {
            areaColloquio.appendText("\n\n(L'anima tace: colloqui o ricordi esauriti.)");
        }
        aggiornaBarraDiStato();
    }

    @FXML
    private void onDenuncia() {
        GiornataService servizio = contesto.getServizio();
        boolean fondata = servizio.denunciaImpostore();
        if (fondata) {
            mostraMessaggio(Alert.AlertType.INFORMATION, "Impostore smascherato!",
                    "L'archivio conferma: quest'anima era gia' stata giudicata.\nKarma +"
                            + GiornataService.KARMA_DENUNCIA_CORRETTA);
            dopoCasoChiuso();
        } else {
            mostraMessaggio(Alert.AlertType.WARNING, "Denuncia infondata",
                    "L'archivio non ha riscontri: l'anima e' nuova.\nKarma "
                            + GiornataService.PENALE_DENUNCIA_ERRATA);
            aggiornaBarraDiStato();
        }
    }

    @FXML
    private void onParadiso() {
        emetti(Destinazione.PARADISO);
    }

    @FXML
    private void onPurgatorio() {
        emetti(Destinazione.PURGATORIO);
    }

    @FXML
    private void onLimbo() {
        emetti(Destinazione.LIMBO);
    }

    @FXML
    private void onInferno() {
        emetti(Destinazione.INFERNO);
    }

    private void emetti(Destinazione destinazione) {
        try {
            Timbro timbro = comboTimbro.getValue() == null
                    ? Timbro.ORDINANZA
                    : comboTimbro.getValue();
            EsitoValutazione esito =
                    contesto.getServizio().emettiVerdetto(destinazione, timbro);
            mostraEsito(destinazione, esito);
            dopoCasoChiuso();
        } catch (IllegalStateException | IllegalArgumentException errore) {
            mostraMessaggio(Alert.AlertType.WARNING, "Operazione non consentita",
                    errore.getMessage());
        }
    }

    private void dopoCasoChiuso() {
        GiornataService servizio = contesto.getServizio();
        if (contesto.getPartita().isFinita()) {
            mostraMessaggio(Alert.AlertType.ERROR, "LICENZIATO",
                    "Il karma e' precipitato: la Direzione ti solleva dall'incarico.\n"
                            + "La carriera riprende dall'ultimo salvataggio.");
            router.vai("menu");
            return;
        }
        if (servizio.isGiornataConclusa()) {
            contesto.chiudiGiornataESalva();
            router.vai("report");
            return;
        }
        servizio.prossimaPratica();
        aggiorna();
    }

    private void mostraEsito(Destinazione scelta, EsitoValutazione esito) {
        Alert finestra = new Alert(esito.isCorretto()
                ? Alert.AlertType.INFORMATION
                : Alert.AlertType.WARNING);
        finestra.setHeaderText(esito.isCorretto()
                ? "Verdetto conforme al regolamento"
                : "Verdetto difforme dal regolamento");
        StringBuilder testo = new StringBuilder();
        testo.append("Hai scelto: ").append(scelta.getEtichetta());
        testo.append("\nIl regolamento prevedeva: ")
                .append(esito.getDestinazioneAttesa().getEtichetta());
        testo.append("\nKarma: ").append(esito.getDeltaKarma() >= 0 ? "+" : "")
                .append(esito.getDeltaKarma());
        testo.append("\n\nMotivazioni a verbale:");
        for (String motivazione : esito.getMotivazioni()) {
            testo.append("\n- ").append(motivazione);
        }
        finestra.setContentText(testo.toString());
        finestra.showAndWait();
    }

    private void aggiorna() {
        Fascicolo fascicolo = contesto.getServizio().getFascicoloCorrente();
        if (fascicolo == null) {
            return;
        }
        areaColloquio.setText(fascicolo.getAnima().presentazione());
        listaDocumenti.getItems().setAll(fascicolo.getDocumenti().stream()
                .map(this::etichettaDocumento)
                .toList());
        listaDocumenti.getSelectionModel().selectFirst();
        comboTimbro.getItems().setAll(
                contesto.getPartita().getFunzionario().timbriDisponibili());
        comboTimbro.getSelectionModel().selectFirst();
        aggiornaBarraDiStato();
    }

    private String etichettaDocumento(Documento documento) {
        return documento.isAttendibile()
                ? documento.getIntestazione()
                : documento.getIntestazione() + " (fonte dubbia)";
    }

    private void mostraDocumento(int indice) {
        Fascicolo fascicolo = contesto.getServizio().getFascicoloCorrente();
        if (fascicolo == null || indice < 0 || indice >= fascicolo.getDocumenti().size()) {
            areaDocumento.clear();
            return;
        }
        areaDocumento.setText(fascicolo.getDocumenti().get(indice).contenuto());
    }

    private void aggiornaBarraDiStato() {
        GiornataService servizio = contesto.getServizio();
        lblGiornata.setText("Giornata " + servizio.getGiornata().getNumero());
        lblKarma.setText("Karma: " + contesto.getPartita().getFunzionario().getKarma());
        lblLivello.setText("Livello: " + contesto.getPartita().getFunzionario().getLivello());
        lblRimanenti.setText("In attesa: " + servizio.getGiornata().animeRimanenti());
        lblColloqui.setText("Colloqui: " + servizio.getColloquiRimasti());
        btnColloquio.setDisable(servizio.getColloquiRimasti() <= 0);
    }

    private void mostraMessaggio(Alert.AlertType tipo, String intestazione, String testo) {
        Alert finestra = new Alert(tipo);
        finestra.setHeaderText(intestazione);
        finestra.setContentText(testo);
        finestra.showAndWait();
    }
}
