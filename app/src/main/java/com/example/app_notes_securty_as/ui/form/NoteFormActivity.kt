package com.example.app_notes_securty_as.ui.form

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.app_notes_securty_as.R
import com.example.app_notes_securty_as.databinding.ActivityNoteFormBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class NoteFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteFormBinding

    private var PICK_IMAGE: Int = 1
    private lateinit var imgForm: ImageView
    private var storage = Firebase.storage
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    var imgUri : Uri? = null
    private var teste = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //register
        registerActivityforResult()


        imgForm = binding.formImg

        var storageRef = storage.reference
        val mountainsRef = storageRef.child("teste.jpeg")


        //get image from internal
        imgForm.setOnClickListener {
            getImage()
        }


        binding.btnSave.setOnClickListener {
            imgForm.isDrawingCacheEnabled = true
            imgForm.buildDrawingCache()
            val bitmap = (imgForm.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            var uploadTask = mountainsRef.putBytes(data)
            uploadTask.addOnFailureListener {


                Log.d(TAG, "IMG ERR 1=> ${it.message}")
                Log.d(TAG, "IMG ERR 2=> ${it.stackTrace}")
                Log.d(TAG, "IMG ERR 3=> ${it.localizedMessage}")


            }.addOnSuccessListener { taskSnapshot ->

                Log.d(TAG, "IMG DATA 1=> ${taskSnapshot.storage.downloadUrl.toString()}")
                Log.d(TAG, "IMG DATA 2=> ${taskSnapshot.toString()}")
                teste = taskSnapshot.storage.downloadUrl.toString()

            }

            binding.btnTeste.setOnClickListener {
                val httpsReference = storage.getReferenceFromUrl(
                    teste
                )
                Log.d(TAG, "IMG TESTE=> ${httpsReference.toString()}")

            }


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
        }else{

        }
    }

    fun getImage() {
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

    fun registerActivityforResult() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                val resultCode = result.resultCode
                val imgData = result.data
                if (resultCode == RESULT_OK && imgData != null){
                    imgUri = imgData.data

                    imgUri.let {
                        Glide.with(this)
                            .load(it)
                            .into(imgForm)
                    }
                }
            })
    }
}