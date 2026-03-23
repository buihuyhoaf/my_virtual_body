package com.hoabui.virtualbody3d.ui.common_ui.organism.scaffold

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.ui.common_ui.molecule.topbar.GTopBar
import com.hoabui.virtualbody3d.ui.common_ui.molecule.topbar.GTopBarBackIcon
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

// ─────────────────────────────────────────────────────────────────────────────
// GScaffold
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Layout shell organism for the Gym design system.
 *
 * A thin, opinionated wrapper around M3's [Scaffold] that enforces two invariants
 * across every screen in the app:
 *
 * ### 1. Window insets — centralised
 * `contentWindowInsets` defaults to [WindowInsets.safeDrawing], which covers the
 * status bar, navigation bar, and display cutouts in a single declaration.
 * Previously each screen independently used `WindowInsets.safeDrawing`,
 * `WindowInsets.ime`, or nothing — leading to silent layout bugs.
 *
 * ### 2. Background colour — always on-brand
 * `containerColor` defaults to `GymTheme.token.colors.background` so no screen
 * accidentally inherits M3's default white or surface colour.
 *
 * ### Interaction with [GTopBar]
 * When `GTopBar` is provided via the [topBar] slot, pass
 * `windowInsets = WindowInsets(0)` to the bar to avoid double status-bar padding:
 * ```kotlin
 * GScaffold(
 *     topBar = {
 *         GTopBar(
 *             title = "My Screen",
 *             windowInsets = WindowInsets(0),   // GScaffold already consumes safeDrawing
 *             navigationIcon = { GTopBarBackIcon(onBack = onNavigateUp) },
 *         )
 *     }
 * ) { padding ->
 *     ScreenContent(Modifier.padding(padding))
 * }
 * ```
 *
 * ### All other slots
 * All M3 `Scaffold` slots are forwarded unchanged: [topBar], [bottomBar],
 * [floatingActionButton], [floatingActionButtonPosition], [snackbarHost], and [content].
 *
 * @param containerColor Screen background colour.
 *   Defaults to `GymTheme.token.colors.background`.
 * @param contentWindowInsets Window insets consumed and converted to padding for [content].
 *   Defaults to [WindowInsets.safeDrawing].
 * @param content Screen content lambda. Receives [PaddingValues] that include
 *   insets + top/bottom bar heights; apply via `Modifier.padding(paddingValues)`.
 */
@Composable
fun GScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    snackbarHost: @Composable () -> Unit = {},
    containerColor: Color = GymTheme.token.colors.background,
    contentWindowInsets: WindowInsets = WindowInsets.safeDrawing,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        snackbarHost = snackbarHost,
        containerColor = containerColor,
        // contentColor is derived from containerColor by M3 automatically
        contentWindowInsets = contentWindowInsets,
        content = content,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "GScaffold — With GTopBar")
@Composable
private fun PreviewWithTopBar() {
    GymTheme {
        val token = GymTheme.token
        GScaffold(
            topBar = {
                GTopBar(
                    title = "Exercise Library",
                    // windowInsets = WindowInsets(0) would be used in production
                    // so GScaffold's safeDrawing doesn't double-pad.
                    // Using default here for preview isolation.
                    navigationIcon = { GTopBarBackIcon(onBack = {}) },
                )
            },
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                GText(
                    text = "Screen content here",
                    color = token.colors.textSecondary,
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "GScaffold — With FAB")
@Composable
private fun PreviewWithFab() {
    GymTheme {
        val token = GymTheme.token
        GScaffold(
            topBar = {
                GTopBar(title = "Workout Feed")
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {},
                    containerColor = token.colors.primary,
                    contentColor = token.colors.onPrimary,
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add workout")
                }
            },
            floatingActionButtonPosition = FabPosition.End,
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                GText(text = "Content with FAB", color = token.colors.textSecondary)
            }
        }
    }
}

@Preview(showBackground = true, name = "GScaffold — With SnackbarHost")
@Composable
private fun PreviewWithSnackbar() {
    GymTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val token = GymTheme.token
        GScaffold(
            topBar = { GTopBar(title = "Profile") },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                GText(text = "Content with Snackbar host", color = token.colors.textSecondary)
            }
        }
    }
}

@Preview(
    showBackground = true,
    name = "GScaffold — Dark, no top bar",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewDarkNoTopBar() {
    GymTheme(darkTheme = true) {
        val token = GymTheme.token
        GScaffold {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                GText(
                    text = "Dark scaffold — background from token",
                    color = token.colors.textSecondary,
                )
            }
        }
    }
}
