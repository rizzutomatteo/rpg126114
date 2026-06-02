package it.unicam.cs.mpgc.rpg126114.model.anima;

import java.util.List;

/**
 * Un'anima che ha vagato a lungo prima di presentarsi allo sportello:
 * la memoria si e' consumata e durante il colloquio riesce a rendere
 * al piu' una dichiarazione.
 */
public class AnimaErrante extends Anima {

    public AnimaErrante(String nome, int annoMorte) {
        super(nome, annoMorte);
    }

    @Override
    public String presentazione() {
        return "Io... non ricordo bene. Il mio nome era " + getNome() + ", credo. Da quanto tempo sono qui?";
    }

    /**
     * {@inheritDoc}
     *
     * <p>Un'anima errante ricorda al piu' la prima dichiarazione.</p>
     */
    @Override
    public List<String> dichiarazioniRicordate() {
        List<String> tutte = super.dichiarazioniRicordate();
        if (tutte.size() <= 1) {
            return tutte;
        }
        return tutte.subList(0, 1);
    }
}
