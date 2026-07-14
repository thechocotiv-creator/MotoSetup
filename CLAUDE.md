# CLAUDE.md — MotoSetup (iOS)

Guida di riferimento per lo sviluppo dell'app MotoSetup. Modificare questo file solo quando una decisione architetturale cambia davvero — non per dettagli di singola schermata.

## Cos'è MotoSetup

App iOS in italiano per motociclisti da pista: garage moto, sessioni/run con parametri di setup (sospensioni, gomme, rapporti, elettronica), manutenzione moto, consigli AI sull'assetto, piano Premium a pagamento unico (€9,99, non abbonamento).

La fonte di verità per design, copy esatto e business logic è `design_handoff_motosetup_app/README.md` e gli screenshot in `design_handoff_motosetup_app/screenshots/`. `MotoSetup App.dc.html` e `ios-frame.jsx` sono **riferimenti di design**, non codice da portare: vanno ricreati nativamente in SwiftUI.

## Ambiente di sviluppo — IMPORTANTE

Lo sviluppo avviene su **Windows**: qui non è disponibile Xcode né un Simulator. Non è possibile compilare, eseguire o testare localmente in questa sessione.

- Il progetto Xcode **non va creato/committato a mano come `.xcodeproj`**: si usa **XcodeGen** con il manifest `project.yml` alla radice, che genera il progetto in CI (vedi `.github/workflows/`).
- La verifica avviene tramite **GitHub Actions su runner macOS**: build (`xcodebuild build`) e test (`xcodebuild test`) headless su Simulator. Non fare mai affermazioni tipo "ho verificato che funziona" senza che la CI sia passata — se non è possibile lanciare la CI, dichiararlo esplicitamente.
- Se in futuro sarà disponibile un Mac (fisico, VM o sessione Claude Code su macOS), lì si potrà aprire `project.yml` con `xcodegen generate` e usare Xcode/Simulator normalmente.

## Piattaforma e target

- Solo **iOS** (iPhone). Android è fuori scope: sarà eventualmente un progetto separato con stack diverso (SwiftUI non gira su Android).
- **Deployment target: iOS 18.0**. Xcode/SDK richiesti: Xcode 26+ (necessario anche solo per compilare i branch `#available(iOS 26, *)`).
- **Liquid Glass** (API iOS 26: `.glassEffect()`, `GlassEffectContainer`, `.buttonStyle(.glass)`, glass `TabView`) usato ovunque il design lo richiede, con **fallback su iOS 18–25** basato su `.ultraThinMaterial` + bordo 1px + ombra, mai la sola API iOS 26 senza fallback. Vedi `DesignSystem/GlassModifier.swift` come primitiva unica — non duplicare codice di blur/bordo in giro per le view.
- Riferirsi alla skill/plugin **Liquid Glass** per pattern, pitfall (`GlassEffectContainer` obbligatorio per glass vicini, no glass su liste/contenuti, solo su nav layer/controlli) e API aggiornate.
- Tema **dark-mode fisso**, nessuna light mode.
- Lingua UI: **italiano ovunque**, stringhe esatte da `README.md` del design handoff dove specificate (es. messaggi paywall) — non parafrasare.

## Architettura

**MVVM + Repository layer + DI via `@Environment`.**

- Le View non chiamano mai direttamente Firebase/StoreKit: passano sempre da un protocollo di repository/service (`BikeRepository`, `AuthService`, `EntitlementStore`, ecc.) iniettato tramite `AppEnvironment` in `@Environment(\.appEnvironment)`.
- Ogni feature ha un `@Observable` ViewModel che orchestra i repository e espone stato alla View.
- Esiste sempre una implementazione `.live` (Firebase-backed) e una `.preview` (fake in-memory, in `Mocks/PreviewData.swift`) di `AppEnvironment`, per poter usare le SwiftUI Preview senza toccare la rete.
- Struttura cartelle: `App/`, `DesignSystem/`, `Models/`, `Services/`, `Features/<NomeFeature>/`, `Resources/`, `Mocks/`. Organizzazione per feature, non per layer, tranne le parti trasversali (design system, modelli, servizi).

## Design system — token principali

Fonte: `design_handoff_motosetup_app/README.md` sezione "Design Tokens".

- Colori (hex derivati da OKLCH): background `#1c1c1c`, panel `#1f1f1f`, testo primario `#f7f7f7`, testo secondario `#a3a3a3`, accento blu `#7ab8ff`, rosso `#e0432f`, oro `#d9a441`, verde `#52cf83`, viola `#d17ee8`. Questi 5 colori (blu/rosso/oro/verde/viola) sono anche le card-color selezionabili per le moto.
- Scala stato a 3 colori (riusata sia per manutenzione che per best-lap): rosso = scaduto/peggiore, oro = in scadenza/medio, grigio = ok.
- Font: **Inter** (pesi 400/500/600/700/800/900), fallback automatico a San Francisco se il file non è embeddato — mai un fallback silenzioso, va segnalato in `Typography.swift`.
- Raggio angoli: bottoni 12–16px, sheet/modali 20–26px (bottom sheet 24px, wheel picker 26px, alert 20px), card moto 13–18px.
- Effetto vetro: blur 16–32px, saturazione 170–200%, bordo 1px bianco 16–24% opacità, inset highlight in alto, ombra morbida sotto. Vale per tab bar, pill checklist, pill categorie, stepper, input, FAB, tutti i sheet/alert/paywall. **Mai** applicare glass a contenuti/liste — solo a controlli e layer di navigazione (regola della skill Liquid Glass).

## Modello dati (Firestore)

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

## Piano Premium — regole (non modificare senza motivo esplicito)

Free di default. Premium = **pagamento unico** €9,99 (StoreKit 2 non-consumable), nessun downgrade.

| Limite | Free | Messaggio paywall esatto |
|---|---|---|
| Moto in garage | 1 | "Il piano Free include 1 sola moto in garage. Passa a Premium per moto illimitate." |
| Consigli AI | 1/giorno | "Hai esaurito il consiglio AI gratuito di oggi. Passa a Premium per consigli illimitati." |
| Run per sessione | 3 | "Il piano Free include massimo 3 run per sessione. Passa a Premium per run illimitati." |

Manutenzione: nessun limite di piano. Gating centralizzato in `EntitlementStore` (`canAddBike`, `canAskAI`, `canAddRun`); mai duplicare i controlli nei ViewModel. Il paywall è uno **sheet custom** condiviso (`appState.paywallReason`), non un `.alert()` di sistema (non supporta contenuto custom).

## Navigazione — mapping presentazioni

- `.sheet()`: Modifica moto, Modifica profilo, Modifica password, Abbonamento, wheel picker.
- `.fullScreenCover()`: Checklist pista, Dettaglio run, Manutenzione moto, Nuova sessione, Tutte le sessioni.
- `.alert()` / `.confirmationDialog()` (`role: .destructive`): Elimina moto, Elimina account.
- Tab bar: 4 tab (Home, Setup, Consigli AI, Profilo), root in `TabView` reale (per preservare stato/scroll per tab) con chrome nativo nascosto e `GlassTabBar` custom sovrapposto.
- Root app: switch tra `.loading` / `OnboardingRootView` (loggedOut) / `RootTabView` (loggedIn), pilotato da `AuthService`.

## Rischi/decisioni aperte da tenere a mente

1. **Font Inter**: usare la distribuzione statica per-peso (non variable font) per semplicità in SwiftUI; licenza SIL OFL, includere il testo di licenza nel repo quando aggiunta.
2. **Icone**: meteo ed elettronica mappano bene su SF Symbols; Sospensioni/Gomme/Rapporti non hanno un match nativo preciso — serve una decisione esplicita (symbol approssimato vs icone custom) prima della Fase 4 (Run detail).
3. **Consigli AI**: richiede una **Firebase Cloud Function** (chiamata LLM lato server, mai chiave API nel client) — infrastruttura aggiuntiva rispetto ad Auth/Firestore/Storage.
4. **Eliminazione account / cambio email**: Firebase Auth richiede spesso re-autenticazione recente (`requires-recent-login`); l'eliminazione ricorsiva delle subcollection Firestore va fatta lato server (Cloud Function `deleteUserData`), non dal client.
5. **Wheel picker**: usare `Picker(.wheel)`/`DatePicker(.wheel)` nativi componendo più colonne, non un componente scroll-snap custom.
6. **Restore Purchases**: obbligatorio per App Review anche se non mostrato nei mockup — aggiungere comunque nello sheet Abbonamento.

## Convenzioni di codice

- Nessun commento superfluo; solo dove il *perché* non è ovvio dal codice.
- Niente astrazioni premature: se un pattern si ripete 2-3 volte va bene duplicato finché non emerge davvero un terzo caso simile con lo stesso shape.
- Modelli `Codable` puri, senza logica; la logica di business (es. `maintenanceStatus()`) vive in funzioni pure testabili, non nei model.
- Le stringhe utente restano in italiano esatto come da design handoff; non tradurre né parafrasare i messaggi paywall/errore già definiti.
