package kibaan.android.task

import android.os.Handler
import kibaan.android.ios.TimeInterval
import kibaan.android.ios.removeAll

open class Task {

    /** タスクの所有者 */
    var owner: TaskHolder? = null
    /** タスクの一意キー */
    var key: String? = null
    /** タスクの監視者 */
    var observers: MutableList<TaskObserver> = mutableListOf()

    /** 後処理 */
    private var nextProcess: (() -> Unit)? = null
    /** 後処理のディレイ時間 */
    private var nextProcessDelaySec: TimeInterval = 0.0
    /** 後処理のタイマー */
    var nextProcessTimer: Handler? = null

    /** 完了処理 */
    var finishHandler: (() -> Unit)? = null

    constructor()

    constructor(owner: TaskHolder, key: String?) {
        this.owner = owner
        this.key = key
        @Suppress("LeakingThis")
        owner.add(this, key = key)
    }

    /**
     * 開始
     */
    open fun start() {}

    fun restart() {
        owner?.add(this, key)
        start()
    }

    open fun cancel() {
        nextProcessTimer?.removeCallbacks(nextProcessTimerTask)
        nextProcessTimer = null
        end()
    }

    fun onFinish(action: (() -> Unit)?) {
        finishHandler = action
    }

    fun setNextProcess(delaySec: TimeInterval, process: () -> Unit) {
        nextProcess = process
        nextProcessDelaySec = delaySec
    }

    open fun end() {
        observers.forEach {
            it.onEnd(task = this)
        }
        observers.removeAll()
        if (nextProcessTimer == null) {
            owner?.remove(task = this)
        }
    }

    fun error() {
        observers.forEach {
            it.onError(task = this)
        }
    }

    fun complete() {
        observers.forEach {
            it.onComplete(task = this)
        }
    }

    fun next() {
        if (nextProcess != null) {
            val delayMillis = (nextProcessDelaySec * 1000).toLong()
            nextProcessTimer = Handler()
            nextProcessTimer?.postDelayed(nextProcessTimerTask, delayMillis)
        }
    }

    private var nextProcessTimerTask = Runnable {
        owner?.remove(task = this)
        nextProcess?.invoke()
    }
}

interface TaskObserver {
    fun onComplete(task: Task)
    fun onError(task: Task)
    fun onEnd(task: Task)
}
