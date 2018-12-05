package kibaan.http

class Request(val url: String) {
    var httpMethod = "GET"
    var body: ByteArray? = null
    var headers = mutableMapOf<String, String>()
}