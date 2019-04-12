package kibaan.android.ios

class DispatchGroup {

    var waitingCount = 0
    var work: (() -> Unit)? = null

    fun enter() {
        waitingCount++
    }

    fun leave() {
        waitingCount--
        if (waitingCount == 0) {
            work?.invoke()
        }
    }

    fun notify(queue: DispatchQueue, work: () -> Unit) {
        this.work = work
    }
}

// iOSと合わせるために作成するが、現状使わない
class DispatchQueue {
    companion object {
        val main = DispatchQueue()
    }
}