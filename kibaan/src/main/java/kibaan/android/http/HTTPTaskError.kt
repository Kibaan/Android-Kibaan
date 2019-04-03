package kibaan.android.http

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
                invalidURL -> "リクエストするURLが不正です。"
                network -> "通信エラーが発生しました。 通信環境が不安定か、接続先が誤っている可能性があります。"
                statusCode -> "HTTPステータスコードが不正です。"
                parse -> "レスポンスデータのパースに失敗しました。"
                invalidResponse -> "レスポンスデータのバリデーションエラーです。"
            }
        }
}
