package com.example.mediaplayer

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    var mediaPlayer: MediaPlayer = MediaPlayer()
    var c = true
    lateinit var thread: Thread
    var prg = 0
    var flag = false
    var name = ""
    var seek = 0
    var uri:Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var seekbar = findViewById<SeekBar>(R.id.seekBar)
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, currentValue: Int, p2: Boolean) {
                seek = currentValue
                prg = seek
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
                stop()
                seekStart()
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                seekStop()
                start()
            }
        })
        thread = Thread{
            while (true){
                if(flag){
                    seekbar.post {
                        seekbar.setProgress(prg)
                        prg = prg + 1000
                    }

                    Thread.sleep(1000)
                }
            }
        }
        thread.start()
    }
    fun Play(view: View){

        try {
            if (!c){
                mediaPlayer.stop()
                stop()
                c = true
                findViewById<ImageView>(R.id.play).setImageResource(R.drawable.ic_baseline_play_circle_24);
            }
            else
            {
                mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(this, uri!!)
                mediaPlayer.prepare()
                mediaPlayer.seekTo(seek)
                mediaPlayer.start()
                start()
                c = false
                findViewById<ImageView>(R.id.play).setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
            }

        }catch (E:Exception){}
    }
    fun seekStop() {
        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(this, uri!!)
            mediaPlayer.prepare()

            mediaPlayer.seekTo(seek)

            mediaPlayer.start()

            Toast.makeText(this, seek.toString(), Toast.LENGTH_SHORT).show()
        }
        catch (e:Exception){}
    }
    fun seekStart(){
        mediaPlayer.reset()
    }
    fun stop() {
        flag = false
    }

    fun start() {
        flag = true
    }
    fun addSong(view: View){

        try {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "audio/*"
            startActivityForResult(intent, 1234)
        } catch (e: Exception) {
            println("An exception occurred: ${e.message}")
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1234 && resultCode == RESULT_OK) {
            uri = data?.data
            var seekbar = findViewById<SeekBar>(R.id.seekBar)

            val cursor = contentResolver.query(
                uri!!,null,null,null,null
            )
            if(cursor != null && cursor.moveToFirst()){
                name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                findViewById<TextView>(R.id.songName).text = name
            }

            mediaPlayer.reset()
            mediaPlayer.setDataSource(this,uri!!)
            mediaPlayer.prepare()
            seekbar.setMax(mediaPlayer.duration)
            seekbar.setProgress(0)
            mediaPlayer.start()
            findViewById<ImageView>(R.id.play).setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);

            c = false
        }
    }
    fun plus(view:View){
        try {
            stop()
            mediaPlayer.reset()
            prg = prg+10000
            mediaPlayer.setDataSource(this,uri!!)
            mediaPlayer.prepare()
            mediaPlayer.seekTo(prg)
            mediaPlayer.start()
            start()
        }
        catch (E:Exception){}
    }
    fun minus(view: View){
        try {
            stop()
            mediaPlayer.reset()
            prg = prg-10000
            mediaPlayer.setDataSource(this,uri!!)
            mediaPlayer.prepare()
            mediaPlayer.seekTo(prg)
            mediaPlayer.start()
            start()
        }
        catch (E:Exception){}
    }
}