package com.ykams.todo.tasklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ykams.todo.databinding.FragmentTaskListBinding
import java.util.*

class TaskListFragment: Fragment() {
    private var _binding: FragmentTaskListBinding? = null

    private val binding get() = _binding!!

    private val taskList = mutableListOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val taskAdapter = TaskListAdapter()
        taskAdapter.submitList(taskList.toList())

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = taskAdapter
        }

        binding.fbAddTask.setOnClickListener {
            taskList.add(Task(id = UUID.randomUUID().toString(), title = "Task ${taskList.size + 1}"))
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
    }
}