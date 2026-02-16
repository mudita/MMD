/*
 *
 * Copyright 2025 The Android Open Source Project
 *
 * Based on Material 3. Modified by Mudita Sp. z o.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.mudita.mmd.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Card MMD
 *
 * Cards contain contain content and actions that relate information about a subject. Filled cards
 * provide subtle separation from the background. This has less emphasis than elevated or outlined
 * cards.
 *
 * This Card does not handle input events - see the other Card overloads if you want a clickable or
 * selectable Card.
 *
 * Card sample:
 *
 *```kotlin
 * CardMMD(
 *     colors = CardDefaultsMMD.cardColors(
 *         containerColor = MaterialTheme.colorScheme.surfaceVariant,
 *     ),
 *     modifier = Modifier
 *         .size(width = 320.dp, height = 140.dp)
 * ) {
 *     Box(modifier = Modifier.fillMaxSize()) {
 *         TextMMD(
 *             text = "Filled",
 *             modifier = Modifier.align(Alignment.TopStart)
 *                 .padding(8.dp),
 *             textAlign = TextAlign.Center,
 *         )
 *
 *         ButtonMMD(
 *             onClick = { /* akcja przycisku */ },
 *             modifier = Modifier
 *                 .align(Alignment.BottomEnd)
 *                 .padding(8.dp)
 *         ) {
 *             TextMMD("Click Me")
 *         }
 *     }
 * }
 *```
 *
 * @param modifier the [Modifier] to be applied to this card
 * @param shape defines the shape of this card's container, border (when [border] is not null), and
 *   shadow (when using [elevation])
 * @param colors [CardColorsMMD] that will be used to resolve the colors used for this card in
 *   different states. See [CardDefaultsMMD.cardColors].
 * @param elevation [CardElevationMMD] used to resolve the elevation for this card in different states.
 *   This controls the size of the shadow below the card. Additionally, when the container color is
 *   [ColorScheme.surface], this controls the amount of primary color applied as an overlay. See
 *   also: [Surface].
 * @param border the border to draw around the container of this card
 */
@Composable
fun CardMMD(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaultsMMD.shape,
    colors: CardColorsMMD = CardDefaultsMMD.cardColors(),
    elevation: CardElevationMMD = CardDefaultsMMD.cardElevation(),
    border: BorderStroke? = CardDefaultsMMD.border,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = colors.containerColor(enabled = true),
        contentColor = colors.contentColor(enabled = true),
        shadowElevation = elevation.shadowElevation(enabled = true, interactionSource = null).value,
        border = border,
    ) {
        Column(content = content)
    }
}

/**
 * Card MMD
 *
 * Cards contain contain content and actions that relate information about a subject. Filled cards
 * provide subtle separation from the background. This has less emphasis than outlined
 * cards.
 *
 * This Card handles click events, calling its [onClick] lambda.
 *
 * Clickable card sample:
 *
 *```kotlin
 * CardMMD(
 *     onClick = { /* click action */ },
 *     colors = CardDefaultsMMD.cardColors(
 *         containerColor = MaterialTheme.colorScheme.surfaceVariant,
 *     ),
 *     modifier = Modifier
 *         .size(width = 320.dp, height = 140.dp),
 *     enabled = false
 * ) {
 *     Box(modifier = Modifier.fillMaxSize()) {
 *         TextMMD(
 *             text = "Filled Disabled",
 *             modifier = Modifier.align(Alignment.TopStart)
 *                 .padding(8.dp),
 *             textAlign = TextAlign.Center,
 *         )
 *
 *         ButtonMMD(
 *             enabled = false,
 *             onClick = { /* akcja przycisku */ },
 *             modifier = Modifier
 *                 .align(Alignment.BottomEnd)
 *                 .padding(8.dp)
 *         ) {
 *             TextMMD("Click Me")
 *         }
 *     }
 * }
 *```
 *
 * @param onClick called when this card is clicked
 * @param modifier the [Modifier] to be applied to this card
 * @param enabled controls the enabled state of this card. When `false`, this component will not
 *   respond to user input, and it will appear visually disabled and disabled to accessibility
 *   services.
 * @param shape defines the shape of this card's container, border (when [border] is not null), and
 *   shadow (when using [elevation])
 * @param colors [CardColorsMMD] that will be used to resolve the color(s) used for this card in
 *   different states. See [CardDefaultsMMD.cardColors].
 * @param elevation [CardElevationMMD] used to resolve the elevation for this card in different states.
 *   This controls the size of the shadow below the card. Additionally, when the container color is
 *   [ColorScheme.surface], this controls the amount of primary color applied as an overlay. See
 *   also: [Surface].
 * @param border the border to draw around the container of this card
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this card. You can use this to change the card's appearance or
 *   preview the card in different states. Note that if `null` is provided, interactions will still
 *   happen internally.
 */
@Composable
fun CardMMD(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CardDefaultsMMD.shape,
    colors: CardColorsMMD = CardDefaultsMMD.cardColors(),
    elevation: CardElevationMMD = CardDefaultsMMD.cardElevation(),
    border: BorderStroke? = CardDefaultsMMD.border(enabled),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    Surface(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        color = colors.containerColor(enabled),
        contentColor = colors.contentColor(enabled),
        shadowElevation = elevation.shadowElevation(enabled, interactionSource).value,
        border = border,
        interactionSource = interactionSource,
    ) {
        Column(content = content)
    }
}

/** Contains the default values used by all card types. */
object CardDefaultsMMD {
    /** Default shape for a card. */
    val shape: Shape
        @Composable get() = RoundedCornerShape(8.dp)

    /**
     * Default border for Card
     */
    val border: BorderStroke
        @Composable get() = BorderStroke(3.dp, MaterialTheme.colorScheme.outline)

    /**
     * Creates a [CardElevationMMD] that will switch between the provided values according to the
     * Material specification for a [CardMMD].
     *
     * @param defaultElevation the elevation used when the [CardMMD] is has no other [Interaction]s.
     * @param pressedElevation the elevation used when the [CardMMD] is pressed.
     * @param focusedElevation the elevation used when the [CardMMD] is focused.
     * @param hoveredElevation the elevation used when the [CardMMD] is hovered.
     * @param draggedElevation the elevation used when the [CardMMD] is dragged.
     */
    @Composable
    fun cardElevation(
        defaultElevation: Dp = 0.dp,
        pressedElevation: Dp = 0.dp,
        focusedElevation: Dp = 0.dp,
        hoveredElevation: Dp = 0.dp,
        draggedElevation: Dp = 0.dp,
        disabledElevation: Dp = 0.dp,
    ): CardElevationMMD =
        CardElevationMMD(
            defaultElevation = defaultElevation,
            pressedElevation = pressedElevation,
            focusedElevation = focusedElevation,
            hoveredElevation = hoveredElevation,
            draggedElevation = draggedElevation,
            disabledElevation = disabledElevation,
        )

    /**
     * Creates a [CardColorsMMD] that represents the default container and content colors used in a
     * [CardMMD].
     */
    @Composable
    fun cardColors() = defaultCardColors

    /**
     * Creates a [CardColorsMMD] that represents the default container and content colors used in a
     * [CardMMD].
     *
     * @param containerColor the container color of this [CardMMD] when enabled.
     * @param contentColor the content color of this [CardMMD] when enabled.
     * @param disabledContainerColor the container color of this [CardMMD] when not enabled.
     * @param disabledContentColor the content color of this [CardMMD] when not enabled.
     */
    @Composable
    fun cardColors(
        containerColor: Color = Color.Unspecified,
        contentColor: Color = contentColorFor(containerColor),
        disabledContainerColor: Color = Color.Unspecified,
        disabledContentColor: Color = contentColor.copy(.38f),
    ): CardColorsMMD =
        defaultCardColors.copy(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    private val defaultCardColors: CardColorsMMD
        @Composable
        get() = CardColorsMMD(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHighest),
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                .copy(alpha = .38f)
                .compositeOver(MaterialTheme.colorScheme.surfaceContainerHighest),
            disabledContentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHighest)
                .copy(.38f),
        )

    @Composable
    fun border(enabled: Boolean = true): BorderStroke {
        val color =
            if (enabled) {
                MaterialTheme.colorScheme.outline
            } else {
                MaterialTheme.colorScheme.outline
                    .copy(alpha = .12f)
                    .compositeOver(MaterialTheme.colorScheme.surfaceContainerLow)
            }
        return remember(color) { BorderStroke(3.dp, color) }
    }
}

/**
 * Represents the elevation for a card in different states.
 * - See [CardDefaultsMMD.cardElevation] for the default elevation used in a [CardMMD].
 */
@Immutable
class CardElevationMMD
internal constructor(
    private val defaultElevation: Dp,
    private val pressedElevation: Dp,
    private val focusedElevation: Dp,
    private val hoveredElevation: Dp,
    private val draggedElevation: Dp,
    private val disabledElevation: Dp,
) {
    /**
     * Represents the shadow elevation used in a card, depending on its [enabled] state and
     * [interactionSource].
     *
     * Shadow elevation is used to apply a shadow around the card to give it higher emphasis.
     *
     * @param enabled whether the card is enabled
     * @param interactionSource the [InteractionSource] for this card
     */
    @Composable
    internal fun shadowElevation(
        enabled: Boolean,
        interactionSource: InteractionSource?,
    ): State<Dp> {
        val interactions = remember { mutableStateListOf<Interaction>() }

        LaunchedEffect(interactionSource) {
            interactionSource?.interactions?.collect { interaction ->
                when (interaction) {
                    is HoverInteraction.Enter -> interactions.add(interaction)
                    is HoverInteraction.Exit -> interactions.remove(interaction.enter)
                    is FocusInteraction.Focus -> interactions.add(interaction)
                    is FocusInteraction.Unfocus -> interactions.remove(interaction.focus)
                    is PressInteraction.Press -> interactions.add(interaction)
                    is PressInteraction.Release -> interactions.remove(interaction.press)
                    is PressInteraction.Cancel -> interactions.remove(interaction.press)
                    is DragInteraction.Start -> interactions.add(interaction)
                    is DragInteraction.Stop -> interactions.remove(interaction.start)
                    is DragInteraction.Cancel -> interactions.remove(interaction.start)
                }
            }
        }

        val interaction = interactions.lastOrNull()

        val target = if (!enabled) {
            disabledElevation
        } else {
            when (interaction) {
                is PressInteraction.Press -> pressedElevation
                is HoverInteraction.Enter -> hoveredElevation
                is FocusInteraction.Focus -> focusedElevation
                is DragInteraction.Start -> draggedElevation
                else -> defaultElevation
            }
        }

        return rememberUpdatedState(target)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is CardElevationMMD) return false

        if (defaultElevation != other.defaultElevation) return false
        if (pressedElevation != other.pressedElevation) return false
        if (focusedElevation != other.focusedElevation) return false
        if (hoveredElevation != other.hoveredElevation) return false
        if (disabledElevation != other.disabledElevation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = defaultElevation.hashCode()
        result = 31 * result + pressedElevation.hashCode()
        result = 31 * result + focusedElevation.hashCode()
        result = 31 * result + hoveredElevation.hashCode()
        result = 31 * result + disabledElevation.hashCode()
        return result
    }
}

/**
 * Represents the container and content colors used in a card in different states.
 *
 * @param containerColor the container color of this [CardMMD] when enabled.
 * @param contentColor the content color of this [CardMMD] when enabled.
 * @param disabledContainerColor the container color of this [CardMMD] when not enabled.
 * @param disabledContentColor the content color of this [CardMMD] when not enabled.
 * @constructor create an instance with arbitrary colors.
 * - See [CardDefaultsMMD.cardColors] for the default colors used in a [CardMMD].
 * - See [CardDefaultsMMD.elevatedCardColors] for the default colors used in a [ElevatedCard].
 * - See [CardDefaultsMMD.outlinedCardColors] for the default colors used in a [OutlinedCardMMD].
 */
@Immutable
class CardColorsMMD(
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
) {
    /**
     * Returns a copy of this CardColors, optionally overriding some of the values. This uses the
     * Color.Unspecified to mean “use the value from the source”
     */
    fun copy(
        containerColor: Color = this.containerColor,
        contentColor: Color = this.contentColor,
        disabledContainerColor: Color = this.disabledContainerColor,
        disabledContentColor: Color = this.disabledContentColor,
    ) =
        CardColorsMMD(
            containerColor.takeOrElse { this.containerColor },
            contentColor.takeOrElse { this.contentColor },
            disabledContainerColor.takeOrElse { this.disabledContainerColor },
            disabledContentColor.takeOrElse { this.disabledContentColor },
        )

    /**
     * Represents the container color for this card, depending on [enabled].
     *
     * @param enabled whether the card is enabled
     */
    @Stable
    internal fun containerColor(enabled: Boolean): Color =
        if (enabled) containerColor else disabledContainerColor

    /**
     * Represents the content color for this card, depending on [enabled].
     *
     * @param enabled whether the card is enabled
     */
    @Stable
    internal fun contentColor(enabled: Boolean) =
        if (enabled) contentColor else disabledContentColor

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is CardColorsMMD) return false

        if (containerColor != other.containerColor) return false
        if (contentColor != other.contentColor) return false
        if (disabledContainerColor != other.disabledContainerColor) return false
        if (disabledContentColor != other.disabledContentColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = containerColor.hashCode()
        result = 31 * result + contentColor.hashCode()
        result = 31 * result + disabledContainerColor.hashCode()
        result = 31 * result + disabledContentColor.hashCode()
        return result
    }
}
