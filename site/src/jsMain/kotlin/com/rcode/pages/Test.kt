package com.rcode.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.core.Page
import org.jetbrains.compose.web.dom.Text

@Page
@Composable
fun TestPage() {
    // TODO: Replace the following with your own content
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("THIS IS A TEST PAGE")
    }
}