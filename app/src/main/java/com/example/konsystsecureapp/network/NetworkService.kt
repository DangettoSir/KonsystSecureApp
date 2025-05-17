package com.example.konsystsecureapp.network

import android.util.Log
import com.example.konsystsecureapp.Preferences.PreferenceManager
import com.example.konsystsecureapp.data.CreateDataRequest
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import okhttp3.OkHttpClient

class NetworkService {
    private val URL: String = "http://192.168.0.100:5085"
    private val client = OkHttpClient()
    private data class AuthRequest(val login: String, val password: String)
    fun authenticate(login: String, password: String, callback: (Boolean, String?) -> Unit) {
        val authRequest = AuthRequest(login, password)
        val json = Gson().toJson(authRequest).toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url("$URL/login").post(json).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                when (response.code) {
                    200 -> callback(true, responseBody)
                    400 -> callback(false, "Invalid JSON format")
                    401 -> callback(false, responseBody ?: "Invalid credentials")
                    else -> callback(false, "Error: ${response.code} $responseBody")
                }
            }
        })
    }
    fun isTokenValid(callback: (Boolean, String?) -> Unit) {
        val getProtectedAuthToken = PreferenceManager.getProtectedAuthToken()
        val request = Request.Builder()
            .url("$URL/protected")
            .header("Authorization", "Bearer $getProtectedAuthToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                when (response.code) {
                    200 -> callback(true, responseBody)
                    401 -> callback(false, "Invalid or expired token")
                    else -> callback(false, "Error: ${response.code} $responseBody")
                }
            }
        })
    }

    fun sendDataSteps(createDataRequest: CreateDataRequest, callback: (Boolean, String?) -> Unit) {
        Log.i("sendDataSteps", "Sending data request: $createDataRequest")
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("userId", createDataRequest.userId?.toString() ?: "")
            .addFormDataPart("eventId", createDataRequest.eventId?.toString() ?: "")
            .addFormDataPart("scenarioId", createDataRequest.scenarioId?.toString() ?: "")
            .addFormDataPart("stepId", createDataRequest.stepId?.toString() ?: "")
            .addFormDataPart("userComment", createDataRequest.userComment ?: "")
            .also { builder ->
                createDataRequest.videoFile?.let {
                    Log.i("KONSYST", "Adding videoFile to request")
                    builder.addPart(MultipartBody.Part.createFormData("videoFile", "video.mp4", RequestBody.create(
                        "video/mp4".toMediaTypeOrNull(), it)))
                }
                createDataRequest.photoFiles?.forEachIndexed { index, photoFile ->
                    Log.i("KONSYST", "Adding photoFile$index to request")
                    builder.addPart(MultipartBody.Part.createFormData("photoFiles[]", "photo$index.jpg", RequestBody.create(
                        "image/jpeg".toMediaTypeOrNull(), photoFile)))
                }
            }
            .build()

        val request = Request.Builder()
            .url("$URL/userdata/request")
            .addHeader("Bearer-Authorization", "${PreferenceManager.getAuthToken()}")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("KONSYST", "Error sending data: ${e.message}")
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                when (response.code) {
                    200 -> {
                        Log.i("KONSYST", "Data sent successfully")
                        callback(true, responseBody)
                    }
                    400 -> {
                        Log.e("KONSYST", "Error: ${response.code} $responseBody")
                        callback(false, "Error: ${response.code} $responseBody")
                    }
                    else -> {
                        Log.e("KONSYST", "Error: ${response.code} $responseBody")
                        callback(false, "Error: ${response.code} $responseBody")
                    }
                }
            }
        })
    }


    fun UpdateScenario(id: Int, eventId: Int, isCompleted: Boolean?, callback: (Boolean, String?) -> Unit) {
        val requestBody = mapOf(
            "id" to id,
            "eventId" to eventId,
            "isCompleted" to isCompleted
        )
        val json = Gson().toJson(requestBody).toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url("$URL/scenarios/update").post(json).addHeader("Bearer-Authorization", "${PreferenceManager.getAuthToken()}").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                when (response.code) {
                    200 -> callback(true, responseBody)
                    else -> callback(false, "Error: ${response.code} $responseBody")
                }
            }
        })
    }
    fun searchEvents(searchQuery: String, callback: (Boolean, String?) -> Unit) {
        val json = Gson().toJson(searchQuery).toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url("$URL/events/search").post(json).addHeader("Bearer-Authorization", "$searchQuery").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                when (response.code) {
                    200 -> callback(true, responseBody)
                    else -> callback(false, "Error: ${response.code} $responseBody")
                }
            }
        })
    }
    data class SearchQuery(val searchQuery: Int)
    fun checkEventStatus(searchQuery: Int, callback: (Boolean, String?) -> Unit){
        Log.d("searchQuery checkEventStatusMethod", searchQuery.toString())
        val searchQueryObj = SearchQuery(searchQuery)
        Log.d("searchQueryObj checkEventStatusMethod", searchQueryObj.toString())
        val json = Gson().toJson(searchQueryObj).toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url("$URL/events/status").post(json).addHeader("Bearer-Authorization", "${PreferenceManager.getAuthToken()}").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                when (response.code) {
                    200 -> callback(true, responseBody)
                    else -> callback(false, "Error: ${response.code} $responseBody")
                }
            }
        })
    }
    fun updateEventStatus(searchQuery: Int, callback: (Boolean, String?) -> Unit){
        val searchQueryObj = SearchQuery(searchQuery)
        val json = Gson().toJson(searchQueryObj).toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url("$URL/events/statusUpdate").post(json).addHeader("Bearer-Authorization", "${PreferenceManager.getAuthToken()}").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                when (response.code) {
                    200 -> callback(true, responseBody)
                    else -> callback(false, "Error: ${response.code} $responseBody")
                }
            }
        })
    }
    fun searchScenarios(searchQuery: Int, callback: (Boolean, String?) -> Unit) {
        val searchQueryObj = SearchQuery(searchQuery)
        val json = Gson().toJson(searchQueryObj).toRequestBody("application/json".toMediaType())
        val token = PreferenceManager.getAuthToken()
        val request = Request.Builder().url("$URL/scenarios/search").post(json).addHeader("Bearer-Authorization", "$token").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                when (response.code) {
                    200 -> callback(true, responseBody)
                    else -> callback(false, "Error: ${response.code} $responseBody")
                }
            }
        })
    }
    fun searchAllScenarios(searchQuery: String, callback: (Boolean, String?) -> Unit) {
        val json = Gson().toJson(searchQuery).toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url("$URL/scenarios/search-all").post(json).addHeader("Bearer-Authorization", "$searchQuery").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                when (response.code) {
                    200 -> callback(true, responseBody)
                    else -> callback(false, "Error: ${response.code} $responseBody")
                }
            }
        })
    }
    data class SearchQuery2(val searchQuery: Int)
    fun searchSteps(searchQuery: Int, callback: (Boolean, String?) -> Unit) {
        val token = PreferenceManager.getAuthToken()
        val searchQueryObj = SearchQuery2(searchQuery)
        val json = Gson().toJson(searchQueryObj).toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url("$URL/steps/search").post(json).addHeader("Bearer-Authorization", "$token").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                when (response.code) {
                    200 -> callback(true, responseBody)
                    else -> callback(false, "Error: ${response.code} $responseBody")
                }
            }
        })
    }



}