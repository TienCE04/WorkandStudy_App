package com.example.workandstudy_app.todolist.todo_schedule

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.workandstudy_app.R
import com.example.workandstudy_app.todolist.Entity.TasksData


class TaskAdapter(private val listener: TaskItemListener) :
    ListAdapter<TasksData, TaskAdapter.TaskViewHolder>(DiffCallback()) {


    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTask: TextView = itemView.findViewById(R.id.tenTask)
        val timeTask: TextView = itemView.findViewById(R.id.timeTask)
        val tickComplete: CheckBox = itemView.findViewById(R.id.markWork)
        val flagImportant: ImageView = itemView.findViewById(R.id.importantStar)
        val detailButton: TextView = itemView.findViewById(R.id.detailWork)
        val delTaskBut: TextView = itemView.findViewById(R.id.xoaTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_work, parent, false)
        return TaskViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: TaskViewHolder,
        position: Int
    ) {
        val task = getItem(position)
        holder.titleTask.text = task.titleTask
        holder.timeTask.text = "[" + task.timeTask + "]"

        //đánh dấu đã hoàn thành
        holder.tickComplete.setOnCheckedChangeListener(null) //
        holder.tickComplete.isChecked = task.tick
        holder.tickComplete.setOnCheckedChangeListener { _, isChecked ->
            task.tick = isChecked
            listener.onCheckChanged(task)
        }

        //Đánh dấu cờ
        // Đánh dấu cờ quan trọng
        val tintColor = if (task.flag) {
            holder.itemView.context.getColor(R.color.star_color)
        } else {
            holder.itemView.context.getColor(R.color.black)
        }
        holder.flagImportant.setColorFilter(tintColor)
        holder.flagImportant.setOnClickListener {
            listener.onFlagChanged(task.copy(flag = !task.flag))
        }

        // Các button khác
        holder.detailButton.setOnClickListener {
            listener.onDetailClicked(task)
        }

        holder.delTaskBut.setOnClickListener {
            listener.onDeleteClicked(task)
        }

    }


    class DiffCallback : DiffUtil.ItemCallback<TasksData>() {
        override fun areItemsTheSame(oldItem: TasksData, newItem: TasksData): Boolean {
            return oldItem.taskIdDate == newItem.taskIdDate
        }

        override fun areContentsTheSame(oldItem: TasksData, newItem: TasksData): Boolean {
            return oldItem.titleTask == newItem.titleTask &&
                    oldItem.timeTask == newItem.timeTask &&
                    oldItem.tick == newItem.tick &&
                    oldItem.flag == newItem.flag
        }
    }

    interface TaskItemListener {
        fun onCheckChanged(task: TasksData)
        fun onDetailClicked(task: TasksData)
        fun onDeleteClicked(task: TasksData)
        fun onFlagChanged(task: TasksData)
    }
}