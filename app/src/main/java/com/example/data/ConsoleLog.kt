package com.example.data

enum class LogType {
    SYSTEM, AGENT, USER, ERROR
}

data class ConsoleLog(
    val message: String,
    val type: LogType
)
