package com.dmobileapps.barcodescanner.extension

fun Int?.orZero(): Int {
    return this ?: 0
}