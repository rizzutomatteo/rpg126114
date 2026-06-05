package it.unicam.cs.mpgc.rpg126114.model.carriera;

import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Verdetto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * L'archivio delle anime gia' giudicate. Sfrutta il contratto
 * equals/hashCode di {@link Anima} dentro un {@link HashSet}: se la
 * stessa persona si ripresenta allo sportello, l'archivio la smaschera
 * come impostore.
 */
public class ArchivioAnime {

    private final Set<Anima> animeGiudicate = new HashSet<>();
    private final List<Verdetto> verdetti = new ArrayList<>();

    /**
     * Registra un verdetto e archivia l'anima giudicata.
     *
     * @param verdetto il verdetto da archiviare, non null
     * @throws IllegalArgumentException se il verdetto e' null
     * @throws IllegalStateException    se l'anima risulta gia' giudicata
     */
    public void registra(Verdetto verdetto) {
        if (verdetto == null) {
            throw new IllegalArgumentException("Il verdetto da archiviare non puo' essere null");
        }
        if (!animeGiudicate.add(verdetto.getAnima())) {
            throw new IllegalStateException(
                    "L'anima " + verdetto.getAnima() + " risulta gia' giudicata");
        }
        verdetti.add(verdetto);
    }

    /**
     * @param anima l'anima da controllare
     * @return true se l'anima e' gia' passata in giudicato
     */
    public boolean giaGiudicata(Anima anima) {
        return animeGiudicate.contains(anima);
    }

    /**
     * Cerca tra i verdetti per nome dell'anima, ignorando le maiuscole.
     *
     * @param frammento parte del nome da cercare
     * @return i verdetti delle anime il cui nome contiene il frammento
     */
    public List<Verdetto> cercaPerNome(String frammento) {
        String chiave = frammento == null ? "" : frammento.toLowerCase();
        return verdetti.stream()
                .filter(verdetto -> verdetto.getAnima().getNome().toLowerCase().contains(chiave))
                .collect(Collectors.toList());
    }

    /**
     * @return quante anime sono state smistate verso ciascuna destinazione
     */
    public Map<Destinazione, Long> conteggioPerDestinazione() {
        return verdetti.stream()
                .collect(Collectors.groupingBy(Verdetto::getDestinazione, Collectors.counting()));
    }

    /**
     * @return vista non modificabile di tutti i verdetti in ordine di emissione
     */
    public List<Verdetto> getVerdetti() {
        return Collections.unmodifiableList(verdetti);
    }

    public int totaleGiudicate() {
        return animeGiudicate.size();
    }
}
