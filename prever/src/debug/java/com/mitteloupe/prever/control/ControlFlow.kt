package com.mitteloupe.prever.control

@Suppress("ControlFlowWithEmptyBody")
fun doIf(predicate: Boolean, command: () -> Unit) = if (predicate) {
    command()
} else {}
