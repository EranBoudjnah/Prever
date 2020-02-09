package com.mitteloupe.prever

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class PreverView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defaultStyleAttribute: Int = 0
) : View(context, attributeSet, defaultStyleAttribute) {
    private var didPreMeasureViews = false
    private var didUpdateViews = false
    private val viewUpdater = ViewUpdater()

    private val isPreMeasureRequested get() = isInEditMode && !didPreMeasureViews

    override fun isLayoutRequested() = isInEditMode && !didUpdateViews

    @SuppressLint("WrongCall")
    override fun layout(l: Int, t: Int, r: Int, b: Int) {
        if (isLayoutRequested) {
            onLayout(true, l, t, r, b)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        updateViewsPreMeasure()
        setMeasuredDimension(0, 0)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        updateViewsOnLayout()
        super.onLayout(changed, left, top, right, bottom)
    }

    private fun updateViewsPreMeasure() {
        if (!isPreMeasureRequested) return

        didPreMeasureViews = true
        rootView.updateViewsPreMeasure()
    }

    private fun View.updateViewsPreMeasure() {
        if (this == this@PreverView) return
        (this as? ViewGroup)?.updateChildrenPreMeasure()
        viewUpdater.updateViewPreMeasure(this)
    }

    private fun ViewGroup.updateChildrenPreMeasure() {
        if (this::class.java.simpleName == "TextInputLayout") return
        for (i in 0 until childCount) {
            getChildAt(i).updateViewsPreMeasure()
        }
    }

    private fun updateViewsOnLayout() {
        if (!isLayoutRequested) return

        rootView.updateViewsOnLayout()
        viewUpdater.updateRootView(parent as View)
    }

    private fun View.updateViewsOnLayout() {
        if (this == this@PreverView) return
        (this as? ViewGroup)?.updateChildrenOnLayout()
        viewUpdater.updateViewOnLayout(this)
    }

    private fun ViewGroup.updateChildrenOnLayout() {
        if (this::class.java.simpleName == "TextInputLayout") return
        for (i in 0 until childCount) {
            getChildAt(i).updateViewsOnLayout()
        }
    }
}
