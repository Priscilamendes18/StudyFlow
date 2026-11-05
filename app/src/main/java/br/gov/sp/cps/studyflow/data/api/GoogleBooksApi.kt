package br.gov.sp.cps.studyflow.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class GoogleBooksResponse(
    val items: List<GoogleBookItem>?
)

data class GoogleBookItem(
    val id: String?,
    val volumeInfo: VolumeInfo?
)

data class VolumeInfo(
    val title: String?,
    val authors: List<String>?,
    val description: String?,
    val imageLinks: ImageLinks?,
    val infoLink: String?
)

data class ImageLinks(
    val smallThumbnail: String?,
    val thumbnail: String?
)

interface GoogleBooksApi {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 10
    ): GoogleBooksResponse
}

object GoogleBooksClient {
    private const val BASE_URL = "https://www.googleapis.com/books/v1/"

    val api: GoogleBooksApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleBooksApi::class.java)
    }
}
