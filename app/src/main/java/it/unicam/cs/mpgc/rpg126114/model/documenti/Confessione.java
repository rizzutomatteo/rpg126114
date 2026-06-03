package it.unicam.cs.mpgc.rpg126114.model.documenti;

import it.unicam.cs.mpgc.rpg126114.model.anima.Peccato;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * La confessione resa dall'anima allo sportello. Confrontarla con la
 * Fedina Karmica permette di scoprire i peccati taciuti.
 */
public class Confessione extends Documento {

    private final List<String> peccatiConfessati;

    /**
     * @param peccatiConfessati descrizioni dei peccati ammessi, non null
     * @throws IllegalArgumentException se la lista e' null
     */
    public Confessione(List<String> peccatiConfessati) {
        super("Confessione", false);
        if (peccatiConfessati == null) {
            throw new IllegalArgumentException("La lista dei peccati confessati non puo' essere null");
        }
        this.peccatiConfessati = new ArrayList<>(peccatiConfessati);
    }

    /**
     * @return vista non modificabile dei peccati ammessi
     */
    public List<String> getPeccatiConfessati() {
        return Collections.unmodifiableList(peccatiConfessati);
    }

    /**
     * @param peccato peccato da cercare nella confessione
     * @return true se l'anima ha taciuto il peccato indicato
     */
    public boolean omette(Peccato peccato) {
        return !peccatiConfessati.contains(peccato.getDescrizione());
    }

    @Override
    public String contenuto() {
        if (peccatiConfessati.isEmpty()) {
            return "\"Non ho nulla da confessare.\"";
        }
        return "L'anima confessa: " + String.join("; ", peccatiConfessati) + ".";
    }
}
