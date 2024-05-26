package com.example.ezemkofitry

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.ezemkofitry.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.username.setText("mahdi")
        binding.password.setText("1234")

      binding.creatae.setOnClickListener{
          startActivity(Intent(this@MainActivity,Register::class.java))
          finish()
      }

        binding.btnSingnIN.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO){
                val conn = URL("http://10.0.2.2:5000/api/auth").openConnection() as HttpURLConnection
                conn.requestMethod="POST"
                conn.setRequestProperty("Content-Type","application/json")
                try{
                    val json = JSONObject().apply {
                        put("username", binding.username.text)
                        put("password", binding.password.text)
                    }
                    conn.outputStream.write(json.toString().toByteArray())
                }
                catch (e:Exception){
                    Log.e("Network", "Error sending data: ${e.message}")
                }
                finally {

                }


                val code = conn.responseCode

                    if(code in 200..299){
                        runOnUiThread {
                            getSharedPreferences("EsemkaCoffe", Context.MODE_PRIVATE).edit().putString("DataToken",  conn.inputStream.bufferedReader().readText()).apply()

                            runOnUiThread {
                                startActivity(Intent(this@MainActivity, Mainscreen::class.java))
                                Toast.makeText(this@MainActivity, "sukses", Toast.LENGTH_LONG).show()
                                finish()
                            }

                        }
                    }else{
                        val erorr  = conn.errorStream.bufferedReader().readText()
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, erorr, Toast.LENGTH_SHORT).show()
                        }
                    }

            }
        }
    }
}