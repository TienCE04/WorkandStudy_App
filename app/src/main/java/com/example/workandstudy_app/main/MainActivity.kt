package com.example.workandstudy_app.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.workandstudy_app.databinding.IntroActivityBinding
import com.example.workandstudy_app.login.ScreenLogin

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: IntroActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= IntroActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init(){
        binding.start.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            binding.start -> {
                val intent=Intent(this, ScreenLogin::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}