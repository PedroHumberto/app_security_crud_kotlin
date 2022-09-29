package com.example.app_notes_securty_as.ui.form

import android.Manifest
import android.annotation.SuppressLint
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
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.app_notes_securty_as.databinding.ActivityNoteFormBinding
import com.example.app_notes_securty_as.domain.models.Note
import com.example.app_notes_securty_as.service.Encrypter
import com.example.app_notes_securty_as.service.NoteDAO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class NoteFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteFormBinding

    private lateinit var imgForm: ImageView
    private var storage = Firebase.storage
    private val noteDAO = NoteDAO()
    private lateinit var db: FirebaseFirestore
    private lateinit var image: String
    private lateinit var editTitle: CharSequence
    private lateinit var editNote: CharSequence
    private var CAMERA_REQUEST = 1000


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore

        imgForm = binding.formImg

        //get image from internal
        imgForm.setOnClickListener {
            getCam()
        }

        binding.btnSave.setOnClickListener {
            editTitle = binding.editTitle.text
            editNote = binding.editNote.text
            saveData()
            val baseImage = imgBase64().toByteArray()
            val textCypher = Encrypter(
                editTitle.toString().toByteArray(),
                "TITLE(${dateTime()}).txt",
                this
            )
            val imageCypher = Encrypter(
                baseImage,
                "image(${dateTime()}).fig",
                this
            )

            textCypher.getCypher()
            imageCypher.getCypher()
        }

        binding.btnTeste.setOnClickListener {

        }

    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras!!.get("data") as Bitmap
            imgForm.setImageBitmap(imageBitmap)
        }
    }


    private fun getCam(){
        val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(camIntent, CAMERA_REQUEST)
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
        val dateTime = dateTime()
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
                addNote(imgURL, dateTime!!)
                finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun dateTime(): String? {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = current.format(formatter)
        return dateTime
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


}

