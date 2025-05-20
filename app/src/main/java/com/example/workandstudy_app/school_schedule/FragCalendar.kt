package com.example.workandstudy_app.school_schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.workandstudy_app.R
import com.example.workandstudy_app.data_school_schedule.CustomAdaterDataSubject
import com.example.workandstudy_app.data_school_schedule.DataSubject
import com.example.workandstudy_app.data_school_schedule.DataTKB
import com.example.workandstudy_app.data_school_schedule.DayoftheWeeks
import com.example.workandstudy_app.data_school_schedule.DesignListSubject
import com.example.workandstudy_app.databinding.FragmentCalendarBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class FragCalendar : Fragment(), CalendarAdapter.OnItemListener,
    CustomAdaterDataSubject.OnItemClickListener {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var monthYearText: TextView
    private lateinit var calendarRecyclerView: RecyclerView
    private var selectedDate: LocalDate = LocalDate.now() // Current selected month
    private val db = FirebaseFirestore.getInstance() // Firestore instance
    private val auth = FirebaseAuth.getInstance() // Firebase Auth instance
    private var dayPresent = ""
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private lateinit var calendarAdapter: CalendarAdapter
    private val sharedViewModel: SharedViewModel by lazy {
        ViewModelProvider(requireActivity())[SharedViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        binding.datePresent.text = formatVietnameseDate(selectedDate)
        binding.dsSubject.addItemDecoration(DesignListSubject(22))
        dayPresent = binding.datePresent.text.toString()
        initWidgets()
        setMonthView()
        // Set up navigation buttons
        binding.prevButton.setOnClickListener {
            selectedDate = selectedDate.minusMonths(1)
            setMonthView()
        }
        binding.nextButton.setOnClickListener {
            selectedDate = selectedDate.plusMonths(1)
            setMonthView()
        }
        return binding.root
    }

    // Initialize UI widgets
    private fun initWidgets() {
        calendarRecyclerView = binding.calendarRecyclerView
        monthYearText = binding.monthYearTV
    }

    // Set up the calendar view for the selected month
    fun setMonthView() {
        monthYearText.text = monthYearFromDate(selectedDate)
        val daysInMonth = daysInMonthArray(selectedDate)

        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Fetch schedules for the current month from Firestore
            db.collection("Users").document(userId).collection("schedules")
                .whereGreaterThanOrEqualTo(
                    "date",
                    "${selectedDate.year}-${selectedDate.monthValue.toString().padStart(2, '0')}-01"
                )
                .whereLessThanOrEqualTo(
                    "date",
                    "${selectedDate.year}-${
                        selectedDate.monthValue.toString().padStart(2, '0')
                    }-${
                        daysInMonthArray(selectedDate).last { it.dayText.isNotEmpty() }.dayText.padStart(
                            2,
                            '0'
                        )
                    }"
                )
                .get()
                .addOnSuccessListener { documents ->
                    // Extract dates with schedules
                    val scheduledDates = documents.mapNotNull { it.getString("date") }

                    // Mark days with schedules
                    daysInMonth.forEach { day ->
                        if (day.dayText.toIntOrNull() != null) { // Only process actual days
                            val dayDate = "${selectedDate.year}-${
                                selectedDate.monthValue.toString().padStart(2, '0')
                            }-${day.dayText.padStart(2, '0')}"
                            if (scheduledDates.contains(dayDate)) {
                                day.hasSchedule = true
                            }
                        }
                    }

                    // Update RecyclerView with calendar data
                    updateCalendarAdapter(daysInMonth)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error loading schedule data!", Toast.LENGTH_SHORT)
                        .show()
                    updateCalendarAdapter(daysInMonth) // Show default calendar on failure
                }
        } else {
            // No user logged in, show default calendar
            updateCalendarAdapter(daysInMonth)
        }
    }

    // Helper method to update RecyclerView with calendar data
    private fun updateCalendarAdapter(daysInMonth: ArrayList<DayoftheWeeks>) {
        calendarAdapter = CalendarAdapter(daysInMonth, this)
        calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        calendarRecyclerView.adapter = calendarAdapter
    }

    // Generate array of days for the selected month
    private fun daysInMonthArray(date: LocalDate): ArrayList<DayoftheWeeks> {
        val daysInMonthArray = ArrayList<DayoftheWeeks>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val dayNames = arrayListOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        // Add day-of-week headers
        dayNames.forEach { daysInMonthArray.add(DayoftheWeeks(it)) }

        // Calculate the first day of the month
        val firstOfMonth = date.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value // vị trí tương ứng với thứ

        // Fill the calendar grid (6 weeks = 42 slots)
        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add(DayoftheWeeks("")) // Empty slots
            } else {
                daysInMonthArray.add(DayoftheWeeks((i - dayOfWeek).toString())) // Actual days
            }
        }

        // Remove extra row if the first week is empty
        if (daysInMonthArray[13].dayText.isEmpty() && daysInMonthArray.size > 13) {
            for (i in 7..13) {
                daysInMonthArray.removeAt(7)
            }
        }
        return daysInMonthArray
    }

    // Format month and year for display
    private fun monthYearFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    // Handle click on a calendar day
    override fun onItemClick(position: Int, dayText: String) {
        if (dayText.isNotEmpty() && position > 6) {
            val selectedDay = dayText.toInt()
            val selectedLocalDate = selectedDate.withDayOfMonth(selectedDay)
            val message = "Selected Date $dayText ${monthYearFromDate(selectedDate)}"
            binding.datePresent.text = "Ngày $dayText tháng ${selectedDate.monthValue}"
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()

            val userId = auth.currentUser?.uid
            if (userId != null) {
                // Fetch detailed schedule for the selected day
                db.collection("Users").document(userId).collection("schedules")
                    .whereEqualTo("date", selectedLocalDate.toString())
                    .get()
                    .addOnSuccessListener { documents ->
                        val list = mutableListOf<DataTKB>()
                        for (document in documents) {
                            val idDocument = document.id
                            var startDate = ""
                            if (selectedDate.monthValue.toInt() < 10 && dayText.toInt() < 10) {
                                startDate =
                                    "0$dayText/0${selectedDate.monthValue}/${selectedDate.year}"
                            } else {
                                if (selectedDate.monthValue.toInt() < 10) {
                                    startDate =
                                        "$dayText/0${selectedDate.monthValue}/${selectedDate.year}"
                                }
                                if (dayText.toInt() < 10) {
                                    startDate =
                                        "0$dayText/${selectedDate.monthValue}/${selectedDate.year}"
                                }
                            }
                            val timeStart = document.getString("timeStart") ?: ""
                            val timeEnd = document.getString("timeEnd") ?: ""
                            val classCode = document.getString("classCode") ?: ""
                            val subjectName = document.getString("subjectName") ?: ""
                            val subjectHP = document.getString("subjectHP") ?: ""
                            val timeInDay = document.getString("timeInDay") ?: ""
                            val location = document.getString("location") ?: ""
                            val week = document.getString("week") ?: ""
                            list.add(
                                DataTKB(
                                    idDocument,
                                    startDate,
                                    timeStart,
                                    timeEnd,
                                    classCode,
                                    subjectName,
                                    subjectHP,
                                    timeInDay,
                                    location,
                                    week
                                )
                            )

                        }
                        val sortedList = list.sortedBy {
                            try {
                                LocalTime.parse(it.timeStart, timeFormatter)
                            } catch (e: Exception) {
                                LocalTime.MIN // Nếu có lỗi, đưa về 00:00 để không ảnh hưởng thứ tự
                            }
                        }
                        val adapterList = sortedList.map {
                            var maMonHoc = ""
                            var maLop = ""
                            if (it.classCode == "" && it.subjectHP == "") {
                                maMonHoc = it.subjectName
                            } else {
                                if (it.classCode != "" && it.subjectHP != "") {
                                    maMonHoc = "${it.classCode} - ${it.subjectName} - ${it.subjectHP}"
                                } else {
                                    if (it.classCode != "") {
                                        maMonHoc = "${it.classCode} - ${it.subjectName}"
                                    }
                                    if (it.subjectHP != "") {
                                        maMonHoc = "${it.subjectName} - ${it.subjectHP} "
                                    }
                                }
                            }
                            if (it.timeInDay == "") {
                                maLop = it.location
                            } else {
                                maLop = "${it.timeInDay}, ${it.location}"
                            }
                            DataSubject(
                                id = it.id,
                                dateStart = it.dateStart,
                                time_Start = it.timeStart,
                                time_End = it.timeEnd,
                                maMonHoc = maMonHoc,
                                maLop = maLop,
                                tuanHoc = "Week ${it.week}"
                            )
                        }
                        val adapter = CustomAdaterDataSubject(requireActivity(), adapterList, this)
                        binding.dsSubject.layoutManager = LinearLayoutManager(requireContext())
                        binding.dsSubject.adapter = adapter
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Error loading schedule details!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    fun formatVietnameseDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("'Ngày' d 'tháng' M")
        return date.format(formatter)
    }

    override fun onItemClick(position: Int, item: DataSubject, list: List<DataSubject>) {
        val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewCalendar)
        if (viewPager != null) {
            viewPager.setCurrentItem(2, true)
            sharedViewModel.setSelectedSubject(item)
        } else {
            Toast.makeText(requireContext(), "ViewPager not found", Toast.LENGTH_SHORT).show()
        }
    }

}