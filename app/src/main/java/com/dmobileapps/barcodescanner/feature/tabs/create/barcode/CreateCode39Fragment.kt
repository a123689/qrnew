package com.dmobileapps.barcodescanner.feature.tabs.create.barcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.dmobileapps.barcodescanner.R
import com.dmobileapps.barcodescanner.extension.isNotBlank
import com.dmobileapps.barcodescanner.extension.textString
import com.dmobileapps.barcodescanner.feature.tabs.create.BaseCreateBarcodeFragment
import com.dmobileapps.barcodescanner.model.schema.Other
import com.dmobileapps.barcodescanner.model.schema.Schema
import kotlinx.android.synthetic.main.fragment_create_code_39.*

class CreateCode39Fragment : BaseCreateBarcodeFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_code_39, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edit_text.requestFocus()
        edit_text.addTextChangedListener {
            parentActivity.isCreateBarcodeButtonEnabled = edit_text.isNotBlank()
        }
    }

    override fun getBarcodeSchema(): Schema {
        return Other(edit_text.textString)
    }
}