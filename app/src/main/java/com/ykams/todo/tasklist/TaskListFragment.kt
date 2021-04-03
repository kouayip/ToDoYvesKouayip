package com.ykams.todo.tasklist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.ykams.todo.databinding.FragmentTaskListBinding
import com.ykams.todo.network.Api
import com.ykams.todo.network.TaskListViewModel
import com.ykams.todo.network.TasksRepository
import com.ykams.todo.task.EditTask
import com.ykams.todo.task.TaskActivity
import com.ykams.todo.userinfo.UserInfoActivity
import kotlinx.coroutines.launch

class TaskListFragment: Fragment() {
    private var _binding: FragmentTaskListBinding? = null

    private var taskAdapter : TaskListAdapter = TaskListAdapter()

    private val binding get() = _binding!!

    // On récupère une instance de ViewModel
    private val viewModel: TaskListViewModel by viewModels()

    private val taskList = mutableListOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    )

    private val openEditTaskActivityCustom = registerForActivityResult(EditTask()) { task ->
        if(task != null) {
            viewModel.editTask(task)
        }
    }

    private val openAddTaskActivityCustom  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK) {
            val task = result.data?.getParcelableExtra(TaskActivity.TASK_KEY) as? Task
            if (task != null) {
                viewModel.addTask(task)
            }
        }
    }

    private val openUserInfoActivity  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        savedInstanceState?.getParcelableArrayList<Task>(SAVE_TASK_LIST).apply {
            this?.let {
                taskList.clear()
                taskList.addAll(it.toList())
            }
        }

        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView.setOnClickListener {
            openUserInfoActivity.launch(Intent(activity, UserInfoActivity::class.java))
        }

        taskAdapter.onDeleteTask = { task ->
            viewModel.deleteTask(task)
        }

        taskAdapter.onEditTask = { task -> openEditTaskActivityCustom.launch(task) }

        taskAdapter.onShareTask = { task ->
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Title: ${task.title}\nDescription: ${task.description}")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        binding.fbAddTask.setOnClickListener {
            openAddTaskActivityCustom.launch(Intent(activity, TaskActivity::class.java))
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = taskAdapter
        }

        viewModel.taskList.observe(viewLifecycleOwner) { newList ->
            taskAdapter.submitList(newList)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(SAVE_TASK_LIST, ArrayList(taskList))
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            val userInfo = Api.userService.getInfo().body()!!
            binding.textView.text = "${userInfo.firstName} ${userInfo.lastName}"
            binding.imageView.load(userInfo.avatar)
        }

        viewModel.loadTasks()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val SAVE_TASK_LIST = "SAVE_TASK_LIST"
    }
}