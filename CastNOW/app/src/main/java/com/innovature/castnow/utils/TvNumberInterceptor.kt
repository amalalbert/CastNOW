package com.innovature.castnow.utils

import android.util.Log

private const val SECRET_CODE = "7890"
private const val CONFIGURE_CODE = "5800"

class TvNumberInterceptor(
    val onSecretCodeMatchedSuccess: () -> Unit,
    val onConfigureCodeMatchedSuccess: () -> Unit
) {

    private val enteredNumbers =
        StringBuilder()

    fun onNumberPressed(number: Int) {
        Log.d("amal", "onNumberPressed: $number")
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
        } else if (enteredNumbers.toString() == CONFIGURE_CODE) {
            onConfigureCodeMatched()
            enteredNumbers.clear()
        }
    }

    private fun onConfigureCodeMatched() {
        Log.d("amal", "Configure Code Matched")
        onConfigureCodeMatchedSuccess.invoke()
    }

    private fun onSecretCodeMatched() {
        Log.d("amal", "Secret Code Matched")
        onSecretCodeMatchedSuccess.invoke()
    }
}