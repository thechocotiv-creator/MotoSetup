# CLAUDE.md — MotoSetup

Guida di riferimento condivisa tra le due piattaforme. Contiene solo ciò che **non deve divergere** tra iOS e Android (business logic, design token, schema dati, copy). Modificare solo quando una decisione davvero cambia — non per dettagli di singola schermata.

## Cos'è MotoSetup

App in italiano per motociclisti da pista: garage moto, sessioni/run con parametri di setup (sospensioni, gomme, rapporti, elettronica), manutenzione moto, consigli AI sull'assetto, piano Premium a pagamento unico (€9,99, non abbonamento).

## Fonte di verità

Design, copy esatto e business logic: `design_handoff_motosetup_app/README.md` e gli screenshot in `design_handoff_motosetup_app/screenshots/`. `MotoSetup App.dc.html` e `ios-frame.jsx` sono **riferimenti di design**, non codice da portare: vanno ricreati nativamente su ciascuna piattaforma.

## Struttura del repo (monorepo)

```
CLAUDE.md                      -- questo file: business logic e design condivisi
design_handoff_motosetup_app/  -- design handoff (condiviso)
ios/                            -- app SwiftUI, vedi ios/CLAUDE.md
android/                        -- app Kotlin/Compose, vedi android/CLAUDE.md
.github/workflows/              -- ios-ci.yml e android-ci.yml, innescate solo dai path pertinenti
```

Ogni sottocartella ha un proprio `CLAUDE.md` con le sole informazioni specifiche di piattaforma (ambiente, stack tecnico, architettura, mapping navigazione, rischi/convenzioni). Le regole di business e i design token vivono **solo qui** — se una piattaforma ha bisogno di adattarli, il file da aggiornare è questo, non la copia locale.

## Design tokens

- Colori (hex derivati da OKLCH): background `#1c1c1c`, panel `#1f1f1f`, testo primario `#f7f7f7`, testo secondario `#a3a3a3`, accento blu `#7ab8ff`, rosso `#e0432f`, oro `#d9a441`, verde `#52cf83`, viola `#d17ee8`. Questi 5 colori (blu/rosso/oro/verde/viola) sono anche le card-color selezionabili per le moto.
- Scala stato a 3 colori (riusata sia per manutenzione che per best-lap): rosso = scaduto/peggiore, oro = in scadenza/medio, grigio = ok.
- Font: **Inter** (pesi 400/500/600/700/800/900); ogni piattaforma ha un proprio fallback di sistema se il file non è ancora disponibile (mai un fallback silenzioso — va segnalato in codice).
- Raggio angoli: bottoni 12–16px, sheet/modali 20–26px (bottom sheet 24px, wheel picker 26px, alert 20px), card moto 13–18px.
- Effetto "vetro": blur 16–32px, saturazione 170–200%, bordo 1px bianco 16–24% opacità, inset highlight in alto, ombra morbida sotto. Vale per tab bar, pill checklist, pill categorie, stepper, input, FAB, tutti i sheet/alert/paywall. **Mai** applicare glass a contenuti/liste — solo a controlli e layer di navigazione. Come tradurre questo look in API native è specifico di piattaforma (vedi `ios/CLAUDE.md` per Liquid Glass, `android/CLAUDE.md` per Haze).
- Tema **dark-mode fisso** su entrambe le piattaforme, nessuna light mode.
- Lingua UI: **italiano ovunque**, stringhe esatte da `design_handoff_motosetup_app/README.md` dove specificate (es. messaggi paywall) — non parafrasare, non deve divergere tra le due app.

## Modello dati (Firestore)

Stesso progetto Firebase per entrambe le piattaforme (un'app iOS + un'app Android registrate sotto lo stesso progetto).

```
users/{uid}                                     -- AppUser (nickname, email, plan, aiUsedToday, aiUsageDate, avatarURL)
users/{uid}/bikes/{bikeId}                      -- Bike
users/{uid}/bikes/{bikeId}/maintenance/{itemId} -- MaintenanceItem
users/{uid}/sessions/{sessionId}                -- Session
users/{uid}/sessions/{sessionId}/runs/{runId}   -- Run
users/{uid}/checklist/{itemId}                  -- ChecklistItem
users/{uid}/aiAdvice/{entryId}                  -- AIAdviceEntry
users/{uid}/customTracks/{trackId}              -- Piste aggiunte dall'utente
tracks/{trackId}                                -- Elenco piste curato, sola lettura
```

Storage: `users/{uid}/avatar.jpg`, `users/{uid}/bikes/{bikeId}/photo.jpg`. Regole di sicurezza: lettura/scrittura solo al proprietario (`request.auth.uid == uid`); `tracks/` sola lettura per utenti autenticati.

La **Cloud Function** per Consigli AI (chiamata LLM lato server, mai chiave API nel client) è platform-agnostic: si costruisce una sola volta e la consumano entrambe le app.

## Piano Premium — regole (non modificare senza motivo esplicito)

Free di default. Premium = **pagamento unico** €9,99 (StoreKit 2 non-consumable su iOS, Google Play Billing non-consumable su Android), nessun downgrade.

| Limite | Free | Messaggio paywall esatto |
|---|---|---|
| Moto in garage | 1 | "Il piano Free include 1 sola moto in garage. Passa a Premium per moto illimitate." |
| Consigli AI | 1/giorno | "Hai esaurito il consiglio AI gratuito di oggi. Passa a Premium per consigli illimitati." |
| Run per sessione | 3 | "Il piano Free include massimo 3 run per sessione. Passa a Premium per run illimitati." |

Manutenzione: nessun limite di piano. Gating centralizzato in un unico servizio/entitlement store per piattaforma (mai duplicare i controlli nei ViewModel/View). Il paywall è una **sheet custom** condivisa, non un alert di sistema (serve contenuto custom).

## Funzionamento Sessioni e Manutenzione

- Una **sessione** rappresenta un giorno in pista: pista, moto, meteo, una o più **run** (max 3 su Free, illimitati su Premium). Ogni run ha dati generali (ora, temperatura, best lap, giri) più parametri di setup per categoria (Sospensioni, Gomme, Rapporti, Elettronica) e note testuali per sottocategoria.
- Ogni voce di **manutenzione** ha `daysSinceService` e `intervalDays`. Stato = `daysSinceService / intervalDays`: **≥ 1.0 → "Scaduta"** (rosso), **≥ 0.8 → "In scadenza"** (oro), **altrimenti → "OK"** (grigio). Ordinamento: Scaduta → In scadenza → OK. Questa è logica pura, va implementata come funzione testabile indipendente dai model, identica su entrambe le piattaforme.

## Convenzioni generali

- Stringhe utente in italiano esatto come da design handoff; non tradurre né parafrasare i messaggi paywall/errore già definiti.
- Niente astrazioni premature: se un pattern si ripete 2-3 volte va bene duplicato finché non emerge davvero un terzo caso simile con lo stesso shape.
- Logica di business pura (status manutenzione, gating piano, ecc.) va in funzioni testabili separate dai modelli/dati, non incorporata nei model o nelle view.
