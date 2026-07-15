package com.motosetup.app.data.ai

data class MockAdviceResult(
    val parameterName: String,
    val parameterValue: String,
    val explanation: String,
)

private data class AdviceRule(val keywords: List<String>, val result: MockAdviceResult)

private val adviceRules = listOf(
    AdviceRule(
        keywords = listOf("sottosterza", "sottosterzo", "largo", "non chiude"),
        result = MockAdviceResult(
            parameterName = "Forcella — Compressione",
            parameterValue = "Aumenta di 2 click",
            explanation = "Il sottosterzo in staccata è spesso legato a un affondamento eccessivo dell'anteriore: più compressione riduce l'affondamento e stabilizza la forcella in frenata.",
        ),
    ),
    AdviceRule(
        keywords = listOf("sovrasterza", "sovrasterzo", "chiude troppo", "scivola dietro"),
        result = MockAdviceResult(
            parameterName = "Mono — Precarico",
            parameterValue = "Riduci di 2mm",
            explanation = "Un posteriore troppo carico in percorrenza tende a far chiudere la moto: meno precarico sul mono libera l'avantreno e rende la guida più neutra.",
        ),
    ),
    AdviceRule(
        keywords = listOf("pattina", "trazione", "scalda", "esce di gomma"),
        result = MockAdviceResult(
            parameterName = "Elettronica — Traction Control",
            parameterValue = "Aumenta di 1 livello",
            explanation = "Un pattinamento eccessivo in uscita di curva si corregge alzando il livello di TC, a costo di qualche decimo in accelerazione pura.",
        ),
    ),
    AdviceRule(
        keywords = listOf("vibra", "instabile", "rettilineo", "dritto"),
        result = MockAdviceResult(
            parameterName = "Rapporti — Passo catena",
            parameterValue = "Allunga di 2mm",
            explanation = "Un passo più lungo aumenta la stabilità in rettilineo a discapito della maneggevolezza in ingresso curva.",
        ),
    ),
    AdviceRule(
        keywords = listOf("pressione", "gomma fredda", "poco grip"),
        result = MockAdviceResult(
            parameterName = "Gomme — Pressione anteriore",
            parameterValue = "Riduci di 0.1 bar",
            explanation = "Una pressione a freddo troppo alta ritarda la messa in temperatura della gomma e riduce il grip nei primi giri.",
        ),
    ),
)

private val fallbackAdvice = MockAdviceResult(
    parameterName = "Sospensioni — Compressione",
    parameterValue = "Regola di 1 click",
    explanation = "Senza altri dettagli, il punto di partenza più comune è una piccola regolazione della compressione: annota il risultato nelle note del run e ripeti la domanda con più dettagli se il problema persiste.",
)

/**
 * Euristica locale a parole chiave — sostituisce temporaneamente la Cloud
 * Function LLM (CLAUDE.md radice, "La Cloud Function per Consigli AI...")
 * finché l'infrastruttura server non è pronta. Nessuna chiamata di rete.
 */
fun generateMockAdvice(question: String): MockAdviceResult {
    val normalized = question.lowercase()
    return adviceRules.firstOrNull { rule -> rule.keywords.any { normalized.contains(it) } }?.result
        ?: fallbackAdvice
}
