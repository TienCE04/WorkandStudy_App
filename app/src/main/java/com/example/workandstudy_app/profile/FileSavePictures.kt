package com.example.workandstudy_app.profile

import android.content.Context
import android.net.Uri

import java.io.File
import java.io.FileOutputStream

class FileSavePictures(
) {
    fun saveImageToAppStorage(context: Context, imageUri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val fileName = "image_avt.jpg"
            val file = File(context.filesDir, fileName)

            // Ghi đè ảnh cũ nếu đã tồn tại
            if (file.exists()) {
                file.delete()
            }

            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}