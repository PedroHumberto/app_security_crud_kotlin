package com.example.app_notes_securty_as.service

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import java.io.File

class Encrypter {

    fun cypher(byteArray: ByteArray, fileName: String, context: Context,) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            file.delete()
        }
        val encryptedFile = EncryptedFile.Builder(
            context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        val fos = encryptedFile.openFileOutput()
        fos.write(byteArray)
        fos.write(byteArray)
        fos.close()
        Log.d(TAG, "CRIPTOGRAFADO!!")
    }

    private fun decrypt(fileName: String, context: Context) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val file = File(context.filesDir, fileName)

        val encryptedFile = EncryptedFile.Builder(
            context,
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