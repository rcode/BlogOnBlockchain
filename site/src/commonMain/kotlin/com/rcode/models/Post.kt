package com.rcode.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.reflect.KProperty

@Serializable
data class Post(
    @SerialName("_id")
    val _id: String = "",
    val author: String = "",
    val date: Long = 0L,
    val title: String,
    val subtitle: String,
    val thumbnail: String,
    val content: String,
    val category: Category,
    val popular: Boolean = false,
    val main: Boolean = false,
    val sponsored: Boolean = false
)

@Serializable
data class PostWithoutDetails(
    @SerialName("_id")
    val _id: String = "",
    val author: String,
    val date: Long,
    val title: String,
    val subtitle: String,
    val thumbnail: String,
    val category: Category,
    val popular: Boolean = false,
    val main: Boolean = false,
    val sponsored: Boolean = false
)

operator fun PostWithoutDetails.getValue(value: Nothing?, kProperty1: KProperty<*>): PostWithoutDetails {
    return this
}