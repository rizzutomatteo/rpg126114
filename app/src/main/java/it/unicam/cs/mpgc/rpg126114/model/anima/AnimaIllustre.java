package it.unicam.cs.mpgc.rpg126114.model.anima;

/**
 * Un'anima che in vita ha ricoperto cariche o goduto di fama: si presenta
 * con il proprio titolo e spesso arriva accompagnata da lettere di
 * raccomandazione (non sempre attendibili).
 */
public class AnimaIllustre extends Anima {

    private final String titolo;

    /**
     * @param nome      nome dell'anima, non vuoto
     * @param annoMorte anno di morte
     * @param titolo    titolo onorifico portato in vita, non vuoto
     * @throws IllegalArgumentException se nome o titolo sono vuoti
     */
    public AnimaIllustre(String nome, int annoMorte, String titolo) {
        super(nome, annoMorte);
        if (titolo == null || titolo.isBlank()) {
            throw new IllegalArgumentException("Il titolo di un'anima illustre non puo' essere vuoto");
        }
        this.titolo = titolo;
    }

    public String getTitolo() {
        return titolo;
    }

    @Override
    public String presentazione() {
        return "Sono " + titolo + " " + getNome() + ". Confido che la mia pratica riceva il riguardo dovuto.";
    }
}
