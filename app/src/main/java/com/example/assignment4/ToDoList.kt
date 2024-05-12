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
    private lateinit var repository: ToDoRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_to_do_list)
        repository = ToDoRepo(ToDoDatabase.getInstance(this))
        val recyclerView: RecyclerView = findViewById(R.id.rvToDoList)
        viewModel = ViewModelProvider(this)[ToDoViewModel::class.java]

        adapter = TaskAdapter(mutableListOf(), repository, viewModel, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.data.observe(this) {
            adapter.updateData(it)
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
        // Create an EditText input field
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_add_todo, null)
        val itemNameEditText = dialogLayout.findViewById<EditText>(R.id.Item)
        val descriptionEditText = dialogLayout.findViewById<EditText>(R.id.Description)
        val priorityEditText = dialogLayout.findViewById<EditText>(R.id.Priority)
        val deadlineEditText = dialogLayout.findViewById<EditText>(R.id.Deadline)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { dialog, which ->
            val item = itemNameEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val priority = priorityEditText.text.toString()
            val deadline = deadlineEditText.text.toString()

            // Create a ToDo object with the input data
            val todo = ToDo(item, description, priority, deadline)

            CoroutineScope(Dispatchers.IO).launch {
                repository.insert(todo)
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
    fun showEditDialog(todo: ToDo) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Todo item:")
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_add_todo, null)
        val itemNameEditText = dialogLayout.findViewById<EditText>(R.id.Item)
        val descriptionEditText = dialogLayout.findViewById<EditText>(R.id.Description)
        val priorityEditText = dialogLayout.findViewById<EditText>(R.id.Priority)
        val deadlineEditText = dialogLayout.findViewById<EditText>(R.id.Deadline)

        itemNameEditText.setText(todo.item)
        descriptionEditText.setText(todo.description)
        priorityEditText.setText(todo.priority)
        deadlineEditText.setText(todo.deadline)

        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { dialog, which ->
            val updatedItem = itemNameEditText.text.toString()
            val updatedDescription = descriptionEditText.text.toString()
            val updatedPriority = priorityEditText.text.toString()
            val updatedDeadline = deadlineEditText.text.toString()
            todo.apply {
                item = updatedItem
                description = updatedDescription
                priority = updatedPriority
                deadline = updatedDeadline
            }
            CoroutineScope(Dispatchers.IO).launch {
                repository.update(todo)
                val data = repository.getAllTodoItems()
                runOnUiThread {
                    viewModel.setData(data)
                }
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }

}
