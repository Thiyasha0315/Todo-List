package com.example.assignment4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Delayed transition to the next activity after 3 seconds
        android.os.Handler().postDelayed({
            val intent = Intent(this, ToDoList::class.java)
            startActivity(intent)
            // Finish current activity to avoid user going back to this screen
            finish()
        }, 3000) // 3000 milliseconds = 3 seconds
    }
}
