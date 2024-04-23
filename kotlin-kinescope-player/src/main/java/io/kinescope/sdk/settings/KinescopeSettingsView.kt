package io.kinescope.sdk.settings

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import io.kinescope.sdk.databinding.ViewSettingsBinding
import io.kinescope.sdk.extensions.toPx
import java.lang.IllegalStateException

class KinescopeSettingsView(
    context: Context,
    attributes: AttributeSet? = null,
) : FrameLayout(context, attributes) {

    private val parameters = mutableSetOf<Parameter>()
    private val parameterOptions = mutableMapOf<Parameter, List<KinescopeSettingsOption>>()

    private val binding =
        ViewSettingsBinding.inflate(LayoutInflater.from(context), this, true)

    var onOptionSelected: ((parameter: Parameter, optionId: Int) -> Unit)? = null

    init {
        isVisible = false

        binding.parameterOptionsTitleTv.setOnClickListener {
            hideOptions()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val rect = Rect()
            .also { binding.settingsMenuFl.getGlobalVisibleRect(it) }

        if (!rect.contains(event.rawX.toInt(), event.rawY.toInt())) {
            hide()
        }

        return super.dispatchTouchEvent(event)
    }

    fun addParameter(
        parameter: Parameter,
        title: String,
        @DrawableRes icon: Int,
    ) {
        if (parameters.contains(parameter)) {
            throw IllegalStateException(ERROR_TEXT_PARAMETER_DUPLICATION.format(parameter))
        }

        parameters.add(parameter)
        binding.parametersLl
            .addView(KinescopeSettingsParameterView(context)
                .apply {
                    setTitle(title)
                    setIcon(icon)
                    setOnClickListener {
                        onParameterClick(
                            title = title,
                            parameter = parameter,
                        )
                    }
                    tag = parameter
                })
    }

    fun setParameterCurrentValue(parameter: Parameter, value: String) {
        checkParameterAddedOrException(parameter)
        with(binding.parametersLl) {
            findViewWithTag<KinescopeSettingsParameterView>(parameter)
                ?.setCurrentValue(value)
        }
    }

    fun setParameterOptions(parameter: Parameter, options: List<KinescopeSettingsOption>) {
        checkParameterAddedOrException(parameter)
        parameterOptions[parameter] = options
    }

    fun show() {
        isVisible = true
        animateTransitionX(
            view = binding.settingsMenuFl,
            transition = 0f,
        )
    }

    private fun hide() {
        animateTransitionX(
            view = binding.settingsMenuFl,
            transition = WIDTH.toFloat().toPx,
            onAnimationEnd = {
                hideOptions(isAnimated = false)
                isVisible = false
            },
        )
    }

    private fun showOptions() =
        with(binding.optionsLl) {
            isVisible = true
            post {
                animateTransitionX(
                    view = this,
                    transition = 0f,
                )
            }
        }

    private fun hideOptions(isAnimated: Boolean = true) =
        with(binding.optionsLl) {
            if (!isAnimated) {
                translationX = WIDTH.toFloat().toPx
                isVisible = false
                return@with
            }

            animateTransitionX(
                view = this,
                transition = WIDTH.toFloat().toPx,
                onAnimationEnd = { isVisible = false }
            )
        }

    private fun animateTransitionX(
        view: View,
        transition: Float,
        onAnimationEnd: (() -> Unit)? = null,
    ) = ObjectAnimator.ofFloat(view, PROPERTY_TRANSLATION_X, transition)
        .apply {
            duration = ANIMATION_DURATION
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}

                override fun onAnimationEnd(animator: Animator) {
                    onAnimationEnd?.invoke()
                }

                override fun onAnimationCancel(animator: Animator) {}

                override fun onAnimationRepeat(animator: Animator) {}
            })
            start()
        }

    private fun onParameterClick(title: String, parameter: Parameter) {
        with(binding) {
            parameterOptionsTitleTv.text = title
            with(parameterOptionsLl) {
                removeAllViews()
                parameterOptions[parameter]
                    .orEmpty()
                    .forEach { option ->
                        addView(KinescopeSettingsOptionView(context)
                            .apply {
                                setTitle(option.title)
                                setIsSelected(option.isSelected)
                                setOnClickListener {
                                    hide()
                                    onOptionSelected?.invoke(parameter, option.id)
                                }
                            })
                    }
            }
            showOptions()
        }
    }

    private fun checkParameterAddedOrException(parameter: Parameter) {
        if (!parameters.contains(parameter)) {
            throw IllegalStateException(ERROR_TEXT_NO_PARAMETER.format(parameter))
        }
    }

    sealed class Parameter {
        object PlaybackSpeed : Parameter()
        object VideoQuality : Parameter()
    }

    private companion object {
        private const val ERROR_TEXT_PARAMETER_DUPLICATION =
            "Parameter duplication error. The %s parameter has already been added."
        private const val ERROR_TEXT_NO_PARAMETER =
            "The %s parameter has not been added. First add it to set options for it."

        private const val WIDTH = 300
        private const val ANIMATION_DURATION = 210L
        private const val PROPERTY_TRANSLATION_X = "translationX"
    }
}