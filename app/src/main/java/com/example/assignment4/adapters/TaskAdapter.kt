package com.example.assignment4.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
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
import java.util.Locale

class TaskAdapter(
    private var items: MutableList<ToDo>, private val repository: ToDoRepo,
    private val viewModel: ToDoViewModel,
    private val activity: ToDoList,
) : RecyclerView.Adapter<ToDoViewHolder>(), Filterable {

    private var sortedItems: MutableList<ToDo> = mutableListOf()
    private var searchQuery: String? = null

    // Sorting options enum
    enum class SortOption {
        PRIORITY_LOWEST_TO_HIGHEST,
        PRIORITY_HIGHEST_TO_LOWEST,
        RECENT_DEADLINE,
        OLDEST_DEADLINE
    }

    // Current sort option
    private var currentSortOption: SortOption? = null

    init {
        sortedItems.addAll(items)
    }

    fun setItems(newItems: List<ToDo>) {
        items.clear()
        items.addAll(newItems)
        filterItems()
        notifyDataSetChanged()
    }

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

    fun setSearchQuery(query: String?) {
        searchQuery = query
        filterItems()
    }

    private fun filterItems() {
        sortedItems.clear()
        sortedItems.addAll(
            if (searchQuery.isNullOrEmpty()) {
                items
            } else {
                items.filter { it.item?.contains(searchQuery ?: "", ignoreCase = true) ?: false }
            }
        )
        currentSortOption?.let { sortItems(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_item, parent, false)
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
            if (isChecked) {
                CoroutineScope(Dispatchers.IO).launch {
                    repository.delete(currentItem)
                    val data = repository.getAllTodoItems()
                    withContext(Dispatchers.Main) {
                        viewModel.setData(data)
                    }
                }
                Toast.makeText(holder.itemView.context, "Item Deleted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    holder.itemView.context,
                    "Select the item to delete",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<ToDo>()
                val filterPattern = constraint.toString().lowercase(Locale.getDefault()).trim()

                for (item in items) {
                    if (item.item?.lowercase(Locale.getDefault())?.contains(filterPattern) == true) {
                        filteredList.add(item)
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                sortedItems.clear()
                sortedItems.addAll(results?.values as MutableList<ToDo>)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return sortedItems.size
    }
}