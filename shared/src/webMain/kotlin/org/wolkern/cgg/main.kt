package org.wolkern.cgg

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // On initialise le stockage Wasm (le path n'a pas d'importance pour le localStorage)
    val storage = KeyValueStorage(directoryPath = "")

    // On cible directement le body du fichier index.html
    ComposeViewport(viewportContainer = document.body!!) {
        App(storage = storage)
    }
}