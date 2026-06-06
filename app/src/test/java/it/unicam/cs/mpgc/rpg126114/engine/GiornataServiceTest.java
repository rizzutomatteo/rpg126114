package it.unicam.cs.mpgc.rpg126114.engine;

import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;
import it.unicam.cs.mpgc.rpg126114.model.anima.AnimaComune;
import it.unicam.cs.mpgc.rpg126114.model.anima.Virtu;
import it.unicam.cs.mpgc.rpg126114.model.carriera.Funzionario;
import it.unicam.cs.mpgc.rpg126114.model.carriera.Partita;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Fascicolo;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Timbro;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Verdetto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica il flusso completo di una giornata allo sportello.
 */
class GiornataServiceTest {

    private List<Anima> poolVirtuoso(int quante) {
        List<Anima> pool = new ArrayList<>();
        for (int i = 1; i <= quante; i++) {
            Anima anima = new AnimaComune("Anima Virtuosa " + i, 1900 + i);
            anima.aggiungiVirtu(new Virtu("Bonta' esemplare", 8));
            anima.aggiungiDichiarazione("Ho sempre fatto del mio meglio.");
            anima.aggiungiDichiarazione("Lo ripeto: del mio meglio.");
            pool.add(anima);
        }
        return pool;
    }

    private GiornataService servizioCon(List<Anima> pool, int probabilitaImpostore) {
        Partita partita = new Partita(new Funzionario("Funzionario di Prova"));
        return new GiornataService(partita,
                new GeneratoreAnime(pool, new Random(7), probabilitaImpostore),
                new GeneratoreFascicoli(new Random(7)),
                new ValutatoreVerdetti());
    }

    @Test
    void ilRegolamentoCresceConLeGiornate() {
        GiornataService servizio = servizioCon(poolVirtuoso(3), 0);

        assertEquals(1, servizio.regolamentoPerGiornata(1).getRegole().size());
        assertEquals(2, servizio.regolamentoPerGiornata(2).getRegole().size());
        assertEquals(5, servizio.regolamentoPerGiornata(5).getRegole().size());
        assertEquals(5, servizio.regolamentoPerGiornata(9).getRegole().size());
    }

    @Test
    void unaGiornataCompletaProduceReportEAvanzamento() {
        GiornataService servizio = servizioCon(poolVirtuoso(3), 0);

        assertTrue(servizio.avviaGiornata());
        assertEquals(3, servizio.getGiornata().getAnimePreviste());

        while (!servizio.isGiornataConclusa()) {
            Fascicolo fascicolo = servizio.prossimaPratica();
            assertNotNull(fascicolo);
            EsitoValutazione esito =
                    servizio.emettiVerdetto(Destinazione.PARADISO, Timbro.ORDINANZA);
            assertTrue(esito.isCorretto());
        }

        ReportGiornata report = servizio.chiudiGiornata();
        assertEquals(100.0, report.percentualeCorretti());
        assertEquals(3 * ValutatoreVerdetti.KARMA_BASE, report.karmaTotale());
        assertEquals(3, report.conteggioPerDestinazione().get(Destinazione.PARADISO));
        assertEquals(2, servizio.getPartita().getGiornataCorrente());
        assertEquals(30, servizio.getPartita().getFunzionario().getKarma());
    }

    @Test
    void unVerdettoSbagliatoCostaKarma() {
        GiornataService servizio = servizioCon(poolVirtuoso(3), 0);
        servizio.avviaGiornata();
        servizio.prossimaPratica();

        EsitoValutazione esito = servizio.emettiVerdetto(Destinazione.INFERNO, Timbro.ORDINANZA);

        assertFalse(esito.isCorretto());
        assertEquals(-ValutatoreVerdetti.KARMA_BASE, esito.getDeltaKarma());
        assertEquals(Destinazione.PARADISO, esito.getDestinazioneAttesa());
    }

    @Test
    void laDenunciaInfondataCostaELaPraticaRestaAperta() {
        GiornataService servizio = servizioCon(poolVirtuoso(3), 0);
        servizio.avviaGiornata();
        servizio.prossimaPratica();

        assertFalse(servizio.denunciaImpostore());
        assertEquals(GiornataService.PENALE_DENUNCIA_ERRATA,
                servizio.getPartita().getFunzionario().getKarma());
        assertNotNull(servizio.getFascicoloCorrente());
    }

    @Test
    void lImpostoreDenunciatoFruttaKarma() {
        GiornataService servizio = servizioCon(poolVirtuoso(3), 100);
        Anima giaGiudicata = new AnimaComune("Anima Virtuosa 1", 1901);
        servizio.getPartita().getArchivio().registra(
                new Verdetto(giaGiudicata, Destinazione.PARADISO, Timbro.ORDINANZA, 1));

        servizio.avviaGiornata();
        servizio.prossimaPratica();

        assertTrue(servizio.denunciaImpostore());
        assertEquals(GiornataService.KARMA_DENUNCIA_CORRETTA,
                servizio.getPartita().getFunzionario().getKarma());
        assertEquals(1, servizio.getGiornata().getCasiSenzaVerdetto());
    }

    @Test
    void lImpostoreGiudicatoVieneRespintoDallArchivio() {
        GiornataService servizio = servizioCon(poolVirtuoso(3), 100);
        Anima giaGiudicata = new AnimaComune("Anima Virtuosa 1", 1901);
        servizio.getPartita().getArchivio().registra(
                new Verdetto(giaGiudicata, Destinazione.PARADISO, Timbro.ORDINANZA, 1));

        servizio.avviaGiornata();
        servizio.prossimaPratica();
        EsitoValutazione esito = servizio.emettiVerdetto(Destinazione.PARADISO, Timbro.ORDINANZA);

        assertFalse(esito.isCorretto());
        assertEquals(GiornataService.PENALE_IMPOSTORE_GIUDICATO, esito.getDeltaKarma());
        assertTrue(esito.getMotivazioni().get(0).contains("impostore"));
        assertEquals(1, servizio.getGiornata().getCasiSenzaVerdetto());
    }

    @Test
    void unTimbroNonSbloccatoVieneRifiutato() {
        GiornataService servizio = servizioCon(poolVirtuoso(3), 0);
        servizio.avviaGiornata();
        servizio.prossimaPratica();

        assertThrows(IllegalArgumentException.class,
                () -> servizio.emettiVerdetto(Destinazione.PARADISO, Timbro.DORATO));
    }

    @Test
    void iColloquiSonoLimitatiDallaPazienza() {
        GiornataService servizio = servizioCon(poolVirtuoso(3), 0);
        servizio.avviaGiornata();
        servizio.prossimaPratica();

        Optional<String> primo = servizio.colloquio();
        Optional<String> secondo = servizio.colloquio();

        assertTrue(primo.isPresent());
        assertTrue(secondo.isEmpty());
        assertEquals(0, servizio.getColloquiRimasti());
    }

    @Test
    void aPoolEsauritoScattaIlPensionamento() {
        List<Anima> pool = poolVirtuoso(1);
        GiornataService servizio = servizioCon(pool, 0);
        servizio.getPartita().getArchivio().registra(
                new Verdetto(pool.get(0), Destinazione.PARADISO, Timbro.ORDINANZA, 1));

        assertFalse(servizio.avviaGiornata());
    }

    @Test
    void leOperazioniFuoriSequenzaSonoRifiutate() {
        GiornataService servizio = servizioCon(poolVirtuoso(3), 0);

        assertThrows(IllegalStateException.class, servizio::prossimaPratica);
        assertThrows(IllegalStateException.class,
                () -> servizio.emettiVerdetto(Destinazione.PARADISO, Timbro.ORDINANZA));

        servizio.avviaGiornata();
        assertThrows(IllegalStateException.class, servizio::chiudiGiornata);

        servizio.prossimaPratica();
        assertThrows(IllegalStateException.class, servizio::prossimaPratica);
    }
}
