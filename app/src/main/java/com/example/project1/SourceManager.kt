package com.example.project1

import androidx.compose.ui.platform.LocalContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

class SourceManager {
    val client: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()
        val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)
        client = builder.build()
    }

    suspend fun retrieveSources(category: String, apiKey: String): List<SourceData> {

        val request = Request.Builder()
            .url("https://newsapi.org/v2/sources?country=us&category=$category&apiKey=$apiKey")
            .get()
            .build()

        val response: Response = client.newCall(request).execute();
        val responseBody = response.body?.string()

        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            val sourcesList = mutableListOf<SourceData>()
            val json = JSONObject(responseBody)
            val sources = json.getJSONArray("sources")
            for (i in 0 until sources.length()) {
                val currentSource = sources.getJSONObject(i)

                val sourceData: SourceData = SourceData(
                    name = currentSource.getString("name"),
                    description = currentSource.getString("description"),
                    category = currentSource.getString("category"),
                    id = currentSource.getString("id")
                )

                sourcesList.add(sourceData)
            }

            return sourcesList
        }
        else {
            return listOf()
        }
    }


}