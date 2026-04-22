package com.example.managemate

data class Task(
    var title: String,
    val subtasks: MutableList<String> = mutableListOf()
)