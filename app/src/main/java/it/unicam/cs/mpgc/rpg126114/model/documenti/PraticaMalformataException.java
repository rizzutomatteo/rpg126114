package it.unicam.cs.mpgc.rpg126114.model.documenti;

/**
 * Segnala un fascicolo in uno stato non valido per il giudizio,
 * ad esempio privo della Fedina Karmica obbligatoria.
 */
public class PraticaMalformataException extends IllegalStateException {

    public PraticaMalformataException(String messaggio) {
        super(messaggio);
    }
}
