package com.example.workandstudy_app.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workandstudy_app.R
import com.example.workandstudy_app.databinding.ScreenMainBinding
import com.example.workandstudy_app.document.Document_Activity.Document
import com.example.workandstudy_app.profile.Profile
import com.example.workandstudy_app.school_schedule.ScreenTKB
import com.example.workandstudy_app.settting.Setting
import com.example.workandstudy_app.tienich.ScreenUtilities
import com.example.workandstudy_app.todolist.Entity.TasksData
import com.example.workandstudy_app.todolist.todo_schedule.ScreenTodoList
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File

class ScreenMain : AppCompatActivity(), View.OnClickListener, TabLayout.OnTabSelectedListener {
    private lateinit var binding: ScreenMainBinding
    private lateinit var intent: Intent
    private lateinit var adapterNoti: AdapterNoti
    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var tabLayout: TabLayout
    private lateinit var db: DatabaseReference
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ScreenMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tabLayout = binding.tabLayout
        init()
        initNameUser()
    }

    //
    private fun initNameUser() {
        val avatar = File(filesDir, "image_avt.jpg")
        if (avatar.exists()) {
            binding.avt.setImageURI(Uri.fromFile(avatar))
        }
        db =
            FirebaseDatabase.getInstance("https://login-95b7a-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Users")
        loadInfo()
    }


    fun init() {
        binding.avtTen.setOnClickListener(this)
        tabLayout.addOnTabSelectedListener(this)
        binding.calendarTKB.setOnClickListener(this)
        binding.documentMain.setOnClickListener(this)
        binding.TodoList.setOnClickListener(this)
        binding.tienich.setOnClickListener(this)
        adapterNoti = AdapterNoti()
        val initialTasks = listOf(
            TasksData(
                titleTask = "Task 1", timeTask = "10:00",
                taskIdDate = "",
                contentTask = "",
            ),
            TasksData(
                titleTask = "Task 2", timeTask = "12:00",
                taskIdDate = "",
                contentTask = "",
            )
        )
        adapterNoti.submitList(initialTasks)
        notificationRecyclerView = binding.notificationRecyclerView
        notificationRecyclerView.layoutManager = LinearLayoutManager(this)
        notificationRecyclerView.adapter = adapterNoti
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.calendarTKB -> {
                intent = Intent(this, ScreenTKB::class.java)
                startActivity(intent)
            }

            R.id.documentMain -> {
                intent = Intent(this, Document::class.java)
                startActivity(intent)
            }

            R.id.TodoList -> {
                intent = Intent(this, ScreenTodoList::class.java)
                startActivity(intent)
            }

            R.id.tienich -> {
                intent = Intent(this, ScreenUtilities::class.java)
                startActivity(intent)
            }
            R.id.avtTen->{
                intent= Intent(this, Profile::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        val intent: Intent
        when (tab?.position) {
            0 -> {
                return
            }

            1 -> {
                intent = Intent(this, ScreenTKB::class.java)
                startActivity(intent)
            }

            2 -> {
                intent = Intent(this, Profile::class.java)
                startActivity(intent)
            }

            3 -> {
                intent = Intent(this, Setting::class.java)
                startActivity(intent)
            }
        }
        binding.tabLayout.getTabAt(0)?.select()
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {}

    override fun onTabReselected(tab: TabLayout.Tab?) {}

    //
    private fun loadInfo() {
        val userId = auth.currentUser?.uid ?: return
        db.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.userName.text =
                        snapshot.child("tenSV").getValue(String::class.java) ?: "Love you"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (!isFinishing) {
                    Toast.makeText(this@ScreenMain, "Lỗi tải thông tin", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val avatar = File(filesDir, "image_avt.jpg")
        if (avatar.exists()) {
            binding.avt.setImageURI(null)
            binding.avt.setImageURI(Uri.fromFile(avatar))
        }
    }
}