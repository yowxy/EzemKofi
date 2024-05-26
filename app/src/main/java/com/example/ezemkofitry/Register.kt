package com.example.ezemkofitry

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.example.ezemkofitry.databinding.ActivityRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class Register : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnSingnin.setOnClickListener {
            if(binding.username.text.isNullOrEmpty()){
                Toast.makeText(this, "kolom username tidaknboleh kosong", Toast.LENGTH_SHORT).show()
            }
            else if(binding.fullname.text.isNullOrEmpty()){
                Toast.makeText(this, "kolom fullname harus di isi", Toast.LENGTH_SHORT).show()
            }
            else if(binding.Email.text.isNullOrEmpty()){
                Toast.makeText(this, "kolom email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            else if(binding.password.text.isNullOrEmpty()){
                Toast.makeText(this, "kolom password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            else if(binding.password.text == binding.confirmButton.text){
                Toast.makeText(this, "kolom konfirm password tidak boleh sama", Toast.LENGTH_SHORT).show()
            }
            else{
                GlobalScope.launch(Dispatchers.IO) {
                    val conn = URL("http://10.0.2.2:5000/api/register").openConnection().apply {
                        this as HttpURLConnection
                        requestMethod = "POST"
                        setRequestProperty("Content-Type", "application/json")
                        outputStream.write(JSONObject().apply {
                            put("username", binding.username.text)
                            put("password", binding.password.text)
                            put("fullname", binding.fullname.text)
                            put("email", binding.Email.text)
                        }.toString().toByteArray())
                    } as HttpURLConnection

                    if (conn.responseCode in 200..299) {
                        getSharedPreferences("EsemkaCoffe", Context.MODE_PRIVATE).edit().putString("DataToken", conn.inputStream.bufferedReader().readText()).apply()
                        runOnUiThread {
                            Toast.makeText(this@Register, "hello ${binding.username.text}", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@Register, Mainscreen::class.java))
                            finish()
                        }
                    }else {
                        val erorr  = conn.errorStream.bufferedReader().readText()
                        runOnUiThread {
                            Toast.makeText(this@Register, erorr, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }
}