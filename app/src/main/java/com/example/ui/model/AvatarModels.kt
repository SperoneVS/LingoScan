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
    HEADWEAR("Kopfbedeckung", "hat")
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
    val unlockedSuits: Set<String> = setOf("none", "keys")
) {
    // Skin Tone Colors palette (matching screenshot #7)
    companion object {
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
    }

    val currentSkinColor: Color
        get() = SKIN_COLORS.getOrElse(skinToneIndex) { SKIN_COLORS[0] }

    val currentHairColor: Color
        get() = HAIR_COLORS.getOrElse(hairColorIndex) { HAIR_COLORS[0] }

    val currentGlassesColor: Color
        get() = GLASSES_COLORS.getOrElse(glassesColorIndex) { GLASSES_COLORS[0] }

    val currentHeadwearColor: Color
        get() = HEADWEAR_COLORS.getOrElse(headwearColorIndex) { HEADWEAR_COLORS[0] }
}
