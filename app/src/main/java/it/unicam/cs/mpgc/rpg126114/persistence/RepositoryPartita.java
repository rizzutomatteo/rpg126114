package it.unicam.cs.mpgc.rpg126114.persistence;

import it.unicam.cs.mpgc.rpg126114.model.carriera.Partita;

import java.util.Optional;

/**
 * Contratto di persistenza per lo stato della carriera.
 *
 * <p>Il resto dell'applicazione dipende solo da questa astrazione
 * (inversione delle dipendenze): l'implementazione attuale salva su file
 * JSON, ma un backend XML o un database JPA si integrano scrivendo una
 * nuova implementazione, senza toccare GUI ed engine.</p>
 */
public interface RepositoryPartita {

    /**
     * Salva lo stato della partita, sovrascrivendo l'eventuale
     * salvataggio precedente.
     *
     * @param partita la partita da salvare, non null
     * @throws IllegalArgumentException se la partita e' null
     * @throws PersistenzaException     se il salvataggio fallisce
     */
    void salva(Partita partita);

    /**
     * Carica l'ultima partita salvata.
     *
     * @return la partita, o vuoto se non esiste alcun salvataggio
     * @throws PersistenzaException se il salvataggio esiste ma e' illeggibile
     */
    Optional<Partita> carica();

    /**
     * @return true se esiste un salvataggio da caricare
     */
    boolean esisteSalvataggio();
}
