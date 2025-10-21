package com.example.project1

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

object ArticleManager {

    val client: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()
        val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)
        client = builder.build()
    }

    suspend fun retrieveArticles(sourceId: String, searchTerm: String, apiKey: String): List<ArticleData> {

        val request = Request.Builder()
            .url("https://newsapi.org/v2/everything?languages=en&sources=$sourceId&q=$searchTerm&apiKey=$apiKey")
            .get()
            .build()

        try {
            val response: Response = client.newCall(request).execute();
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val articleList = mutableListOf<ArticleData>()
                val json = JSONObject(responseBody)
                val articles = json.getJSONArray("articles")
                for (i in 0 until articles.length()) {
                    val currentArticle = articles.getJSONObject(i)

                    val articleData = ArticleData(
                        title = currentArticle.getString("title"),
                        url = currentArticle.getString("url"),
                        icon = currentArticle.getString("urlToImage"),
                        description = currentArticle.getString("description"),
                        source = currentArticle.getJSONObject("source").getString("name")
                    )
                    articleList.add(articleData)
                }

                response.close()
                return articleList
            } else {
                response.close()
                return listOf()
            }
        } catch (e: Exception) {
            Log.d("EXCEPTION", e.message.toString())
            return listOf()
        }
    }

    suspend fun retrieveLocalArticles(location: String, apiKey: String): List<ArticleData> {

        Log.d("GEOCODE", location)
        val request = Request.Builder()
            .url("https://newsapi.org/v2/everything?q=$location&searchIn=title&apiKey=$apiKey")
            .get()
            .build()

        try {
            val response: Response = client.newCall(request).execute();
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val articleList = mutableListOf<ArticleData>()
                val json = JSONObject(responseBody)
                val articles = json.getJSONArray("articles")
                for (i in 0 until articles.length()) {
                    val currentArticle = articles.getJSONObject(i)

                    val articleData = ArticleData(
                        title = currentArticle.getString("title"),
                        url = currentArticle.getString("url"),
                        icon = currentArticle.getString("urlToImage"),
                        description = currentArticle.getString("description"),
                        source = currentArticle.getJSONObject("source").getString("name")
                    )
                    articleList.add(articleData)
                }

                response.close()
                return articleList
            } else {
                response.close()
                return listOf()
            }
        } catch (e: Exception) {
            Log.d("EXCEPTION", e.message.toString())
            return listOf()
        }
    }

    suspend fun retrieveTopHeadlines(category: String, pageNumber: Int, apiKey: String): Pair<List<ArticleData>, Int> {
        val request = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines?category=$category&page=$pageNumber&language=en&apiKey=$apiKey")
            .get()
            .build()

        try {
            val response: Response = client.newCall(request).execute();
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val articleList = mutableListOf<ArticleData>()
                val json = JSONObject(responseBody)
                val articles = json.getJSONArray("articles")
                for (i in 0 until articles.length()) {
                    val currentArticle = articles.getJSONObject(i)

                    val articleData = ArticleData(
                        title = currentArticle.getString("title"),
                        url = currentArticle.getString("url"),
                        icon = currentArticle.getString("urlToImage"),
                        description = currentArticle.getString("description"),
                        source = currentArticle.getJSONObject("source").getString("name")
                    )
                    articleList.add(articleData)
                }

                val totalArticleCount = json.optInt("totalResults")

                response.close()
                return Pair(articleList, totalArticleCount)
            } else {
                response.close()
                return Pair(listOf(), 0)
            }
        } catch (e: Exception) {
            Log.d("EXCEPTION", e.message.toString())
            return Pair(listOf(), 0)
        }
    }

    suspend fun retrieveSources(category: String, apiKey: String): List<SourceData> {

        val request = Request.Builder()
            .url("https://newsapi.org/v2/sources?country=us&category=$category&apiKey=$apiKey")
            .get()
            .build()

        try {
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

                response.close()
                return sourcesList
            }
            else {
                response.close()
                return listOf()
            }
        } catch (e: Exception) {
            Log.d("EXCEPTION", e.message.toString())
            return listOf()
        }
    }
}