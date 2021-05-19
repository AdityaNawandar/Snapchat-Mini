package com.example.snapchatmini

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.ByteArrayOutputStream

class HomeActivity : AppCompatActivity() {
    var currentUser = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
/*
        var intent = intent
        var currentUser = intent.extras*/
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_snap, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.newsnap) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                getPhoto()
            }
        } else if (item.itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val selectedImage = data!!.data
        try {
            if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
/*                val objBitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                val outStream = ByteArrayOutputStream()
                objBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                val bytarrStream = outStream.toByteArray()
                val parseFile = ParseFile("image.png", bytarrStream)
                val parseObject = ParseObject("Image")
                parseObject.put("image", parseFile)
                parseObject.put("username", strCurrentUser)
                parseObject.saveInBackground(object : SaveCallback() {
                    fun done(e: ParseException?) {
                        if (e == null) {
                            Toast.makeText(
                                this@UserListActivity,
                                "Image has been shared!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
                Log.i("", "")*/
            } else {
                Toast.makeText(
                    this,
                    "There has been an issue uploading the image :(",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }
}