package com.example.workandstudy_app.document.Document_Activity

import android.annotation.SuppressLint

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workandstudy_app.Database.AppDatabase
import com.example.workandstudy_app.document.Entity.Subjects
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.workandstudy_app.R


class FragDocMajor : Fragment(), SearchView.OnQueryTextListener {
    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var adapter: MonHocAdapter
    private var dsMonHocGoc: List<Subjects> = emptyList()
//    private val searchDoc= SearchDoc.Trie()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_major, container, false)
        db = AppDatabase.getDatabase(requireContext())
        recyclerView = view.findViewById(R.id.dsTaiLieu)
        recyclerView.layoutManager = LinearLayoutManager(context)
        searchView = view.findViewById(R.id.searchMonHoc)
        searchView.setOnQueryTextListener(this)
        loadMonHoc()
        return view
    }



    private fun loadMonHoc() {
        CoroutineScope(Dispatchers.Main).launch {
            dsMonHocGoc = withContext(Dispatchers.IO) { db.monHocDao().getAll() }
            dsMonHocGoc.forEach { println("Môn học: ${it.tenMonHoc}") }
            adapter = MonHocAdapter(dsMonHocGoc) { monHoc ->
                // Chuyển đến màn hình tài liệu khi click vào môn học
                val intent = Intent(context, ResourceActivity::class.java)
                intent.putExtra("monHocId", monHoc.id)
                startActivity(intent)
            }
            recyclerView.adapter = adapter
            recyclerView.addItemDecoration(SpaceItemDecoration(12))
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        filter(newText.orEmpty())
        return true
    }

    fun filter(query: String) {
        val danhSachLoc = if (query.isEmpty()) {
            dsMonHocGoc
        } else {
            dsMonHocGoc.filter {
                it.tenMonHoc.contains(query, ignoreCase = true)
            }
//            searchDoc.searchPrefix(query)
        }
        adapter.filterList(danhSachLoc)
    }
}

class MonHocAdapter(monHocList: List<Subjects>, private val onClick: (Subjects) -> Unit) :
    RecyclerView.Adapter<MonHocAdapter.ViewHolder>() {
    private var listMonHocChange = monHocList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mon_hoc, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val monHoc = listMonHocChange[position]
        holder.tenMonHoc.text = monHoc.tenMonHoc
        holder.ngayThem.text = monHoc.ngayThem
        holder.itemView.setOnClickListener { onClick(monHoc) }
    }

    override fun getItemCount(): Int = listMonHocChange.size
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tenMonHoc: TextView = itemView.findViewById(R.id.tenTaiLieu)
        val ngayThem: TextView = itemView.findViewById(R.id.ngayThem)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterList: List<Subjects>) {
        listMonHocChange = filterList
        notifyDataSetChanged()
    }

}