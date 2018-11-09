package jp.co.altonotes.android.kibaan.http

import jp.co.altonotes.android.kibaan.ios.HTTPURLResponse
import jp.co.altonotes.android.kibaan.task.TaskHolder


typealias ErrorHandler<DataType> = (DataType?, HTTPDataTask.ErrorInfo) -> HTTPTask.ErrorHandlingStatus

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
            result = parseResponse(data)
        } catch (e: Exception) {
            handleError(HTTPTaskError.parse, result = null, response = response, data = data)
            return
        }

        if (isValidResponse(result)) {
            preProcessOnComplete(result)
            successHandler?.invoke(result)
            postProcessOnComplete(result)

            complete()
            next()
        } else {
            handleError(HTTPTaskError.invalidResponse, result = result, response = response, data = data)
        }
    }

    override fun handleConnectionError(type: HTTPTaskError, error: Exception?, response: HTTPURLResponse?, data: ByteArray?) {
        super.handleConnectionError(type, error = error, response = response, data = data)
        handleError(type, result = null, response = response, data = data)

        next()
    }

    open fun handleError(type: HTTPTaskError, result: DataType?, error: Exception? = null, response: HTTPURLResponse?, data: ByteArray?) {
        val errorInfo = ErrorInfo(error = error, response = response, data = data)

        if (errorHandler == null || errorHandler?.invoke(result, errorInfo) == ErrorHandlingStatus.handleError) {
            errorProcess(type, result = result, errorInfo = errorInfo)
        }

        this.error()
    }

    open fun errorProcess(type: HTTPTaskError, result: DataType?, errorInfo: ErrorInfo) {
        // Override
    }

    abstract fun parseResponse(data: ByteArray): DataType

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

    data class ErrorInfo(
            var error: Exception? = null,
            var response: HTTPURLResponse? = null,
            var data: ByteArray? = null)

}