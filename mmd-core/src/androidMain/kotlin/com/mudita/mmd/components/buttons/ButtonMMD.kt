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

package com.mudita.mmd.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Choose the best button for an action based on the amount of emphasis it needs. The more important
 * an action is, the higher emphasis its button should be.
 *
 * - See [OutlinedButtonMMD] for a medium-emphasis button with a border.
 *
 * The default text style for internal [Text] components will be set to [Typography.labelLarge].
 *
 * @param onClick called when this button is clicked
 * @param modifier the [Modifier] to be applied to this button
 * @param enabled controls the enabled state of this button. When `false`, this component will not
 * respond to user input, and it will appear visually disabled and disabled to accessibility
 * services.
 * @param shape defines the shape of this button's container, border (when [border] is not null)
 * @param colors [ButtonColors] that will be used to resolve the colors for this button in different
 * states. See [ButtonDefaultsMMD.buttonColors].
 * @param border the border to draw around the container of this button
 * @param contentPadding the spacing values to apply internally between the container and the
 * content
 */
@Composable
fun ButtonMMD(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaultsMMD.shape,
    colors: ButtonColors = ButtonDefaultsMMD.buttonColors(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaultsMMD.contentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        modifier = modifier.defaultMinSize(
            minWidth = ButtonDefaultsMMD.minWidth,
            minHeight = ButtonDefaultsMMD.minHeight,
        ),
        contentPadding = contentPadding,
        shape = shape,
        border = border,
        colors = colors,
        elevation = null,
        enabled = enabled,
        interactionSource = remember { NoRippleInteractionSource() },
        onClick = onClick,
    ) {
        content()
    }
}

/**
 * Outlined buttons are medium-emphasis buttons. They contain actions that are important, but are
 * not the primary action in an app. Outlined buttons pair well with [ButtonMMD]s to indicate an
 * alternative, secondary action.
 *
 * Choose the best button for an action based on the amount of emphasis it needs. The more important
 * an action is, the higher emphasis its button should be.
 *
 * - See [ButtonMMD] for a high-emphasis button without a shadow, also known as a filled button.
 * - See [OutlinedButtonMMD] for a medium-emphasis button with a border.
 *
 * The default text style for internal [Text] components will be set to [Typography.labelLarge].
 *
 * @param onClick called when this button is clicked
 * @param modifier the [Modifier] to be applied to this button
 * @param enabled controls the enabled state of this button. When `false`, this component will not
 * respond to user input, and it will appear visually disabled and disabled to accessibility
 * services.
 * @param shape defines the shape of this button's container, border (when [border] is not null),
 * and shadow (when using [elevation]).
 * @param colors [ButtonColors] that will be used to resolve the colors for this button in different
 * states. See [ButtonDefaultsMMD.outlinedButtonColors].
 * @param border the border to draw around the container of this button. Pass `null` for no border.
 * @param contentPadding the spacing values to apply internally between the container and the
 * content
 */
@Composable
fun OutlinedButtonMMD(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaultsMMD.outlinedShape,
    colors: ButtonColors = ButtonDefaultsMMD.outlinedButtonColors(),
    border: BorderStroke? = ButtonDefaultsMMD.outlinedButtonBorder(enabled),
    contentPadding: PaddingValues = ButtonDefaultsMMD.contentPadding,
    content: @Composable RowScope.() -> Unit,
) = ButtonMMD(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    colors = colors,
    border = border,
    contentPadding = contentPadding,
    content = content,
)

object ButtonDefaultsMMD {
    val buttonHorizontalPadding = 16.dp
    val buttonVerticalPadding = 8.dp
    val buttonCornerRadius = 8.dp
    val borderWidth = 2.dp
    val borderColor1 = Color.Black
    val borderColor2 = Color.Black.copy(alpha = 0.75f)

    /**
     * The default content padding used by [ButtonMMD] and [OutlinedButton] buttons.
     */
    val contentPadding =
        PaddingValues(
            horizontal = buttonHorizontalPadding,
            vertical = buttonVerticalPadding,
        )

    /**
     * The default min width applied for all buttons. Note that you can override it by applying
     * Modifier.widthIn directly on the button composable.
     */
    val minWidth = 50.dp

    /**
     * The default min height applied for all buttons. Note that you can override it by applying
     * Modifier.heightIn directly on the button composable.
     */
    val minHeight = 32.dp

    /** Default shape for a button. */
    val shape: Shape @Composable get() = RoundedCornerShape(buttonCornerRadius)

    /** Default shape for an outlined button. */
    val outlinedShape: Shape @Composable get() = RoundedCornerShape(buttonCornerRadius)

    /** The default [BorderStroke] used by [OutlinedButton]. */
    @Composable
    fun outlinedButtonBorder(enabled: Boolean): BorderStroke =
        BorderStroke(width = borderWidth, color = if (enabled) borderColor1 else borderColor2)

    /**
     * The default [ButtonColors] used by [ButtonMMD].
     */
    @Composable
    fun buttonColors() = ButtonDefaults
        .buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer
                .copy(alpha = 0.75f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                .copy(alpha = 0.75f),
        )

    /**
     * The default [ButtonColors] used by [OutlinedButtonMMD].
     */
    @Composable
    fun outlinedButtonColors() = ButtonDefaults
        .buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.primaryContainer
                .copy(alpha = 0.75f),
        )
}

/**
 * A [MutableInteractionSource] that does not emit any interactions.
 */
private class NoRippleInteractionSource : MutableInteractionSource {
    override val interactions: Flow<Interaction> = emptyFlow()

    override suspend fun emit(interaction: Interaction) {
        // no-op
    }

    override fun tryEmit(interaction: Interaction) = true
}
