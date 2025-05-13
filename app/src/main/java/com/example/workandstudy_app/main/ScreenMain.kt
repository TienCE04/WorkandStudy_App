package com.example.workandstudy_app.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workandstudy_app.Database.AppDatabase
import com.example.workandstudy_app.R
import com.example.workandstudy_app.databinding.ScreenMainBinding
import com.example.workandstudy_app.document.Document_Activity.Document
import com.example.workandstudy_app.profile.Profile
import com.example.workandstudy_app.school_schedule.ScreenTKB
import com.example.workandstudy_app.settting.Setting
import com.example.workandstudy_app.tienich.ScreenUtilities
import com.example.workandstudy_app.todolist.todo_schedule.ScreenTodoList
import com.example.workandstudy_app.todolist.todo_schedule.SharedViewModelTodo
import com.example.workandstudy_app.todolist.todo_schedule.TaskRepository
import com.example.workandstudy_app.todolist.todo_schedule.ViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.time.LocalDate

class ScreenMain : AppCompatActivity(), View.OnClickListener, TabLayout.OnTabSelectedListener {
    private lateinit var binding: ScreenMainBinding
    private lateinit var intent: Intent
    private lateinit var adapterNoti: AdapterNoti
    private lateinit var tabLayout: TabLayout
    private lateinit var db: DatabaseReference
    private val auth = FirebaseAuth.getInstance()
    private lateinit var viewModel: SharedViewModelTodo
    private val timeDate = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ScreenMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tabLayout = binding.tabLayout
        init()
        initListinScreenMain()
        initNameUser()
    }

    private fun initListinScreenMain() {
        val repository = TaskRepository(AppDatabase.getDatabase(this).tasksDao())
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(repository)
        )[SharedViewModelTodo::class.java]
        adapterNoti = AdapterNoti()
        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notificationRecyclerView.adapter = adapterNoti

        viewModel.selectedTasks.observe(this) { tasks ->
            adapterNoti.submitList(tasks)
        }

        viewModel.loadTasks("${timeDate.dayOfMonth}${timeDate.monthValue}${timeDate.year}%")
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
        binding.detail.setOnClickListener (this)
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

            R.id.avtTen -> {
                intent = Intent(this, Profile::class.java)
                startActivity(intent)
            }
            R.id.detail ->{
                intent= Intent(this,ScreenTodoList::class.java)
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
        initListinScreenMain()
    }
}