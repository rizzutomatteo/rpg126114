package it.unicam.cs.mpgc.rpg126114.persistence.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;
import it.unicam.cs.mpgc.rpg126114.model.anima.AnimaComune;
import it.unicam.cs.mpgc.rpg126114.model.anima.AnimaErrante;
import it.unicam.cs.mpgc.rpg126114.model.anima.AnimaIllustre;

import java.lang.reflect.Type;

/**
 * Adapter Gson per la gerarchia di {@link Anima}.
 *
 * <p>Gson da solo non sa quale sottoclasse istanziare quando incontra un
 * campo di tipo {@code Anima}: questo adapter scrive un discriminatore
 * {@code "tipo"} accanto ai dati e lo usa in lettura per ricostruire il
 * sottotipo corretto, come visto a lezione con i serializzatori
 * personalizzati.</p>
 */
public class AnimaAdapter implements JsonSerializer<Anima>, JsonDeserializer<Anima> {

    private static final String CAMPO_TIPO = "tipo";
    private static final String CAMPO_DATI = "dati";

    private static final String TIPO_COMUNE = "comune";
    private static final String TIPO_ILLUSTRE = "illustre";
    private static final String TIPO_ERRANTE = "errante";

    @Override
    public JsonElement serialize(Anima anima, Type tipo, JsonSerializationContext contesto) {
        JsonObject involucro = new JsonObject();
        involucro.addProperty(CAMPO_TIPO, etichettaDi(anima));
        involucro.add(CAMPO_DATI, contesto.serialize(anima, anima.getClass()));
        return involucro;
    }

    @Override
    public Anima deserialize(JsonElement json, Type tipo, JsonDeserializationContext contesto)
            throws JsonParseException {
        JsonObject involucro = json.getAsJsonObject();
        if (!involucro.has(CAMPO_TIPO) || !involucro.has(CAMPO_DATI)) {
            throw new JsonParseException("Anima senza discriminatore di tipo o dati");
        }
        String etichetta = involucro.get(CAMPO_TIPO).getAsString();
        return contesto.deserialize(involucro.get(CAMPO_DATI), classePer(etichetta));
    }

    private String etichettaDi(Anima anima) {
        if (anima instanceof AnimaIllustre) {
            return TIPO_ILLUSTRE;
        }
        if (anima instanceof AnimaErrante) {
            return TIPO_ERRANTE;
        }
        return TIPO_COMUNE;
    }

    private Class<? extends Anima> classePer(String etichetta) {
        switch (etichetta) {
            case TIPO_ILLUSTRE:
                return AnimaIllustre.class;
            case TIPO_ERRANTE:
                return AnimaErrante.class;
            case TIPO_COMUNE:
                return AnimaComune.class;
            default:
                throw new JsonParseException("Tipo di anima sconosciuto: " + etichetta);
        }
    }
}
