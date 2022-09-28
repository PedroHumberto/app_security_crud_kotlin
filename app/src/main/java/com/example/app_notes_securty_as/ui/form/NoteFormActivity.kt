package com.example.app_notes_securty_as.ui.form

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.bumptech.glide.Glide
import com.example.app_notes_securty_as.databinding.ActivityNoteFormBinding
import com.example.app_notes_securty_as.domain.models.Note
import com.example.app_notes_securty_as.service.NoteDAO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class NoteFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteFormBinding

    private var PICK_IMAGE: Int = 1
    private lateinit var imgForm: ImageView
    private var storage = Firebase.storage
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    var imgUri: Uri? = null
    private val noteDAO = NoteDAO()
    private lateinit var db: FirebaseFirestore
    private lateinit var image: String
    private lateinit var editTitle: CharSequence
    private lateinit var editNote: CharSequence


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //---- register to get image ------
        registerActivityforResult()
        //---------------------------------
        db = Firebase.firestore

        imgForm = binding.formImg

        //get image from internal
        imgForm.setOnClickListener {
            getImage()
        }

        binding.btnSave.setOnClickListener {
            editTitle = binding.editTitle.text
            editNote = binding.editNote.text
            saveData()
            cypher(editTitle.toString().toByteArray(), "title.txt")
            val baseImage = imgBase64().toByteArray()
            cypher(baseImage, "image.fig")
        }

        binding.btnTeste.setOnClickListener {
            //decrypt("title.txt")
            //decrypt( "image.fig")
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PICK_IMAGE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        } else {

        }
    }

    private fun getImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PICK_IMAGE
            )
        } else {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        }
    }

    private fun registerActivityforResult() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                val resultCode = result.resultCode
                val imgData = result.data
                if (resultCode == RESULT_OK && imgData != null) {
                    imgUri = imgData.data

                    imgUri.let {
                        Glide.with(this)
                            .load(it)
                            .into(imgForm)
                    }
                }
            })
    }


    private fun addNote(url: String, dateTime: String) {
        val note = Note(
            "${editTitle}",
            "${editNote}",
            "$dateTime",
            url
        )


        noteDAO.addNote(note)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveData() {
        //---------get date time------------------
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = current.format(formatter)
        //--------get storage reference in FireBase------------------
        var storageRef = storage.reference
        val imgRef = storageRef.child("images").child("$dateTime.jpeg")
        val bitmap = (imgForm.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        //---------------upload image------------------------------------
        var uploadTask = imgRef.putBytes(data)
        uploadTask.addOnFailureListener {
        }.addOnSuccessListener { taskSnapshot ->
            Log.d(TAG, "IMDATA  1=> ${taskSnapshot.uploadSessionUri}")

            imgRef.downloadUrl.addOnSuccessListener { url ->
                val imgURL = url.toString()
                Log.d(TAG, "IMDATA  2=> ${imgURL}")
                Log.d(TAG, "IMDATA  3=> ${url.toString()}")
                addNote(imgURL, dateTime)
                finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun imgBase64(): String {
        imgForm.buildDrawingCache()
        val bmap = imgForm.drawingCache
        val bos = ByteArrayOutputStream()
        bmap.compress(CompressFormat.PNG, 100, bos)
        val bb = bos.toByteArray()
        image = Base64.getEncoder().encodeToString(bb)
        Log.d(TAG, "IMG => $image")

        return image

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun decodeImgBase64() {
        val imageBytes = Base64.getDecoder().decode(image)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

    }

    private fun cypher(byteArray: ByteArray, fileName: String) {
        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val file = File(this.filesDir, fileName)
        if (file.exists()) {
            file.delete()
        }
        val encryptedFile = EncryptedFile.Builder(
            this,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        val fos = encryptedFile.openFileOutput()
        fos.write(byteArray)
        fos.write(byteArray)
        fos.close()
    }
    private fun decrypt(fileName: String) {
        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val file = File(this.filesDir, fileName)

        val encryptedFile = EncryptedFile.Builder(
            this,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        val fos = encryptedFile.openFileInput()
        val bytes = fos.readBytes()


        fos.close()
        Log.d(TAG, "DECIFRADO => ${String(bytes)}")
    }
}

