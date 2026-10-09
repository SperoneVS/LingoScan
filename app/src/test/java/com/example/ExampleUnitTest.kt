package com.example

import com.example.data.local.UserPreferences
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun supportedLanguages_containsGermanAndSpanish() {
    val languages = UserPreferences.SUPPORTED_LANGUAGES
    assertTrue(languages.any { it.code == "de" && it.name == "Deutsch" })
    assertTrue(languages.any { it.code == "sk" && it.name == "Slowakisch" })
    assertTrue(languages.any { it.code == "es" && it.name == "Spanisch" })
    assertTrue(languages.any { it.code == "fr" && it.name == "Französisch" })
  }

  @Test
  fun wordSanitizer_cleansPunctuationProperly() {
    val raw = "„serpenteaba,“"
    val clean = raw.replace(Regex("[^\\p{L}\\p{Nd}-]"), "").lowercase()
    assertEquals("serpenteaba", clean)
  }

  @Test
  fun fuzzyAnswerMatching_validatesCorrectly() {
    val target = "gebirgszug / bergkette"
    val typed = "gebirgszug"
    val cleanTarget = target.replace(Regex("[/;,]"), " ")
    val isCorrect = typed == target ||
        cleanTarget.split(" ").any { it.length > 2 && typed.contains(it) } ||
        target.split("/").any { it.trim() == typed }
    assertTrue(isCorrect)
  }

  @Test
  fun avatarSuits_hasExpectedSuitsAndCosts() {
    val keysSuit = com.example.ui.model.SuitId.KEYS
    val rexSuit = com.example.ui.model.SuitId.REX
    val duckySuit = com.example.ui.model.SuitId.DUCKY

    assertEquals("keys", keysSuit.id)
    assertEquals(15, keysSuit.starCost)
    assertEquals("BELIEBT", keysSuit.badge)

    assertEquals("rex", rexSuit.id)
    assertEquals(35, rexSuit.starCost)

    assertEquals("ducky", duckySuit.id)
    assertEquals("EPIC", duckySuit.badge)

    val defaultAvatar = com.example.ui.model.AvatarCustomization()
    assertEquals("LernPionier", defaultAvatar.username)
    assertTrue(defaultAvatar.unlockedSuits.contains("keys"))
  }
}


