package com.dmobileapps.barcodescanner.model.schema

import com.dmobileapps.barcodescanner.extension.removePrefixIgnoreCase
import com.dmobileapps.barcodescanner.extension.startsWithIgnoreCase

class Phone(val phone: String) : Schema {

    companion object {
        private const val PREFIX = "tel:"

        fun parse(text: String): Phone? {
            if (text.startsWithIgnoreCase(PREFIX).not()) {
                return null
            }

            val phone = text.removePrefixIgnoreCase(PREFIX)
            return Phone(phone)
        }
    }

    override val schema = BarcodeSchema.PHONE
    override fun toFormattedText(): String = phone
    override fun toBarcodeText(): String = "$PREFIX$phone"
}