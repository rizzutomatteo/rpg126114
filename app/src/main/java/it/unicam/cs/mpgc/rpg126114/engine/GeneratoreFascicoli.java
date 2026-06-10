package it.unicam.cs.mpgc.rpg126114.engine;

import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;
import it.unicam.cs.mpgc.rpg126114.model.anima.AnimaIllustre;
import it.unicam.cs.mpgc.rpg126114.model.anima.Peccato;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Confessione;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Fascicolo;
import it.unicam.cs.mpgc.rpg126114.model.documenti.FedinaKarmica;
import it.unicam.cs.mpgc.rpg126114.model.documenti.LetteraRaccomandazione;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Testamento;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Compone il fascicolo di un'anima: la Fedina Karmica d'ufficio e' sempre
 * presente, gli altri documenti compaiono con probabilita' diverse e
 * possono nascondere contraddizioni (testamenti postumi, confessioni
 * reticenti, firme contraffatte) che le regole sapranno smascherare.
 */
public class GeneratoreFascicoli {

    private static final int PERCENTO_TESTAMENTO = 60;
    private static final int PERCENTO_TESTAMENTO_POSTUMO = 25;
    private static final int PERCENTO_CONFESSIONE = 60;
    private static final int PERCENTO_CONFESSA_CAPITALE = 60;
    private static final int PERCENTO_CONFESSA_VENIALE = 70;
    private static final int PERCENTO_LETTERA_ILLUSTRE = 75;
    private static final int PERCENTO_LETTERA_COMUNE = 10;
    private static final int PERCENTO_FIRMA_AUTENTICA_ILLUSTRE = 50;
    private static final int PERCENTO_FIRMA_AUTENTICA_COMUNE = 30;

    private static final String[] FRASI_TESTAMENTO = {
            "Lascio ogni mio avere al gatto Ernesto.",
            "Che i miei debiti seguano i miei creditori.",
            "Tutto alla parrocchia, tranne la cantina.",
            "Ai nipoti la casa, alle nipoti il buon esempio.",
            "Nulla a nessuno: portero' tutto con me."
    };

    private static final String[] FIRMATARI = {
            "Arcangelo Capoufficio",
            "Madama la Marchesa del Pianto",
            "Gran Visir delle Nuvole",
            "Direttore del Coro Celeste"
    };

    private final Random random;

    /**
     * @param random sorgente di casualita', non null
     * @throws IllegalArgumentException se random e' null
     */
    public GeneratoreFascicoli(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("La sorgente di casualita' non puo' essere null");
        }
        this.random = random;
    }

    /**
     * Compone il fascicolo dell'anima con la fedina obbligatoria e gli
     * eventuali documenti accessori.
     *
     * @param anima l'anima della pratica, non null
     * @return un fascicolo completo e valido
     */
    public Fascicolo componi(Anima anima) {
        Fascicolo fascicolo = new Fascicolo(anima);
        fascicolo.aggiungi(new FedinaKarmica(anima.getPeccati(), anima.getVirtu()));
        if (chance(PERCENTO_TESTAMENTO)) {
            fascicolo.aggiungi(componiTestamento(anima));
        }
        if (chance(PERCENTO_CONFESSIONE)) {
            fascicolo.aggiungi(componiConfessione(anima));
        }
        if (chance(percentoLettera(anima))) {
            fascicolo.aggiungi(componiLettera(anima));
        }
        return fascicolo;
    }

    private Testamento componiTestamento(Anima anima) {
        int annoRedazione;
        if (chance(PERCENTO_TESTAMENTO_POSTUMO)) {
            annoRedazione = anima.getAnnoMorte() + 1 + random.nextInt(10);
        } else {
            annoRedazione = anima.getAnnoMorte() - random.nextInt(31);
        }
        String volonta = FRASI_TESTAMENTO[random.nextInt(FRASI_TESTAMENTO.length)];
        return new Testamento(volonta, annoRedazione);
    }

    private Confessione componiConfessione(Anima anima) {
        List<String> confessati = new ArrayList<>();
        for (Peccato peccato : anima.getPeccati()) {
            int percento = peccato.isCapitale()
                    ? PERCENTO_CONFESSA_CAPITALE
                    : PERCENTO_CONFESSA_VENIALE;
            if (chance(percento)) {
                confessati.add(peccato.getDescrizione());
            }
        }
        return new Confessione(confessati);
    }

    private int percentoLettera(Anima anima) {
        return anima instanceof AnimaIllustre ? PERCENTO_LETTERA_ILLUSTRE : PERCENTO_LETTERA_COMUNE;
    }

    private LetteraRaccomandazione componiLettera(Anima anima) {
        String firmatario = FIRMATARI[random.nextInt(FIRMATARI.length)];
        if (anima instanceof AnimaIllustre) {
            int peso = 2 + random.nextInt(LetteraRaccomandazione.PESO_MASSIMO - 1);
            return new LetteraRaccomandazione(firmatario, peso,
                    chance(PERCENTO_FIRMA_AUTENTICA_ILLUSTRE));
        }
        int peso = 1 + random.nextInt(3);
        return new LetteraRaccomandazione(firmatario, peso,
                chance(PERCENTO_FIRMA_AUTENTICA_COMUNE));
    }

    private boolean chance(int percento) {
        return random.nextInt(100) < percento;
    }
}
