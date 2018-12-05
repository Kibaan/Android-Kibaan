package kibaan.android.storage

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.util.Base64
import kibaan.android.util.AESUtils
import java.math.BigInteger
import java.security.GeneralSecurityException
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.util.*
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal

class SecureStorage(private val context: Context, private val alias: String = context.packageName) {

    // region -> Constants

    /** 鍵ペアを生成する際のプロバイダ名称 */
    private val keyProvider = "AndroidKeyStore"
    /** 暗号化方式(APILevel23未満では「RSA」のみサポートされている */
    private val keyAlgorithmRsa = "RSA"
    /** Cipherのアルゴリズム */
    private val cipherAlgorithm = "RSA/ECB/PKCS1Padding"
    /** 秘密鍵保存用のキー(APILevel18未満用) */
    private val keyOfSecretKey = "SecureStorage.SecretKey"
    /** IV保存用のキー(APILevel18未満用) */
    private val keyOfIV = "SecureStorage.IV"

    // endregion

    // region -> Variables

    /**
     * Key Store
     */
    private val keyStore: KeyStore
        get() {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            return keyStore
        }

    /**
     * Shared Preferences
     */
    private val sharedPreferences: SharedPreferences get() = context.getSharedPreferences(alias, Context.MODE_PRIVATE)
    /**
     * Android Key Storeが使用できるかどうか
     */
    private val isKeyStoreEnabled: Boolean get() = Build.VERSION_CODES.JELLY_BEAN_MR2 <= Build.VERSION.SDK_INT
    /**
     * 秘密鍵を生成済みかどうか（APILevel18未満用）
     */
    private val hasSecretKey: Boolean get() = sharedPreferences.getString(keyOfSecretKey, null) != null

    // endregion

    // region -> Initializer

    init {
        if (!hasSecretKey) {
            if (isKeyStoreEnabled) {
                val keyStore = keyStore
                // 鍵ペアが生成されていない場合は生成する
                if (!keyStore.containsAlias(this.alias)) {
                    generateNewKeyPair(keyStore, this.alias, context)
                }
            } else {
                val secretKey = Base64.encodeToString(AESUtils.createRandomByteArray(), Base64.DEFAULT)
                val iv = Base64.encodeToString(AESUtils.createRandomByteArray(), Base64.DEFAULT)
                sharedPreferences.edit().putString(keyOfSecretKey, secretKey).apply()
                sharedPreferences.edit().putString(keyOfIV, iv).apply()
            }
        }
    }

    // endregion

    // region -> Save

    /**
     * 指定した[key]で[value]を保存する
     */
    fun save(value: String?, key: String): Boolean {
        val bytes = value?.toByteArray(charset = Charsets.UTF_8) ?: return false
        return saveBytes(bytes, key = key)
    }

    fun saveBytes(bytes: ByteArray?, key: String): Boolean {
        val rawBytes = bytes ?: return false
        val encrypted = encrypt(bytes = rawBytes)
        if (encrypted != null) {
            saveToPrefs(Base64.encodeToString(encrypted, Base64.DEFAULT), key = key)
            return true
        }
        return false
    }

    // endregion

    // region -> Load

    /**
     * 指定した[key]に対応する文字列データを取得する
     */
    fun load(key: String): String? {
        val bytes = loadBytes(key) ?: return null
        return String(bytes, charset = Charsets.UTF_8)
    }

    fun loadBytes(key: String): ByteArray? {
        val rawValue = loadForPrefs(key = key) ?: return null
        val encrypted = Base64.decode(rawValue, Base64.DEFAULT)
        return decrypt(encrypted)
    }

    // endregion

    // region -> Delete

    /**
     * 指定したキーに対応するデータを削除する
     */
    fun delete(key: String): Boolean {
        sharedPreferences.edit().remove(key).apply()
        return true
    }

    /**
     * 全てのデータを破棄する
     */
    fun clear(): Boolean {
        sharedPreferences.edit().clear().apply()
        return true
    }

    // endregion

    // region -> Support

    private fun saveToPrefs(value: String, key: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    private fun loadForPrefs(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    // endregion

    // region -> Encrypt & Decrypt

    /**
     * 指定されたデータを暗号化して返す
     */
    private fun encrypt(bytes: ByteArray): ByteArray? {
        if (hasSecretKey) {
            return encryptNotKeyStore(bytes)
        }
        return try {
            val cipher = Cipher.getInstance(cipherAlgorithm)
            cipher.init(Cipher.ENCRYPT_MODE, keyStore.getCertificate(alias))
            cipher.doFinal(bytes)
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 指定されたデータを暗号化して返す（APILevel18未満用）
     */
    private fun encryptNotKeyStore(bytes: ByteArray): ByteArray? {
        val key = sharedPreferences.getString(keyOfSecretKey, null) ?: return null
        val iv = sharedPreferences.getString(keyOfIV, null) ?: return null
        return try {
            AESUtils.encrypt(src = bytes, key = key.toByteArray(), iv = iv.toByteArray())
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 指定されたデータを復号化して返す
     */
    private fun decrypt(bytes: ByteArray): ByteArray? {
        if (hasSecretKey) {
            return decryptNotKeyStore(bytes)
        }
        return try {
            val privateKey = keyStore.getKey(alias, null) as? PrivateKey ?: return null
            val cipher = Cipher.getInstance(cipherAlgorithm)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            cipher.doFinal(bytes)
        } catch (e: GeneralSecurityException) {
            null
        }
    }

    /**
     * 指定されたデータを復号化して返す（APILevel18未満用）
     */
    private fun decryptNotKeyStore(bytes: ByteArray): ByteArray? {
        val key = sharedPreferences.getString(keyOfSecretKey, null) ?: return null
        val iv = sharedPreferences.getString(keyOfIV, null) ?: return null
        return try {
            AESUtils.decrypt(encoded = bytes, key = key.toByteArray(), iv = iv.toByteArray())
        } catch (e: Exception) {
            null
        }
    }

    // endregion

    // region -> Android KeyStore

    /**
     * 鍵ペアを生成する
     */
    private fun generateNewKeyPair(keyStore: KeyStore, alias: String, context: Context) {
        try {
            if (!keyStore.containsAlias(alias)) {
                val keyPairGenerator = KeyPairGenerator.getInstance(keyAlgorithmRsa, keyProvider)
                keyPairGenerator.initialize(createKeyPairGeneratorSpec(alias, context))
                keyPairGenerator.generateKeyPair()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 鍵ペアを生成する際のパラメータを生成して返す
     * ※APILevel23で非推奨となっているが、APILevel23未満をサポートする為に使用している
     */
    @Suppress("DEPRECATION")
    private fun createKeyPairGeneratorSpec(alias: String, context: Context): KeyPairGeneratorSpec {
        val start = Calendar.getInstance()
        val end = Calendar.getInstance()
        end.add(Calendar.YEAR, 100)

        return KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias)
                .setSubject(X500Principal(String.format("CN=%s", alias)))
                .setSerialNumber(BigInteger.valueOf(1337))
                .setStartDate(start.time)
                .setEndDate(end.time)
                .build()
    }

    // endregion
}