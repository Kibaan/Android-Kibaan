package kibaan.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View

/**
 * スクロールインジケータ表示用のスクロールリスナー
 */
class ScrollIndicatorDrawer(@Suppress("UNUSED_PARAMETER") context: Context) {

    /** 矢印表示タイプ  */
    private var _scrollIndicatorType = ScrollIndicatorType.NONE
    /** アップ矢印画像  */
    var topIndicatorBitmap: Bitmap? = null
    /** ダウン矢印画像  */
    var bottomIndicatorBitmap: Bitmap? = null
    private val indicatorPaint: Paint by lazy {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.isFilterBitmap = true
        return@lazy paint
    }

    /**
     * 矢印表示タイプ種別
     */
    enum class ScrollIndicatorType {
        /** 非表示  */
        NONE,
        /** 上部のみ  */
        TOP,
        /** 下部のみ  */
        BOTTOM,
        /** 上部・下部両方  */
        BOTH
    }

    /**
     * 描画処理.
     * @param view 描画対象ビュー
     * @param canvas キャンバス
     * @param scrollY スクロール座標
     * @param width 横幅
     */
    @JvmOverloads
    fun draw(view: View, canvas: Canvas, scrollY: Int = 0, width: Int = canvas.width) {
        if (_scrollIndicatorType == ScrollIndicatorType.TOP || _scrollIndicatorType == ScrollIndicatorType.BOTH) {
            val topIndicatorBitmap = topIndicatorBitmap
            if (topIndicatorBitmap != null) {
                val src = Rect(0, 0, topIndicatorBitmap.width, topIndicatorBitmap.height)
                val dst = Rect(0, scrollY, width, scrollY + topIndicatorBitmap.height)
                canvas.drawBitmap(topIndicatorBitmap, src, dst, indicatorPaint)
            }
        }
        if (_scrollIndicatorType == ScrollIndicatorType.BOTTOM || _scrollIndicatorType == ScrollIndicatorType.BOTH) {
            val bottomIndicatorBitmap = bottomIndicatorBitmap
            if (bottomIndicatorBitmap != null) {
                val top = view.height - bottomIndicatorBitmap.height + scrollY
                val src = Rect(0, 0, bottomIndicatorBitmap.width, bottomIndicatorBitmap.height)
                val dst = Rect(0, top, width, top + bottomIndicatorBitmap.height)
                canvas.drawBitmap(bottomIndicatorBitmap, src, dst, indicatorPaint)
            }
        }
    }

    /**
     * 表示タイプをセットします.
     * @param scrollIndicatorType  表示タイプ
     */
    fun setScrollIndicatorType(scrollIndicatorType: ScrollIndicatorType) {
        this._scrollIndicatorType = scrollIndicatorType
    }
}