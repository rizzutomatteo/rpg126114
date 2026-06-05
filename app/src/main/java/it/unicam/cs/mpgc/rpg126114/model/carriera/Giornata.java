package it.unicam.cs.mpgc.rpg126114.model.carriera;

import it.unicam.cs.mpgc.rpg126114.model.regole.Regolamento;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Verdetto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Una giornata di lavoro allo sportello: il regolamento in vigore,
 * quante anime vanno processate e i verdetti emessi finora.
 */
public class Giornata {

    private final int numero;
    private final int animePreviste;
    private final Regolamento regolamento;
    private final List<Verdetto> verdettiDelGiorno = new ArrayList<>();

    /**
     * @param numero        numero progressivo della giornata, almeno 1
     * @param animePreviste anime da processare, almeno 1
     * @param regolamento   il regolamento in vigore, non null
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    public Giornata(int numero, int animePreviste, Regolamento regolamento) {
        if (numero < 1) {
            throw new IllegalArgumentException("Numero di giornata non valido: " + numero);
        }
        if (animePreviste < 1) {
            throw new IllegalArgumentException("Una giornata prevede almeno un'anima");
        }
        if (regolamento == null) {
            throw new IllegalArgumentException("Una giornata richiede un regolamento");
        }
        this.numero = numero;
        this.animePreviste = animePreviste;
        this.regolamento = regolamento;
    }

    public int getNumero() {
        return numero;
    }

    public int getAnimePreviste() {
        return animePreviste;
    }

    public Regolamento getRegolamento() {
        return regolamento;
    }

    /**
     * @return vista non modificabile dei verdetti emessi oggi
     */
    public List<Verdetto> getVerdettiDelGiorno() {
        return Collections.unmodifiableList(verdettiDelGiorno);
    }

    /**
     * Registra un verdetto emesso in questa giornata.
     *
     * @param verdetto il verdetto, non null
     * @throws IllegalArgumentException se il verdetto e' null
     * @throws IllegalStateException    se la giornata e' gia' conclusa
     */
    public void registraVerdetto(Verdetto verdetto) {
        if (verdetto == null) {
            throw new IllegalArgumentException("Il verdetto non puo' essere null");
        }
        if (isConclusa()) {
            throw new IllegalStateException("La giornata " + numero + " e' gia' conclusa");
        }
        verdettiDelGiorno.add(verdetto);
    }

    /**
     * @return true se tutte le anime previste sono state giudicate
     */
    public boolean isConclusa() {
        return verdettiDelGiorno.size() >= animePreviste;
    }

    /**
     * @return quante anime restano da giudicare oggi
     */
    public int animeRimanenti() {
        return animePreviste - verdettiDelGiorno.size();
    }
}
