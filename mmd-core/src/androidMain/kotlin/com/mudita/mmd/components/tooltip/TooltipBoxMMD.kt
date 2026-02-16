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

package com.mudita.mmd.components.tooltip

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipScope
import androidx.compose.material3.TooltipState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mudita.mmd.components.tooltip.CaretDirectionMMD.Down

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TooltipBoxMMD(
    tooltip: @Composable TooltipScope.() -> Unit,
    modifier: Modifier = Modifier,
    focusable: Boolean = true,
    enableUserInput: Boolean = true,
    state: TooltipState = rememberTooltipState(),
    content: @Composable () -> Unit
) {
    val caretDirectionState = remember { mutableStateOf(Down) }
    val positionProvider =
        TooltipDefaultsMMD.rememberPlainTooltipPositionProvider { direction ->
            caretDirectionState.value = direction
        }

    TooltipBox(
        positionProvider = positionProvider,
        tooltip = {
            CompositionLocalProvider(
                TooltipDefaultsMMD.LocalCaretDirection provides caretDirectionState.value
            ) {
                tooltip()
            }
        },
        modifier = modifier,
        focusable = focusable,
        enableUserInput = enableUserInput,
        state = state,
        content = content
    )
}
