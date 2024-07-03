package com.example.konsystsecureapp.data
import java.io.File
import java.io.Serializable

data class Event(
    val id: Int,
    val title: String,
    val date: String,
    val scenariosCount: Int,
    val scenariosComplete: Int? = null,
    val userId: Int? = null,
    val status: EventStatus
) : Serializable

enum class EventStatus {
    UPCOMING, // Предстоящее событие
    ONGOING, // Текущее событие
    EXPIRED, // Событие истекло
    REVIEW // Событие на проверке
}
data class Scenario(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val steps: List<Step>,
    val isCompleted: Boolean = false,
    val eventFrom: String? = null
) : Serializable

data class Step(
    val id: Int,
    val title : String,
    val scenarioId: Int,
    val action: String,
    val number: Int,
    var photoPaths: MutableList<String> = mutableListOf(),
    var videoPath: String? = null,
) : Serializable

data class Action(
    val type: ActionType,
    val content: String
) : Serializable

enum class ActionType {
    TEXT,
    PHOTO,
    VIDEO
}
data class CreateDataRequest(
    val userId: Int?,
    val eventId: Int?,
    val scenarioId: Int?,
    val stepId: Int?,
    val videoFile: File? = null,
    val photoFiles: List<File>? = null,
    val userComment: String? = null
) : Serializable




