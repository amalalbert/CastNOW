package com.innovature.castnow.utils

import android.util.Log

private const val SECRET_CODE = "7890"

class TvNumberInterceptor(val onSecretCodeMatchedSuccess: () -> Unit) {

    private val enteredNumbers =
        StringBuilder()

    fun onNumberPressed(number: Int) {

        enteredNumbers.append(number)

        // keep last 4 digits only
        if (enteredNumbers.length > 4) {
            enteredNumbers.deleteCharAt(0)
        }

        if (
            enteredNumbers.toString() ==
            SECRET_CODE
        ) {
            onSecretCodeMatched()
            enteredNumbers.clear()
        }
    }

    private fun onSecretCodeMatched() {
        Log.d("amal", "Secret Code Matched")
        onSecretCodeMatchedSuccess.invoke()
    }
}