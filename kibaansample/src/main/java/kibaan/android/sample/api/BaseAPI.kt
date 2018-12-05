package kibaan.android.sample.api

import com.google.gson.Gson
import kibaan.android.AndroidUnique
import kibaan.android.http.HTTPDataTask
import kibaan.android.http.HTTPTaskError
import kibaan.android.ios.HTTPURLResponse
import kibaan.android.task.TaskHolder
import java.nio.charset.Charset
import kotlin.reflect.KClass

abstract class BaseAPI<DataType : Any> : HTTPDataTask<DataType> {

    // region -> Variables

    abstract val resultClass: KClass<DataType>

    @AndroidUnique
    val encoding: Charset = Charset.forName("UTF-8")

    override val baseURL: String get() = "https://jsonplaceholder.typicode.com/"
    override val httpMethod: String get() = "GET"
    override val headers: Map<String, String> get() = mapOf("Content-type" to "application/json;charset=UTF-8")
    open var autoRefreshInterval: Double = 2.0

    // endregion

    // region -> Constructor

    constructor() : super()
    constructor(owner: TaskHolder, key: String?) : super(owner, key)

    // endregion

    // region -> Functions

    fun setAutoRefresh(onRefresh: () -> Unit) {
        if (owner == null) {
            throw AssertionError("when you set auto refresh, you must set owner property to the api.")
        }
        setNextProcess(delaySec = autoRefreshInterval) {
            onRefresh()
        }
    }

    override fun parseResponse(data: ByteArray, response: HTTPURLResponse): DataType {
        try {
            val jsonString = String(bytes = data, charset = encoding)
            return Gson().fromJson(jsonString, resultClass.java)
        } catch (e: Exception) {
            throw HTTPTaskError.parse
        }
    }

    // endregion
}