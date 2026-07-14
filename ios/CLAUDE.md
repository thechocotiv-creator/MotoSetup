# CLAUDE.md — MotoSetup (iOS)

Guida specifica per la piattaforma iOS. Per business logic, design token e schema dati condivisi vedi il `CLAUDE.md` alla radice del repo — non duplicare qui quel contenuto.

## Ambiente di sviluppo — IMPORTANTE

Lo sviluppo avviene su **Windows**: qui non è disponibile Xcode né un Simulator. Non è possibile compilare, eseguire o testare localmente in questa sessione.

- Il progetto Xcode **non va creato/committato a mano come `.xcodeproj`**: si usa **XcodeGen** con il manifest `project.yml` in questa cartella, che genera il progetto in CI (vedi `.github/workflows/ios-ci.yml`).
- La verifica avviene tramite **GitHub Actions su runner macOS**: build (`xcodebuild build`) e test (`xcodebuild test`) headless su Simulator. Non fare mai affermazioni tipo "ho verificato che funziona" senza che la CI sia passata — se non è possibile lanciare la CI, dichiararlo esplicitamente.
- Se in futuro sarà disponibile un Mac (fisico, VM o sessione Claude Code su macOS), lì si potrà aprire `project.yml` con `xcodegen generate` e usare Xcode/Simulator normalmente.
- Lo sviluppo iOS è attualmente **in pausa** a favore della versione Android (verificabile in locale su questa macchina); riprendere da qui quando sarà disponibile un modo di verificare il lavoro direttamente.

## Piattaforma e target

- Solo **iOS** (iPhone).
- **Deployment target: iOS 18.0**. Xcode/SDK richiesti: Xcode 26+ (necessario anche solo per compilare i branch `#available(iOS 26, *)`).
- **Liquid Glass** (API iOS 26: `.glassEffect()`, `GlassEffectContainer`, `.buttonStyle(.glass)`, glass `TabView`) usato ovunque il design lo richiede, con **fallback su iOS 18–25** basato su `.ultraThinMaterial` + bordo 1px + ombra, mai la sola API iOS 26 senza fallback. Vedi `MotoSetup/DesignSystem/GlassModifier.swift` come primitiva unica — non duplicare codice di blur/bordo in giro per le view.
- Riferirsi alla skill/plugin **Liquid Glass** per pattern, pitfall (`GlassEffectContainer` obbligatorio per glass vicini, no glass su liste/contenuti, solo su nav layer/controlli) e API aggiornate.

## Architettura

**MVVM + Repository layer + DI via `@Environment`.**

- Le View non chiamano mai direttamente Firebase/StoreKit: passano sempre da un protocollo di repository/service (`BikeRepository`, `AuthService`, `EntitlementStore`, ecc.) iniettato tramite `AppEnvironment` in `@Environment(\.appEnvironment)`.
- Ogni feature ha un `@Observable` ViewModel che orchestra i repository e espone stato alla View.
- Esiste sempre una implementazione `.live` (Firebase-backed) e una `.preview` (fake in-memory, in `Mocks/PreviewData.swift`) di `AppEnvironment`, per poter usare le SwiftUI Preview senza toccare la rete.
- Struttura cartelle: `App/`, `DesignSystem/`, `Models/`, `Services/`, `Features/<NomeFeature>/`, `Resources/`, `Mocks/`. Organizzazione per feature, non per layer, tranne le parti trasversali (design system, modelli, servizi).

## Navigazione — mapping presentazioni

- `.sheet()`: Modifica moto, Modifica profilo, Modifica password, Abbonamento, wheel picker.
- `.fullScreenCover()`: Checklist pista, Dettaglio run, Manutenzione moto, Nuova sessione, Tutte le sessioni.
- `.alert()` / `.confirmationDialog()` (`role: .destructive`): Elimina moto, Elimina account.
- Tab bar: 4 tab (Home, Setup, Consigli AI, Profilo), root in `TabView` reale (per preservare stato/scroll per tab) con chrome nativo nascosto e `GlassTabBar` custom sovrapposto.
- Root app: switch tra `.loading` / `OnboardingRootView` (loggedOut) / `RootTabView` (loggedIn), pilotato da `AuthService`.

## Rischi/decisioni aperte da tenere a mente

1. **Font Inter**: usare la distribuzione statica per-peso (non variable font) per semplicità in SwiftUI; licenza SIL OFL, includere il testo di licenza nel repo quando aggiunta. Condiviso con Android: gli stessi file `.ttf` vanno duplicati in `MotoSetup/Resources/Fonts/`.
2. **Icone**: meteo ed elettronica mappano bene su SF Symbols; Sospensioni/Gomme/Rapporti non hanno un match nativo preciso — decisione condivisa con Android (icone custom), non da risolvere due volte.
3. **Eliminazione account / cambio email**: Firebase Auth richiede spesso re-autenticazione recente (`requires-recent-login`); l'eliminazione ricorsiva delle subcollection Firestore va fatta lato server (Cloud Function `deleteUserData`), non dal client.
4. **Wheel picker**: usare `Picker(.wheel)`/`DatePicker(.wheel)` nativi componendo più colonne, non un componente scroll-snap custom.
5. **Restore Purchases**: obbligatorio per App Review anche se non mostrato nei mockup — aggiungere comunque nello sheet Abbonamento.

## Convenzioni di codice (Swift)

- Nessun commento superfluo; solo dove il *perché* non è ovvio dal codice.
- Modelli `Codable` puri, senza logica; la logica di business (es. `maintenanceStatus()`) vive in funzioni pure testabili, non nei model.
