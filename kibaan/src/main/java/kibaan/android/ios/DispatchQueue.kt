package kibaan.android.ios

import android.os.Handler
import android.os.Looper

class DispatchQueue(private val looper: Looper) {

    companion object {
        val main = DispatchQueue(looper = Looper.getMainLooper())
    }

    fun async(execute: () -> Unit) = Handler(looper).post(execute)

    fun asyncAfter(deadline: Double, execute: () -> Unit) =
        Handler(looper).postDelayed(execute, (deadline * 1000).toLong())
}