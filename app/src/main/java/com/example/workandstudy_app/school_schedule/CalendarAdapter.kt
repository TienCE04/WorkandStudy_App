package com.example.workandstudy_app.school_schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.workandstudy_app.R
import com.example.workandstudy_app.data_school_schedule.DayoftheWeeks

class CalendarAdapter(
    private val daysOfMonth: ArrayList<DayoftheWeeks>,
    private val onItemListener: OnItemListener
) : RecyclerView.Adapter<CalendarViewHolder>() {
    private val daysOfWeekCount = 7  // Tổng số ngày trong tuần (từ thứ Hai đến Chủ Nhật)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.calendar_cell, parent, false)

        // Đặt chiều cao cho các ô
        val layoutParams = view.layoutParams
        // Kiểm tra xem có phải dòng đầu tiên (dòng của thứ trong tuần) hay không
        if (viewType == 0) {
            layoutParams.height = (parent.height* 0.11).toInt()  // Dòng thứ 1 (thứ trong tuần) có chiều cao khác
        } else {
            layoutParams.height = (parent.height * 0.15).toInt()  // Các dòng còn lại có chiều cao 10% chiều cao của RecyclerView
        }

        view.layoutParams = layoutParams

        return CalendarViewHolder(view, onItemListener)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val day=daysOfMonth[position]
        holder.dayOfMonth.text = day.dayText
        holder.setDotVisibility(day.hasSchedule)
    }

    override fun getItemCount(): Int = daysOfMonth.size

    // Xác định loại view: 0 là dòng đầu tiên (dòng thứ trong tuần), còn lại là các dòng chứa ngày
    override fun getItemViewType(position: Int): Int {
        return if (position < daysOfWeekCount) {
            0 // Dòng thứ của tuần
        } else {
            1 // Các ngày trong tháng
        }
    }

    interface OnItemListener {
        fun onItemClick(position: Int, dayText: String)
    }
}
