package com.fridgepet.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fridgepet.app.domain.EvolutionStage
import com.fridgepet.app.domain.PetState
import kotlin.math.PI
import kotlin.math.sin

/**
 * Custom animated Jetpack Compose Canvas creature representing all 6 pet states
 * and 3 evolution stages. Designed behind a simple state+stage API so Rive/Lottie
 * assets can later drop in without touching screen code.
 */
@Composable
fun FridgePetAvatar(
    petState: PetState,
    evolutionStage: EvolutionStage,
    modifier: Modifier = Modifier,
    size: Dp = 188.dp,
) {
    val transition = rememberInfiniteTransition(label = "pet_anim")

    val bobPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (petState) {
                    PetState.CELEBRATING -> 900
                    PetState.ANXIOUS -> 750
                    PetState.SLEEPY -> 3200
                    else -> 2100
                },
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "bob",
    )

    val blinkValue by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "blink",
    )

    val backdropGradient = when (petState) {
        PetState.SLEEPY -> listOf(Color(0xFF1E293B), Color(0xFF334155))
        PetState.SICK -> listOf(Color(0xFFE7F0DC), Color(0xFFD5E2C3))
        PetState.ANXIOUS -> listOf(Color(0xFFFFF4D6), Color(0xFFFDE68A))
        PetState.CELEBRATING -> listOf(Color(0xFFD8F3E5), Color(0xFFFEF3C7))
        PetState.HUNGRY -> listOf(Color(0xFFFDE8E1), Color(0xFFFDF2E9))
        PetState.CONTENT -> listOf(Color(0xFFE0F7EE), Color(0xFFC8EFE0))
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(36.dp))
            .background(Brush.verticalGradient(backdropGradient)),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f

            val bounceAmp = when (petState) {
                PetState.CELEBRATING -> h * 0.045f
                PetState.SLEEPY -> h * 0.01f
                else -> h * 0.024f
            }
            val shiverX = if (petState == PetState.ANXIOUS) sin(bobPhase * 3f) * w * 0.014f else 0f
            val cy = h * 0.56f + sin(bobPhase) * bounceAmp

            // Fridge shelf line beneath the creature
            drawRoundRect(
                color = if (petState == PetState.SLEEPY) Color(0xFF475569) else Color.White.copy(alpha = 0.72f),
                topLeft = Offset(w * 0.14f, h * 0.80f),
                size = Size(w * 0.72f, h * 0.055f),
                cornerRadius = CornerRadius(20f, 20f),
            )

            // Soft ground shadow
            drawOval(
                color = Color.Black.copy(alpha = 0.10f),
                topLeft = Offset(cx - w * 0.21f + shiverX, h * 0.76f),
                size = Size(w * 0.42f, h * 0.05f),
            )

            val bodyColor = when (petState) {
                PetState.SICK -> Color(0xFFA3C585)
                PetState.SLEEPY -> Color(0xFF8BB8C9)
                PetState.ANXIOUS -> Color(0xFF8CD3C2)
                PetState.HUNGRY -> Color(0xFF9ED5D1)
                PetState.CELEBRATING -> Color(0xFF6DD5B0)
                PetState.CONTENT -> Color(0xFF7AD7BE)
            }
            val bellyColor = Color.White.copy(alpha = 0.65f)
            val outlineColor = Color(0xFF1C3B33)

            val bodyW = w * 0.48f
            val bodyH = h * 0.44f
            val bodyLeft = cx - bodyW / 2f + shiverX
            val bodyTop = cy - bodyH / 2f

            // Cute little stubby arms
            val armWave = if (petState == PetState.CELEBRATING) sin(bobPhase * 2f) * 18f else 0f
            drawCircle(
                color = bodyColor,
                radius = w * 0.06f,
                center = Offset(bodyLeft - w * 0.02f, cy + h * 0.02f - armWave),
            )
            drawCircle(
                color = bodyColor,
                radius = w * 0.06f,
                center = Offset(bodyLeft + bodyW + w * 0.02f, cy + h * 0.02f + armWave),
            )

            // Main rounded creature silhouette
            drawRoundRect(
                color = bodyColor,
                topLeft = Offset(bodyLeft, bodyTop),
                size = Size(bodyW, bodyH),
                cornerRadius = CornerRadius(bodyW * 0.46f, bodyW * 0.46f),
            )
            drawRoundRect(
                color = outlineColor.copy(alpha = 0.22f),
                topLeft = Offset(bodyLeft, bodyTop),
                size = Size(bodyW, bodyH),
                cornerRadius = CornerRadius(bodyW * 0.46f, bodyW * 0.46f),
                style = Stroke(width = 4f),
            )

            // Cozy pale belly patch
            drawOval(
                color = bellyColor,
                topLeft = Offset(cx - bodyW * 0.30f + shiverX, cy - bodyH * 0.02f),
                size = Size(bodyW * 0.60f, bodyH * 0.44f),
            )

            // Evolution accessories
            when (evolutionStage) {
                EvolutionStage.STAGE_1 -> Unit
                EvolutionStage.STAGE_2 -> drawLeafSprout(cx + shiverX, bodyTop)
                EvolutionStage.STAGE_3 -> {
                    drawLeafSprout(cx + shiverX + bodyW * 0.16f, bodyTop + h * 0.02f)
                    drawChefHat(cx + shiverX, bodyTop, bodyW)
                }
            }

            // Face (eyes + cheeks + mouth)
            val isBlinking = blinkValue > 0.93f
            drawPetFace(
                petState = petState,
                cx = cx + shiverX,
                cy = cy - bodyH * 0.10f,
                bodyW = bodyW,
                isBlinking = isBlinking,
                ink = outlineColor,
            )

            // Mood ambient particles
            when (petState) {
                PetState.ANXIOUS -> drawSweatDrop(bodyLeft + bodyW * 0.88f, bodyTop + bodyH * 0.16f)
                PetState.CELEBRATING -> drawConfetti(w, h, bobPhase)
                PetState.SLEEPY -> drawSleepyStars(w, h, bobPhase)
                PetState.SICK -> drawDizzyBubble(bodyLeft + bodyW * 0.92f, bodyTop + bodyH * 0.05f, bobPhase)
                else -> Unit
            }
        }
    }
}

private fun DrawScope.drawLeafSprout(x: Float, topY: Float) {
    val stemPath = Path().apply {
        moveTo(x, topY + 6f)
        quadraticTo(x + 4f, topY - 22f, x + 14f, topY - 26f)
    }
    drawPath(stemPath, color = Color(0xFF1F6E58), style = Stroke(width = 6f, cap = StrokeCap.Round))
    drawOval(
        color = Color(0xFF4ADE80),
        topLeft = Offset(x + 6f, topY - 38f),
        size = Size(28f, 16f),
    )
}

private fun DrawScope.drawChefHat(cx: Float, topY: Float, bodyW: Float) {
    val hatW = bodyW * 0.56f
    val hatH = bodyW * 0.30f
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(cx - hatW / 2f, topY - hatH * 0.65f),
        size = Size(hatW, hatH * 0.72f),
        cornerRadius = CornerRadius(14f, 14f),
    )
    drawCircle(
        color = Color.White,
        radius = hatW * 0.28f,
        center = Offset(cx - hatW * 0.18f, topY - hatH * 0.75f),
    )
    drawCircle(
        color = Color.White,
        radius = hatW * 0.30f,
        center = Offset(cx + hatW * 0.15f, topY - hatH * 0.78f),
    )
    drawRoundRect(
        color = Color(0xFFF59E0B),
        topLeft = Offset(cx - hatW / 2f, topY - hatH * 0.10f),
        size = Size(hatW, 10f),
        cornerRadius = CornerRadius(5f, 5f),
    )
}

private fun DrawScope.drawPetFace(
    petState: PetState,
    cx: Float,
    cy: Float,
    bodyW: Float,
    isBlinking: Boolean,
    ink: Color,
) {
    val eyeOffsetX = bodyW * 0.18f
    val eyeY = cy
    val eyeR = bodyW * 0.055f

    // Cheeks
    if (petState != PetState.SICK) {
        val cheekColor = if (petState == PetState.CELEBRATING) Color(0xFFFB7185) else Color(0xFFFDA4AF)
        drawOval(
            color = cheekColor.copy(alpha = 0.65f),
            topLeft = Offset(cx - eyeOffsetX - eyeR * 2f, eyeY + eyeR * 1.1f),
            size = Size(eyeR * 2.4f, eyeR * 1.3f),
        )
        drawOval(
            color = cheekColor.copy(alpha = 0.65f),
            topLeft = Offset(cx + eyeOffsetX - eyeR * 0.4f, eyeY + eyeR * 1.1f),
            size = Size(eyeR * 2.4f, eyeR * 1.3f),
        )
    }

    // Eyes
    when {
        petState == PetState.SLEEPY || isBlinking -> {
            val stroke = Stroke(width = 5f, cap = StrokeCap.Round)
            drawArc(
                color = ink,
                startAngle = 20f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(cx - eyeOffsetX - eyeR, eyeY - eyeR * 0.4f),
                size = Size(eyeR * 2f, eyeR * 1.4f),
                style = stroke,
            )
            drawArc(
                color = ink,
                startAngle = 20f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(cx + eyeOffsetX - eyeR, eyeY - eyeR * 0.4f),
                size = Size(eyeR * 2f, eyeR * 1.4f),
                style = stroke,
            )
        }
        petState == PetState.SICK -> {
            val stroke = Stroke(width = 5f, cap = StrokeCap.Round)
            val s = eyeR * 0.9f
            drawLine(ink, Offset(cx - eyeOffsetX - s, eyeY - s), Offset(cx - eyeOffsetX + s, eyeY + s), strokeWidth = 5f, cap = StrokeCap.Round)
            drawLine(ink, Offset(cx - eyeOffsetX + s, eyeY - s), Offset(cx - eyeOffsetX - s, eyeY + s), strokeWidth = 5f, cap = StrokeCap.Round)
            drawLine(ink, Offset(cx + eyeOffsetX - s, eyeY - s), Offset(cx + eyeOffsetX + s, eyeY + s), strokeWidth = 5f, cap = StrokeCap.Round)
            drawLine(ink, Offset(cx + eyeOffsetX + s, eyeY - s), Offset(cx + eyeOffsetX - s, eyeY + s), strokeWidth = 5f, cap = StrokeCap.Round)
            drawLine(ink, Offset(cx, cy), Offset(cx, cy), strokeWidth = stroke.width)
        }
        petState == PetState.CELEBRATING -> {
            val stroke = Stroke(width = 6f, cap = StrokeCap.Round)
            drawArc(
                color = ink,
                startAngle = 195f,
                sweepAngle = 150f,
                useCenter = false,
                topLeft = Offset(cx - eyeOffsetX - eyeR, eyeY - eyeR * 0.7f),
                size = Size(eyeR * 2f, eyeR * 1.8f),
                style = stroke,
            )
            drawArc(
                color = ink,
                startAngle = 195f,
                sweepAngle = 150f,
                useCenter = false,
                topLeft = Offset(cx + eyeOffsetX - eyeR, eyeY - eyeR * 0.7f),
                size = Size(eyeR * 2f, eyeR * 1.8f),
                style = stroke,
            )
        }
        else -> {
            val r = if (petState == PetState.HUNGRY || petState == PetState.ANXIOUS) eyeR * 1.2f else eyeR
            drawCircle(color = ink, radius = r, center = Offset(cx - eyeOffsetX, eyeY))
            drawCircle(color = ink, radius = r, center = Offset(cx + eyeOffsetX, eyeY))
            drawCircle(color = Color.White, radius = r * 0.36f, center = Offset(cx - eyeOffsetX - r * 0.25f, eyeY - r * 0.25f))
            drawCircle(color = Color.White, radius = r * 0.36f, center = Offset(cx + eyeOffsetX - r * 0.25f, eyeY - r * 0.25f))
        }
    }

    // Mouth
    val mouthY = eyeY + eyeR * 2.3f
    val stroke = Stroke(width = 5f, cap = StrokeCap.Round)
    when (petState) {
        PetState.CONTENT, PetState.CELEBRATING -> {
            drawArc(
                color = ink,
                startAngle = 15f,
                sweepAngle = 150f,
                useCenter = false,
                topLeft = Offset(cx - eyeR * 1.4f, mouthY - eyeR),
                size = Size(eyeR * 2.8f, eyeR * 2.0f),
                style = stroke,
            )
        }
        PetState.HUNGRY -> {
            drawOval(
                color = ink,
                topLeft = Offset(cx - eyeR * 0.9f, mouthY - eyeR * 0.4f),
                size = Size(eyeR * 1.8f, eyeR * 1.6f),
            )
        }
        PetState.ANXIOUS, PetState.SICK -> {
            val wavy = Path().apply {
                moveTo(cx - eyeR * 1.6f, mouthY + 4f)
                quadraticTo(cx - eyeR * 0.6f, mouthY - 8f, cx, mouthY + 2f)
                quadraticTo(cx + eyeR * 0.6f, mouthY + 12f, cx + eyeR * 1.6f, mouthY)
            }
            drawPath(wavy, color = ink, style = stroke)
        }
        PetState.SLEEPY -> {
            drawCircle(
                color = ink,
                radius = eyeR * 0.55f,
                center = Offset(cx, mouthY + 2f),
            )
        }
    }
}

private fun DrawScope.drawSweatDrop(x: Float, y: Float) {
    val drop = Path().apply {
        moveTo(x, y - 16f)
        quadraticTo(x + 12f, y, x, y + 12f)
        quadraticTo(x - 12f, y, x, y - 16f)
    }
    drawPath(drop, color = Color(0xFF38BDF8))
}

private fun DrawScope.drawConfetti(w: Float, h: Float, phase: Float) {
    val colors = listOf(Color(0xFFF59E0B), Color(0xFFFB7185), Color(0xFF34D399), Color(0xFF60A5FA))
    for (i in 0..7) {
        val px = w * (0.14f + 0.10f * i)
        val py = h * (0.18f + 0.08f * ((i * 3) % 4)) + sin(phase + i) * 10f
        drawCircle(color = colors[i % colors.size], radius = 7f, center = Offset(px, py))
    }
}

private fun DrawScope.drawSleepyStars(w: Float, h: Float, phase: Float) {
    val alpha = 0.55f + 0.35f * sin(phase)
    drawCircle(color = Color(0xFFFDE68A).copy(alpha = alpha), radius = 6f, center = Offset(w * 0.22f, h * 0.22f))
    drawCircle(color = Color(0xFFFDE68A).copy(alpha = alpha), radius = 4f, center = Offset(w * 0.78f, h * 0.18f))
    drawCircle(color = Color(0xFFBAE6FD).copy(alpha = alpha), radius = 5f, center = Offset(w * 0.84f, h * 0.34f))
}

private fun DrawScope.drawDizzyBubble(x: Float, y: Float, phase: Float) {
    val drift = sin(phase) * 8f
    drawCircle(color = Color(0xFF84CC16).copy(alpha = 0.45f), radius = 12f, center = Offset(x, y + drift))
    drawCircle(color = Color(0xFF84CC16).copy(alpha = 0.30f), radius = 8f, center = Offset(x + 16f, y - 18f + drift))
}
