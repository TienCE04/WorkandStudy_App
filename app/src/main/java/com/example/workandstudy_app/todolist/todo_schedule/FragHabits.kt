package com.example.workandstudy_app.todolist.todo_schedule

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.workandstudy_app.R
import com.example.workandstudy_app.databinding.FragmentHabitsBinding
import com.example.workandstudy_app.todolist.Entity.DataHabits
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragHabits : Fragment(), View.OnClickListener, AdapterHabit.HabitListener {

    private lateinit var binding: FragmentHabitsBinding
    private lateinit var adapterHabit: AdapterHabit
    private lateinit var db: DatabaseReference
    private val auth = FirebaseAuth.getInstance()
    private val listHabit = mutableListOf<DataHabits>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHabitsBinding.inflate(inflater, container, false)
        init()
        initAdapter()
        getData()
        return binding.root
    }

    private fun init() {
        // Khởi tạo Realtime Database với đường dẫn cho user hiện tại
        val userId = auth.currentUser?.uid ?: return
        db = FirebaseDatabase.getInstance("https://login-95b7a-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Users").child(userId).child("Habits")

        // Khởi tạo adapter cho spinner
        val adapterRecommend = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.habbits,
            android.R.layout.simple_spinner_item
        )
        adapterRecommend.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.dsRecommend.adapter = adapterRecommend
        binding.them.setOnClickListener(this)
    }

    private fun getData() {
        // Lắng nghe dữ liệu từ Realtime Database
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listHabit.clear()
                for (data in snapshot.children) {
                    val habit = data.getValue(DataHabits::class.java)
                    habit?.let { listHabit.add(it) }
                }
                adapterHabit.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Lỗi tải dữ liệu: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initAdapter() {
        adapterHabit = AdapterHabit(this, listHabit as ArrayList<DataHabits>)
        binding.dsHabits.adapter = adapterHabit
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.them -> {
                // Hiển thị dialog để thêm thói quen mới
                showAddHabitDialog()
            }
        }
    }

    private fun showAddHabitDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_add_habit) // Tạo layout dialog_add_habit.xml
        val etHabitName = dialog.findViewById<EditText>(R.id.et_habit_name)
        val etHabitTime = dialog.findViewById<EditText>(R.id.et_habit_time)
        val btnSave = dialog.findViewById<Button>(R.id.btn_save)
        val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog)
        val window = dialog.window
        window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),// chiều rộng
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        btnSave.setOnClickListener {
            val name = etHabitName.text.toString().trim()
            val time = etHabitTime.text.toString().trim()


            if (name.isEmpty() || time.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tạo ID mới dựa trên timestamp
            val newId = System.currentTimeMillis().toInt()
            val newHabit = DataHabits(newId, name, "Hằng ngày", time)

            // Lưu vào Realtime Database
            db.child(newId.toString()).setValue(newHabit)
                .addOnSuccessListener {
                    Toast.makeText(context, "Thêm thói quen thành công!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Lỗi khi thêm: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onSaveClicked(viewHolder: AdapterHabit.ViewHolder,habit: DataHabits, position: Int) {
        // Cập nhật thói quen trong Realtime Database
        habit.tenHabit=viewHolder.tenHabit.text.toString()
        habit.thoiGian=viewHolder.tgian.text.toString()
        db.child(habit.id.toString()).setValue(habit)
            .addOnSuccessListener {
                Toast.makeText(context, "Lưu thói quen thành công!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Lỗi khi lưu: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDeleteClicked(habit: DataHabits, position: Int) {
        // Xóa thói quen khỏi Realtime Database
        db.child(habit.id.toString()).removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Xóa thói quen thành công!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Lỗi khi xóa: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}