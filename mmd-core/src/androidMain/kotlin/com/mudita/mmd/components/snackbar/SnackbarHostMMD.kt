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

package com.mudita.mmd.components.snackbar

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.RecomposeScope
import androidx.compose.runtime.Stable
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AccessibilityManager
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

/**
 * State of the [SnackbarHostMMD], which controls the queue and the current [SnackbarMMD] being shown
 * inside the [SnackbarHostMMD].
 *
 * This state is usually [remember]ed and used to provide a [SnackbarHostMMD] to a [Scaffold].
 */
@Stable
class SnackbarHostStateMMD {

    /**
     * Only one [SnackbarMMD] can be shown at a time. Since a suspending Mutex is a fair queue, this
     * manages our message queue and we don't have to maintain one.
     */
    private val mutex = Mutex()

    /** The current [SnackbarDataMMD] being shown by the [SnackbarHostMMD], or `null` if none. */
    var currentSnackbarData by mutableStateOf<SnackbarDataMMD?>(null)
        private set

    /**
     * Shows or queues to be shown a [SnackbarMMD] at the bottom of the [Scaffold] to which this state
     * is attached and suspends until the snackbar has disappeared.
     *
     * [SnackbarHostStateMMD] guarantees to show at most one snackbar at a time. If this function is
     * called while another snackbar is already visible, it will be suspended until this snackbar is
     * shown and subsequently addressed. If the caller is cancelled, the snackbar will be removed
     * from display and/or the queue to be displayed.
     *
     * All of this allows for granular control over the snackbar queue from within:
     *
     * Example usage:
     *
     *```kotlin
     * @Preview
     * @Composable
     * fun ScaffoldWithCoroutinesSnackbar() {
     *     // decouple snackbar host state from scaffold state for demo purposes
     *     // this state, channel and flow is for demo purposes to demonstrate business logic layer
     *     val snackbarHostState = remember { SnackbarHostStateMMD() }
     *     // we allow only one snackbar to be in the queue here, hence conflated
     *     val channel = remember { Channel<Int>(Channel.CONFLATED) }
     *     LaunchedEffect(channel) {
     *         channel.receiveAsFlow().collect { index ->
     *             val result =
     *                 snackbarHostState.showSnackbar(
     *                     message = "Snackbar # $index",
     *                     actionLabel = "Action on $index"
     *                 )
     *             when (result) {
     *                 SnackbarResultMMD.ActionPerformed -> {
     *                     /* action has been performed */
     *                 }
     *                 SnackbarResultMMD.Dismissed -> {
     *                     /* dismissed, no action needed */
     *                 }
     *             }
     *         }
     *     }
     *     Scaffold(
     *         snackbarHost = { SnackbarHostMMD(snackbarHostState) },
     *         floatingActionButton = {
     *             var clickCount by remember { mutableStateOf(0) }
     *             FloatingActionButtonMMD(
     *                 onClick = {
     *                     // offset snackbar data to the business logic
     *                     channel.trySend(++clickCount)
     *                 }
     *             ) {
     *                 Text("Show snackbar")
     *             }
     *         },
     *         content = { innerPadding ->
     *             Text(
     *                 "Snackbar demo",
     *                 modifier = Modifier.padding(innerPadding).fillMaxSize().wrapContentSize()
     *             )
     *         }
     *     )
     * }
     *```
     * To change the Snackbar appearance, change it in 'snackbarHost' on the [Scaffold].
     *
     * @param message text to be shown in the SnackbarMMD
     * @param actionLabel optional action label to show as button in the SnackbarMMD
     * @param withDismissAction a boolean to show a dismiss action in the SnackbarMMD. This is
     *   recommended to be set to true for better accessibility when a SnackbarMMD is set with a
     *   [SnackbarDuration.Indefinite]
     * @param duration duration to control how long snackbar will be shown in [SnackbarHostMMD], either
     *   [SnackbarDurationMMD.Short], [SnackbarDurationMMD.Long] or [SnackbarDurationMMD.Indefinite].
     * @return [SnackbarResultMMD.ActionPerformed] if option action has been clicked or
     *   [SnackbarResultMMD.Dismissed] if snackbar has been dismissed via timeout or by the user
     */
    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDurationMMD =
            if (actionLabel == null) SnackbarDurationMMD.Short else SnackbarDurationMMD.Indefinite
    ): SnackbarResultMMD =
        showSnackbar(SnackbarVisualsImpl(message, actionLabel, withDismissAction, duration))

    /**
     * Shows or queues to be shown a [SnackbarMMD] at the bottom of the [Scaffold] to which this state
     * is attached and suspends until the snackbar has disappeared.
     *
     * [SnackbarHostStateMMD] guarantees to show at most one snackbar at a time. If this function is
     * called while another snackbar is already visible, it will be suspended until this snackbar is
     * shown and subsequently addressed. If the caller is cancelled, the snackbar will be removed
     * from display and/or the queue to be displayed.
     *
     * All of this allows for granular control over the snackbar queue from within:
     *
     * ```
     * @Preview
     * @Composable
     * fun ScaffoldWithCustomSnackbar() {
     *     class SnackbarVisualsWithError(override val message: String, val isError: Boolean) :
     *         SnackbarVisualsMMD {
     *         override val actionLabel: String
     *             get() = if (isError) "Error" else "OK"
     *
     *         override val withDismissAction: Boolean
     *             get() = false
     *
     *         override val duration: SnackbarDurationMMD
     *             get() = SnackbarDurationMMD.Indefinite
     *     }
     *
     *     val snackbarHostState = remember { SnackbarHostStateMMD() }
     *     val scope = rememberCoroutineScope()
     *     Scaffold(
     *         snackbarHost = {
     *             // reuse default SnackbarHost to have default animation and timing handling
     *             SnackbarHostMMD(snackbarHostState) { data ->
     *                 // custom snackbar with the custom action button color and border
     *                 val isError = (data.visuals as? SnackbarVisualsWithError)?.isError ?: false
     *                 val buttonColor =
     *                     if (isError) {
     *                         ButtonDefaults.textButtonColors(
     *                             containerColor = MaterialTheme.colorScheme.errorContainer,
     *                             contentColor = MaterialTheme.colorScheme.error
     *                         )
     *                     } else {
     *                         ButtonDefaults.textButtonColors(
     *                             contentColor = MaterialTheme.colorScheme.inversePrimary
     *                         )
     *                     }
     *
     *                 SnackbarMMD(
     *                     modifier =
     *                     Modifier.border(2.dp, MaterialTheme.colorScheme.secondary).padding(12.dp),
     *                     action = {
     *                         TextButton(
     *                             onClick = { if (isError) data.dismiss() else data.performAction() },
     *                             colors = buttonColor
     *                         ) {
     *                             Text(data.visuals.actionLabel ?: "")
     *                         }
     *                     }
     *                 ) {
     *                     Text(data.visuals.message)
     *                 }
     *             }
     *         },
     *         floatingActionButton = {
     *             var clickCount by remember { mutableStateOf(0) }
     *             FloatingActionButtonMMD(
     *                 onClick = {
     *                     scope.launch {
     *                         snackbarHostState.showSnackbar(
     *                             SnackbarVisualsWithError(
     *                                 "Snackbar # ${++clickCount}",
     *                                 isError = clickCount % 2 != 0
     *                             )
     *                         )
     *                     }
     *                 }
     *             ) {
     *                 Text("Show snackbar")
     *             }
     *         },
     *         content = { innerPadding ->
     *             Text(
     *                 text = "Custom Snackbar Demo",
     *                 modifier = Modifier.padding(innerPadding).fillMaxSize().wrapContentSize()
     *             )
     *         }
     *     )
     *```
     * @param visuals [SnackbarVisualsMMD] that are used to create a SnackbarMMD
     * @return [SnackbarResultMMD.ActionPerformed] if option action has been clicked or
     *   [SnackbarResultMMD.Dismissed] if snackbar has been dismissed via timeout or by the user
     */
    suspend fun showSnackbar(visuals: SnackbarVisualsMMD): SnackbarResultMMD =
        mutex.withLock {
            try {
                return suspendCancellableCoroutine { continuation ->
                    currentSnackbarData = SnackbarDataImpl(visuals, continuation)
                }
            } finally {
                currentSnackbarData = null
            }
        }

    private class SnackbarVisualsImpl(
        override val message: String,
        override val actionLabel: String?,
        override val withDismissAction: Boolean,
        override val duration: SnackbarDurationMMD
    ) : SnackbarVisualsMMD {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as SnackbarVisualsImpl

            if (message != other.message) return false
            if (actionLabel != other.actionLabel) return false
            if (withDismissAction != other.withDismissAction) return false
            if (duration != other.duration) return false

            return true
        }

        override fun hashCode(): Int {
            var result = message.hashCode()
            result = 31 * result + actionLabel.hashCode()
            result = 31 * result + withDismissAction.hashCode()
            result = 31 * result + duration.hashCode()
            return result
        }
    }

    private class SnackbarDataImpl(
        override val visuals: SnackbarVisualsMMD,
        private val continuation: CancellableContinuation<SnackbarResultMMD>
    ) : SnackbarDataMMD {

        override fun performAction() {
            if (continuation.isActive) continuation.resume(SnackbarResultMMD.ActionPerformed)
        }

        override fun dismiss() {
            if (continuation.isActive) continuation.resume(SnackbarResultMMD.Dismissed)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as SnackbarDataImpl

            if (visuals != other.visuals) return false
            if (continuation != other.continuation) return false

            return true
        }

        override fun hashCode(): Int {
            var result = visuals.hashCode()
            result = 31 * result + continuation.hashCode()
            return result
        }
    }
}

/**
 * Host for [SnackbarMMD]s to be used in [Scaffold] to properly show, hide and dismiss items based on
 * Material specification and the [hostState].
 *
 * This component with default parameters comes build-in with [Scaffold], if you need to show a
 * default [SnackbarMMD], use [SnackbarHostStateMMD.showSnackbar].
 *
 * ```
 * @Preview
 * @Composable
 * fun ScaffoldWithSimpleSnackbar() {
 *     val snackbarHostState = remember { SnackbarHostStateMMD() }
 *     val scope = rememberCoroutineScope()
 *     Scaffold(
 *         snackbarHost = { SnackbarHostMMD(snackbarHostState) },
 *         floatingActionButton = {
 *             var clickCount by remember { mutableStateOf(0) }
 *             FloatingActionButtonMMD(
 *                 onClick = {
 *                     // show snackbar as a suspend function
 *                     scope.launch { snackbarHostState.showSnackbar("Snackbar # ${++clickCount}") }
 *                 }
 *             ) {
 *                 Text("Show snackbar")
 *             }
 *         },
 *         content = { innerPadding ->
 *             Text(
 *                 text = "Body content",
 *                 modifier = Modifier.padding(innerPadding).fillMaxSize().wrapContentSize()
 *             )
 *         }
 *     )
 * }
 * ```
 *
 * If you want to customize appearance of the [SnackbarMMD], you can pass your own version as a child
 * of the [SnackbarHostMMD] to the [Scaffold]:
 *
 *
 * ```
 * @Preview
 * @Composable
 * fun ScaffoldWithCustomSnackbar() {
 *     class SnackbarVisualsWithError(override val message: String, val isError: Boolean) :
 *         SnackbarVisualsMMD {
 *         override val actionLabel: String
 *             get() = if (isError) "Error" else "OK"
 *
 *         override val withDismissAction: Boolean
 *             get() = false
 *
 *         override val duration: SnackbarDurationMMD
 *             get() = SnackbarDurationMMD.Indefinite
 *     }
 *
 *     val snackbarHostState = remember { SnackbarHostStateMMD() }
 *     val scope = rememberCoroutineScope()
 *     Scaffold(
 *         snackbarHost = {
 *             // reuse default SnackbarHost to have default animation and timing handling
 *             SnackbarHostMMD(snackbarHostState) { data ->
 *                 // custom snackbar with the custom action button color and border
 *                 val isError = (data.visuals as? SnackbarVisualsWithError)?.isError ?: false
 *                 val buttonColor =
 *                     if (isError) {
 *                         ButtonDefaults.textButtonColors(
 *                             containerColor = MaterialTheme.colorScheme.errorContainer,
 *                             contentColor = MaterialTheme.colorScheme.error
 *                         )
 *                     } else {
 *                         ButtonDefaults.textButtonColors(
 *                             contentColor = MaterialTheme.colorScheme.inversePrimary
 *                         )
 *                     }
 *
 *                 SnackbarMMD(
 *                     modifier =
 *                     Modifier.border(2.dp, MaterialTheme.colorScheme.secondary).padding(12.dp),
 *                     action = {
 *                         TextButton(
 *                             onClick = { if (isError) data.dismiss() else data.performAction() },
 *                             colors = buttonColor
 *                         ) {
 *                             Text(data.visuals.actionLabel ?: "")
 *                         }
 *                     }
 *                 ) {
 *                     Text(data.visuals.message)
 *                 }
 *             }
 *         },
 *         floatingActionButton = {
 *             var clickCount by remember { mutableStateOf(0) }
 *             FloatingActionButtonMMD(
 *                 onClick = {
 *                     scope.launch {
 *                         snackbarHostState.showSnackbar(
 *                             SnackbarVisualsWithError(
 *                                 "Snackbar # ${++clickCount}",
 *                                 isError = clickCount % 2 != 0
 *                             )
 *                         )
 *                     }
 *                 }
 *             ) {
 *                 Text("Show snackbar")
 *             }
 *         },
 *         content = { innerPadding ->
 *             Text(
 *                 text = "Custom Snackbar Demo",
 *                 modifier = Modifier.padding(innerPadding).fillMaxSize().wrapContentSize()
 *             )
 *         }
 *     )
 * }
 *```
 *
 * @param hostState state of this component to read and show [SnackbarMMD]s accordingly
 * @param modifier the [Modifier] to be applied to this component
 * @param snackbar the instance of the [SnackbarMMD] to be shown at the appropriate time with
 *   appearance based on the [SnackbarDataMMD] provided as a param
 */
@Composable
fun SnackbarHostMMD(
    hostState: SnackbarHostStateMMD,
    modifier: Modifier = Modifier,
    snackbar: @Composable (SnackbarDataMMD) -> Unit = { SnackbarMMD(it) }
) {
    val currentSnackbarData = hostState.currentSnackbarData
    val accessibilityManager = LocalAccessibilityManager.current
    LaunchedEffect(currentSnackbarData) {
        if (currentSnackbarData != null) {
            val duration =
                currentSnackbarData.visuals.duration.toMillis(
                    currentSnackbarData.visuals.actionLabel != null,
                    accessibilityManager
                )
            delay(duration)
            currentSnackbarData.dismiss()
        }
    }
    SnackbarAppearance(
        current = hostState.currentSnackbarData,
        modifier = modifier,
        content = snackbar
    )
}

/**
 * Interface to represent the visuals of one particular [SnackbarMMD] as a piece of the [SnackbarDataMMD].
 *
 * @property message text to be shown in the SnackbarMMD
 * @property actionLabel optional action label to show as button in the SnackbarMMD
 * @property withDismissAction a boolean to show a dismiss action in the SnackbarMMD. This is
 *   recommended to be set to true better accessibility when a SnackbarMMD is set with a
 *   [SnackbarDurationMMD.Indefinite]
 * @property duration duration of the SnackbarMMD
 */
@Stable
interface SnackbarVisualsMMD {
    val message: String
    val actionLabel: String?
    val withDismissAction: Boolean
    val duration: SnackbarDurationMMD
}

/**
 * Interface to represent the data of one particular [SnackbarMMD] as a piece of the
 * [SnackbarHostStateMMD].
 *
 * @property visuals Holds the visual representation for a particular [SnackbarMMD].
 */
@Stable
interface SnackbarDataMMD {
    val visuals: SnackbarVisualsMMD

    /** Function to be called when SnackbarMMD action has been performed to notify the listeners. */
    fun performAction()

    /** Function to be called when SnackbarMMD is dismissed either by timeout or by the user. */
    fun dismiss()
}

/** Possible results of the [SnackbarHostStateMMD.showSnackbar] call */
enum class SnackbarResultMMD {
    /** [SnackbarMMD] that is shown has been dismissed either by timeout of by user */
    Dismissed,

    /** Action on the [SnackbarMMD] has been clicked before the time out passed */
    ActionPerformed,
}

/** Possible durations of the [SnackbarMMD] in [SnackbarHostMMD] */
enum class SnackbarDurationMMD {
    /** Show the Snackbar for a short period of time */
    Short,

    /** Show the Snackbar for a long period of time */
    Long,

    /** Show the Snackbar indefinitely until explicitly dismissed or action is clicked */
    Indefinite
}

// TODO: magic numbers adjustment
internal fun SnackbarDurationMMD.toMillis(
    hasAction: Boolean,
    accessibilityManager: AccessibilityManager?
): Long {
    val original =
        when (this) {
            SnackbarDurationMMD.Indefinite -> Long.MAX_VALUE
            SnackbarDurationMMD.Long -> 10000L
            SnackbarDurationMMD.Short -> 4000L
        }
    if (accessibilityManager == null) {
        return original
    }
    return accessibilityManager.calculateRecommendedTimeoutMillis(
        original,
        containsIcons = true,
        containsText = true,
        containsControls = hasAction
    )
}

@Composable
private fun SnackbarAppearance(
    current: SnackbarDataMMD?,
    modifier: Modifier = Modifier,
    content: @Composable (SnackbarDataMMD) -> Unit
) {
    val state = remember { VisibilityState<SnackbarDataMMD?>() }
    if (current != state.current) {
        state.current = current
        state.items.clear()
        if (current != null) {
            state.items.add(current)
        }
    }
    Box(modifier) {
        state.scope = currentRecomposeScope
        state.items.fastForEach { item -> key(item) { content(item!!) } }
    }
}

private class VisibilityState<T> {
    var current: T? = null
    var items = mutableListOf<T?>()
    var scope: RecomposeScope? = null
}
