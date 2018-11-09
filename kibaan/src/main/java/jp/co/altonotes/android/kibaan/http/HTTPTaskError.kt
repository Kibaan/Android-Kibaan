package jp.co.altonotes.android.kibaan.http

/**
 * HTTP通信のエラー種別
 */
sealed class HTTPTaskError : Exception() {
    /** URL不正 */
    object invalidURL : HTTPTaskError()

    /** オフライン、タイムアウトなどの通信エラー */
    object network : HTTPTaskError()

    /** HTTPステータスコードが既定ではないエラー */
    object statusCode : HTTPTaskError()

    /** レスポンスのパースに失敗 */
    object parse : HTTPTaskError()

    /** レスポンスは取得できたが内容がエラー */
    object invalidResponse : HTTPTaskError()

    val description: String
        get() {
            return when (this) {
                invalidURL -> "URLが不正です"
                network -> "オフライン、タイムアウトなどの通信エラー"
                statusCode -> "HTTPステータスコードが既定ではないエラー"
                parse -> "レスポンスデータのパースに失敗"
                invalidResponse -> "レスポンスデータの内容がエラー"
            }
        }
}
