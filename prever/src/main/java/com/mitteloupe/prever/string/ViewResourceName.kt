package com.mitteloupe.prever.string

import android.view.View

val View.localName: String?
    get() = this.name?.let { name -> name.substringAfter("/", name) }

private val View.name
    get() = if (id == View.NO_ID) null else resources.getResourceName(id)
