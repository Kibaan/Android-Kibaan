package kibaan.android.animation

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import kibaan.android.ios.isHidden
import kibaan.android.ios.superview

class FlipHorizontal(private val duration: Long = 200) : TransitionAnimator {

    override fun animateIn(view: View?, completion: (() -> Unit)?) {
        val targetView = view ?: return
        val parentView = targetView.superview ?: return
        targetView.isHidden = true
//        parentView.animate().scaleX(0f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).withEndAction {
//            targetView.isHidden = false
//            parentView.animate().scaleX(1f).setDuration(300).setInterpolator(DecelerateInterpolator()).start()
//        }

        parentView.animate().rotationY(90f).setDuration(300).setInterpolator(DecelerateInterpolator()).withEndAction {
            parentView.rotationY = 270f
            targetView.isHidden = false
            parentView.animate().rotationY(360f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }

    override fun animateOut(view: View?, completion: (() -> Unit)?) {
        val targetView = view ?: return
        val parentView = targetView.superview ?: return
//        parentView.animate().scaleX(0f).setDuration(300).setInterpolator(DecelerateInterpolator()).withEndAction {
//            targetView.isHidden = true
//            parentView.animate().scaleX(1f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).withEndAction(completion)
//        }

        parentView.animate().rotationY(270f).setDuration(300).setInterpolator(DecelerateInterpolator()).withEndAction {
            parentView.rotationY = 90f
            targetView.isHidden = true
            parentView.animate().rotationY(0f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }

}