package com.example.ui.model

import androidx.compose.ui.graphics.Color

enum class SuitId(
    val id: String,
    val title: String,
    val description: String,
    val starCost: Int,
    val badge: String? = null
) {
    NONE("none", "Standard", "Klassisches gemütliches Lern-Outfit", 0),
    KEYS("keys", "Keys (Klavier)", "Legendärer Klavier-Tasten Anzug!", 15, "BELIEBT"),
    REX("rex", "Rex (Dino)", "Wilder grüner Dino-Kuschelanzug mit Zähnen", 35),
    LAVA_LAMP("lava_lamp", "Lava Lamp", "Retro Lava-Lampe mit leuchtenden Blasen", 60),
    MECHA("mecha", "Mecha (Roboter)", "Futuristischer High-Tech Weltraum-Mech", 90),
    DUCKY("ducky", "Ducky (Ente)", "Epischer sonnengelber Quietsche-Enten Regenmantel", 130, "EPIC"),
    WIZARD("wizard", "Zauberer", "Magischer Sternenmantel für Sprach-Genies", 160, "MAGIE");

    companion object {
        fun fromId(id: String): SuitId {
            return entries.find { it.id == id } ?: NONE
        }
    }
}

enum class AvatarCategory(val title: String, val iconResName: String) {
    SKIN_TONE("Hautfarbe", "skin"),
    EXPRESSION("Ausdruck", "face"),
    HAIRSTYLE("Frisur", "hair"),
    GLASSES("Brille", "glasses"),
    FACIAL_HAIR("Bart", "beard"),
    HEADWEAR("Kopfbedeckung", "hat"),
    BACKGROUND("Hintergrund", "background")
}

enum class SceneryTheme(
    val title: String,
    val iconEmoji: String,
    val description: String
) {
    MINIMAL("Studio", "🎨", "Schlichter Studio-Hintergrund"),
    PARK("Sonnige Wiese", "🌻", "Sanfte grüne Hügel und Wolken"),
    SPACE("Sternenhimmel", "✨", "Kosmische Nacht mit leuchtenden Sternen"),
    LIBRARY("Bibliothek", "📚", "Warme Bücherregale zum Lesen"),
    CLASSROOM("Sprach-Klasse", "🎓", "Klassenzimmer mit Lerntafel"),
    CYBER("Neon Cyber", "⚡", "Futuristische Cyberpunk-Strahlen"),
    SUNSET("Sonnenuntergang", "🌅", "Goldener warmer Farbverlauf")
}

data class AvatarCustomization(
    val username: String = "LernPionier",
    val suitId: SuitId = SuitId.KEYS, // Default with the iconic Keys suit like user showed!
    val skinToneIndex: Int = 0,
    val expressionIndex: Int = 1, // Happy open mouth like screenshot!
    val hairStyleIndex: Int = 2,
    val hairColorIndex: Int = 1,
    val glassesIndex: Int = 0,
    val glassesColorIndex: Int = 1,
    val facialHairIndex: Int = 0,
    val facialHairColorIndex: Int = 1,
    val headwearIndex: Int = 0,
    val headwearColorIndex: Int = 0,
    val backgroundIndex: Int = 0, // Selectable background color
    val sceneryIndex: Int = 0, // Selectable scenery theme
    val unlockedSuits: Set<String> = setOf("none", "keys")
) {
    companion object {
        // Skin Tone Colors palette (matching screenshot #7)
        val SKIN_COLORS = listOf(
            Color(0xFFFFD1A4), // Light peach
            Color(0xFFF9C18F), // Peach
            Color(0xFFE8AB75), // Warm sand
            Color(0xFFD49257), // Tan
            Color(0xFFBA773E), // Golden brown
            Color(0xFF9E5C28), // Medium brown
            Color(0xFF814619), // Deep brown
            Color(0xFF5A2E0E), // Espresso
            Color(0xFFFFDFBF), // Ivory
            Color(0xFFECC5A8), // Soft beige
            Color(0xFFC78D63), // Chestnut
            Color(0xFF945731), // Mocha
            Color(0xFF6B3A19), // Dark chocolate
            Color(0xFFFFE6D0), // Fair
            Color(0xFFE2A176)  // Sun-kissed
        )

        val HAIR_COLORS = listOf(
            Color(0xFF4A3728), // Dark brown
            Color(0xFF2C221E), // Black/Dark
            Color(0xFF7B5233), // Milk chocolate
            Color(0xFFC48E56), // Caramel / Light brown
            Color(0xFFE8B960), // Blonde
            Color(0xFFB84B29), // Auburn / Redhead
            Color(0xFF8E3A59), // Magenta / Berry
            Color(0xFF3B5998), // Indigo / Blue
            Color(0xFF2E7D32), // Emerald
            Color(0xFF9E9E9E)  // Silver / White
        )

        val GLASSES_COLORS = listOf(
            Color(0xFF212121), // Slate black
            Color(0xFF1976D2), // Royal blue
            Color(0xFF7B1FA2), // Violet
            Color(0xFFD32F2F), // Crimson
            Color(0xFFE65100), // Tangerine
            Color(0xFF00897B), // Teal
            Color(0xFFF06292), // Pink
            Color(0xFF43A047)  // Green
        )

        val HEADWEAR_COLORS = listOf(
            Color(0xFF7E57C2), // Purple (like screenshot)
            Color(0xFF29B6F6), // Sky blue
            Color(0xFF66BB6A), // Fresh green
            Color(0xFFFFCA28), // Golden yellow
            Color(0xFFFFA726), // Orange
            Color(0xFFEF5350), // Cherry red
            Color(0xFF263238), // Charcoal
            Color(0xFFEC407A)  // Bubblegum pink
        )

        // 24 Background Colors matching Duolingo's palette in Screenshot 1!
        val BACKGROUND_COLORS = listOf(
            Color(0xFFFFF9DB), // 0: Buttercup Yellow (Iconic Duolingo stage!)
            Color(0xFFFFFFFF), // 1: Pure White
            Color(0xFFECEFF1), // 2: Soft Grey
            Color(0xFF37474F), // 3: Charcoal Dark
            Color(0xFFEDE7F6), // 4: Pale Lilac
            Color(0xFFD1C4E9), // 5: Soft Violet
            Color(0xFF9575CD), // 6: Deep Purple
            Color(0xFFB3E5FC), // 7: Ice Blue
            Color(0xFF4FC3F7), // 8: Sky Blue
            Color(0xFF1E88E5), // 9: Royal Blue
            Color(0xFFE0F2F1), // 10: Mint Glow
            Color(0xFF4DB6AC), // 11: Aqua Teal
            Color(0xFF00897B), // 12: Emerald Green
            Color(0xFFDCEDC8), // 13: Pale Lime
            Color(0xFF9CCC65), // 14: Fresh Meadow
            Color(0xFF7CB342), // 15: Apple Green
            Color(0xFFFFE082), // 16: Sunny Amber
            Color(0xFFFFB74D), // 17: Warm Tangerine
            Color(0xFFFF8A65), // 18: Sunset Coral
            Color(0xFFFFCDD2), // 19: Soft Peach
            Color(0xFFF48FB1), // 20: Bubblegum Pink
            Color(0xFFEC407A), // 21: Vibrant Magenta
            Color(0xFF80DEEA), // 22: Electric Cyan
            Color(0xFF5C6BC0)  // 23: Indigo Dusk
        )
    }

    val currentSkinColor: Color
        get() = SKIN_COLORS.getOrElse(skinToneIndex) { SKIN_COLORS[0] }

    val currentHairColor: Color
        get() = HAIR_COLORS.getOrElse(hairColorIndex) { HAIR_COLORS[0] }

    val currentGlassesColor: Color
        get() = GLASSES_COLORS.getOrElse(glassesColorIndex) { GLASSES_COLORS[0] }

    val currentHeadwearColor: Color
        get() = HEADWEAR_COLORS.getOrElse(headwearColorIndex) { HEADWEAR_COLORS[0] }

    val currentBackgroundColor: Color
        get() = BACKGROUND_COLORS.getOrElse(backgroundIndex) { BACKGROUND_COLORS[0] }

    val currentScenery: SceneryTheme
        get() = SceneryTheme.entries.getOrElse(sceneryIndex) { SceneryTheme.MINIMAL }
}
