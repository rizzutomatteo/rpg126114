package it.unicam.cs.mpgc.rpg126114.model.anima;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica la validazione e l'uguaglianza per valore di peccati e virtu'.
 */
class PeccatoTest {

    @Test
    void gravitaFuoriScalaRifiutata() {
        assertThrows(IllegalArgumentException.class, () -> new Peccato("Superbia", 0));
        assertThrows(IllegalArgumentException.class, () -> new Peccato("Superbia", 11));
    }

    @Test
    void descrizioneVuotaRifiutata() {
        assertThrows(IllegalArgumentException.class, () -> new Peccato("   ", 5));
        assertThrows(IllegalArgumentException.class, () -> new Virtu(null, 5));
    }

    @Test
    void duePeccatiConStessiValoriSonoUguali() {
        assertEquals(new Peccato("Gola", 3), new Peccato("Gola", 3));
        assertEquals(new Virtu("Pazienza", 4), new Virtu("Pazienza", 4));
    }

    @Test
    void laSogliaCapitaleSepara() {
        assertTrue(new Peccato("Tradimento", 7).isCapitale());
        assertFalse(new Peccato("Pigrizia", 6).isCapitale());
    }
}
