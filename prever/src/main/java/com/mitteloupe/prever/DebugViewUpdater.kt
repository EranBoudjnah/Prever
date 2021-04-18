package com.mitteloupe.prever

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import com.mitteloupe.prever.color.getRandomColor
import com.mitteloupe.prever.control.doIf
import com.mitteloupe.prever.string.isBlurb
import com.mitteloupe.prever.string.isDate
import com.mitteloupe.prever.string.isGenerated
import com.mitteloupe.prever.string.isSubtitle
import com.mitteloupe.prever.string.isTitle
import com.mitteloupe.prever.string.localName
import com.mitteloupe.randomgenkt.RandomGen
import java.text.SimpleDateFormat
import java.util.GregorianCalendar
import java.util.Locale
import kotlin.random.Random

private val calendar by lazy { GregorianCalendar() }
private val dateFormatter by lazy { SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH) }

class DebugViewUpdater(resources: Resources) : ViewUpdater {
    private val paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    private val titleLoremIpsumGenerator by lazy { getLoremIpsumRandomGen(30, 40) }

    private val subtitleLoremIpsumGenerator by lazy { getLoremIpsumRandomGen(60, 100) }

    private val blurbLoremIpsumGenerator by lazy { getLoremIpsumRandomGen(200, 400) }

    private val backgroundSize by lazy { resources.getDimensionPixelSize(R.dimen.prever_background_size) }

    private val borderThickness by lazy { resources.getDimension(R.dimen.prever_border_thickness) }

    private fun getLoremIpsumRandomGen(minLength: Int, maxLength: Int) =
        RandomGen.Builder<LoremIpsum>()
            .ofClass<LoremIpsum>()
            .withField("text")
            .returningLoremIpsum(minLength, maxLength)
            .build()

    override fun updateViewPreMeasure(view: View) {
        view.updatePreMeasure()
    }

    override fun updateViewOnLayout(view: View) {
        view.updateOnLayout()
    }

    override fun updateRootView(rootView: View) {
        rootView.updateBackground()
    }

    private fun ImageView.updateImageViewOnLayout() = doIf(drawable == null) {
        setRandomColorBackground()
    }

    private fun View.updateBackground() = doIf(background == null) {
        setCheckerBoardBackground(backgroundSize)
    }

    private fun View.updatePreMeasure() = when (this) {
        is TextView -> updateTextViewPreMeasure()
        else -> {
            when (this::class.java.simpleName) {
                "TextInputLayout" -> (this as TextInputLayout).updateTextInputLayout()
                else -> println("No stubbing for ${this::class.java.simpleName}")
            }
        }
    }

    private fun View.updateOnLayout() = when (this) {
        is ImageView -> updateImageViewOnLayout()
        else -> updateXYTranslation()
    }

    private fun View.updateXYTranslation() {
        translationX *= Random.nextFloat()
        translationY *= Random.nextFloat()
    }

    private fun TextInputLayout.updateTextInputLayout() {
        editText?.let { editText ->
            isHintAnimationEnabled = false
            if (editText.text.isNullOrBlank()) {
                editText.updateTextViewPreMeasure(localName)
            }
        }
    }

    private fun TextView.updateTextViewPreMeasure(parentName: String? = null) {
        if (text.isNullOrBlank() || text.toString().isGenerated()) {
            text = (parentName ?: localName)?.toText() ?: titleLoremIpsumGenerator.generate().text
        }
    }

    private fun String.toText(): String = when {
        isDate() -> getRandomDate()
        isTitle() -> titleLoremIpsumGenerator.generate().text
        isSubtitle() -> subtitleLoremIpsumGenerator.generate().text
        isBlurb() -> blurbLoremIpsumGenerator.generate().text
        else -> this
    }

    private fun View.setCheckerBoardBackground(tileSize: Int) {
        val checkerBoardPaint = createCheckerBoard(tileSize)
        val backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val backgroundCanvas = Canvas(backgroundBitmap)
        backgroundCanvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), checkerBoardPaint)
        setBackgroundCompat(BitmapDrawable(resources, backgroundBitmap))
    }

    private fun View.setRandomColorBackground() {
        val backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val backgroundCanvas = Canvas(backgroundBitmap)
        paint.apply {
            style = Paint.Style.FILL
            color = getRandomColor(0x44)
        }
        backgroundCanvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.apply {
            style = Paint.Style.STROKE
            strokeWidth = borderThickness
            color = Color.argb(255, 0x33, 0x33, 0x33)
        }
        backgroundCanvas.drawRect(0f, 0f, width.toFloat() - 1f, height.toFloat() - 1f, paint)
        setBackgroundCompat(BitmapDrawable(resources, backgroundBitmap))
    }

    private fun createCheckerBoard(tileSize: Int): Paint {
        val tileSizeFloat = tileSize.toFloat()
        val bitmapSize = tileSize * 2
        val bitmapSizeFloat = tileSizeFloat * 2f
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = BitmapShader(
                Bitmap.createBitmap(
                    bitmapSize,
                    bitmapSize,
                    Bitmap.Config.ARGB_8888
                ).apply {
                    Canvas(this).apply {
                        paint.apply {
                            style = Paint.Style.FILL
                            color = 0x22000000
                        }
                        drawRect(0f, 0f, tileSizeFloat, tileSizeFloat, paint)
                        drawRect(
                            tileSizeFloat, tileSizeFloat, bitmapSizeFloat, bitmapSizeFloat, paint
                        )
                    }
                }, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT
            )
        }
    }
}

private fun View.setBackgroundCompat(drawable: Drawable) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        background = drawable
    } else {
        @Suppress("DEPRECATION")
        setBackgroundDrawable(drawable)
    }

data class LoremIpsum(val text: String)

private fun getRandomDate(): String {
    val year = Random.nextInt(1930, 2031)
    calendar.set(GregorianCalendar.YEAR, year)

    val dayOfYear = Random.nextInt(
        1,
        calendar.getActualMaximum(GregorianCalendar.DAY_OF_YEAR)
    )//randomInRange(1, calendar.getActualMaximum(GregorianCalendar.DAY_OF_YEAR))
    calendar.set(GregorianCalendar.DAY_OF_YEAR, dayOfYear)

    return dateFormatter.format(calendar.time)
}
