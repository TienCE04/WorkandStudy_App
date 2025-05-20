package com.example.workandstudy_app.todolist.todo_schedule


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import com.example.workandstudy_app.R
import com.example.workandstudy_app.todolist.Entity.DataHabits



class AdapterHabit(val listener: HabitListener, val items: ArrayList<DataHabits>) : BaseAdapter() {
    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any? {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return items[position].id.toLong()
    }


    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater = LayoutInflater.from(parent?.context)
            view = inflater.inflate(R.layout.item_habbit, parent, false)
            viewHolder = ViewHolder()
            viewHolder.tenHabit = view.findViewById(R.id.tenHabit)
            viewHolder.tgian = view.findViewById(R.id.thoiGian)
            viewHolder.repeat = view.findViewById(R.id.repeatText)
            viewHolder.luu = view.findViewById(R.id.luuTask)
            viewHolder.xoa = view.findViewById(R.id.xoaTask)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val item = items[position]
        viewHolder.tenHabit.setText(item.tenHabit)
        viewHolder.tgian.setText(item.thoiGian)
        viewHolder.repeat.text = item.modeRepeat
        viewHolder.luu.setOnClickListener {
            listener.onSaveClicked(viewHolder,item,position)
        }
        viewHolder.xoa.setOnClickListener {
            listener.onDeleteClicked(item,position)
        }

        return view
    }

    public class ViewHolder() {
        lateinit var tenHabit: EditText
        lateinit var tgian: EditText
        lateinit var repeat: TextView
        lateinit var luu: TextView
        lateinit var xoa: TextView
    }

    interface HabitListener {
        fun onSaveClicked(viewHolder: ViewHolder,habit: DataHabits, position: Int)
        fun onDeleteClicked(habit: DataHabits, position: Int)
    }
}