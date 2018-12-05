package kibaan.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import kibaan.extension.isEmpty
import java.io.InputStream
import java.net.URL

class URLImagePicker : AsyncTask<String, Void, Bitmap>() {

    var onComplete: ((Bitmap?) -> Unit)? = null
    var url: String? = null

    fun execute(url: String, onComplete: (Bitmap?) -> Unit) {
        if (url.isEmpty) return
        this.onComplete = onComplete
        this.url = url

        this.execute()
    }

    override fun doInBackground(vararg str: String): Bitmap? {

        var inputStream: InputStream? = null
        try {
            inputStream = URL(url).openStream()
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.d(this::class.simpleName, "[ERROR] couldn't load $url > $e")
        } finally {
            inputStream?.close()
        }
        return null
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        onComplete?.invoke(result)
    }
}