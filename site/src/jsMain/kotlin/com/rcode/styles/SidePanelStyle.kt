package com.rcode.styles

import com.varabyte.kobweb.compose.css.CSSTransition
import com.varabyte.kobweb.compose.css.TransitionProperty
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.transition
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.rcode.ui.Theme
import com.rcode.util.Id
import org.jetbrains.compose.web.css.ms

val NavigationItemStyle by ComponentStyle {
    cssRule(" > #${Id.svgParent} > #${Id.vectorIcon}") {
        Modifier
            .transition(CSSTransition(property = TransitionProperty.All, duration = 300.ms))
            .styleModifier {
                property("stroke", Theme.White.hex)
            }
    }
    cssRule(":hover > #${Id.svgParent} > #${Id.vectorIcon}") {
        Modifier
            .transition(CSSTransition(property = TransitionProperty.All, duration = 300.ms))
            .styleModifier {
            property("stroke", Theme.Primary.hex)
        }
    }

    cssRule(" > #${Id.navigationText}") {
        Modifier.color(Theme.White.rgb)
    }
    cssRule(":hover > #${Id.navigationText}") {
        Modifier.color(Theme.Primary.rgb)
    }
}