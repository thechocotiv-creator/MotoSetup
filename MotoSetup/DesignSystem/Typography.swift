import SwiftUI

/// Inter (pesi 400-900) è il font del design; se i file non sono ancora
/// stati aggiunti a Resources/Fonts, si ricade su San Francisco. Il fallback
/// è esplicito (non silenzioso): `AppFont.usingFallback` lo espone per poterlo
/// segnalare in UI/log durante lo sviluppo.
enum AppFont {
    private static let interFamily = "Inter"

    private(set) static var usingFallback = !interAvailable

    private static var interAvailable: Bool {
        UIFont.familyNames.contains { $0.localizedCaseInsensitiveCompare(interFamily) == .orderedSame }
    }

    static func font(_ weight: Font.Weight, size: CGFloat) -> Font {
        guard interAvailable, let postScriptName = interPostScriptName(for: weight) else {
            return .system(size: size, weight: weight, design: .default)
        }
        return .custom(postScriptName, size: size)
    }

    private static func interPostScriptName(for weight: Font.Weight) -> String? {
        let suffix: String
        switch weight {
        case .regular: suffix = "Regular"
        case .medium: suffix = "Medium"
        case .semibold: suffix = "SemiBold"
        case .bold: suffix = "Bold"
        case .heavy: suffix = "ExtraBold"
        case .black: suffix = "Black"
        default: suffix = "Regular"
        }
        let name = "\(interFamily)-\(suffix)"
        return UIFont(name: name, size: 12) != nil ? name : nil
    }
}

extension Font {
    static let appLargeTitle = AppFont.font(.heavy, size: 28)      // "Home", "Setup", "Profilo"
    static let appTitle = AppFont.font(.bold, size: 20)
    static let appHeadline = AppFont.font(.semibold, size: 17)
    static let appBody = AppFont.font(.medium, size: 15)
    static let appCaption = AppFont.font(.medium, size: 13)
    static let appEyebrow = AppFont.font(.bold, size: 11)          // label sezione, uppercase, tracking
}

extension View {
    /// Stile per le etichette di sezione tipo "ULTIMA SESSIONE", "ACCOUNT".
    func eyebrowStyle() -> some View {
        self
            .font(.appEyebrow)
            .textCase(.uppercase)
            .tracking(0.5)
            .foregroundStyle(AppColor.textSecondary)
    }
}
