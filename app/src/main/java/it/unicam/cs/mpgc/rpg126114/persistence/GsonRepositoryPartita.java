package it.unicam.cs.mpgc.rpg126114.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;
import it.unicam.cs.mpgc.rpg126114.model.carriera.Partita;
import it.unicam.cs.mpgc.rpg126114.persistence.adapter.AnimaAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Implementazione su file JSON del {@link RepositoryPartita}, basata su
 * Gson con l'adapter polimorfico per le anime e scritture bufferizzate
 * come visto a lezione.
 */
public class GsonRepositoryPartita implements RepositoryPartita {

    private final Path file;
    private final Gson gson;

    /**
     * @param file il percorso del file di salvataggio, non null
     * @throws IllegalArgumentException se il percorso e' null
     */
    public GsonRepositoryPartita(Path file) {
        if (file == null) {
            throw new IllegalArgumentException("Il percorso del salvataggio non puo' essere null");
        }
        this.file = file;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Anima.class, new AnimaAdapter())
                .setPrettyPrinting()
                .create();
    }

    @Override
    public void salva(Partita partita) {
        if (partita == null) {
            throw new IllegalArgumentException("La partita da salvare non puo' essere null");
        }
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            try (BufferedWriter scrittore = Files.newBufferedWriter(file)) {
                gson.toJson(partita, scrittore);
            }
        } catch (IOException errore) {
            throw new PersistenzaException("Impossibile salvare la partita in " + file, errore);
        }
    }

    @Override
    public Optional<Partita> carica() {
        if (!esisteSalvataggio()) {
            return Optional.empty();
        }
        try (BufferedReader lettore = Files.newBufferedReader(file)) {
            return Optional.ofNullable(gson.fromJson(lettore, Partita.class));
        } catch (IOException | JsonParseException errore) {
            throw new PersistenzaException("Salvataggio illeggibile in " + file, errore);
        }
    }

    @Override
    public boolean esisteSalvataggio() {
        return Files.exists(file);
    }
}
