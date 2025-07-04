package com.example.workandstudy_app.school_schedule

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPaperAdapterSchudeles(fragmentManager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fragmentManager,lifecycle){
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{
                FragCalendar()
            }
            1-> {
                FragCreateTKB()
            }
            2 -> FragUpdateDelete()
            else -> FragCalendar()
        }
    }
}