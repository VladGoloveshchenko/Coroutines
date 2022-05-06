package com.example.coroutines

import kotlinx.coroutines.delay

class DataSource {
    val list = listOf(
        "callback",
        "flow",
        "list",
        "item",
        "element",
        "element1",
        "element2",
        "element3",
        "data",
        "source"
    )

    suspend fun getData(query: String = ""): List<String> {
        delay(1000)
        return list.filter { it.contains(query, ignoreCase = true) }
    }
}