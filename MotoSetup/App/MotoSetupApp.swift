import SwiftUI

@main
struct MotoSetupApp: App {
    // Fase 1 aggiungerà qui FirebaseApp.configure() e lo switch
    // loggedOut/loggedIn via AppRootView (vedi CLAUDE.md).
    var body: some Scene {
        WindowGroup {
            RootTabView()
                .preferredColorScheme(.dark)
        }
    }
}
