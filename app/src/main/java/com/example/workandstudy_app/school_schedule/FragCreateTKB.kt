package com.example.workandstudy_app.school_schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.workandstudy_app.data_school_schedule.CheckCalendar
import com.example.workandstudy_app.databinding.FragmentScheduleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.example.workandstudy_app.R
import java.time.format.DateTimeFormatter

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FragCreateTKB : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentScheduleBinding
    private lateinit var db: FirebaseFirestore
    private val auth = FirebaseAuth.getInstance()
    private var calendar = Calendar.getInstance()
    private val checkTime = CheckDateTime()
    private val TAG = "FragCreateTKB" // Tag cho log

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        db = FirebaseFirestore.getInstance()

        binding.btnSave.setOnClickListener(this)
        binding.btnFresher.setOnClickListener(this)
        binding.edtStartDate.setOnClickListener(this)
        binding.tgStart.setOnClickListener(this)
        binding.tgEnd.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.edtStartDate -> {
                DatePickerDialog(
                    requireContext(),
                    DatePickerDialog.OnDateSetListener { _, year, month, dayofMonth ->
                        val formattedDate = when {
                            dayofMonth < 10 && month < 10 -> "0$dayofMonth/0${month + 1}/$year"
                            dayofMonth < 10 -> "0$dayofMonth/${month + 1}/$year"
                            month < 10 -> "$dayofMonth/0${month + 1}/$year"
                            else -> "$dayofMonth/${month + 1}/$year"
                        }
                        binding.edtStartDate.setText(formattedDate)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), 
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            R.id.tgStart -> {
                TimePickerDialog(
                    requireContext(),
                    TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        val formattedTime = checkTime.checkTime(hour, minute)
                        binding.tgStart.setText(formattedTime)
                    },
                    calendar.get(java.util.Calendar.HOUR),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            }

            R.id.tgEnd -> {
                TimePickerDialog(
                    requireContext(),
                    TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        val formattedTime = checkTime.checkTime(hour, minute)
                        binding.tgEnd.setText(formattedTime)
                    },
                    calendar.get(java.util.Calendar.HOUR),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            }

            R.id.btnSave -> {
                Log.d(TAG, "onClick: Nhấn nút Lưu")
                val startDate = binding.edtStartDate.text.toString().trim()
                val timeStart = binding.tgStart.text.toString().trim()
                val timeEnd = binding.tgEnd.text.toString().trim()
                val classCode = binding.edtClassCode.text.toString().trim()
                val subjectName = binding.edtSubjectName.text.toString().trim()
                val subjectHP = binding.edtSubjectHP.text.toString().trim()
                val timeInDay = binding.edtTime.text.toString().trim()
                val location = binding.edtLocation.text.toString().trim()
                val weeks = binding.edtWeek.text.toString().trim()

                val checkData = CheckDateTime()

                if (startDate.isEmpty()) {
                    binding.edtStartDate.error = "Vui lòng nhập ngày bắt đầu"
                    binding.edtStartDate.requestFocus()
                    return
                }
                if (timeStart.isEmpty()) {
                    binding.tgStart.error = "Vui lòng nhập giờ bắt đầu"
                    binding.tgStart.requestFocus()
                    return
                }
                if (timeEnd.isEmpty()) {
                    binding.tgEnd.error = "Vui lòng nhập giờ kết thúc"
                    binding.tgEnd.requestFocus()
                    return
                }
                if (classCode.isEmpty()) {
                    binding.edtClassCode.error = "Vui lòng nhập mã lớp"
                    binding.edtClassCode.requestFocus()
                    return
                }
                if (subjectName.isEmpty()) {
                    binding.edtSubjectName.error = "Vui lòng nhập tên môn"
                    binding.edtSubjectName.requestFocus()
                    return
                }
                if (subjectHP.isEmpty()) {
                    binding.edtSubjectHP.error = "Vui lòng nhập mã học phần"
                    binding.edtSubjectHP.requestFocus()
                    return
                }
                if (timeInDay.isEmpty()) {
                    binding.edtTime.error = "Vui lòng nhập thời gian"
                    binding.edtTime.requestFocus()
                    return
                }
                if (location.isEmpty()) {
                    binding.edtLocation.error = "Vui lòng nhập địa điểm"
                    binding.edtLocation.requestFocus()
                    return
                }
                if (weeks.isEmpty()) {
                    binding.edtWeek.error = "Vui lòng nhập tuần học"
                    binding.edtWeek.requestFocus()
                    return
                }

                if (!weeks.matches(Regex("^(\\d+\\s*-\\s*\\d+)(\\s*,\\s*\\d+\\s*-\\s*\\d+)*\$|^\\d+\$|^(\\d+)(\\s*,\\s*\\d+)+\$"))) {
                    binding.edtWeek.error = "Định dạng tuần không hợp lệ (ví dụ: 24-31, 33-42 hoặc 24, 25)"
                    binding.edtWeek.requestFocus()
                    return
                }

                if (!checkData.checktimeStartEnd(timeStart, timeEnd)) {
                    binding.tgEnd.error = "Vui lòng nhập đúng trình tự thời gian"
                    binding.tgEnd.requestFocus()
                    return
                }

                saveSchedule(
                    startDate,
                    timeStart,
                    timeEnd,
                    classCode,
                    subjectName,
                    subjectHP,
                    timeInDay,
                    location,
                    weeks
                )
            }

            R.id.btnFresher -> {
                binding.btnSave.error = null
                binding.edtStartDate.setText("")
                binding.tgStart.setText("")
                binding.tgEnd.setText("")
                binding.edtClassCode.setText("")
                binding.edtSubjectName.setText("")
                binding.edtSubjectHP.setText("")
                binding.edtTime.setText("")
                binding.edtLocation.setText("")
                binding.edtWeek.setText("")
                binding.edtStartDate.error = null
                binding.tgStart.error = null
                binding.tgEnd.error = null
                binding.edtClassCode.error = null
                binding.edtSubjectName.error = null
                binding.edtSubjectHP.error = null
                binding.edtTime.error = null
                binding.edtLocation.error = null
                binding.edtWeek.error = null
            }
        }
    }

    private fun saveSchedule(
        startDate: String, timeStart: String, timeEnd: String,
        classCode: String, subjectName: String, subjectHP: String,
        timeInDay: String, location: String, weeks: String
    ) {

        val startLocalDate: LocalDate
        try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            startLocalDate = LocalDate.parse(startDate, formatter)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Ngày không hợp lệ: $startDate", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val weeksList = parseWeeks(weeks)
            Log.d(TAG, "saveSchedule: Danh sách tuần: $weeksList")
            val baseWeek = weeksList.minOrNull() ?: 0
            val userId = auth.currentUser?.uid
            if (userId == null) {
                Log.e(TAG, "saveSchedule: Người dùng chưa đăng nhập")
                Toast.makeText(requireContext(), "Người dùng chưa đăng nhập!", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val schedules = mutableListOf<HashMap<String, Any>>()
            for (week in weeksList) {
                val weeksOffset = week - baseWeek
                val dateForWeek = startLocalDate.plusWeeks(weeksOffset.toLong())
                val schedule = hashMapOf(
                    "date" to dateForWeek.toString(),
                    "timeStart" to timeStart,
                    "timeEnd" to timeEnd,
                    "classCode" to classCode,
                    "subjectName" to subjectName,
                    "subjectHP" to subjectHP,
                    "timeInDay" to timeInDay,
                    "location" to location,
                    "week" to week.toString()
                )
                schedules.add(schedule as HashMap<String, Any>)
            }

            val dbRef = db.collection("Users").document(userId).collection("schedules")

            // Kiểm tra trùng lặp trước khi thêm mới
            val calendar = CheckCalendar()
            val result = calendar.checkDuplicateSchedule(
                startDate,
                timeStart,
                timeEnd,
                subjectName,
                weeksList
            )
            if (result != "Không có trùng lặp") {
                Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
                binding.btnSave.error = result
                return@launch
            }

            // Thêm mới lịch
            var i = 0
            var keyTime = schedules[0]["date"].toString()
            for (schedule in schedules) {
                val timeKey = timeKey(schedule["timeStart"].toString())
                val keyID = classCode + timeKey + keyTime + i.toString()
                i++
                Log.d(TAG, "saveSchedule: Lưu document với keyID: $keyID, dữ liệu: $schedule")
                dbRef.document(keyID)
                    .set(schedule)
                    .addOnSuccessListener {
                        if (isAdded) {
                            Toast.makeText(
                                requireContext(),
                                "Thêm thành công!",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.btnSave.error = null
                        }
                    }
                    .addOnFailureListener { e ->
//                        Log.e(TAG, "saveSchedule: Lỗi khi lưu document $keyID: ${e.message}")
                        if (isAdded) {
                            Toast.makeText(
                                requireContext(),
                                "Lỗi khi thêm: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }

    //dùng thời gian bắt đầu làm key bỏ phần : trong thời gian
    private fun CoroutineScope.timeKey(string: String): String {
        var keyTime = ""
        for (i in 0 until string.length) {
            if (i != 2) {
                keyTime += string[i]
            }
        }
        return keyTime
    }

    // Hàm phân tích chuỗi weeks thành danh sách các tuần
    private fun parseWeeks(weeks: String): List<Int> {
        val weeksList = mutableListOf<Int>()
        val ranges = weeks.split(",").map { it.trim() }

        for (range in ranges) {
            if (range.contains("-")) {
                val (start, end) = range.split("-").map { it.trim().toInt() }
                Log.d(TAG, "parseWeeks: Xử lý khoảng tuần: $start-$end")
                for (week in start..end) {
                    weeksList.add(week)
                }
            } else {
                weeksList.add(range.toInt())
            }
        }

        weeksList.sort()
        val uniqueList = mutableListOf<Int>()
        for (week in weeksList) {
            if (week !in uniqueList) {
                uniqueList.add(week)
            }
        }
        return uniqueList
    }
}