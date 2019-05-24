package kibaan.android.animation

import android.view.View
import android.view.animation.DecelerateInterpolator
import kibaan.android.extension.height
import kibaan.android.ios.frame
import kibaan.android.ios.superview

class CoverVertical(private val duration: Long = 320) : TransitionAnimator {

    override fun animateIn(view: View?, completion: (() -> Unit)?) {
        val targetView = view ?: return
        val viewHeight = targetView.superview?.height ?: return
        view.translationY = viewHeight.toFloat()
        view.animate()
            .setDuration(duration)
            .translationY(0.0f)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction(completion).start()
    }

    override fun animateOut(view: View?, completion: (() -> Unit)?) {
        val viewHeight = view?.height ?: return
        view.animate()
            .setDuration(duration)
            .translationY(viewHeight.toFloat())
            .setInterpolator(DecelerateInterpolator())
            .withEndAction(completion)
    }
}