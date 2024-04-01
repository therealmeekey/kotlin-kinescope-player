package io.kinescope.sdk.utils

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation

fun View.animateRotation() {
    val rotate = RotateAnimation(
        0f, 360f,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f
    )
    rotate.duration = 800
    rotate.repeatCount = Animation.INFINITE
    rotate.interpolator = LinearInterpolator()
    this.startAnimation(rotate)
}

internal fun Context.dip(dp: Int): Int {
    val metrics = resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), metrics).toInt()
}