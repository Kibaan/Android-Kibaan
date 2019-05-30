package kibaan.android.ios

class NSAttributedString {
    enum class Key {
        font,
        foregroundColor,
        paragraphStyle,
        ;
    }
}

interface NSParagraphStyle {
    var alignment: NSTextAlignment
    var lineBreakMode: LineBreakMode
}

class NSMutableParagraphStyle: NSParagraphStyle {
    override var alignment: NSTextAlignment = NSTextAlignment.left
    override var lineBreakMode: LineBreakMode = LineBreakMode.byTruncatingTail
}

enum class LineBreakMode {
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