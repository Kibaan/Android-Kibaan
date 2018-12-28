package kibaan.android.valueobject

data class KeyValue(
    var key: String,
    var value: String? = null) {

    val stringValue: String
        get() {
            return if (value != null) {
                "$key=$value"
            } else {
                key
            }
        }
}