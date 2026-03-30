package dev.sobhy.gameya.data.backup

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

internal fun sha256HexUtf8(text: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(text.toByteArray(StandardCharsets.UTF_8))
    return hash.joinToString("") { byte -> "%02x".format(byte) }
}
