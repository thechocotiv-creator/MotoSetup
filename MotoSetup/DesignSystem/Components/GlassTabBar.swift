import SwiftUI

enum AppTab: String, CaseIterable, Identifiable {
    case home, setup, consigliAI, profilo
    var id: String { rawValue }

    var title: String {
        switch self {
        case .home: "Home"
        case .setup: "Setup"
        case .consigliAI: "Consigli AI"
        case .profilo: "Profilo"
        }
    }

    var systemImage: String {
        switch self {
        case .home: "house.fill"
        case .setup: "magnifyingglass"
        case .consigliAI: "bubble.left.and.bubble.right.fill"
        case .profilo: "person.fill"
        }
    }
}

/// Tab bar custom stile Liquid Glass: pill animata dietro l'icona attiva.
/// Il `TabView` reale resta sotto (chrome nativo nascosto) per preservare
/// stato/scroll per tab — questa è solo la barra visuale sovrapposta.
/// Vedi CLAUDE.md — "Navigazione — mapping presentazioni".
struct GlassTabBar: View {
    @Binding var selection: AppTab
    @Namespace private var namespace

    var body: some View {
        Group {
            if #available(iOS 26, *) {
                GlassEffectContainer(spacing: 8) {
                    tabRow
                }
            } else {
                tabRow
                    .appGlass(cornerRadius: 28)
            }
        }
        .padding(.horizontal, AppSpacing.lg)
    }

    private var tabRow: some View {
        HStack(spacing: 4) {
            ForEach(AppTab.allCases) { tab in
                tabButton(tab)
            }
        }
        .padding(6)
    }

    @ViewBuilder
    private func tabButton(_ tab: AppTab) -> some View {
        let isSelected = tab == selection
        Button {
            withAnimation(.spring(response: 0.35, dampingFraction: 0.8)) {
                selection = tab
            }
        } label: {
            VStack(spacing: 2) {
                Image(systemName: tab.systemImage)
                    .font(.system(size: 20, weight: .medium))
                Text(tab.title)
                    .font(.appCaption)
            }
            .foregroundStyle(isSelected ? AppColor.textPrimary : AppColor.textSecondary)
            .padding(.vertical, 8)
            .frame(maxWidth: .infinity)
            .background(alignment: .center) {
                if isSelected {
                    selectedBackground
                        .matchedGeometryEffect(id: "tabPill", in: namespace)
                }
            }
            .contentShape(.rect)
        }
        .buttonStyle(.plain)
    }

    @ViewBuilder
    private var selectedBackground: some View {
        if #available(iOS 26, *) {
            Capsule()
                .fill(.clear)
                .glassEffect(.regular.tint(AppColor.accentBlue.opacity(0.35)), in: .capsule)
        } else {
            Capsule()
                .fill(AppColor.accentBlue.opacity(0.25))
                .background(.ultraThinMaterial, in: .capsule)
        }
    }
}

#Preview {
    ZStack(alignment: .bottom) {
        AppColor.background.ignoresSafeArea()
        GlassTabBar(selection: .constant(.home))
            .padding(.bottom, AppSpacing.lg)
    }
}
