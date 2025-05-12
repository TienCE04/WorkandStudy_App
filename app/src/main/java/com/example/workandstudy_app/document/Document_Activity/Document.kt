package com.example.workandstudy_app.document.Document_Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.workandstudy_app.R
import com.example.workandstudy_app.databinding.ScreenDocumentBinding
import com.google.android.material.tabs.TabLayoutMediator

class Document : AppCompatActivity(), View.OnClickListener{
    private lateinit var binding: ScreenDocumentBinding
    private lateinit var adapter: ViewPagerAdapterDocument

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ScreenDocumentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter= ViewPagerAdapterDocument(this)
        binding.viewCalendar.adapter=adapter
        TabLayoutMediator(binding.tabCalendar,binding.viewCalendar){tab,position->
            tab.text=when(position){
                0->"Ngành học"
                1->"Cá nhân"
                2->"Other"
                else->"Ngành học"
            }
        }.attach()
        binding.fab.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.fab -> {
                val intent=Intent(this,AddDocumentActivity::class.java)
                startActivity(intent)
            }
        }
    }

}