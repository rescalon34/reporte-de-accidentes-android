package com.escalon.dev.mylocationapp.util

import android.databinding.BindingAdapter
import android.support.design.widget.TextInputLayout

object BindingAdapter {

    @JvmStatic
    @BindingAdapter("error_message")
    fun TextInputLayout.errorMessage(errorMessage: String) {
        this.error = errorMessage
    }
}