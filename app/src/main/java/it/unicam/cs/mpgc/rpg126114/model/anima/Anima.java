package it.unicam.cs.mpgc.rpg126114.model.anima;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Classe base astratta per tutte le anime in attesa di giudizio.
 *
 * <p>L'identita' di un'anima e' definita da nome e anno di morte: due anime
 * con gli stessi valori sono la stessa persona, qualunque sia il sottotipo
 * con cui vengono rappresentate. Per questo {@link #equals(Object)} e
 * {@link #hashCode()} sono dichiarati {@code final}: i sottotipi possono
 * variare il comportamento, mai l'identita'.</p>
 */
public abstract class Anima {

    private final String nome;
    private final int annoMorte;
    private final List<Peccato> peccati = new ArrayList<>();
    private final List<Virtu> virtu = new ArrayList<>();
    private final List<String> dichiarazioni = new ArrayList<>();

    /**
     * @param nome      nome dell'anima, non vuoto
     * @param annoMorte anno di morte (anche remoto, per le anime piu' antiche)
     * @throws IllegalArgumentException se il nome e' vuoto
     */
    protected Anima(String nome, int annoMorte) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Il nome dell'anima non puo' essere vuoto");
        }
        this.nome = nome;
        this.annoMorte = annoMorte;
    }

    /**
     * @return la frase con cui l'anima si presenta allo sportello
     */
    public abstract String presentazione();

    public String getNome() {
        return nome;
    }

    public int getAnnoMorte() {
        return annoMorte;
    }

    /**
     * @return vista non modificabile dei peccati registrati
     */
    public List<Peccato> getPeccati() {
        return Collections.unmodifiableList(peccati);
    }

    /**
     * @return vista non modificabile delle virtu' registrate
     */
    public List<Virtu> getVirtu() {
        return Collections.unmodifiableList(virtu);
    }

    /**
     * Le dichiarazioni che l'anima e' in grado di rendere durante il
     * colloquio. I sottotipi possono ricordarne solo una parte.
     *
     * @return vista non modificabile delle dichiarazioni disponibili
     */
    public List<String> dichiarazioniRicordate() {
        return Collections.unmodifiableList(dichiarazioni);
    }

    /**
     * @param peccato peccato da registrare, non null
     * @throws IllegalArgumentException se il peccato e' null
     */
    public void aggiungiPeccato(Peccato peccato) {
        if (peccato == null) {
            throw new IllegalArgumentException("Il peccato non puo' essere null");
        }
        peccati.add(peccato);
    }

    /**
     * @param virtuosa virtu' da registrare, non null
     * @throws IllegalArgumentException se la virtu' e' null
     */
    public void aggiungiVirtu(Virtu virtuosa) {
        if (virtuosa == null) {
            throw new IllegalArgumentException("La virtu' non puo' essere null");
        }
        virtu.add(virtuosa);
    }

    /**
     * @param dichiarazione frase pronunciata dall'anima, non vuota
     * @throws IllegalArgumentException se la dichiarazione e' vuota
     */
    public void aggiungiDichiarazione(String dichiarazione) {
        if (dichiarazione == null || dichiarazione.isBlank()) {
            throw new IllegalArgumentException("La dichiarazione non puo' essere vuota");
        }
        dichiarazioni.add(dichiarazione);
    }

    /**
     * Bilancio morale complessivo dell'anima: somma dei meriti delle virtu'
     * meno somma delle gravita' dei peccati.
     *
     * @return il bilancio karmico (puo' essere negativo)
     */
    public int bilancioKarmico() {
        int meriti = virtu.stream().mapToInt(Virtu::getMerito).sum();
        int colpe = peccati.stream().mapToInt(Peccato::getGravita).sum();
        return meriti - colpe;
    }

    @Override
    public final boolean equals(Object oggetto) {
        if (this == oggetto) {
            return true;
        }
        if (!(oggetto instanceof Anima)) {
            return false;
        }
        Anima altra = (Anima) oggetto;
        return annoMorte == altra.annoMorte && nome.equals(altra.nome);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(nome, annoMorte);
    }

    @Override
    public String toString() {
        return nome + " (" + annoMorte + ")";
    }
}
