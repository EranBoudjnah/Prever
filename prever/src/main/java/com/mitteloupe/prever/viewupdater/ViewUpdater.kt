package com.mitteloupe.prever.viewupdater

import android.view.View

interface ViewUpdater {
    fun updateViewPreMeasure(view: View)
    fun updateViewOnLayout(view: View)
    fun updateRootView(rootView: View)
}
