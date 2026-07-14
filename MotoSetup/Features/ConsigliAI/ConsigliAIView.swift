import SwiftUI

/// Placeholder Fase 0 — contenuto reale arriva in Fase 5.
/// Vedi design_handoff_motosetup_app/README.md #5.
struct ConsigliAIView: View {
    var body: some View {
        VStack {
            Text("Consigli AI")
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
    ConsigliAIView()
}
