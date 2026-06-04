package it.unicam.cs.mpgc.rpg126114.model.verdetti;

/**
 * Le destinazioni che il Funzionario puo' assegnare a un'anima.
 *
 * <p>L'ordine di dichiarazione conta: le destinazioni piu' in basso sono
 * considerate piu' severe e prevalgono in caso di parita' di peso negli
 * esiti del regolamento.</p>
 */
public enum Destinazione {

    PARADISO("Paradiso", "Beatitudine eterna, ascensore diretto."),
    PURGATORIO("Purgatorio", "Purificazione a tempo determinato."),
    LIMBO("Limbo", "Sala d'attesa senza numero di prenotazione."),
    INFERNO("Inferno", "Reparto fiamme, nessun reclamo accettato.");

    private final String etichetta;
    private final String descrizione;

    Destinazione(String etichetta, String descrizione) {
        this.etichetta = etichetta;
        this.descrizione = descrizione;
    }

    public String getEtichetta() {
        return etichetta;
    }

    public String getDescrizione() {
        return descrizione;
    }

    /**
     * @param altra destinazione con cui confrontarsi
     * @return true se questa destinazione e' piu' severa dell'altra
     */
    public boolean piuSeveraDi(Destinazione altra) {
        return this.ordinal() > altra.ordinal();
    }
}
