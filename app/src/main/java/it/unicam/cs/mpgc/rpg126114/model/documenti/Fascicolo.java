package it.unicam.cs.mpgc.rpg126114.model.documenti;

import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Il fascicolo di una pratica: raccoglie l'anima in giudizio e tutti i
 * documenti che la riguardano. E' l'unico oggetto che le regole del
 * regolamento ricevono in ingresso.
 */
public class Fascicolo {

    private final Anima anima;
    private final List<Documento> documenti = new ArrayList<>();

    /**
     * @param anima l'anima a cui il fascicolo si riferisce, non null
     * @throws IllegalArgumentException se l'anima e' null
     */
    public Fascicolo(Anima anima) {
        if (anima == null) {
            throw new IllegalArgumentException("Un fascicolo deve riferirsi a un'anima");
        }
        this.anima = anima;
    }

    public Anima getAnima() {
        return anima;
    }

    /**
     * @return vista non modificabile dei documenti raccolti
     */
    public List<Documento> getDocumenti() {
        return Collections.unmodifiableList(documenti);
    }

    /**
     * @param documento documento da allegare, non null
     * @throws IllegalArgumentException se il documento e' null
     */
    public void aggiungi(Documento documento) {
        if (documento == null) {
            throw new IllegalArgumentException("Non si puo' allegare un documento null");
        }
        documenti.add(documento);
    }

    /**
     * Cerca il primo documento del tipo richiesto.
     *
     * @param tipo classe del documento cercato
     * @param <T>  tipo concreto di documento
     * @return il documento, se presente
     */
    public <T extends Documento> Optional<T> trova(Class<T> tipo) {
        return documenti.stream()
                .filter(tipo::isInstance)
                .map(tipo::cast)
                .findFirst();
    }

    /**
     * Un fascicolo e' completo solo se contiene la Fedina Karmica,
     * documento d'ufficio obbligatorio.
     *
     * @return true se il fascicolo puo' andare a giudizio
     */
    public boolean isCompleto() {
        return trova(FedinaKarmica.class).isPresent();
    }

    /**
     * Verifica che il fascicolo sia in uno stato valido per il giudizio.
     *
     * @throws PraticaMalformataException se manca la Fedina Karmica
     */
    public void valida() {
        if (!isCompleto()) {
            throw new PraticaMalformataException(
                    "Il fascicolo di " + anima.getNome() + " e' privo della Fedina Karmica");
        }
    }

    @Override
    public String toString() {
        return "Fascicolo di " + anima + " (" + documenti.size() + " documenti)";
    }
}
