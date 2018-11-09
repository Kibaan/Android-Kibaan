package jp.co.altonotes.android.kibaan.ios

import android.graphics.Paint
import android.graphics.Typeface

class FontUtils {

    companion object {
        /**
         * 特定の幅に指定したテキストが収まるフォントサイズ（px）を計算する
         */
        @Suppress("NAME_SHADOWING")
        fun adjustTextSize(text: String, baseTextSize: Float, width: Int, typeface: Typeface?, minSize: Float): Float {

            // ドットとスペースのサイズが実際より小さく計算されてしまう場合があるため、Aに置換して幅に余裕を持たせる（暫定対応）
            val text = text.replace('.', 'A').replace(' ', 'A')

            var textSize = baseTextSize

            val paint = Paint()
            paint.textSize = textSize
            paint.typeface = typeface
            paint.isAntiAlias = true

            var textWidth = paint.measureText(text)

            // 横幅に収まるまでループ
            while (width < textWidth) {
                if (textSize <= minSize) {
                    // 最小サイズ以下になる場合は最小サイズ
                    return minSize
                }

                val ratio = textWidth / width
                val scaledSize = textSize / ratio
                val decreasedSize = textSize - 1

                // テキストサイズを縮小
                textSize = Math.min(scaledSize, decreasedSize)
                paint.textSize = textSize

                // テキストの横幅を再取得
                textWidth = paint.measureText(text)
            }
            return textSize
        }

    }

}