package by.bsuir.quickqr.ui.scanner

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import by.bsuir.quickqr.R
import by.bsuir.quickqr.databinding.FragmentGeneratorBinding
import by.bsuir.quickqr.databinding.FragmentScannerBinding
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity


class ScannerFragment : Fragment() {

    private lateinit var binding: FragmentScannerBinding
    private lateinit var viewModel: ScannerViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val orientation = resources.configuration.orientation

        val layoutId = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            R.layout.fragment_scanner
        } else {
            R.layout.fragment_scanner_landscape
        }

        val rootView = inflater.inflate(layoutId, container, false)
        binding = FragmentScannerBinding.bind(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        viewModel = ViewModelProvider(this)[ScannerViewModel::class.java]

        binding.scanBtn.setOnClickListener {
            viewModel.initiateScan(this)
        }

        binding.copyBtn.setOnClickListener {
            viewModel.copyScannedResultToClipboard(requireContext())
        }

        binding.openBtn.setOnClickListener {
            viewModel.openScannedResultInBrowser(requireContext())
        }

        binding.shareBtn.setOnClickListener {
            viewModel.showSharingDialog(requireContext())
        }

        binding.searchBtn.setOnClickListener {
            viewModel.searchInBrowser(requireContext())
        }

        viewModel.scannedResult.observe(viewLifecycleOwner) { result ->
            binding.resultText.text = result

            if (result.isNotEmpty()) {
                setButtonVisibility(viewModel.isUri(result), binding.openBtn)
                setButtonVisibility(!viewModel.isUri(result), binding.searchBtn)
                setButtonVisibility(true, binding.copyBtn, binding.shareBtn)
            } else {
                setButtonVisibility(false, binding.copyBtn, binding.shareBtn)
            }

        }
    }

    private fun setButtonVisibility(condition: Boolean, vararg buttons: View) {
        for (button in buttons) {
            button.visibility = if (condition) View.VISIBLE else View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            viewModel.processScannedResult(result.contents)
        }
    }


}

