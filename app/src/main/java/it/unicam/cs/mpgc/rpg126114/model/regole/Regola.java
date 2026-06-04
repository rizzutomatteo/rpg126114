package it.unicam.cs.mpgc.rpg126114.model.regole;

import it.unicam.cs.mpgc.rpg126114.model.documenti.Fascicolo;

import java.util.Optional;

/**
 * Una regola del regolamento celeste: esamina un fascicolo e, se e'
 * applicabile al caso, produce un {@link Esito}.
 *
 * <p>E' il punto di estensione principale del progetto: per introdurre
 * un nuovo criterio di giudizio basta una nuova implementazione, senza
 * toccare il motore (principio Open/Closed). Essendo un'interfaccia
 * funzionale, nei test una regola puo' essere espressa come lambda.</p>
 */
@FunctionalInterface
public interface Regola {

    /**
     * Esamina il fascicolo e produce un esito se la regola e' applicabile.
     *
     * @param fascicolo il fascicolo a giudizio, gia' validato
     * @return l'esito, oppure {@link Optional#empty()} se la regola non si applica
     */
    Optional<Esito> valuta(Fascicolo fascicolo);

    /**
     * @return il nome con cui la regola compare a verbale
     */
    default String descrizione() {
        return getClass().getSimpleName();
    }
}
