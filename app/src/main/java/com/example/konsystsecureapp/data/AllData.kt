package com.example.konsystsecureapp.data
import java.io.Serializable

data class Event(
    val id: Int,
    val title: String,
    val date: String,
    val scenarios: List<Scenario>,
    val scenariosCount: Int,
    val scenariosComplete: Int? = null,
    val userId: Int? = null,
    val userIds: List<Int>? = null,
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
    val isCompleted: Boolean = false
) : Serializable

data class Step(
    val id: Int,
    val title : String,
    val scenarioId: Int,
    val action: String,
    val number: Int
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



