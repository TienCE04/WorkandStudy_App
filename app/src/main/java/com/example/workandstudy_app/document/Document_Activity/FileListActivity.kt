package com.example.workandstudy_app.document.Document_Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workandstudy_app.Database.AppDatabase
import com.example.workandstudy_app.document.Entity.Documents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.workandstudy_app.R


class FileListActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_list)
        db = AppDatabase.getDatabase(this)
        val monHocId = intent.getIntExtra("monHocId", -1)
        val phanLoai = intent.getStringExtra("phanLoai")

        CoroutineScope(Dispatchers.Main).launch {
            val monHoc = withContext(Dispatchers.IO) { db.monHocDao().getById(monHocId) }
            findViewById<TextView>(R.id.tieudeMonhoc).text = monHoc?.tenMonHoc ?: "Unknown"
            if (monHoc?.isDefault == 1) {
                findViewById<TextView>(R.id.xoaitem).visibility = View.GONE
            }
        }
        findViewById<TextView>(R.id.phanLoaiItem).text = phanLoai

        val recyclerView = findViewById<RecyclerView>(R.id.dsTaiLieu)
        recyclerView.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.Main).launch {
            var taiLieuList = withContext(Dispatchers.IO) {
                db.taiLieuDao().getByMonHocId(monHocId).filter { it.phanLoai == phanLoai }
            }
            val adapter = TaiLieuAdapter(taiLieuList.toMutableList()) { taiLieu ->
                openFile(taiLieu.urlFile)
            }
            recyclerView.adapter = adapter
            findViewById<TextView>(R.id.xoaitem).setOnClickListener {
                val selectedIds = adapter.getSelectedItemIds()
                if (selectedIds.isEmpty()) {
                    Toast.makeText(
                        this@FileListActivity,
                        "Vui lòng chọn tài liệu để xoá",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        db.taiLieuDao().deleteByIds(selectedIds)
                        withContext(Dispatchers.Main) {
                            adapter.removeSelectedItems()
                            Toast.makeText(
                                this@FileListActivity,
                                "Đã xoá thành công",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun openFile(url: String) {
        val uri = Uri.parse(url)

        val mimeType = when {
            url.contains("drive.google.com/drive/folders") -> null // là thư mục -> mở trình duyệt
            url.contains("drive.google.com/file/d/") -> "*/*"       // file drive -> mở xem
            url.endsWith(".pdf") -> "application/pdf"
            url.endsWith(".doc") || url.endsWith(".docx") -> "application/msword"
            url.endsWith(".txt") -> "text/plain"
            else -> "*/*"
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW, uri)

            if (mimeType != null) {
                intent.setDataAndType(uri, mimeType)
            }

            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Không thể mở liên kết hoặc không có ứng dụng hỗ trợ",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}

class TaiLieuAdapter(
    private var taiLieuList: MutableList<Documents>,
    private val onFileClick: (Documents) -> Unit
) : RecyclerView.Adapter<TaiLieuAdapter.ViewHolder>() {
    val selectedItems = mutableSetOf<Int>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_tai_lieu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val taiLieu = taiLieuList[position]
        holder.tenTaiLieu.text = taiLieu.tenFile

        holder.chechBox.isChecked = selectedItems.contains(taiLieu.id)
        holder.chechBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) selectedItems.add(taiLieu.id)
            else selectedItems.remove(taiLieu.id)
        }
        holder.itemView.setOnClickListener { onFileClick(taiLieu) }
    }

    override fun getItemCount(): Int = taiLieuList.size
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tenTaiLieu: TextView = itemView.findViewById(R.id.linkurl)
        val chechBox: CheckBox = itemView.findViewById(R.id.ChoiceDelete)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeSelectedItems() {
        taiLieuList = taiLieuList.filter { !selectedItems.contains(it.id) }.toMutableList()
        notifyDataSetChanged()
    }

    fun getSelectedItemIds(): List<Int> {
        return selectedItems.toList()
    }
}
