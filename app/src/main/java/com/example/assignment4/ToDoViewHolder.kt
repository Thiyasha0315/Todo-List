package com.example.assignment4

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ToDoViewHolder (view: View): RecyclerView.ViewHolder(view){
    val cbTodo: CheckBox
    val ivDelete: ImageView
    val tvItemName: TextView = itemView.findViewById(R.id.tvItemName)
    val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
    val tvPriority: TextView = itemView.findViewById(R.id.tvPriority)
    val tvDeadline: TextView = itemView.findViewById(R.id.tvDeadline)
    val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)


    init {
        cbTodo = view.findViewById(R.id.cbTodo)
        ivDelete = view.findViewById(R.id.ivDelete)
    }
}