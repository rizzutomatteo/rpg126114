package it.unicam.cs.mpgc.rpg126114.model.regole;

import it.unicam.cs.mpgc.rpg126114.model.anima.AnimaComune;
import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;
import it.unicam.cs.mpgc.rpg126114.model.anima.Peccato;
import it.unicam.cs.mpgc.rpg126114.model.anima.Virtu;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Confessione;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Fascicolo;
import it.unicam.cs.mpgc.rpg126114.model.documenti.FedinaKarmica;
import it.unicam.cs.mpgc.rpg126114.model.documenti.LetteraRaccomandazione;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Testamento;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Casi tabellari per ciascuna regola del regolamento.
 */
class RegoleTest {

    private Fascicolo fascicoloConBilancio(int meriti, int colpe) {
        Anima anima = new AnimaComune("Anima di prova", 1950);
        if (meriti > 0) {
            anima.aggiungiVirtu(new Virtu("Merito di prova", meriti));
        }
        if (colpe > 0) {
            anima.aggiungiPeccato(new Peccato("Colpa di prova", colpe));
        }
        Fascicolo fascicolo = new Fascicolo(anima);
        fascicolo.aggiungi(new FedinaKarmica(anima.getPeccati(), anima.getVirtu()));
        return fascicolo;
    }

    @Test
    void bilancioAltoPortaInParadiso() {
        RegolaBilancioKarmico regola = new RegolaBilancioKarmico(5, -5);

        Optional<Esito> esito = regola.valuta(fascicoloConBilancio(8, 0));

        assertEquals(Destinazione.PARADISO, esito.orElseThrow().getDestinazione());
    }

    @Test
    void bilancioBassoPortaAllInferno() {
        RegolaBilancioKarmico regola = new RegolaBilancioKarmico(5, -5);

        Optional<Esito> esito = regola.valuta(fascicoloConBilancio(0, 9));

        assertEquals(Destinazione.INFERNO, esito.orElseThrow().getDestinazione());
    }

    @Test
    void bilancioIntermedioPortaInPurgatorio() {
        RegolaBilancioKarmico regola = new RegolaBilancioKarmico(5, -5);

        Optional<Esito> esito = regola.valuta(fascicoloConBilancio(3, 2));

        assertEquals(Destinazione.PURGATORIO, esito.orElseThrow().getDestinazione());
    }

    @Test
    void soglieIncoerentiRifiutate() {
        assertThrows(IllegalArgumentException.class, () -> new RegolaBilancioKarmico(-5, 5));
    }

    @Test
    void testamentoPostumoEUnaCondanna() {
        Anima anima = new AnimaComune("Furbo Postumo", 1900);
        Fascicolo fascicolo = new Fascicolo(anima);
        fascicolo.aggiungi(new FedinaKarmica(List.of(), List.of()));
        fascicolo.aggiungi(new Testamento("Lascio tutto a me stesso.", 1905));

        Optional<Esito> esito = new RegolaContraddizioni().valuta(fascicolo);

        assertEquals(Destinazione.INFERNO, esito.orElseThrow().getDestinazione());
    }

    @Test
    void tacereUnPeccatoCapitaleEUnaCondanna() {
        Anima anima = new AnimaComune("Omertosa Bruni", 1960);
        Peccato capitale = new Peccato("Tradimento della patria", 9);
        anima.aggiungiPeccato(capitale);
        Fascicolo fascicolo = new Fascicolo(anima);
        fascicolo.aggiungi(new FedinaKarmica(List.of(capitale), List.of()));
        fascicolo.aggiungi(new Confessione(List.of("Piccole bugie")));

        Optional<Esito> esito = new RegolaContraddizioni().valuta(fascicolo);

        assertEquals(Destinazione.INFERNO, esito.orElseThrow().getDestinazione());
        assertTrue(esito.orElseThrow().getMotivazione().contains("Tradimento della patria"));
    }

    @Test
    void senzaContraddizioniLaRegolaNonSiApplica() {
        Fascicolo fascicolo = fascicoloConBilancio(3, 0);

        assertTrue(new RegolaContraddizioni().valuta(fascicolo).isEmpty());
    }

    @Test
    void raccomandazioneAutenticaPortaInParadiso() {
        Fascicolo fascicolo = fascicoloConBilancio(0, 0);
        fascicolo.aggiungi(new LetteraRaccomandazione("Arcangelo Capoufficio", 4, true));

        Esito esito = new RegolaRaccomandazione().valuta(fascicolo).orElseThrow();

        assertEquals(Destinazione.PARADISO, esito.getDestinazione());
    }

    @Test
    void raccomandazioneContraffattaAggrava() {
        Fascicolo fascicolo = fascicoloConBilancio(0, 0);
        fascicolo.aggiungi(new LetteraRaccomandazione("Firma Sospetta", 5, false));

        Esito esito = new RegolaRaccomandazione().valuta(fascicolo).orElseThrow();

        assertEquals(Destinazione.INFERNO, esito.getDestinazione());
    }

    @Test
    void leAnimeAntichePassanoAlLimbo() {
        Anima antica = new AnimaComune("Anima del Trecento", 1350);
        Fascicolo fascicolo = new Fascicolo(antica);
        fascicolo.aggiungi(new FedinaKarmica(List.of(), List.of()));

        Optional<Esito> esito = new RegolaAnzianita(1500).valuta(fascicolo);

        assertEquals(Destinazione.LIMBO, esito.orElseThrow().getDestinazione());
        assertTrue(new RegolaAnzianita(1300).valuta(fascicolo).isEmpty());
    }

    @Test
    void ilPentimentoSinceroMeritaIlPurgatorio() {
        Anima anima = new AnimaComune("Sincera Costa", 1971);
        Peccato capitale = new Peccato("Avidita' senza freni", 8);
        anima.aggiungiPeccato(capitale);
        Fascicolo fascicolo = new Fascicolo(anima);
        fascicolo.aggiungi(new FedinaKarmica(List.of(capitale), List.of()));
        fascicolo.aggiungi(new Confessione(List.of("Avidita' senza freni")));

        Optional<Esito> esito = new RegolaPentimento().valuta(fascicolo);

        assertEquals(Destinazione.PURGATORIO, esito.orElseThrow().getDestinazione());
    }

    @Test
    void unaConfessioneVuotaNonEPentimento() {
        Fascicolo fascicolo = fascicoloConBilancio(2, 0);
        fascicolo.aggiungi(new Confessione(List.of()));

        assertTrue(new RegolaPentimento().valuta(fascicolo).isEmpty());
    }

    @Test
    void ogniRegolaSiSpiegaAlGiocatoreConISuoiParametri() {
        List<Regola> regole = List.of(
                new RegolaBilancioKarmico(5, -5),
                new RegolaContraddizioni(),
                new RegolaRaccomandazione(),
                new RegolaAnzianita(1700),
                new RegolaPentimento());

        for (Regola regola : regole) {
            assertFalse(regola.spiegazione().isBlank(),
                    regola.descrizione() + " deve spiegarsi al giocatore");
        }
        assertTrue(new RegolaBilancioKarmico(5, -5).spiegazione().contains("5"));
        assertTrue(new RegolaAnzianita(1700).spiegazione().contains("1700"));
        assertTrue(new RegolaContraddizioni().spiegazione()
                .contains(String.valueOf(Peccato.SOGLIA_CAPITALE)));
        assertTrue(new RegolaPentimento().spiegazione().contains("Purgatorio"));
    }

    @Test
    void unaRegolaPuoEssereEspressaComeLambda() {
        Regola regolaSpeciale = fascicolo ->
                Optional.of(new Esito(Destinazione.LIMBO, "giornata di sciopero celeste"));

        Esito esito = regolaSpeciale.valuta(fascicoloConBilancio(0, 0)).orElseThrow();

        assertEquals(Destinazione.LIMBO, esito.getDestinazione());
    }
}
