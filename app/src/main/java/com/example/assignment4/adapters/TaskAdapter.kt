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
    private var items: MutableList<ToDo> = mutableListOf()
    private var sortedItems: MutableList<ToDo> = mutableListOf()
    val repository = repository
    val viewModel = viewModel
    val activity = activity

    // Sorting options enum
    enum class SortOption {
        PRIORITY_LOWEST_TO_HIGHEST,
        PRIORITY_HIGHEST_TO_LOWEST,
        RECENT_DEADLINE,
        OLDEST_DEADLINE
    }
    // Current sort option
    private var currentSortOption: SortOption? = null

    fun setItems(newItems: List<ToDo>) {
        items.clear()
        items.addAll(newItems)
        sortedItems.clear()
        sortedItems.addAll(items)
        currentSortOption?.let { sortItems(it) }
        notifyDataSetChanged()
    }

    // Function to sort items based on the given option
    fun sortItems(option: SortOption) {
        currentSortOption = option
        when (option) {
            SortOption.PRIORITY_LOWEST_TO_HIGHEST -> sortedItems.sortBy { it.priority }
            SortOption.PRIORITY_HIGHEST_TO_LOWEST -> sortedItems.sortByDescending { it.priority }
            SortOption.RECENT_DEADLINE -> sortedItems.sortByDescending { it.deadline }
            SortOption.OLDEST_DEADLINE -> sortedItems.sortBy { it.deadline }
        }
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_item, parent, false)
        context = parent.context
        return ToDoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val currentItem = sortedItems[position]
        holder.cbTodo.text = currentItem.item

        holder.tvItemName.text = currentItem.item
        holder.tvDescription.text = currentItem.description
        holder.tvPriority.text = currentItem.priority
        holder.tvDeadline.text = currentItem.deadline

        holder.itemView.setOnLongClickListener {
            activity.showEditDialog(currentItem) // Call showEditDialog from ToDoList instance
            true
        }
        holder.ivDelete.setOnClickListener {
            val isChecked = holder.cbTodo.isChecked
            if(isChecked){
                CoroutineScope(Dispatchers.IO).launch {
                    repository.delete(currentItem)
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
        currentSortOption?.let { sortItems(it) } // Maintain sorting order
    }
    override fun getItemCount(): Int {
        return sortedItems.size
    }


}