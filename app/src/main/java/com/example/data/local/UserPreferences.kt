package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.model.AvatarCustomization
import com.example.ui.model.SuitId

data class LanguageOption(
    val code: String,
    val name: String,
    val flagEmoji: String
)

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("lingoscan_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_NATIVE_LANG_CODE = "native_lang_code"
        private const val KEY_NATIVE_LANG_NAME = "native_lang_name"

        private const val KEY_USERNAME = "user_name"
        private const val KEY_SUIT_ID = "avatar_suit_id"
        private const val KEY_SKIN_TONE = "avatar_skin_tone"
        private const val KEY_EXPRESSION = "avatar_expression"
        private const val KEY_HAIR_STYLE = "avatar_hair_style"
        private const val KEY_HAIR_COLOR = "avatar_hair_color"
        private const val KEY_GLASSES = "avatar_glasses"
        private const val KEY_GLASSES_COLOR = "avatar_glasses_color"
        private const val KEY_FACIAL_HAIR = "avatar_facial_hair"
        private const val KEY_FACIAL_HAIR_COLOR = "avatar_facial_hair_color"
        private const val KEY_HEADWEAR = "avatar_headwear"
        private const val KEY_HEADWEAR_COLOR = "avatar_headwear_color"
        private const val KEY_BACKGROUND = "avatar_background"
        private const val KEY_SCENERY = "avatar_scenery"
        private const val KEY_UNLOCKED_SUITS = "avatar_unlocked_suits"

        val SUPPORTED_LANGUAGES = listOf(
            LanguageOption("de", "Deutsch", "🇩🇪"),
            LanguageOption("sk", "Slowakisch", "🇸🇰"),
            LanguageOption("en", "Englisch", "🇬🇧"),
            LanguageOption("es", "Spanisch", "🇪🇸"),
            LanguageOption("fr", "Französisch", "🇫🇷"),
            LanguageOption("it", "Italienisch", "🇮🇹"),
            LanguageOption("pt", "Portugiesisch", "🇵🇹"),
            LanguageOption("pl", "Polnisch", "🇵🇱"),
            LanguageOption("nl", "Niederländisch", "🇳🇱"),
            LanguageOption("ru", "Russisch", "🇷🇺"),
            LanguageOption("uk", "Ukrainisch", "🇺🇦"),
            LanguageOption("tr", "Türkisch", "🇹🇷"),
            LanguageOption("ar", "Arabisch", "🇸🇦"),
            LanguageOption("zh", "Chinesisch", "🇨🇳"),
            LanguageOption("ja", "Japanisch", "🇯🇵")
        )
    }

    var nativeLanguageCode: String
        get() = prefs.getString(KEY_NATIVE_LANG_CODE, "de") ?: "de"
        set(value) = prefs.edit().putString(KEY_NATIVE_LANG_CODE, value).apply()

    var nativeLanguageName: String
        get() = prefs.getString(KEY_NATIVE_LANG_NAME, "Deutsch") ?: "Deutsch"
        set(value) = prefs.edit().putString(KEY_NATIVE_LANG_NAME, value).apply()

    fun getSelectedLanguage(): LanguageOption {
        return SUPPORTED_LANGUAGES.find { it.code == nativeLanguageCode }
            ?: LanguageOption("de", "Deutsch", "🇩🇪")
    }

    fun setLanguage(option: LanguageOption) {
        nativeLanguageCode = option.code
        nativeLanguageName = option.name
    }

    // Avatar & Username Preferences
    var username: String
        get() = prefs.getString(KEY_USERNAME, "LernPionier") ?: "LernPionier"
        set(value) = prefs.edit().putString(KEY_USERNAME, value.ifBlank { "LernPionier" }).apply()

    fun getAvatarCustomization(): AvatarCustomization {
        val suitString = prefs.getString(KEY_SUIT_ID, SuitId.KEYS.id) ?: SuitId.KEYS.id
        val unlocked = prefs.getStringSet(KEY_UNLOCKED_SUITS, setOf("none", "keys")) ?: setOf("none", "keys")

        return AvatarCustomization(
            username = username,
            suitId = SuitId.fromId(suitString),
            skinToneIndex = prefs.getInt(KEY_SKIN_TONE, 0),
            expressionIndex = prefs.getInt(KEY_EXPRESSION, 1),
            hairStyleIndex = prefs.getInt(KEY_HAIR_STYLE, 2),
            hairColorIndex = prefs.getInt(KEY_HAIR_COLOR, 0),
            glassesIndex = prefs.getInt(KEY_GLASSES, 0),
            glassesColorIndex = prefs.getInt(KEY_GLASSES_COLOR, 1),
            facialHairIndex = prefs.getInt(KEY_FACIAL_HAIR, 0),
            facialHairColorIndex = prefs.getInt(KEY_FACIAL_HAIR_COLOR, 0),
            headwearIndex = prefs.getInt(KEY_HEADWEAR, 0),
            headwearColorIndex = prefs.getInt(KEY_HEADWEAR_COLOR, 0),
            backgroundIndex = prefs.getInt(KEY_BACKGROUND, 0),
            sceneryIndex = prefs.getInt(KEY_SCENERY, 0),
            unlockedSuits = unlocked
        )
    }

    fun saveAvatarCustomization(config: AvatarCustomization) {
        username = config.username
        prefs.edit()
            .putString(KEY_SUIT_ID, config.suitId.id)
            .putInt(KEY_SKIN_TONE, config.skinToneIndex)
            .putInt(KEY_EXPRESSION, config.expressionIndex)
            .putInt(KEY_HAIR_STYLE, config.hairStyleIndex)
            .putInt(KEY_HAIR_COLOR, config.hairColorIndex)
            .putInt(KEY_GLASSES, config.glassesIndex)
            .putInt(KEY_GLASSES_COLOR, config.glassesColorIndex)
            .putInt(KEY_FACIAL_HAIR, config.facialHairIndex)
            .putInt(KEY_FACIAL_HAIR_COLOR, config.facialHairColorIndex)
            .putInt(KEY_HEADWEAR, config.headwearIndex)
            .putInt(KEY_HEADWEAR_COLOR, config.headwearColorIndex)
            .putInt(KEY_BACKGROUND, config.backgroundIndex)
            .putInt(KEY_SCENERY, config.sceneryIndex)
            .putStringSet(KEY_UNLOCKED_SUITS, config.unlockedSuits)
            .apply()
    }
}

