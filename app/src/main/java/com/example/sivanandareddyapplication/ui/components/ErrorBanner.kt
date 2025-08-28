
package com.example.sivanandareddyapplication.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun ErrorBanner(message: String?, onDismiss: () -> Unit) {
    if (message != null) {
        Snackbar(action = { TextButton(onClick = onDismiss) { Text("Dismiss") } }) {
            Text(message)
        }
    }
}
