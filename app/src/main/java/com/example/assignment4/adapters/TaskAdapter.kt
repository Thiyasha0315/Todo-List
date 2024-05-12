package com.example.assignment4.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment4.R
import com.example.assignment4.ToDoList
import com.example.assignment4.ToDoViewHolder
import com.example.assignment4.ToDoViewModel
import com.example.assignment4.database.entities.ToDo
import com.example.assignment4.database.repository.ToDoRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskAdapter(
    items: MutableList<ToDo>, repository: ToDoRepo,
    viewModel: ToDoViewModel,
    activity: ToDoList,
) : RecyclerView.Adapter<ToDoViewHolder>() {
    var context: Context? = null
    val items = items
    val repository = repository
    val viewModel = viewModel
    val activity = activity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_item, parent, false)
        context = parent.context
        return ToDoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val currentItem = items[position]
        holder.cbTodo.text = currentItem.item

        holder.tvItemName.text = currentItem.item
        holder.tvDescription.text = currentItem.description
        holder.tvPriority.text = currentItem.priority
        holder.tvDeadline.text = currentItem.deadline
        
        holder.cbTodo.setOnLongClickListener {
            activity.showEditDialog(items[position]) // Call showEditDialog from ToDoList instance
            true
        }
        holder.ivDelete.setOnClickListener {
            val isChecked = holder.cbTodo.isChecked
            if(isChecked){
                CoroutineScope(Dispatchers.IO).launch {
                    repository.delete(items.get(position))
                    val data = repository.getAllTodoItems()
                    withContext(Dispatchers.Main){
                        viewModel.setData(data)
                    }
                }
                Toast.makeText(context,"Item Deleted", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context,"Select the item to delete", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun updateData(newItems: List<ToDo>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return items.size
    }
}