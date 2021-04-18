package com.mitteloupe.prever

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.mitteloupe.prever.attributes.applyAttributes

private const val MAXIMUM_LEVELS = 500

class PreverView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defaultStyleAttribute: Int = 0
) : View(context, attributeSet, defaultStyleAttribute) {
    private var didPreMeasureViews = false
    private var didUpdateViews = false
    private val viewUpdater = ViewUpdater(resources)
    private var levels: Int = 0
    private val topView by lazy {
        var parentView: View? = this
        repeat(levels) findParent@{
            val lastParentView = parentView
            parentView = parentView?.parent?.let { immediateParent ->
                if (immediateParent::class.qualifiedName == "com.android.layoutlib.bridge.impl.Layout") {
                    return@findParent
                }
                immediateParent as View
            }
            if (parentView == lastParentView) {
                return@findParent
            }
        }
        parentView ?: throw IllegalStateException("Parent not found.")
    }

    private val isPreMeasureRequested get() = isInEditMode && !didPreMeasureViews

    override fun isLayoutRequested() = isInEditMode && !didUpdateViews

    init {
        attributeSet?.applyAttributes(context, R.styleable.PreverView, defaultStyleAttribute) {
            levels = getInt(R.styleable.PreverView_levels, MAXIMUM_LEVELS)
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
        topView.updateViewsPreMeasure()
    }

    private fun View.updateViewsPreMeasure() {
        if (this == this@PreverView) return
        (this as? ViewGroup)?.updateChildrenPreMeasure()
        viewUpdater.updateViewPreMeasure(this)
    }

    private fun ViewGroup.updateChildrenPreMeasure() {
        if (this::class.java.simpleName == "TextInputLayout") return
        repeat(childCount) { index ->
            getChildAt(index).updateViewsPreMeasure()
        }
    }

    private fun updateViewsOnLayout() {
        if (!isLayoutRequested) return

        topView.updateViewsOnLayout()
        viewUpdater.updateRootView(topView)
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
