package it.unicam.cs.mpgc.rpg126114.model.documenti;

import it.unicam.cs.mpgc.rpg126114.model.anima.Peccato;
import it.unicam.cs.mpgc.rpg126114.model.anima.Virtu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Documento d'ufficio con il casellario morale completo dell'anima:
 * virtu' e peccati registrati negli archivi celesti e il bilancio
 * karmico risultante. Essendo redatta dagli archivi e' sempre
 * attendibile, ed e' l'unico documento obbligatorio del fascicolo:
 * senza, il giudizio sul bilancio sarebbe alla cieca.
 */
public class FedinaKarmica extends Documento {

    private final List<Peccato> peccatiRegistrati;
    private final List<Virtu> virtuRegistrate;

    /**
     * @param peccati peccati risultanti dagli archivi, non null
     * @param virtu   virtu' risultanti dagli archivi, non null
     * @throws IllegalArgumentException se una delle liste e' null
     */
    public FedinaKarmica(List<Peccato> peccati, List<Virtu> virtu) {
        super("Fedina Karmica", true);
        if (peccati == null || virtu == null) {
            throw new IllegalArgumentException(
                    "Le registrazioni della fedina non possono essere null");
        }
        this.peccatiRegistrati = new ArrayList<>(peccati);
        this.virtuRegistrate = new ArrayList<>(virtu);
    }

    /**
     * @return vista non modificabile dei peccati registrati
     */
    public List<Peccato> getPeccatiRegistrati() {
        return Collections.unmodifiableList(peccatiRegistrati);
    }

    /**
     * @return vista non modificabile delle virtu' registrate
     */
    public List<Virtu> getVirtuRegistrate() {
        return Collections.unmodifiableList(virtuRegistrate);
    }

    /**
     * Il bilancio karmico risultante dalla fedina: somma dei meriti
     * meno somma delle gravita'.
     *
     * @return il bilancio (puo' essere negativo)
     */
    public int bilancio() {
        int meriti = virtuRegistrate.stream().mapToInt(Virtu::getMerito).sum();
        int colpe = peccatiRegistrati.stream().mapToInt(Peccato::getGravita).sum();
        return meriti - colpe;
    }

    @Override
    public String contenuto() {
        StringBuilder testo = new StringBuilder();
        testo.append(elenco("Virtu' registrate", virtuRegistrate));
        testo.append("\n\n");
        testo.append(elenco("Peccati registrati", peccatiRegistrati));
        testo.append("\n\nBilancio karmico risultante: ");
        if (bilancio() >= 0) {
            testo.append('+');
        }
        testo.append(bilancio());
        return testo.toString();
    }

    private String elenco(String titolo, List<?> voci) {
        if (voci.isEmpty()) {
            return titolo + ": nessuna voce a registro.";
        }
        return voci.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n- ", titolo + ":\n- ", ""));
    }
}
