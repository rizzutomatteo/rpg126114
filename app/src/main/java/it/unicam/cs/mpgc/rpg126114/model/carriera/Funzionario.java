package it.unicam.cs.mpgc.rpg126114.model.carriera;

import it.unicam.cs.mpgc.rpg126114.model.verdetti.Timbro;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Il Funzionario del Purgatorio: il personaggio del giocatore.
 *
 * <p>Accumula karma con i verdetti corretti, sale di livello ogni
 * {@link #KARMA_PER_LIVELLO} punti e spende i punti abilita' guadagnati
 * per potenziare Intuito (premio extra sui verdetti corretti) e
 * Pazienza (colloqui disponibili in una giornata). Sotto la soglia di
 * licenziamento la carriera finisce.</p>
 */
public class Funzionario {

    public static final int KARMA_PER_LIVELLO = 50;
    public static final int SOGLIA_LICENZIAMENTO = -50;
    public static final int COLLOQUI_BASE = 1;

    private final String nome;
    private int karma;
    private int livello = 1;
    private int puntiAbilita;
    private int intuito;
    private int pazienza;

    /**
     * @param nome il nome del Funzionario, non vuoto
     * @throws IllegalArgumentException se il nome e' vuoto
     */
    public Funzionario(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Il nome del Funzionario non puo' essere vuoto");
        }
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public int getKarma() {
        return karma;
    }

    public int getLivello() {
        return livello;
    }

    public int getPuntiAbilita() {
        return puntiAbilita;
    }

    public int getIntuito() {
        return intuito;
    }

    public int getPazienza() {
        return pazienza;
    }

    /**
     * Registra il karma di un verdetto e gestisce le promozioni: ogni
     * {@link #KARMA_PER_LIVELLO} punti il Funzionario sale di livello e
     * guadagna un punto abilita'.
     *
     * @param delta variazione di karma (anche negativa)
     * @return il numero di promozioni ottenute con questa variazione
     */
    public int aggiungiKarma(int delta) {
        karma += delta;
        int promozioni = 0;
        while (karma >= livello * KARMA_PER_LIVELLO) {
            livello++;
            puntiAbilita++;
            promozioni++;
        }
        return promozioni;
    }

    /**
     * @return true se il karma e' precipitato sotto la soglia di licenziamento
     */
    public boolean isLicenziato() {
        return karma <= SOGLIA_LICENZIAMENTO;
    }

    /**
     * Spende un punto abilita' per potenziare l'Intuito.
     *
     * @throws IllegalStateException se non ci sono punti abilita'
     */
    public void potenziaIntuito() {
        spendiPuntoAbilita();
        intuito++;
    }

    /**
     * Spende un punto abilita' per potenziare la Pazienza.
     *
     * @throws IllegalStateException se non ci sono punti abilita'
     */
    public void potenziaPazienza() {
        spendiPuntoAbilita();
        pazienza++;
    }

    private void spendiPuntoAbilita() {
        if (puntiAbilita <= 0) {
            throw new IllegalStateException("Nessun punto abilita' da spendere");
        }
        puntiAbilita--;
    }

    /**
     * @return i colloqui che il Funzionario puo' concedere in una giornata
     */
    public int colloquiPerGiornata() {
        return COLLOQUI_BASE + pazienza;
    }

    /**
     * @return i timbri utilizzabili al livello attuale
     */
    public List<Timbro> timbriDisponibili() {
        return Arrays.stream(Timbro.values())
                .filter(timbro -> timbro.getLivelloRichiesto() <= livello)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return nome + " (livello " + livello + ", karma " + karma + ")";
    }
}
