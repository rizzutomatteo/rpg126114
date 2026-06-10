package it.unicam.cs.mpgc.rpg126114.engine;

import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * La coda d'attesa dello sportello: un thread produttore consegna le
 * anime del lotto a intervalli regolari, mentre il giocatore lavora.
 *
 * <p>Schema produttore-consumatore: il lotto viene generato prima di
 * avviare il thread, cosi' il produttore possiede i propri dati e non
 * condivide stato mutabile con il resto dell'applicazione; lo scambio
 * avviene solo attraverso la {@link BlockingQueue}, che e' thread-safe.
 * Il flag {@code attiva} e' {@code volatile} perche' letto dal thread
 * produttore e scritto da quello che ferma la coda.</p>
 *
 * <p>Nota per la GUI: l'osservatore degli arrivi viene invocato sul
 * thread produttore; chi aggiorna nodi JavaFX deve rientrare nel thread
 * dell'interfaccia con {@code Platform.runLater}.</p>
 */
public class CodaArrivi {

    public static final long INTERVALLO_PREDEFINITO_MILLIS = 2500;

    private final BlockingQueue<Anima> coda = new LinkedBlockingQueue<>();
    private final Random random;
    private ExecutorService esecutore;
    private volatile boolean attiva;
    private volatile Consumer<Integer> osservatoreArrivi;

    public CodaArrivi() {
        this(new Random());
    }

    /**
     * @param random sorgente di casualita' per il ritmo degli arrivi, non null
     * @throws IllegalArgumentException se random e' null
     */
    public CodaArrivi(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("La sorgente di casualita' non puo' essere null");
        }
        this.random = random;
    }

    /**
     * Avvia la consegna del lotto a intervalli fissi (comodo nei test).
     *
     * @param arrivi           le anime da consegnare, in ordine, non vuoto
     * @param intervalloMillis attesa tra un arrivo e il successivo, non negativa
     * @throws IllegalArgumentException se i parametri non sono validi
     * @throws IllegalStateException    se la coda e' gia' attiva
     */
    public void avvia(List<Anima> arrivi, long intervalloMillis) {
        avvia(arrivi, intervalloMillis, intervalloMillis, intervalloMillis);
    }

    /**
     * Avvia la consegna con un ritmo "da sportello": il primo arrivo dopo
     * una breve attesa (il Funzionario non deve fissare una scrivania
     * vuota), i successivi a intervalli casuali nell'intervallo indicato,
     * cosi' la coda si muove davvero mentre il giocatore lavora.
     *
     * @param arrivi            le anime da consegnare, in ordine, non vuoto
     * @param primoArrivoMillis attesa prima del primo arrivo, non negativa
     * @param minimoMillis      attesa minima tra gli arrivi successivi
     * @param massimoMillis     attesa massima tra gli arrivi successivi
     * @throws IllegalArgumentException se i parametri non sono validi
     * @throws IllegalStateException    se la coda e' gia' attiva
     */
    public void avvia(List<Anima> arrivi, long primoArrivoMillis,
                      long minimoMillis, long massimoMillis) {
        if (arrivi == null || arrivi.isEmpty()) {
            throw new IllegalArgumentException("Il lotto degli arrivi non puo' essere vuoto");
        }
        if (primoArrivoMillis < 0 || minimoMillis < 0 || massimoMillis < minimoMillis) {
            throw new IllegalArgumentException("Ritmo degli arrivi non valido");
        }
        if (attiva) {
            throw new IllegalStateException("La coda degli arrivi e' gia' attiva");
        }
        coda.clear();
        attiva = true;
        List<Anima> lotto = new ArrayList<>(arrivi);
        esecutore = Executors.newSingleThreadExecutor(compito -> {
            Thread thread = new Thread(compito, "coda-arrivi");
            thread.setDaemon(true);
            return thread;
        });
        esecutore.submit(() -> consegna(lotto, primoArrivoMillis, minimoMillis, massimoMillis));
    }

    private void consegna(List<Anima> lotto, long primoArrivoMillis,
                          long minimoMillis, long massimoMillis) {
        try {
            boolean primoArrivo = true;
            for (Anima anima : lotto) {
                if (!attiva) {
                    return;
                }
                Thread.sleep(primoArrivo ? primoArrivoMillis
                        : attesaCasuale(minimoMillis, massimoMillis));
                primoArrivo = false;
                if (!attiva) {
                    return;
                }
                coda.put(anima);
                Consumer<Integer> osservatore = osservatoreArrivi;
                if (osservatore != null) {
                    osservatore.accept(coda.size());
                }
            }
        } catch (InterruptedException interruzione) {
            Thread.currentThread().interrupt();
        } finally {
            attiva = false;
        }
    }

    private long attesaCasuale(long minimoMillis, long massimoMillis) {
        long ampiezza = massimoMillis - minimoMillis;
        if (ampiezza <= 0) {
            return minimoMillis;
        }
        return minimoMillis + random.nextLong(ampiezza + 1);
    }

    /**
     * Preleva la prossima anima in attesa, se gia' arrivata.
     *
     * @return l'anima in testa alla coda, o vuoto se lo sportello e' vuoto
     */
    public Optional<Anima> preleva() {
        return Optional.ofNullable(coda.poll());
    }

    /**
     * @return quante anime aspettano allo sportello
     */
    public int inAttesa() {
        return coda.size();
    }

    /**
     * @return true se il produttore sta ancora consegnando arrivi
     */
    public boolean isAttiva() {
        return attiva;
    }

    /**
     * Registra chi viene avvisato a ogni nuovo arrivo (con il numero di
     * anime in attesa). L'avviso parte dal thread produttore.
     *
     * @param osservatore il destinatario degli avvisi, o null per nessuno
     */
    public void setOsservatoreArrivi(Consumer<Integer> osservatore) {
        this.osservatoreArrivi = osservatore;
    }

    /**
     * Ferma il produttore e attende la terminazione del thread, cosi'
     * la coda puo' essere riavviata in sicurezza per la giornata dopo.
     */
    public void ferma() {
        attiva = false;
        if (esecutore == null) {
            return;
        }
        esecutore.shutdownNow();
        try {
            esecutore.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException interruzione) {
            Thread.currentThread().interrupt();
        }
        esecutore = null;
    }
}
