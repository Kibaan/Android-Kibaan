package kibaan.android.http

import kibaan.android.ios.Data
import kibaan.android.ios.HTTPURLResponse
import kibaan.android.task.TaskHolder


typealias ErrorHandler<DataType> = (DataType?, HTTPErrorInfo) -> HTTPTask.ErrorHandlingStatus

abstract class HTTPDataTask<DataType : Any> : HTTPTask {

    var successHandler: ((DataType) -> Unit)? = null
    var errorHandler: ErrorHandler<DataType>? = null

    constructor() : super()
    constructor(owner: TaskHolder, key: String?) : super(owner, key)

    /**
     * 完了処理を指定して処理を開始する
     */
    fun execute(onSuccess: ((DataType) -> Unit)? = null): HTTPDataTask<DataType> {
        successHandler = onSuccess
        start()
        return this
    }

    /**
     * エラー処理をセットする
     */
    fun onError(action: ErrorHandler<DataType>? = null): HTTPDataTask<DataType> {
        errorHandler = action
        return this
    }

    override fun handleResponseData(data: ByteArray, response: HTTPURLResponse) {
        super.handleResponseData(data, response = response)

        // データをパース
        val result: DataType
        try {
            result = parseResponse(data, response = response)
        } catch (e: Exception) {
            handler.post {
                handleError(HTTPTaskError.parse, result = null, error = e, response = response, data = data)
            }
            return
        }

        if (isValidResponse(result)) {
            handler.post {
                handleValidResponse(result = result)
            }
        } else {
            handler.post {
                handleError(HTTPTaskError.invalidResponse, result = result, response = response, data = data)
            }
        }
    }

    fun handleValidResponse(result: DataType) {
        preProcessOnComplete(result)
        successHandler?.invoke(result)
        postProcessOnComplete(result)
        complete()
        next()
    }


    override fun handleConnectionError(type: HTTPTaskError, error: Exception?, response: HTTPURLResponse?, data: ByteArray?) {
        super.handleConnectionError(type, error = error, response = response, data = data)
        handler.post {
            handleError(type, result = null, response = response, data = data)
            next()
        }
    }

    override fun statusCodeError(response: HTTPURLResponse?, data: Data?) {
        super.statusCodeError(response, data)
        handler.post {
            handleError(HTTPTaskError.statusCode, result = null, response = response, data = data)
            next()
        }
    }

    open fun handleError(type: HTTPTaskError, result: DataType?, error: Exception? = null, response: HTTPURLResponse?, data: ByteArray?) {
        val errorInfo = HTTPErrorInfo(error = error, response = response, data = data)

        if (errorHandler == null || errorHandler?.invoke(result, errorInfo) == ErrorHandlingStatus.handleError) {
            errorProcess(type, result = result, errorInfo = errorInfo)
        }

        this.error()
    }

    open fun errorProcess(type: HTTPTaskError, result: DataType?, errorInfo: HTTPErrorInfo) {
        // Override
    }

    abstract fun parseResponse(data: ByteArray, response: HTTPURLResponse): DataType

    /**
     * レスポンス内容が正常か判定する
     */
    open fun isValidResponse(response: DataType): Boolean = true

    /**
     * 共通前処理
     */
    open fun preProcessOnComplete(result: DataType) {
        // Override
    }

    /**
     * 共通後処理
     */
    open fun postProcessOnComplete(result: DataType) {
        // Override
    }

}