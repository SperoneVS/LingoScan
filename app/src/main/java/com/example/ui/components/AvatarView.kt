package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.model.AvatarCustomization
import com.example.ui.model.SuitId

@Composable
fun AvatarView(
    customization: AvatarCustomization,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    showBackground: Boolean = false
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = this.size.width
            val canvasH = this.size.height

            // Scale factor relative to 200x240 reference coordinate system
            val scaleX = canvasW / 200f
            val scaleY = canvasH / 240f

            fun x(valX: Float): Float = valX * scaleX
            fun y(valY: Float): Float = valY * scaleY
            fun w(valW: Float): Float = valW * scaleX
            fun h(valH: Float): Float = valH * scaleY

            // 0. Optional Background & Scenery
            if (showBackground) {
                drawBackgroundScenery(customization, ::x, ::y, ::w, ::h)
            }

            // 1. Draw Suit Body / Legs / Base
            drawSuitBody(customization.suitId, ::x, ::y, ::w, ::h)

            // 2. Draw Face Circle (Skin tone)
            val faceCenterX = x(100f)
            val faceCenterY = y(95f)
            val faceRadius = w(38f)

            // Draw Head / Face base
            drawCircle(
                color = customization.currentSkinColor,
                radius = faceRadius,
                center = Offset(faceCenterX, faceCenterY)
            )

            // 3. Draw Hair (Back/Base layer)
            drawHairBack(customization, faceCenterX, faceCenterY, faceRadius, ::w, ::h)

            // 4. Draw Facial Features (Eyes, Eyebrows, Nose, Mouth)
            drawFaceFeatures(customization, faceCenterX, faceCenterY, ::x, ::y, ::w, ::h)

            // 5. Draw Facial Hair
            drawFacialHair(customization, faceCenterX, faceCenterY, ::w, ::h)

            // 6. Draw Hair (Front bangs/curls)
            drawHairFront(customization, faceCenterX, faceCenterY, faceRadius, ::w, ::h)

            // 7. Draw Glasses
            drawGlasses(customization, faceCenterX, faceCenterY, ::x, ::y, ::w, ::h)

            // 8. Draw Headwear
            drawHeadwear(customization, faceCenterX, faceCenterY, ::w, ::h)

            // 9. Draw Suit Overlays (e.g. Piano Keys black notes in front of face, Dino teeth hood, Ducky beak hood)
            drawSuitForeground(customization.suitId, ::x, ::y, ::w, ::h)
        }
    }
}

// -------------------------------------------------------------
// SUIT BACKGROUND / BODY RENDERING
// -------------------------------------------------------------
private fun DrawScope.drawSuitBody(
    suitId: SuitId,
    x: (Float) -> Float,
    y: (Float) -> Float,
    w: (Float) -> Float,
    h: (Float) -> Float
) {
    when (suitId) {
        SuitId.KEYS -> {
            // Shadow under feet
            drawOval(
                color = Color(0x33000000),
                topLeft = Offset(x(65f), y(222f)),
                size = Size(w(70f), h(14f))
            )
            // Little feet/shoes
            drawRoundRect(
                color = Color(0xFF4A4A5A),
                topLeft = Offset(x(78f), y(214f)),
                size = Size(w(18f), h(12f)),
                cornerRadius = CornerRadius(w(6f), h(6f))
            )
            drawRoundRect(
                color = Color(0xFF4A4A5A),
                topLeft = Offset(x(104f), y(214f)),
                size = Size(w(18f), h(12f)),
                cornerRadius = CornerRadius(w(6f), h(6f))
            )
            // Little hands on side
            drawCircle(
                color = Color(0xFFFFD1A4),
                radius = w(9f),
                center = Offset(x(48f), y(150f))
            )
            drawCircle(
                color = Color(0xFFFFD1A4),
                radius = w(9f),
                center = Offset(x(152f), y(150f))
            )

            // Piano Keys Suit Body (Tall white rounded column with grey frame)
            val bodyLeft = x(52f)
            val bodyTop = y(35f)
            val bodyWidth = w(96f)
            val bodyHeight = h(182f)

            // Outer grey shell
            drawRoundRect(
                color = Color(0xFFD6D9E0),
                topLeft = Offset(bodyLeft, bodyTop),
                size = Size(bodyWidth, bodyHeight),
                cornerRadius = CornerRadius(w(24f), h(24f))
            )
            // Inner white keys board
            drawRoundRect(
                color = Color(0xFFFFFFFF),
                topLeft = Offset(bodyLeft + w(4f), bodyTop + h(4f)),
                size = Size(bodyWidth - w(8f), bodyHeight - h(8f)),
                cornerRadius = CornerRadius(w(20f), h(20f))
            )

            // Piano Keys horizontal partition dividers
            val dividerColor = Color(0xFFE2E4EB)
            for (step in 1..4) {
                val lineY = bodyTop + h(step * 35f)
                drawRoundRect(
                    color = dividerColor,
                    topLeft = Offset(bodyLeft + w(6f), lineY),
                    size = Size(bodyWidth - w(12f), h(4f)),
                    cornerRadius = CornerRadius(w(2f), h(2f))
                )
            }
        }

        SuitId.REX -> {
            // Dino green body
            drawOval(
                color = Color(0x33000000),
                topLeft = Offset(x(60f), y(222f)),
                size = Size(w(80f), h(14f))
            )
            // Dino feet with claws
            drawRoundRect(
                color = Color(0xFF4CAF50),
                topLeft = Offset(x(74f), y(214f)),
                size = Size(w(22f), h(14f)),
                cornerRadius = CornerRadius(w(6f), h(6f))
            )
            drawRoundRect(
                color = Color(0xFF4CAF50),
                topLeft = Offset(x(104f), y(214f)),
                size = Size(w(22f), h(14f)),
                cornerRadius = CornerRadius(w(6f), h(6f))
            )
            // Dino green torso
            drawRoundRect(
                color = Color(0xFF4CAF50),
                topLeft = Offset(x(50f), y(55f)),
                size = Size(w(100f), h(160f)),
                cornerRadius = CornerRadius(w(40f), h(40f))
            )
            // Light green belly
            drawOval(
                color = Color(0xFF81C784),
                topLeft = Offset(x(68f), y(135f)),
                size = Size(w(64f), h(70f))
            )
            // Dino little cute arms
            drawCircle(
                color = Color(0xFF388E3C),
                radius = w(12f),
                center = Offset(x(50f), y(150f))
            )
            drawCircle(
                color = Color(0xFF388E3C),
                radius = w(12f),
                center = Offset(x(150f), y(150f))
            )
        }

        SuitId.DUCKY -> {
            // Duck yellow shadow and orange feet
            drawOval(
                color = Color(0x33000000),
                topLeft = Offset(x(60f), y(222f)),
                size = Size(w(80f), h(14f))
            )
            // Webbed orange feet
            drawRoundRect(
                color = Color(0xFFFF9800),
                topLeft = Offset(x(72f), y(214f)),
                size = Size(w(24f), h(14f)),
                cornerRadius = CornerRadius(w(6f), h(6f))
            )
            drawRoundRect(
                color = Color(0xFFFF9800),
                topLeft = Offset(x(104f), y(214f)),
                size = Size(w(24f), h(14f)),
                cornerRadius = CornerRadius(w(6f), h(6f))
            )
            // Sunny yellow raincoat suit body
            drawRoundRect(
                color = Color(0xFFFFEB3B),
                topLeft = Offset(x(50f), y(50f)),
                size = Size(w(100f), h(165f)),
                cornerRadius = CornerRadius(w(44f), h(44f))
            )
            // Cute duck wings
            drawOval(
                color = Color(0xFFFDD835),
                topLeft = Offset(x(38f), y(135f)),
                size = Size(w(22f), h(42f))
            )
            drawOval(
                color = Color(0xFFFDD835),
                topLeft = Offset(x(140f), y(135f)),
                size = Size(w(22f), h(42f))
            )
        }

        SuitId.LAVA_LAMP -> {
            // Metal base and glass body
            drawRoundRect(
                color = Color(0xFF78909C),
                topLeft = Offset(x(65f), y(205f)),
                size = Size(w(70f), h(22f)),
                cornerRadius = CornerRadius(w(8f), h(8f))
            )
            // Glass cone body with glowing indigo/cyan liquid
            drawRoundRect(
                color = Color(0xFF26C6DA),
                topLeft = Offset(x(52f), y(55f)),
                size = Size(w(96f), h(152f)),
                cornerRadius = CornerRadius(w(36f), h(36f))
            )
            // Floating lava bubbles
            drawCircle(Color(0xFF76FF03), radius = w(14f), center = Offset(x(80f), y(165f)))
            drawCircle(Color(0xFF76FF03), radius = w(10f), center = Offset(x(120f), y(175f)))
            drawCircle(Color(0xFF69F0AE), radius = w(8f), center = Offset(x(98f), y(145f)))
        }

        SuitId.MECHA -> {
            // Metal legs and boots
            drawRoundRect(
                color = Color(0xFFB0BEC5),
                topLeft = Offset(x(74f), y(210f)),
                size = Size(w(20f), h(18f)),
                cornerRadius = CornerRadius(w(6f), h(6f))
            )
            drawRoundRect(
                color = Color(0xFFB0BEC5),
                topLeft = Offset(x(106f), y(210f)),
                size = Size(w(20f), h(18f)),
                cornerRadius = CornerRadius(w(6f), h(6f))
            )
            // High-tech Mecha robot body
            drawRoundRect(
                color = Color(0xFFECEFF1),
                topLeft = Offset(x(52f), y(50f)),
                size = Size(w(96f), h(162f)),
                cornerRadius = CornerRadius(w(30f), h(30f))
            )
            // Chest control panel with neon green indicator
            drawRoundRect(
                color = Color(0xFF37474F),
                topLeft = Offset(x(76f), y(155f)),
                size = Size(w(48f), h(30f)),
                cornerRadius = CornerRadius(w(8f), h(8f))
            )
            drawCircle(Color(0xFF00E676), radius = w(5f), center = Offset(x(88f), y(170f)))
            drawCircle(Color(0xFF29B6F6), radius = w(5f), center = Offset(x(100f), y(170f)))
            drawCircle(Color(0xFFFFEA00), radius = w(5f), center = Offset(x(112f), y(170f)))
        }

        SuitId.WIZARD -> {
            // Wizard starry deep violet robe
            drawRoundRect(
                color = Color(0xFF5E35B1),
                topLeft = Offset(x(48f), y(65f)),
                size = Size(w(104f), h(155f)),
                cornerRadius = CornerRadius(w(24f), h(24f))
            )
            // Golden magical trim
            drawRoundRect(
                color = Color(0xFFFFD54F),
                topLeft = Offset(x(96f), y(130f)),
                size = Size(w(8f), h(90f)),
                cornerRadius = CornerRadius(w(4f), h(4f))
            )
            // Golden stars on robe
            drawCircle(Color(0xFFFFEE58), radius = w(5f), center = Offset(x(70f), y(160f)))
            drawCircle(Color(0xFFFFEE58), radius = w(6f), center = Offset(x(130f), y(175f)))
            drawCircle(Color(0xFFFFEE58), radius = w(4f), center = Offset(x(80f), y(195f)))
        }

        SuitId.NONE -> {
            // Classic comfortable green sweater / t-shirt
            drawOval(
                color = Color(0x33000000),
                topLeft = Offset(x(65f), y(222f)),
                size = Size(w(70f), h(14f))
            )
            // Shoes
            drawRoundRect(
                color = Color(0xFF37474F),
                topLeft = Offset(x(78f), y(214f)),
                size = Size(w(18f), h(12f)),
                cornerRadius = CornerRadius(w(6f), h(6f))
            )
            drawRoundRect(
                color = Color(0xFF37474F),
                topLeft = Offset(x(104f), y(214f)),
                size = Size(w(18f), h(12f)),
                cornerRadius = CornerRadius(w(6f), h(6f))
            )
            // Blue jeans
            drawRoundRect(
                color = Color(0xFF1976D2),
                topLeft = Offset(x(74f), y(180f)),
                size = Size(w(52f), h(38f)),
                cornerRadius = CornerRadius(w(8f), h(8f))
            )
            // Cheerful green hoodie/sweater
            drawRoundRect(
                color = Color(0xFF58CC02), // Duolingo green!
                topLeft = Offset(x(56f), y(128f)),
                size = Size(w(88f), h(62f)),
                cornerRadius = CornerRadius(w(20f), h(20f))
            )
            // Hands
            drawCircle(
                color = Color(0xFFFFD1A4),
                radius = w(9f),
                center = Offset(x(52f), y(168f))
            )
            drawCircle(
                color = Color(0xFFFFD1A4),
                radius = w(9f),
                center = Offset(x(148f), y(168f))
            )
        }
    }
}

// -------------------------------------------------------------
// SUIT FOREGROUND RENDERING (Keys, Dino Hood, Duck Beak, Robot Antenna)
// -------------------------------------------------------------
private fun DrawScope.drawSuitForeground(
    suitId: SuitId,
    x: (Float) -> Float,
    y: (Float) -> Float,
    w: (Float) -> Float,
    h: (Float) -> Float
) {
    when (suitId) {
        SuitId.KEYS -> {
            // Exactly matching user's screenshots!
            // 3 black piano keys on right side with 3D depth and shadow
            // Key 1 (upper right, near head)
            drawRoundRect(
                color = Color(0xFF2C2F38),
                topLeft = Offset(x(108f), y(56f)),
                size = Size(w(36f), h(16f)),
                cornerRadius = CornerRadius(w(5f), h(5f))
            )
            // Key 2 (middle right, alongside cheek)
            drawRoundRect(
                color = Color(0xFF2C2F38),
                topLeft = Offset(x(114f), y(78f)),
                size = Size(w(34f), h(16f)),
                cornerRadius = CornerRadius(w(5f), h(5f))
            )
            // Key 3 (lower right, beneath body partition)
            drawRoundRect(
                color = Color(0xFF2C2F38),
                topLeft = Offset(x(108f), y(165f)),
                size = Size(w(36f), h(16f)),
                cornerRadius = CornerRadius(w(5f), h(5f))
            )
        }

        SuitId.REX -> {
            // Dino hood with teeth around face opening
            val hoodColor = Color(0xFF388E3C)
            drawArc(
                color = hoodColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(x(56f), y(50f)),
                size = Size(w(88f), h(60f)),
                style = Stroke(width = w(8f))
            )
            // White dino teeth points
            val teethColor = Color.White
            for (i in 0..4) {
                val tX = x(74f + i * 13f)
                val path = Path().apply {
                    moveTo(tX, y(62f))
                    lineTo(tX + w(5f), y(70f))
                    lineTo(tX + w(10f), y(62f))
                    close()
                }
                drawPath(path, color = teethColor)
            }
            // Dino hood eyes
            drawCircle(Color.White, radius = w(7f), center = Offset(x(80f), y(45f)))
            drawCircle(Color.Black, radius = w(3f), center = Offset(x(81f), y(45f)))
            drawCircle(Color.White, radius = w(7f), center = Offset(x(120f), y(45f)))
            drawCircle(Color.Black, radius = w(3f), center = Offset(x(119f), y(45f)))
        }

        SuitId.DUCKY -> {
            // Cute bright orange duck beak right above the forehead
            val beakPath = Path().apply {
                moveTo(x(78f), y(64f))
                quadraticTo(x(100f), y(54f), x(122f), y(64f))
                quadraticTo(x(100f), y(76f), x(78f), y(64f))
                close()
            }
            drawPath(beakPath, color = Color(0xFFFF9800))
            // Duck nostrils
            drawCircle(Color(0xFFE65100), radius = w(1.5f), center = Offset(x(96f), y(64f)))
            drawCircle(Color(0xFFE65100), radius = w(1.5f), center = Offset(x(104f), y(64f)))
            // Duck hood eyes
            drawCircle(Color(0xFF212121), radius = w(3.5f), center = Offset(x(75f), y(48f)))
            drawCircle(Color(0xFF212121), radius = w(3.5f), center = Offset(x(125f), y(48f)))
        }

        SuitId.MECHA -> {
            // Antenna on top of helmet
            drawRoundRect(
                color = Color(0xFF78909C),
                topLeft = Offset(x(97f), y(24f)),
                size = Size(w(6f), h(26f)),
                cornerRadius = CornerRadius(w(3f), h(3f))
            )
            drawCircle(Color(0xFF00E676), radius = w(6f), center = Offset(x(100f), y(22f)))
            // Side helmet ear bolts
            drawRoundRect(
                color = Color(0xFF90A4AE),
                topLeft = Offset(x(48f), y(86f)),
                size = Size(w(10f), h(20f)),
                cornerRadius = CornerRadius(w(4f), h(4f))
            )
            drawRoundRect(
                color = Color(0xFF90A4AE),
                topLeft = Offset(x(142f), y(86f)),
                size = Size(w(10f), h(20f)),
                cornerRadius = CornerRadius(w(4f), h(4f))
            )
        }

        SuitId.WIZARD -> {
            // Wand in hand with star sparkles
            drawRoundRect(
                color = Color(0xFF8D6E63),
                topLeft = Offset(x(145f), y(130f)),
                size = Size(w(5f), h(45f)),
                cornerRadius = CornerRadius(w(2f), h(2f))
            )
            drawCircle(Color(0xFFFFEA00), radius = w(5f), center = Offset(x(147f), y(128f)))
        }

        SuitId.LAVA_LAMP -> {
            // Lamp metal top cap
            drawRoundRect(
                color = Color(0xFF546E7A),
                topLeft = Offset(x(78f), y(38f)),
                size = Size(w(44f), h(16f)),
                cornerRadius = CornerRadius(w(6f), h(6f))
            )
        }

        SuitId.NONE -> {}
    }
}

// -------------------------------------------------------------
// HAIR BACK LAYER
// -------------------------------------------------------------
private fun DrawScope.drawHairBack(
    customization: AvatarCustomization,
    centerX: Float,
    centerY: Float,
    radius: Float,
    w: (Float) -> Float,
    h: (Float) -> Float
) {
    val hairColor = customization.currentHairColor
    when (customization.hairStyleIndex) {
        3 -> { // Curly Afro
            drawCircle(hairColor, radius = radius + w(10f), center = Offset(centerX, centerY - h(4f)))
        }
        6 -> { // Long Hair
            drawRoundRect(
                color = hairColor,
                topLeft = Offset(centerX - radius - w(6f), centerY - h(10f)),
                size = Size((radius * 2) + w(12f), h(70f)),
                cornerRadius = CornerRadius(w(24f), h(24f))
            )
        }
        7 -> { // Pigtails
            drawCircle(hairColor, radius = w(16f), center = Offset(centerX - radius - w(4f), centerY + h(8f)))
            drawCircle(hairColor, radius = w(16f), center = Offset(centerX + radius + w(4f), centerY + h(8f)))
        }
    }
}

// -------------------------------------------------------------
// FACE FEATURES (Eyes, Nose, Mouth)
// -------------------------------------------------------------
private fun DrawScope.drawFaceFeatures(
    customization: AvatarCustomization,
    centerX: Float,
    centerY: Float,
    x: (Float) -> Float,
    y: (Float) -> Float,
    w: (Float) -> Float,
    h: (Float) -> Float
) {
    // Eyebrows
    val browColor = customization.currentHairColor
    val browY = centerY - h(18f)
    drawRoundRect(
        color = browColor,
        topLeft = Offset(centerX - w(24f), browY),
        size = Size(w(14f), h(4f)),
        cornerRadius = CornerRadius(w(2f), h(2f))
    )
    drawRoundRect(
        color = browColor,
        topLeft = Offset(centerX + w(10f), browY),
        size = Size(w(14f), h(4f)),
        cornerRadius = CornerRadius(w(2f), h(2f))
    )

    // Eyes (Big rounded Duolingo style!)
    val eyeY = centerY - h(6f)
    val eyeRadiusX = w(10f)
    val eyeRadiusY = h(14f)

    when (customization.expressionIndex) {
        2 -> { // Wink
            // Left eye open
            drawEye(centerX - w(16f), eyeY, eyeRadiusX, eyeRadiusY, w, h)
            // Right eye wink arc
            drawArc(
                color = Color(0xFF2C2F38),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(centerX + w(8f), eyeY - h(2f)),
                size = Size(w(16f), h(10f)),
                style = Stroke(width = w(3f))
            )
        }
        4 -> { // Super Curious / Excited
            drawEye(centerX - w(16f), eyeY, eyeRadiusX + w(1f), eyeRadiusY + h(1f), w, h)
            drawEye(centerX + w(16f), eyeY, eyeRadiusX + w(1f), eyeRadiusY + h(1f), w, h)
        }
        else -> {
            drawEye(centerX - w(16f), eyeY, eyeRadiusX, eyeRadiusY, w, h)
            drawEye(centerX + w(16f), eyeY, eyeRadiusX, eyeRadiusY, w, h)
        }
    }

    // Cute Triangle / Button Nose
    val noseColor = customization.currentSkinColor.copy(
        red = (customization.currentSkinColor.red * 0.88f).coerceIn(0f, 1f),
        green = (customization.currentSkinColor.green * 0.75f).coerceIn(0f, 1f),
        blue = (customization.currentSkinColor.blue * 0.75f).coerceIn(0f, 1f)
    )
    val nosePath = Path().apply {
        moveTo(centerX, centerY + h(3f))
        lineTo(centerX - w(4f), centerY + h(10f))
        lineTo(centerX + w(4f), centerY + h(10f))
        close()
    }
    drawPath(nosePath, color = noseColor)

    // Mouth
    val mouthY = centerY + h(16f)
    when (customization.expressionIndex) {
        0 -> { // Simple cheerful smile
            drawArc(
                color = Color(0xFF9E2A2B),
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(centerX - w(12f), mouthY - h(2f)),
                size = Size(w(24f), h(12f)),
                style = Stroke(width = w(3.5f))
            )
        }
        else -> {
            // Open cheerful mouth with white tooth peeking out like Duolingo screenshot!
            val mouthW = w(24f)
            val mouthH = h(16f)
            val mouthLeft = centerX - w(12f)

            // Dark red mouth cavity
            val mouthPath = Path().apply {
                moveTo(mouthLeft, mouthY)
                quadraticTo(centerX, mouthY - h(3f), mouthLeft + mouthW, mouthY)
                quadraticTo(centerX, mouthY + mouthH, mouthLeft, mouthY)
                close()
            }
            drawPath(mouthPath, color = Color(0xFF9E2A2B))

            // White cute front tooth in the upper left of mouth!
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(centerX - w(5f), mouthY - h(1f)),
                size = Size(w(6f), h(5f)),
                cornerRadius = CornerRadius(w(1f), h(1f))
            )
        }
    }
}

private fun DrawScope.drawEye(
    eyeX: Float,
    eyeY: Float,
    radiusX: Float,
    radiusY: Float,
    w: (Float) -> Float,
    h: (Float) -> Float
) {
    // Eye white
    drawOval(
        color = Color.White,
        topLeft = Offset(eyeX - radiusX, eyeY - radiusY),
        size = Size(radiusX * 2, radiusY * 2)
    )
    // Pupil (Golden brown/amber Duolingo style!)
    val pupilX = eyeX + w(1f)
    val pupilY = eyeY
    drawOval(
        color = Color(0xFF8D6E18),
        topLeft = Offset(pupilX - w(5f), pupilY - h(7f)),
        size = Size(w(10f), h(14f))
    )
    // Dark core
    drawOval(
        color = Color(0xFF2C2F38),
        topLeft = Offset(pupilX - w(3.5f), pupilY - h(5f)),
        size = Size(w(7f), h(10f))
    )
    // White shine highlight
    drawCircle(
        color = Color.White,
        radius = w(2.5f),
        center = Offset(pupilX - w(1.5f), pupilY - h(2.5f))
    )
}

// -------------------------------------------------------------
// FACIAL HAIR
// -------------------------------------------------------------
private fun DrawScope.drawFacialHair(
    customization: AvatarCustomization,
    centerX: Float,
    centerY: Float,
    w: (Float) -> Float,
    h: (Float) -> Float
) {
    if (customization.facialHairIndex <= 0) return
    val beardColor = customization.currentHairColor

    when (customization.facialHairIndex) {
        1 -> { // Neat Mustache
            val mY = centerY + h(13f)
            drawRoundRect(
                color = beardColor,
                topLeft = Offset(centerX - w(16f), mY),
                size = Size(w(32f), h(7f)),
                cornerRadius = CornerRadius(w(4f), h(4f))
            )
        }
        2 -> { // Goatee
            drawRoundRect(
                color = beardColor,
                topLeft = Offset(centerX - w(8f), centerY + h(26f)),
                size = Size(w(16f), h(10f)),
                cornerRadius = CornerRadius(w(4f), h(4f))
            )
        }
        3 -> { // Full Beard
            val beardPath = Path().apply {
                moveTo(centerX - w(28f), centerY + h(10f))
                quadraticTo(centerX - w(30f), centerY + h(36f), centerX, centerY + h(40f))
                quadraticTo(centerX + w(30f), centerY + h(36f), centerX + w(28f), centerY + h(10f))
                quadraticTo(centerX, centerY + h(26f), centerX - w(28f), centerY + h(10f))
                close()
            }
            drawPath(beardPath, color = beardColor)
        }
    }
}

// -------------------------------------------------------------
// HAIR FRONT LAYER
// -------------------------------------------------------------
private fun DrawScope.drawHairFront(
    customization: AvatarCustomization,
    centerX: Float,
    centerY: Float,
    radius: Float,
    w: (Float) -> Float,
    h: (Float) -> Float
) {
    val hairColor = customization.currentHairColor
    val topY = centerY - radius

    when (customization.hairStyleIndex) {
        0 -> { /* Bald/None */ }
        1 -> { // Buzzcut
            drawArc(
                color = hairColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(centerX - radius - w(1f), topY - h(4f)),
                size = Size((radius * 2) + w(2f), h(36f))
            )
        }
        2 -> { // Classic side-part / bangs (matching default)
            val hairPath = Path().apply {
                moveTo(centerX - radius, centerY - h(10f))
                quadraticTo(centerX - radius, topY - h(10f), centerX, topY - h(10f))
                quadraticTo(centerX + radius, topY - h(10f), centerX + radius, centerY - h(10f))
                quadraticTo(centerX + w(10f), topY + h(12f), centerX, topY + h(6f))
                quadraticTo(centerX - w(16f), topY + h(14f), centerX - radius, centerY - h(10f))
                close()
            }
            drawPath(hairPath, color = hairColor)
        }
        4 -> { // Pompadour / Wavy volume
            drawRoundRect(
                color = hairColor,
                topLeft = Offset(centerX - radius + w(4f), topY - h(18f)),
                size = Size((radius * 2) - w(8f), h(34f)),
                cornerRadius = CornerRadius(w(14f), h(14f))
            )
        }
        5 -> { // Single cute curl strand (like in screenshot #5!)
            val curlPath = Path().apply {
                moveTo(centerX, topY + h(2f))
                cubicTo(centerX - w(12f), topY - h(14f), centerX + w(14f), topY - h(24f), centerX - w(2f), topY - h(30f))
            }
            drawPath(
                curlPath,
                color = hairColor,
                style = Stroke(width = w(4.5f))
            )
        }
    }
}

// -------------------------------------------------------------
// GLASSES
// -------------------------------------------------------------
private fun DrawScope.drawGlasses(
    customization: AvatarCustomization,
    centerX: Float,
    centerY: Float,
    x: (Float) -> Float,
    y: (Float) -> Float,
    w: (Float) -> Float,
    h: (Float) -> Float
) {
    if (customization.glassesIndex <= 0) return
    val glassesColor = customization.currentGlassesColor
    val gY = centerY - h(8f)

    when (customization.glassesIndex) {
        1 -> { // Modern Round Glasses
            val radius = w(12f)
            // Left lens
            drawCircle(glassesColor, radius = radius, center = Offset(centerX - w(16f), gY), style = Stroke(width = w(3f)))
            // Right lens
            drawCircle(glassesColor, radius = radius, center = Offset(centerX + w(16f), gY), style = Stroke(width = w(3f)))
            // Bridge
            drawLine(glassesColor, start = Offset(centerX - w(4f), gY), end = Offset(centerX + w(4f), gY), strokeWidth = w(3f))
        }
        2 -> { // Square Geek Frames
            // Left square
            drawRoundRect(
                color = glassesColor,
                topLeft = Offset(centerX - w(28f), gY - h(11f)),
                size = Size(w(22f), h(22f)),
                cornerRadius = CornerRadius(w(4f), h(4f)),
                style = Stroke(width = w(3.5f))
            )
            // Right square
            drawRoundRect(
                color = glassesColor,
                topLeft = Offset(centerX + w(6f), gY - h(11f)),
                size = Size(w(22f), h(22f)),
                cornerRadius = CornerRadius(w(4f), h(4f)),
                style = Stroke(width = w(3.5f))
            )
            // Bridge
            drawLine(glassesColor, start = Offset(centerX - w(6f), gY), end = Offset(centerX + w(6f), gY), strokeWidth = w(3.5f))
        }
        3 -> { // Cool Sunglasses
            drawRoundRect(
                color = glassesColor,
                topLeft = Offset(centerX - w(28f), gY - h(11f)),
                size = Size(w(22f), h(22f)),
                cornerRadius = CornerRadius(w(4f), h(4f))
            )
            drawRoundRect(
                color = glassesColor,
                topLeft = Offset(centerX + w(6f), gY - h(11f)),
                size = Size(w(22f), h(22f)),
                cornerRadius = CornerRadius(w(4f), h(4f))
            )
            drawLine(glassesColor, start = Offset(centerX - w(6f), gY), end = Offset(centerX + w(6f), gY), strokeWidth = w(3.5f))
        }
    }
}

// -------------------------------------------------------------
// HEADWEAR (Cap, Beanie, Chef Hat, Crown, Party Hat)
// -------------------------------------------------------------
private fun DrawScope.drawHeadwear(
    customization: AvatarCustomization,
    centerX: Float,
    centerY: Float,
    w: (Float) -> Float,
    h: (Float) -> Float
) {
    if (customization.headwearIndex <= 0) return
    val hatColor = customization.currentHeadwearColor
    val topY = centerY - h(38f)

    when (customization.headwearIndex) {
        1 -> { // Baseball cap with 'L' monogram (like screenshot 2/9!)
            // Cap dome
            drawArc(
                color = hatColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(centerX - w(36f), topY - h(20f)),
                size = Size(w(72f), h(46f))
            )
            // Visor brim
            drawRoundRect(
                color = hatColor.copy(red = (hatColor.red * 0.85f).coerceIn(0f, 1f)),
                topLeft = Offset(centerX - w(38f), topY + h(2f)),
                size = Size(w(76f), h(10f)),
                cornerRadius = CornerRadius(w(4f), h(4f))
            )
            // White button on top
            drawCircle(Color.White, radius = w(3.5f), center = Offset(centerX, topY - h(18f)))
        }
        2 -> { // Warm Beanie
            drawRoundRect(
                color = hatColor,
                topLeft = Offset(centerX - w(35f), topY - h(18f)),
                size = Size(w(70f), h(38f)),
                cornerRadius = CornerRadius(w(22f), h(22f))
            )
            // Pompom on top
            drawCircle(hatColor, radius = w(8f), center = Offset(centerX, topY - h(22f)))
        }
        3 -> { // Golden Crown
            val crownPath = Path().apply {
                moveTo(centerX - w(28f), topY + h(6f))
                lineTo(centerX - w(30f), topY - h(18f))
                lineTo(centerX - w(14f), topY - h(8f))
                lineTo(centerX, topY - h(22f))
                lineTo(centerX + w(14f), topY - h(8f))
                lineTo(centerX + w(30f), topY - h(18f))
                lineTo(centerX + w(28f), topY + h(6f))
                close()
            }
            drawPath(crownPath, color = Color(0xFFFFD54F))
        }
        4 -> { // Chef Hat
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(centerX - w(30f), topY - h(28f)),
                size = Size(w(60f), h(38f)),
                cornerRadius = CornerRadius(w(16f), h(16f))
            )
            drawRoundRect(
                color = Color(0xFFEEEEEE),
                topLeft = Offset(centerX - w(26f), topY + h(2f)),
                size = Size(w(52f), h(10f)),
                cornerRadius = CornerRadius(w(2f), h(2f))
            )
        }
    }
}

// -------------------------------------------------------------
// BACKGROUND SCENERY RENDERING
// -------------------------------------------------------------
private fun DrawScope.drawBackgroundScenery(
    customization: AvatarCustomization,
    x: (Float) -> Float,
    y: (Float) -> Float,
    w: (Float) -> Float,
    h: (Float) -> Float
) {
    val bgColor = customization.currentBackgroundColor
    drawRoundRect(
        color = bgColor,
        topLeft = Offset(0f, 0f),
        size = Size(this.size.width, this.size.height),
        cornerRadius = CornerRadius(w(24f), h(24f))
    )

    when (customization.currentScenery) {
        com.example.ui.model.SceneryTheme.PARK -> {
            drawCircle(Color(0xFFFFD54F), radius = w(18f), center = Offset(x(170f), y(35f)))
            drawCircle(Color(0xCCFFFFFF), radius = w(14f), center = Offset(x(45f), y(40f)))
            drawCircle(Color(0xCCFFFFFF), radius = w(11f), center = Offset(x(32f), y(44f)))
            drawCircle(Color(0xCCFFFFFF), radius = w(12f), center = Offset(x(58f), y(44f)))
            val hillPath = Path().apply {
                moveTo(x(0f), y(190f))
                quadraticTo(x(60f), y(170f), x(130f), y(185f))
                quadraticTo(x(170f), y(175f), x(200f), y(190f))
                lineTo(x(200f), y(240f))
                lineTo(x(0f), y(240f))
                close()
            }
            drawPath(hillPath, color = Color(0xFF81C784))
        }

        com.example.ui.model.SceneryTheme.SPACE -> {
            val starColor = Color(0xFFFFEE58)
            drawCircle(starColor, radius = w(2.5f), center = Offset(x(30f), y(30f)))
            drawCircle(Color.White, radius = w(2f), center = Offset(x(70f), y(20f)))
            drawCircle(starColor, radius = w(3f), center = Offset(x(165f), y(35f)))
            drawCircle(Color.White, radius = w(1.5f), center = Offset(x(140f), y(60f)))
            drawCircle(Color.White, radius = w(2.5f), center = Offset(x(25f), y(80f)))
            drawCircle(starColor, radius = w(2f), center = Offset(x(180f), y(90f)))
            drawCircle(Color(0xFFFFEE58), radius = w(12f), center = Offset(x(160f), y(30f)))
            drawCircle(bgColor, radius = w(10f), center = Offset(x(156f), y(27f)))
        }

        com.example.ui.model.SceneryTheme.LIBRARY -> {
            val shelfColor = Color(0xFF8D6E63)
            drawRoundRect(shelfColor, topLeft = Offset(x(10f), y(55f)), size = Size(w(180f), h(8f)), cornerRadius = CornerRadius(w(2f), h(2f)))
            drawRoundRect(shelfColor, topLeft = Offset(x(10f), y(115f)), size = Size(w(180f), h(8f)), cornerRadius = CornerRadius(w(2f), h(2f)))
            val bookColors = listOf(Color(0xFFE53935), Color(0xFF1E88E5), Color(0xFF43A047), Color(0xFFFB8C00), Color(0xFF8E24AA))
            for (i in 0..6) {
                val bColor = bookColors[i % bookColors.size]
                drawRoundRect(bColor, topLeft = Offset(x(18f + i * 8f), y(32f)), size = Size(w(6f), h(23f)), cornerRadius = CornerRadius(w(1f), h(1f)))
            }
            for (i in 0..5) {
                val bColor = bookColors[(i + 2) % bookColors.size]
                drawRoundRect(bColor, topLeft = Offset(x(135f + i * 8f), y(34f)), size = Size(w(6f), h(21f)), cornerRadius = CornerRadius(w(1f), h(1f)))
            }
        }

        com.example.ui.model.SceneryTheme.CLASSROOM -> {
            drawRoundRect(Color(0xFF2E7D32), topLeft = Offset(x(15f), y(15f)), size = Size(w(170f), h(95f)), cornerRadius = CornerRadius(w(10f), h(10f)))
            drawRoundRect(Color(0xFF8D6E63), topLeft = Offset(x(12f), y(12f)), size = Size(w(176f), h(101f)), cornerRadius = CornerRadius(w(12f), h(12f)), style = Stroke(width = w(3.5f)))
            drawCircle(Color(0xCCFFFFFF), radius = w(2.5f), center = Offset(x(40f), y(35f)))
            drawCircle(Color(0xCCFFFFFF), radius = w(2f), center = Offset(x(150f), y(40f)))
        }

        com.example.ui.model.SceneryTheme.CYBER -> {
            val neonColor = Color(0xFF00E5FF)
            drawLine(neonColor, start = Offset(x(0f), y(180f)), end = Offset(x(200f), y(180f)), strokeWidth = w(2f))
            drawLine(neonColor.copy(alpha = 0.5f), start = Offset(x(0f), y(200f)), end = Offset(x(200f), y(200f)), strokeWidth = w(1.5f))
            drawLine(neonColor.copy(alpha = 0.3f), start = Offset(x(0f), y(220f)), end = Offset(x(200f), y(220f)), strokeWidth = w(1f))
            drawLine(neonColor.copy(alpha = 0.4f), start = Offset(x(100f), y(180f)), end = Offset(x(40f), y(240f)), strokeWidth = w(1.5f))
            drawLine(neonColor.copy(alpha = 0.4f), start = Offset(x(100f), y(180f)), end = Offset(x(160f), y(240f)), strokeWidth = w(1.5f))
        }

        com.example.ui.model.SceneryTheme.SUNSET -> {
            drawCircle(Color(0xFFFF7043), radius = w(30f), center = Offset(x(100f), y(110f)))
            drawCircle(Color(0xFFFFCA28), radius = w(24f), center = Offset(x(100f), y(110f)))
            drawOval(Color(0x55FFA726), topLeft = Offset(x(20f), y(170f)), size = Size(w(160f), h(25f)))
        }

        com.example.ui.model.SceneryTheme.MINIMAL -> {
            drawCircle(
                color = Color(0x15000000),
                radius = w(65f),
                center = Offset(x(100f), y(110f))
            )
        }
    }
}

