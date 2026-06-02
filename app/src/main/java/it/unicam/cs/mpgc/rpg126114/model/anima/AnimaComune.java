package it.unicam.cs.mpgc.rpg126114.model.anima;

/**
 * Un'anima qualunque, senza titoli ne' particolarita': la maggior parte
 * delle pratiche che passano per lo sportello.
 */
public class AnimaComune extends Anima {

    public AnimaComune(String nome, int annoMorte) {
        super(nome, annoMorte);
    }

    @Override
    public String presentazione() {
        return "Mi chiamo " + getNome() + ", una vita normale, lo giuro. Posso passare?";
    }
}
