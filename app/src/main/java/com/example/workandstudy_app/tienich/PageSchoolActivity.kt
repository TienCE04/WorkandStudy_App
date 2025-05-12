package com.example.workandstudy_app.tienich

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.workandstudy_app.R
import com.example.workandstudy_app.databinding.ItemInforSchoolBinding

class PageSchoolActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ItemInforSchoolBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ItemInforSchoolBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        // Gán sự kiện click cho từng nút
        with(binding) {
            trangchu.setOnClickListener (this@PageSchoolActivity)
            ctt.setOnClickListener(this@PageSchoolActivity)
            fed.setOnClickListener(this@PageSchoolActivity)
            sme.setOnClickListener(this@PageSchoolActivity)
            soict.setOnClickListener(this@PageSchoolActivity)
            seee.setOnClickListener(this@PageSchoolActivity)
            scls.setOnClickListener(this@PageSchoolActivity)
            smse.setOnClickListener(this@PageSchoolActivity)
            sem.setOnClickListener(this@PageSchoolActivity)
            fami.setOnClickListener(this@PageSchoolActivity)
            sep.setOnClickListener(this@PageSchoolActivity)
            sofl.setOnClickListener(this@PageSchoolActivity)
            sis.setOnClickListener(this@PageSchoolActivity)
            dksis.setOnClickListener(this@PageSchoolActivity)
            fanpage.setOnClickListener(this@PageSchoolActivity)
            thuctap.setOnClickListener(this@PageSchoolActivity)
        }
    }

    override fun onClick(v: View?) {
        // Ánh xạ ID nút với URL tương ứng
        val url = when (v?.id) {
            R.id.trangchu -> "https://www.hust.edu.vn/"
            R.id.ctt -> "https://ctt.hust.edu.vn/"
            R.id.fed -> "https://fed.hust.edu.vn/"
            R.id.sme -> "https://sme.hust.edu.vn/"
            R.id.soict -> "https://soict.hust.edu.vn/"
            R.id.seee -> "https://seee.hust.edu.vn/"
            R.id.scls -> "https://scls.hust.edu.vn/"
            R.id.smse -> "https://smse.hust.edu.vn/"
            R.id.sem -> "https://sem.hust.edu.vn/"
            R.id.fami -> "https://fami.hust.edu.vn/"
            R.id.sep -> "https://sep.hust.edu.vn/"
            R.id.sofl -> "https://sofl.hust.edu.vn/"
            R.id.sis -> "https://ctt-sis.hust.edu.vn/Account/Login.aspx"
            R.id.dksis -> "https://dk-sis.hust.edu.vn/Users/Login.aspx"
            R.id.fanpage -> "https://www.facebook.com/dhbkhanoi"
            R.id.thuctap -> "https://www.facebook.com/groups/ttdn.soict"
            else -> "https://www.google.com/" // URL mặc định nếu không khớp
        }

        // Chuyển sang WebViewActivity và truyền URL
        startActivity(Intent(this, WebViewActivity::class.java).apply {
            putExtra("URL", url)
        })
    }
}