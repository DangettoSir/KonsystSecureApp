package com.example.konsystsecureapp.Preferences

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    private const val PREFS_NAME = "auth_prefs"
    private const val EVENT_ID_KEY = "selected_event_id"
    private const val SCENARIO_ID_KEY = "selected_scenario_id"
    private const val STEP_ID_KEY = "selected_step_id"
    private const val AUTH_TOKEN_KEY = "auth_token"
    private const val PROTECTED_AUTH_TOKEN_KEY = "protected_auth_token"
    private const val USERNAME_KEY = "username_key"
    private const val USERNICKNAME_KEY = "usernickname_key"
    private const val EVENTTITLE_KEY = "eventTitle_key"
    private const val USERID = "userid"
    private const val VIDEO_FILE = "video_prefs"
    private const val STEP_IDS_KEY = "step_ids"
    private const val VIDEO_FILE_KEY = "video_file_%d"
    private const val PREF_FILE = "app_preferences"
    private const val IMAGE_PATH_PREFIX = "image_path_"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        if (!::sharedPreferences.isInitialized) {
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }


    fun saveImagePath(stepId: Int, index: Int, imagePath: String) {
        val sharedPreferences = getSharedPreferences()
        sharedPreferences.edit()
            .putString("$IMAGE_PATH_PREFIX$stepId,$index", imagePath)
            .apply()
    }
    fun getImagePaths(stepId: Int): List<String?> {
        val sharedPreferences = getSharedPreferences()
        val paths = mutableListOf<String?>()
        var index = 0
        while (true) {
            val path = sharedPreferences.getString("$IMAGE_PATH_PREFIX$stepId,$index", null)
            if (path == null) break
            paths.add(path)
            index++
        }
        return paths
    }
    fun removeImagePath(stepId: Int, index: Int) {
        val sharedPreferences = getSharedPreferences()
        sharedPreferences.edit()
            .remove("$IMAGE_PATH_PREFIX$stepId,$index")
            .apply()
    }
    fun getSharedPreferences(): SharedPreferences {
        return sharedPreferences
    }

    fun saveUserId(userId: Int){
        sharedPreferences.edit()
            .putInt(USERID, userId)
            .apply()
    }
    fun getUserId(): Int?{
        val userId = sharedPreferences.getInt(USERID, -1)
        return if (userId == -1) null else userId
    }


    fun saveStepIds(stepIds: List<Int>) {
        val stepIdsString = stepIds.joinToString(",")
        sharedPreferences.edit()
            .putString(STEP_IDS_KEY, stepIdsString)
            .apply()
    }

    fun getStepIds(): List<Int> {
        val stepIdsString = sharedPreferences.getString(STEP_IDS_KEY, "")
        return stepIdsString?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()
    }
    fun clearStepIds() {
        sharedPreferences.edit()
            .remove(STEP_IDS_KEY)
            .apply()
    }
    fun saveEventId(eventId: Int) {
        sharedPreferences.edit()
            .putInt(EVENT_ID_KEY, eventId)
            .apply()
    }


    fun getEventId(): Int? {
        val eventId = sharedPreferences.getInt(EVENT_ID_KEY, -1)
        return if (eventId == -1) null else eventId
    }
    fun saveScenarioId(scenarioId: Int) {
        sharedPreferences.edit()
            .putInt(SCENARIO_ID_KEY, scenarioId)
            .apply()
    }
    fun saveStepId(stepId: Int) {
        sharedPreferences.edit()
            .putInt(STEP_ID_KEY, stepId)
            .apply()
    }
    fun removeStepId() {
        sharedPreferences.edit()
            .remove(STEP_ID_KEY)
            .apply()
    }
    fun getStepId(): Int? {
        val stepId = sharedPreferences.getInt(STEP_ID_KEY, -1)
        return if (stepId == -1) null else stepId
    }

    fun getScenarioId(): Int? {
        val scenarioId = sharedPreferences.getInt(SCENARIO_ID_KEY, -1)
        return if (scenarioId == -1) null else scenarioId
    }
    fun saveAuthToken(token: String) {
        sharedPreferences.edit()
            .putString(AUTH_TOKEN_KEY, token)
            .apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(AUTH_TOKEN_KEY, null)
    }

    fun clearAuthToken() {
        with(sharedPreferences.edit()) {
            remove(AUTH_TOKEN_KEY)
            apply()
        }
    }
    fun saveProtectedAuthToken(protectedToken: String) {
        sharedPreferences.edit()
            .putString(PROTECTED_AUTH_TOKEN_KEY, protectedToken)
            .apply()
    }

    fun getProtectedAuthToken(): String? {
        return sharedPreferences.getString(PROTECTED_AUTH_TOKEN_KEY, null)
    }
    fun clearProtectedAuthToken() {
        with(sharedPreferences.edit()) {
            remove(PROTECTED_AUTH_TOKEN_KEY)
            apply()
        }
    }

    fun saveUsername(protectedToken: String) {
        sharedPreferences.edit()
            .putString(USERNAME_KEY, protectedToken)
            .apply()
    }

    fun getUsername(): String? {
        return sharedPreferences.getString(USERNAME_KEY, null)
    }
    fun clearUsername() {
        with(sharedPreferences.edit()) {
            remove(USERNAME_KEY)
            apply()
        }
    }

    fun saveUserNickname(protectedToken: String) {
        sharedPreferences.edit()
            .putString(USERNICKNAME_KEY, protectedToken)
            .apply()
    }

    fun getUserNickname(): String? {
        return sharedPreferences.getString(USERNICKNAME_KEY, null)
    }
    fun clearUserNickname() {
        with(sharedPreferences.edit()) {
            remove(USERNICKNAME_KEY)
            apply()
        }
    }
    fun saveEventTitle(title: String) {
        sharedPreferences.edit()
            .putString(EVENTTITLE_KEY, title)
            .apply()
    }

    fun getEventTitle(): String? {
        return sharedPreferences.getString(EVENTTITLE_KEY, null)
    }
    fun clearEventTitle() {
        with(sharedPreferences.edit()) {
            remove(EVENTTITLE_KEY)
            apply()
        }
    }

    fun saveVideoPath(stepId: Int, videoPath: String) {
        val key = VIDEO_FILE_KEY.format(stepId)
        sharedPreferences.edit()
            .putString(key, videoPath)
            .apply()
    }

    fun getVideoPath(stepId: Int): String? {
        val key = VIDEO_FILE_KEY.format(stepId)
        return sharedPreferences.getString(key, null)
    }
    fun removeVideoPath(stepId: Int) {
        val key = VIDEO_FILE_KEY.format(stepId)
        sharedPreferences.edit()
            .remove(key)
            .apply()
    }

    fun clearVideoPathForStep(stepId: Int) {
        val key = VIDEO_FILE_KEY.format(stepId)
        sharedPreferences.edit()
            .remove(key)
            .apply()
    }
}