package com.example.fastcampusbasic.Part2.chapter3

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.fastcampusbasic.databinding.ActivityNotificationBinding
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val editText = binding.serverHostEt
        val confirmButton = binding.confirmBtn
        val informationTextView = binding.informationTv
        val client = OkHttpClient()
        var serverHost = ""

        editText.addTextChangedListener {
            serverHost = it.toString()
        }

        confirmButton.setOnClickListener {
            val request: Request = Request.Builder()
                .url("http://$serverHost:8080")
                .build()

            val callback = object: Callback {
                override fun onFailure(call: Call, e: IOException) {

                    runOnUiThread {
                        Toast.makeText(this@NotificationActivity, "수신에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }

                    Log.e("Client", e.toString())
                }

                override fun onResponse(call: Call, response: Response) {

                    if (response.isSuccessful) {
                        val response = response.body?.string()

                        val message = Gson().fromJson(response, Message::class.java)

                        runOnUiThread {
                            informationTextView.isVisible = true
                            informationTextView.text = message.message

                            editText.isVisible = false
                            confirmButton.isVisible = false
                        }


                    } else {
                        runOnUiThread {
                            Toast.makeText(this@NotificationActivity, "수신에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            client.newCall(request).enqueue(callback)
        }

/*        Thread {
            try {
                val socket = Socket("10.0.2.2", 8080)
                val printer = PrintWriter(socket.getOutputStream())
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                printer.println("GET / HTTP/1.1")
                printer.println("Host: 127.0.0.1:8080")
                printer.println("User-Agent: android")
                printer.println("\r\n")
                printer.flush()

                var input: String? = "-1"
                while(input != null) {
                    input = reader.readLine()
                    Log.e("Client", "$input")
                }

                reader.close()
                printer.close()
                socket.close()
            } catch (e: Exception) {
                Log.e("Client", e.toString())
            }
        }.start()*/


    }
}