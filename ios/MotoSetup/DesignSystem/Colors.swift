import SwiftUI

/// Palette fissa (dark-mode only) derivata dai token OKLCH del design handoff.
/// Vedi design_handoff_motosetup_app/README.md — sezione "Design Tokens".
enum AppColor {
    static let background = Color(hex: 0x1C1C1C)
    static let panel = Color(hex: 0x1F1F1F)
    static let textPrimary = Color(hex: 0xF7F7F7)
    static let textSecondary = Color(hex: 0xA3A3A3)

    static let accentBlue = Color(hex: 0x7AB8FF)
    static let red = Color(hex: 0xE0432F)
    static let gold = Color(hex: 0xD9A441)
    static let green = Color(hex: 0x52CF83)
    static let purple = Color(hex: 0xD17EE8)

    /// Colore associato allo stato di manutenzione o al best-lap relativo di una sessione.
    static func status(_ status: AppStatus) -> Color {
        switch status {
        case .ok: return textSecondary
        case .upcoming: return gold
        case .expired: return red
        }
    }
}

/// Scala a 3 stati riusata sia per la manutenzione (scaduta/in scadenza/ok)
/// sia per il colore del best-lap relativo tra le sessioni di una pista.
enum AppStatus {
    case ok
    case upcoming
    case expired
}

/// I 5 colori selezionabili per le card moto nel garage.
enum BikeCardColor: String, CaseIterable, Codable {
    case blu, rosso, oro, verde, viola

    var swatch: Color {
        switch self {
        case .blu: return AppColor.accentBlue
        case .rosso: return AppColor.red
        case .oro: return AppColor.gold
        case .verde: return AppColor.green
        case .viola: return AppColor.purple
        }
    }

    var label: String {
        rawValue.prefix(1).uppercased() + rawValue.dropFirst()
    }
}

extension Color {
    init(hex: UInt32) {
        let r = Double((hex >> 16) & 0xFF) / 255
        let g = Double((hex >> 8) & 0xFF) / 255
        let b = Double(hex & 0xFF) / 255
        self.init(red: r, green: g, blue: b)
    }
}
