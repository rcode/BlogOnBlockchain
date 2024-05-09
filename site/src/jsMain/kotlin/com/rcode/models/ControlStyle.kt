package com.rcode.models

sealed class ControlStyle(val style: String) {
    data class Bold(val selectedText: String?): ControlStyle(
        style = "<strong>$selectedText</strong>"
    )
    data class Italic (val selectedText: String?): ControlStyle(
        style = "<em>$selectedText</em>"
    )
    data class Link(
        val selectedText: String?,
        val href: String,
        val desc: String
    ): ControlStyle (
        style = "<a href=\"$href\" title=\"$desc\">$selectedText</a>"
    )
    data class Title(val selectedText: String?): ControlStyle(
        style = "<h1><strong>$selectedText</strong></h1>"
    )
    data class Subtitle(val selectedText: String?): ControlStyle(
        style = "<h3>$selectedText</h3>"
    )
    data class Quote(val selectedText: String?) : ControlStyle(
        style = "<div style=\"background-color:#FAFAFA;padding:12px;border-radius:6px;\"><em>❞ $selectedText</em></div>"
    )
    data class Code (val selectedText: String?) : ControlStyle(
        style = "<div style=\"background-color:#0d1117;padding:12px;border-radius:6px;\"><pre><code class=\"language-kotlin\"> $selectedText </code></pre></div>"
    )
    data class Break (val selectedText: String?) : ControlStyle(
        style = "$selectedText<br>"
    )
    data class Paragraph(val selectedText: String?): ControlStyle(
        style = "<p>$selectedText</p>"
    )
    data class BlockQuote(val selectedText: String?): ControlStyle(
        style = "<blockquote>$selectedText</blockquote>"
    )
    data class SimpleCode(val selectedText: String?): ControlStyle(
        style = "<code>$selectedText</code>"
    )
    data class List(val selectedText: String?): ControlStyle(
        style = "<ul><li>$selectedText</li></ul>"
    )
    data class OrderedList(val selectedText: String?): ControlStyle(
        style = "<ol><li>$selectedText</li></ol>"
    )
    data class Image(
        val selectedText: String?,
        val src: String,
        val desc: String
    ): ControlStyle (
        style = "<img src=\"$src\" alt=\"$desc\" style=\"max-width: 100%\">$selectedText</img>"
    )
    data class Underline(val selectedText: String?): ControlStyle(
        style = "<u>$selectedText</u>"
    )
    data class Strikethrough(val selectedText: String?): ControlStyle(
        style = "<strong>$selectedText</strong>"
    )
}