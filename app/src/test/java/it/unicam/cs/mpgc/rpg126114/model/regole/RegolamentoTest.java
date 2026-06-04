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
        fascicolo.aggiungi(new FedinaKarmica(anima.getPeccati()));
        return fascicolo;
    }

    private Regola regolaFissa(Destinazione destinazione, int peso) {
        return fascicolo -> Optional.of(new Esito(destinazione, peso, "esito di prova"));
    }

    @Test
    void vinceLaDestinazioneConPesoTotaleMaggiore() {
        Regolamento regolamento = new Regolamento(List.of(
                regolaFissa(Destinazione.PARADISO, 2),
                regolaFissa(Destinazione.PARADISO, 2),
                regolaFissa(Destinazione.INFERNO, 3)));

        assertEquals(Destinazione.PARADISO, regolamento.destinazioneAttesa(fascicoloValido()));
    }

    @Test
    void aParitaDiPesoPrevaleLaDestinazionePiuSevera() {
        Regolamento regolamento = new Regolamento(List.of(
                regolaFissa(Destinazione.PARADISO, 2),
                regolaFissa(Destinazione.INFERNO, 2)));

        assertEquals(Destinazione.INFERNO, regolamento.destinazioneAttesa(fascicoloValido()));
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
        Regolamento regolamento = new Regolamento(List.of(regolaFissa(Destinazione.PARADISO, 1)));
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
