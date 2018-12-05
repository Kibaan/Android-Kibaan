package kibaan.task

import android.os.Handler
import kibaan.ios.append
import kibaan.ios.removeAll


/**
 * 複数タスクの完了待ちのためのクラス
 */
class TaskSet : TaskObserver {

    private var tasks: MutableList<Task> = mutableListOf()

    private var onAllCompleted: (() -> Unit)? = null
    private var onAnyError: (() -> Unit)? = null

    private val lockQueue = Handler()

    private var hasError = false

    fun add(task: Task) {
        tasks.append(task)
        task.observers.append(this)
    }

    fun cancelAll() {
        tasks.forEach { it.cancel() }
    }

    fun clearAll() {
        cancelAll()
        tasks.removeAll()
    }

    fun allCompleted(onAllCompleted: () -> Unit) {
        this.onAllCompleted = onAllCompleted
    }

    fun anyError(onAnyError: () -> Unit) {
        this.onAnyError = onAnyError
    }

    override fun onComplete(task: Task) {
        // マルチスレッドで呼ばれる可能性があるため単一スレッドで同期する必要がある
        lockQueue.post {
            tasks.remove(element = task)
            if (tasks.size == 0 && !hasError) {
                onAllCompleted?.invoke()
            }
        }
    }

    override fun onError(task: Task) {
        // マルチスレッドで呼ばれる可能性があるため単一スレッドで同期する必要がある
        lockQueue.post {
            tasks.remove(element = task)
            if (!hasError) {
                hasError = true
                onAnyError?.invoke()
            }
        }
    }

    override fun onEnd(task: Task) {}

}