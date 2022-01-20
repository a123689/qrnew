package com.dmobileapps.barcodescanner.model

import androidx.room.TypeConverters
import com.dmobileapps.barcodescanner.usecase.BarcodeDatabaseTypeConverter
import com.google.zxing.BarcodeFormat

@TypeConverters(BarcodeDatabaseTypeConverter::class)
data class ExportBarcode(
    val date: Long,
    val format: BarcodeFormat,
    val text: String
)