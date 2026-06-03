package it.unicam.cs.mpgc.rpg126114.model.documenti;

import it.unicam.cs.mpgc.rpg126114.model.anima.Peccato;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Documento d'ufficio che elenca i peccati registrati a carico dell'anima.
 * Essendo redatta dagli archivi celesti e' sempre attendibile.
 */
public class FedinaKarmica extends Documento {

    private final List<Peccato> peccatiRegistrati;

    /**
     * @param peccati peccati risultanti dagli archivi, non null
     * @throws IllegalArgumentException se la lista e' null
     */
    public FedinaKarmica(List<Peccato> peccati) {
        super("Fedina Karmica", true);
        if (peccati == null) {
            throw new IllegalArgumentException("La lista dei peccati non puo' essere null");
        }
        this.peccatiRegistrati = new ArrayList<>(peccati);
    }

    /**
     * @return vista non modificabile dei peccati registrati
     */
    public List<Peccato> getPeccatiRegistrati() {
        return Collections.unmodifiableList(peccatiRegistrati);
    }

    @Override
    public String contenuto() {
        if (peccatiRegistrati.isEmpty()) {
            return "Nessun peccato registrato negli archivi.";
        }
        return peccatiRegistrati.stream()
                .map(Peccato::toString)
                .collect(Collectors.joining("\n- ", "Peccati registrati:\n- ", ""));
    }
}
