import SwiftUI

/// Placeholder Fase 0 — contenuto reale arriva in Fase 6.
/// Vedi design_handoff_motosetup_app/README.md #6.
struct ProfiloView: View {
    var body: some View {
        VStack {
            Text("Profilo")
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
    ProfiloView()
}
