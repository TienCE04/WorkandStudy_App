package com.example.workandstudy_app.todolist.todo_schedule

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workandstudy_app.Database.AppDatabase
import com.example.workandstudy_app.R
import com.example.workandstudy_app.data_school_schedule.DayoftheWeeks

import com.example.workandstudy_app.databinding.FragmentTasksBinding
import com.example.workandstudy_app.school_schedule.CheckDateTime
import com.example.workandstudy_app.todolist.Entity.TasksData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FragTasks : Fragment(), CalendarAdapterTodo.OnItemListener, TaskAdapter.TaskItemListener {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private lateinit var monthYearText: TextView
    private lateinit var calendarRecyclerView: RecyclerView
    private var selectedDate: LocalDate = LocalDate.now()
    private lateinit var tasksRecyclerView: RecyclerView
    private var keyID: String = ""
    private var listener: OnDaySelectedListener? = null
    private lateinit var calendarAdapter: CalendarAdapterTodo
    private lateinit var viewModel: SharedViewModelTodo
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var dialog: Dialog
    private lateinit var timeTask: EditText
    private lateinit var huyTask: TextView
    private lateinit var luuTask: TextView
    private lateinit var tieuDe: EditText
    private lateinit var noiDung: EditText
    private val calendar = Calendar.getInstance()
    private val checkTime = CheckDateTime()
    private var countTasks=0

    // BroadcastReceiver để nhận thông báo task đã được thêm
    private val taskAddedReceiver = object : BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "TASK_ADDED_ACTION") {
                val receivedKeyID = intent.getStringExtra("KEY_ID") ?: ""
                if (receivedKeyID.isNotEmpty()) {
                    viewModel.loadTasks("$receivedKeyID%")
                    setWeekView()
                }
            }
        }
    }

    interface OnDaySelectedListener {
        fun onDaySelected(keyID: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDaySelectedListener) {
            listener = context
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        initWidgets()
        initTaskAdapter()
        initViewModel()
        setWeekView()
        //
        viewModel.loadTasks("${selectedDate.dayOfMonth}${selectedDate.monthValue}${selectedDate.year}%")

        val dayOfWeek = selectedDate.dayOfWeek
        val vietnameseDay = when (dayOfWeek) {
            java.time.DayOfWeek.MONDAY -> "Thứ 2"
            java.time.DayOfWeek.TUESDAY -> "Thứ 3"
            java.time.DayOfWeek.WEDNESDAY -> "Thứ 4"
            java.time.DayOfWeek.THURSDAY -> "Thứ 5"
            java.time.DayOfWeek.FRIDAY -> "Thứ 6"
            java.time.DayOfWeek.SATURDAY -> "Thứ 7"
            java.time.DayOfWeek.SUNDAY -> "Chủ nhật"
        }
        //
        binding.ngayThang.text =
            vietnameseDay + " ngày " + selectedDate.dayOfMonth + " tháng " + selectedDate.monthValue

        // Create Dialog
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_work_activity)
        tieuDe = dialog.findViewById<EditText>(R.id.nameTask)
        luuTask = dialog.findViewById<TextView>(R.id.luuTask)
        huyTask = dialog.findViewById<TextView>(R.id.huyTask)
        timeTask = dialog.findViewById<EditText>(R.id.timeWork)
        noiDung = dialog.findViewById<EditText>(R.id.detailTask)

        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog)
        val window = dialog.window
        window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),// chiều rộng
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        // Đăng ký BroadcastReceiver
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(taskAddedReceiver, IntentFilter("TASK_ADDED_ACTION"))

        binding.prevButton.setOnClickListener {
            selectedDate = selectedDate.minusWeeks(1)
            setWeekView()
        }
        binding.nextButton.setOnClickListener {
            selectedDate = selectedDate.plusWeeks(1)
            setWeekView()
        }
        return binding.root
    }

    override fun onDestroyView() {
        // Hủy đăng ký BroadcastReceiver để tránh rò rỉ bộ nhớ
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(taskAddedReceiver)
        super.onDestroyView()
        _binding = null
    }

    private fun initWidgets() {
        calendarRecyclerView = binding.dsLichTuan
        tasksRecyclerView = binding.dsTasks
        monthYearText = binding.monthYearTV
    }

    private fun initTaskAdapter() {
        taskAdapter = TaskAdapter(this)
        tasksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        tasksRecyclerView.adapter = taskAdapter
        tasksRecyclerView.setItemViewCacheSize(20)

    }

    private fun initViewModel() {
        val repository = TaskRepository(AppDatabase.getDatabase(requireContext()).tasksDao())
        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(repository)
        )[SharedViewModelTodo::class.java]

        viewModel.selectedTasks.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
            countTasks=tasks.size
            if(countTasks<=1){
                binding.numberTasks.text = "You have ${countTasks} task for today"
            }
            else{
                binding.numberTasks.text = "You have ${countTasks} tasks for today"
            }
            Log.d("FragTasks", "Updated tasks: ${tasks.size}")
        }
    }

    private fun setWeekView() {
        val daysInWeek = daysInWeekArray(selectedDate)
        val currentDayOfWeek = selectedDate.dayOfWeek.value
        val daysToSubtract = if (currentDayOfWeek == 7) 0 else currentDayOfWeek
        val startOfWeek = selectedDate.minusDays(daysToSubtract.toLong())

        if (startOfWeek.month != selectedDate.month || selectedDate.dayOfMonth <= 7) {
            monthYearText.text = monthYearFromDate(startOfWeek)
        } else {
            monthYearText.text = monthYearFromDate(selectedDate)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            for (i in 0..6) {
                val day = startOfWeek.plusDays(i.toLong())
                val dayText = day.dayOfMonth.toString()
                val month = day.monthValue // Sử dụng monthValue thay vì month
                val year = day.year

                val pattern = "$dayText$month$year" // Ví dụ: "142025" cho ngày 1/4/2025

                // Gọi trực tiếp repository.getIdCount để lấy số lượng nhiệm vụ
                val count = withContext(Dispatchers.IO) {
                    val repository =
                        TaskRepository(AppDatabase.getDatabase(requireContext()).tasksDao())
                    repository.countTasks("$pattern%")
                }

                if (count > 0) {
                    daysInWeek[i + 7].hasSchedule = true
                    Log.d("FragTasks", "Day $dayText has $count tasks, hasSchedule = true")
                } else {
                    Log.d("FragTasks", "Day $dayText has no tasks")
                }
            }
            updateCalendarAdapter(daysInWeek)
        }
    }

    private fun updateCalendarAdapter(daysInWeek: ArrayList<DayoftheWeeks>) {
        calendarAdapter = CalendarAdapterTodo(daysInWeek, this, selectedDate)
        calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        calendarRecyclerView.adapter = calendarAdapter
    }

    private fun daysInWeekArray(date: LocalDate): ArrayList<DayoftheWeeks> {
        val daysInWeekArray = ArrayList<DayoftheWeeks>()
        val dayNames = arrayListOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        dayNames.forEach { daysInWeekArray.add(DayoftheWeeks(it)) }
        val currentDayOfWeek = date.dayOfWeek.value
        val daysToSubtract = if (currentDayOfWeek == 7) 0 else currentDayOfWeek
        val startOfWeek = date.minusDays(daysToSubtract.toLong())

        for (i in 0..6) {
            val day = startOfWeek.plusDays(i.toLong())
            daysInWeekArray.add(DayoftheWeeks(day.dayOfMonth.toString()))
        }

        return daysInWeekArray
    }

    private fun monthYearFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    @SuppressLint("SetTextI18n")
    override fun onItemClick(position: Int, dayText: String) {
        if (position > 6) {
            val currentDayOfWeek = selectedDate.dayOfWeek.value
            val daysToSubtract = if (currentDayOfWeek == 7) 0 else currentDayOfWeek
            val startOfWeek = selectedDate.minusDays(daysToSubtract.toLong())
            val selectedLocalDate = startOfWeek.plusDays((position - 7).toLong())

            val selectedDay = selectedLocalDate.dayOfMonth
            val month = selectedLocalDate.monthValue
            val year = selectedLocalDate.year

            Toast.makeText(
                requireContext(),
                "Ngày ${selectedDay} tháng ${month} năm ${year}",
                Toast.LENGTH_SHORT
            ).show()

            keyID = "${selectedDay}${month}${year}"
            listener?.onDaySelected(keyID)
            viewLifecycleOwner.lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    viewModel.loadTasks("$keyID%")
                }
            }
            if (position != 7) {
                binding.ngayThang.text =
                    "Thứ " + "${position - 6}" + " ngày $selectedDay tháng $month"
            } else {
                binding.ngayThang.text = "Chủ nhật ngày $selectedDay tháng $month"
            }
        }
    }

    override fun onCheckChanged(task: TasksData) {
        viewModel.updateTaskFromDialog(task)
    }

    override fun onDetailClicked(task: TasksData) {
        tieuDe.setText(task.titleTask)
        noiDung.setText(task.contentTask)
        timeTask.setText(task.timeTask)
        dialog.show()
        luuTask.setOnClickListener {
            if (tieuDe.text.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Vui lòng nhập tiêu đề công việc!",
                    Toast.LENGTH_SHORT
                ).show()
                tieuDe.requestFocus()
                return@setOnClickListener
            }
            if (timeTask.text.isEmpty()) {
                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                timeTask.setText(currentTime)
            }
            task.contentTask = noiDung.text.toString()
            dialog.dismiss()
            viewModel.updateTaskFromDialog(
                task.copy(
                    titleTask = tieuDe.text.toString(),
                    timeTask = timeTask.text.toString()
                )
            )
        }
            huyTask.setOnClickListener {
                dialog.dismiss()
            }
        timeTask.setOnClickListener {
            TimePickerDialog(
                requireContext(), TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    val formattedTime = checkTime.checkTime(hour, minute)
                    timeTask.setText(formattedTime)
                }, calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    override fun onDeleteClicked(task: TasksData) {
        viewModel.deleteTask(task)
        setWeekView()
    }

    override fun onFlagChanged(task: TasksData) {
        viewModel.updateTaskFromDialog(task)
    }
}