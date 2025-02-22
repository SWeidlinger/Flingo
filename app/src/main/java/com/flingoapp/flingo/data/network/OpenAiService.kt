package com.flingoapp.flingo.data.network

import com.flingoapp.flingo.BuildConfig
import com.flingoapp.flingo.data.model.genAi.OpenAiImageRequest
import com.flingoapp.flingo.data.model.genAi.OpenAiImageResponse
import com.flingoapp.flingo.data.model.genAi.OpenAiTextRequest
import com.flingoapp.flingo.data.model.genAi.OpenAiTextResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAiService {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer ${BuildConfig.OPENAI_API_KEY}"
    )
    @POST("v1/chat/completions")
    suspend fun getTextResponse(@Body request: OpenAiTextRequest): OpenAiTextResponse

    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer ${BuildConfig.OPENAI_API_KEY}"
    )
    @POST("v1/images/generations")
    suspend fun getImageResponse(@Body request: OpenAiImageRequest): OpenAiImageResponse

    companion object {
        private const val BASE_URL = "https://api.openai.com/"

        //should be injected
        private val retrofitInstance = RetrofitProviderImpl.getInstance(BASE_URL)
        private lateinit var service: OpenAiService

        val instance: OpenAiService
            get() {
                if (!this::service.isInitialized) {
                    createInstance()
                }

                return service
            }

        private fun createInstance() {
            service = retrofitInstance.create(OpenAiService::class.java)
        }
    }
}