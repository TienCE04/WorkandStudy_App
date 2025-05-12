package com.example.workandstudy_app.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.workandstudy_app.R
import com.example.workandstudy_app.todolist.Entity.TasksData
import com.example.workandstudy_app.todolist.todo_schedule.TaskAdapter.DiffCallback

class AdapterNoti() : ListAdapter<TasksData, AdapterNoti.NotiViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotiViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotiViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: NotiViewHolder,
        position: Int
    ) {
        val task = getItem(position)
        holder.tieuDe.text = task.titleTask
        holder.thoiGian.text = task.timeTask
    }

    class NotiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tieuDe = itemView.findViewById<TextView>(R.id.tieuDe)
        val thoiGian = itemView.findViewById<TextView>(R.id.thoiGian)
    }

}