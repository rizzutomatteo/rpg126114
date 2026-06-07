package it.unicam.cs.mpgc.rpg126114.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;
import it.unicam.cs.mpgc.rpg126114.persistence.adapter.AnimaAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Carica i contenuti di gioco dalle risorse del progetto: il pool delle
 * anime e' un file JSON, cosi' aggiungere nuove anime non richiede di
 * toccare il codice.
 */
public class CaricatorePool {

    private static final String RISORSA_ANIME = "/data/anime.json";

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Anima.class, new AnimaAdapter())
            .create();

    /**
     * Legge il pool delle anime dalle risorse.
     *
     * @return le anime disponibili per le partite
     * @throws PersistenzaException se la risorsa manca o e' malformata
     */
    public List<Anima> caricaAnime() {
        try (InputStream flusso = CaricatorePool.class.getResourceAsStream(RISORSA_ANIME)) {
            if (flusso == null) {
                throw new PersistenzaException("Risorsa non trovata: " + RISORSA_ANIME);
            }
            Reader lettore = new InputStreamReader(flusso, StandardCharsets.UTF_8);
            Type tipoLista = new TypeToken<List<Anima>>() { }.getType();
            List<Anima> anime = gson.fromJson(lettore, tipoLista);
            if (anime == null || anime.isEmpty()) {
                throw new PersistenzaException("Il pool delle anime e' vuoto");
            }
            return anime;
        } catch (IOException | JsonParseException errore) {
            throw new PersistenzaException("Errore nella lettura del pool delle anime", errore);
        }
    }
}
