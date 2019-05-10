@file:Suppress("SpellCheckingInspection")

package kibaan.android.extension

fun Char.toHiragana(): Char {
    return if (this in '\u30A1'..'\u30F6') {
        this - 0x60
    } else {
        this
    }
}

fun Char.toKatakana(): Char {
    return if (this in '\u3041'..'\u3096') {
        this + 0x60
    } else {
        this
    }
}