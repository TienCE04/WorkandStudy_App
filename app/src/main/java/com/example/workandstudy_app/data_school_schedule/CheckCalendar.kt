package com.example.workandstudy_app.data_school_schedule

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CheckCalendar {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun checkDuplicateSchedule(
        startDate: String,  // Định dạng "dd/MM/yyyy", đã hợp lệ
        startTime: String,  // Định dạng "HH:mm", đã hợp lệ
        endTime: String,    // Định dạng "HH:mm", đã hợp lệ và lớn hơn startTime
        tenMon: String,
        listWeek: List<Int> // Danh sách các tuần
    ): String {
        // Chuyển đổi startDate thành LocalDate
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val startLocalDate = LocalDate.parse(startDate, formatter)

        // Chuyển đổi startTime và endTime thành LocalTime
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val startLocalTime = LocalTime.parse(startTime, timeFormatter)
        val endLocalTime = LocalTime.parse(endTime, timeFormatter)

        // Kiểm tra userId
        val userId = auth.currentUser?.uid
        if (userId == null) {
            return "Người dùng chưa đăng nhập"
        }

        // Nếu listWeek rỗng, không cần kiểm tra
        if (listWeek.isEmpty()) {
            return "Không có tuần nào để kiểm tra"
        }

        // Tính ngày bắt đầu và kết thúc từ listWeek
        val baseWeek = listWeek.minOrNull() ?: 0
        val minOffset = listWeek.minOrNull()!! - baseWeek
        val maxOffset = listWeek.maxOrNull()!! - baseWeek
        val earliestDate = startLocalDate.plusWeeks(minOffset.toLong()).toString()
        val latestDate = startLocalDate.plusWeeks(maxOffset.toLong()).toString()

        // Truy vấn Firestore một lần duy nhất
        return try {
            val snapshot = db.collection("Users").document(userId).collection("schedules")
                .whereGreaterThanOrEqualTo("date", earliestDate) // Ngày nhỏ nhất
                .whereLessThanOrEqualTo("date", latestDate)      // Ngày lớn nhất
                .get()
                .await()

            // Tạo danh sách các ngày cần kiểm tra từ listWeek
            val datesToCheck = listWeek.map { week ->
                val weeksOffset = week - baseWeek
                startLocalDate.plusWeeks(weeksOffset.toLong()).toString()
            }.toSet() // Chuyển thành Set để loại bỏ trùng lặp

            // Kiểm tra trùng lặp trong dữ liệu đã lấy
            for (document in snapshot.documents) {
                val existingDate = document.getString("date") ?: continue
                // Chỉ kiểm tra nếu ngày tồn tại trong datesToCheck
                if (existingDate in datesToCheck) {
                    val existingStartTimeStr = document.getString("timeStart")
                    val existingEndTimeStr = document.getString("timeEnd")

                    if (existingStartTimeStr != null && existingEndTimeStr != null) {
                        val existingStartTime = LocalTime.parse(existingStartTimeStr, timeFormatter)
                        val existingEndTime = LocalTime.parse(existingEndTimeStr, timeFormatter)

                        if (startLocalTime < existingEndTime && endLocalTime > existingStartTime) {
                            return "Lịch bị trùng vào ngày $existingDate với: ${document.getString("subjectName") ?: "Unknown"}"
                        }
                    }
                }
            }
            "Không có trùng lặp"
        } catch (e: Exception) {
            "Lỗi khi truy vấn Firestore: ${e.message}"
        }
    }
}