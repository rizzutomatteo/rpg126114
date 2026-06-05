package it.unicam.cs.mpgc.rpg126114.model.carriera;

import it.unicam.cs.mpgc.rpg126114.model.anima.AnimaComune;
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
 * Verifica la progressione del Funzionario e lo stato di giornata
 * e partita.
 */
class CarrieraTest {

    @Test
    void ogniCinquantaKarmaScattaUnaPromozione() {
        Funzionario funzionario = new Funzionario("Astolfo Pratiche");

        int promozioni = funzionario.aggiungiKarma(105);

        assertEquals(2, promozioni);
        assertEquals(3, funzionario.getLivello());
        assertEquals(2, funzionario.getPuntiAbilita());
    }

    @Test
    void iPuntiAbilitaSiSpendonoPerLeStatistiche() {
        Funzionario funzionario = new Funzionario("Astolfo Pratiche");
        funzionario.aggiungiKarma(50);

        funzionario.potenziaIntuito();

        assertEquals(1, funzionario.getIntuito());
        assertEquals(0, funzionario.getPuntiAbilita());
        assertThrows(IllegalStateException.class, funzionario::potenziaPazienza);
    }

    @Test
    void laPazienzaAumentaIColloquiGiornalieri() {
        Funzionario funzionario = new Funzionario("Astolfo Pratiche");
        assertEquals(Funzionario.COLLOQUI_BASE, funzionario.colloquiPerGiornata());

        funzionario.aggiungiKarma(50);
        funzionario.potenziaPazienza();

        assertEquals(Funzionario.COLLOQUI_BASE + 1, funzionario.colloquiPerGiornata());
    }

    @Test
    void sottoLaSogliaScattaIlLicenziamento() {
        Funzionario funzionario = new Funzionario("Astolfo Pratiche");

        funzionario.aggiungiKarma(-50);

        assertTrue(funzionario.isLicenziato());
    }

    @Test
    void iTimbriSiSbloccanoConIlLivello() {
        Funzionario funzionario = new Funzionario("Astolfo Pratiche");

        assertEquals(List.of(Timbro.ORDINANZA), funzionario.timbriDisponibili());

        funzionario.aggiungiKarma(100);
        assertTrue(funzionario.timbriDisponibili().contains(Timbro.DORATO));
        assertFalse(funzionario.timbriDisponibili().contains(Timbro.SIGILLO_FIAMMEGGIANTE));
    }

    @Test
    void unaGiornataSiConcludeDopoLeAnimePreviste() {
        Giornata giornata = new Giornata(1, 2, new Regolamento(List.of()));
        Verdetto verdetto = new Verdetto(new AnimaComune("Primo Caso", 1900),
                Destinazione.PURGATORIO, Timbro.ORDINANZA, 1);

        giornata.registraVerdetto(verdetto);
        assertFalse(giornata.isConclusa());
        assertEquals(1, giornata.animeRimanenti());

        giornata.registraVerdetto(new Verdetto(new AnimaComune("Secondo Caso", 1901),
                Destinazione.INFERNO, Timbro.ORDINANZA, 1));
        assertTrue(giornata.isConclusa());
        assertThrows(IllegalStateException.class, () -> giornata.registraVerdetto(verdetto));
    }

    @Test
    void laGiornataValidaIParametri() {
        Regolamento regolamento = new Regolamento(List.of());

        assertThrows(IllegalArgumentException.class, () -> new Giornata(0, 5, regolamento));
        assertThrows(IllegalArgumentException.class, () -> new Giornata(1, 0, regolamento));
        assertThrows(IllegalArgumentException.class, () -> new Giornata(1, 5, null));
    }

    @Test
    void laPartitaParteDalPrimoGiornoEAvanza() {
        Partita partita = new Partita(new Funzionario("Astolfo Pratiche"));

        assertEquals(1, partita.getGiornataCorrente());
        partita.avanzaGiornata();
        assertEquals(2, partita.getGiornataCorrente());
        assertFalse(partita.isFinita());
    }

    @Test
    void laPartitaFinisceColLicenziamento() {
        Partita partita = new Partita(new Funzionario("Astolfo Pratiche"));

        partita.getFunzionario().aggiungiKarma(-60);

        assertTrue(partita.isFinita());
    }
}
