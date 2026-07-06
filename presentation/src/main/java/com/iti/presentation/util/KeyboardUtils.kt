package com.iti.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
fun rememberSubmitAction(action: () -> Unit): () -> Unit {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    return {
        focusManager.clearFocus()
        keyboardController?.hide()
        action()
    }
}