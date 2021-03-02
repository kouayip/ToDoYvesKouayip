package com.ykams.todo.task

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.ykams.todo.tasklist.Task

class EditTask: ActivityResultContract<Task, Task>() {
    override fun createIntent(context: Context, task: Task?): Intent = Intent(context, TaskActivity::class.java).apply {
        putExtra(TaskActivity.TASK_KEY, task)
    }

    override fun parseResult(resultCode: Int, result: Intent?): Task? = when {
        resultCode != Activity.RESULT_OK -> null
        else -> result?.getParcelableExtra(TaskActivity.TASK_KEY)
    }
}