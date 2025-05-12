package com.example.workandstudy_app.data_school_schedule

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.workandstudy_app.R

class CustomAdaterDataSubject(
    private val activity: Activity,
    private val list: List<DataSubject>,
    private val listener: OnItemClickListener? = null // Listener cho sự kiện nhấp chuột
) : RecyclerView.Adapter<CustomAdaterDataSubject.ViewHolder>() {

    // ViewHolder cho item lịch học, tham chiếu
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeStart: TextView = itemView.findViewById(R.id.time_Start)
        val timeEnd: TextView = itemView.findViewById(R.id.time_End)
        val maMonHoc: TextView = itemView.findViewById(R.id.maMH)
        val maLop: TextView = itemView.findViewById(R.id.maLop)
        val tuanHoc: TextView = itemView.findViewById(R.id.weekStudy)
    }
    //tạo viewholder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.list_monhoc, parent, false)
        return ViewHolder(view)
    }

    //gán dữ liệu
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.timeStart.text = item.time_Start
        holder.timeEnd.text = item.time_End
        holder.maMonHoc.text = item.maMonHoc
        holder.maLop.text = item.maLop
        holder.tuanHoc.text = item.tuanHoc

        // Gắn sự kiện nhấp chuột vào item
        holder.itemView.setOnClickListener {
            listener?.onItemClick(position, item,list)
        }
    }

    override fun getItemCount(): Int = list.size

    // Giao diện xử lý sự kiện nhấp chuột
    interface OnItemClickListener {
        fun onItemClick(position: Int, item: DataSubject,list:List<DataSubject>)
    }
}