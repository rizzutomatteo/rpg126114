package it.unicam.cs.mpgc.rpg126114.model.carriera;

import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;
import it.unicam.cs.mpgc.rpg126114.model.anima.AnimaComune;
import it.unicam.cs.mpgc.rpg126114.model.anima.AnimaErrante;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Timbro;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Verdetto;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica l'archivio anti-impostori e le ricerche sui verdetti.
 */
class ArchivioAnimeTest {

    private Verdetto verdettoPer(Anima anima, Destinazione destinazione) {
        return new Verdetto(anima, destinazione, Timbro.ORDINANZA, 1);
    }

    @Test
    void unAnimaGiudicataVieneRiconosciuta() {
        ArchivioAnime archivio = new ArchivioAnime();
        archivio.registra(verdettoPer(new AnimaComune("Aldo Brace", 1920), Destinazione.INFERNO));

        assertTrue(archivio.giaGiudicata(new AnimaErrante("Aldo Brace", 1920)));
        assertFalse(archivio.giaGiudicata(new AnimaComune("Aldo Brace", 1921)));
    }

    @Test
    void registrareDueVolteLaStessaAnimaEUnErrore() {
        ArchivioAnime archivio = new ArchivioAnime();
        archivio.registra(verdettoPer(new AnimaComune("Aldo Brace", 1920), Destinazione.INFERNO));

        assertThrows(IllegalStateException.class,
                () -> archivio.registra(verdettoPer(new AnimaComune("Aldo Brace", 1920),
                        Destinazione.PARADISO)));
    }

    @Test
    void laRicercaPerNomeIgnoraLeMaiuscole() {
        ArchivioAnime archivio = new ArchivioAnime();
        archivio.registra(verdettoPer(new AnimaComune("Brunilde Cenere", 1880), Destinazione.LIMBO));
        archivio.registra(verdettoPer(new AnimaComune("Carlo Fiamma", 1932), Destinazione.INFERNO));

        List<Verdetto> trovati = archivio.cercaPerNome("cenere");

        assertEquals(1, trovati.size());
        assertEquals("Brunilde Cenere", trovati.get(0).getAnima().getNome());
        assertEquals(2, archivio.cercaPerNome("").size());
    }

    @Test
    void ilConteggioRaggruppaPerDestinazione() {
        ArchivioAnime archivio = new ArchivioAnime();
        archivio.registra(verdettoPer(new AnimaComune("Anima Uno", 1900), Destinazione.INFERNO));
        archivio.registra(verdettoPer(new AnimaComune("Anima Due", 1901), Destinazione.INFERNO));
        archivio.registra(verdettoPer(new AnimaComune("Anima Tre", 1902), Destinazione.PARADISO));

        Map<Destinazione, Long> conteggio = archivio.conteggioPerDestinazione();

        assertEquals(2, conteggio.get(Destinazione.INFERNO));
        assertEquals(1, conteggio.get(Destinazione.PARADISO));
        assertEquals(3, archivio.totaleGiudicate());
    }

    @Test
    void verdettoNullRifiutato() {
        assertThrows(IllegalArgumentException.class, () -> new ArchivioAnime().registra(null));
    }
}
