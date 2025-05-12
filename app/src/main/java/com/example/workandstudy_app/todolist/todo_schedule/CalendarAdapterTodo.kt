package com.example.workandstudy_app.todolist.todo_schedule


import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.workandstudy_app.R
import com.example.workandstudy_app.data_school_schedule.DayoftheWeeks
import com.example.workandstudy_app.school_schedule.CalendarAdapter
import androidx.core.graphics.toColorInt
import java.time.LocalDate


class CalendarAdapterTodo(
    private val daysOfWeek: ArrayList<DayoftheWeeks>,
    private val onItemListener: CalendarAdapterTodo.OnItemListener,
    private val timeNow: LocalDate
) : RecyclerView.Adapter<CalendarViewHolderTodo>() {
    private val daysOfWeekCount = 7  // Tổng số ngày trong tuần (Chủ nhật đến Thứ Bảy)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolderTodo {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.calender_work_cell, parent, false)

        // Đặt chiều cao cho các ô
        val layoutParams = view.layoutParams
        if (viewType == 0) {
            layoutParams.height = 70
        }
        if (viewType == 1) {
            layoutParams.height = 110
        }

        return CalendarViewHolderTodo(view, onItemListener)
    }

    override fun onBindViewHolder(holder: CalendarViewHolderTodo, position: Int) {
        val day = daysOfWeek[position]
        holder.dayOfMonth.text = day.dayText
        if (timeNow.dayOfMonth == LocalDate.now().dayOfMonth) {
            if (LocalDate.now().dayOfWeek.value == 7 && position == 7) {
                holder.dayOfMonth.setTextColor("#FF5722".toColorInt())
            } else {
                if (LocalDate.now().dayOfWeek.value + 7 == position)
                    holder.dayOfMonth.setTextColor("#FF5722".toColorInt())
            }
        }
        holder.setDotVisibility(day.hasSchedule)
    }

    override fun getItemViewType(position: Int): Int {
        if (position < 7) {
            return 0
        }
        return 1
    }

    override fun getItemCount(): Int = daysOfWeek.size
    interface OnItemListener {
        fun onItemClick(position: Int, dayText: String)
    }
}