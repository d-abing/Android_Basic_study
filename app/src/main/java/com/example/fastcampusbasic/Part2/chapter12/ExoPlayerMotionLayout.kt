package com.example.fastcampusbasic.Part2.chapter12

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout

class ExoPlayerMotionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr) {

    var targetView: View? = null
    private val gestureDetector by lazy {
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                e1?.let {
                    return targetView?.containTouchArea(it.x.toInt(), it.y.toInt()) ?: false
                }
                return false
            }
        })
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return event?.let {
            gestureDetector.onTouchEvent(event)
        } ?: false
    }

    private fun View.containTouchArea(x: Int, y: Int): Boolean {
        return (x in left..right && y in top..bottom)
    }
}