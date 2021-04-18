package com.mitteloupe.prever

import android.view.View

interface ViewUpdater {
    fun updateViewPreMeasure(view: View)
    fun updateViewOnLayout(view: View)
    fun updateRootView(rootView: View)
}
