package com.example.workandstudy_app.school_schedule

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.workandstudy_app.R

class CalendarViewHolder(itemView:View, private val onItemListener:CalendarAdapter.OnItemListener):
    RecyclerView.ViewHolder(itemView),View.OnClickListener{

    val dayOfMonth=itemView.findViewById<TextView>(R.id.cellDayText)
    private val doIndicator:View=itemView.findViewById<View>(R.id.dot_indicator)

    init{
        itemView.setOnClickListener(this)
    }
    override fun onClick(v: View?) {
        onItemListener.onItemClick(adapterPosition,dayOfMonth.text.toString())
    }
    fun setDotVisibility(isVisible: Boolean){
        doIndicator.visibility=if(isVisible) View.VISIBLE else View.GONE
    }
}