package kibaan.android.extension

import org.junit.Assert.assertEquals
import org.junit.Test

@Suppress("SpellCheckingInspection")
class CharExtTest {

    private val kanaMap = mapOf(
        'あ' to 'ア', 'い' to 'イ', 'う' to 'ウ', 'え' to 'エ', 'お' to 'オ',
        'か' to 'カ', 'が' to 'ガ',
        'は' to 'ハ', 'ば' to 'バ', 'ぱ' to 'パ',
        'っ' to 'ッ',
        'ゃ' to 'ャ', 'ゅ' to 'ュ', 'ょ' to 'ョ',
        'ぁ' to 'ァ', 'ぃ' to 'ィ', 'ぅ' to 'ゥ', 'ぇ' to 'ェ', 'ぉ' to 'ォ',
        'ん' to 'ン',
        'ゔ' to 'ヴ', 'ゐ' to 'ヰ', 'ゑ' to 'ヱ',
        'ゕ' to 'ヵ'
    )

    private val noEffectList = listOf(
        'ヷ', 'ヸ', 'ヹ', 'ヺ', '龍', ' ', '・', 'ー', 'ヽ', 'ヾ'
    )

    @Test
    fun testHiraganaToKatakana() {
        kanaMap.forEach { (hiragana, katakana) ->
            assertEquals(katakana, hiragana.toKatakana())
        }
    }

    @Test
    fun testKatakanaToKatakana() {
        kanaMap.forEach { (_, katakana) ->
            assertEquals(katakana, katakana.toKatakana())
        }
    }

    @Test
    fun testKatakanaToHiragana() {
        kanaMap.forEach { (hiragana, katakana) ->
            assertEquals(hiragana, katakana.toHiragana())
        }
    }

    @Test
    fun testHiraganaToHiragana() {
        kanaMap.forEach { (hiragana, _) ->
            assertEquals(hiragana, hiragana.toHiragana())
        }
    }

    @Test
    fun testToKatakanaNoEffect() {
        noEffectList.forEach {
            assertEquals(it, it.toKatakana())
        }
    }

    @Test
    fun testToHiraganaNoEffect() {
        noEffectList.forEach {
            assertEquals(it, it.toHiragana())
        }
    }
}
