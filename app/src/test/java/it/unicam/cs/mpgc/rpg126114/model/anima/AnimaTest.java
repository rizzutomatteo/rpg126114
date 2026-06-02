package it.unicam.cs.mpgc.rpg126114.model.anima;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica il contratto di identita' delle anime e il comportamento
 * dei sottotipi.
 */
class AnimaTest {

    @Test
    void animeConStessoNomeEAnnoSonoLaStessaPersona() {
        Anima prima = new AnimaComune("Ettore Vanni", 1888);
        Anima seconda = new AnimaIllustre("Ettore Vanni", 1888, "Barone");

        assertEquals(prima, seconda);
        assertEquals(seconda, prima);
        assertEquals(prima.hashCode(), seconda.hashCode());
    }

    @Test
    void animeConAnnoDiversoSonoPersoneDiverse() {
        Anima prima = new AnimaComune("Ettore Vanni", 1888);
        Anima seconda = new AnimaComune("Ettore Vanni", 1923);

        assertNotEquals(prima, seconda);
    }

    @Test
    void unHashSetRiconosceLaStessaAnimaPresentataDueVolte() {
        Set<Anima> archivio = new HashSet<>();
        archivio.add(new AnimaComune("Rosa Malpighi", 1742));

        boolean aggiunta = archivio.add(new AnimaErrante("Rosa Malpighi", 1742));

        assertFalse(aggiunta);
        assertEquals(1, archivio.size());
    }

    @Test
    void bilancioKarmicoSommaMeritiESottraeColpe() {
        Anima anima = new AnimaComune("Lino Scarsi", 1969);
        anima.aggiungiVirtu(new Virtu("Generosita' coi poveri", 6));
        anima.aggiungiVirtu(new Virtu("Onesta' sul lavoro", 3));
        anima.aggiungiPeccato(new Peccato("Invidia del vicino", 4));

        assertEquals(5, anima.bilancioKarmico());
    }

    @Test
    void costruttoreRifiutaNomeVuoto() {
        assertThrows(IllegalArgumentException.class, () -> new AnimaComune("  ", 1900));
    }

    @Test
    void titoloVuotoRifiutatoPerAnimaIllustre() {
        assertThrows(IllegalArgumentException.class, () -> new AnimaIllustre("Ada Lovi", 1850, ""));
    }

    @Test
    void leListeEspoteNonSonoModificabiliDallEsterno() {
        Anima anima = new AnimaComune("Pia Castagna", 1931);

        assertThrows(UnsupportedOperationException.class,
                () -> anima.getPeccati().add(new Peccato("Furto di mele", 2)));
    }

    @Test
    void unAnimaErranteRicordaAlPiuUnaDichiarazione() {
        Anima errante = new AnimaErrante("Bice Nebbia", 1601);
        errante.aggiungiDichiarazione("Ricordo un campo di grano.");
        errante.aggiungiDichiarazione("E poi piu' nulla.");

        assertEquals(1, errante.dichiarazioniRicordate().size());
        assertEquals("Ricordo un campo di grano.", errante.dichiarazioniRicordate().get(0));
    }

    @Test
    void leAltreAnimeRicordanoTutteLeDichiarazioni() {
        Anima comune = new AnimaComune("Olga Ferri", 1955);
        comune.aggiungiDichiarazione("Ho sempre pagato le tasse.");
        comune.aggiungiDichiarazione("Quasi sempre.");

        assertEquals(2, comune.dichiarazioniRicordate().size());
    }

    @Test
    void laPresentazioneDipendeDalSottotipo() {
        Anima illustre = new AnimaIllustre("Carlo Brivio", 1799, "Conte");

        assertTrue(illustre.presentazione().contains("Conte"));
    }
}
