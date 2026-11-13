package com.fridgepet.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fridgepet.app.domain.EvolutionStage
import com.fridgepet.app.domain.PetSpecies
import com.fridgepet.app.domain.PetState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

private data class HeartParticle(
    val id: Long,
    val xOffset: Float,
    val yOffset: Float,
    val scale: Float,
    val alpha: Float,
)

/**
 * Interactive Fridge Pet Avatar supporting 10 species, 6 mood states,
 * evolution stages, accessories (Cozy Scarf), and real-time touch / pampering gestures.
 */
@Composable
fun FridgePetAvatar(
    petState: PetState,
    evolutionStage: EvolutionStage,
    species: PetSpecies = PetSpecies.MOCHI,
    hasCozyScarf: Boolean = false,
    modifier: Modifier = Modifier,
    size: Dp = 196.dp,
    interactive: Boolean = true,
    onPetTapped: () -> Unit = {},
    onPetPampered: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val transition = rememberInfiniteTransition(label = "pet_anim")

    // Dynamic Squish & Stretch Physics Animatable
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }
    var isBeingPampered by remember { mutableStateOf(false) }

    // Heart particles generated when pampered/rubbed
    val hearts = remember { mutableStateListOf<HeartParticle>() }

    val bobPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (petState) {
                    PetState.CELEBRATING -> 800
                    PetState.ANXIOUS -> 650
                    PetState.SLEEPY -> 3400
                    else -> 2000
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
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "blink",
    )

    val backdropGradient = when (petState) {
        PetState.SLEEPY -> listOf(Color(0xFF1E293B), Color(0xFF334155))
        PetState.SICK -> listOf(Color(0xFFE5EDE0), Color(0xFFD4E2CC))
        PetState.ANXIOUS -> listOf(Color(0xFFFFF4D6), Color(0xFFFDE68A))
        PetState.CELEBRATING -> listOf(Color(0xFFD8F3E5), Color(0xFFFEF3C7))
        PetState.HUNGRY -> listOf(Color(0xFFFDE8E1), Color(0xFFFDF2E9))
        PetState.CONTENT -> listOf(Color(0xFFE4F7EF), Color(0xFFCCF0E1))
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(36.dp))
            .background(Brush.verticalGradient(backdropGradient))
            .then(
                if (interactive) {
                    Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    onPetTapped()
                                    // Elastic squash on tap
                                    coroutineScope.launch {
                                        scaleX.animateTo(1.18f, spring(stiffness = Spring.StiffnessHigh))
                                        scaleX.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                    }
                                    coroutineScope.launch {
                                        scaleY.animateTo(0.82f, spring(stiffness = Spring.StiffnessHigh))
                                        scaleY.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                    }
                                },
                            )
                        }
                        .pointerInput(Unit) {
                            var dragAccumulator = 0f
                            detectDragGestures(
                                onDragStart = { isBeingPampered = true },
                                onDragEnd = {
                                    isBeingPampered = false
                                    dragAccumulator = 0f
                                },
                                onDragCancel = {
                                    isBeingPampered = false
                                    dragAccumulator = 0f
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    dragAccumulator += dragAmount.getDistance()
                                    if (dragAccumulator > 35f) {
                                        dragAccumulator = 0f
                                        onPetPampered()
                                        // Spawn a cheerful floating heart particle
                                        val newHeart = HeartParticle(
                                            id = System.nanoTime(),
                                            xOffset = (Math.random().toFloat() - 0.5f) * 60f,
                                            yOffset = -20f,
                                            scale = 0.8f + (Math.random().toFloat() * 0.4f),
                                            alpha = 1f,
                                        )
                                        hearts.add(newHeart)
                                    }
                                },
                            )
                        }
                } else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        // Floating Heart Particles logic
        LaunchedEffect(hearts.size) {
            if (hearts.isNotEmpty()) {
                delay(60)
                val iterator = hearts.listIterator()
                while (iterator.hasNext()) {
                    val p = iterator.next()
                    val nextAlpha = p.alpha - 0.08f
                    val nextY = p.yOffset - 6f
                    if (nextAlpha <= 0f) {
                        iterator.remove()
                    } else {
                        iterator.set(p.copy(alpha = nextAlpha, yOffset = nextY))
                    }
                }
            }
        }

        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f

            val bounceAmp = when {
                isBeingPampered -> h * 0.035f
                petState == PetState.CELEBRATING -> h * 0.045f
                petState == PetState.SLEEPY -> h * 0.01f
                else -> h * 0.022f
            }
            val shiverX = if (petState == PetState.ANXIOUS) sin(bobPhase * 3f) * w * 0.015f else 0f
            val cy = h * 0.55f + sin(bobPhase) * bounceAmp

            // Shelf glass base beneath creature
            drawRoundRect(
                color = if (petState == PetState.SLEEPY) Color(0xFF475569) else Color.White.copy(alpha = 0.75f),
                topLeft = Offset(w * 0.12f, h * 0.82f),
                size = Size(w * 0.76f, h * 0.052f),
                cornerRadius = CornerRadius(18f, 18f),
            )

            // Ground shadow
            drawOval(
                color = Color.Black.copy(alpha = 0.09f),
                topLeft = Offset(cx - w * 0.22f + shiverX, h * 0.78f),
                size = Size(w * 0.44f * scaleX.value, h * 0.05f),
            )

            val basePrimary = Color(species.primaryColorHex)
            val baseAccent = Color(species.accentColorHex)

            val bodyColor = when (petState) {
                PetState.SICK -> Color(0xFFA5C984)
                PetState.SLEEPY -> Color(0xFF8BB5C9)
                else -> basePrimary
            }

            val bodyW = w * 0.50f * scaleX.value
            val bodyH = h * 0.45f * scaleY.value
            val bodyLeft = cx - bodyW / 2f + shiverX
            val bodyTop = cy - bodyH / 2f

            // Species-specific appendages (arms / ears / tail / zest)
            drawSpeciesAppendages(species, bodyLeft, bodyTop, bodyW, bodyH, bodyColor, bobPhase)

            // Main body
            drawRoundRect(
                color = bodyColor,
                topLeft = Offset(bodyLeft, bodyTop),
                size = Size(bodyW, bodyH),
                cornerRadius = CornerRadius(bodyW * 0.46f, bodyW * 0.46f),
            )
            drawRoundRect(
                color = baseAccent.copy(alpha = 0.20f),
                topLeft = Offset(bodyLeft, bodyTop),
                size = Size(bodyW, bodyH),
                cornerRadius = CornerRadius(bodyW * 0.46f, bodyW * 0.46f),
                style = Stroke(width = 4f),
            )

            // Tummy patch
            drawOval(
                color = Color.White.copy(alpha = 0.55f),
                topLeft = Offset(cx - bodyW * 0.28f + shiverX, cy - bodyH * 0.02f),
                size = Size(bodyW * 0.56f, bodyH * 0.42f),
            )

            // Evolution 1 Accessory: Cozy Scarf (1 low-waste week)
            if (hasCozyScarf) {
                drawCozyScarf(cx + shiverX, cy + bodyH * 0.16f, bodyW)
            }

            // Evolution Stages (Leaf sprout, Golden Chef Hat)
            when (evolutionStage) {
                EvolutionStage.STAGE_1 -> Unit
                EvolutionStage.STAGE_2 -> drawLeafSprout(cx + shiverX, bodyTop)
                EvolutionStage.STAGE_3 -> {
                    drawLeafSprout(cx + shiverX + bodyW * 0.18f, bodyTop + h * 0.02f)
                    drawChefHat(cx + shiverX, bodyTop, bodyW)
                }
            }

            // Eyes, cheeks & mouth
            val isBlinking = blinkValue > 0.92f || isBeingPampered
            drawPetFace(
                petState = if (isBeingPampered) PetState.CELEBRATING else petState,
                cx = cx + shiverX,
                cy = cy - bodyH * 0.10f,
                bodyW = bodyW,
                isBlinking = isBlinking,
                ink = Color(0xFF14201B),
            )

            // Floating Heart Particles Render
            hearts.forEach { heart ->
                drawHeart(
                    cx = cx + heart.xOffset,
                    cy = cy + heart.yOffset,
                    size = 14f * heart.scale,
                    alpha = heart.alpha,
                )
            }
        }
    }
}

private fun DrawScope.drawSpeciesAppendages(
    species: PetSpecies,
    bodyLeft: Float,
    bodyTop: Float,
    bodyW: Float,
    bodyH: Float,
    color: Color,
    phase: Float,
) {
    when (species) {
        PetSpecies.MOCHI, PetSpecies.LABAN -> {
            // Little round ears
            drawCircle(color, bodyW * 0.11f, Offset(bodyLeft + bodyW * 0.18f, bodyTop + 6f))
            drawCircle(color, bodyW * 0.11f, Offset(bodyLeft + bodyW * 0.82f, bodyTop + 6f))
        }
        PetSpecies.CHILLI -> {
            // Green chili stem on top
            val stemPath = Path().apply {
                moveTo(bodyLeft + bodyW * 0.50f, bodyTop)
                quadraticTo(bodyLeft + bodyW * 0.50f, bodyTop - 26f, bodyLeft + bodyW * 0.65f, bodyTop - 32f)
            }
            drawPath(stemPath, color = Color(0xFF227847), style = Stroke(width = 8f, cap = StrokeCap.Round))
        }
        PetSpecies.FROSTY -> {
            // Crystal icy corner spikes
            drawCircle(Color.White.copy(alpha = 0.8f), 6f, Offset(bodyLeft + 12f, bodyTop + 14f))
            drawCircle(Color.White.copy(alpha = 0.8f), 4f, Offset(bodyLeft + bodyW - 14f, bodyTop + 18f))
        }
        PetSpecies.CITRUS -> {
            // Tiny green leaf tail
            val leafPath = Path().apply {
                moveTo(bodyLeft + bodyW * 0.50f, bodyTop)
                quadraticTo(bodyLeft + bodyW * 0.58f, bodyTop - 18f, bodyLeft + bodyW * 0.70f, bodyTop - 14f)
            }
            drawPath(leafPath, color = Color(0xFF16A34A), style = Stroke(width = 6f, cap = StrokeCap.Round))
        }
        PetSpecies.ZAATAR -> {
            // Tiny sesame sprinkles
            drawCircle(Color(0xFF6B4310), 3f, Offset(bodyLeft + bodyW * 0.32f, bodyTop + bodyH * 0.22f))
            drawCircle(Color(0xFF6B4310), 3f, Offset(bodyLeft + bodyW * 0.68f, bodyTop + bodyH * 0.24f))
            drawCircle(Color(0xFF6B4310), 2.5f, Offset(bodyLeft + bodyW * 0.50f, bodyTop + bodyH * 0.15f))
        }
        else -> Unit
    }

    // Friendly little stubby arms
    val wave = sin(phase * 2f) * 12f
    drawCircle(color, bodyW * 0.10f, Offset(bodyLeft - bodyW * 0.04f, bodyTop + bodyH * 0.52f - wave))
    drawCircle(color, bodyW * 0.10f, Offset(bodyLeft + bodyW + bodyW * 0.04f, bodyTop + bodyH * 0.52f + wave))
}

private fun DrawScope.drawCozyScarf(cx: Float, y: Float, bodyW: Float) {
    val scarfW = bodyW * 0.70f
    val scarfH = 14f
    // Knitted coral scarf loop
    drawRoundRect(
        color = Color(0xFFE5604D),
        topLeft = Offset(cx - scarfW / 2f, y - scarfH / 2f),
        size = Size(scarfW, scarfH),
        cornerRadius = CornerRadius(7f, 7f),
    )
    // Hanging scarf tail
    val tailPath = Path().apply {
        moveTo(cx + scarfW * 0.20f, y)
        lineTo(cx + scarfW * 0.34f, y + 26f)
        lineTo(cx + scarfW * 0.18f, y + 26f)
        close()
    }
    drawPath(tailPath, color = Color(0xFFC74332))
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
    val eyeR = bodyW * 0.058f

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
            drawCircle(color = ink, radius = eyeR * 0.55f, center = Offset(cx, mouthY + 2f))
        }
    }
}

private fun DrawScope.drawHeart(cx: Float, cy: Float, size: Float, alpha: Float) {
    val heartColor = Color(0xFFF43F5E).copy(alpha = alpha.coerceIn(0f, 1f))
    val path = Path().apply {
        moveTo(cx, cy + size * 0.6f)
        cubicTo(
            cx - size * 1.1f, cy,
            cx - size * 0.8f, cy - size * 0.9f,
            cx, cy - size * 0.3f,
        )
        cubicTo(
            cx + size * 0.8f, cy - size * 0.9f,
            cx + size * 1.1f, cy,
            cx, cy + size * 0.6f,
        )
        close()
    }
    drawPath(path, color = heartColor)
}
