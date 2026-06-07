package it.unicam.cs.mpgc.rpg126114.persistence;

import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;
import it.unicam.cs.mpgc.rpg126114.model.anima.AnimaErrante;
import it.unicam.cs.mpgc.rpg126114.model.anima.AnimaIllustre;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica che il pool delle anime nelle risorse sia ben formato e
 * abbastanza vario da alimentare tutte le regole del gioco.
 */
class CaricatorePoolTest {

    private final List<Anima> pool = new CaricatorePool().caricaAnime();

    @Test
    void ilPoolEsisteEdEPopolato() {
        assertTrue(pool.size() >= 20, "Servono almeno 20 anime per le prime giornate");
    }

    @Test
    void nonCiSonoAnimeDuplicate() {
        Set<Anima> uniche = new HashSet<>(pool);

        assertEquals(pool.size(), uniche.size());
    }

    @Test
    void ilPoolContieneTuttiISottotipi() {
        assertTrue(pool.stream().anyMatch(anima -> anima instanceof AnimaIllustre));
        assertTrue(pool.stream().anyMatch(anima -> anima instanceof AnimaErrante));
    }

    @Test
    void ogniAnimaHaAlmenoUnaDichiarazionePerIlColloquio() {
        assertTrue(pool.stream()
                .noneMatch(anima -> anima.dichiarazioniRicordate().isEmpty()));
    }

    @Test
    void cisonoAnimeAntichePerLaRegolaDellAnzianita() {
        assertTrue(pool.stream().anyMatch(anima -> anima.getAnnoMorte() < 1700));
    }

    @Test
    void iBilanciKarmiciCopronoTuttiICasi() {
        assertTrue(pool.stream().anyMatch(anima -> anima.bilancioKarmico() >= 5),
                "Serve almeno una candidata al Paradiso");
        assertTrue(pool.stream().anyMatch(anima -> anima.bilancioKarmico() <= -5),
                "Serve almeno un candidato all'Inferno");
        assertFalse(pool.stream().allMatch(anima -> anima.getPeccati().isEmpty()),
                "Le fedine non possono essere tutte immacolate");
    }

    @Test
    void leAnimeIllustriHannoUnTitolo() {
        pool.stream()
                .filter(anima -> anima instanceof AnimaIllustre)
                .map(anima -> (AnimaIllustre) anima)
                .forEach(illustre -> assertFalse(illustre.getTitolo().isBlank()));
    }
}
