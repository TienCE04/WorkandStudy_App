package com.example.workandstudy_app.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.workandstudy_app.R
import com.example.workandstudy_app.databinding.ProfileActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class Profile : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ProfileActivityBinding
    private lateinit var db: DatabaseReference
    private val auth = FirebaseAuth.getInstance()
    private val TAG = "Profile"
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private val savePic = FileSavePictures()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                imageUri?.let {
                    val savedFile = savePic.saveImageToAppStorage(this, it)
                    if (savedFile != null) {
                        binding.avt.setImageURI(null)
                        binding.avt.setImageURI(Uri.fromFile(savedFile))
                        Toast.makeText(this, "Đã lưu ảnh", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Lỗi khi lưu ảnh", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        init()
        // Hiển thị ảnh avatar nếu có, nếu không thì hiển thị ảnh mặc định
        val avatarFile = File(filesDir, "image_avt.jpg")
        if (avatarFile.exists()) {
            binding.avt.setImageURI(Uri.fromFile(avatarFile))
        }
    }

    private fun init() {
        db =
            FirebaseDatabase.getInstance("https://login-95b7a-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Users")
        binding.luuInfor.setOnClickListener(this)
        binding.changePic.setOnClickListener(this)
        loadProfile()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.luuInfor -> {
                // Lấy dữ liệu từ các trường nhập liệu
                val hoten = binding.tenSV.text.toString().trim()
                val tentruong = binding.tenDH.text.toString().trim()
                val ngaysinh = binding.nSinh.text.toString().trim()
                val email = binding.eMail.text.toString().trim()
                val sodt = binding.sDT.text.toString().trim()
                val nganhhoc = binding.nHoc.text.toString().trim()
                val khoa = binding.khoa.text.toString().trim()


                // Tạo map dữ liệu
                val inforSV = hashMapOf(
                    "tenSV" to hoten,
                    "tenTruong" to tentruong,
                    "ngaySinh" to ngaysinh,
                    "eMail" to email,
                    "soDT" to sodt,
                    "nganhHoc" to nganhhoc,
                    "khoa" to khoa
                )

                // Lưu dữ liệu vào Firestore
                lifecycleScope.launch {
                    val userId = auth.currentUser?.uid
                    if (userId == null) {
                        Log.e(TAG, "Người dùng chưa đăng nhập")
                        Toast.makeText(
                            this@Profile,
                            "Người dùng chưa đăng nhập!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        return@launch
                    }

                    try {
                        db.child(userId).setValue(inforSV)
                            .addOnSuccessListener {
                                if (!isFinishing) {
                                    Toast.makeText(
                                        this@Profile,
                                        "Lưu thành công",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                            .addOnFailureListener { e ->
                                if (!isFinishing) {
                                    Log.e(TAG, "Lỗi khi lưu hồ sơ: ${e.message}", e)
                                    Toast.makeText(
                                        this@Profile,
                                        "Lỗi khi lưu: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } catch (e: Exception) {
                        Log.e(TAG, "Lỗi không mong muốn: ${e.message}", e)
                        if (!isFinishing) {
                            Toast.makeText(
                                this@Profile,
                                "Đã xảy ra lỗi: ${e.message}",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
                binding.root.clearFocus()
            }

            R.id.changePic -> {
                checkAndRequestPermission(this) {
                    openGallery()
                }
            }
        }
    }

    private fun loadProfile() {
        val userId = auth.currentUser?.uid ?: return
        db.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.tenSV.setText(
                        snapshot.child("tenSV").getValue(String::class.java) ?: ""
                    )
                    binding.tenDH.setText(
                        snapshot.child("tenTruong").getValue(String::class.java) ?: ""
                    )
                    binding.nSinh.setText(
                        snapshot.child("ngaySinh").getValue(String::class.java) ?: ""
                    )
                    binding.eMail.setText(
                        snapshot.child("eMail").getValue(String::class.java) ?: ""
                    )
                    binding.sDT.setText(snapshot.child("soDT").getValue(String::class.java) ?: "")
                    binding.nHoc.setText(
                        snapshot.child("nganhHoc").getValue(String::class.java) ?: ""
                    )
                    binding.khoa.setText(snapshot.child("khoa").getValue(String::class.java) ?: "")
                } else {
                    Log.e(TAG, "Hồ sơ chưa tồn tại")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Lỗi khi tải hồ sơ: ${error.message}")
                if (!isFinishing) {
                    Toast.makeText(this@Profile, "Lỗi khi tải hồ sơ!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    //them avt
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    //check sdk
    fun checkAndRequestPermission(context: Context, onGranted: () -> Unit) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onGranted()
        } else {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(permission),
                1001
            )
        }
    }

}