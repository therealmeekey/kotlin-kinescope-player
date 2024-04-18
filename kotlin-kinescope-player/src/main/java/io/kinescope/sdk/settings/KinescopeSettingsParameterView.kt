package io.kinescope.sdk.settings

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import io.kinescope.sdk.databinding.ViewSettingsParameterBinding

class KinescopeSettingsParameterView(
    context: Context,
    attributes: AttributeSet? = null,
) : ConstraintLayout(context, attributes) {

    private val binding =
        ViewSettingsParameterBinding.inflate(LayoutInflater.from(context), this, true)

    fun setIcon(@DrawableRes iconRes: Int) = with(binding.iconIv) {
        setImageResource(iconRes)
    }

    fun setTitle(title: String) = with(binding.titleTv) {
        text = title
    }

    fun setCurrentValue(value: String) = with(binding.currentValueTv) {
        isVisible = value.isNotEmpty()
        text = value
    }
}