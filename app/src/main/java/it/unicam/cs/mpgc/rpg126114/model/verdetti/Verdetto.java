package it.unicam.cs.mpgc.rpg126114.model.verdetti;

import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;

/**
 * Il verdetto emesso dal Funzionario per un'anima: destinazione scelta,
 * timbro usato e giornata in cui e' stato pronunciato.
 */
public final class Verdetto {

    private final Anima anima;
    private final Destinazione destinazione;
    private final Timbro timbro;
    private final int numeroGiornata;

    /**
     * @param anima          l'anima giudicata, non null
     * @param destinazione   la destinazione assegnata, non null
     * @param timbro         il timbro usato, non null
     * @param numeroGiornata la giornata del giudizio, almeno 1
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    public Verdetto(Anima anima, Destinazione destinazione, Timbro timbro, int numeroGiornata) {
        if (anima == null || destinazione == null || timbro == null) {
            throw new IllegalArgumentException("Un verdetto richiede anima, destinazione e timbro");
        }
        if (numeroGiornata < 1) {
            throw new IllegalArgumentException("Numero di giornata non valido: " + numeroGiornata);
        }
        this.anima = anima;
        this.destinazione = destinazione;
        this.timbro = timbro;
        this.numeroGiornata = numeroGiornata;
    }

    public Anima getAnima() {
        return anima;
    }

    public Destinazione getDestinazione() {
        return destinazione;
    }

    public Timbro getTimbro() {
        return timbro;
    }

    public int getNumeroGiornata() {
        return numeroGiornata;
    }

    @Override
    public String toString() {
        return "G" + numeroGiornata + " - " + anima + " -> " + destinazione.getEtichetta()
                + " [" + timbro.getEtichetta() + "]";
    }
}
