package com.example.workandstudy_app.school_schedule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.workandstudy_app.databinding.ScreenTkbBinding
import com.google.android.material.tabs.TabLayoutMediator


class ScreenTKB : AppCompatActivity() {
    private lateinit var binding: ScreenTkbBinding
    private lateinit var adapter: ViewPagerAdapterSchedules

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ScreenTkbBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ViewPagerAdapterSchedules(this)
        binding.viewCalendar.adapter = adapter
        TabLayoutMediator(binding.tabCalendar, binding.viewCalendar) { tab, position ->
            tab.text = when (position) {
                0 -> "Calendar"
                1 -> "Create"
                2 -> "Update"
                else -> "Calendar"
            }
        }.attach()

    }
}

class ViewPagerAdapterSchedules(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val fragments = mutableListOf<Fragment>(
        FragCalendar(), FragCreateTKB(),
        FragUpdateDelete()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]


}