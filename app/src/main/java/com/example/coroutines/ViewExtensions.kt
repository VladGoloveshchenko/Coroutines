package com.example.coroutines

import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

val Toolbar.searchQueryFlow: Flow<String>
    get() = callbackFlow {
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView

        val queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                trySend(newText)
                return true
            }
        }

        searchView.setOnQueryTextListener(queryTextListener)

        awaitClose {
            searchView.setOnQueryTextListener(null)
        }
    }

fun EditText.onTextChanged() = callbackFlow {
    val textWatcher = addTextChangedListener { value -> trySend(value.toString()) }

    awaitClose {
        removeTextChangedListener(textWatcher)
    }
}

fun TextView.onClickListener() = callbackFlow {
    this@onClickListener.setOnClickListener {
        trySend(Unit)
    }

    awaitClose()
}