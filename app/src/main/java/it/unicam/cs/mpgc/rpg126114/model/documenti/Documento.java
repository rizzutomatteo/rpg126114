package it.unicam.cs.mpgc.rpg126114.model.documenti;

/**
 * Classe base astratta per i documenti contenuti in un fascicolo.
 *
 * <p>Ogni documento ha un'intestazione e un grado di attendibilita':
 * i documenti d'ufficio sono sempre attendibili, quelli portati
 * dall'anima possono non esserlo.</p>
 */
public abstract class Documento {

    private final String intestazione;
    private final boolean attendibile;

    /**
     * @param intestazione titolo del documento, non vuoto
     * @param attendibile  true se il documento proviene da fonte certa
     * @throws IllegalArgumentException se l'intestazione e' vuota
     */
    protected Documento(String intestazione, boolean attendibile) {
        if (intestazione == null || intestazione.isBlank()) {
            throw new IllegalArgumentException("L'intestazione del documento non puo' essere vuota");
        }
        this.intestazione = intestazione;
        this.attendibile = attendibile;
    }

    /**
     * @return il testo del documento cosi' come appare al Funzionario
     */
    public abstract String contenuto();

    public String getIntestazione() {
        return intestazione;
    }

    public boolean isAttendibile() {
        return attendibile;
    }

    @Override
    public String toString() {
        return intestazione;
    }
}
