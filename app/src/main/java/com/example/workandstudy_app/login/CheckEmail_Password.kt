package com.example.workandstudy_app.login

class CheckEmail_Password(
    private val e:String,
    private val p: String) {
    fun isValidEmail(): Boolean {
        val emailPattern = Regex("^[\\w.-]+@gmail\\.com$")
        return emailPattern.matches(e) && e.length >= 11 && e.length <= 40
    }

    fun isValidPassword(): Boolean {
        val passwordPattern = Regex("^(?=.*\\d)(?=.*[@#\$%^&+=!*])[\\S]{6,}$")
        return passwordPattern.matches(p) && p.length <= 40
    }
}