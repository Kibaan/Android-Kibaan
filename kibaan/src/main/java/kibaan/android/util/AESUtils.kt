package kibaan.android.util

import android.util.Base64
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES暗号化のユーティリティー
 *
 * 鍵長: 128ビット
 * IV長: 128ビット
 * 暗号利用モード: CBC
 * パディング方式：PKCS5
 *
 * Created by Yamamoto Keita on 2018/08/11.
 */
object AESUtils {
    private const val ALGORITHM = "AES"
    private const val CBC_METHOD = "AES/CBC/PKCS5Padding"
    private val CHARSET = Charsets.UTF_8

    /**
     * 簡易的な暗号化。暗号化したBase64の文字列を返す
     */
    fun convenientEncrypt(src: String?, secretKey: String): String? {
        val rawString = src ?: return null
        return try {
            val srcData = rawString.toByteArray(CHARSET)
            val keyData = toBytes(secretKey, byteSize = 16)
            val ivData = toBytes(secretKey, byteSize = 16)

            val encrypted = encrypt(src = srcData, key = keyData, iv = ivData)

            Base64.encodeToString(encrypted, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Failed to encrypt", e)
            null
        }
    }

    /**
     * `convenientEncrypt`で暗号化したデータを復号化する
     */
    fun convenientDecrypt(encodedBase64: String?, secretKey: String): String? {
        val base64 = encodedBase64 ?: return null

        return try {
            val encryptedData = Base64.decode(base64, Base64.DEFAULT)
            val keyData = toBytes(secretKey, byteSize = 16)
            val ivData = toBytes(secretKey, byteSize = 16)

            val decrypted = decrypt(encoded = encryptedData, key = keyData, iv = ivData)

            String(decrypted, CHARSET)
        } catch (e: Exception) {
            Log.i(javaClass.simpleName, "Failed to decrypt", e)
            return null
        }
    }

    /**
     * 暗号化する
     */
    @Throws(Exception::class)
    fun encrypt(src: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(CBC_METHOD)
        val ivSpec = IvParameterSpec(iv)
        val keySpec = SecretKeySpec(key, ALGORITHM)

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        return cipher.doFinal(src)
    }

    /**
     * 複合化する
     */
    @Throws(Exception::class)
    fun decrypt(encoded: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        val secretKey = SecretKeySpec(key, ALGORITHM)
        val cipher = Cipher.getInstance(CBC_METHOD)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        return cipher.doFinal(encoded)
    }

    /**
     * ランダムなIVを生成する
     */
    fun createRandomByteArray(): ByteArray {
        val data = ByteArray(16)
        val random = Random()
        for (i in data.indices) {
            val randomInt = random.nextInt() % 128
            data[i] = randomInt.toByte()
        }
        return data
    }

    /**
     * 文字列を指定サイズのByteArrayにする
     */
    private fun toBytes(data: String, byteSize: Int): ByteArray {
        val result = ByteArray(byteSize)
        val bytes = data.toByteArray(CHARSET)

        for (i in result.indices) {
            if (bytes.size <= i) continue
            result[i] = bytes[i]
        }

        return result
    }

}