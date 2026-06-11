package it.unicam.cs.mpgc.rpg126114.model.regole;

import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;
import it.unicam.cs.mpgc.rpg126114.model.anima.AnimaComune;
import it.unicam.cs.mpgc.rpg126114.model.anima.Peccato;
import it.unicam.cs.mpgc.rpg126114.model.anima.Virtu;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Fascicolo;
import it.unicam.cs.mpgc.rpg126114.model.documenti.FedinaKarmica;
import it.unicam.cs.mpgc.rpg126114.model.documenti.PraticaMalformataException;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica la combinazione degli esiti da parte del regolamento.
 */
class RegolamentoTest {

    private Fascicolo fascicoloValido() {
        Anima anima = new AnimaComune("Caso di Prova", 1980);
        anima.aggiungiVirtu(new Virtu("Gentilezza", 3));
        anima.aggiungiPeccato(new Peccato("Parcheggio in doppia fila", 2));
        Fascicolo fascicolo = new Fascicolo(anima);
        fascicolo.aggiungi(new FedinaKarmica(anima.getPeccati(), anima.getVirtu()));
        return fascicolo;
    }

    private Regola regolaFissa(Destinazione destinazione) {
        return fascicolo -> Optional.of(new Esito(destinazione, "esito di prova"));
    }

    @Test
    void laRegolaPiuRecenteCheSiApplicaDecide() {
        // L'ultima regola della lista e' la piu' recente: la sua
        // destinazione prevale su quelle delle regole piu' vecchie.
        Regolamento regolamento = new Regolamento(List.of(
                regolaFissa(Destinazione.PARADISO),
                regolaFissa(Destinazione.PARADISO),
                regolaFissa(Destinazione.INFERNO)));

        assertEquals(Destinazione.INFERNO, regolamento.destinazioneAttesa(fascicoloValido()));
    }

    @Test
    void seLaPiuRecenteNonSiApplicaDecideLaPrecedente() {
        Regolamento regolamento = new Regolamento(List.of(
                regolaFissa(Destinazione.PARADISO),
                fascicolo -> Optional.empty()));

        assertEquals(Destinazione.PARADISO, regolamento.destinazioneAttesa(fascicoloValido()));
    }

    @Test
    void leMotivazioniPartonoDallaRegolaCheDecide() {
        Regolamento regolamento = new Regolamento(List.of(
                regolaFissa(Destinazione.PARADISO),
                regolaFissa(Destinazione.INFERNO)));

        List<String> motivazioni = regolamento.motivazioni(fascicoloValido());

        assertEquals(2, motivazioni.size());
        assertTrue(motivazioni.get(0).contains("decide"));
        assertTrue(motivazioni.get(1).contains("non vincolante"));
    }

    @Test
    void senzaRegoleApplicabiliSiFiniscePerSmistamentoInPurgatorio() {
        Regolamento vuoto = new Regolamento(List.of());
        Regolamento nonApplicabile = new Regolamento(List.of(fascicolo -> Optional.empty()));

        assertEquals(Destinazione.PURGATORIO, vuoto.destinazioneAttesa(fascicoloValido()));
        assertEquals(Destinazione.PURGATORIO, nonApplicabile.destinazioneAttesa(fascicoloValido()));
    }

    @Test
    void unFascicoloMalformatoNonVaAGiudizio() {
        Regolamento regolamento = new Regolamento(List.of(regolaFissa(Destinazione.PARADISO)));
        Fascicolo senzaFedina = new Fascicolo(new AnimaComune("Senza Fedina", 1990));

        assertThrows(PraticaMalformataException.class,
                () -> regolamento.destinazioneAttesa(senzaFedina));
    }

    @Test
    void leMotivazioniRiportanoLeRegoleApplicabili() {
        Regolamento regolamento = new Regolamento(List.of(
                new RegolaBilancioKarmico(5, -5),
                new RegolaContraddizioni()));

        List<String> motivazioni = regolamento.motivazioni(fascicoloValido());

        assertEquals(1, motivazioni.size());
        assertTrue(motivazioni.get(0).startsWith("Regola del Bilancio Karmico"));
        assertTrue(motivazioni.get(0).contains("decide"),
                "La regola applicabile deve risultare quella che decide");
    }

    @Test
    void ilVerbaleValutaLeRegoleInUnSoloPassaggio() {
        Regolamento regolamento = new Regolamento(List.of(new RegolaBilancioKarmico(5, -5)));
        Fascicolo fascicolo = fascicoloValido();

        Verbale verbale = regolamento.verbale(fascicolo);

        assertEquals(regolamento.destinazioneAttesa(fascicolo), verbale.getDestinazione());
        assertEquals(regolamento.motivazioni(fascicolo), verbale.getMotivazioni());
        assertThrows(UnsupportedOperationException.class,
                () -> verbale.getMotivazioni().add("intrusione"));
    }

    @Test
    void regoleNullRifiutate() {
        assertThrows(IllegalArgumentException.class, () -> new Regolamento(null));
    }

    @Test
    void leRegoleEsposteNonSonoModificabili() {
        Regolamento regolamento = new Regolamento(List.of(new RegolaContraddizioni()));

        assertThrows(UnsupportedOperationException.class,
                () -> regolamento.getRegole().add(new RegolaContraddizioni()));
    }
}
