package com.example.workandstudy_app.settting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.workandstudy_app.R
import com.example.workandstudy_app.databinding.SettingActitivityBinding
import com.example.workandstudy_app.login.ScreenLogin

class Setting : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: SettingActitivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingActitivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.signOut.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.signOut ->{
                intent=Intent(this, ScreenLogin::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}