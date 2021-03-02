package com.ykams.todo.tasklist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ykams.todo.databinding.FragmentTaskListBinding
import com.ykams.todo.task.EditTask
import com.ykams.todo.task.TaskActivity
import java.util.*

class TaskListFragment: Fragment() {
    private var _binding: FragmentTaskListBinding? = null

    private var taskAdapter : TaskListAdapter = TaskListAdapter()

    private val binding get() = _binding!!

    private val taskList = mutableListOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    )

    private val openEditTaskActivityCustom = registerForActivityResult(EditTask()) { task ->
        if(task != null) {
            val index: Int = taskList.indexOfFirst { t -> t.id == task.id }
            if (index > 0) {
                taskList[index] = task
                taskAdapter.submitList(taskList.toList())
            }
        }
    }

    private val openAddTaskActivityCustom  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK) {
            val task = result.data?.getSerializableExtra(TaskActivity.TASK_KEY) as? Task
            if (task != null) {
                taskList.add(task)
                taskAdapter.submitList(taskList.toList())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskAdapter.onDeleteTask = { task ->
            taskList.remove(task)
            taskAdapter.submitList(taskList.toList())
        }

        taskAdapter.onEditTask = { task -> openEditTaskActivityCustom.launch(task) }

        binding.fbAddTask.setOnClickListener {
            openAddTaskActivityCustom.launch(Intent(activity, TaskActivity::class.java))
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = taskAdapter
        }

        taskAdapter.submitList(taskList.toList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}