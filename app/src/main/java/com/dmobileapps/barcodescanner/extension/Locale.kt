package com.dmobileapps.barcodescanner.extension

import java.util.*

val Locale?.isRussian: Boolean
    get() = this?.language == "ru"