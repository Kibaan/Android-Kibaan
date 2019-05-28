package kibaan.android.ios

class DispatchGroup {

    var waitingCount = 0
    var work: (() -> Unit)? = null
    var post: ((() -> Unit) -> Unit)? = null

    fun enter() {
        waitingCount++
    }

    var isChecking = false

    fun leave() {
        if (waitingCount == 0) {
            throw IllegalStateException("This DispatchGroup can't leave anymore.")
        }
        waitingCount--
        postCheck()
    }

    fun notify(post: (() -> Unit) -> Unit, work: () -> Unit) {
        this.work = work
        this.post = post
        postCheck()
    }

    private fun postCheck() {
        if (waitingCount == 0 && !isChecking) {
            isChecking = true

            val post = post
            if (post != null) {
                post.invoke {
                    checkAndCallback()
                    isChecking = false
                }
            } else {
                isChecking = false
            }
        }
    }

    private fun checkAndCallback() {
        if (waitingCount == 0) {
            work?.invoke()
        }
    }
}