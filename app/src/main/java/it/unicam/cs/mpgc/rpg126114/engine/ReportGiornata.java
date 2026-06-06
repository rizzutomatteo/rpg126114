package it.unicam.cs.mpgc.rpg126114.engine;

import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Verdetto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Il rendiconto statistico di una giornata allo sportello, calcolato
 * con gli stream sulle liste di verdetti ed esiti.
 */
public final class ReportGiornata {

    private final int numeroGiornata;
    private final List<Verdetto> verdetti;
    private final List<EsitoValutazione> esiti;
    private final int casiSenzaVerdetto;
    private final int karmaExtra;

    /**
     * @param numeroGiornata    la giornata rendicontata, almeno 1
     * @param verdetti          i verdetti emessi, non null
     * @param esiti             gli esiti delle valutazioni, non null
     * @param casiSenzaVerdetto i casi chiusi senza verdetto (impostori)
     * @param karmaExtra        karma maturato fuori dai verdetti (denunce)
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    public ReportGiornata(int numeroGiornata, List<Verdetto> verdetti,
                          List<EsitoValutazione> esiti, int casiSenzaVerdetto, int karmaExtra) {
        if (numeroGiornata < 1) {
            throw new IllegalArgumentException("Numero di giornata non valido: " + numeroGiornata);
        }
        if (verdetti == null || esiti == null) {
            throw new IllegalArgumentException("Verdetti ed esiti sono obbligatori");
        }
        if (casiSenzaVerdetto < 0) {
            throw new IllegalArgumentException("I casi senza verdetto non possono essere negativi");
        }
        this.numeroGiornata = numeroGiornata;
        this.verdetti = new ArrayList<>(verdetti);
        this.esiti = new ArrayList<>(esiti);
        this.casiSenzaVerdetto = casiSenzaVerdetto;
        this.karmaExtra = karmaExtra;
    }

    public int getNumeroGiornata() {
        return numeroGiornata;
    }

    public int getCasiSenzaVerdetto() {
        return casiSenzaVerdetto;
    }

    /**
     * @return quante anime sono state smistate verso ciascuna destinazione
     */
    public Map<Destinazione, Long> conteggioPerDestinazione() {
        return verdetti.stream()
                .collect(Collectors.groupingBy(Verdetto::getDestinazione, Collectors.counting()));
    }

    /**
     * @return percentuale di verdetti conformi al regolamento (0 se nessuno)
     */
    public double percentualeCorretti() {
        if (esiti.isEmpty()) {
            return 0;
        }
        long corretti = esiti.stream().filter(EsitoValutazione::isCorretto).count();
        return 100.0 * corretti / esiti.size();
    }

    /**
     * @return il karma complessivo maturato nella giornata
     */
    public int karmaTotale() {
        int daVerdetti = esiti.stream().mapToInt(EsitoValutazione::getDeltaKarma).sum();
        return daVerdetti + karmaExtra;
    }

    /**
     * @return il testo del rendiconto da mostrare a fine giornata
     */
    public String riepilogo() {
        StringBuilder testo = new StringBuilder();
        testo.append("Giornata ").append(numeroGiornata).append(" conclusa.\n");
        testo.append("Casi chiusi: ").append(verdetti.size() + casiSenzaVerdetto);
        if (casiSenzaVerdetto > 0) {
            testo.append(" (di cui ").append(casiSenzaVerdetto).append(" senza verdetto)");
        }
        testo.append("\nVerdetti conformi al regolamento: ")
                .append(String.format("%.0f%%", percentualeCorretti()));
        testo.append("\nKarma di giornata: ").append(karmaTotale() >= 0 ? "+" : "")
                .append(karmaTotale());
        String smistamenti = conteggioPerDestinazione().entrySet().stream()
                .map(voce -> voce.getKey().getEtichetta() + " " + voce.getValue())
                .collect(Collectors.joining(", "));
        if (!smistamenti.isEmpty()) {
            testo.append("\nSmistamenti: ").append(smistamenti);
        }
        return testo.toString();
    }
}
