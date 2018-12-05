package kibaan.ui

import android.annotation.SuppressLint
import java.util.*
import kotlin.concurrent.schedule

/**
 * マルチタッチ・連打防止のための排他制御
 */
@SuppressLint("StaticFieldLeak")
object TouchLock {

    val canTouch: Boolean get() = !isLocked

    private var isLocked = false

    fun lock(duration: Long) {
        if (duration <= 0) return

        isLocked = true
        Timer().schedule(duration) {
            isLocked = false
        }
    }
}