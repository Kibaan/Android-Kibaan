package kibaan.android.ios

data class NSAttributedString(val string: String, val attributes: Map<Key, Any>) {
    /**
     * iOSにあるキーの一部のみサポート
     */
    enum class Key {
        font,
        foregroundColor,
        paragraphStyle,
        ;
    }
}

interface NSParagraphStyle {
    var alignment: NSTextAlignment
    var lineBreakMode: NSLineBreakMode
}

class NSMutableParagraphStyle: NSParagraphStyle {
    override var alignment: NSTextAlignment = NSTextAlignment.left
    override var lineBreakMode: NSLineBreakMode = NSLineBreakMode.byTruncatingTail
}

enum class NSLineBreakMode {
    byTruncatingTail,
    byWordWrapping,
    byClipping
    ;
}

enum class NSTextAlignment {
    left,
    right,
    center,
    ;
}