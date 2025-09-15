package com.mudita.mmdtest.components.search

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * A [WindowInsets] whose values can change without changing the instance. This is useful to avoid
 * recomposition when [WindowInsets] can change.
 *
 * Copied from [androidx.compose.foundation.layout.MutableWindowInsets], which is marked as
 * experimental and thus cannot be used cross-module.
 */
internal class MutableWindowInsets(initialInsets: WindowInsets = WindowInsets(0, 0, 0, 0)) :
    WindowInsets {
    /**
     * The [WindowInsets] that are used for [left][getLeft], [top][getTop], [right][getRight], and
     * [bottom][getBottom] values.
     */
    var insets by mutableStateOf(initialInsets)

    override fun getLeft(density: Density, layoutDirection: LayoutDirection): Int =
        insets.getLeft(density, layoutDirection)

    override fun getTop(density: Density): Int = insets.getTop(density)

    override fun getRight(density: Density, layoutDirection: LayoutDirection): Int =
        insets.getRight(density, layoutDirection)

    override fun getBottom(density: Density): Int = insets.getBottom(density)
}
