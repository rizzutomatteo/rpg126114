package it.unicam.cs.mpgc.rpg126114.engine;

import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;
import it.unicam.cs.mpgc.rpg126114.model.anima.AnimaComune;
import it.unicam.cs.mpgc.rpg126114.model.anima.Virtu;
import it.unicam.cs.mpgc.rpg126114.model.carriera.Funzionario;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Fascicolo;
import it.unicam.cs.mpgc.rpg126114.model.documenti.FedinaKarmica;
import it.unicam.cs.mpgc.rpg126114.model.regole.RegolaBilancioKarmico;
import it.unicam.cs.mpgc.rpg126114.model.regole.Regolamento;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Timbro;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Verdetto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica il calcolo del karma nella valutazione dei verdetti.
 */
class ValutatoreVerdettiTest {

    private final ValutatoreVerdetti valutatore = new ValutatoreVerdetti();
    private final Regolamento regolamento =
            new Regolamento(List.of(new RegolaBilancioKarmico(5, -5)));

    private Fascicolo fascicoloDaParadiso() {
        Anima anima = new AnimaComune("Santa Subito", 2000);
        anima.aggiungiVirtu(new Virtu("Carita' instancabile", 9));
        Fascicolo fascicolo = new Fascicolo(anima);
        fascicolo.aggiungi(new FedinaKarmica(anima.getPeccati()));
        return fascicolo;
    }

    @Test
    void verdettoCorrettoPremiato() {
        Fascicolo fascicolo = fascicoloDaParadiso();
        Verdetto verdetto = new Verdetto(fascicolo.getAnima(),
                Destinazione.PARADISO, Timbro.ORDINANZA, 1);

        EsitoValutazione esito = valutatore.valuta(verdetto, regolamento,
                fascicolo, new Funzionario("Probo"));

        assertTrue(esito.isCorretto());
        assertEquals(ValutatoreVerdetti.KARMA_BASE, esito.getDeltaKarma());
        assertEquals(Destinazione.PARADISO, esito.getDestinazioneAttesa());
        assertFalse(esito.getMotivazioni().isEmpty());
    }

    @Test
    void verdettoSbagliatoPunito() {
        Fascicolo fascicolo = fascicoloDaParadiso();
        Verdetto verdetto = new Verdetto(fascicolo.getAnima(),
                Destinazione.INFERNO, Timbro.ORDINANZA, 1);

        EsitoValutazione esito = valutatore.valuta(verdetto, regolamento,
                fascicolo, new Funzionario("Distratto"));

        assertFalse(esito.isCorretto());
        assertEquals(-ValutatoreVerdetti.KARMA_BASE, esito.getDeltaKarma());
    }

    @Test
    void ilTimbroMoltiplicaNelBeneENelMale() {
        Fascicolo fascicolo = fascicoloDaParadiso();
        Funzionario funzionario = new Funzionario("Ambizioso");

        EsitoValutazione vittoria = valutatore.valuta(
                new Verdetto(fascicolo.getAnima(), Destinazione.PARADISO, Timbro.DORATO, 1),
                regolamento, fascicolo, funzionario);
        EsitoValutazione rovina = valutatore.valuta(
                new Verdetto(fascicolo.getAnima(), Destinazione.LIMBO, Timbro.DORATO, 1),
                regolamento, fascicolo, funzionario);

        assertEquals(2 * ValutatoreVerdetti.KARMA_BASE, vittoria.getDeltaKarma());
        assertEquals(-2 * ValutatoreVerdetti.KARMA_BASE, rovina.getDeltaKarma());
    }

    @Test
    void lIntuitoAumentaIlPremioDeiVerdettiCorretti() {
        Fascicolo fascicolo = fascicoloDaParadiso();
        Funzionario esperto = new Funzionario("Esperto");
        esperto.aggiungiKarma(50);
        esperto.potenziaIntuito();

        EsitoValutazione esito = valutatore.valuta(
                new Verdetto(fascicolo.getAnima(), Destinazione.PARADISO, Timbro.ORDINANZA, 1),
                regolamento, fascicolo, esperto);

        assertEquals(ValutatoreVerdetti.KARMA_BASE + ValutatoreVerdetti.PREMIO_INTUITO,
                esito.getDeltaKarma());
    }

    @Test
    void verdettoSuAnimaDiversaRifiutato() {
        Fascicolo fascicolo = fascicoloDaParadiso();
        Verdetto estraneo = new Verdetto(new AnimaComune("Altro Soggetto", 1700),
                Destinazione.LIMBO, Timbro.ORDINANZA, 1);

        assertThrows(IllegalArgumentException.class,
                () -> valutatore.valuta(estraneo, regolamento, fascicolo, new Funzionario("P")));
    }
}
