# CLAUDE.md — MotoSetup (Android)

Guida specifica per la piattaforma Android. Per business logic, design token e schema dati condivisi vedi il `CLAUDE.md` alla radice del repo — non duplicare qui quel contenuto.

## Ambiente di sviluppo — IMPORTANTE

A differenza di iOS, qui lo sviluppo **si verifica in locale su questa stessa macchina Windows**: Android Studio, SDK, NDK e un emulatore (`Pixel_10_Pro` AVD) sono già installati e funzionanti. La CI (`.github/workflows/android-ci.yml`) è una rete di sicurezza secondaria, non l'unico modo di verificare il lavoro.

Workflow di verifica standard per ogni fase:
1. `cd android && ./gradlew :app:assembleDebug`
2. Avviare l'emulatore se non attivo: `emulator -avd Pixel_10_Pro`, poi `adb wait-for-device` + polling `adb shell getprop sys.boot_completed` fino a `1` (mai uno sleep cieco lungo).
3. `adb install -r app/build/outputs/apk/debug/app-debug.apk` poi `adb shell am start -n com.motosetup.app/.MainActivity`.
4. Screenshot di verifica: `adb exec-out screencap -p > .local/screenshot.png`, poi lettura con il tool Read per conferma visiva diretta.
5. `./gradlew testDebugUnitTest` per i test unitari (veloce, JVM-only); `connectedDebugAndroidTest` solo quando serve, contro l'emulatore già attivo.

Non affermare mai che una feature "funziona" senza aver fatto questo giro (build → install → screenshot letto) o senza CI verde — stesso principio dell'iOS, ma qui è possibile farlo davvero ad ogni fase, non solo a fine lavoro.

**Nota pratica**: `java`/`gradle`/`kotlinc` non sono sul PATH diretto, ma `JAVA_HOME` punta al JBR di Android Studio e il Gradle wrapper lo trova da solo — usare sempre `./gradlew`, mai un comando `gradle` nudo.

## Piattaforma e target

- **minSdk 26**, **compileSdk/targetSdk 36** (Android 16 — richiesto da Google Play per nuove app entro il 31/08/2026). Usato **AGP 8.13.0** (non 9.x — vedi rischio #7): supporta compileSdk 36 restando sulla linea 8.x con plugin Kotlin classico, evitando il supporto Kotlin integrato di AGP 9 ancora immaturo in combinazione con KSP+Compose+Hilt a luglio 2026.
- **applicationId `com.motosetup.app`** — identico al bundle id iOS, stesso progetto Firebase per entrambe le app.
- **Jetpack Compose** + **Material 3**, tema dark-mode fisso (vedi `ui/theme/Theme.kt`).
- Effetto "vetro": **Haze** (`dev.chrisbanes.haze`), non `Modifier.blur()` nativo (richiederebbe API 31+ senza fallback). Un unico `HazeState` condiviso via `CompositionLocal`, contenuto marcato `Modifier.hazeSource(state)` — equivalente strutturale del `GlassEffectContainer` di iOS (tutte le superfici vetro campionano lo stesso backdrop). Primitiva unica: `Modifier.appGlass(cornerRadius, tint)` in `ui/theme/Glass.kt` — non duplicare codice di blur/bordo nelle singole schermate.
- Morph della pill di selezione (tab bar, pill categorie): `SharedTransitionLayout` + `rememberSharedContentState` — analogo Compose di `matchedGeometryEffect`.

## Architettura

**MVVM + Repository layer + DI via Hilt.**

- Le Composable non chiamano mai direttamente Firebase/Billing: passano sempre da un'interfaccia di repository/service iniettata via Hilt (`@HiltViewModel`).
- `data/di/FirebaseModule.kt` fornisce i singleton Firebase; `data/di/RepositoryModule.kt` fa il bind interfacce → implementazioni Firebase.
- Ogni feature ha un `XyzViewModel` (`StateFlow<UiState>`) + `XyzScreen.kt` (Composable stateless + wrapper `hiltViewModel()`).
- `model/` — data class Kotlin che rispecchiano 1:1 i modelli iOS: `AppUser`, `Bike`, `Session`, `Run` (+ `SuspensioneSetup`/`GommeSetup`/`RapportiSetup`/`ElettronicaSetup`), `MaintenanceItem`, `ChecklistItem`, `AIAdviceEntry`. Mapping Firestore nativo (`@DocumentId`, no-arg constructor via default arguments), nessuna libreria di serializzazione aggiuntiva.
- Struttura cartelle: `app/src/main/java/com/motosetup/app/{ui/theme, data/di, data/repository, model, feature/<nome>, navigation}`.

## Navigazione — mapping presentazioni (Jetpack Navigation 3)

Implementato in Fase 2 (`navigation/AppRoute.kt`, `AppSheet.kt`, `AppDialog.kt`, `PaywallReason.kt`, `AppNavActions.kt`, `RootScaffold.kt`). Le schermate di destinazione restano placeholder fino a Fase 3/4/6 — solo l'infrastruttura è reale.

- **Push (NavEntry in back stack)**: Checklist pista, Dettaglio run, Manutenzione moto, Nuova sessione, Tutte le sessioni. `AppRoute` è un sealed interface (include anche `HomeRoot`/`SetupRoot`/`ConsigliAIRoot`/`ProfiloRoot`, i root dei 4 tab); ogni tab ha il proprio `SnapshotStateList<AppRoute>` (`remember { mutableStateListOf(...) }`, non `rememberNavBackStack` — non serve la persistenza tra process death via kotlinx.serialization in questa fase), renderizzato da un unico `NavDisplay` con `entryProvider`.
- **Bottom sheet / alert dialog**: Modifica moto, Modifica profilo, Modifica password, Abbonamento, wheel/number picker (sheet, `AppSheet`); Elimina moto, Elimina account (dialog distruttivo, `AppDialog`). **Non** `ModalBottomSheet`/`AlertDialog` di Material3: aprono in una `Dialog`, una finestra Android separata che Haze non può sfocare (limite noto della libreria — non può campionare un backdrop fuori dalla propria finestra). Implementati come overlay custom (`AppBottomSheetHost.kt`, `AppAlertDialogHost.kt`) nello stesso `Box`/`CompositionLocalProvider(LocalHazeState)` di `RootScaffold`, con `appGlass` per il trattamento vetro e comportamento equivalente (scrim, dismiss su tap/back).
- **Paywall**: stesso pattern overlay-custom (`PaywallSheet.kt`), pilotato da `PaywallReason?` esposto tramite `AppNavActions.showPaywall` — non un vero `StateFlow` root-level ma stato Compose (`remember { mutableStateOf<PaywallReason?>(null) }`) hoistato in `RootScaffold`, sufficiente finché nessun ViewModel esterno deve osservarlo.
- `LocalAppNavActions` (CompositionLocal, stesso pattern di `LocalHazeState`) espone `navigate`/`navigateBack`/`openSheet`/`closeSheet`/`openDialog`/`closeDialog`/`showPaywall` a qualunque schermata annidata senza prop-drilling.
- Root: `AppRoot.kt` con switch `Loading / LoggedOut / LoggedIn` guidato da `AuthViewModel`.
- Ogni tab mantiene il proprio back stack (`SnapshotStateList<AppRoute>`) per preservare lo stato di navigazione allo switch tab (il `remember` dei 4 back stack sta sopra il `when(selectedTab)` in `RootScaffold`).

## Rischi/decisioni aperte da tenere a mente

1. **Font Inter**: condiviso con iOS — stessi file `.ttf`, rinominati secondo le regole Android (minuscolo+underscore, es. `inter_semibold.ttf`) in `app/src/main/res/font/`. Fallback `FontFamily.Default` (non serve la stessa cautela "no fallback silenzioso" di iOS, ma mantenere comunque `AppFont.usingFallback` per parità/debug).
2. **Icone categorie**: nessun match nativo preciso su Material Symbols per Sospensioni/Gomme/Rapporti — decisione condivisa con iOS (icone custom), non da risolvere due volte.
3. **Google Sign-In**: implementato in Fase 1 via Credential Manager (`GetGoogleIdOption`, non il deprecato `GoogleSignInClient`) in `feature/onboarding/OnboardingHost.kt`. Richiede comunque che l'SHA-1 del debug keystore sia registrato manualmente su Firebase Console (`keytool -list -v -keystore %USERPROFILE%\.android\debug.keystore -alias androiddebugkey -storepass android -keypass android`) e che `google-services.json` (scaricato dopo aver abilitato Google come provider) sia presente in `app/`. Firebase CLI non installata su questa macchina — nessuna automazione tentata, setup fatto manualmente in console.
4. **Play Billing**: nessun account Play Console richiesto fino alla Fase 7. Fino ad allora `EntitlementStore` resta un'implementazione fake/debug (pattern identico al `.preview` di iOS); si passa a `BillingClient` reale solo quando l'account ($25 una tantum) sarà creato.
5. **Wheel picker**: Compose non ha un equivalente nativo di `Picker(.wheel)` — da costruire (es. `LazyColumn` con snapping) in Fase 4.
6. **Gradle wrapper su Windows**: bit eseguibile + line-ending gestiti da `.gitattributes` alla radice (`android/gradlew text eol=lf`) + `git update-index --chmod=+x android/gradlew` dopo la prima generazione — altrimenti la CI Linux fallisce per permessi.
7. **AGP 9 evitata**: AGP 9.x ha introdotto il supporto Kotlin integrato che rende il plugin `org.jetbrains.kotlin.android` opzionale/deprecato; provato in Fase 0 e risultato incompatibile con il setup KSP+Compose+Hilt corrente (crash di cast interno anche con l'opt-out `android.builtInKotlin=false`). Risolto restando su **AGP 8.13.0**, che supporta comunque compileSdk 36 mantenendo il plugin Kotlin classico — nessuna migrazione necessaria per ora. Da rivalutare solo se una libreria futura richiederà esplicitamente AGP 9+.
8. **Sheet/dialog e Haze**: Material3 `ModalBottomSheet`/`AlertDialog` aprono in una `Dialog` (finestra Android separata); Haze sfoca solo contenuto nella stessa finestra/albero di composizione, quindi non può campionare il backdrop reale dietro quei componenti. Risolto in Fase 2 con overlay custom (`AppBottomSheetHost.kt`, `AppAlertDialogHost.kt`, `PaywallSheet.kt`) nello stesso `Box` di `RootScaffold` — stesso `LocalHazeState`, stesso comportamento (scrim, dismiss su tap/back) ma nessuna finestra separata. Non tornare a Material3 `ModalBottomSheet`/`AlertDialog` per superfici che devono avere l'effetto vetro.

## Convenzioni di codice (Kotlin)

- Nessun commento superfluo; solo dove il *perché* non è ovvio dal codice.
- Data class pure, senza logica; la logica di business (es. `maintenanceStatus()`) vive in funzioni pure top-level testabili in `app/src/test/`, non nelle data class.
