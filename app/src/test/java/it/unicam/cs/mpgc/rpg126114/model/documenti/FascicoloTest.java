package it.unicam.cs.mpgc.rpg126114.model.documenti;

import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;
import it.unicam.cs.mpgc.rpg126114.model.anima.AnimaComune;
import it.unicam.cs.mpgc.rpg126114.model.anima.Peccato;
import it.unicam.cs.mpgc.rpg126114.model.anima.Virtu;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica la validazione del fascicolo e la ricerca tipizzata
 * dei documenti.
 */
class FascicoloTest {

    private Fascicolo fascicoloDi(String nome) {
        return new Fascicolo(new AnimaComune(nome, 1900));
    }

    @Test
    void unFascicoloSenzaFedinaNonVaAGiudizio() {
        Fascicolo fascicolo = fascicoloDi("Guido Lanterna");
        fascicolo.aggiungi(new Testamento("Lascio tutto al gatto.", 1899));

        assertFalse(fascicolo.isCompleto());
        assertThrows(PraticaMalformataException.class, fascicolo::valida);
    }

    @Test
    void unFascicoloConFedinaEValido() {
        Fascicolo fascicolo = fascicoloDi("Guido Lanterna");
        fascicolo.aggiungi(new FedinaKarmica(List.of(), List.of()));

        assertTrue(fascicolo.isCompleto());
        fascicolo.valida();
    }

    @Test
    void trovaRestituisceIlDocumentoDelTipoRichiesto() {
        Fascicolo fascicolo = fascicoloDi("Nora Vento");
        Testamento testamento = new Testamento("Ai posteri l'ardua sentenza.", 1820);
        fascicolo.aggiungi(new FedinaKarmica(List.of(), List.of()));
        fascicolo.aggiungi(testamento);

        Optional<Testamento> trovato = fascicolo.trova(Testamento.class);

        assertTrue(trovato.isPresent());
        assertEquals(testamento, trovato.get());
        assertTrue(fascicolo.trova(Confessione.class).isEmpty());
    }

    @Test
    void documentoNullRifiutato() {
        Fascicolo fascicolo = fascicoloDi("Nora Vento");

        assertThrows(IllegalArgumentException.class, () -> fascicolo.aggiungi(null));
        assertThrows(IllegalArgumentException.class, () -> new Fascicolo(null));
    }

    @Test
    void laListaDeiDocumentiNonEModificabileDallEsterno() {
        Fascicolo fascicolo = fascicoloDi("Nora Vento");

        assertThrows(UnsupportedOperationException.class,
                () -> fascicolo.getDocumenti().add(new FedinaKarmica(List.of(), List.of())));
    }

    @Test
    void laFedinaESempreAttendibile() {
        FedinaKarmica fedina = new FedinaKarmica(List.of(new Peccato("Usura", 8)), List.of());

        assertTrue(fedina.isAttendibile());
        assertTrue(fedina.contenuto().contains("Usura"));
    }

    @Test
    void laFedinaRiportaVirtuPeccatiEBilancio() {
        FedinaKarmica fedina = new FedinaKarmica(
                List.of(new Peccato("Usura", 8)),
                List.of(new Virtu("Elemosine domenicali", 3)));

        assertTrue(fedina.contenuto().contains("Elemosine domenicali"));
        assertTrue(fedina.contenuto().contains("Usura"));
        assertEquals(-5, fedina.bilancio());
        assertTrue(fedina.contenuto().contains("-5"));
        assertThrows(IllegalArgumentException.class,
                () -> new FedinaKarmica(null, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FedinaKarmica(List.of(), null));
    }

    @Test
    void laConfessioneRilevaIPeccatiTaciuti() {
        Peccato taciuto = new Peccato("Falso in bilancio", 7);
        Peccato ammesso = new Peccato("Golosita'", 2);
        Confessione confessione = new Confessione(List.of("Golosita'"));

        assertTrue(confessione.omette(taciuto));
        assertFalse(confessione.omette(ammesso));
    }

    @Test
    void soloLeLettereRichiedonoVerificaDellaFirma() {
        assertTrue(new Testamento("Tutto ai posteri.", 1800).isAttendibile());
        assertTrue(new Confessione(List.of()).isAttendibile());
        assertFalse(new LetteraRaccomandazione("Firma Sospetta", 3, false).isAttendibile());
    }

    @Test
    void laLetteraValidaPesoEFirmatario() {
        assertThrows(IllegalArgumentException.class,
                () -> new LetteraRaccomandazione("", 3, true));
        assertThrows(IllegalArgumentException.class,
                () -> new LetteraRaccomandazione("Cardinale Oscuro", 6, true));

        LetteraRaccomandazione lettera = new LetteraRaccomandazione("Cardinale Oscuro", 5, false);
        assertFalse(lettera.isAttendibile());
        assertEquals(5, lettera.getPeso());
    }

    @Test
    void anchePerLeAnimeIllustriServeLaFedina() {
        Anima anima = new AnimaComune("Furio Tasso", 1700);
        Fascicolo fascicolo = new Fascicolo(anima);
        fascicolo.aggiungi(new LetteraRaccomandazione("Duca del Brivido", 4, true));

        assertThrows(PraticaMalformataException.class, fascicolo::valida);
    }
}
