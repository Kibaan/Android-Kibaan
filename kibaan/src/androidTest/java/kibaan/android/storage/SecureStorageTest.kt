package kibaan.android.storage

import android.content.Context
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SecureStorageTest {

    private var appContext: Context = InstrumentationRegistry.getTargetContext()
    @Test
    fun testSaveAndLoad() {
        val value = "1fndaiufjdaifoi4129"
        val key = "password"
        assertTrue(SecureStorage(context = appContext).save(value, key = key))
        val loadValue = SecureStorage(context = appContext).load(key = key)
        assertEquals(value, loadValue)
    }

    @Test
    fun testSaveAndLoadNil() {
        val value = "abc"
        val key = "password"

        SecureStorage(context = appContext).save(value, key = key)
        assertEquals(value, SecureStorage(context = appContext).load(key = key))

        SecureStorage(context = appContext).save(null, key = key)
        assertNull(SecureStorage(context = appContext).load(key = key))
    }

    @Test
    fun testDelete() {
        val value = "1fndaiufjdaifoi4129"
        val key = "password"
        assertTrue(SecureStorage(context = appContext).save(value, key = key))
        val loadValue = SecureStorage(context = appContext).load(key = key)
        assertEquals(value, loadValue)
        assertTrue(SecureStorage(context = appContext).delete(key = key))
        val deletedValue = SecureStorage(context = appContext).load(key = key)
        assertNull(deletedValue)
        assertNotEquals(loadValue, deletedValue)
    }

    @Test
    fun testClear() {
        val keys = listOf("pass1", "pass2", "passe")
        keys.forEach {
            SecureStorage(context = appContext).save("delete target.", key = it)
            assertNotNull(SecureStorage(context = appContext).load(key = it))
        }
        SecureStorage(context = appContext).clear()
        keys.forEach { assertNull(SecureStorage(context = appContext).load(key = it)) }
    }

    @Test
    fun testSaveAndLoadCache() {
        val secureStorage = SecureStorage(context = appContext)
        val value = "1fndaiufjdaifoi4129"
        val key = "password"
        assertTrue(secureStorage.save(value, key = key))
        val loadValue = secureStorage.load(key = key)
        assertEquals(value, loadValue)
    }

    @Test
    fun testSaveAndLoadNilCache() {
        val secureStorage = SecureStorage(context = appContext)
        val value = "abc"
        val key = "password"

        secureStorage.save(value, key = key)
        assertEquals(value, secureStorage.load(key = key))

        secureStorage.save(null, key = key)
        assertNull(secureStorage.load(key = key))
    }

    @Test
    fun testDeleteCache() {
        val secureStorage = SecureStorage(context = appContext)
        val value = "1fndaiufjdaifoi4129"
        val key = "password"
        assertTrue(secureStorage.save(value, key = key))
        val loadValue = secureStorage.load(key = key)
        assertEquals(value, loadValue)
        assertTrue(secureStorage.delete(key = key))
        val deletedValue = secureStorage.load(key = key)
        assertNull(deletedValue)
        assertNotEquals(loadValue, deletedValue)
    }

    @Test
    fun testClearCache() {
        val secureStorage = SecureStorage(context = appContext)
        val keys = listOf("pass1", "pass2", "passe")
        keys.forEach {
            secureStorage.save("delete target.", key = it)
            assertNotNull(secureStorage.load(key = it))
        }
        secureStorage.clear()
        keys.forEach { assertNull(secureStorage.load(key = it)) }
    }

}