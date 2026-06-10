package it.unicam.cs.mpgc.rpg126114.model.documenti;

/**
 * Le ultime volonta' dell'anima. L'anno di redazione e' un dettaglio
 * prezioso: un testamento redatto dopo la morte e' una contraddizione
 * che il regolamento non perdona.
 */
public class Testamento extends Documento {

    private final String volonta;
    private final int annoRedazione;

    /**
     * Il documento in se' e' sempre autentico (depositato presso un
     * notaio): a tradire l'anima puo' essere il contenuto, come un anno
     * di redazione successivo alla morte.
     *
     * @param volonta       testo delle ultime volonta', non vuoto
     * @param annoRedazione anno in cui il testamento risulta redatto
     * @throws IllegalArgumentException se il testo e' vuoto
     */
    public Testamento(String volonta, int annoRedazione) {
        super("Testamento", true);
        if (volonta == null || volonta.isBlank()) {
            throw new IllegalArgumentException("Le ultime volonta' non possono essere vuote");
        }
        this.volonta = volonta;
        this.annoRedazione = annoRedazione;
    }

    public int getAnnoRedazione() {
        return annoRedazione;
    }

    @Override
    public String contenuto() {
        return "Redatto nell'anno " + annoRedazione + ".\n\"" + volonta + "\"";
    }
}
