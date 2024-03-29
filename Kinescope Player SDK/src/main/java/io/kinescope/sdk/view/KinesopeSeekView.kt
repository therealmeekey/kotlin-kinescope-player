package io.kinescope.sdk.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import io.kinescope.sdk.R

class KinesopeSeekView (
                        context: Context,
                        attrs: AttributeSet? = null,
                        defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet? = null,) : this(context, attrs, 0)

    private val leftView:CircleAnimationView
    private val rightView:CircleAnimationView

    init {
        inflate(context, R.layout.view_kinescope_seek_view, this)

        leftView = findViewById(R.id.cav_left)
        rightView = findViewById(R.id.cav_right)
    }

    fun showForwardView(event:MotionEvent) {
        rightView.resetAnimation { rightView.updatePosition(event.x, event.y) }
    }

    fun showBackView(event:MotionEvent) {
        leftView.resetAnimation { leftView.updatePosition(event.x, event.y) }
    }
}