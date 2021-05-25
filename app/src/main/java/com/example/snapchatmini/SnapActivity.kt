package com.example.snapchatmini

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.component4
import com.google.firebase.storage.ktx.storage
import java.io.*
import java.util.*


class SnapActivity : AppCompatActivity() {

    //var storage: FirebaseStorage? = null
    lateinit var storage: FirebaseStorage
    var storageReference: StorageReference? = null
    private var imageUri: Uri? = null
    var imgvwSnap: ImageView? = null
    private val PICK_IMAGE_REQUEST = 1
    lateinit var edttxtMessage: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap)
        // [START storage_field_initialization]
        storage = Firebase.storage
        imgvwSnap = findViewById(R.id.imgvwSnap)
        //storage = FirebaseStorage.getInstance();
        storageReference = storage!!.getReference();
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //val selectedImage = data!!.data
        try {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
                imageUri = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    imgvwSnap!!.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun uploadImage() {

        try {
            var randomKey = UUID.randomUUID().toString()
            if (imageUri != null) {
                edttxtMessage = findViewById(R.id.edttxtMessage)
                var strMessage = edttxtMessage.text.toString()
                //progress dialog
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Uploading...")
                progressDialog.show()

                //#UPLOAD FILE
                // Points to the root reference
                val storageReference: StorageReference = storage.reference
                val imagesRef = storageReference.child("images/" + randomKey)
                val uploadTask = imagesRef.putFile(imageUri!!)

                val urlTask = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imagesRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result.toString()

                        //Create intent to go to UserList(Activity) to choose from
                        val intent = Intent(this, UserListActivity::class.java)
                        //add data to intent
                        intent.putExtra("imageURL", downloadUri)
                        intent.putExtra("imageName", randomKey)
                        intent.putExtra("message", strMessage)
                        startActivity(intent)
                    } else {
                        // Handle failures
                        // ...
                    }
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun chooseImage(view: View) {
        //Take picture with camera
/*      val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePicture,1)*/ //one can be replaced with any action code (called requestCode)

        //Choose picture from gallery
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(
            pickPhoto,
            PICK_IMAGE_REQUEST
        ) //one can be replaced with any action code

    }

    fun next(view: View) {
        uploadImage()
    }

/*    fun getPathFromInputStreamUri(context: Context, uri: Uri): String? {
        var filePath: String? = null
        uri.authority?.let {
            try {
                context.contentResolver.openInputStream(uri).use {
                    val photoFile: File? = createTemporalFileFrom(it)
                    filePath = photoFile?.path
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return filePath
    }

    @Throws(IOException::class)
    private fun createTemporalFileFrom(inputStream: InputStream?): File? {
        var targetFile: File? = null
        return if (inputStream == null) targetFile
        else {
            var read: Int
            val buffer = ByteArray(8 * 1024)
            targetFile = createTemporalFile()
            FileOutputStream(targetFile).use { out ->
                while (inputStream.read(buffer).also { read = it } != -1) {
                    out.write(buffer, 0, read)
                }
                out.flush()
            }
            targetFile
        }
    }

    private fun createTemporalFile(): File = File(filesDir, "tempPicture.jpg")*/
}