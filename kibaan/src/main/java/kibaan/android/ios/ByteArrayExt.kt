package kibaan.android.ios

import android.util.Base64

enum class Base64EncodingOptions {
    lineLength76Characters,
    endLineWithLineFeed,
    ;
}

fun ByteArray.base64EncodedString(options: Array<Base64EncodingOptions> = arrayOf()): String {

    var flags = if (options.contains(Base64EncodingOptions.lineLength76Characters)) {
        Base64.DEFAULT
    } else {
        Base64.NO_WRAP
    }

    var suffix = "\n"
    if (!options.contains(Base64EncodingOptions.endLineWithLineFeed)) {
        flags = (flags or Base64.CRLF)
        suffix = "\r\n"
    }

    return Base64.encodeToString(this, flags).removeSuffix(suffix)
}

