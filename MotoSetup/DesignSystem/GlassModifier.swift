import SwiftUI

/// Primitiva unica per l'effetto vetro dell'app: iOS 26+ usa la vera Liquid
/// Glass (`.glassEffect`), iOS 18-25 ricade su `.ultraThinMaterial` + bordo +
/// ombra per approssimare lo stesso look. Non duplicare questa logica nelle
/// singole view — usare sempre `.appGlass(...)`.
///
/// Per gruppi di elementi glass ravvicinati (es. tab bar, pill categorie) non
/// basta questo modifier: servono `GlassEffectContainer` + `@Namespace` su
/// iOS 26 (il glass non può campionare altro glass senza un container
/// condiviso) — vedi i componenti dedicati in DesignSystem/Components/.
struct AppGlassModifier: ViewModifier {
    var cornerRadius: CGFloat = AppRadius.button
    var tint: Color? = nil

    @Environment(\.accessibilityReduceTransparency) private var reduceTransparency

    func body(content: Content) -> some View {
        if reduceTransparency {
            content
                .background(AppColor.panel, in: .rect(cornerRadius: cornerRadius))
        } else if #available(iOS 26, *) {
            content
                .glassEffect(glassStyle, in: .rect(cornerRadius: cornerRadius))
        } else {
            content
                .background(.ultraThinMaterial, in: .rect(cornerRadius: cornerRadius))
                .overlay {
                    RoundedRectangle(cornerRadius: cornerRadius, style: .continuous)
                        .strokeBorder(Color.white.opacity(0.18), lineWidth: 1)
                }
                .shadow(color: .black.opacity(0.35), radius: 16, y: 6)
        }
    }

    @available(iOS 26, *)
    private var glassStyle: Glass {
        tint == nil ? .regular : .regular.tint(tint!)
    }
}

extension View {
    func appGlass(cornerRadius: CGFloat = AppRadius.button, tint: Color? = nil) -> some View {
        modifier(AppGlassModifier(cornerRadius: cornerRadius, tint: tint))
    }
}
