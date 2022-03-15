package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val fileNameTextView = findViewById<TextView>(R.id.file_name_value)
        val statusTextView = findViewById<TextView>(R.id.status_value)

        val fileName = intent.extras?.getString("filename")
        val status = intent.extras?.getString("status")

        fileNameTextView.text = fileName
        fileNameTextView.setTextColor(getColor(R.color.colorPrimaryDark))
        statusTextView.text = status
        if (status == "failed") statusTextView.setTextColor(Color.RED) else statusTextView
                                            .setTextColor(getColor(R.color.colorPrimaryDark))

        return_button.setOnClickListener{
            navigateToMain()
        }
    }

    fun navigateToMain(){
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
    }

}
