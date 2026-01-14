package com.example.workandstudy_app.login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.workandstudy_app.R
import com.example.workandstudy_app.databinding.LoginActivityBinding
import com.example.workandstudy_app.main.MainActivity
import com.example.workandstudy_app.main.ScreenMain
import com.example.workandstudy_app.signup.ScreenSignUp
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.GoogleAuthProvider


class ScreenLogin : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: LoginActivityBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private val RC_SIGN_IN = 9001
    private lateinit var email: String
    private lateinit var pass: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Sử dụng View Binding
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Khởi tạo FirebaseAuth
        auth = FirebaseAuth.getInstance()
        FirebaseApp.initializeApp(this)

        //Cấu hình Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //Cấu hình Facebook Login
        FacebookSdk.sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()

        //Lấy dữ liệu từ sign up
        getDatafromSignUp()

        //Ánh xạ các View
        init()
    }

    private fun getDatafromSignUp() {
        val i = intent
        val bundle = i.extras
        if (bundle != null) {
            val email_signup = bundle.getString("email")
            val password_signup = bundle.getString("password")
            binding.eMail.setText(email_signup)
            binding.passWord.setText(password_signup)
        }
    }

    private fun init() {
        binding.login.setOnClickListener(this)
        binding.signUp.setOnClickListener(this)
        binding.facebookicon.setOnClickListener(this)
        binding.googleicon.setOnClickListener(this)
        binding.forgotPassword.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.login -> loginUser()
            R.id.signUp -> signupUser()
            R.id.googleicon -> loginGG()
            R.id.facebookicon -> loginFB()
            R.id.forgotPassword -> forgotPW()
        }
    }

    //Quên mật khẩu
    private fun forgotPW() {
        val emailAddress = binding.eMail.text.toString().trim()

        if (emailAddress.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ email", Toast.LENGTH_SHORT).show()
            return
        }
        val checkemailPassword = CheckEmail_Password(emailAddress, "")
        if (!checkemailPassword.isValidEmail()) {
            Toast.makeText(this, "Vui lòng nhập đúng định dạng", Toast.LENGTH_SHORT).show()
            return
        }

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Đang gửi yêu cầu đặt lại mật khẩu...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                progressDialog.dismiss()
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Đã gửi email đặt lại mật khẩu. Vui lòng kiểm tra email của bạn.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Lỗi khi gửi yêu cầu. Vui lòng thử lại.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

    }

    //đăng nhập facebook
    private fun loginFB() {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() {
                Toast.makeText(
                    this@ScreenLogin,
                    "Đăng nhập Facebook đã bị hủy.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(
                    this@ScreenLogin,
                    "Đăng nhập Facebook thất bại: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Đăng nhập Facebook thành công!", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorMessage = task.exception?.message
                    Toast.makeText(
                        this,
                        "Đăng nhập Facebook thất bại: $errorMessage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    //đăng nhập google
    private fun loginGG() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    //đăng ký tài khoản
    private fun signupUser() {
        val intent = Intent(this, ScreenSignUp::class.java)
        startActivity(intent)
    }

    //đăng nhập
    private fun loginUser() {
        email = binding.eMail.text.toString().trim()
        pass = binding.passWord.text.toString().trim()

        if (email.isEmpty()) {
            binding.eMail.error = "Vui lòng nhập email"
            binding.eMail.requestFocus()
            return
        }
        if (pass.isEmpty()) {
            binding.passWord.error = "Vui lòng nhập password"
            binding.passWord.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ScreenMain::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        val errorMessage = e.message ?: ""
                        if (errorMessage.contains("badly formatted")) {
                            // Email không đúng định dạng
                            binding.eMail.error = "Vui lòng nhập đúng định dạng example@gmail.com"
                            binding.eMail.requestFocus()
                        } else {
                            // Email đúng định dạng nhưng không tồn tại hoặc mật khẩu không đúng
                            Toast.makeText(
                                this,
                                "Thông tin đăng nhập không đúng!",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.passWord.requestFocus()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    //Xử lý kết quả đăng nhập từ Google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w("SignIn", "Google sign in failed", e)
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    //xác thực với tài khoản từ Google
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ScreenMain::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorMessage = task.exception?.message
                    Toast.makeText(
                        this,
                        "Đăng nhập Google thất bại: $errorMessage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}