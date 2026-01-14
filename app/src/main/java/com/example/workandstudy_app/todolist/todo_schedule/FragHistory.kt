package com.example.workandstudy_app.todolist.todo_schedule

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workandstudy_app.Database.AppDatabase
import com.example.workandstudy_app.R
import com.example.workandstudy_app.databinding.FragmentHistoryBinding
import com.example.workandstudy_app.main.ScreenMain
import com.example.workandstudy_app.todolist.Entity.TasksData
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class FragHistory : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SharedViewModelTodo
    private lateinit var adapterTaskHis: AdapterTaskHis
    private lateinit var adapterTaskHisNoComplete: AdapterTaskHis
    private val timePresent = LocalDate.now()
    var cnt = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        initAdapter()
        initViewModel()
        //lay data 7 ngay truoc
        getData7Day()
        return binding.root
    }

    private fun getData7Day() {
        val chuoiNgay: MutableList<String> = mutableListOf()
        for (i in 7 downTo 0) {
            val date = timePresent.minusDays(i.toLong())
            val pattern = "${date.dayOfMonth}${date.monthValue}${date.year}"
            chuoiNgay.add(pattern)
        }
        viewModel.getListTasks7day(chuoiNgay)
    }

    private fun initAdapter() {
        adapterTaskHis = AdapterTaskHis()
        adapterTaskHisNoComplete = AdapterTaskHis()
        binding.dahoanthanh.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FragHistory.adapterTaskHis
        }
        binding.quahan.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FragHistory.adapterTaskHisNoComplete
        }
    }

    private fun initViewModel() {
        val repository = TaskRepository(
            AppDatabase.getDatabase(requireContext()).tasksDao()
        )//tao doi tuong truy cap csdl
        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(repository)
        )[SharedViewModelTodo::class.java]

        viewModel.selectedTasksHistory.observe(viewLifecycleOwner) { tasks ->
            val historyItems = mutableListOf<HistoryItem>()
            val historyItemsNoComplete = mutableListOf<HistoryItem>()
            val filteredTasks = tasks.filter {
                it.tick
            }
            val filteredTasksNoComplete = tasks.filter {
                !it.tick
            }

            for (i in 7 downTo 0) {
                val date = timePresent.minusDays(i.toLong())
                val pattern = "${date.dayOfMonth}${date.monthValue}${date.year}"
                val formatter =
                    DateTimeFormatter.ofPattern("EEEE, 'ngày' d 'tháng' M", Locale("vi"))
                val displayDate = date.format(formatter).replaceFirstChar { it.uppercase() }
                val dayTasks = filteredTasks.filter {
                    it.taskIdDate.take(pattern.length) == pattern
                }
                //cong viec qua han
                val dayTasksNoComplete = filteredTasksNoComplete.filter {
                    it.taskIdDate.take(pattern.length) == pattern
                }
                if (dayTasks.isNotEmpty()) {
                    historyItems.add(HistoryItem.Header(date, displayDate))
                    dayTasks.sortedBy { it.timeTask }.forEach { task ->
                        historyItems.add(HistoryItem.Task(task))
                    }
                }
                if (dayTasksNoComplete.isNotEmpty()) {
                    historyItemsNoComplete.add(HistoryItem.Header(date, displayDate))
                    dayTasksNoComplete.sortedBy { it.timeTask }.forEach { task ->
                        historyItemsNoComplete.add(HistoryItem.TaskNoComplete(task))
                    }
                }
            }

            cnt = historyItems.size
            Log.e("soluong", cnt.toString())
            adapterTaskHis.submitList(historyItems)
            if (historyItems.isEmpty()) {
                binding.dahoanthanh.visibility = View.GONE
            } else {
                binding.dahoanthanh.visibility = View.VISIBLE
            }
            adapterTaskHisNoComplete.submitList(historyItemsNoComplete)
            if (historyItemsNoComplete.isEmpty()) {
                binding.quahan.visibility = View.GONE
            } else {
                binding.quahan.visibility = View.VISIBLE
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}