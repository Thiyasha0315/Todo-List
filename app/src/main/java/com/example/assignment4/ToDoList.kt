package com.example.assignment4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment4.adapters.TaskAdapter
import com.example.assignment4.database.ToDoDatabase
import com.example.assignment4.database.entities.ToDo
import com.example.assignment4.database.repository.ToDoRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToDoList : AppCompatActivity() {
    private lateinit var adapter:TaskAdapter
    private lateinit var viewModel:ToDoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_to_do_list)
        val repository = ToDoRepo(ToDoDatabase.getInstance(this))
        val recyclerView: RecyclerView = findViewById(R.id.rvToDoList)
        viewModel = ViewModelProvider(this)[ToDoViewModel::class.java]
        viewModel.data.observe(this) {
            adapter = TaskAdapter(it, repository, viewModel)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
        CoroutineScope(Dispatchers.IO).launch {
            val data = repository.getAllTodoItems()
            runOnUiThread {
                viewModel.setData(data)
            }
        }
        val btnAddItem: Button = findViewById(R.id.btnAddToDo)
        btnAddItem.setOnClickListener {
            displayDialog(repository)
        }
    }

    private fun displayDialog(repository: ToDoRepo) {
        val builder = AlertDialog.Builder(this)
        // Set the alert dialog title and message
        builder.setTitle("Enter New Todo item:")
        builder.setMessage("Enter the todo item below:")
        // Create an EditText input field
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("OK") { dialog, which ->
            val item = input.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                repository.insert(ToDo(item))
                val data = repository.getAllTodoItems()
                runOnUiThread {
                    viewModel.setData(data)
                }
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}