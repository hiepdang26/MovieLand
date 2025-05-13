package com.example.admin.data.cloudinary


import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.BufferedSink
import okio.source
import org.json.JSONObject
import java.io.IOException

class CloudinaryUploader(
    private val context: Context,
    private val cloudName: String,
    private val uploadPreset: String
) {
    private val client = OkHttpClient()

    suspend fun uploadImage(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(Exception("Không thể mở inputStream"))

            val requestBody = object : RequestBody() {
                override fun contentType(): MediaType? = "image/*".toMediaTypeOrNull()
                override fun writeTo(sink: BufferedSink) {
                    inputStream.source().use { source -> sink.writeAll(source) }
                }
            }

            val multipart = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "poster.jpg", requestBody)
                .addFormDataPart("upload_preset", uploadPreset)
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
                .post(multipart)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful) {
                Log.e("CloudinaryUploader", "❌ Upload failed: ${response.code} - $responseBody")
                return@withContext Result.failure(IOException("Lỗi: ${response.code}"))
            }

            Log.d("CloudinaryUploader", "✅ Upload thành công: $responseBody")

            val json = JSONObject(responseBody ?: "")
            val imageUrl = json.getString("secure_url")
            Log.d("CloudinaryUploader", "📸 Image URL: $imageUrl")
            return@withContext Result.success(imageUrl)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
