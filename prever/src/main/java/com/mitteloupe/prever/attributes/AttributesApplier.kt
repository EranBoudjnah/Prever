package com.mitteloupe.prever.attributes

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.annotation.StyleableRes

fun AttributeSet.applyAttributes(
    context: Context,
    @StyleableRes attributeIds: IntArray,
    defaultStyledAttributes: Int,
    applyStyledAttributes: TypedArray.() -> Unit
) {
    val attributes = context.obtainStyledAttributes(this, attributeIds, defaultStyledAttributes, 0)
    try {
        applyStyledAttributes(attributes)
    } finally {
        attributes.recycle()
    }
}
