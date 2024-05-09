package com.rcode.util

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

fun String.getHash(): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val hashBytes = messageDigest.digest(this.toByteArray(StandardCharsets.UTF_8))

    val hexString = StringBuffer()
    for(byte in hashBytes) {
        hexString.append(String.format("%02x", byte))
    }

    return hexString.toString().uppercase()
}