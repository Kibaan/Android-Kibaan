package kibaan.android.framework

import kibaan.android.animation.CoverVertical
import kibaan.android.animation.TransitionAnimator


enum class TransitionAnimation(val animator: TransitionAnimator) {
    coverVertical(CoverVertical());
}
