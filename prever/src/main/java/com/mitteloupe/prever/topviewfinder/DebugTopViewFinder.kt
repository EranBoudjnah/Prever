package com.mitteloupe.prever.topviewfinder

import android.view.View
import com.mitteloupe.prever.topviewfinder.TopViewFinder

class DebugTopViewFinder(private val view: View, private var levels: Int) : TopViewFinder {
    override val topView by lazy {
        var parentView: View? = view
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
}
