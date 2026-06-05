package it.unicam.cs.mpgc.rpg126114.model.verdetti;

/**
 * I timbri con cui il Funzionario sigla i verdetti. I timbri piu'
 * prestigiosi moltiplicano il karma guadagnato o perso: piu' potere,
 * piu' responsabilita'.
 */
public enum Timbro {

    ORDINANZA("Timbro d'Ordinanza", 1, 1),
    DORATO("Timbro Dorato", 2, 3),
    SIGILLO_FIAMMEGGIANTE("Sigillo Fiammeggiante", 3, 5);

    private final String etichetta;
    private final int moltiplicatore;
    private final int livelloRichiesto;

    Timbro(String etichetta, int moltiplicatore, int livelloRichiesto) {
        this.etichetta = etichetta;
        this.moltiplicatore = moltiplicatore;
        this.livelloRichiesto = livelloRichiesto;
    }

    public String getEtichetta() {
        return etichetta;
    }

    /**
     * @return il fattore applicato al karma del verdetto, nel bene e nel male
     */
    public int getMoltiplicatore() {
        return moltiplicatore;
    }

    public int getLivelloRichiesto() {
        return livelloRichiesto;
    }
}
