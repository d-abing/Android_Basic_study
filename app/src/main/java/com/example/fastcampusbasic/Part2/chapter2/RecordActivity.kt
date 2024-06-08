package com.example.fastcampusbasic.Part2.chapter2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.ActivityRecordBinding
import java.io.IOException

class RecordActivity : AppCompatActivity(), OnTimerTickListener {

    companion object {
        private const val REQUEST_RECORD_AUDIO_CODE = 200
    }

    private enum class State {
        RELEASE, RECORDING, PLAYING
    }

    private lateinit var timer: Timer
    private lateinit var binding: ActivityRecordBinding
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var fileName: String = ""
    private var state: State = State.RELEASE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fileName = "${externalCacheDir?.absolutePath}/audiorecordtest.3gp"
        timer = Timer(this)

        binding.recordBtn.setOnClickListener {

            when(state) {
                State.RELEASE -> {
                    checkRecordPermission()
                }
                State.RECORDING -> {
                    stopRecording()
                }
                else -> {
                    // do nothing
                }
            }
        }

        binding.playBtn.setOnClickListener {

            when(state) {
                State.RELEASE -> {
                    startPlaying()
                }
                else -> {
                    // do nothing
                }
            }
        }

        binding.stopBtn.setOnClickListener {

            when(state) {
                State.PLAYING -> {
                    stopPlaying()
                }
                else -> {
                    // do nothing
                }
            }
        }
    }

    private fun checkRecordPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startRecording()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.RECORD_AUDIO) -> {
                showPermissionRationalDialog()
            }
            else -> {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_CODE)
            }
        }
    }

    private fun startRecording() {
        state = State.RECORDING
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("APP", "prepare() failed $e")
            }

            start()
        }

        binding.waveformView.clearData()
        timer.start()

        binding.recordBtn.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.baseline_stop_24
            )
        )
        binding.recordBtn.imageTintList = ColorStateList.valueOf(Color.BLACK)
        binding.playG.visibility = View.GONE
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null

        timer.stop()

        state = State.RELEASE

        binding.recordBtn.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.baseline_fiber_manual_record_24
            )
        )
        binding.recordBtn.imageTintList = ColorStateList.valueOf(Color.RED)
        binding.playG.visibility = View.VISIBLE
    }

    private fun startPlaying() {
        state = State.PLAYING

        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
            } catch (e:IOException) {
                Log.e("APP", "media player prepare fail $e")
            }

            start()
        }

        binding.waveformView.clearWave()
        timer.start()

        player?.setOnCompletionListener {
            stopPlaying()
        }

        binding.recordBtn.isEnabled = false
        binding.recordBtn.alpha = 0.3f
    }

    private fun stopPlaying() {
        player?.release()
        player = null
        state = State.RELEASE

        timer.stop()

        binding.recordBtn.isEnabled = true
        binding.recordBtn.alpha = 1.0f
    }


    private fun showPermissionRationalDialog() {
        AlertDialog.Builder(this)
            .setMessage("녹음 권한을 켜주셔야 앱을 정상적으로 사용할 수 있습니다.")
            .setPositiveButton("권한 허용하기") { _, _ ->
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_CODE)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun showPermissionSettingDialog() {
        AlertDialog.Builder(this)
            .setMessage("녹음 권한을 켜주셔야 앱을 정상적으로 사용할 수 있습니다. 앱 설정 화면에서 권한을 켜주세요.")
            .setPositiveButton("권한 변경하러 가기") { _, _ ->
                navigateToAppSetting()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun navigateToAppSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermissionGranted =
            requestCode == REQUEST_RECORD_AUDIO_CODE && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        if (audioRecordPermissionGranted) {
            startRecording()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                showPermissionRationalDialog()
            } else {
                showPermissionSettingDialog()
            }
        }
    }

    override fun onTick(duration: Long) {
        val millisecond = (duration % 1000) / 100
        val second = (duration / 1000) % 60
        val minute = (duration / 1000) / 60

        binding.timerTv.text = String.format("%02d:%02d.%01d", minute, second, millisecond)

        if(state == State.PLAYING) {
            binding.waveformView.replayAmplitude()
        } else if (state == State.RECORDING) {
            binding.waveformView.addAmplitude(
                recorder?.maxAmplitude?.toFloat() ?: 0f
            )
        }
    }
}