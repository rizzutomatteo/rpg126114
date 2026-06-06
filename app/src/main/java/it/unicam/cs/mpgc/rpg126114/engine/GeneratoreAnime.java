package it.unicam.cs.mpgc.rpg126114.engine;

import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;
import it.unicam.cs.mpgc.rpg126114.model.carriera.ArchivioAnime;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Verdetto;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * Distribuisce le anime allo sportello pescando da un pool mescolato.
 *
 * <p>Con una probabilita' configurabile fa ripresentare un'anima gia'
 * giudicata: un impostore che il Funzionario puo' smascherare grazie
 * all'archivio. La casualita' e' iniettata dall'esterno per rendere il
 * comportamento riproducibile nei test.</p>
 */
public class GeneratoreAnime {

    public static final int PROBABILITA_IMPOSTORE_PREDEFINITA = 10;

    private final List<Anima> pool;
    private final Random random;
    private final int probabilitaImpostore;
    private final Deque<Anima> mazzo = new ArrayDeque<>();

    /**
     * @param pool                 le anime disponibili, non vuoto e senza null
     * @param random               sorgente di casualita', non null
     * @param probabilitaImpostore probabilita' percentuale (0-100) di un impostore
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    public GeneratoreAnime(List<Anima> pool, Random random, int probabilitaImpostore) {
        if (pool == null || pool.isEmpty() || pool.stream().anyMatch(anima -> anima == null)) {
            throw new IllegalArgumentException("Il pool delle anime deve esistere e non contenere null");
        }
        if (random == null) {
            throw new IllegalArgumentException("La sorgente di casualita' non puo' essere null");
        }
        if (probabilitaImpostore < 0 || probabilitaImpostore > 100) {
            throw new IllegalArgumentException(
                    "Probabilita' impostore fuori scala: " + probabilitaImpostore);
        }
        this.pool = new ArrayList<>(pool);
        this.random = random;
        this.probabilitaImpostore = probabilitaImpostore;
    }

    /**
     * @param archivio l'archivio delle anime gia' giudicate
     * @return quante anime del pool non sono ancora state giudicate
     */
    public int animeFresche(ArchivioAnime archivio) {
        return (int) pool.stream()
                .filter(anima -> !archivio.giaGiudicata(anima))
                .count();
    }

    /**
     * Fa arrivare la prossima anima allo sportello. Di norma e' un'anima
     * mai giudicata; occasionalmente e' un impostore pescato dall'archivio.
     *
     * @param archivio l'archivio delle anime gia' giudicate
     * @return la prossima anima in coda
     * @throws IllegalStateException se il pool delle anime fresche e' esaurito
     */
    public Anima prossima(ArchivioAnime archivio) {
        List<Verdetto> giudicate = archivio.getVerdetti();
        if (!giudicate.isEmpty() && random.nextInt(100) < probabilitaImpostore) {
            return giudicate.get(random.nextInt(giudicate.size())).getAnima();
        }
        return pescaFresca(archivio);
    }

    private Anima pescaFresca(ArchivioAnime archivio) {
        if (animeFresche(archivio) == 0) {
            throw new IllegalStateException("Il pool delle anime e' esaurito: nessuna pratica nuova");
        }
        while (true) {
            if (mazzo.isEmpty()) {
                rimescola(archivio);
            }
            Anima candidata = mazzo.poll();
            if (!archivio.giaGiudicata(candidata)) {
                return candidata;
            }
        }
    }

    private void rimescola(ArchivioAnime archivio) {
        List<Anima> fresche = new ArrayList<>();
        for (Anima anima : pool) {
            if (!archivio.giaGiudicata(anima)) {
                fresche.add(anima);
            }
        }
        Collections.shuffle(fresche, random);
        mazzo.addAll(fresche);
    }
}
