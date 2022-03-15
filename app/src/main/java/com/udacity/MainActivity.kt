package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.toolbar
import java.io.File

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var urlToDownload : String
    lateinit var fileToDownload : String
    lateinit var button: LoadingButton

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(
            getString(R.string.channelID),
            getString(R.string.channelName)
        )

        notificationManager = getSystemService(
            NotificationManager::class.java
        ) as NotificationManager

        button = findViewById(R.id.button)
        button.setOnClickListener {
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val action = intent.action

            if (downloadID == id) {
                if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    val query = DownloadManager.Query()
                    query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
                    val manager = context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val cursor: Cursor = manager.query(query)
                    if (cursor.moveToFirst()) {
                        if (cursor.count > 0) {
                            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                button.setLoadingButtonState(ButtonState.Completed)
                                notificationManager.sendNotification(applicationContext, getString(R.string.notification_description), fileToDownload, "success")
                            } else {
                                button.setLoadingButtonState(ButtonState.Completed)
                                notificationManager.sendNotification(applicationContext,getString(R.string.notification_description), fileToDownload, "failed")
                            }
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun download() {
        button.setLoadingButtonState(ButtonState.Clicked)
        if (!this::urlToDownload.isInitialized){
            button.setLoadingButtonState(ButtonState.Completed)
            Toast.makeText(this,getString(R.string.file_missing), Toast.LENGTH_SHORT).show()
        } else {
            button.setLoadingButtonState(ButtonState.Loading)

            val request =
                DownloadManager.Request(Uri.parse(urlToDownload))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
            Log.i(TAG,"downloaded?")
        }
    }

    fun onRadioButtonClicked(view: View) {
        when (view.id){
            R.id.glide_radio_button ->
                {
                    urlToDownload = getString(R.string.glide_url)
                    fileToDownload = getString(R.string.glide_radio_text)
                }
            R.id.loadapp_radio_button ->
                {
                    urlToDownload = getString(R.string.loadapp_url)
                    fileToDownload = getString(R.string.loadapp_radio_text)
                }
            else ->
            {
                urlToDownload = getString(R.string.retrofit_url)
                fileToDownload = getString(R.string.retrofit_radio_text)
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW)
                .apply{
                    setShowBadge(false)
                }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.channel_description)

            val notificationManager = getSystemService(
                NotificationManager::class.java)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
