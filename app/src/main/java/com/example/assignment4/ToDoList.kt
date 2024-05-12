package com.example.assignment4

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment4.R.*
import com.example.assignment4.adapters.TaskAdapter
import com.example.assignment4.database.ToDoDatabase
import com.example.assignment4.database.entities.ToDo
import com.example.assignment4.database.repository.ToDoRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ToDoList : AppCompatActivity() {
    private lateinit var adapter:TaskAdapter
    private lateinit var viewModel:ToDoViewModel
    private lateinit var repository: ToDoRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(layout.activity_to_do_list)
        repository = ToDoRepo(ToDoDatabase.getInstance(this))
        val recyclerView: RecyclerView = findViewById(id.rvToDoList)
        viewModel = ViewModelProvider(this)[ToDoViewModel::class.java]

        adapter = TaskAdapter(mutableListOf(), repository, viewModel, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.data.observe(this) {newData ->
            adapter.setItems(newData)
        }
        CoroutineScope(Dispatchers.IO).launch {
            val data = repository.getAllTodoItems()
            runOnUiThread {
                viewModel.setData(data)
            }
        }
        val spinnerSort: Spinner = findViewById(id.spinnerSort)
        spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> adapter.sortItems(TaskAdapter.SortOption.PRIORITY_LOWEST_TO_HIGHEST)
                    1 -> adapter.sortItems(TaskAdapter.SortOption.PRIORITY_HIGHEST_TO_LOWEST)
                    2 -> adapter.sortItems(TaskAdapter.SortOption.RECENT_DEADLINE)
                    3 -> adapter.sortItems(TaskAdapter.SortOption.OLDEST_DEADLINE)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        val searchView: SearchView = findViewById(id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
        val btnAddItem: Button = findViewById(id.btnAddToDo)
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
        val categoryEditText = dialogLayout.findViewById<EditText>(R.id.Category)
        val datePickerImageView: ImageView = dialogLayout.findViewById(R.id.ivDatePicker)

        datePickerImageView.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(selectedDate.time)
                    deadlineEditText.setText(formattedDate)
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { dialog, which ->
            val item = itemNameEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val priority = priorityEditText.text.toString()
            val deadline = deadlineEditText.text.toString()
            val category = categoryEditText.text.toString()

            // Create a ToDo object with the input data
            val todo = ToDo(item, description, priority, SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(deadline),category)

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
        val categoryEditText = dialogLayout.findViewById<EditText>(R.id.Category)


        itemNameEditText.setText(todo.item)
        descriptionEditText.setText(todo.description)
        priorityEditText.setText(todo.priority)
        deadlineEditText.setText(todo.deadline?.let { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(it) })
        categoryEditText.setText(todo.category)

        deadlineEditText.setOnClickListener {
            val selectedDate = todo.deadline ?: Calendar.getInstance().time
            val calendar = Calendar.getInstance()
            calendar.time = selectedDate

            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
                    deadlineEditText.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { dialog, which ->
            val updatedItem = itemNameEditText.text.toString()
            val updatedDescription = descriptionEditText.text.toString()
            val updatedPriority = priorityEditText.text.toString()
            val updatedDeadline = deadlineEditText.text.toString()
            val updatedCategory = categoryEditText.text.toString()
            todo.apply {
                item = updatedItem
                description = updatedDescription
                priority = updatedPriority
                deadline = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(updatedDeadline)
                category = updatedCategory
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
