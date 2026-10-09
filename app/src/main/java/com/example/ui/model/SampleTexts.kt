package com.example.ui.model

data class SampleTextItem(
    val title: String,
    val languageCode: String,
    val languageName: String,
    val flagEmoji: String,
    val levelBadge: String,
    val description: String,
    val text: String
)

object SampleTextsData {
    val samples = listOf(
        SampleTextItem(
            title = "Los Secretos de la Alhambra",
            languageCode = "es",
            languageName = "Spanisch",
            flagEmoji = "🇪🇸",
            levelBadge = "B2",
            description = "Historischer Text über die maurische Architektur in Granada.",
            text = """El murmullo constante del agua en los patios de la Alhambra suscita una profunda melancolía. Las intrincadas yeserías revelan la destreza incomparable de los artesanos andalusíes. Caminar bajo los arcos de herradura es sumergirse en un pasado donde la luz y la sombra dialogan en perpetua armonía. Cada inscripción poética en las paredes rinde homenaje a la belleza efímera de la existencia terrenal."""
        ),
        SampleTextItem(
            title = "Le Marché Provençal",
            languageCode = "fr",
            languageName = "Französisch",
            flagEmoji = "🇫🇷",
            levelBadge = "B1/B2",
            description = "Schilderung eines sonnigen Markttages im Süden Frankreichs.",
            text = """Le soleil matinal inondait les ruelles pavées tandis que les étals s'animaient. L'arôme enivrant de la lavande se mêlait au parfum du pain croustillant tout juste sorti du fourneau. Les commerçants vantaient avec enthousiasme la saveur délicate de leurs fromages affinés. Flâner à travers cette mosaïque de couleurs offrait une véritable fête pour les sens."""
        ),
        SampleTextItem(
            title = "The Hidden Life of Ancient Forests",
            languageCode = "en",
            languageName = "Englisch",
            flagEmoji = "🇬🇧",
            levelBadge = "C1",
            description = "Naturwissenschaftlicher Artikel über Baumkommunikation.",
            text = """Beneath the forest floor lies an intricate subterranean network woven by fungal mycelium. This astonishing biological lattice enables towering redwoods to distribute nutrients and transmit biochemical warnings against impending perils. Far from being solitary sentinels, trees actively nurture their offspring and foster collective resilience against environmental adversity."""
        ),
        SampleTextItem(
            title = "I Vicoli Segreti di Venezia",
            languageCode = "it",
            languageName = "Italienisch",
            flagEmoji = "🇮🇹",
            levelBadge = "B2",
            description = "Atmosphärischer Reisebericht über Venedigs Kanäle.",
            text = """La nebbia autunnale avvolgeva i ponti di pietra in un'atmosfera quasi fiabesca. Il riflesso dei lampioni danzava sull'acqua scura dei canali solitari. All'angolo di una calle appartata, una bottega artigiana custodiva maschere decorate con foglia d'oro e velluto prezioso, custodi di una tradizione millenaria e affascinante."""
        )
    )
}
