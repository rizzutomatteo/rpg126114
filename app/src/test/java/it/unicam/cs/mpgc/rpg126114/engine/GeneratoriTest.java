package it.unicam.cs.mpgc.rpg126114.engine;

import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;
import it.unicam.cs.mpgc.rpg126114.model.anima.AnimaComune;
import it.unicam.cs.mpgc.rpg126114.model.anima.AnimaIllustre;
import it.unicam.cs.mpgc.rpg126114.model.anima.Peccato;
import it.unicam.cs.mpgc.rpg126114.model.carriera.ArchivioAnime;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Confessione;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Fascicolo;
import it.unicam.cs.mpgc.rpg126114.model.documenti.FedinaKarmica;
import it.unicam.cs.mpgc.rpg126114.model.documenti.LetteraRaccomandazione;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Testamento;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Timbro;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Verdetto;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica i generatori di anime e fascicoli con casualita' riproducibile.
 */
class GeneratoriTest {

    private List<Anima> poolDiTre() {
        return List.of(
                new AnimaComune("Anima Alfa", 1900),
                new AnimaComune("Anima Beta", 1910),
                new AnimaComune("Anima Gamma", 1920));
    }

    @Test
    void ilGeneratoreContaLeAnimeFresche() {
        GeneratoreAnime generatore = new GeneratoreAnime(poolDiTre(), new Random(42), 0);
        ArchivioAnime archivio = new ArchivioAnime();

        assertEquals(3, generatore.animeFresche(archivio));

        archivio.registra(new Verdetto(new AnimaComune("Anima Alfa", 1900),
                Destinazione.LIMBO, Timbro.ORDINANZA, 1));
        assertEquals(2, generatore.animeFresche(archivio));
    }

    @Test
    void senzaImpostoriNonEsconoMaiAnimeGiaGiudicate() {
        GeneratoreAnime generatore = new GeneratoreAnime(poolDiTre(), new Random(42), 0);
        ArchivioAnime archivio = new ArchivioAnime();
        archivio.registra(new Verdetto(new AnimaComune("Anima Alfa", 1900),
                Destinazione.LIMBO, Timbro.ORDINANZA, 1));
        archivio.registra(new Verdetto(new AnimaComune("Anima Beta", 1910),
                Destinazione.INFERNO, Timbro.ORDINANZA, 1));

        for (int i = 0; i < 20; i++) {
            assertEquals("Anima Gamma", generatore.prossima(archivio).getNome());
        }
    }

    @Test
    void aPoolEsauritoIlGeneratoreSiFerma() {
        List<Anima> pool = List.of(new AnimaComune("Unica Anima", 1950));
        GeneratoreAnime generatore = new GeneratoreAnime(pool, new Random(42), 0);
        ArchivioAnime archivio = new ArchivioAnime();
        archivio.registra(new Verdetto(new AnimaComune("Unica Anima", 1950),
                Destinazione.PARADISO, Timbro.ORDINANZA, 1));

        assertEquals(0, generatore.animeFresche(archivio));
        assertThrows(IllegalStateException.class, () -> generatore.prossima(archivio));
    }

    @Test
    void conProbabilitaPienaArrivaSempreUnImpostore() {
        GeneratoreAnime generatore = new GeneratoreAnime(poolDiTre(), new Random(42), 100);
        ArchivioAnime archivio = new ArchivioAnime();
        archivio.registra(new Verdetto(new AnimaComune("Anima Alfa", 1900),
                Destinazione.LIMBO, Timbro.ORDINANZA, 1));

        for (int i = 0; i < 10; i++) {
            Anima arrivata = generatore.prossima(archivio);
            assertTrue(archivio.giaGiudicata(arrivata));
        }
    }

    @Test
    void ilGeneratoreValidaIParametri() {
        assertThrows(IllegalArgumentException.class,
                () -> new GeneratoreAnime(List.of(), new Random(), 10));
        assertThrows(IllegalArgumentException.class,
                () -> new GeneratoreAnime(null, new Random(), 10));
        assertThrows(IllegalArgumentException.class,
                () -> new GeneratoreAnime(poolDiTre(), null, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new GeneratoreAnime(poolDiTre(), new Random(), 101));
    }

    @Test
    void ogniFascicoloHaSempreLaFedina() {
        GeneratoreFascicoli generatore = new GeneratoreFascicoli(new Random(42));
        Anima anima = new AnimaComune("Anima di Prova", 1950);
        anima.aggiungiPeccato(new Peccato("Pigrizia cronica", 3));

        for (int i = 0; i < 100; i++) {
            Fascicolo fascicolo = generatore.componi(anima);
            assertTrue(fascicolo.isCompleto());
            assertTrue(fascicolo.trova(FedinaKarmica.class).isPresent());
        }
    }

    @Test
    void primaOPoiCompaionoTuttiITipiDiDocumento() {
        GeneratoreFascicoli generatore = new GeneratoreFascicoli(new Random(42));
        Anima illustre = new AnimaIllustre("Contessa di Prova", 1800, "Contessa");
        illustre.aggiungiPeccato(new Peccato("Alterigia", 5));

        boolean testamento = false;
        boolean confessione = false;
        boolean lettera = false;
        for (int i = 0; i < 100; i++) {
            Fascicolo fascicolo = generatore.componi(illustre);
            testamento = testamento || fascicolo.trova(Testamento.class).isPresent();
            confessione = confessione || fascicolo.trova(Confessione.class).isPresent();
            lettera = lettera || fascicolo.trova(LetteraRaccomandazione.class).isPresent();
        }
        assertTrue(testamento);
        assertTrue(confessione);
        assertTrue(lettera);
    }

    @Test
    void laCasualitaNullaERifiutata() {
        assertThrows(IllegalArgumentException.class, () -> new GeneratoreFascicoli(null));
    }
}
