package com.example.workandstudy_app.document.Document_Activity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.workandstudy_app.Database.AppDatabase
import com.example.workandstudy_app.document.Entity.Documents
import com.example.workandstudy_app.document.Entity.Subjects
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.workandstudy_app.R
import com.example.workandstudy_app.databinding.ActivityAddDocumentBinding
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddDocumentActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var binding: ActivityAddDocumentBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_document)
        db = AppDatabase.getDatabase(this)
        binding = ActivityAddDocumentBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        binding.ngayTao.text=formattedDate
        init()

        binding.Them.setOnClickListener {
            if (binding.tenMonHoc.text.isEmpty()) {
                binding.tenMonHoc.error = "Vui lòng nhập tên môn học"
                binding.tenMonHoc.requestFocus()
                return@setOnClickListener
            } else if (binding.tenFile.text.isEmpty()) {
                binding.tenFile.error = "Vui lòng nhập tên file"
                binding.tenFile.requestFocus()
                return@setOnClickListener
            } else if (binding.urlFile.text.isEmpty()) {
                binding.urlFile.error = "Vui lòng nhập url file"
                binding.urlFile.requestFocus()
                return@setOnClickListener
            } else if (binding.ngayTao.text.isEmpty()) {
                binding.ngayTao.error = "Vui lòng nhập ngày tạo"
                binding.ngayTao.requestFocus()
                return@setOnClickListener
            }
            else {
                val tenMonHoc = binding.tenMonHoc.text.toString()
                val danhMuc = binding.danhMuc.text.toString()
                val phanLoai = binding.phanLoai.text.toString()
                val tenFile = binding.tenFile.text.toString()
                val urlFile = binding.urlFile.text.toString()
                val ngayTao =formattedDate

                CoroutineScope(Dispatchers.IO).launch {
                    val existingMonHoc = db.monHocDao().getByName(tenMonHoc)
                    val monHocId: Int
                    if (existingMonHoc == null) {
                        val monHoc =
                            Subjects(tenMonHoc = tenMonHoc, ngayThem = ngayTao, isDefault = 0)
                        monHocId = db.monHocDao().insert(monHoc).toInt()
                    } else {
                        monHocId = existingMonHoc.id
                    }

                    val taiLieu = Documents(
                        monHocId = monHocId, danhMuc = danhMuc, tenFile = tenFile,
                        phanLoai = phanLoai,
                        urlFile = urlFile, ngayTao = ngayTao, isDefault = 0
                    )
                    db.taiLieuDao().insert(taiLieu)
                    withContext(Dispatchers.Main) { finish() }

                }
            }
        }

        binding.Huy.setOnClickListener { finish() }
    }

    fun init() {
        // tạo adapter cho danh mục
        var adapter = ArrayAdapter.createFromResource(
            this,
            R.array.danhmuc,
            android.R.layout.simple_spinner_item
        )
        binding.dsDanhmuc.adapter = adapter

        adapter = ArrayAdapter.createFromResource(
            this,
            R.array.phanloai,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        binding.dsPhanloai.adapter = adapter

        //bắt sự kiện click
        binding.dsDanhmuc.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                binding.danhMuc.text = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.dsPhanloai.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.phanLoai.text = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

}