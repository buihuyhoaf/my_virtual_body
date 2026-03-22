package com.hoabui.virtualbody3d.core.extensions

fun Float.lerpTo(end: Float, fraction: Float): Float =
    this + (end - this) * fraction