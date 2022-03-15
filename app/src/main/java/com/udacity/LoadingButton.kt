package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var valueAnimator = ValueAnimator()
    private var buttonText = "Download"
    private var downloadProgress = 0f
    private var downloadCircleProgress = 0f
    private var buttonColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
    private val progressColor = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
    private val circleColor = ResourcesCompat.getColor(resources, R.color.colorAccent, null)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 55.0f
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = buttonColor
    }

    private val inProgressBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = progressColor
    }

    private val inProgressArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = circleColor
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new){
            ButtonState.Clicked -> {
                invalidate()
            }
            ButtonState.Loading -> {
                Log.i("LoadingButton", "here")
                buttonText = context.getString(R.string.button_loading)
                buttonColor = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
                valueAnimator = ValueAnimator.ofFloat(0f, 1f)
                valueAnimator.duration = 10000
                valueAnimator.addUpdateListener { animation ->
                    downloadProgress = animation.animatedValue as Float
                    downloadCircleProgress = (widthSize.toFloat()/365)*downloadProgress
                    invalidate()
                }
                valueAnimator.addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {
                        downloadProgress = 0f
                        if(buttonState == ButtonState.Loading){
                            buttonState = ButtonState.Loading
                        }
                    }
                })
                valueAnimator.start()
                isClickable = false
            }

            ButtonState.Completed -> {

                valueAnimator.cancel()
                buttonText = "Downloaded"
                buttonColor = ResourcesCompat.getColor(resources, R.color.colorAccent, null)
                downloadProgress = 0f
                downloadCircleProgress = 0f
                isClickable = true
                invalidate()
            }
        }
    }

    init {
        isClickable = true
        buttonColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val corner = 10.0f
        val width = measuredWidth.toFloat()
        val height = measuredHeight.toFloat()
        val rect = Rect()

        canvas?.drawColor(buttonColor)
        paint.getTextBounds(buttonText, 0, buttonText.length, rect)
        canvas?.drawRoundRect(0f, 0f, width, height, corner, corner, backgroundPaint)

        if (buttonState == ButtonState.Loading) {
            var progressVal = downloadProgress * measuredWidth.toFloat()
            canvas?.drawRoundRect(0f, 0f, progressVal, height, corner, corner, inProgressBackgroundPaint)

            val arcDiameter = corner * 5
            val arcRectSize = measuredHeight.toFloat() - paddingBottom.toFloat() - arcDiameter

            progressVal = downloadProgress * 360f
            val textWidth = paint.measureText(buttonText)
            val textSize = 20f

            canvas?.save()
            canvas?.translate(widthSize / 2 + textWidth / 2 + textSize/2, 0f)
            canvas?.drawArc(paddingStart + arcDiameter,
                paddingTop.toFloat() + arcDiameter,
                arcRectSize,
                arcRectSize,
                0f,
                progressVal,
                true,
                inProgressArcPaint)
            canvas?.restore()
        }
        val centerX = measuredWidth.toFloat() / 2
        val centerY = measuredHeight.toFloat() / 2 - rect.centerY()

        canvas?.drawText(buttonText,centerX, centerY, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minwidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val width: Int = resolveSizeAndState(minwidth, widthMeasureSpec, 1)
        val height: Int = resolveSizeAndState(
            MeasureSpec.getSize(width),
            heightMeasureSpec,
            0
        )
        widthSize = width
        heightSize = height
        setMeasuredDimension(width, height)
    }

    fun setLoadingButtonState(state: ButtonState) {
        buttonState = state
    }
}