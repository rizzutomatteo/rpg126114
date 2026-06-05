package it.unicam.cs.mpgc.rpg126114.model.carriera;

/**
 * Lo stato complessivo di una carriera: il Funzionario, l'archivio delle
 * anime giudicate e la giornata raggiunta. E' la radice dell'oggetto che
 * viene salvato e ricaricato dalla persistenza.
 */
public class Partita {

    private final Funzionario funzionario;
    private final ArchivioAnime archivio;
    private int giornataCorrente;

    /**
     * Inizia una nuova carriera dal primo giorno.
     *
     * @param funzionario il Funzionario protagonista, non null
     * @throws IllegalArgumentException se il funzionario e' null
     */
    public Partita(Funzionario funzionario) {
        if (funzionario == null) {
            throw new IllegalArgumentException("Una partita richiede un Funzionario");
        }
        this.funzionario = funzionario;
        this.archivio = new ArchivioAnime();
        this.giornataCorrente = 1;
    }

    public Funzionario getFunzionario() {
        return funzionario;
    }

    public ArchivioAnime getArchivio() {
        return archivio;
    }

    public int getGiornataCorrente() {
        return giornataCorrente;
    }

    /**
     * Chiude la giornata corrente e passa alla successiva.
     */
    public void avanzaGiornata() {
        giornataCorrente++;
    }

    /**
     * @return true se la carriera e' finita per licenziamento
     */
    public boolean isFinita() {
        return funzionario.isLicenziato();
    }

    @Override
    public String toString() {
        return "Carriera di " + funzionario.getNome() + ", giornata " + giornataCorrente
                + ", " + archivio.totaleGiudicate() + " anime giudicate";
    }
}
