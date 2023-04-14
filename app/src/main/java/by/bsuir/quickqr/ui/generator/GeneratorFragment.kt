package by.bsuir.quickqr.ui.generator

import GeneratorViewModel
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import by.bsuir.quickqr.R
import by.bsuir.quickqr.databinding.FragmentGeneratorBinding
import by.bsuir.quickqr.databinding.FragmentScannerBinding

class GeneratorFragment : Fragment() {

    private lateinit var binding: FragmentGeneratorBinding
    private lateinit var viewModel: GeneratorViewModel
    private var editTextValue: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val orientation = resources.configuration.orientation

        val layoutId = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            R.layout.fragment_generator
        } else {
            R.layout.fragment_generator_landscape
        }

        val rootView = inflater.inflate(layoutId, container, false)
        binding = FragmentGeneratorBinding.bind(rootView)

        if (savedInstanceState != null) {
            editTextValue = savedInstanceState.getString("editTextValue", "")
            binding.etQrText.setText(editTextValue)
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(GeneratorViewModel::class.java)

        binding.btnGenerateQr.setOnClickListener {
            val text = binding.etQrText.text.toString()
            viewModel.generateQrCode(text)
        }

        binding.root.setOnClickListener {
            hideKeyboard()
        }

        viewModel.qrCodeBitmap.observe(viewLifecycleOwner) { bitmap ->
            binding.ivQrCode.setImageBitmap(bitmap)
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etQrText.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::binding.isInitialized) {
            editTextValue = binding.etQrText.text.toString()
            outState.putString("editTextValue", editTextValue)
        }
    }
}