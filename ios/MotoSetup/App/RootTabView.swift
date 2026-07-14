import SwiftUI

/// Shell dei 4 tab con GlassTabBar custom sovrapposta e chrome nativo nascosto.
/// Usa un TabView reale sotto per preservare stato/scroll per tab.
struct RootTabView: View {
    @State private var selection: AppTab = .home

    var body: some View {
        ZStack(alignment: .bottom) {
            TabView(selection: $selection) {
                NavigationStack { HomeView() }
                    .tag(AppTab.home)
                NavigationStack { SetupView() }
                    .tag(AppTab.setup)
                NavigationStack { ConsigliAIView() }
                    .tag(AppTab.consigliAI)
                NavigationStack { ProfiloView() }
                    .tag(AppTab.profilo)
            }
            .toolbar(.hidden, for: .tabBar)

            GlassTabBar(selection: $selection)
                .padding(.bottom, AppSpacing.sm)
        }
        .background(AppColor.background.ignoresSafeArea())
    }
}

#Preview {
    RootTabView()
}
