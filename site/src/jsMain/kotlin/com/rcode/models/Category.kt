@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.rcode.models

import com.rcode.ui.Theme
import kotlinx.serialization.Serializable

@Serializable
actual enum class Category(val color: String) {
    Technology(color = Theme.Green.hex),
    Programming(color = Theme.Yellow.hex),
    Design(color = Theme.Purple.hex)
}