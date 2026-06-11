package it.unicam.cs.mpgc.rpg126114.gui.controller;

import it.unicam.cs.mpgc.rpg126114.engine.EsitoValutazione;
import it.unicam.cs.mpgc.rpg126114.engine.GiornataService;
import it.unicam.cs.mpgc.rpg126114.gui.ContestoGioco;
import it.unicam.cs.mpgc.rpg126114.gui.SceneRouter;
import it.unicam.cs.mpgc.rpg126114.model.carriera.Funzionario;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Documento;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Fascicolo;
import it.unicam.cs.mpgc.rpg126114.model.regole.Regola;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Timbro;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.util.Optional;

/**
 * Controller della scrivania: la schermata principale dove il
 * Funzionario esamina il fascicolo, concede colloqui, denuncia gli
 * impostori ed emette i verdetti.
 *
 * <p>Le anime arrivano in coda da un thread produttore mentre il
 * giocatore lavora: l'avviso di arrivo rientra nel thread JavaFX con
 * {@code Platform.runLater}, come richiesto dalla regola del singolo
 * thread per la modifica dei nodi. Il controller non contiene logica di
 * gioco: traduce gli eventi dell'interfaccia in chiamate al
 * {@link GiornataService} e ne mostra i risultati.</p>
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
    private Label lblCoda;
    @FXML
    private Label lblColloqui;
    @FXML
    private Label lblAvviso;
    @FXML
    private Label lblSportello;
    @FXML
    private ListView<String> listaDocumenti;
    @FXML
    private TextArea areaDocumento;
    @FXML
    private TextArea areaColloquio;
    @FXML
    private TextArea areaRegolamento;
    @FXML
    private ComboBox<Timbro> comboTimbro;
    @FXML
    private Button btnColloquio;
    @FXML
    private Button btnDenuncia;
    @FXML
    private Button btnParadiso;
    @FXML
    private Button btnPurgatorio;
    @FXML
    private Button btnLimbo;
    @FXML
    private Button btnInferno;

    private PauseTransition pulsazioneAvviso;

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
        pulsazioneAvviso = new PauseTransition(Duration.seconds(4));
        pulsazioneAvviso.setOnFinished(evento -> lblAvviso.setText(""));

        GiornataService servizio = contesto.getServizio();
        servizio.setOsservatoreArrivi(inAttesa -> Platform.runLater(this::onArrivo));
        if (servizio.getGiornata() == null) {
            if (!servizio.avviaGiornata()) {
                // Difesa in profondita': il pensionamento e' gestito da chi
                // naviga verso la scrivania. Qui non si cambia scena durante
                // il caricamento FXML: la navigazione va solo rimandata.
                Platform.runLater(() -> router.vai("menu"));
                return;
            }
            annunciaCircolareDelGiorno();
        }
        mostraRegolamento();
        tentaProssimaPratica();
    }

    /**
     * La circolare con la regola che entra in vigore oggi: mostrata dopo
     * la costruzione della scena, sul thread JavaFX.
     */
    private void annunciaCircolareDelGiorno() {
        contesto.getServizio().nuovaRegolaDelGiorno().ifPresent(regola ->
                Platform.runLater(() -> mostraMessaggio(Alert.AlertType.INFORMATION,
                        "Circolare di servizio",
                        "Da oggi e' in vigore: " + regola.descrizione()
                                + "\n\n" + regola.spiegazione())));
    }

    /**
     * Il pannello sempre visibile con le regole in vigore e i timbri
     * disponibili: il Funzionario deve sapere con quali criteri giudica.
     */
    private void mostraRegolamento() {
        GiornataService servizio = contesto.getServizio();
        if (servizio.getGiornata() == null) {
            return;
        }
        StringBuilder testo = new StringBuilder();
        for (Regola regola : servizio.getGiornata().getRegolamento().getRegole()) {
            testo.append("• ").append(regola.descrizione()).append('\n')
                    .append(regola.spiegazione()).append("\n\n");
        }
        testo.append("TIMBRI DISPONIBILI\n");
        for (Timbro timbro : contesto.getPartita().getFunzionario().timbriDisponibili()) {
            testo.append("• ").append(timbro.getEtichetta())
                    .append(" (karma x").append(timbro.getMoltiplicatore()).append(")\n");
        }
        testo.append("\nCOME SI DECIDE\n");
        testo.append("Ogni regola applicabile vota una destinazione con un peso. ");
        testo.append("Vince il peso totale piu' alto; a parita' prevale la ");
        testo.append("destinazione piu' severa. Il timbro moltiplica il karma ");
        testo.append("nel bene e nel male.\n");
        testo.append("\nPROCEDURE D'UFFICIO\n");
        testo.append("• Denuncia impostore: se l'anima risulta gia' giudicata, +")
                .append(GiornataService.KARMA_DENUNCIA_CORRETTA)
                .append(" karma; infondata ")
                .append(GiornataService.PENALE_DENUNCIA_ERRATA).append(".\n");
        testo.append("• Giudicare un impostore senza accorgersene: ")
                .append(GiornataService.PENALE_IMPOSTORE_GIUDICATO)
                .append(" e pratica respinta.\n");
        testo.append("• Le anime erranti ricordano al piu' una dichiarazione.\n");
        areaRegolamento.setText(testo.toString());
    }

    private void onArrivo() {
        lblAvviso.setText("Nuovo arrivo allo sportello");
        pulsazioneAvviso.playFromStart();
        aggiornaBarraDiStato();
        if (contesto.getServizio().getFascicoloCorrente() == null) {
            tentaProssimaPratica();
        }
    }

    private void tentaProssimaPratica() {
        GiornataService servizio = contesto.getServizio();
        if (servizio.getGiornata() == null || servizio.isGiornataConclusa()) {
            return;
        }
        if (servizio.getFascicoloCorrente() == null) {
            Optional<Fascicolo> pratica = servizio.prossimaPratica();
            if (pratica.isEmpty()) {
                modalitaAttesa();
                return;
            }
        }
        mostraPratica();
    }

    private void modalitaAttesa() {
        lblSportello.setText("Allo sportello");
        areaColloquio.setText("Lo sportello e' vuoto.\n\nIn attesa del prossimo arrivo...");
        listaDocumenti.getItems().clear();
        areaDocumento.clear();
        abilitaAzioni(false);
        aggiornaBarraDiStato();
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
        int livelloPrima = contesto.getPartita().getFunzionario().getLivello();
        boolean fondata = servizio.denunciaImpostore();
        if (fondata) {
            mostraMessaggio(Alert.AlertType.INFORMATION, "Impostore smascherato!",
                    "L'archivio conferma: quest'anima era gia' stata giudicata.\nKarma +"
                            + GiornataService.KARMA_DENUNCIA_CORRETTA);
            notificaSePromosso(livelloPrima);
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
            int livelloPrima = contesto.getPartita().getFunzionario().getLivello();
            EsitoValutazione esito =
                    contesto.getServizio().emettiVerdetto(destinazione, timbro);
            mostraEsito(destinazione, esito);
            notificaSePromosso(livelloPrima);
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
        tentaProssimaPratica();
    }

    @FXML
    private void onArchivio() {
        router.vai("archivio");
    }

    /**
     * Una promozione a meta' giornata merita un annuncio: livello, punti
     * abilita' maturati ed eventuali timbri appena sbloccati.
     */
    private void notificaSePromosso(int livelloPrima) {
        Funzionario funzionario = contesto.getPartita().getFunzionario();
        if (funzionario.getLivello() <= livelloPrima) {
            return;
        }
        StringBuilder testo = new StringBuilder("La Direzione si congratula: livello ")
                .append(funzionario.getLivello())
                .append(".\nPunti abilita' da spendere a fine giornata: ")
                .append(funzionario.getPuntiAbilita()).append('.');
        for (Timbro timbro : Timbro.values()) {
            if (timbro.getLivelloRichiesto() > livelloPrima
                    && timbro.getLivelloRichiesto() <= funzionario.getLivello()) {
                testo.append("\nSbloccato: ").append(timbro.getEtichetta())
                        .append(" (karma x").append(timbro.getMoltiplicatore()).append(')');
            }
        }
        mostraMessaggio(Alert.AlertType.INFORMATION, "Promozione!", testo.toString());
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

    private void mostraPratica() {
        Fascicolo fascicolo = contesto.getServizio().getFascicoloCorrente();
        if (fascicolo == null) {
            return;
        }
        lblSportello.setText("Allo sportello: " + fascicolo.getAnima().getNome()
                + " - anno di morte " + fascicolo.getAnima().getAnnoMorte());
        areaColloquio.setText(fascicolo.getAnima().presentazione());
        listaDocumenti.getItems().setAll(fascicolo.getDocumenti().stream()
                .map(this::etichettaDocumento)
                .toList());
        listaDocumenti.getSelectionModel().selectFirst();
        comboTimbro.getItems().setAll(
                contesto.getPartita().getFunzionario().timbriDisponibili());
        comboTimbro.getSelectionModel().selectFirst();
        mostraRegolamento();
        abilitaAzioni(true);
        aggiornaBarraDiStato();
    }

    private void abilitaAzioni(boolean praticaAperta) {
        btnDenuncia.setDisable(!praticaAperta);
        btnParadiso.setDisable(!praticaAperta);
        btnPurgatorio.setDisable(!praticaAperta);
        btnLimbo.setDisable(!praticaAperta);
        btnInferno.setDisable(!praticaAperta);
        btnColloquio.setDisable(!praticaAperta
                || contesto.getServizio().getColloquiRimasti() <= 0);
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
        if (servizio.getGiornata() == null) {
            return;
        }
        lblGiornata.setText("Giornata " + servizio.getGiornata().getNumero());
        Funzionario funzionario = contesto.getPartita().getFunzionario();
        StringBuilder karma = new StringBuilder("Karma: ").append(funzionario.getKarma())
                .append(" (promozione a ").append(funzionario.sogliaProssimaPromozione());
        if (funzionario.getKarma() < 0) {
            karma.append(", licenziamento a ").append(Funzionario.SOGLIA_LICENZIAMENTO);
        }
        karma.append(')');
        lblKarma.setText(karma.toString());
        lblLivello.setText("Livello: " + funzionario.getLivello()
                + " (Intuito " + funzionario.getIntuito()
                + ", Pazienza " + funzionario.getPazienza() + ")");
        lblRimanenti.setText("Casi da chiudere: " + servizio.getGiornata().animeRimanenti());
        lblCoda.setText("In coda: " + servizio.arriviInAttesa());
        lblColloqui.setText("Colloqui: " + servizio.getColloquiRimasti());
        if (servizio.getFascicoloCorrente() != null) {
            btnColloquio.setDisable(servizio.getColloquiRimasti() <= 0);
        }
    }

    private void mostraMessaggio(Alert.AlertType tipo, String intestazione, String testo) {
        Alert finestra = new Alert(tipo);
        finestra.setHeaderText(intestazione);
        finestra.setContentText(testo);
        finestra.showAndWait();
    }
}
