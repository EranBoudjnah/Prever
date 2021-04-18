package com.mitteloupe.prever

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.mitteloupe.prever.attributes.applyAttributes
import com.mitteloupe.prever.topviewfinder.DebugTopViewFinder
import com.mitteloupe.prever.topviewfinder.ReleaseTopViewFinder
import com.mitteloupe.prever.topviewfinder.TopViewFinder
import com.mitteloupe.prever.viewupdater.DebugViewUpdater
import com.mitteloupe.prever.viewupdater.ReleaseViewUpdater

private const val MAXIMUM_LEVELS = 500

class PreverView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defaultStyleAttribute: Int = 0
) : View(context, attributeSet, defaultStyleAttribute) {
    private var didPreMeasureViews = false
    private var didUpdateViews = false
    private val viewUpdater = if (isInEditMode) {
        DebugViewUpdater(resources)
    } else {
        ReleaseViewUpdater()
    }

    private val topViewFinder: TopViewFinder

    private val isPreMeasureRequested get() = isInEditMode && !didPreMeasureViews

    override fun isLayoutRequested() = isInEditMode && !didUpdateViews

    init {
        var maximumLevels = 0
        attributeSet?.applyAttributes(context, R.styleable.PreverView, defaultStyleAttribute) {
            maximumLevels = getInt(R.styleable.PreverView_levels, MAXIMUM_LEVELS)
        }

        topViewFinder = if (isInEditMode) {
            DebugTopViewFinder(this, maximumLevels)
        } else {
            ReleaseTopViewFinder(this)
        }
    }

    @SuppressLint("WrongCall")
    override fun layout(l: Int, t: Int, r: Int, b: Int) {
        if (isLayoutRequested) {
            onLayout(true, l, t, r, b)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        updateViewsPreMeasure()
        val measureSpec = MeasureSpec.makeMeasureSpec(1, MeasureSpec.EXACTLY)
        setMeasuredDimension(measureSpec, measureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        updateViewsOnLayout()
        super.onLayout(changed, left, top, right, bottom)
    }

    private fun updateViewsPreMeasure() {
        if (!isPreMeasureRequested) return

        didPreMeasureViews = true
        topViewFinder.topView?.updateViewsPreMeasure()
    }

    private fun updateViewsOnLayout() {
        if (!isLayoutRequested) return

        topViewFinder.topView?.let { topView ->
            topView.updateViewsOnLayout()
            viewUpdater.updateRootView(topView)
        }
    }

    private fun ViewGroup.updateChildrenPreMeasure() {
        if (this::class.java.simpleName == "TextInputLayout") return
        repeat(childCount) { index ->
            getChildAt(index).updateViewsPreMeasure()
        }
    }

    private fun View.updateViewsPreMeasure() {
        if (this == this@PreverView) return
        (this as? ViewGroup)?.updateChildrenPreMeasure()
        viewUpdater.updateViewPreMeasure(this)
    }

    private fun View.updateViewsOnLayout() {
        if (this == this@PreverView) return
        (this as? ViewGroup)?.updateChildrenOnLayout()
        viewUpdater.updateViewOnLayout(this)
    }

    private fun ViewGroup.updateChildrenOnLayout() {
        if (this::class.java.simpleName == "TextInputLayout") return
        repeat(childCount) { index ->
            getChildAt(index).updateViewsOnLayout()
        }
    }
}

