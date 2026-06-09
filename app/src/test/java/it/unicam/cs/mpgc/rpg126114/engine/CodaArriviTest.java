package it.unicam.cs.mpgc.rpg126114.engine;

import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;
import it.unicam.cs.mpgc.rpg126114.model.anima.AnimaComune;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica il produttore-consumatore della coda degli arrivi.
 */
class CodaArriviTest {

    private List<Anima> lottoDi(int quante) {
        List<Anima> lotto = new ArrayList<>();
        for (int i = 1; i <= quante; i++) {
            lotto.add(new AnimaComune("Arrivo " + i, 1900 + i));
        }
        return lotto;
    }

    private List<Anima> prelevaTutte(CodaArrivi coda, int attese, long scadenzaMillis)
            throws InterruptedException {
        List<Anima> raccolte = new ArrayList<>();
        long limite = System.currentTimeMillis() + scadenzaMillis;
        while (raccolte.size() < attese && System.currentTimeMillis() < limite) {
            Optional<Anima> anima = coda.preleva();
            if (anima.isPresent()) {
                raccolte.add(anima.get());
            } else {
                Thread.sleep(5);
            }
        }
        return raccolte;
    }

    @Test
    void tuttiGliArriviVengonoConsegnatiInOrdine() throws InterruptedException {
        CodaArrivi coda = new CodaArrivi();
        coda.avvia(lottoDi(3), 1);

        List<Anima> raccolte = prelevaTutte(coda, 3, 2000);

        assertEquals(3, raccolte.size());
        assertEquals("Arrivo 1", raccolte.get(0).getNome());
        assertEquals("Arrivo 3", raccolte.get(2).getNome());
        assertEquals(0, coda.inAttesa());
    }

    @Test
    void lOsservatoreVieneAvvisatoAOgniArrivo() throws InterruptedException {
        CodaArrivi coda = new CodaArrivi();
        AtomicInteger avvisi = new AtomicInteger();
        CountDownLatch tuttiArrivati = new CountDownLatch(3);
        coda.setOsservatoreArrivi(inAttesa -> {
            avvisi.incrementAndGet();
            tuttiArrivati.countDown();
        });

        coda.avvia(lottoDi(3), 1);

        assertTrue(tuttiArrivati.await(2, TimeUnit.SECONDS));
        assertEquals(3, avvisi.get());
    }

    @Test
    void fermareLaCodaInterrompeLeConsegne() throws InterruptedException {
        CodaArrivi coda = new CodaArrivi();
        coda.avvia(lottoDi(50), 50);

        Thread.sleep(120);
        coda.ferma();
        int consegnate = coda.inAttesa();

        Thread.sleep(150);
        assertEquals(consegnate, coda.inAttesa(), "Dopo ferma() non arrivano altre anime");
        assertTrue(consegnate < 50);
        assertFalse(coda.isAttiva());
    }

    @Test
    void laCodaSiRiavviaDopoEssereStataFermata() throws InterruptedException {
        CodaArrivi coda = new CodaArrivi();
        coda.avvia(lottoDi(5), 20);
        coda.ferma();

        coda.avvia(lottoDi(2), 1);
        List<Anima> raccolte = prelevaTutte(coda, 2, 2000);

        assertEquals(2, raccolte.size());
    }

    @Test
    void unAvvioDoppioVieneRifiutato() {
        CodaArrivi coda = new CodaArrivi();
        coda.avvia(lottoDi(10), 100);

        assertThrows(IllegalStateException.class, () -> coda.avvia(lottoDi(2), 1));

        coda.ferma();
    }

    @Test
    void iParametriVengonoValidati() {
        CodaArrivi coda = new CodaArrivi();

        assertThrows(IllegalArgumentException.class, () -> coda.avvia(List.of(), 10));
        assertThrows(IllegalArgumentException.class, () -> coda.avvia(null, 10));
        assertThrows(IllegalArgumentException.class, () -> coda.avvia(lottoDi(1), -1));
    }
}
