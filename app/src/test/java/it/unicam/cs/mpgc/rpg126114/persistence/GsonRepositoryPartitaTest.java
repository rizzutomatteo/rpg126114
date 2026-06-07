package it.unicam.cs.mpgc.rpg126114.persistence;

import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;
import it.unicam.cs.mpgc.rpg126114.model.anima.AnimaComune;
import it.unicam.cs.mpgc.rpg126114.model.anima.AnimaIllustre;
import it.unicam.cs.mpgc.rpg126114.model.anima.Peccato;
import it.unicam.cs.mpgc.rpg126114.model.carriera.Funzionario;
import it.unicam.cs.mpgc.rpg126114.model.carriera.Partita;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Timbro;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Verdetto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica il viaggio completo della partita: salvataggio su file JSON
 * e ricostruzione fedele, sottotipi delle anime compresi.
 */
class GsonRepositoryPartitaTest {

    @TempDir
    Path cartellaTemporanea;

    private Path fileSalvataggio() {
        return cartellaTemporanea.resolve("salvataggio.json");
    }

    private Partita partitaDiProva() {
        Partita partita = new Partita(new Funzionario("Funzionario di Prova"));
        partita.getFunzionario().aggiungiKarma(60);
        partita.getFunzionario().potenziaIntuito();

        Anima illustre = new AnimaIllustre("Goffredo dei Brivido", 1789, "Marchese");
        illustre.aggiungiPeccato(new Peccato("Tradimento del casato", 9));
        partita.getArchivio().registra(
                new Verdetto(illustre, Destinazione.INFERNO, Timbro.ORDINANZA, 1));
        partita.getArchivio().registra(
                new Verdetto(new AnimaComune("Rosa Benedetti", 1958),
                        Destinazione.PARADISO, Timbro.ORDINANZA, 1));
        partita.avanzaGiornata();
        return partita;
    }

    @Test
    void salvataggioECaricamentoRicostruisconoLaPartita() {
        RepositoryPartita repository = new GsonRepositoryPartita(fileSalvataggio());
        Partita originale = partitaDiProva();

        assertFalse(repository.esisteSalvataggio());
        repository.salva(originale);
        assertTrue(repository.esisteSalvataggio());

        Partita caricata = repository.carica().orElseThrow();

        assertEquals(originale.getFunzionario().getKarma(),
                caricata.getFunzionario().getKarma());
        assertEquals(originale.getFunzionario().getLivello(),
                caricata.getFunzionario().getLivello());
        assertEquals(originale.getFunzionario().getIntuito(),
                caricata.getFunzionario().getIntuito());
        assertEquals(originale.getGiornataCorrente(), caricata.getGiornataCorrente());
        assertEquals(2, caricata.getArchivio().totaleGiudicate());
    }

    @Test
    void ilContrattoDiUguaglianzaSopravviveAlCaricamento() {
        RepositoryPartita repository = new GsonRepositoryPartita(fileSalvataggio());
        repository.salva(partitaDiProva());

        Partita caricata = repository.carica().orElseThrow();

        assertTrue(caricata.getArchivio()
                .giaGiudicata(new AnimaComune("Rosa Benedetti", 1958)));
        assertFalse(caricata.getArchivio()
                .giaGiudicata(new AnimaComune("Rosa Benedetti", 1900)));
    }

    @Test
    void iSottotipiDelleAnimeVengonoPreservati() {
        RepositoryPartita repository = new GsonRepositoryPartita(fileSalvataggio());
        repository.salva(partitaDiProva());

        Partita caricata = repository.carica().orElseThrow();
        Anima ricostruita = caricata.getArchivio().cercaPerNome("Goffredo").get(0).getAnima();

        assertTrue(ricostruita instanceof AnimaIllustre);
        assertEquals("Marchese", ((AnimaIllustre) ricostruita).getTitolo());
        assertEquals(1, ricostruita.getPeccati().size());
    }

    @Test
    void senzaSalvataggioIlCaricamentoEVuoto() {
        RepositoryPartita repository = new GsonRepositoryPartita(fileSalvataggio());

        Optional<Partita> caricata = repository.carica();

        assertTrue(caricata.isEmpty());
    }

    @Test
    void unSalvataggioCorrottoSegnalaErroreChiaro() throws IOException {
        Files.writeString(fileSalvataggio(), "{ questo non e' json valido ");
        RepositoryPartita repository = new GsonRepositoryPartita(fileSalvataggio());

        assertThrows(PersistenzaException.class, repository::carica);
    }

    @Test
    void gliArgomentiNulliSonoRifiutati() {
        assertThrows(IllegalArgumentException.class, () -> new GsonRepositoryPartita(null));
        assertThrows(IllegalArgumentException.class,
                () -> new GsonRepositoryPartita(fileSalvataggio()).salva(null));
    }
}
