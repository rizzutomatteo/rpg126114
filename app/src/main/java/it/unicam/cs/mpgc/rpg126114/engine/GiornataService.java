package it.unicam.cs.mpgc.rpg126114.engine;

import it.unicam.cs.mpgc.rpg126114.model.anima.Anima;
import it.unicam.cs.mpgc.rpg126114.model.carriera.Giornata;
import it.unicam.cs.mpgc.rpg126114.model.carriera.Partita;
import it.unicam.cs.mpgc.rpg126114.model.documenti.Fascicolo;
import it.unicam.cs.mpgc.rpg126114.model.regole.Regola;
import it.unicam.cs.mpgc.rpg126114.model.regole.RegolaAnzianita;
import it.unicam.cs.mpgc.rpg126114.model.regole.RegolaBilancioKarmico;
import it.unicam.cs.mpgc.rpg126114.model.regole.RegolaContraddizioni;
import it.unicam.cs.mpgc.rpg126114.model.regole.RegolaPentimento;
import it.unicam.cs.mpgc.rpg126114.model.regole.RegolaRaccomandazione;
import it.unicam.cs.mpgc.rpg126114.model.regole.Regolamento;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Destinazione;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Timbro;
import it.unicam.cs.mpgc.rpg126114.model.verdetti.Verdetto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * L'orchestratore di una giornata allo sportello: apre la giornata con il
 * regolamento progressivo, mette in coda gli arrivi su un thread dedicato,
 * raccoglie verdetti, colloqui e denunce, e a fine turno produce il
 * {@link ReportGiornata}.
 *
 * <p>Dipende solo dalle astrazioni del model e dai collaboratori iniettati
 * nel costruttore: la GUI vi accede senza conoscere le regole concrete.
 * Gli arrivi sono consegnati dalla {@link CodaArrivi} mentre il giocatore
 * lavora: {@link #prossimaPratica()} restituisce vuoto finche' nessuna
 * anima e' in attesa.</p>
 */
public class GiornataService {

    public static final int ANIME_BASE = 4;
    public static final int ANIME_MASSIME = 8;
    public static final int KARMA_DENUNCIA_CORRETTA = 15;
    public static final int PENALE_DENUNCIA_ERRATA = -10;
    public static final int PENALE_IMPOSTORE_GIUDICATO = -10;
    public static final int SOGLIA_PARADISO = 5;
    public static final int SOGLIA_INFERNO = -5;
    public static final int ANNO_ANIME_ANTICHE = 1700;
    public static final long PRIMO_ARRIVO_PREDEFINITO_MILLIS = 1500;
    public static final long INTERVALLO_MINIMO_PREDEFINITO_MILLIS = 18000;
    public static final long INTERVALLO_MASSIMO_PREDEFINITO_MILLIS = 32000;

    private final Partita partita;
    private final GeneratoreAnime generatoreAnime;
    private final GeneratoreFascicoli generatoreFascicoli;
    private final ValutatoreVerdetti valutatore;
    private final CodaArrivi codaArrivi;

    private Giornata giornata;
    private Fascicolo fascicoloCorrente;
    private final List<EsitoValutazione> esitiDelGiorno = new ArrayList<>();
    private int karmaExtraDelGiorno;
    private int colloquiRimasti;
    private int dichiarazioniRese;
    private long primoArrivoMillis = PRIMO_ARRIVO_PREDEFINITO_MILLIS;
    private long intervalloMinimoMillis = INTERVALLO_MINIMO_PREDEFINITO_MILLIS;
    private long intervalloMassimoMillis = INTERVALLO_MASSIMO_PREDEFINITO_MILLIS;

    /**
     * @param partita             lo stato della carriera, non null
     * @param generatoreAnime     il distributore di anime, non null
     * @param generatoreFascicoli il compositore di fascicoli, non null
     * @param valutatore          il valutatore dei verdetti, non null
     * @param codaArrivi          la coda concorrente degli arrivi, non null
     * @throws IllegalArgumentException se un collaboratore e' null
     */
    public GiornataService(Partita partita, GeneratoreAnime generatoreAnime,
                           GeneratoreFascicoli generatoreFascicoli, ValutatoreVerdetti valutatore,
                           CodaArrivi codaArrivi) {
        if (partita == null || generatoreAnime == null || generatoreFascicoli == null
                || valutatore == null || codaArrivi == null) {
            throw new IllegalArgumentException("Tutti i collaboratori del servizio sono obbligatori");
        }
        this.partita = partita;
        this.generatoreAnime = generatoreAnime;
        this.generatoreFascicoli = generatoreFascicoli;
        this.valutatore = valutatore;
        this.codaArrivi = codaArrivi;
    }

    /**
     * Regola il ritmo degli arrivi allo sportello (utile nei test e per
     * future opzioni di difficolta').
     *
     * @param primoMillis   attesa prima del primo arrivo, non negativa
     * @param minimoMillis  attesa minima tra gli arrivi successivi
     * @param massimoMillis attesa massima tra gli arrivi successivi
     * @throws IllegalArgumentException se il ritmo non e' valido
     */
    public void setRitmoArrivi(long primoMillis, long minimoMillis, long massimoMillis) {
        if (primoMillis < 0 || minimoMillis < 0 || massimoMillis < minimoMillis) {
            throw new IllegalArgumentException("Ritmo degli arrivi non valido");
        }
        this.primoArrivoMillis = primoMillis;
        this.intervalloMinimoMillis = minimoMillis;
        this.intervalloMassimoMillis = massimoMillis;
    }

    /**
     * Apre la giornata corrente della partita.
     *
     * @return false se il pool delle anime e' esaurito e il Funzionario
     *         merita il pensionamento, true se la giornata e' iniziata
     * @throws IllegalStateException se la giornata precedente non e' conclusa
     */
    public boolean avviaGiornata() {
        if (giornata != null && !giornata.isConclusa()) {
            throw new IllegalStateException("La giornata precedente non e' ancora conclusa");
        }
        int fresche = generatoreAnime.animeFresche(partita.getArchivio());
        if (fresche == 0) {
            return false;
        }
        int numero = partita.getGiornataCorrente();
        int previste = Math.min(Math.min(ANIME_BASE + (numero - 1) / 2, ANIME_MASSIME), fresche);
        giornata = new Giornata(numero, previste, regolamentoPerGiornata(numero));
        esitiDelGiorno.clear();
        karmaExtraDelGiorno = 0;
        colloquiRimasti = partita.getFunzionario().colloquiPerGiornata();
        fascicoloCorrente = null;
        codaArrivi.avvia(generatoreAnime.prossime(previste, partita.getArchivio()),
                primoArrivoMillis, intervalloMinimoMillis, intervalloMassimoMillis);
        return true;
    }

    /**
     * Il regolamento si arricchisce con il passare delle giornate: ogni
     * giorno di carriera introduce un nuovo criterio di giudizio.
     *
     * @param numero il numero della giornata
     * @return il regolamento in vigore quel giorno
     */
    public Regolamento regolamentoPerGiornata(int numero) {
        List<Regola> regole = new ArrayList<>();
        regole.add(new RegolaBilancioKarmico(SOGLIA_PARADISO, SOGLIA_INFERNO));
        if (numero >= 2) {
            regole.add(new RegolaContraddizioni());
        }
        if (numero >= 3) {
            regole.add(new RegolaRaccomandazione());
        }
        if (numero >= 4) {
            regole.add(new RegolaAnzianita(ANNO_ANIME_ANTICHE));
        }
        if (numero >= 5) {
            regole.add(new RegolaPentimento());
        }
        return new Regolamento(regole);
    }

    /**
     * La regola che entra in vigore nella giornata indicata: per la prima
     * giornata e' la regola di base, per le successive l'eventuale regola
     * aggiunta rispetto al giorno prima. Alimenta le "circolari di
     * servizio" mostrate al giocatore.
     *
     * @param numero il numero della giornata, almeno 1
     * @return la regola introdotta quel giorno, se ce n'e' una
     * @throws IllegalArgumentException se il numero non e' valido
     */
    public Optional<Regola> nuovaRegolaPer(int numero) {
        if (numero < 1) {
            throw new IllegalArgumentException("Numero di giornata non valido: " + numero);
        }
        List<Regola> regoleDiOggi = regolamentoPerGiornata(numero).getRegole();
        if (numero == 1) {
            return Optional.of(regoleDiOggi.get(regoleDiOggi.size() - 1));
        }
        List<Regola> regoleDiIeri = regolamentoPerGiornata(numero - 1).getRegole();
        if (regoleDiOggi.size() > regoleDiIeri.size()) {
            return Optional.of(regoleDiOggi.get(regoleDiOggi.size() - 1));
        }
        return Optional.empty();
    }

    /**
     * @return la regola entrata in vigore oggi, se la giornata ne introduce una
     * @throws IllegalStateException se nessuna giornata e' avviata
     */
    public Optional<Regola> nuovaRegolaDelGiorno() {
        richiediGiornataAttiva();
        return nuovaRegolaPer(giornata.getNumero());
    }

    /**
     * Chiama allo sportello la prossima anima arrivata in coda e ne
     * compone il fascicolo.
     *
     * @return il fascicolo della nuova pratica, o vuoto se nessuna anima
     *         e' ancora arrivata
     * @throws IllegalStateException se c'e' gia' una pratica aperta o la
     *                               giornata e' conclusa o non avviata
     */
    public Optional<Fascicolo> prossimaPratica() {
        richiediGiornataAttiva();
        if (fascicoloCorrente != null) {
            throw new IllegalStateException("C'e' gia' una pratica aperta sulla scrivania");
        }
        if (giornata.isConclusa()) {
            throw new IllegalStateException("La giornata e' conclusa: nessuna nuova pratica");
        }
        Optional<Anima> arrivata = codaArrivi.preleva();
        if (arrivata.isEmpty()) {
            return Optional.empty();
        }
        fascicoloCorrente = generatoreFascicoli.componi(arrivata.get());
        dichiarazioniRese = 0;
        return Optional.of(fascicoloCorrente);
    }

    /**
     * @return quante anime aspettano in coda allo sportello
     */
    public int arriviInAttesa() {
        return codaArrivi.inAttesa();
    }

    /**
     * @return quante anime restano negli archivi in attesa di giudizio
     */
    public int animeFrescheRimaste() {
        return generatoreAnime.animeFresche(partita.getArchivio());
    }

    /**
     * Registra chi viene avvisato a ogni nuovo arrivo. L'avviso parte dal
     * thread produttore della coda: la GUI deve rientrare nel thread
     * JavaFX con {@code Platform.runLater}.
     *
     * @param osservatore il destinatario degli avvisi, o null per nessuno
     */
    public void setOsservatoreArrivi(Consumer<Integer> osservatore) {
        codaArrivi.setOsservatoreArrivi(osservatore);
    }

    /**
     * Concede un colloquio all'anima della pratica corrente.
     *
     * @return la prossima dichiarazione, o vuoto se i colloqui o le
     *         dichiarazioni sono esauriti
     * @throws IllegalStateException se non c'e' una pratica aperta
     */
    public Optional<String> colloquio() {
        richiediPraticaAperta();
        if (colloquiRimasti <= 0) {
            return Optional.empty();
        }
        List<String> dichiarazioni = fascicoloCorrente.getAnima().dichiarazioniRicordate();
        if (dichiarazioniRese >= dichiarazioni.size()) {
            return Optional.empty();
        }
        colloquiRimasti--;
        return Optional.of(dichiarazioni.get(dichiarazioniRese++));
    }

    /**
     * Denuncia l'anima corrente come impostore.
     *
     * <p>Se l'archivio conferma che era gia' stata giudicata, la denuncia
     * frutta karma e chiude il caso; altrimenti il Funzionario paga la
     * figuraccia e la pratica resta aperta.</p>
     *
     * @return true se la denuncia era fondata
     * @throws IllegalStateException se non c'e' una pratica aperta
     */
    public boolean denunciaImpostore() {
        richiediPraticaAperta();
        if (partita.getArchivio().giaGiudicata(fascicoloCorrente.getAnima())) {
            karmaExtraDelGiorno += KARMA_DENUNCIA_CORRETTA;
            partita.getFunzionario().aggiungiKarma(KARMA_DENUNCIA_CORRETTA);
            giornata.registraCasoSenzaVerdetto();
            fascicoloCorrente = null;
            return true;
        }
        karmaExtraDelGiorno += PENALE_DENUNCIA_ERRATA;
        partita.getFunzionario().aggiungiKarma(PENALE_DENUNCIA_ERRATA);
        return false;
    }

    /**
     * Emette il verdetto per la pratica corrente.
     *
     * <p>Se l'anima era un impostore non denunciato, l'archivio respinge
     * la pratica e il Funzionario paga la penale.</p>
     *
     * @param destinazione la destinazione scelta
     * @param timbro       il timbro da usare
     * @return l'esito della valutazione, con karma e motivazioni
     * @throws IllegalStateException    se non c'e' una pratica aperta
     * @throws IllegalArgumentException se il timbro non e' sbloccato
     */
    public EsitoValutazione emettiVerdetto(Destinazione destinazione, Timbro timbro) {
        richiediPraticaAperta();
        if (!partita.getFunzionario().timbriDisponibili().contains(timbro)) {
            throw new IllegalArgumentException(
                    "Timbro non ancora sbloccato: " + timbro.getEtichetta());
        }
        Anima anima = fascicoloCorrente.getAnima();
        if (partita.getArchivio().giaGiudicata(anima)) {
            return praticaRespinta();
        }
        Verdetto verdetto = new Verdetto(anima, destinazione, timbro, giornata.getNumero());
        EsitoValutazione esito = valutatore.valuta(verdetto, giornata.getRegolamento(),
                fascicoloCorrente, partita.getFunzionario());
        partita.getFunzionario().aggiungiKarma(esito.getDeltaKarma());
        partita.getArchivio().registra(verdetto);
        giornata.registraVerdetto(verdetto);
        esitiDelGiorno.add(esito);
        fascicoloCorrente = null;
        return esito;
    }

    private EsitoValutazione praticaRespinta() {
        partita.getFunzionario().aggiungiKarma(PENALE_IMPOSTORE_GIUDICATO);
        giornata.registraCasoSenzaVerdetto();
        Destinazione attesa = giornata.getRegolamento().destinazioneAttesa(fascicoloCorrente);
        EsitoValutazione esito = new EsitoValutazione(false, PENALE_IMPOSTORE_GIUDICATO, attesa,
                List.of("L'archivio respinge la pratica: anima gia' giudicata. "
                        + "Un impostore ti ha beffato."));
        esitiDelGiorno.add(esito);
        fascicoloCorrente = null;
        return esito;
    }

    /**
     * @return true se tutti i casi della giornata sono stati chiusi
     */
    public boolean isGiornataConclusa() {
        return giornata != null && giornata.isConclusa();
    }

    /**
     * Chiude la giornata, produce il rendiconto e avanza la carriera.
     *
     * @return il report della giornata appena conclusa
     * @throws IllegalStateException se la giornata non e' conclusa
     */
    public ReportGiornata chiudiGiornata() {
        richiediGiornataAttiva();
        if (!giornata.isConclusa()) {
            throw new IllegalStateException("Ci sono ancora casi aperti allo sportello");
        }
        codaArrivi.ferma();
        ReportGiornata report = new ReportGiornata(giornata.getNumero(),
                giornata.getVerdettiDelGiorno(), esitiDelGiorno,
                giornata.getCasiSenzaVerdetto(), karmaExtraDelGiorno);
        partita.avanzaGiornata();
        giornata = null;
        return report;
    }

    public Partita getPartita() {
        return partita;
    }

    /**
     * @return la giornata in corso, o null se nessuna giornata e' aperta
     */
    public Giornata getGiornata() {
        return giornata;
    }

    /**
     * @return la pratica aperta sulla scrivania, o null se non ce ne sono
     */
    public Fascicolo getFascicoloCorrente() {
        return fascicoloCorrente;
    }

    public int getColloquiRimasti() {
        return colloquiRimasti;
    }

    private void richiediGiornataAttiva() {
        if (giornata == null) {
            throw new IllegalStateException("Nessuna giornata avviata");
        }
    }

    private void richiediPraticaAperta() {
        richiediGiornataAttiva();
        if (fascicoloCorrente == null) {
            throw new IllegalStateException("Nessuna pratica aperta sulla scrivania");
        }
    }
}
