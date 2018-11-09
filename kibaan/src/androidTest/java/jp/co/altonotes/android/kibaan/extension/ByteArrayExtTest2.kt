package jp.co.altonotes.android.kibaan.extension

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import jp.co.altonotes.android.kibaan.ios.data
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ByteArrayExtTest2 {

    private var appContext: Context = InstrumentationRegistry.getTargetContext()

    @Test
    fun testInit_FileFound() {
        val testFileName = "Found.txt"
        val testText = "This is Test Data."
        val testData = testText.data(using = Charsets.UTF_8)
        testData.writeTo(fileName = testFileName, context = appContext)

        val loadData = ByteArray(fileName = testFileName, context = appContext)
        val loadText = loadData?.toString(Charsets.UTF_8)
        assertNotNull(loadData)
        assertEquals(testText, loadText)
    }

    @Test
    fun testWritTo_Success() {
        val testFileName = "TestData.txt"
        val testText = "This is Test Data."
        val testData = testText.data(using = Charsets.UTF_8)
        testData.writeTo(fileName = testFileName, context = appContext)
        val loadData = ByteArray(fileName = testFileName, context = appContext)
        val loadText = loadData?.toString(Charsets.UTF_8)
        assertNotNull(loadData)
        assertEquals(testText, loadText)
    }

    @Test
    fun testWritTo_Failure() {
        val testFileName = ""
        val testText = "This is Test Data."
        val testData = testText.data(using = Charsets.UTF_8)

        try {
            testData.writeTo(fileName = testFileName, context = appContext)
        } catch (e: IOException) {
        }

        val loadData = ByteArray(fileName = testFileName, context = appContext)
        assertNull(loadData)
    }

    @Test
    fun testInit_FileNotFound() {
        val data = ByteArray(fileName = "NotFound.txt", context = appContext)
        assertNull(data)
    }
}
