package com.example.workandstudy_app.school_schedule

import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.workandstudy_app.data_school_schedule.DataSubject
import com.example.workandstudy_app.databinding.FragmentUpdateDeleteBinding
import com.example.workandstudy_app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass responsible for displaying a monthly calendar.
 * It shows a grid of days with dots indicating scheduled events and allows viewing details on click.
 * Use the [FragUpdateDelete.newInstance] factory method to create an instance of this fragment.
 */
class FragUpdateDelete : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentUpdateDeleteBinding
    private lateinit var db: FirebaseFirestore
    private val auth = FirebaseAuth.getInstance()
    private lateinit var classCode: String
    private lateinit var location: String
    private lateinit var subjectHP: String
    private lateinit var subjectName: String
    private lateinit var timeEnd: String
    private lateinit var timeInDay: String
    private lateinit var timeStart: String
    private var scheduleId: String? = null
    private var calendar= Calendar.getInstance()
    private var flag = false
    val userID = auth.currentUser?.uid
    private val checkTime= CheckDateTime()
    private val sharedViewModel: SharedViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateDeleteBinding.inflate(inflater, container, false)
        init()
        observeSelectedSubject()
        return binding.root
    }

    private fun init() {
        db = FirebaseFirestore.getInstance()
        binding.UpdateOne.setOnClickListener(this)
        binding.UpdateAll.setOnClickListener(this)
        binding.DeleteAll.setOnClickListener(this)
        binding.DeleteOne.setOnClickListener(this)
        binding.timeBatDau.setOnClickListener(this)
        binding.timeKetThuc.setOnClickListener(this)
    }

    private fun observeSelectedSubject() {
        sharedViewModel.selectedSubject.observe(viewLifecycleOwner) { subject ->
            if (subject != null) {
                fillSchedulesFromSubject(subject)
                sharedViewModel.clearSelectedSubject()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.timeBatDau -> {
                TimePickerDialog(
                    requireContext(),
                    TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        binding.timeBatDau.setText(checkTime.checkTime(hour,minute))
                    },
                    calendar.get(java.util.Calendar.HOUR),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            }
            R.id.timeKetThuc -> {
                TimePickerDialog(
                    requireContext(),
                    TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        binding.timeKetThuc.setText(checkTime.checkTime(hour,minute))
                    },
                    calendar.get(java.util.Calendar.HOUR),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            }

            R.id.UpdateAll -> {
                lifecycleScope.launch {
                    updateAllSchedules()
                }
            }

            R.id.UpdateOne -> {
                lifecycleScope.launch {
                    updateSchedule()
                }
            }

            R.id.DeleteAll -> {
                lifecycleScope.launch {
                    deleteScheduleAll()
                }
            }

            R.id.DeleteOne -> {
                lifecycleScope.launch {
                    deleteSchedule()
                }
            }
        }

    }

    //Cập nhật tất cả
    private suspend fun updateAllSchedules() {
        if (userID == null || scheduleId == null) {
            Toast.makeText(
                requireContext(),
                "UserID hoặc SchedulesID không tồn tại",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val chuoiKey = scheduleId.toString()
        var keyID = ""
        var index = 0
        for (i in chuoiKey.length - 1 downTo 0) {
            if (chuoiKey[i] == '-') {
                index = i
                break
            }
        }
        for (i in 0..index) {
            keyID += chuoiKey[i]
        }
        getData()
        try {
            val updates = mapOf(
                "classCode" to classCode, "location" to location,
                "subjectName" to subjectName, "subjectHP" to subjectHP,
                "timeStart" to timeStart, "timeEnd" to timeEnd,
                "timeInDay" to timeInDay
            )
            val dbRef = db.collection("Users").document(userID).collection("schedules")
            val querySnapshot = dbRef
                .whereGreaterThanOrEqualTo(FieldPath.documentId(), keyID)
                .whereLessThanOrEqualTo(FieldPath.documentId(), keyID + "\uf8ff")
                .get()
                .await()
            for (document in querySnapshot.documents) {
                document.reference.update(updates).await()
            }
            if (isAdded) {
                Toast.makeText(requireContext(), "Cập nhật thành công!!", Toast.LENGTH_SHORT).show()
                flag = true
                notification()
            }
        } catch (e: Exception) {
            flag = false
            notification()
        }
    }

    //xóa tất cả theo mã lớp
    private suspend fun deleteScheduleAll() {
        if (userID == null || scheduleId == null) {
            Toast.makeText(
                requireContext(),
                "UserID hoặc ScheduleID không hợp lệ",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val chuoiKey = scheduleId.toString()
        var keyID = ""
        var index = 0
        for (i in chuoiKey.length - 1 downTo 0) {
            if (chuoiKey[i] == '-') {
                index = i
                break
            }
        }
        for (i in 0..index) {
            keyID += chuoiKey[i]
        }
        if (binding.timeBatDau.text.toString().isEmpty()) {
            binding.timeBatDau.error = "Vui lòng nhập thời gian bắt đầu"
            binding.timeBatDau.requestFocus()
        }
        if (binding.maclass.text.toString().isEmpty()) {
            binding.maclass.error = "Vui lòng nhập mã lớp"
            binding.maclass.requestFocus()
        }
        try {
            val dbRef = db.collection("Users").document(userID).collection("schedules")
            val querySnapshot = dbRef
                .whereGreaterThanOrEqualTo(FieldPath.documentId(), keyID)
                .whereLessThanOrEqualTo(FieldPath.documentId(), keyID + "\uf8ff")
                .get()
                .await()
            for (document in querySnapshot.documents) {
                document.reference.delete().await()
            }
            if (isAdded) {
                Toast.makeText(requireContext(), "Xóa thành công!!", Toast.LENGTH_SHORT).show()
                refresh()
            }
        } catch (e: Exception) {
            if (isAdded) {
                Toast.makeText(requireContext(), "Lỗi!!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun getData() {
        classCode = binding.maclass.text.toString()
        location = binding.diaDiem.text.toString()
        subjectName = binding.tenMonHoc.text.toString()
        subjectHP = binding.maHocPhan.text.toString()
        timeStart = binding.timeBatDau.text.toString()
        timeEnd = binding.timeKetThuc.text.toString()
        timeInDay = binding.timeDay.text.toString()
        checkData(classCode, location, subjectName, subjectHP, timeInDay, timeStart, timeEnd)

    }

    private fun checkData(
        classCode: String, location: String, subjectName: String, subjectHP: String,
        timeInDay: String, timeStart: String, timeEnd: String
    ) {
        if (classCode.isEmpty()) {
            binding.maclass.error = "Vui lòng nhập mã lớp"
            binding.maclass.requestFocus()
            return
        }
        if (location.isEmpty()) {
            binding.diaDiem.error = "Vui lòng nhập địa điểm"
            binding.diaDiem.requestFocus()
            return
        }
        if (subjectName.isEmpty()) {
            binding.tenMonHoc.error = "Vui lòng nhập tên môn học"
            binding.tenMonHoc.requestFocus()
            return
        }
        if (subjectHP.isEmpty()) {
            binding.maHocPhan.error = "Vui lòng nhập mã học phần"
            binding.maHocPhan.requestFocus()
            return
        }
        if (timeInDay.isEmpty()) {
            binding.timeDay.error = "Vui lòng nhập thời gian trong ngày"
            binding.timeDay.requestFocus()
            return
        }
        if (timeStart.isEmpty()) {
            binding.timeBatDau.error = "Vui lòng nhập thời gian bắt đầu"
            binding.timeBatDau.requestFocus()
            return
        }
        if (timeEnd.isEmpty()) {
            binding.timeKetThuc.error = "Vui lòng nhập thời gian kết thúc"
            binding.timeKetThuc.requestFocus()
            return
        }

    }

    private suspend fun updateSchedule() {
        if (userID == null || scheduleId == null) {
            Toast.makeText(
                requireContext(),
                "UserID hoặc SchedulesID không tồn tại",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        getData()
        try {
            val updates = mapOf(
                "classCode" to classCode, "location" to location,
                "subjectName" to subjectName, "subjectHP" to subjectHP,
                "timeStart" to timeStart, "timeEnd" to timeEnd,
                "timeInDay" to timeInDay
            )
            db.collection("Users").document(userID).collection("schedules")
                .document(scheduleId!!)
                .update(updates)
                .await()
            flag = true
            notification()
        } catch (e: Exception) {
            flag = false
            notification()
        }
    }

    private fun notification() {
        when (flag) {
            true -> {
                Toast.makeText(requireContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
            }

            else -> {
                Toast.makeText(requireContext(), "Lỗi!!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private suspend fun deleteSchedule() {
        if (userID == null || scheduleId == null) {
            Toast.makeText(
                requireContext(),
                "UserID hoặc ScheduleID không hợp lệ",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        try {
            Log.d("DeleteSchedule", "Deleting UserID: $userID, ScheduleID: $scheduleId")
            db.collection("Users").document(userID).collection("schedules")
                .document(scheduleId!!)
                .delete()
                .await()
            if (isAdded) {
                Toast.makeText(requireContext(), "Xóa thành công!", Toast.LENGTH_SHORT).show()
                refresh()
            }
        } catch (e: Exception) {
            if (isAdded) {
                Toast.makeText(requireContext(), "Lỗi khi xóa: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                Log.e("DeleteSchedule", "Lỗi: ${e.message}")
            }
        }
    }

    //gán dữ liệu
    fun fillSchedulesFromSubject(subject: DataSubject) {
        scheduleId = subject.id
        binding.edtStartDay.setText(subject.dateStart) // Sử dụng date từ dataSubject
        binding.timeBatDau.setText(subject.time_Start)
        binding.timeKetThuc.setText(subject.time_End)
        // Phân tách maMonHoc
        val maMonHocParts = subject.maMonHoc.split(" - ")
        binding.maclass.setText(if (maMonHocParts.size > 0) maMonHocParts[0] else "")
        binding.tenMonHoc.setText(if (maMonHocParts.size > 1) maMonHocParts[1] else "")
        binding.maHocPhan.setText(if (maMonHocParts.size > 2) maMonHocParts[2] else "")
        // Phân tách maLop
        val maLopParts = subject.maLop.split(", ")
        binding.timeDay.setText(if (maLopParts.size > 0) maLopParts[0] else "")
        binding.diaDiem.setText(if (maLopParts.size > 1) maLopParts[1] else "")
        binding.tuanHoc.setText(subject.tuanHoc.removePrefix("Week "))
    }

    //làm mới dữ liệu
    private fun refresh() {
        binding.edtStartDay.text = ""
        binding.timeDay.setText("")
        binding.timeBatDau.setText("")
        binding.timeKetThuc.setText("")
        binding.diaDiem.setText("")
        binding.tuanHoc.setText("")
        binding.maclass.setText("")
        binding.tenMonHoc.setText("")
        binding.maHocPhan.setText("")
        scheduleId = null
    }
}