package com.dmobileapps.barcodescanner.extension

import com.dmobileapps.barcodescanner.model.Barcode
import com.google.zxing.Result

fun Result.equalTo(barcode: Barcode?): Boolean {
    return barcodeFormat == barcode?.format && text == barcode?.text
}