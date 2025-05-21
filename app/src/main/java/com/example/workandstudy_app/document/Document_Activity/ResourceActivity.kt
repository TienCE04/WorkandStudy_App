package com.example.workandstudy_app.document.Document_Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.workandstudy_app.Database.AppDatabase
import com.example.workandstudy_app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ResourceActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resource)
        db = AppDatabase.getDatabase(this)
        val monHocId = intent.getIntExtra("monHocId", -1)

        CoroutineScope(Dispatchers.Main).launch {
            val monHoc = withContext(Dispatchers.IO) { db.monHocDao().getById(monHocId) }
            findViewById<TextView>(R.id.tieude).text = monHoc?.tenMonHoc ?: "Unknown"
            if (monHoc?.isDefault == 1) {
                findViewById<TextView>(R.id.xoaMonHoc).visibility = View.GONE
            }
        }

        findViewById<TextView>(R.id.baigiang).setOnClickListener {
            val intent = Intent(this, FileListActivity::class.java)
            intent.putExtra("monHocId", monHocId)
            intent.putExtra("phanLoai", "Bài giảng")
            startActivity(intent)
        }
        findViewById<TextView>(R.id.decuong).setOnClickListener {
            val intent = Intent(this, FileListActivity::class.java)
            intent.putExtra("monHocId", monHocId)
            intent.putExtra("phanLoai", "Đề cương")
            startActivity(intent)
        }
        findViewById<TextView>(R.id.tailieugiuaky).setOnClickListener {
            val intent = Intent(this, FileListActivity::class.java)
            intent.putExtra("monHocId", monHocId)
            intent.putExtra("phanLoai", "Tài liệu giữa kỳ")
            startActivity(intent)
        }
        findViewById<TextView>(R.id.tailieucuoiky).setOnClickListener {
            val intent = Intent(this, FileListActivity::class.java)
            intent.putExtra("monHocId", monHocId)
            intent.putExtra("phanLoai", "Tài liệu cuối kỳ")
            startActivity(intent)
        }
        findViewById<TextView>(R.id.tailieuthamkhao).setOnClickListener {
            val intent = Intent(this, FileListActivity::class.java)
            intent.putExtra("monHocId", monHocId)
            intent.putExtra("phanLoai", "Tài liệu tham khảo")
            startActivity(intent)
        }
        findViewById<TextView>(R.id.xoaMonHoc).setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val monHoc = withContext(Dispatchers.IO) { db.monHocDao().getById(monHocId) }
                if (monHoc != null) {
                    try {
                        withContext(Dispatchers.IO) {
                            db.taiLieuDao().deleteBymonHocID(monHocId)
                            db.monHocDao().delete(monHoc)
                        }
                        Toast.makeText(
                            this@ResourceActivity,
                            "Xóa thành công !",
                            Toast.LENGTH_SHORT
                        ).show()
                        intent.putExtra("update",1)
                        finish()

                    } catch (e: Exception) {
                        return@launch
                    }
                }
            }
        }
    }
}