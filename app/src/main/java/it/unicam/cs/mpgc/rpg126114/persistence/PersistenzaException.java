package it.unicam.cs.mpgc.rpg126114.persistence;

/**
 * Segnala un errore di lettura o scrittura dello stato persistente,
 * incapsulando la causa tecnica originale.
 */
public class PersistenzaException extends RuntimeException {

    public PersistenzaException(String messaggio) {
        super(messaggio);
    }

    public PersistenzaException(String messaggio, Throwable causa) {
        super(messaggio, causa);
    }
}
