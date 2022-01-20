package com.dmobileapps.barcodescanner.feature

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.dmobileapps.barcodescanner.R
import kotlinx.android.synthetic.main.fragment_exit.*

class ExitFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_exit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvNo.setOnClickListener {
            findNavController().popBackStack()
        }

        tvYes.setOnClickListener {
            System.exit(0)
        }
    }


}