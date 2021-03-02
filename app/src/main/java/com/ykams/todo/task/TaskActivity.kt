package com.ykams.todo.task

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ykams.todo.databinding.ActivityTaskBinding
import com.ykams.todo.tasklist.Task
import java.io.Serializable
import java.util.*

class TaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    handleSendText(intent) // Handle text being sent
                }
            }
        }

        val task = intent.getParcelableExtra(TASK_KEY) as? Task

        if(task != null){
            binding.editTextTitle.setText(task.title)
            binding.editTextDesc.setText(task.description)
        }

        binding.btnValidation.setOnClickListener {
            val title: String = binding.editTextTitle.text.toString()
            val desc: String = binding.editTextDesc.text.toString()
            val id: String = task?.id ?: UUID.randomUUID().toString()

            if(title.isNotEmpty()){
                val newTask = Task(id, title, desc)
                intent.putExtra(TASK_KEY, newTask)
                setResult(RESULT_OK, intent)
                finish()
            }else{
                Toast.makeText(this, "Veillez renseigner un titre ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let { text ->
            binding.editTextDesc.setText(text)
        }
    }

    companion object {
        const val TASK_KEY = "task_key"
    }
}
