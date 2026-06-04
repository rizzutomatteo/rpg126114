package it.unicam.cs.mpgc.rpg126114.model.regole;

import it.unicam.cs.mpgc.rpg126114.model.documenti.Fascicolo;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;

import java.util.Optional;

/**
 * La regola fondamentale del giudizio: confronta il bilancio karmico
 * dell'anima con due soglie e suggerisce la destinazione corrispondente.
 */
public class RegolaBilancioKarmico implements Regola {

    private final int sogliaParadiso;
    private final int sogliaInferno;

    /**
     * @param sogliaParadiso bilancio minimo per meritare il Paradiso
     * @param sogliaInferno  bilancio massimo per meritare l'Inferno
     * @throws IllegalArgumentException se le soglie sono incoerenti
     */
    public RegolaBilancioKarmico(int sogliaParadiso, int sogliaInferno) {
        if (sogliaParadiso <= sogliaInferno) {
            throw new IllegalArgumentException(
                    "La soglia del Paradiso deve essere maggiore di quella dell'Inferno");
        }
        this.sogliaParadiso = sogliaParadiso;
        this.sogliaInferno = sogliaInferno;
    }

    @Override
    public Optional<Esito> valuta(Fascicolo fascicolo) {
        int bilancio = fascicolo.getAnima().bilancioKarmico();
        if (bilancio >= sogliaParadiso) {
            return Optional.of(new Esito(Destinazione.PARADISO, 2,
                    "bilancio karmico " + bilancio + ", sopra la soglia di beatitudine"));
        }
        if (bilancio <= sogliaInferno) {
            return Optional.of(new Esito(Destinazione.INFERNO, 2,
                    "bilancio karmico " + bilancio + ", sotto la soglia di dannazione"));
        }
        return Optional.of(new Esito(Destinazione.PURGATORIO, 1,
                "bilancio karmico " + bilancio + ", caso intermedio"));
    }

    @Override
    public String descrizione() {
        return "Regola del Bilancio Karmico";
    }
}
