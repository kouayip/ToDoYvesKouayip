package com.ykams.todo.tasklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ykams.todo.BR
import com.ykams.todo.R

object TasksDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id && oldItem.title == newItem.title && oldItem.description == newItem.description
}

class TaskListAdapter : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TasksDiffCallback) {
    inner class TaskViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.setVariable(BR.task, task)
            binding.executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int = R.layout.item_task

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, viewType, parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}