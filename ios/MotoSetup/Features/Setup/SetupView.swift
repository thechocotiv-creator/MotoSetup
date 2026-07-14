import SwiftUI

/// Placeholder Fase 0 — contenuto reale (sessioni, nuova sessione, dettaglio
/// run) arriva in Fase 4. Vedi design_handoff_motosetup_app/README.md #3-4.
struct SetupView: View {
    var body: some View {
        VStack {
            Text("Setup")
                .font(.appLargeTitle)
                .foregroundStyle(AppColor.textPrimary)
            Spacer()
        }
        .padding(.top, AppSpacing.xl)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(AppColor.background)
    }
}

#Preview {
    SetupView()
}
