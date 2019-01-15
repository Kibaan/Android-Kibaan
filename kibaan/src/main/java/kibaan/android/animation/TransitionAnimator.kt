package kibaan.android.animation

import android.view.View

interface TransitionAnimator {

    fun animateIn(view: View?, completion: (() -> Unit)? = null)

    fun animateOut(view: View?, completion: (() -> Unit)? = null)
}