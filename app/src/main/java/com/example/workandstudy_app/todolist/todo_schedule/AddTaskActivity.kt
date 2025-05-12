package com.example.workandstudy_app.todolist.todo_schedule

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workandstudy_app.Database.AppDatabase
import com.example.workandstudy_app.R
import com.example.workandstudy_app.databinding.ActivityAddWorkBinding
import com.example.workandstudy_app.school_schedule.CheckDateTime
import com.example.workandstudy_app.todolist.Entity.TasksData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class AddTaskActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddWorkBinding
    private var keyID = ""
    private lateinit var db: AppDatabase
    private val calendar = Calendar.getInstance()
    private val checkTime = CheckDateTime()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getDatabase(this)
        binding = ActivityAddWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.timeWork.setOnClickListener(this)
        keyID = intent.getStringExtra("keyID") ?: ""

        init()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.Huy -> {
                finish()
            }

            R.id.Them -> {
                if (binding.tieudeWork.text.toString().isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show()
                    return
                } else if (binding.ghichuWork.text.toString().isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show()
                    return
                } else if (binding.timeWork.text.toString().isEmpty()) {
                    Toast.makeText(this, "Vui lòng chọn thời gian", Toast.LENGTH_SHORT).show()
                    return
                } else {
                    val tieuDe = binding.tieudeWork.text.toString().trim()
                    val ghiChu = binding.ghichuWork.text.toString().trim()
                    val tGian = binding.timeWork.text.toString().trim()
                    CoroutineScope(Dispatchers.IO).launch {
                        val taskData = TasksData(
                            taskIdDate = keyID + "${System.currentTimeMillis()}",
                            titleTask = tieuDe,
                            contentTask = ghiChu,
                            timeTask = tGian,
                            tick = false,
                            flag = false
                        )
                        db.tasksDao().insert(taskData)
                        withContext(Dispatchers.Main) {
                            val intent = Intent("TASK_ADDED_ACTION")
                            intent.putExtra("KEY_ID", keyID)
                            LocalBroadcastManager.getInstance(this@AddTaskActivity).sendBroadcast(intent)
                            finish()
                        }
                    }
                }
            }

            R.id.timeWork -> {
                TimePickerDialog(
                    this, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        val formattedTime = checkTime.checkTime(hour, minute)
                        binding.timeWork.setText(formattedTime)
                    }, calendar.get(Calendar.HOUR),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            }
        }
    }

    private fun init() {
        binding.Huy.setOnClickListener(this)
        binding.Them.setOnClickListener(this)
    }


}
