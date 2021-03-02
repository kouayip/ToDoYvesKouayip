package com.ykams.todo.tasklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ykams.todo.R
import com.ykams.todo.databinding.ItemTaskBinding

object TasksDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id && oldItem.title == newItem.title && oldItem.description == newItem.description
}

class TaskListAdapter : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TasksDiffCallback) {
    var onDeleteTask: ((Task) -> Unit)? = null
    var onEditTask: ((Task) -> Unit)? = null
    var onShareTask: ((Task) -> Unit)? = null

    inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {

            binding.task = task

            binding.btnDelete.setOnClickListener {
                onDeleteTask?.invoke(task)
            }

            binding.btnEdit.setOnClickListener {
                onEditTask?.invoke(task)
            }

            binding.taskCardView.setOnLongClickListener{
                onShareTask?.invoke(task)
                true
            }
        }
    }

    override fun getItemViewType(position: Int): Int = R.layout.item_task

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemTaskBinding>(layoutInflater, viewType, parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}