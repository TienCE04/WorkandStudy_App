package com.example.workandstudy_app.todolist.todo_schedule

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.workandstudy_app.R
import com.example.workandstudy_app.databinding.ScreenTodoBinding
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2


class ScreenTodoList : AppCompatActivity(), View.OnClickListener, FragTasks.OnDaySelectedListener {

    private lateinit var binding: ScreenTodoBinding
    private lateinit var adapter: ViewPagerAdapterTodo
    private var getID: String = ""
    private lateinit var viewModel: SharedViewModelTodo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ScreenTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.fab.setOnClickListener(this)
        adapter = ViewPagerAdapterTodo(this)
        binding.viewTodo.adapter = adapter
        TabLayoutMediator(binding.tabDailyTasks, binding.viewTodo) { tab, position ->
            tab.text = when (position) {
                0 -> "Tasks"
                1 -> "Habits"
                2 -> "History"
                else -> "Tasks"
            }
        }.attach()

//        binding.viewTodo.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                when (position) {
//                    0 -> binding.fab.visibility = View.VISIBLE // Hiển thị Button ở FragTasks
//                    1, 2 -> binding.fab.visibility = View.GONE // Ẩn Button ở FragHabits và FragHistory
//                }
//            }
//        })
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab -> {
                if (getID.isEmpty()) {
                    Toast.makeText(this, "Vui lòng chọn ngày cần thêm!", Toast.LENGTH_SHORT).show()
                } else {
                    val addTaskIntent = Intent(this, AddTaskActivity::class.java)
                    addTaskIntent.putExtra("keyID", getID)
                    startActivity(addTaskIntent)
                }
            }
        }
    }

    override fun onDaySelected(keyID: String) {
        getID = keyID
    }

}



class ViewPagerAdapterTodo(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val fragments = mutableListOf<Fragment>(
        FragTasks(), FragHabits(), FragHistory()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
