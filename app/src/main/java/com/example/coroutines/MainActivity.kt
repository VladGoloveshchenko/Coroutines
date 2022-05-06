package com.example.coroutines

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonsContainer = findViewById<LinearLayout>(R.id.buttons_container)

        val buttonClickListener = View.OnClickListener {
            val fragment = when (it.id) {
                R.id.text_stopwatch -> StopwatchFragment()
                R.id.text_flat_map -> FlatMapFlowFragment()
                R.id.text_list -> ListFragment()
                else -> return@OnClickListener
            }

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }

        buttonsContainer
            .children
            .forEach {
                it.setOnClickListener(buttonClickListener)
            }

        supportFragmentManager
            .addOnBackStackChangedListener {
                buttonsContainer.isVisible = supportFragmentManager.backStackEntryCount == 0
            }
    }
}