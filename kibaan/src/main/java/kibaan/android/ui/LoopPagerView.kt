package kibaan.android.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager
import kibaan.android.ios.isHidden
import kibaan.android.ios.removeFromSuperview
import kibaan.android.ios.removeLast
import kibaan.android.ios.safeGet

/**
 * ViewPagerを用いてViewをループさせる
 * Created by yamamoto on 2018/05/17.
 */
open class LoopPagerView : ViewPager {

    private val TAG = javaClass.simpleName

    private val pageViews: MutableList<View> = mutableListOf()
    private val pageFrames: MutableList<FrameLayout> = mutableListOf()

    var pageChangeAction: ((Int, Int) -> Unit)? = null

    var loadedPagePosition: Int? = null

    var isScrollEnabled = true

    var onTouchEvent: ((MotionEvent?) -> Boolean)? = null

    var pageIndex: Int
        get() = toPagePosition(currentItem)
        set(value) {
            changePage(value, animated = false)
        }
    var oldPageIndex = 0

    constructor(context: Context) : super(context) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize(context)
    }

    private fun initialize(context: Context) {
        addOnPageChangeListener(PageChangeListener())

        // 実ページ数 + 余白2ページが必要なので余白分のページ枠を追加
        addPageFrame()
        addPageFrame()

        adapter = Adapter()

        currentItem = 1
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (isScrollEnabled) {
            try {
                return super.onInterceptTouchEvent(ev)
            } catch (e: Exception) {
                // マルチタッチで連打すると"pointerIndex out of range"でクラッシュする場合がある
                e.printStackTrace()
            }
        }
        return false
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (isScrollEnabled) {
            onTouchEvent?.invoke(ev)
            return super.onTouchEvent(ev)
        }
        return false
    }

    /**
     * ページ枠を追加
     */
    private fun addPageFrame() {
        val frame = FrameLayout(context)
        frame.layoutParams =
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        pageFrames.add(frame)
    }

    fun addPageView(view: View) {
        pageViews += view

        addPageFrame()
        adapter?.notifyDataSetChanged()
    }

    fun changePage(pageIndex: Int, animated: Boolean = false) {
        val position = toRealPosition(pageIndex)

        if (currentItem == position) {
            pageChangeAction?.invoke(oldPageIndex, pageIndex)
        }
        setCurrentItem(position, animated)
    }

    fun next(animated: Boolean = true) {
        var index = pageIndex + 1
        if (pageViews.size - 1 < index) {
            index = 0
        }
        changePage(pageIndex = index, animated = animated)
    }

    fun back(animated: Boolean = true) {
        var index = pageIndex - 1
        if (index < 0) {
            index = pageViews.size - 1
        }
        changePage(pageIndex = index, animated = animated)
    }

    fun clear() {
        pageViews.forEach {
            it.removeFromSuperview()
            pageFrames.removeAt(pageFrames.size - 1)
        }
        pageViews.clear()
        adapter?.notifyDataSetChanged()
    }

    private fun toRealPosition(position: Int): Int {
        return position + 1
    }

    private fun toPagePosition(position: Int): Int {
        var position = position - 1
        if (position == -1) {
            position = pageViews.lastIndex
        } else if (position == pageViews.lastIndex + 1) {
            position = 0
        }
        return position
    }

    private fun loopPosition(position: Int): Int {
        if (position == 0) {
            return toRealPosition(pageViews.lastIndex)
        } else if (position == pageFrames.lastIndex) {
            return toRealPosition(0)
        }
        return position
    }

    /**
     * 現在表示中のページ以外を非表示にする
     * Landscapeにした際ViewPager内でレイアウトが崩れ、別ページが見えてしまう場合があるため暫定で追加
     */
    fun setSinglePageMode(flag: Boolean) {
        val pageIndex = toPagePosition(currentItem)
        pageViews.forEachIndexed { index, view ->
            view.isHidden = flag && (index != pageIndex)
        }
    }

    /**
     * OnPageChangeListener
     */
    inner class PageChangeListener : ViewPager.OnPageChangeListener {
        override fun onPageSelected(position: Int) {

            val pagePosition = toPagePosition(position)
            if (pagePosition != loadedPagePosition) {
                pageChangeAction?.invoke(oldPageIndex, pagePosition)
            }
            loadedPagePosition = null

            if (currentItem == 0 || currentItem == pageFrames.lastIndex) {
                // 左右端の場合はループでページ移動するので、その際pageChangeActionが再度走らないよう、実行済みのpositionを保持しておく
                loadedPagePosition = pagePosition
            }
            oldPageIndex = pageIndex
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (state != ViewPager.SCROLL_STATE_IDLE) {
                return
            }

            if (currentItem == 0 || currentItem == pageFrames.lastIndex) {
                val loopedPosition = loopPosition(currentItem)
                setCurrentItem(loopedPosition, false)
            }
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            if (positionOffset == 0.0f) {
                val frame = pageFrames[currentItem]
                val page = pageViews.safeGet(toPagePosition(currentItem))
                if (page != null && page.parent != frame) {
                    page.removeFromSuperview()
                    frame.addView(page)
                }
                return
            }

            val nextPosition = if (currentItem != position) position else position + 1
            val nextFrame = pageFrames[nextPosition]
            val nextPage = pageViews.safeGet(toPagePosition(nextPosition)) ?: return

            if (nextPage.parent != nextFrame) {
                nextPage.removeFromSuperview()
                nextFrame.addView(nextPage)
            }
        }
    }

    /**
     * Adapter
     */
    inner class Adapter : androidx.viewpager.widget.PagerAdapter() {

        /**
         * view: containerに追加されたview
         * keyItem: instantiateItemの返り値
         */
        override fun isViewFromObject(view: View, keyItem: Any): Boolean {
            return view == keyItem
        }

        // ページを作成して追加する
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val frame = pageFrames[position]

            if (frame.parent != container) {
                frame.removeFromSuperview()
                container.addView(frame)
            }

            val pageView = pageViews.safeGet(toPagePosition(position))
            if (pageView != null && pageView.parent != frame) {
                pageView.removeFromSuperview()
                pageView.isHidden = false // setSinglePageModeでhiddenにする場合があるため、念の為ここでhidden解除する
                frame.addView(pageView)
            }

            return frame
        }

        override fun getItemPosition(`object`: Any): Int {
            if (pageFrames.contains(`object`)) {
                return POSITION_UNCHANGED
            }
            return POSITION_NONE
        }

        override fun destroyItem(container: ViewGroup, position: Int, keyObject: Any) {}

        override fun getCount(): Int {
            return pageFrames.size
        }
    }

}