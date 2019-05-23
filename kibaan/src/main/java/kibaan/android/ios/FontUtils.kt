package kibaan.android.ios

import android.graphics.Paint
import android.graphics.Typeface

class FontUtils {

    companion object {
        /**
         * 特定の幅に指定したテキストが収まるフォントサイズ（px）を計算する
         */
        @Suppress("NAME_SHADOWING")
        fun adjustTextSize(text: String, baseTextSize: Float, width: Int, typeface: Typeface?, minSize: Float): Float {

            // FIXME: ドットとスペースのサイズが実際より小さく計算されてしまう場合がある
            // ただし、下記のリプレースを行うと幅が"WrapContent"の場合に、'.'か' 'が含まれていると実際のサイズよりも文字幅が大きいと見なされてしまい、
            // 無条件でフォントサイズが縮小されてしまう為、処理をはずした
            // val text = text.replace('.', 'A').replace(' ', 'A')

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