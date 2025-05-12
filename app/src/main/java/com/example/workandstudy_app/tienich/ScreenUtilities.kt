package com.example.workandstudy_app.tienich

import android.app.AlarmManager
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.workandstudy_app.Database.AppDatabase
import com.example.workandstudy_app.R
import com.example.workandstudy_app.databinding.ScreenUtilitiesActivityBinding
import com.example.workandstudy_app.todolist.todo_schedule.SharedViewModelTodo
import com.example.workandstudy_app.todolist.todo_schedule.TaskRepository
import com.example.workandstudy_app.todolist.todo_schedule.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class ScreenUtilities : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ScreenUtilitiesActivityBinding
    private lateinit var dialog: Dialog
    private lateinit var switchNotification: SwitchCompat
    private lateinit var spinner: Spinner
    private lateinit var buttonCancel: TextView
    private lateinit var buttonSave: TextView
    private var textNotification = ""
    private lateinit var viewModel: SharedViewModelTodo
    private var selectedDate: LocalDate = LocalDate.now() // Current selected date
    private val notificationScheduler by lazy { NotificationScheduler(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ScreenUtilitiesActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_notification)
        switchNotification = dialog.findViewById(R.id.switch_toggle)
        spinner = dialog.findViewById(R.id.deltaTime)
        buttonSave = dialog.findViewById(R.id.saveNoti)
        buttonCancel = dialog.findViewById(R.id.huyNoti)
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog)
        val window = dialog.window
        window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        init()
        initViewModel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestExactAlarmPermission()
        }
    }

    private fun initViewModel() {
        val repository = TaskRepository(AppDatabase.getDatabase(this).tasksDao())
        viewModel = ViewModelProvider(
            this, ViewModelFactory(repository)
        )[SharedViewModelTodo::class.java]
    }

    private fun init() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.deltaTime,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        binding.pageTT.setOnClickListener(this)
        binding.setting.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.pageTT -> {
                intent = Intent(this, PageSchoolActivity::class.java)
                startActivity(intent)
            }

            R.id.setting -> {
                dialog.show()
                buttonSave.setOnClickListener {
                    if (switchNotification.isChecked) {
                        textNotification = spinner.selectedItem.toString()
                        val deltaTime = textNotification.replace(" phút", "").toIntOrNull() ?: 0

                        // Lấy danh sách công việc từ Room
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                viewModel.loadTasks("${selectedDate.dayOfMonth}${selectedDate.monthValue}${selectedDate.year}%")
                            }
                            viewModel.selectedTasks.observe(this@ScreenUtilities) { tasks ->
                                // Lên lịch thông báo chỉ dựa trên danh sách công việc
                                notificationScheduler.scheduleNotifications(
                                    emptyList(), // Không sử dụng schedules
                                    tasks ?: emptyList(),
                                    selectedDate,
                                    deltaTime
                                )
                                Toast.makeText(
                                    this@ScreenUtilities,
                                    "Đã cài đặt thông báo trước $deltaTime phút",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dialog.dismiss()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Thông báo đã bị tắt", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
                buttonCancel.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestExactAlarmPermission() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            exactAlarmPermissionLauncher.launch(intent)
        }
    }

    private val exactAlarmPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { /* Xử lý nếu cần */ }
}