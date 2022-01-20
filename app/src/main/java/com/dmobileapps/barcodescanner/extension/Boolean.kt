package com.dmobileapps.barcodescanner.extension

fun Boolean?.orFalse(): Boolean {
    return this ?: false
}