import SwiftUI

/// Placeholder Fase 0 — contenuto reale (ultima sessione, checklist, garage
/// moto) arriva in Fase 3. Vedi design_handoff_motosetup_app/README.md #2.
struct HomeView: View {
    var body: some View {
        VStack {
            Text("Home")
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
    HomeView()
}
