package com.fb.myapplication

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Hide the action bar
        supportActionBar?.hide()

        // Initialize VideoView
        videoView = findViewById(R.id.splashVideo)

        // Set video path from assets
        val videoPath = "android.resource://" + packageName + "/raw/mobile_splash_screen"
        videoView.setVideoURI(Uri.parse(videoPath))

        // Start playing the video
        videoView.start()

        // Set up completion listener
        videoView.setOnCompletionListener {
            // Start LoginActivity when video completes
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Set up error listener
        videoView.setOnErrorListener { _, _, _ ->
            // If there's an error, just start LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            true
        }
    }

    override fun onPause() {
        super.onPause()
        if (videoView.isPlaying) {
            videoView.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.stopPlayback()
    }
} 