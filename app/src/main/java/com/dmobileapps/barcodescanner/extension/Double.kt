package com.dmobileapps.barcodescanner.extension

fun Double?.orZero(): Double {
    return this ?: 0.0
}