package com.example.workandstudy_app.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.workandstudy_app.R
import com.example.workandstudy_app.databinding.SignupActivityBinding
import com.example.workandstudy_app.login.CheckEmail_Password
import com.example.workandstudy_app.login.ScreenLogin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ScreenSignUp : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: SignupActivityBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var checkemailPassword: CheckEmail_Password


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignupActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

    }

    fun init() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        binding.SignUp.setOnClickListener(this)
        binding.backtologin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.SignUp -> registerUser()
            R.id.backtologin -> finish()
        }
    }

    //đăng ký
    fun registerUser() {
        val email = binding.eMail.text.toString().trim()
        val password = binding.passWord.text.toString().trim()
        val confimrpassword = binding.confirmpass.text.toString().trim()
        val phone = binding.phone.text.toString().trim()
        checkemailPassword = CheckEmail_Password(email, password)
        if (checkemailPassword.isValidEmail() && checkemailPassword.isValidPassword()) {
            if (confimrpassword != password) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userID = auth.currentUser?.uid ?: return@addOnCompleteListener

                        //lưu thông tin email và số điện thoại vào firestore
                        val userMap = hashMapOf(
                            "email" to email,
                            "phone" to phone
                        )

                        db.collection("Users").document(userID).set(userMap)
                            .addOnCompleteListener {
                                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT)
                                    .show()
                                val intent = Intent(this, ScreenLogin::class.java)
                                val bundle = Bundle()
                                bundle.putString("email", email)
                                bundle.putString("password", password)
                                intent.putExtras(bundle)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.e(
                                    "Firestore",
                                    "Lỗi khi lưu thông tin người dùng: ${e.message}"
                                )
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "Đăng ký thất bại: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        } else {
            if (checkemailPassword.isValidEmail() != true) {
                binding.eMail.requestFocus()
                Toast.makeText(
                    this,
                    "email có dạng example@gmail.com và nhỏ hơn 25 ký tự",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                binding.passWord.requestFocus()
                Toast.makeText(
                    this,
                    "Mật khẩu gồm tối thiểu 6 ký tự, bao gồm chữ số và ký tự đặc biệt!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}