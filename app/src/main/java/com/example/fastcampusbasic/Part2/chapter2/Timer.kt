package com.example.fastcampusbasic.Part2.chapter2

import android.os.Handler
import android.os.Looper

class Timer(listener: OnTimerTickListener) {
    private var duration: Long = 0L
    private val handler = Handler(Looper.getMainLooper())
    private val runnable: Runnable = object: Runnable {
        override fun run() {
            duration += 100L
            handler.postDelayed(this, 100L)
            listener.onTick(duration)
        }
    }

    fun start() {
        handler.postDelayed(runnable, 100L)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
        duration = 0
    }
}

interface OnTimerTickListener {
    fun onTick(duration: Long)
}