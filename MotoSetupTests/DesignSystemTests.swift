import SwiftUI
import Testing
@testable import MotoSetup

/// Test di base per la Fase 0: confermano che target di test, design system
/// e branch #available compilino ed eseguano correttamente in CI.
struct DesignSystemTests {
    @Test func radiiAreWithinDesignTokenRange() {
        #expect(AppRadius.button >= 12 && AppRadius.button <= 16)
        #expect(AppRadius.bottomSheet >= 20 && AppRadius.bottomSheet <= 26)
        #expect(AppRadius.wheelPicker == 26)
        #expect(AppRadius.alert == 20)
    }

    @Test func bikeCardColorHasFiveOptions() {
        #expect(BikeCardColor.allCases.count == 5)
    }

    @Test func maintenanceStatusColorScaleUsesThreeStates() {
        let ok = AppColor.status(.ok)
        let upcoming = AppColor.status(.upcoming)
        let expired = AppColor.status(.expired)
        #expect(ok != upcoming)
        #expect(upcoming != expired)
        #expect(ok != expired)
    }
}
