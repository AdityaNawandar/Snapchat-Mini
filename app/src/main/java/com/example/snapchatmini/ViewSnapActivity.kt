package com.example.snapchatmini

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.HttpURLConnection
import java.net.URL

class ViewSnapActivity : AppCompatActivity() {

    lateinit var txtvwMessage: TextView
    lateinit var imgvwViewSnap: ImageView
    lateinit var task: ImageDownloader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)

        txtvwMessage = findViewById(R.id.txtvwMessage)
        imgvwViewSnap = findViewById(R.id.imgvwViewSnap)

        txtvwMessage.text = intent.getStringExtra("message")
        task = ImageDownloader()
        downloadImage()
    }

    fun downloadImage() {

        val objBitmap: Bitmap
        try {
            var downloadURL = intent.getStringExtra("imageURL")
            objBitmap = task.execute(downloadURL).get()!!

            imgvwViewSnap!!.setImageBitmap(objBitmap)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    inner class ImageDownloader() : AsyncTask<String, Void?, Bitmap?>() {
        override fun doInBackground(vararg strarrURLs: String): Bitmap? {
            return try {
                val objURL = URL(strarrURLs[0])
                val objConn =
                    objURL.openConnection() as HttpURLConnection
                objConn.connect()
                val objInputStream = objConn.inputStream
                BitmapFactory.decodeStream(objInputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }


    }

}