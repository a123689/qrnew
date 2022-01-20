package com.dmobileapps.barcodescanner.extension

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import com.dmobileapps.barcodescanner.feature.common.dialog.ErrorDialogFragment

val Fragment.packageManager: PackageManager
    get() = requireContext().packageManager

fun Fragment.showError(error: Throwable?) {
    val errorDialog = ErrorDialogFragment.newInstance(requireContext(), error)
    errorDialog.show(childFragmentManager, "")
}
