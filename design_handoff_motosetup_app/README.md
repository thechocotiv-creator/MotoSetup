# Handoff: MotoSetup App (iOS)

## Overview
MotoSetup è un'app iOS per motociclisti da pista: gestione garage moto, sessioni di setup/telemetria per pista, manutenzione moto, consigli AI su assetto e un piano Premium a pagamento unico. Il design è dark-mode, stile "glass" (blur/backdrop-filter), tipografia Inter.

## About the Design Files
I file in questo pacchetto sono **riferimenti di design creati in HTML** — prototipi che mostrano aspetto e comportamento previsti, NON codice di produzione da copiare. Il compito è **ricreare questi design HTML in SwiftUI**, usando i pattern nativi iOS (NavigationStack, TabView, sheet, alert, @State/@Observable, ecc.), non incorporare webview o HTML.

## Fidelity
**Alta fedeltà (hifi)**: mockup con colori, spaziature, tipografia e interazioni quasi definitivi. Vanno ricreati pixel-perfect per quanto ragionevole in SwiftUI nativo (usando componenti nativi equivalenti a blur/backdrop-filter, es. `.ultraThinMaterial`).

## Stack di riferimento
- Piattaforma target: iOS, SwiftUI
- Lingua UI: italiano (mantenere tutte le stringhe in italiano)
- Font: Inter (se non disponibile via SwiftUI, usare San Francisco come fallback e segnalarlo)
- Colori: definiti in OKLCH nei mockup — vedi sezione Design Tokens per gli hex più vicini
- Tema: dark mode fisso (nessuna light mode nei mockup)

## Screenshot inclusi
Vedi cartella `screenshots/`, uno per ogni schermata/popup/sotto-pagina, con nome descrittivo. Fare riferimento a questi per pixel-fidelity di layout, spaziatura e colori — il file HTML è la fonte di verità per testo esatto e logica.

## Screens / Views

### 1. Onboarding — Login / Registrazione (screenshot: 01-onboarding-*.png)
- Se l'utente ha già una sessione attiva, l'onboarding viene SALTATO e si entra direttamente nella tab Home (schermata "Bentornato").
- Tab switcher "Accedi" / "Registrati" in alto (pill toggle scuro).
- Bottoni social "Continua con Google" e "Continua con Apple" (Sign in with Apple obbligatorio se offri social login su iOS).
- Separatore "oppure".
- Form Accedi: campi Email, Password, bottone "Accedi" (bianco pieno), link "Non hai un account? Registrati".
- Form Registrati: campi Nickname, Email, Password, Conferma password, bottone "Crea account". Validazione: nickname/email non vuoti, password === conferma.
- Dopo la registrazione (non dopo il login) appare la schermata **"Account creato — passa a Premium?"**: tabella comparativa features Free/Premium (Moto in garage, Consigli AI, Run per sessione), CTA "Sblocca Premium — €9,99 una tantum" (bottone bianco pieno) e link secondario "Continua con Free".
- Schermata finale "Sei dentro!" prima di passare alla tab Home.

### 2. Home (screenshot: 02-home-*.png)
- Header "Home" centrato.
- Card "Ultima sessione": pista, moto, best lap (colorato), icona meteo (sole/nuvole/pioggia — SVG semplici, sostituibili con SF Symbols), temperatura. Tap → apre dettaglio run (vedi #7).
- Card "Checklist pista" (stile glass/blur): mostra conteggio N/M voci completate, tap apre modale checklist a tutto schermo.
- Riga orizzontale scrollabile di "card moto" (garage): per ogni moto — nome, indicatore rarità (rombi), foto/placeholder 16:9, sottotitolo, elenco manutenzioni con stato colorato (OK / In scadenza / Scaduta), pulsanti modifica/elimina overlay in alto a destra sulla foto.
- Ultima card: "+ Aggiungi nuova moto" (bordo tratteggiato) con caption che mostra il limite piano ("Il piano Free include 1 sola moto" quando free).
- Tab bar in basso con 4 icone: Home, Setup (lente), Consigli AI (nuvoletta), Profilo (persona) — pill animata sotto l'icona attiva.

#### 2a. Modale Checklist pista (fullscreen overlay, blur pesante)
- Elenco voci con checkbox custom (arrotondato), testo con barrato se completata, pulsante elimina (X) per voce.
- "+ Aggiungi voce" in fondo (bordo tratteggiato).

#### 2b. Dettaglio Manutenzione moto (schermata a scorrimento, apre da card moto)
- Titolo "Manutenzione" + nome moto.
- Per ogni voce: nome, badge stato colorato, "Ultimo: N giorni fa · ogni X giorni" (X è editabile via tap → prompt "Ogni quanti giorni va eseguita questa manutenzione?").
- Due bottoni per voce: "Eseguito oggi" (azzera i giorni) e "Data precedente" (prompt "Eseguita quanti giorni fa?").
- "+ Nuova voce" in fondo.
- Stato calcolato: rapporto = giorniDaUltimo / intervalloGiorni → ≥1 "Scaduta" (rosso), ≥0.8 "In scadenza" (arancio/oro), altrimenti "OK" (grigio).

#### 2c. Modale Modifica moto (bottom sheet)
- Foto moto (slot immagine 16:9), Nome moto, Categoria (testo libero), Colore card (5 swatch: Blu, Rosso, Oro, Verde, Viola), Indice di rarità (Comune / Rara / Leggendaria — 3 pill), bottone "Salva".

#### 2d. Conferma eliminazione moto (alert/dialog centrale)
- "Eliminare moto" — testo di conferma, bottoni Annulla / Elimina (rosso).

### 3. Setup — elenco sessioni (screenshot: 03-setup-*.png)
- Titolo "Setup".
- Card "Sessione di oggi" (se presente): pista, badge "N run", moto · meteo, best lap.
- Lista "Sessioni precedenti": data, pista, moto · N run, best lap, icona meteo.
- Bottone "Vedi tutte le sessioni" (outline glass).
- Floating Action Button (+) in basso a destra → apre "Nuova sessione".
- Tab bar identica a Home.

#### 3a. Nuova sessione (fullscreen overlay)
- Selettori a carosello con frecce ← → per: Moto (incluso "+ Aggiungi nuova moto"), Pista (incluso "+ Aggiungi nuova pista"), Meteo (sole/nuvole/pioggia, icona grande).
- Bottone chiudi (X) in alto a sinistra.
- CTA "Inizia sessione" in basso (bottone bianco pieno).
- **Limite piano Free**: se si prova ad aggiungere una nuova moto oltre 1 (piano free) o si supera il limite run per sessione, si apre il **paywall** (vedi sezione Piano Premium sotto) invece di procedere.

#### 3b. Tutte le sessioni (fullscreen overlay)
- Titolo "Tutte le sessioni".
- 3 filtri a chip ciclabili: Moto, Pista, Data (tap cicla tra le opzioni).
- Lista sessioni filtrate, stesso layout riga di "Sessioni precedenti".

### 4. Dettaglio run / sessione (screenshot: 04-run-detail-*.png)
- Riga meteo in alto (tap cicla sole/nuvole/pioggia) + nome pista (es. "Mugello").
- Selettore Run con frecce ← → (Run 1/2/3, più "+ Nuovo run" — **limitato a 3 run/sessione sul piano Free**, oltre apre il paywall).
- Griglia 2x2 di stat rapide: Ora, Temperatura, Best lap (colorato), Giri — ciascuna apribile con un date/number picker (ruota stile iOS, vedi #4a).
- Barra categorie orizzontale (pill animata): Sospensioni, Gomme, Rapporti, Elettronica (icone).
- Sotto-tab quando la categoria ha sottosezioni (es. Sospensioni → Forcella/Mono; Gomme → Ant/Post).
- Campi parametro dinamici in base a categoria/sottocategoria: alcuni sono stepper (– / valore / +), altri campi testo liberi.
- Campo Note (textarea) per la categoria/sottocategoria attiva.

#### 4a. Picker a rotella (bottom sheet)
- Header "Annulla" / titolo / "Fatto".
- Colonne a scorrimento stile iOS wheel (con separatori tipo ":" o "."), per Ora (hh:mm), Temperatura (°C), Best lap (m:ss.SSS), Giri.

### 5. Consigli AI (screenshot: 05-ai-*.png)
- Titolo "Consigli AI".
- Card evidenziata (bordo blu): "Descrivi il problema di guida" con esempio testo, bottone "Chiedi consiglio", caption uso (es. "1/1 oggi" — **limite Free: 1 consiglio AI al giorno**, oltre apre il paywall).
- Sezione "Parametro consigliato": nome parametro, valore consigliato, spiegazione testuale.
- Sezione "Cronologia": lista domande passate con risposta breve e timestamp.
- Tab bar identica.

### 6. Profilo (screenshot: 06-profilo-*.png)
- Avatar (slot immagine circolare) + nickname + email.
- Sezione "Account": righe "Nickname ed email" e "Password" (entrambe aprono bottom sheet di modifica).
- Sezione "Abbonamento": card cliccabile con "Piano Free/Premium", badge di stato, descrizione — tap apre bottom sheet Abbonamento.
- Sezione "Sessione": "Esci" (logout) e "Elimina account" (rosso, apre conferma).
- Tab bar identica.

#### 6a. Bottom sheet Modifica profilo — campi Nickname, Email, bottone Salva.
#### 6b. Bottom sheet Modifica password — campi Password attuale, Nuova password, Conferma nuova password, bottone Salva (valida che nuova === conferma).
#### 6c. Bottom sheet Abbonamento — tabella comparativa piani (Free vs Premium) su 3 feature, stato attuale: se Premium mostra banner "Premium sbloccato — pagamento unico effettuato"; se Free mostra CTA "Sblocca Premium — €9,99 una tantum".
#### 6d. Conferma eliminazione account (alert centrale) — testo di avviso definitivo, Annulla / Elimina (rosso).

### 7. Paywall (popup modale, riutilizzato su più schermate: Home, Setup, dettaglio run, Consigli AI)
- Titolo "Limite piano Free raggiunto", messaggio contestuale (vedi Piano Premium sotto), bottoni "Chiudi" e "Sblocca Premium" (blu pieno) → apre bottom sheet Abbonamento.

## Funzionamento Piano Premium (business logic)
Piano di default per un nuovo utente: **Free**. Passaggio a Premium: **pagamento unico** (one-time purchase, non abbonamento ricorrente), prezzo mostrato **€9,99**. Non c'è downgrade nei mockup.

Punti di ingresso all'acquisto Premium:
1. Durante l'onboarding, subito dopo la registrazione ("Account creato — passa a Premium?").
2. Bottom sheet "Abbonamento" in Profilo (sempre accessibile).
3. Qualsiasi paywall (vedi sotto) → bottone "Sblocca Premium" apre la stessa bottom sheet.

Limiti del piano **Free** (ciascuno con messaggio paywall dedicato, testo esatto da riprodurre):
- **Moto in garage**: massimo 1 moto. Messaggio: "Il piano Free include 1 sola moto in garage. Passa a Premium per moto illimitate."
- **Consigli AI**: massimo 1 al giorno (contatore azzerato ogni giorno — nei mockup è `aiUsedToday`, incrementato ad ogni richiesta, confrontato con 1). Messaggio: "Hai esaurito il consiglio AI gratuito di oggi. Passa a Premium per consigli illimitati."
- **Run per sessione**: massimo 3 run per sessione. Messaggio: "Il piano Free include massimo 3 run per sessione. Passa a Premium per run illimitati."

Piano **Premium** rimuove tutti e 3 i limiti sopra (moto illimitate, consigli AI illimitati, run illimitati per sessione).

Implementazione consigliata in SwiftUI: un enum/flag `isPremium` (o `SubscriptionPlan { case free, premium }`) su un modello utente osservabile; ogni azione soggetta a limite controlla il piano + il contatore pertinente prima di procedere, e se bloccata presenta il paywall come sheet/alert con il messaggio corrispondente. L'acquisto reale andrà integrato con StoreKit 2 (non-consumable one-time purchase) al posto del semplice `purchasePremium()` sintetico dei mockup.

## Funzionamento Sessioni (Setup / Run)
- Una **sessione** rappresenta un giorno in pista: ha pista, moto usata, meteo, e contiene una o più **run** (max 3 su piano Free, illimitati su Premium).
- Ogni run ha parametri di setup raggruppati per categoria: Sospensioni (Forcella: Molla/Altezza/Compressione/Estensione/Precarico; Mono: idem), Gomme (Anteriore/Posteriore: Giri, Pressione Ingresso/Uscita), Rapporti (Pignone, Corona, Passo catena), Elettronica (Mappa, TC, Engine Brake, Anti Wheelie) — più un campo Note testuale per sottocategoria.
- Ogni run ha anche dati generali: Ora, Temperatura, Best lap, Giri (editabili via picker a rotella).
- Le sessioni passate sono elencate in ordine cronologico in "Setup", con vista filtrata "Tutte le sessioni" (filtri: moto, pista, data).
- Creare una nuova sessione: selezionare moto/pista/meteo (o aggiungerne di nuove — l'aggiunta moto è soggetta al limite piano) poi "Inizia sessione".

## Funzionamento Manutenzione
- Ogni moto in garage ha un elenco di voci di manutenzione (es. Pastiglie freno, Olio motore, Catena, Gomme, Cinghie distribuzione, Filtro aria).
- Ogni voce ha: `daysSinceService` (giorni dall'ultimo intervento) e `intervalDays` (intervallo consigliato in giorni).
- Stato calcolato = daysSinceService / intervalDays: **≥ 1.0 → "Scaduta"** (rosso), **≥ 0.8 → "In scadenza"** (oro/arancio), **altrimenti → "OK"** (grigio).
- Azioni utente: modificare l'intervallo (prompt numerico), segnare come "Eseguito oggi" (azzera daysSinceService), oppure "Data precedente" (prompt: quanti giorni fa è stata eseguita).
- Le voci si ordinano per urgenza: Scaduta → In scadenza → OK.
- Non ci sono limiti di piano sulla manutenzione (disponibile sia su Free che Premium).

## Design Tokens
- Sfondo principale: oklch(0.173 0 0) ≈ #1c1c1c (nero/grigio molto scuro)
- Sfondo pannelli/card scure: oklch(0.1913 0 0) ≈ #1f1f1f
- Testo primario: oklch(0.97 0 0) ≈ #f7f7f7
- Testo secondario/muted: oklch(0.67 0 0) ≈ #a3a3a3
- Accento blu (brand/Premium): oklch(0.87 0.18 250) ≈ #7ab8ff
- Rosso (errori/scaduto/elimina): oklch(0.63 0.255 25) ≈ #e0432f
- Oro/arancio (in scadenza): oklch(0.78 0.15 70) ≈ #d9a441
- Verde: oklch(0.75 0.17 145) ≈ #52cf83
- Viola: oklch(0.72 0.19 300) ≈ #d17ee8
- Colori garage/card moto selezionabili (5): Blu, Rosso, Oro, Verde, Viola (valori sopra)
- Font: Inter, pesi 400/500/600/700/800/900
- Border radius: 12–24px (bottoni 12-16px, sheet/modali 20-26px, card moto 13-18px)
- Effetto vetro: backdrop-filter blur(16-32px) saturate(170-200%), bordi 1px oklch(1 0 0 / 0.16-0.24), spesso con inset box-shadow highlight in alto
- Tab bar: 4 voci (Home, Setup, Consigli AI, Profilo), pill di selezione animata dietro l'icona attiva

## Assets
- Nessuna immagine reale: foto moto e avatar profilo sono placeholder drag-and-drop (l'utente li sostituirà con foto vere in produzione).
- Icone meteo (sole/nuvole/pioggia) e icone tab bar sono disegnate come semplici SVG inline nei mockup — in SwiftUI, sostituire con SF Symbols equivalenti (es. sun.max, cloud, cloud.rain) o icone custom coerenti con lo stile a linea sottile.

## Files
- `MotoSetup App.dc.html` — file principale con tutte le schermate (Design Component, HTML+JS). Ogni schermata ha un contenitore `data-screen-label`.
- `ios-frame.jsx` — bezel iPhone usato solo per la preview nel tool di design, non necessario nel progetto SwiftUI.
- `image-slot.js` — placeholder immagine drag-and-drop, non necessario nel progetto SwiftUI (usare `Image`/`PhotosPicker` nativi).
- `screenshots/` — screenshot di ogni schermata, popup e sotto-pagina elencati sopra.
