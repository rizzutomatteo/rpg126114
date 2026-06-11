# Pratiche Infernali

> _Ufficio Smistamento Anime — Sportello Unico del Purgatorio_

Gioco di ruolo "burocratico" in Java: vesti i panni di un **Funzionario del
Purgatorio** che esamina i fascicoli delle anime in arrivo e ne decide il
destino — Paradiso, Purgatorio, Limbo o Inferno — applicando il regolamento
del giorno. Verdetti conformi fruttano karma, promozioni e timbri più
potenti; errori e impostori non denunciati avvicinano il licenziamento.

Progetto per il corso di **Metodologie di Programmazione / Modellazione e
Gestione della Conoscenza** (UNICAM, A.A. 2025/26).

## Esecuzione

```bash
# 1 - Compilazione (esegue anche i test)
./gradlew build

# 2 - Esecuzione
./gradlew run
```

Serve solo un JDK (versione 17 o superiore, per avviare il wrapper
Gradle): la toolchain scarica automaticamente il JDK richiesto dal
progetto. Interfaccia grafica JavaFX,
risolta come dipendenza Gradle (nessuna installazione manuale).

## Come si gioca

1. **Nuova carriera** dal menu (o **Riprendi** l'ultimo salvataggio).
2. Le anime arrivano in coda allo sportello mentre lavori: esamina i
   documenti del fascicolo, concedi colloqui (limitati dalla Pazienza),
   e attento alle contraddizioni.
3. Se l'anima ti sembra già passata di qui, **denuncia l'impostore**:
   l'archivio non dimentica.
4. Scegli timbro e destinazione: il riscontro ti spiega cosa prevedeva il
   regolamento e perché.
5. A fine giornata: rendiconto, punti abilità da spendere e salvataggio
   automatico. Ogni giornata introduce una nuova regola di giudizio.

## Documentazione

La **[Wiki del progetto su GitHub](https://github.com/rizzutomatteo/rpg126114/wiki)**
documenta funzionalità, responsabilità delle classi, architettura (MVC,
livelli, concorrenza), organizzazione dei dati, persistenza e meccanismi
di estensione.

## Dichiarazione sull'uso di strumenti di AI

Questo progetto è stato realizzato con l'assistenza di **Claude
(Anthropic)** tramite Claude Code: ideazione del concept, progettazione
dell'architettura, generazione di codice, test, contenuti di gioco e
documentazione, sotto la supervisione e revisione dello studente. La
dichiarazione dettagliata (strumenti e scopi, ambito per ambito) è nella
pagina della Wiki [`Dichiarazione-AI`](https://github.com/rizzutomatteo/rpg126114/wiki/Dichiarazione-AI).
