package com.sinau.dicodingstory.utils

import android.animation.ObjectAnimator
import android.view.View

fun View.animateLoading(isLoading: Boolean) {
    ObjectAnimator.ofFloat(this, View.ALPHA, if (isLoading) 1f else 0f)
        .setDuration(500)
        .start()
}