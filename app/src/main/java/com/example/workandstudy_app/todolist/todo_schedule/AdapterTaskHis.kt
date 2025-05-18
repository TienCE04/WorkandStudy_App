package com.example.workandstudy_app.todolist.todo_schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.workandstudy_app.R
import com.example.workandstudy_app.todolist.Entity.TasksData
import java.time.LocalDate
import androidx.recyclerview.widget.ListAdapter

sealed class HistoryItem {
    data class Header(val date: LocalDate, val displayDate: String) : HistoryItem()
    data class Task(val task: TasksData) : HistoryItem()
    data class TaskNoComplete (val task: TasksData) : HistoryItem()
}

class AdapterTaskHis : ListAdapter<HistoryItem, RecyclerView.ViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_task_history, parent, false)
            HeaderViewHolder(view)

        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_task_history2, parent, false)
            TaskViewHolder(view)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (val item = getItem(position)) {
            is HistoryItem.Header -> {
                val headerHolder = holder as HeaderViewHolder
                headerHolder.headerDate.text = item.displayDate
            }

            is HistoryItem.Task -> {
                val taskHolder = holder as TaskViewHolder
                val task = item.task
                taskHolder.tieuDe.text = task.titleTask
                taskHolder.noiDung.text = task.contentTask
                taskHolder.timeStart.text = task.timeTask
            }
            is HistoryItem.TaskNoComplete ->{
                val taskHolder = holder as TaskViewHolder
                val task = item.task
                taskHolder.tieuDe.text = task.titleTask
                taskHolder.noiDung.text = task.contentTask
                taskHolder.timeStart.text = task.timeTask
                taskHolder.tickComplete.setImageResource(R.drawable.baseline_clear_24)
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 1
        private const val VIEW_TYPE_TASK = 2
        private const val VIEW_TYPE_TASK_NO_COMPLETE=3
    }

    //view cho header
    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerDate: TextView = itemView.findViewById(R.id.ngOld)
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tieuDe: TextView = itemView.findViewById(R.id.tieuDe)
        val noiDung: TextView = itemView.findViewById(R.id.content)
        val tickComplete: ImageView = itemView.findViewById(R.id.tickComplete)
        val timeStart: TextView = itemView.findViewById(R.id.timeStart)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HistoryItem.Header -> VIEW_TYPE_HEADER
            is HistoryItem.Task -> VIEW_TYPE_TASK
            is HistoryItem.TaskNoComplete -> VIEW_TYPE_TASK_NO_COMPLETE
        }
    }

    class HistoryDiffCallback : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(
            oldItem: HistoryItem,
            newItem: HistoryItem
        ): Boolean {
            return when {
                oldItem is HistoryItem.Header && newItem is HistoryItem.Header ->
                    oldItem.date==newItem.date
                oldItem is HistoryItem.Task && newItem is HistoryItem.Task ->
                    oldItem.task==newItem.task
                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: HistoryItem,
            newItem: HistoryItem
        ): Boolean {
            return oldItem==newItem
        }
    }

}